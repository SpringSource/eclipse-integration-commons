/*******************************************************************************
 * Copyright (c) 2012, 2013 Pivotal Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.springsource.ide.eclipse.commons.internal.content.core;

import java.io.File;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Version;
import org.springsource.ide.eclipse.commons.content.core.ContentManager;
import org.springsource.ide.eclipse.commons.content.core.util.Descriptor;

/**
 * Verifies if a descriptor is valid for the environment by matching the filter
 * expression and version constraints.
 * @author Steffen Pingel
 * @author Christian Dupuis
 */
public class DescriptorMatcher {

	private final ContentManager manager;

	private final Dictionary<Object, Object> environment;

	private File installDirectory;

	/**
	 * Installed version of STS.
	 */
	private Version version;

	/**
	 * 
	 * @param manager content manager that handles the download of descriptor
	 * data. The content manager should also indicate the installation directory
	 * of the content, as this is used to match a descriptor id and version with
	 * the actual content that was downloaded.
	 */
	public DescriptorMatcher(ContentManager manager) {
		this.environment = new Hashtable<Object, Object>(System.getProperties());
		this.manager = manager;
		Bundle bundle = Platform.getBundle("org.springsource.sts"); //$NON-NLS-1$
		if (bundle != null) {
			String versionString = (String) bundle.getHeaders().get("Bundle-Version");
			if (versionString != null) {
				this.version = new Version(versionString);
				this.environment.put("org.springsource.sts.version", version.toString()); //$NON-NLS-1$
				this.environment.put("org.springsource.sts.version.major", version.getMajor()); //$NON-NLS-1$
				this.environment.put("org.springsource.sts.version.minor", version.getMinor()); //$NON-NLS-1$
				this.environment.put("org.springsource.sts.version.micro", version.getMicro()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * An alternative to passing a Content Manager that points to an
	 * installation directory used for descriptor version and ID matching, is to
	 * pass the installation directory itself.
	 * @param installDirectory local directory where contents are installed,
	 * typically in [workspacelocation]/.metadata/.sts/[content directory]
	 */
	public DescriptorMatcher(File installDirectory) {
		this((ContentManager) null);
		this.installDirectory = installDirectory;
	}

	public Map<Object, Object> getEnvironment() {
		return Collections.unmodifiableMap((Map<?, ?>) environment);
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean match(Descriptor descriptor) {
		if (version != null && descriptor.getRequires() != null) {
			try {
				VersionRange versionRange = new VersionRange(descriptor.getRequires());
				if (!versionRange.isIncluded(version)) {
					return false;
				}
			}
			catch (IllegalArgumentException e) {
				// ignore
			}
		}
		if (descriptor.getFilter() != null) {
			try {
				Filter filter = FrameworkUtil.createFilter(descriptor.getFilter());
				// TODO e3.7 remove cast and use expected typesObject
				return filter.match((Dictionary) environment);
			}
			catch (IllegalArgumentException e) {
				// ignore
			}
			catch (InvalidSyntaxException e) {
				// ignore
			}
		}
		if (descriptor.isLocal()) {

			File baseDirectory = installDirectory == null ? manager.getInstallDirectory() : installDirectory;
			File directory = new File(baseDirectory, descriptor.getId() + "-" + descriptor.getVersion());
			if (!directory.exists()) {
				return false;
			}
		}
		return true;
	}

}
