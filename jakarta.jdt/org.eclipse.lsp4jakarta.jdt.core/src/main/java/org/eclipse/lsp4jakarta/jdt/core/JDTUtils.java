/*******************************************************************************
 * Copyright (c) 2016-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.SourceRange;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.internal.corext.dom.IASTSharedValues;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.jdt.ls.core.internal.handlers.DocumentLifeCycleHandler;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import com.google.common.base.Charsets;

/**
 * This class is a copy/paste of JDT LS
 * https://github.com/eclipse/eclipse.jdt.ls/blob/master/org.eclipse.jdt.ls.core/src/org/eclipse/jdt/ls/core/internal/JDTUtils.java
 * with deletions of some unnecessary methods and modifications to logging for
 * Jakarta LS project.
 *
 */
public class JDTUtils {

    public static final String PATH_SEPARATOR = "/";
    public static final String PERIOD = ".";
    public static final String SRC = "src";

    public static final String FILE_UNC_PREFIX = "file:////";
    private static final String JDT_SCHEME = "jdt";
    // Code generators known to cause problems
    private static Set<String> SILENCED_CODEGENS = Collections.singleton("lombok");

    public static final String DEFAULT_PROJECT_NAME = "jdt.java-project";

    private static final int COMPILATION_UNIT_UPDATE_TIMEOUT = 3000;
    // Percent encoding obtained from: https://en.wikipedia.org/wiki/Percent-encoding#Reserved_characters
    private static final String LEVEL1_URI_REGEX = "(?:\\/(?:(?:\\{(\\w|-|%20|%21|%23|%24|%25|%26|%27|%28|%29|%2A|%2B|%2C|%2F|%3A|%3B|%3D|%3F|%40|%5B|%5D)+\\})|(?:(\\w|%20|%21|%23|%24|%25|%26|%27|%28|%29|%2A|%2B|%2C|%2F|%3A|%3B|%3D|%3F|%40|%5B|%5D)+)))*\\/?";

    /**
     * Given the uri returns a {@link ICompilationUnit}. May return null if it can
     * not associate the uri with a Java file.
     *
     * @param uriString
     * @return compilation unit
     */
    public static ICompilationUnit resolveCompilationUnit(String uriString) {
        return resolveCompilationUnit(toURI(uriString));
    }

    /**
     * Given the uri returns a {@link ICompilationUnit}. May return null if it can
     * not associate the uri with a Java file.
     *
     * @param uri
     * @return compilation unit
     */
    public static ICompilationUnit resolveCompilationUnit(URI uri) {
        if (uri == null || JDT_SCHEME.equals(uri.getScheme()) || !uri.isAbsolute()) {
            return null;
        }

        IFile resource = (IFile) findResource(uri, ResourcesPlugin.getWorkspace().getRoot()::findFilesForLocationURI);
        if (resource != null) {
            if (!ProjectUtils.isJavaProject(resource.getProject())) {
                return null;
            }
            if (resource.getFileExtension() != null) {
                String name = resource.getName();
                if (JavaCore.isJavaLikeFileName(name)) {
                    ICompilationUnit unit = JavaCore.createCompilationUnitFrom(resource);
                    try {
                        // Give underlying resource time to catch up
                        // (timeout at COMPILATION_UNIT_UPDATE_TIMEOUT milliseconds).
                        long endTime = System.currentTimeMillis() + COMPILATION_UNIT_UPDATE_TIMEOUT;
                        while (!unit.isConsistent() && System.currentTimeMillis() < endTime) {
                        }
                    } catch (JavaModelException e) {
                    }
                    return unit;
                }
            }
            return null;
        }
        return getFakeCompilationUnit(uri, new NullProgressMonitor());

    }

