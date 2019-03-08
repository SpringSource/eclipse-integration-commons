/*******************************************************************************
 * Copyright (c) 2013 Pivotal Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.springsource.ide.eclipse.dashboard.ui.actions;


/**
 * Interface that the new Dashboard implements so that this plugin can call methods on
 * it without creating a direct dependency on the new gettingstarted plugins.
 * 
 * @author Kris De Volder
 */
public interface IDashboardWithPages {

	/**
	 * Make page with given id visible. Does nothing if page with this id can not
	 * be found.
	 * 
	 * @return Whether or not the page with given id was found.
	 */
	boolean setActivePage(String pageId);
	
}
