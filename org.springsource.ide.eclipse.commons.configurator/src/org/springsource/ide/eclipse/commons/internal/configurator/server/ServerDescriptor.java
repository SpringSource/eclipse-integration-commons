/*******************************************************************************
 * Copyright (c) 2012 Pivotal Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.springsource.ide.eclipse.commons.internal.configurator.server;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.springsource.ide.eclipse.commons.configurator.ServerHandlerCallback;


/**
 * Provides information how to obtain a server runtime;
 * @author Steffen Pingel
 */
public class ServerDescriptor {

	static class ServerHandlerCallbackDelegate extends ServerHandlerCallback {

		private final IConfigurationElement element;

		public ServerHandlerCallbackDelegate(IConfigurationElement element) {
			Assert.isNotNull(element);
			this.element = element;
		}

		@Override
		public void configureServer(IServerWorkingCopy wc) throws CoreException {
			Object object = WorkbenchPlugin.createExtension(element, "callback");
			if (object instanceof ServerHandlerCallback) {
				((ServerHandlerCallback) object).configureServer(wc);
			}
		}

	}

	private final String id;

	private String runtimeTypeId;

	private String serverTypeId;

	private String runtimeName;

	private String serverName;

	private String archiveUrl;

	private String installPath;

	private String licenseUrl;

	private String archivePath;

	private String versionRange;

	private String iconPath;

	private String description;

	private String name;

	private String bundleId;

	private ServerHandlerCallback callback;

	private boolean forceCreateRuntime;

	private boolean autoConfigurable;

	public ServerDescriptor(String id) {
		Assert.isNotNull(id);
		this.id = id;
		this.autoConfigurable = true;
	}

	public ServerDescriptor(IConfigurationElement element) {
		id = element.getAttribute("id");
		setArchivePath(element.getAttribute("archivePath"));
		setRuntimeTypeId(element.getAttribute("runtimeTypeId"));
		setServerTypeId(element.getAttribute("serverTypeId"));
		setRuntimeName(element.getAttribute("runtimeName"));
		setServerName(element.getAttribute("serverName"));
		setName(element.getAttribute("name"));
		setDescription(element.getAttribute("description"));
		setVersionRange(element.getAttribute("versionRange"));
		setInstallPath(element.getAttribute("installPath"));
		setBundleId(element.getAttribute("bundleId"));
		if (element.getAttribute("callback") != null) {
			setCallback(new ServerHandlerCallbackDelegate(element));
		}
		String autoConfigurable = element.getAttribute("autoConfigurable");
		if (autoConfigurable != null) {
			setAutoConfigurable(Boolean.parseBoolean(autoConfigurable));
		}
		else {
			setAutoConfigurable(true);
		}
	}

	/**
	 * Returns the path within in the archive file that is located at
	 * {@link #archiveUrl}. All files under that directory are extracted to
	 * {@link #installPath}.
	 */
	public String getArchivePath() {
		return archivePath;
	}

	public String getArchiveUrl() {
		return archiveUrl;
	}

	/**
	 * Returns the ID of an OSGi bundle that can be used to install the
	 * corresponding runtime.
	 * @return null, if no bundle id has been specified
	 */
	public String getBundleId() {
		return bundleId;
	}

	public ServerHandlerCallback getCallback() {
		return callback;
	}

	public String getDescription() {
		return description;
	}

	public boolean getForceCreateRuntime() {
		return forceCreateRuntime;
	}

	public String getIconPath() {
		return iconPath;
	}

	public String getId() {
		return id;
	}

	/**
	 * Returns the relative directory the runtime is installed into.
	 */
	public String getInstallPath() {
		return installPath;
	}

	public String getLicenseUrl() {
		return licenseUrl;
	}

	public String getName() {
		return name;
	}

	public String getRuntimeName() {
		return runtimeName;
	}

	public String getRuntimeTypeId() {
		return runtimeTypeId;
	}

	public String getServerName() {
		return serverName;
	}

	public String getServerTypeId() {
		return serverTypeId;
	}

	/**
	 * Returns a version range that specifies a range of versions this
	 * descriptor applies to when runtimes are auto-detected by scanning
	 * directories on disk. By convention runtimes are located in directories
	 * named <code>${runtime}-${version}</code> where <code>${version}</code> is
	 * an OSGi version.
	 * @return null, if no version range has been specified
	 */
	public String getVersionRange() {
		return versionRange;
	}

	/**
	 * @see #getArchivePath()
	 */
	public void setArchivePath(String archivePath) {
		this.archivePath = archivePath;
	}

	protected void setArchiveUrl(String downloadUrl) {
		this.archiveUrl = downloadUrl;
	}

	/**
	 * @see #getBundleId()
	 */
	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	/**
	 * Sets an optional callback for setting additional configuration options
	 * when a server instance is created.
	 */
	protected void setCallback(ServerHandlerCallback callback) {
		this.callback = callback;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setForceCreateRuntime(boolean forceCreateRuntime) {
		this.forceCreateRuntime = forceCreateRuntime;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	/**
	 * @see #getInstallPath()
	 */
	protected void setInstallPath(String path) {
		this.installPath = path;
	}

	protected void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setRuntimeName(String runtimeName) {
		this.runtimeName = runtimeName;
	}

	protected void setRuntimeTypeId(String runtimeId) {
		this.runtimeTypeId = runtimeId;
	}

	protected void setServerName(String serverName) {
		this.serverName = serverName;
	}

	protected void setServerTypeId(String serverTypeId) {
		this.serverTypeId = serverTypeId;
	}

	/**
	 * @see #getVersionRange()
	 */
	protected void setVersionRange(String versionRange) {
		this.versionRange = versionRange;
	}

	public void setAutoConfigurable(boolean autoConfigurable) {
		this.autoConfigurable = autoConfigurable;
	}

	public boolean isAutoConfigurable() {
		return autoConfigurable;
	}

	public boolean isValid() {
		return getId() != null;
	}

}