    static ICompilationUnit getFakeCompilationUnit(URI uri, IProgressMonitor monitor) {
        if (uri == null || !"file".equals(uri.getScheme()) || !uri.getPath().endsWith(".java")) {
            return null;
        }
        java.nio.file.Path path = Paths.get(uri);
        // Only support existing standalone java files
        if (!Files.isReadable(path)) {
            return null;
        }

        IProject project = getDefaultProject();
        if (project == null || !project.isAccessible()) {
            return null;
        }
        IJavaProject javaProject = JavaCore.create(project);

        String packageName = getPackageName(javaProject, uri);
        String fileName = path.getName(path.getNameCount() - 1).toString();
        String packagePath = packageName.replace(PERIOD, PATH_SEPARATOR);

        IPath filePath = new Path(SRC).append(packagePath).append(fileName);
        final IFile file = project.getFile(filePath);
        if (!file.isLinked()) {
            try {
                createFolders(file.getParent(), monitor);
                file.createLink(uri, IResource.REPLACE, monitor);
            } catch (CoreException e) {
                String errMsg = "Failed to create linked resource from " + uri + " to " + project.getName();
                JakartaCorePlugin.logException(errMsg, e);
            }
        }
        if (file.isLinked()) {
            return (ICompilationUnit) JavaCore.create(file, javaProject);
        }
        return null;
    }

    public static void createFolders(IContainer folder, IProgressMonitor monitor) throws CoreException {
        if (!folder.exists() && folder instanceof IFolder) {
            IContainer parent = folder.getParent();
            createFolders(parent, monitor);
            folder.refreshLocal(IResource.DEPTH_ZERO, monitor);
            if (!folder.exists()) {
                ((IFolder) folder).create(true, true, monitor);
            }
        }
    }

    public static String getPackageName(IJavaProject javaProject, URI uri) {
        try {
            File file = ResourceUtils.toFile(uri);
            // FIXME need to determine actual charset from file
            String content = com.google.common.io.Files.toString(file, Charsets.UTF_8);
            if (content.isEmpty() && javaProject != null
                    && DEFAULT_PROJECT_NAME.equals(javaProject.getProject().getName())) {
                java.nio.file.Path path = Paths.get(uri);
                java.nio.file.Path parent = path;
                while (parent.getParent() != null && parent.getParent().getNameCount() > 0) {
                    parent = parent.getParent();
                    String name = parent.getName(parent.getNameCount() - 1).toString();
                    if (SRC.equals(name)) {
                        String pathStr = path.getParent().toString();
                        if (pathStr.length() > parent.toString().length()) {
                            pathStr = pathStr.substring(parent.toString().length() + 1);
                            pathStr = pathStr.replace(PATH_SEPARATOR, PERIOD);
                            return pathStr;
                        }
                    }
                }
            } else {
                return getPackageName(javaProject, content);
            }
        } catch (IOException e) {
        	JakartaCorePlugin.logException("Failed to read package name from " + uri, e);
        }
        return "";
    }

    public static String getPackageName(IJavaProject javaProject, String fileContent) {
        if (fileContent == null) {
            return "";
        }
        // TODO probably not the most efficient way to get the package name as this
        // reads the whole file;
        char[] source = fileContent.toCharArray();
        ASTParser parser = ASTParser.newParser(IASTSharedValues.SHARED_AST_LEVEL);
        parser.setProject(javaProject);
        parser.setIgnoreMethodBodies(true);
        parser.setSource(source);
        CompilationUnit ast = (CompilationUnit) parser.createAST(null);
        PackageDeclaration pkg = ast.getPackage();
        return (pkg == null || pkg.getName() == null) ? "" : pkg.getName().getFullyQualifiedName();
    }

    /**
     * Given the uri returns a {@link IClassFile}. May return null if it can not
     * resolve the uri to a library.
     *
     * @see #toLocation(IClassFile, int, int)
     * @param uri with 'jdt' scheme
     * @return class file
     */
    public static IClassFile resolveClassFile(String uriString) {
        return resolveClassFile(toURI(uriString));
    }

    /**
     * Given the uri returns a {@link IClassFile}. May return null if it can not
     * resolve the uri to a library.
     *
     * @see #toLocation(IClassFile, int, int)
     * @param uri with 'jdt' scheme
     * @return class file
     */
    public static IClassFile resolveClassFile(URI uri) {
        if (uri != null && JDT_SCHEME.equals(uri.getScheme()) && "contents".equals(uri.getAuthority())) {
            String handleId = uri.getQuery();
            IJavaElement element = JavaCore.create(handleId);
            IClassFile cf = (IClassFile) element.getAncestor(IJavaElement.CLASS_FILE);
            return cf;
        }
        return null;
    }

    public static IProject getDefaultProject() {
        return getWorkspaceRoot().getProject(DEFAULT_PROJECT_NAME);
    }

    private static IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    public static IFile findFile(String uriString) {
        return (IFile) findResource(toURI(uriString),
                ResourcesPlugin.getWorkspace().getRoot()::findFilesForLocationURI);
    }

