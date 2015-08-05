/*******************************************************************************
 * Copyright (c) 2012 Pivotal Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.springsource.ide.eclipse.commons.livexp.core;

import org.eclipse.core.runtime.ListenerList;

/**
 * A 'live' expression is something that conceptually one would like to think of as an expression
 * that returns a value. However, this expression provides a listener-style interface so that
 * interested parties can subscribe to be notified when the value of the expression changes.
 */
public abstract class LiveExpression<V> {

	public static final LiveExpression<Boolean> TRUE = constant(true);
	public static final LiveExpression<Boolean> FALSE = constant(false);

	private ListenerList fListeners = new ListenerList();

	/**
	 * The last computed value of the expression.
	 */
	protected V value;

	public LiveExpression(V initialValue) {
		this.value = initialValue;
	}

	public LiveExpression() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Clients may call this method to request a recomputation of the expression's value from its inputs.
	 */
	public void refresh() {
		//V oldValue = value;
		V newValue = compute();
		if (!equals(newValue, value)) {
			value = newValue;
			changed();
		}
	}

	/**
	 * Implementation of value equals that works if either one of the values is null.
	 */
	private static <V> boolean equals(V a, V b) {
		if (a==null||b==null) {
			return a==b;
		} else {
			return a.equals(b);
		}
	}

	/**
	 * Declare that this liveExpression depends on some other live expression. This ensures
	 * that this expression will be refreshed if the value of the other expression changes.
	 */
	public <O> LiveExpression<V> dependsOn(LiveExpression<O> other) {
		other.addListener(new ValueListener<O>() {
			public void gotValue(LiveExpression<O> exp, O value) {
				refresh();
			}
		});
		return this;
	};


	protected abstract V compute();

	protected void changed() {
		Object[] listeners = fListeners.getListeners();
		for (Object _l : listeners) {
			@SuppressWarnings("unchecked")
			ValueListener<V> l = (ValueListener<V>) _l;
			l.gotValue(this, value);
		}
	}

	/**
	 * Retrieves the current (cached) value of the expression.
	 */
	public V getValue() {
		return value;
	}

	public void addListener(ValueListener<V> l) {
		fListeners.add(l);
		l.gotValue(this, value);
	}

	public void removeListener(ValueListener<V> l) {
		fListeners.remove(l);
	}

	public static <V> LiveExpression<V> constant(final V value) {
		//TODO: Constant expression can be implemented more efficiently they do not need really any of the
		// super class infrastructure since the value of a constant can never change.
		return new LiveExpression<V>(value) {

			@Override
			protected V compute() {
				return value;
			}

			@Override
			public void addListener(ValueListener<V> l) {
				l.gotValue(this, value);
				//Beyond the initial notification ... we ignore listeners... we will never notify again since
				//constants can't change
			}
			@Override
			public void removeListener(ValueListener<V> l) {
				//Ignore all listeners we will never notify anyone since
				//constants can't change
			}

			/* (non-Javadoc)
			 * @see org.springsource.ide.eclipse.gradle.core.util.expression.LiveExpression#refresh()
			 */
			@Override
			public void refresh() {
				//Ignore all refreshes... no need to refresh anything since
				//constants can't change
			}
		};
	}

	/**
	 * Filter liveexp value to elimante values that are not instances of a given class.
	 * <p>
	 * When target expression has a value is not an instance of the class then the
	 * resulting expression's value is 'null' otherwise its value is the same as the
	 * target expression.
	 */
	public <T> LiveExpression<T> filter(final Class<T> klass) {
		final LiveExpression<V> target = this;
		return new LiveExpression<T>() {
			{
				dependsOn(target);
			}
			@SuppressWarnings("unchecked")
			@Override
			protected T compute() {
				V input = target.getValue();
				if (klass.isInstance(input)) {
					return (T) input;
				} else {
					return null;
				}
			}
		};
	}

}
