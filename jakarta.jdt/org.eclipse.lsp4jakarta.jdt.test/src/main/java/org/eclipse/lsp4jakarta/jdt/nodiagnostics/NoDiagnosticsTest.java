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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		IJavaProject javaProject = loadJavaProject("demo-servlet-no-diagnostics", "");
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
	public static List<String> projectFileProvider() throws Exception {

		String packagePath = "src/main/java/io/openliberty/sample/jakarta/";
		String basePath = System.getProperty("user.dir")
				+ "/projects/demo-servlet-no-diagnostics/" + packagePath;

		List<String> results = new ArrayList<String>();
		File[] directories = new File(basePath).listFiles();
		// If this pathname does not denote a directory, then listFiles() returns null.

		for (File directory : directories) {
			// Check for directory.
			if (directory.isDirectory()) {
				File[] files = new File(basePath + "/" + directory.getName() + "/").listFiles();
				for (File file : files) {
					if (file.isFile()) {
						// Add test file path.
						results.add(packagePath + directory.getName() + "/" + file.getName());
					}
				}
			}

		}
		return results;
	}
}