    public static IResource findResource(URI uri, Function<URI, IResource[]> resourceFinder) {
        if (uri == null || !"file".equals(uri.getScheme())) {
            return null;
        }
        IResource[] resources = resourceFinder.apply(uri);
        if (resources.length == 0) {
            //On Mac, Linked resources are referenced via the "real" URI, i.e file://USERS/username/...
            //instead of file://Users/username/..., so we check against that real URI.
            URI realUri = FileUtil.realURI(uri);
            if (!uri.equals(realUri)) {
                uri = realUri;
                resources = resourceFinder.apply(uri);
            }
        }
        if (resources.length == 0 && Platform.OS_WIN32.equals(Platform.getOS())
                && uri.toString().startsWith(FILE_UNC_PREFIX)) {
            String uriString = uri.toString();
            int index = uriString.indexOf(PATH_SEPARATOR, FILE_UNC_PREFIX.length());
            if (index > 0) {
                String server = uriString.substring(FILE_UNC_PREFIX.length(), index);
                uriString = uriString.replace(server, server.toUpperCase());
                try {
                    uri = new URI(uriString);
                } catch (URISyntaxException e) {
                    // JavaLanguageServerPlugin.logException(e.getMessage(), e);
                }
                resources = resourceFinder.apply(uri);
            }
        }
        switch (resources.length) {
        case 0:
            return null;
        case 1:
            return resources[0];
        default:// several candidates if a linked resource was created before the real project
                // was configured
            IResource resource = null;
            for (IResource f : resources) {
                // find closest project containing that file, in case of nested projects
                if (resource == null || f.getProjectRelativePath().segmentCount() < resource.getProjectRelativePath()
                        .segmentCount()) {
                    resource = f;
                }
            }
            return resource;
        }
    }

    public static URI toURI(String uriString) {
        if (uriString == null || uriString.isEmpty()) {
            return null;
        }
        try {
            URI uri = new URI(uriString);
            if (Platform.OS_WIN32.equals(Platform.getOS()) && URIUtil.isFileURI(uri)) {
                uri = URIUtil.toFile(uri).toURI();
            }
            return uri;
        } catch (URISyntaxException e) {
        	JakartaCorePlugin.logException("Failed to resolve " + uriString, e);
            return null;
        }
    }

    public static String toUri(IClassFile classFile) {
        String packageName = classFile.getParent().getElementName();
        String jarName = classFile.getParent().getParent().getElementName();
        String uriString = null;
        try {
            uriString = new URI(JDT_SCHEME, "contents", PATH_SEPARATOR + jarName + PATH_SEPARATOR + packageName
                    + PATH_SEPARATOR + classFile.getElementName(), classFile.getHandleIdentifier(), null)
                            .toASCIIString();
        } catch (URISyntaxException e) {
        	JakartaCorePlugin.logException("Error generating URI for class ", e);
        }
        return uriString;
    }

    public static String toUri(ITypeRoot typeRoot) {
        if (typeRoot instanceof ICompilationUnit) {
            return toURI((ICompilationUnit) typeRoot);
        }
        if (typeRoot instanceof IClassFile) {
            return toUri((IClassFile) typeRoot);
        }
        return null;
    }

    /**
     * Creates a range for the given offset and length for an {@link IOpenable}
     *
     * @param openable
     * @param offset
     * @param length
     * @return
     * @throws JavaModelException
     */
    public static Range toRange(IOpenable openable, int offset, int length) throws JavaModelException {
        Range range = newRange();
        if (offset > 0 || length > 0) {
            int[] loc = null;
            int[] endLoc = null;
            IBuffer buffer = openable.getBuffer();
            if (buffer != null) {
                loc = JsonRpcHelpers.toLine(buffer, offset);
                endLoc = JsonRpcHelpers.toLine(buffer, offset + length);
            }
            if (loc == null) {
                loc = new int[2];
            }
            if (endLoc == null) {
                endLoc = new int[2];
            }
            setPosition(range.getStart(), loc);
            setPosition(range.getEnd(), endLoc);
        }
        return range;
    }

    /**
     * Creates a new {@link Range} with its start and end {@link Position}s set to
     * line=0, character=0
     *
     * @return a new {@link Range};
     */
    public static Range newRange() {
        return new Range(new Position(), new Position());
    }

