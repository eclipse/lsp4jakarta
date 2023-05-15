/*******************************************************************************
* Copyright (c) 2019, 2022 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.RecordDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionParams;
import org.eclipse.lsp4jakarta.commons.JavaCursorContextKind;
import org.eclipse.lsp4jakarta.commons.JavaCursorContextResult;
import org.eclipse.lsp4jakarta.jdt.codeAction.CodeActionHandler;
import org.eclipse.lsp4jakarta.jdt.core.annotations.AnnotationDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.jax_rs.Jax_RSClassDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.jax_rs.ResourceMethodDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.jsonb.JsonbDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.jsonp.JsonpDiagnosticCollector;
import org.eclipse.lsp4jakarta.jdt.core.persistence.PersistenceEntityDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.persistence.PersistenceMapKeyDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.servlet.FilterDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.servlet.ListenerDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.servlet.ServletDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.websocket.WebSocketDiagnosticsCollector;

/**
 * JDT manager for Java files Modified from
 * https://github.com/eclipse/lsp4mp/blob/master/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/PropertiesManagerForJava.java
 * with methods modified and removed to fit the purposes of the Jakarta Language
 * Server
 * 
 */
public class JDTServicesManager {

    private List<DiagnosticsCollector> diagnosticsCollectors = new ArrayList<>();

    private static final JDTServicesManager INSTANCE = new JDTServicesManager();

    private final CodeActionHandler codeActionHandler;

    public static JDTServicesManager getInstance() {
        return INSTANCE;
    }

    private JDTServicesManager() {
        diagnosticsCollectors.add(new ServletDiagnosticsCollector());
        diagnosticsCollectors.add(new AnnotationDiagnosticsCollector());
        diagnosticsCollectors.add(new FilterDiagnosticsCollector());
        diagnosticsCollectors.add(new ListenerDiagnosticsCollector());
        diagnosticsCollectors.add(new BeanValidationDiagnosticsCollector());
        diagnosticsCollectors.add(new PersistenceEntityDiagnosticsCollector());
        diagnosticsCollectors.add(new PersistenceMapKeyDiagnosticsCollector());
        diagnosticsCollectors.add(new ResourceMethodDiagnosticsCollector());
        diagnosticsCollectors.add(new Jax_RSClassDiagnosticsCollector());
        diagnosticsCollectors.add(new JsonbDiagnosticsCollector());
        diagnosticsCollectors.add(new ManagedBeanDiagnosticsCollector());
        diagnosticsCollectors.add(new DependencyInjectionDiagnosticsCollector());
        diagnosticsCollectors.add(new JsonpDiagnosticCollector());
        diagnosticsCollectors.add(new WebSocketDiagnosticsCollector());
        // comment it out as the collector is doing nothing
        // diagnosticsCollectors.add(new TransactionsDiagnosticsCollector());
        this.codeActionHandler = new CodeActionHandler();
    }

    /**
     * Returns diagnostics for the given uris from the JakartaDiagnosticsParams.
     * 
     * @param javaParams the diagnostics parameters
     * @return diagnostics
     */
    public List<PublishDiagnosticsParams> getJavaDiagnostics(JakartaDiagnosticsParams javaParams) {
        return getJavaDiagnostics(javaParams.getUris(), new NullProgressMonitor());
    }

    /**
     * Returns diagnostics for the given uris
     * 
     * @param uris the list of uris to collect diagnostics for
     * @return diagnostics
     */
    public List<PublishDiagnosticsParams> getJavaDiagnostics(List<String> uris,
            IProgressMonitor monitor) {
        if (uris == null) {
            return Collections.emptyList();
        }

        List<PublishDiagnosticsParams> publishDiagnostics = new ArrayList<PublishDiagnosticsParams>();
        for (String uri : uris) {
            List<Diagnostic> diagnostics = new ArrayList<>();
            URI u = JDTUtils.toURI(uri);
            ICompilationUnit unit = JDTUtils.resolveCompilationUnit(u);
            for (DiagnosticsCollector d : diagnosticsCollectors) {
                if (monitor.isCanceled()) {
                    break;
                }
                d.collectDiagnostics(unit, diagnostics);
            }
            PublishDiagnosticsParams publishDiagnostic = new PublishDiagnosticsParams(uri, diagnostics);
            publishDiagnostics.add(publishDiagnostic);
            if (monitor.isCanceled()) {
                return Collections.emptyList();
            }
        }
        return publishDiagnostics;
    }

