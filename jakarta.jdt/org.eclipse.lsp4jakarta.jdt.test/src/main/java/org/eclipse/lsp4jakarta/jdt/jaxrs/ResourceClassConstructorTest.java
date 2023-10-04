/*******************************************************************************
 * Copyright (c) 2021, 2023 IBM Corporation, Matthew Shocrylas and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation, Matthew Shocrylas - initial API and implementation
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.jaxrs;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaDiagnostics;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.d;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.commons.JakartaJavaDiagnosticsParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.utils.IJDTUtils;
import org.eclipse.lsp4jakarta.jdt.internal.core.ls.JDTUtilsLSImpl;
import org.junit.Test;

public class ResourceClassConstructorTest extends BaseJakartaTest {
	protected static IJDTUtils IJDT_UTILS = JDTUtilsLSImpl.getInstance();

	@Test
	public void MultipleConstructorsWithEqualParams() throws Exception {
		IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
		IFile javaFile = javaProject.getProject().getFile(new Path(
				"src/main/java/io/openliberty/sample/jakarta/jaxrs/RootResourceClassConstructorsEqualLen.java"));
		String uri = javaFile.getLocation().toFile().toURI().toString();

		JakartaJavaDiagnosticsParams diagnosticsParams = new JakartaJavaDiagnosticsParams();
		diagnosticsParams.setUris(Arrays.asList(uri));

		// test expected diagnostics
		Diagnostic d1 = d(7, 8, 45,
				"Multiple constructors have the same number of parameters, it might be ambiguous which constructor is used.",
				DiagnosticSeverity.Warning, "jakarta-jaxrs", "AmbiguousConstructors");

		Diagnostic d2 = d(11, 8, 45,
				"Multiple constructors have the same number of parameters, it might be ambiguous which constructor is used.",
				DiagnosticSeverity.Warning, "jakarta-jaxrs", "AmbiguousConstructors");

		assertJavaDiagnostics(diagnosticsParams, IJDT_UTILS, d1, d2);

	}

	@Test
	public void MultipleConstructorsWithDifferentLength() throws Exception {
		IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
		IFile javaFile = javaProject.getProject().getFile(new Path(
				"src/main/java/io/openliberty/sample/jakarta/jaxrs/RootResourceClassConstructorsDiffLen.java"));
		String uri = javaFile.getLocation().toFile().toURI().toString();

		JakartaJavaDiagnosticsParams diagnosticsParams = new JakartaJavaDiagnosticsParams();
		diagnosticsParams.setUris(Arrays.asList(uri));

		// test expected diagnostics
		Diagnostic d = d(7, 8, 44,
				"This constructor is unused, as root resource classes will only use the constructor with the most parameters.",
				DiagnosticSeverity.Warning, "jakarta-jaxrs", "UnusedConstructor");

		assertJavaDiagnostics(diagnosticsParams, IJDT_UTILS, d);
	}

	@Test
	public void NoPublicConstructor() throws Exception {
		IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
		IFile javaFile = javaProject.getProject()
				.getFile(new Path("src/main/java/io/openliberty/sample/jakarta/jaxrs/NoPublicConstructorClass.java"));
		String uri = javaFile.getLocation().toFile().toURI().toString();

		JakartaJavaDiagnosticsParams diagnosticsParams = new JakartaJavaDiagnosticsParams();
		diagnosticsParams.setUris(Arrays.asList(uri));

		// test expected diagnostics
		Diagnostic d1 = d(7, 12, 36,
				"Root resource classes are instantiated by the JAX-RS runtime and MUST have a public constructor.",
				DiagnosticSeverity.Error, "jakarta-jaxrs", "NoPublicConstructors");

		Diagnostic d2 = d(11, 14, 38,
				"Root resource classes are instantiated by the JAX-RS runtime and MUST have a public constructor.",
				DiagnosticSeverity.Error, "jakarta-jaxrs", "NoPublicConstructors");

		assertJavaDiagnostics(diagnosticsParams, IJDT_UTILS, d1, d2);
	}

	@Test
	public void NoPublicConstructorProviderClass() throws Exception {
		IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
		IFile javaFile = javaProject.getProject()
				.getFile(new Path(
						"src/main/java/io/openliberty/sample/jakarta/jaxrs/NoPublicConstructorProviderClass.java"));
		String uri = javaFile.getLocation().toFile().toURI().toString();

		JakartaJavaDiagnosticsParams diagnosticsParams = new JakartaJavaDiagnosticsParams();
		diagnosticsParams.setUris(Arrays.asList(uri));

		Diagnostic d1 = d(19, 12, 44,
				"Provider classes are instantiated by the JAX-RS runtime and MUST have a public constructor.",
				DiagnosticSeverity.Error, "jakarta-jaxrs", "NoPublicConstructors");

		Diagnostic d2 = d(23, 14, 46,
				"Provider classes are instantiated by the JAX-RS runtime and MUST have a public constructor.",
				DiagnosticSeverity.Error, "jakarta-jaxrs", "NoPublicConstructors");

		assertJavaDiagnostics(diagnosticsParams, IJDT_UTILS, d1, d2);
	}
}
