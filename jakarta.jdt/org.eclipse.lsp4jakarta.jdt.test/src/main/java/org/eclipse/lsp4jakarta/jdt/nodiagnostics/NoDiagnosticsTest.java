/*******************************************************************************
* Copyright (c) 2023 IBM Corporation and others.
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

package org.eclipse.lsp4jakarta.jdt.nodiagnostics;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaDiagnostics;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NoDiagnosticsTest extends BaseJakartaTest {

	@Parameter
	public String filePath;

	protected static JDTUtils JDT_UTILS = new JDTUtils();

	@Test
	public void checkForNoDiagnostics() throws Exception {

		JDTUtils utils = JDT_UTILS;

		IJavaProject javaProject = loadJavaProject("jakarta-noDiagnostic-sample", "");
		IFile javaFile = javaProject.getProject()
				.getFile(new Path(filePath));
		String uri = javaFile.getLocation().toFile().toURI().toString();

		JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
		diagnosticsParams.setUris(Arrays.asList(uri));

		// should be no diagnostics in the file.
		assertJavaDiagnostics(diagnosticsParams, utils);

	}

	// list of java files in demo-servlet-no-diagnostics.
	@Parameters
	public static String[] projectFileProvider() throws Exception {

		String basePath = "src/main/java/io/openliberty/sample/jakarta/";
		String files[] = { basePath + "annotations/GeneratedAnnotation.java",
				basePath + "annotations/PostConstructAnnotation.java",
				basePath + "annotations/PreDestroyAnnotation.java",
				basePath + "annotations/ResourceAnnotation.java",

				basePath + "beanvalidation/FieldConstraintValidation.java",
				basePath + "beanvalidation/MethodConstraintValidation.java",
				basePath + "beanvalidation/ValidConstraints.java",

				basePath + "cdi/InjectAndDisposesObservesObservesAsync.java",
				basePath + "cdi/ManagedBean.java",
				basePath + "cdi/ManagedBeanConstructor.java",
				basePath + "cdi/MultipleDisposes.java",
				basePath + "cdi/ProducesAndDisposesObservesObservesAsync.java",
				basePath + "cdi/ProducesAndInjectTogether.java",
				basePath + "cdi/ScopeDeclaration.java",

				basePath + "di/Greeting.java",
				basePath + "di/MultipleConstructorWithInject.java",

				basePath + "jax_rs/MultipleEntityParamsResourceMethod.java",
				basePath + "jax_rs/NoPublicConstructorClass.java",
				basePath + "jax_rs/NoPublicConstructorProviderClass.java",
				basePath + "jax_rs/NotPublicResourceMethod.java",
				basePath + "jax_rs/RootResourceClassConstructorsDiffLen.java",
				basePath + "jax_rs/RootResourceClassConstructorsEqualLen.java",

				basePath + "jsonb/ExtraJsonbCreatorAnnotations.java",
				basePath + "jsonb/JsonbTransientDiagnostic.java",

				basePath + "jsonp/CreatePointerInvalidTarget.java",

				basePath + "persistence/EntityMissingConstructor.java",
				basePath + "persistence/FinalModifiers.java",
				basePath + "persistence/MapKeyAndMapKeyClassTogether.java",
				basePath + "persistence/MultipleMapKeyAnnotations.java",

				basePath + "servlet/DontExtendHttpServlet.java",
				basePath + "servlet/InvalidWebServlet.java",

				basePath + "websocket/AnnotationTest.java",
				basePath + "websocket/DuplicateOnMessage.java",
				basePath + "websocket/InvalidParamType.java",
				basePath + "websocket/ServerEndpointDuplicateVariableURI.java",
				basePath + "websocket/ServerEndpointInvalidTemplateURI.java",
				basePath + "websocket/ServerEndpointNoSlash.java",
				basePath + "websocket/ServerEndpointRelativePathTest.java",

				basePath + "websockets/PathParamURIWarningTest.java"
		};

		return files;

	}

}
