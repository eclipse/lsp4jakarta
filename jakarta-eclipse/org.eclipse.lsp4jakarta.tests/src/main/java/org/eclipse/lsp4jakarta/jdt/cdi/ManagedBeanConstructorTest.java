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
import org.eclipse.jakartals.commons.JakartaDiagnosticsParams;
import org.eclipse.jakartals.commons.JakartaJavaCodeActionParams;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.jakarta.jdt.JDTUtils;
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
                "If a managed bean does not have a constructor that takes no parameters, it must have a constructor annotated @Inject",
                DiagnosticSeverity.Error, "jakarta-cdi", "InvalidManagedBeanConstructor");

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d);

        // test expected quick-fix
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);
        TextEdit te = te(15, 44, 21, 1,
                "\nimport jakarta.inject.Inject;\n\n@Dependent\npublic class ManagedBeanConstructor {\n	private int a;\n	\n	@Inject\n	");
        CodeAction ca = ca(uri, "Insert @Inject", d, te);
        assertJavaCodeAction(codeActionParams, JDT_UTILS, ca);
    }

}
