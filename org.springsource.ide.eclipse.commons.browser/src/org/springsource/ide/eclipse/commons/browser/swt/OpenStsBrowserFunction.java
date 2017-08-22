/*******************************************************************************
 * Copyright (c) 2014 Pivotal Software, Inc. and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/

package org.springsource.ide.eclipse.commons.browser.swt;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.internal.browser.WebBrowserPreference;
import org.eclipse.ui.statushandlers.StatusManager;
import org.springsource.ide.eclipse.commons.browser.BrowserPlugin;
import org.springsource.ide.eclipse.commons.browser.IBrowserToEclipseFunction;

@SuppressWarnings("restriction")
public class OpenStsBrowserFunction implements IBrowserToEclipseFunction {

	@Override
	public void call(String url) {
		try {
			if (WebBrowserPreference.getBrowserChoice() == WebBrowserPreference.EXTERNAL) {
				PlatformUI.getWorkbench().getBrowserSupport().createBrowser(null).openURL(new URL(url));
			} else {
				WebBrowserEditorInput input = new WebBrowserEditorInput(new URL(url), SWT.NONE, url);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.openEditor(input, StsBrowserEditor.EDITOR_ID);
			}
		}
		catch (MalformedURLException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, BrowserPlugin.PLUGIN_ID, "Bad page url: " + url, e));
		}
		catch (PartInitException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, BrowserPlugin.PLUGIN_ID, "Could not find brwoser editor extension", e));
		}
	}
}