    /**
     * @author ankushsharma
     * @brief Gets all snippet contexts that exist in the current project classpath
     * @param uri            - String representing file from which to derive project
     *                       classpath
     * @param snippetContext - get all the context fields from the snippets and
     *                       check if they exist in this method
     * @return List<String>
     */
    public List<String> getExistingContextsFromClassPath(String uri, List<String> snippetContexts) {
        // Initialize the list that will hold the classpath
        List<String> classpath = new ArrayList<>();
        // Convert URI into a compilation unit
        ICompilationUnit unit = JDTUtils.resolveCompilationUnit(JDTUtils.toURI(uri));
        // Get Java Project
        IJavaProject project = unit.getJavaProject();
        // Get Java Project
        if (project != null) {
            snippetContexts.forEach(ctx -> {
                IType classPathctx = null;
                try {
                    classPathctx = project.findType(ctx);
                    if (classPathctx != null) {
                        classpath.add(ctx);
                    } else {
                        classpath.add(null);
                    }
                } catch (JavaModelException e) {
                    JavaLanguageServerPlugin.logException("Failed to retrieve projectContext from JDT...", e);
                    classpath.add(null);
                }
            });
        } else {
            // Populate the Array with nulls up to length of snippetContext
            snippetContexts.forEach(ctx -> {
                classpath.add(null);
            });
        }

        // FOR NOW, append package name and class name to the list in order for LS to
        // resolve ${packagename} and ${classname} variables
        String className = unit.getElementName();
        if (className.endsWith(".java") == true) {
            className = className.substring(0, className.length() - 5);
        }
        String packageName = unit.getParent() != null ? unit.getParent().getElementName() : "";
        classpath.add(packageName);
        classpath.add(className);

        return classpath;
    }

    public List<CodeAction> getCodeAction(JakartaJavaCodeActionParams params, JDTUtils utils, IProgressMonitor monitor)
            throws JavaModelException {
        return codeActionHandler.codeAction(params, utils, monitor);
    }

	/**
	 * Returns the cursor context for the given file and cursor position.
	 *
	 * @param params  the completion params that provide the file and cursor
	 *                position to get the context for
	 * @param utils   the jdt utils
	 * @param monitor the progress monitor
	 * @return the cursor context for the given file and cursor position
	 * @throws JavaModelException when the buffer for the file cannot be accessed or
	 *                            the Java model cannot be accessed
	 */
	public JavaCursorContextResult javaCursorContext(JakartaJavaCompletionParams params, JDTUtils utils,
			IProgressMonitor monitor) throws JavaModelException {
		String uri = params.getUri();
		ITypeRoot typeRoot = resolveTypeRoot(uri, utils, monitor);

		if (typeRoot == null) {
			return new JavaCursorContextResult(JavaCursorContextKind.IN_EMPTY_FILE, "");
		}
		CompilationUnit ast = ASTResolving.createQuickFixAST((ICompilationUnit) typeRoot, monitor);

		JavaCursorContextKind kind = getJavaCursorContextKind(params, typeRoot, ast, utils, monitor);
		String prefix = getJavaCursorPrefix(params, typeRoot, ast, utils, monitor);

		return new JavaCursorContextResult(kind, prefix);
	}

