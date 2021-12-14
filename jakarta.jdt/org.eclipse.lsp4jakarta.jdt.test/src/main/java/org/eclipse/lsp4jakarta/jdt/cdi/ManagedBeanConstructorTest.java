/*******************************************************************************
* Copyright (c) 2021 IBM Corporation.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Hani Damlaj
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.cdi;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.*;

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

public class ManagedBeanConstructorTest extends BaseJakartaTest {

    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void managedBeanAnnotations() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/cdi/ManagedBeanConstructor.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // test expected diagnostic
        Diagnostic d = d(21, 8, 30,
                "The annotation @Inject must define a managed bean constructor that takes parameters, or the managed bean must resolve to having a no-arg constructor instead.",
                DiagnosticSeverity.Error, "jakarta-cdi", "InvalidManagedBeanConstructor");

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d);

        // test expected quick-fix
        JakartaJavaCodeActionParams codeActionParams1 = createCodeActionParams(uri, d);
        TextEdit te1 = te(15, 44, 21, 1,
                "\nimport jakarta.inject.Inject;\n\n@Dependent\npublic class ManagedBeanConstructor {\n	private int a;\n	\n	@Inject\n	");
        TextEdit te2 = te(19, 1, 19, 1,
        		"protected ManagedBeanConstructor() {\n\t}\n\n\t");
        TextEdit te3 = te(19, 1, 19, 1,
                "public ManagedBeanConstructor() {\n\t}\n\n\t");
        CodeAction ca1 = ca(uri, "Insert @Inject", d, te1);
        CodeAction ca2 = ca(uri, "Add a no-arg protected constructor to this class", d, te2);
        CodeAction ca3 = ca(uri, "Add a no-arg public constructor to this class", d, te3);
        assertJavaCodeAction(codeActionParams1, JDT_UTILS, ca1, ca2, ca3);
    }

}