    private static void setPosition(Position position, int[] coords) {
        assert coords.length == 2;
        position.setLine(coords[0]);
        position.setCharacter(coords[1]);
    }

    /**
     * Returns uri for a compilation unit
     *
     * @param cu
     * @return
     */
    public static String toURI(ICompilationUnit cu) {
        return getFileURI(cu.getResource());
    }

    /**
     * Returns uri for a resource
     * 
     * @param resource
     * @return
     */
    public static String getFileURI(IResource resource) {
        return ResourceUtils.fixURI(
                resource.getRawLocationURI() == null ? resource.getLocationURI() : resource.getRawLocationURI());
    }

    public static boolean isHiddenGeneratedElement(IJavaElement element) {
        // generated elements are annotated with @Generated and they need to be filtered
        // out
        if (element instanceof IAnnotatable) {
            try {
                IAnnotation[] annotations = ((IAnnotatable) element).getAnnotations();
                if (annotations.length != 0) {
                    for (IAnnotation annotation : annotations) {
                        if (isSilencedGeneratedAnnotation(annotation)) {
                            return true;
                        }
                    }
                }
            } catch (JavaModelException e) {
                // ignore
            }
        }
        return false;
    }

    private static boolean isSilencedGeneratedAnnotation(IAnnotation annotation) throws JavaModelException {
        if ("javax.annotation.Generated".equals(annotation.getElementName())
                || "javax.annotation.processing.Generated".equals(annotation.getElementName())) {
            IMemberValuePair[] memberValuePairs = annotation.getMemberValuePairs();
            for (IMemberValuePair m : memberValuePairs) {
                if ("value".equals(m.getMemberName()) && IMemberValuePair.K_STRING == m.getValueKind()) {
                    if (m.getValue() instanceof String) {
                        return SILENCED_CODEGENS.contains(m.getValue());
                    } else if (m.getValue() instanceof Object[]) {
                        for (Object val : (Object[]) m.getValue()) {
                            if (SILENCED_CODEGENS.contains(val)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Enumeration for determining the location of a Java element. Either returns
     * with the name range only, or the extended source range around the name of the
     * element.
     */
    public enum LocationType {
        /**
         * This is range encapsulating only the name of the Java element.
         */
        NAME_RANGE {

            @Override
            ISourceRange getRange(IJavaElement element) throws JavaModelException {
                return getNameRange(element);
            }

        },
        /**
         * The range enclosing this element not including leading/trailing whitespace
         * but everything else like comments. This information is typically used to
         * determine if the client's cursor is inside the element.
         */
        FULL_RANGE {

            @Override
            ISourceRange getRange(IJavaElement element) throws JavaModelException {
                return getSourceRange(element);
            }

        };

        /* default */ abstract ISourceRange getRange(IJavaElement element) throws JavaModelException;
    }

    public static Location toLocation(IJavaElement element) throws JavaModelException {
        return toLocation(element, LocationType.NAME_RANGE);
    }

    /**
     * Creates a location for a given java element. Unlike {@link #toLocation} this
     * method can be called to return with a range that contains surrounding
     * comments (method body), not just the name of the Java element. Element can be
     * a {@link ICompilationUnit} or {@link IClassFile}
     *
     * @param element
     * @param type    the range type. The {@link LocationType#NAME_RANGE name} or
     *                {@link LocationType#FULL_RANGE full} range.
     * @return location or null
     * @throws JavaModelException
     */
    public static Location toLocation(IJavaElement element, LocationType type) throws JavaModelException {
        ICompilationUnit unit = (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
        IClassFile cf = (IClassFile) element.getAncestor(IJavaElement.CLASS_FILE);
        if (unit == null && cf == null) {
            return null;
        }
        if (element instanceof ISourceReference) {
            ISourceRange nameRange = type.getRange(element);
            if (SourceRange.isAvailable(nameRange)) {
                if (cf == null) {
                    return toLocation(unit, nameRange.getOffset(), nameRange.getLength());
                } else {
                    return toLocation(cf, nameRange.getOffset(), nameRange.getLength());
                }
            }
        }
        return null;
    }

    public static ISourceRange getNameRange(IJavaElement element) throws JavaModelException {
        ISourceRange nameRange = null;
        if (element instanceof IMember) {
            IMember member = (IMember) element;
            nameRange = member.getNameRange();
            if ((!SourceRange.isAvailable(nameRange))) {
                nameRange = member.getSourceRange();
            }
        } else if (element instanceof ITypeParameter || element instanceof ILocalVariable) {
            nameRange = ((ISourceReference) element).getNameRange();
        } else if (element instanceof ISourceReference) {
            nameRange = ((ISourceReference) element).getSourceRange();
        }
        if (!SourceRange.isAvailable(nameRange) && element.getParent() != null) {
            nameRange = getNameRange(element.getParent());
        }
        return nameRange;
    }

    private static ISourceRange getSourceRange(IJavaElement element) throws JavaModelException {
        ISourceRange sourceRange = null;
        if (element instanceof IMember) {
            IMember member = (IMember) element;
            sourceRange = member.getSourceRange();
        } else if (element instanceof ITypeParameter || element instanceof ILocalVariable) {
            sourceRange = ((ISourceReference) element).getSourceRange();
        } else if (element instanceof ISourceReference) {
            sourceRange = ((ISourceReference) element).getSourceRange();
        }
        if (!SourceRange.isAvailable(sourceRange) && element.getParent() != null) {
            sourceRange = getSourceRange(element.getParent());
        }
        return sourceRange;
    }

    /**
     * Creates location to the given offset and length for the compilation unit
     *
     * @param unit
     * @param offset
     * @param length
     * @return location or null
     * @throws JavaModelException
     */
    public static Location toLocation(ICompilationUnit unit, int offset, int length) throws JavaModelException {
        return new Location(ResourceUtils.toClientUri(toURI(unit)), toRange(unit, offset, length));
    }

    /**
     * Creates a default location for the class file.
     *
     * @param classFile
     * @return location
     * @throws JavaModelException
     */
    public static Location toLocation(IClassFile classFile) throws JavaModelException {
        return toLocation(classFile, 0, 0);
    }

    /**
     * Creates location to the given offset and length for the class file.
     *
     * @param unit
     * @param offset
     * @param length
     * @return location
     * @throws JavaModelException
     */
    public static Location toLocation(IClassFile classFile, int offset, int length) throws JavaModelException {
        String uriString = toUri(classFile);
        if (uriString != null) {
            Range range = toRange(classFile, offset, length);
            return new Location(uriString, range);
        }
        return null;
    }

    /**
     * Check if a URI starts with a leading slash.
     *
     * @param uriString
     * @return boolean
     */
    public static boolean hasLeadingSlash(String uriString) {
        return uriString.startsWith("/");
    }

    /**
     * Check if a URI follows a valid URI-template (level-1) specified by
     * <a href="https://datatracker.ietf.org/doc/html/rfc6570">RFC 6570</a>.
     *
     * @param uriString
     * @return boolean
     */
    public static boolean isValidLevel1URI(String uriString) {
        return uriString.matches(LEVEL1_URI_REGEX);
    }

    /**
     * Returns a list of all accessors (getter and setter) of the given field.
     * Note that for boolean fields the accessor of the form "isField" is retuned
     * "getField" is not present.
     * 
     * @param unit      the compilation unit the field belongs to
     * @param field     the accesors of this field are returned
     * @return          a list of accessor methods
     * @throws JavaModelException
     */
    public static List<IMethod> getFieldAccessors(ICompilationUnit unit, IField field) throws JavaModelException {
        List<IMethod> accessors = new ArrayList<IMethod>();
        String fieldName = field.getElementName();
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        List<String> accessorNames = new ArrayList<String>();
        accessorNames.add("get" + fieldName);
        accessorNames.add("set" + fieldName);
        accessorNames.add("is" + fieldName); 
        
        for (IType type : unit.getAllTypes()) {
            for (IMethod method : type.getMethods()) {
                String methodName = method.getElementName();
                if (accessorNames.contains(methodName))
                    accessors.add(method);
            }
        }
        return accessors;
    }

	public static void waitForLifecycleJobs(IProgressMonitor monitor) {
		try {
			Job.getJobManager().join(DocumentLifeCycleHandler.DOCUMENT_LIFE_CYCLE_JOBS, monitor);
		} catch (OperationCanceledException ignorable) {
			// No need to pollute logs when query is cancelled
		} catch (Exception e) {
			JavaLanguageServerPlugin.logException(e.getMessage(), e);
		}
	}

	public static int toOffset(IBuffer buffer, int line, int column) {
		return JsonRpcHelpers.toOffset(buffer, line, column);
	}
}
