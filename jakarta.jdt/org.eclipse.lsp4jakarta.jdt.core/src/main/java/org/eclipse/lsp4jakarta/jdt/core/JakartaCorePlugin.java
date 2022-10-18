/*******************************************************************************
* Copyright (c) 2020 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JakartaCorePlugin implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.lsp4jakarta.jdt.core"; //$NON-NLS-1$

    // The shared instance
    private static JakartaCorePlugin plugin;

    public void start(BundleContext context) throws Exception {
//    	super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
//		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JakartaCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Add the given Jakarta properties changed listener.
	 *
	 * @param listener the listener to add
	 */
	public static void log(IStatus status) {
//        getDefault().getLog().log(status);
    }

	/**
	 * Remove the given Jakarta properties changed listener.
	 *
	 * @param listener the listener to remove
	 */
	public static void logException(String errMsg, Throwable ex) {
//        getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, errMsg, ex));
    }

}