	private static JavaCursorContextKind getJavaCursorContextKind(JakartaJavaCompletionParams params,
			ITypeRoot typeRoot, CompilationUnit ast, JDTUtils utils, IProgressMonitor monitor)
			throws JavaModelException {

		if (typeRoot.findPrimaryType() == null) {
			return JavaCursorContextKind.IN_EMPTY_FILE;
		}

		Position completionPosition = params.getPosition();
		int completionOffset = utils.toOffset(typeRoot.getBuffer(), completionPosition.getLine(),
				completionPosition.getCharacter());

		NodeFinder nodeFinder = new NodeFinder(ast, completionOffset, 0);
		ASTNode node = nodeFinder.getCoveringNode();
		ASTNode oldNode = node;
		while (node != null && (!(node instanceof AbstractTypeDeclaration)
				|| offsetOfFirstNonAnnotationModifier((BodyDeclaration) node) >= completionOffset)) {
			if (node.getParent() != null) {
				switch (node.getParent().getNodeType()) {
				case ASTNode.METHOD_DECLARATION:
				case ASTNode.FIELD_DECLARATION:
				case ASTNode.ENUM_CONSTANT_DECLARATION:
				case ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION:
					if (!ASTNodeUtils.isAnnotation(node) && node.getStartPosition() < completionOffset) {
						return JavaCursorContextKind.NONE;
					}
					break;
				}
			}
			oldNode = node;
			node = node.getParent();
		}

		if (node == null) {
			// we are likely before or after the type root class declaration
			FindWhatsBeingAnnotatedASTVisitor visitor = new FindWhatsBeingAnnotatedASTVisitor(completionOffset, false);
			oldNode.accept(visitor);
			switch (visitor.getAnnotatedNodeType()) {
			case ASTNode.TYPE_DECLARATION:
			case ASTNode.ANNOTATION_TYPE_DECLARATION:
			case ASTNode.ENUM_DECLARATION:
			case ASTNode.RECORD_DECLARATION: {
				if (visitor.isInAnnotations()) {
					return JavaCursorContextKind.IN_CLASS_ANNOTATIONS;
				}
				return JavaCursorContextKind.BEFORE_CLASS;
			}
			default:
				return JavaCursorContextKind.NONE;
			}
		}

		AbstractTypeDeclaration typeDeclaration = (AbstractTypeDeclaration) node;
		FindWhatsBeingAnnotatedASTVisitor visitor = new FindWhatsBeingAnnotatedASTVisitor(completionOffset);
		typeDeclaration.accept(visitor);
		switch (visitor.getAnnotatedNodeType()) {
		case ASTNode.TYPE_DECLARATION:
		case ASTNode.ANNOTATION_TYPE_DECLARATION:
		case ASTNode.ENUM_DECLARATION:
		case ASTNode.RECORD_DECLARATION:
			return visitor.isInAnnotations() ? JavaCursorContextKind.IN_CLASS_ANNOTATIONS
					: JavaCursorContextKind.BEFORE_CLASS;
		case ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION:
		case ASTNode.METHOD_DECLARATION:
			return visitor.isInAnnotations() ? JavaCursorContextKind.IN_METHOD_ANNOTATIONS
					: JavaCursorContextKind.BEFORE_METHOD;
		case ASTNode.FIELD_DECLARATION:
		case ASTNode.ENUM_CONSTANT_DECLARATION:
			return visitor.isInAnnotations() ? JavaCursorContextKind.IN_FIELD_ANNOTATIONS
					: JavaCursorContextKind.BEFORE_FIELD;
		default:
			return JavaCursorContextKind.IN_CLASS;
		}
	}

	private static @NonNull String getJavaCursorPrefix(JakartaJavaCompletionParams params, ITypeRoot typeRoot,
			CompilationUnit ast, JDTUtils utils, IProgressMonitor monitor) throws JavaModelException {
		Position completionPosition = params.getPosition();
		int completionOffset = utils.toOffset(typeRoot.getBuffer(), completionPosition.getLine(),
				completionPosition.getCharacter());

		String fileContents = null;
		try {
			IBuffer buffer = typeRoot.getBuffer();
			if (buffer == null) {
				return null;
			}
			fileContents = buffer.getContents();
		} catch (JavaModelException e) {
			return "";
		}
		if (fileContents == null) {
			return "";
		}
		int i;
		for (i = completionOffset; i > 0 && !Character.isWhitespace(fileContents.charAt(i - 1)); i--) {
		}
		return fileContents.substring(i, completionOffset);
	}

	/**
	 * Given the uri returns a {@link ITypeRoot}. May return null if it can not
	 * associate the uri with a Java file or class file.
	 *
	 * @param uri
	 * @param utils   JDT LS utilities
	 * @param monitor the progress monitor
	 * @return compilation unit
	 */
	private static ITypeRoot resolveTypeRoot(String uri, JDTUtils utils, IProgressMonitor monitor) {
		utils.waitForLifecycleJobs(monitor);
		final ICompilationUnit unit = utils.resolveCompilationUnit(uri);
		IClassFile classFile = null;
		if (unit == null) {
			classFile = utils.resolveClassFile(uri);
			if (classFile == null) {
				return null;
			}
		} else {
			if (!unit.getResource().exists() || monitor.isCanceled()) {
				return null;
			}
		}
		return unit != null ? unit : classFile;
	}

	/**
	 * Searches through the AST to figure out the following:
	 * <ul>
	 * <li>If an annotation were to be placed at the completionOffset, what type of
	 * node would it be annotating?</li>
	 * <li>Is the completionOffset within the list of annotations before a
	 * member?</li>
	 * </ul>
	 */
	private static class FindWhatsBeingAnnotatedASTVisitor extends ASTVisitor {

		private int completionOffset;
		private int closest = Integer.MAX_VALUE;
		private int annotatedNode = 0;
		private boolean visitedParentType;
		private boolean inAnnotations = false;

