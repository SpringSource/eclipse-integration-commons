/*******************************************************************************
 * Copyright (c) 2014 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springsource.ide.eclipse.commons.frameworks.core.workspace;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * An instance of this class provides a means to register
 * listeners that get notified when classpath for a IJavaProject
 * changes.
 *
 * @author Kris De Volder
 */
public class ClasspathListenerManager {

	public interface ClasspathListener {
		public abstract void classpathChanged(IJavaProject jp);
	}

	private class MyListener implements IElementChangedListener {

		//@Override
		public void elementChanged(ElementChangedEvent event) {
			visit(event.getDelta());
		}

		private void visit(IJavaElementDelta delta) {
			IJavaElement el = delta.getElement();
			switch (el.getElementType()) {
			case IJavaElement.JAVA_MODEL:
				visitChildren(delta);
				break;
			case IJavaElement.JAVA_PROJECT:
				if (isClasspathChanged(delta.getFlags())) {
					listener.classpathChanged((IJavaProject)el);
				}
				break;
			default:
				break;
			}
		}

		private boolean isClasspathChanged(int flags) {
			return 0!= (flags & (
					IJavaElementDelta.F_CLASSPATH_CHANGED |
					IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED
			));
		}

		public void visitChildren(IJavaElementDelta delta) {
			for (IJavaElementDelta c : delta.getAffectedChildren()) {
				visit(c);
			}
		}
	}

	private ClasspathListener listener;
	private MyListener myListener;

	public ClasspathListenerManager(ClasspathListener listener) {
		this.listener = listener;
		JavaCore.addElementChangedListener(myListener=new MyListener(), ElementChangedEvent.POST_CHANGE);
	}

	public void dispose() {
		if (myListener!=null) {
			JavaCore.removeElementChangedListener(myListener);
			myListener = null;
		}
	}

}