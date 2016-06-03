/*******************************************************************************
 * Copyright (c) 2015 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springsource.ide.eclipse.commons.livexp.core;

import java.util.concurrent.Callable;

import org.springsource.ide.eclipse.commons.livexp.Activator;

import com.google.common.collect.ImmutableSet;

/**
 * This is like LiveSet / LiveSetVariable, but it can only be 'observed' not mutated.
 */
public abstract class ObservableSet<T> extends AsyncLiveExpression<ImmutableSet<T>> {

	/**
	 * Deprecated use one of the constructors that controls synch versus async behavior explicitly.
	 */
	@Deprecated
	public ObservableSet() {
		//Make it synch as that is 'backwards' compatible. Just in case existing code expects it.
		this(ImmutableSet.<T>of(), AsyncMode.SYNC, AsyncMode.SYNC);
	}

	public ObservableSet(ImmutableSet<T> initialValue, AsyncMode refreshMode, AsyncMode eventsMode) {
		super(initialValue, refreshMode, eventsMode);
	}

	public static <T> ObservableSet<T> constant(ImmutableSet<T> value) {
		return new ObservableSet<T>(value, AsyncMode.SYNC, AsyncMode.SYNC) {
			@Override
			protected ImmutableSet<T> compute() {
				return value;
			}

			@Override
			public void addListener(ValueListener<ImmutableSet<T>> l) {
				l.gotValue(this, value);
				//Beyond the initial notification ... we ignore listeners... we will never notify again since
				//constants can't change
			}
			@Override
			public void removeListener(ValueListener<ImmutableSet<T>> l) {
				//Ignore all listeners we will never notify anyone since
				//constants can't change
			}

			@Override
			public void syncRefresh() {
				//Ignore all refreshes... no need to refresh anything since
				//constants can't change
			}
		};
	}

	public ImmutableSet<T> getValues() {
		return getValue();
	}

	public boolean contains(T e) {
		return getValues().contains(e);
	}

	/**
	 * A lambda-friendly way to create a ObservableSet from a 'compute' function.
	 */
	public static <T> ObservableSet<T> create(Callable<ImmutableSet<T>> computer) {
		return new ObservableSet<T>() {
			protected ImmutableSet<T> compute() {
				try {
					return computer.call();
				} catch (Exception e) {
					Activator.log(e);
					return getValues();
				}
			}
		};
	}

}