		public FindWhatsBeingAnnotatedASTVisitor(int completionOffset, boolean startingInParent) {
			this.completionOffset = completionOffset;
			this.visitedParentType = !startingInParent;
		}

		public FindWhatsBeingAnnotatedASTVisitor(int completionOffset) {
			this(completionOffset, true);
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			return visitNode(node);
		}

		@Override
		public boolean visit(FieldDeclaration node) {
			return visitNode(node);
		}

		@Override
		public boolean visit(EnumConstantDeclaration node) {
			return visitNode(node);
		}

		@Override
		public boolean visit(AnnotationTypeMemberDeclaration node) {
			return visitNode(node);
		}

		@Override
		public boolean visit(TypeDeclaration node) {
			return visitAbstractType(node);
		}

		@Override
		public boolean visit(EnumDeclaration node) {
			return visitAbstractType(node);
		}

		@Override
		public boolean visit(AnnotationTypeDeclaration node) {
			return visitAbstractType(node);
		}

		@Override
		public boolean visit(RecordDeclaration node) {
			return visitAbstractType(node);
		}

		private boolean visitAbstractType(AbstractTypeDeclaration node) {
			// we need to visit the children of the first type declaration,
			// since the visitor start visiting from the supplied node.
			if (!visitedParentType) {
				visitedParentType = true;
				return true;
			}
			return visitNode(node);
		}

		private boolean visitNode(BodyDeclaration node) {
			// ignore generated nodes
			if (isGenerated(node)) {
				return false;
			}
			// consider the start of the declaration to be after the annotations
			int start = node.modifiers().isEmpty() ? node.getStartPosition() : offsetOfFirstNonAnnotationModifier(node);
			if (start < closest && completionOffset <= start) {
				closest = node.getStartPosition();
				annotatedNode = node.getNodeType();
				inAnnotations = node.getStartPosition() < completionOffset && completionOffset <= start;
			}
			// We don't want to enter nested classes
			return false;
		}

		/**
		 * Returns the type of the node that an annotation placed at the completion
		 * offset would be annotating.
		 *
		 * @see org.eclipse.jdt.core.dom.ASTNode#getNodeType()
		 * @return the type of the node that an annotation placed at the completion
		 *         offset would be annotating
		 */
		public int getAnnotatedNodeType() {
			return annotatedNode;
		}

		/**
		 * Returns true if the completion offset is within the list of annotations
		 * preceding a body declaration (field, method, class declaration) or false
		 * otherwise.
		 *
		 * @return true if the completion offset is within the list of annotations
		 *         preceding a body declaration (field, method, class declaration) or
		 *         false otherwise
		 */
		public boolean isInAnnotations() {
			return inAnnotations;
		}

	}

	private static int offsetOfFirstNonAnnotationModifier(BodyDeclaration node) {
		List modifiers = node.modifiers();
		for (int i = 0; i < modifiers.size(); i++) {
			ASTNode modifier = (ASTNode) modifiers.get(i);
			if (!ASTNodeUtils.isAnnotation(modifier)) {
				return modifier.getStartPosition();
			}
		}
		if (node instanceof MethodDeclaration method) {
			if (method.getReturnType2() != null) {
				return method.getReturnType2().getStartPosition();
			}
			// package protected constructor
			return method.getName().getStartPosition();
		} else if (node instanceof FieldDeclaration field) {
			return field.getType().getStartPosition();
		} else {
			var type = (AbstractTypeDeclaration) node;
			int nameOffset = type.getName().getStartPosition();
			int keywordLength = (switch (type.getNodeType()) {
			case ASTNode.TYPE_DECLARATION -> ((TypeDeclaration) type).isInterface() ? "interface" : "class";
			case ASTNode.ENUM_DECLARATION -> "enum";
			case ASTNode.ANNOTATION_TYPE_DECLARATION -> "@interface";
			case ASTNode.RECORD_DECLARATION -> "record";
			default -> "";
			}).length();

			// HACK: this assumes the code contains one space between the keyword and the
			// type name, which isn't always the case
			return nameOffset - (keywordLength + 1);
		}
	}

	/**
	 * Returns true if the given node was generated by Project Lombok and false
	 * otherwise.
	 * 
	 * @param node the node to check if it's generated
	 * @return true if the given node was generated by Project Lombok and false
	 *         otherwise
	 */
	private static boolean isGenerated(ASTNode node) {
		try {
			return ((Boolean) node.getClass().getField("$isGenerated").get(node)).booleanValue();
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			return false;
		}
	}

}
