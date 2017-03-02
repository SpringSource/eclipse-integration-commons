/*******************************************************************************
 * Copyright (c) 2015-2017 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springsource.ide.eclipse.commons.livexp.ui.util;

import org.eclipse.swt.widgets.Control;
import org.springsource.ide.eclipse.commons.livexp.ui.IPageWithSections;
import org.springsource.ide.eclipse.commons.livexp.ui.Reflowable;

/**
 * @author Kris De Volder
 */
public class ReflowUtil {

	public static void reflow(IPageWithSections owner, Control changed) {
		boolean reflowed = findAndReflowControl(changed);
		if (!reflowed && owner instanceof Reflowable) {
			((Reflowable) owner).reflow();
		}
	}

	public static boolean findAndReflowControl(Control control) {
		while (control!=null) {
			if (control instanceof Reflowable) {
				if (((Reflowable)control).reflow()) {
					return true;
				}
			}
			control = control.getParent();
		}
		//No Reflowable found. Sorry!
		return false;
	}

}
