/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.annotations;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaCodeAction;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaDiagnostics;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.ca;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.createCodeActionParams;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.d;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.te;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.junit.Test;

public class PostConstructAnnotationTest extends BaseJakartaTest {

	protected static JDTUtils JDT_UTILS = new JDTUtils();

	@Test
	public void GeneratedAnnotation() throws Exception {
		IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
		IFile javaFile = javaProject.getProject().getFile(
				new Path("src/main/java/io/openliberty/sample/jakarta/annotations/PostConstructAnnotation.java"));
		String uri = javaFile.getLocation().toFile().toURI().toString();

		JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
		diagnosticsParams.setUris(Arrays.asList(uri));

		// expected Diagnostics

		Diagnostic d1 = d(15, 16, 28, "A method with the annotation @PostConstruct must be void.",
				DiagnosticSeverity.Error, "jakarta-annotations", "PostConstructReturnType");

		Diagnostic d2 = d(20, 13, 25, "A method with the annotation @PostConstruct must not have any parameters.",
				DiagnosticSeverity.Error, "jakarta-annotations", "PostConstructParams");

		Diagnostic d3 = d(25, 13, 25, "A method with the annotation @PostConstruct must not throw checked exceptions.",
				DiagnosticSeverity.Warning, "jakarta-annotations", "PostConstructException");

		assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d1, d2, d3);

		JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d2);
		TextEdit te = te(19, 1, 20, 1, "");
		TextEdit te1 = te(20, 26, 20, 37, "");
		CodeAction ca = ca(uri, "Remove @PostConstruct", d2, te);
		CodeAction ca1 = ca(uri, "Remove all parameters", d2, te1);
		assertJavaCodeAction(codeActionParams, JDT_UTILS, ca, ca1);

	}

}
