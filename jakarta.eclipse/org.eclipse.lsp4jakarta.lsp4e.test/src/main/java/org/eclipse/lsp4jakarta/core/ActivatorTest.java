package org.eclipse.lsp4jakarta.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.lsp4jakarta.lsp4e.Activator;
import org.junit.Test;

/**
* Sample integration test. In Eclipse, right-click > Run As > JUnit-Plugin. <br/>
* In Maven CLI, run "mvn integration-test".
*/
public class ActivatorTest {

	@Test
	public void veryStupidTest() {
		assertEquals("org.eclipse.lsp4jakarta.lsp4e.core",Activator.PLUGIN_ID);
		assertTrue("Plugin should be started", Activator.getDefault().started);
	}
}