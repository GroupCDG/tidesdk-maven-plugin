/*
 * Copyright 2014 Computing Distribution Group Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.groupcdg.maven.tidesdk;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class AbstractTidesdkMojo extends AbstractMojo {

	protected static final String CREATE_DIRECTORY_ERROR_MESSAGE = "Could not create directory ";


	private static final String LOGS_DIRECTORY = "logs";

	private static final String OUT_LOG_SUFFIX = ".out";

	private static final String ERR_LOG_SUFFIX = ".err";

	private static final String COMMAND_MESSAGE_PREFIX = "Running: ";



	@Parameter(defaultValue = "${project.build.directory}/tidesdk", required = true, readonly = true)
	private File tidesdkDirectory;

	@Parameter(defaultValue = "${project.basedir}/src/main/webapp", required = true, readonly = true)
	private String defaultFileSet;

	@Parameter(property = "project", defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	@Parameter(property = "pythonCommand", defaultValue = "python", required = true)
	private String pythonCommand;

	@Parameter(property = "sdkHome", defaultValue = "/Users/simon/Library/Application Support/TideSDK", required = true)
	private String sdkHome;

	@Parameter(property = "sdkVersion", defaultValue = "1.3.1-beta", required = true)
	private String sdkVersion;

	@Parameter(property = "command", defaultValue = "/Users/simon/Library/Application Support/TideSDK/sdk/osx/1.3.1-beta/tidebuilder.py", required = true)
	private String command;

	@Parameter(property = "name", defaultValue = "${project.name}", required = true)
	private String name;

	@Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources/tidesdk", required = true)
	private File outputDirectory;

	@Parameter(property = "fileSets")
	private List<FileSet> fileSets;

	@Parameter(property = "platforms", required = true)
	private List<String> platforms;

	@Parameter(property = "plugins")
	private List<String> plugins;

	@Parameter(property = "icon", defaultValue = "default_app_logo.png")
	private String icon;

	@Parameter(property = "width", defaultValue = "1024")
	private Integer width;

	@Parameter(property = "max-width")
	private Integer maxWidth;

	@Parameter(property = "min-width")
	private Integer minWidth;

	@Parameter(property = "height", defaultValue = "768")
	private Integer height;

	@Parameter(property = "max-height")
	private Integer maxHeight;

	@Parameter(property = "min-height")
	private Integer minHeight;

	@Parameter(property = "fullscreen", defaultValue = "false")
	private Boolean fullscreen;

	@Parameter(property = "resizable", defaultValue = "true")
	private Boolean resizable;

	@Parameter(property = "chrome", defaultValue = "true")
	private Boolean chrome;

	@Parameter(property = "scrollable", defaultValue = "true")
	private Boolean scrollable;

	@Parameter(property = "maximizable", defaultValue = "true")
	private Boolean maximizable;

	@Parameter(property = "minimizable", defaultValue = "true")
	private Boolean minimizable;

	@Parameter(property = "closeable", defaultValue = "true")
	private Boolean closeable;

	private final Log log = getLog();



	public void setProject(MavenProject project) {
		this.project = project;
	}

	public void setPythonCommand(String pythonCommand) {
		this.pythonCommand = pythonCommand;
	}

	public void setSdkHome(String sdkHome) {
		this.sdkHome = sdkHome;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void setFileSets(List<FileSet> fileSets) {
		this.fileSets = fileSets;
	}

	public void setPlatforms(List<String> platforms) {
		this.platforms = platforms;
	}

	public void setPlugins(List<String> plugins) {
		this.plugins = plugins;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setMinWidth(Integer minWidth) {
		this.minWidth = minWidth;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setMinHeight(Integer minHeight) {
		this.minHeight = minHeight;
	}

	public void setFullscreen(Boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public void setResizable(Boolean resizable) {
		this.resizable = resizable;
	}

	public void setScrollable(Boolean scrollable) {
		this.scrollable = scrollable;
	}

	public void setChrome(Boolean chrome) {
		this.chrome = chrome;
	}

	public void setMaximizable(Boolean maximizable) {
		this.maximizable = maximizable;
	}

	public void setMinimizable(Boolean minimizable) {
		this.minimizable = minimizable;
	}

	public void setCloseable(Boolean closeable) {
		this.closeable = closeable;
	}

	protected MavenProject getProject() {
		return project;
	}

	protected String getPythonCommand() {
		return pythonCommand;
	}

	protected String getSdkHome() {
		return sdkHome;
	}

	protected String getSdkVersion() {
		return sdkVersion;
	}

	protected String getCommand() {
		return command;
	}

	protected String getName() {
		return name;
	}

	protected String getEscapedName() {
		return getName().replaceAll("\\s", "_");
	}

	protected File getOutputDirectory() {
		return outputDirectory;
	}

	protected List<FileSet> getFileSets() {
		if(fileSets == null || fileSets.isEmpty()) {
			FileSet r = new FileSet();
			r.setDirectory(defaultFileSet);
			fileSets = Collections.singletonList(r);
		}
		return fileSets;
	}

	protected List<String> getPlatforms() {
		return platforms;
	}

	protected List<String> getPlugins() {
		return plugins;
	}

	protected String getIcon() {
		return icon;
	}

	protected Integer getWidth() {
		return width;
	}

	protected Integer getMaxWidth() {
		return maxWidth == null ? getWidth() : maxWidth;
	}

	protected Integer getMinWidth() {
		return minWidth == null ? getWidth() : minWidth;
	}

	protected Integer getHeight() {
		return height;
	}

	protected Integer getMaxHeight() {
		return maxHeight == null ? getHeight() : maxHeight;
	}

	protected Integer getMinHeight() {
		return minHeight == null ? getHeight() : minHeight;
	}

	protected Boolean getFullscreen() {
		return fullscreen;
	}

	protected Boolean getResizable() {
		return resizable;
	}

	protected Boolean getScrollable() {
		return scrollable;
	}

	protected Boolean getChrome() {
		return chrome;
	}

	protected Boolean getMaximizable() {
		return maximizable;
	}

	protected Boolean getMinimizable() {
		return minimizable;
	}

	protected Boolean getCloseable() {
		return closeable;
	}

	protected File getTidesdkDirectory() {
		tidesdkDirectory.mkdirs();
		return tidesdkDirectory;
	}

	protected File getLogsDirectory() {
		File logsDirectory = new File(getTidesdkDirectory(), LOGS_DIRECTORY);
		logsDirectory.mkdirs();
		return logsDirectory;
	}

	protected ProcessBuilder logCommand(ProcessBuilder processBuilder) {
		if(log.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder(COMMAND_MESSAGE_PREFIX);
			for(String s : processBuilder.command()) sb.append(' ').append(s);
			log.info(sb);
		}
		return processBuilder;
	}

	protected int run(ProcessBuilder processBuilder, String goal) throws InterruptedException, IOException {
		final File out = new File(getLogsDirectory(), goal + OUT_LOG_SUFFIX);
		final File err = new File(getLogsDirectory(), goal + ERR_LOG_SUFFIX);
		return logCommand(processBuilder)
				.redirectOutput(out.exists() ? ProcessBuilder.Redirect.appendTo(out) : ProcessBuilder.Redirect.to(out))
				.redirectError(err.exists() ? ProcessBuilder.Redirect.appendTo(err) : ProcessBuilder.Redirect.to(err))
				.start().waitFor();
	}
}
