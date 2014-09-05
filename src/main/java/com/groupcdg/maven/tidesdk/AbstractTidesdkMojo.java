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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class AbstractTidesdkMojo extends AbstractMojo {

	protected static final String CREATE_DIRECTORY_ERROR_MESSAGE = "Could not create directory ";

	protected static enum OS {
		win32("Windows", "python", "C:\\ProgramData\\TideSDK"),
		osx("Mac", "python", System.getProperty("user.home") + "/Library/Application Support/TideSDK"),
		linux("Linux", "python", System.getProperty("user.home") + "/.tidesdk");

		public static OS system() throws MojoExecutionException {
			final String name = System.getProperty("os.name");
			for(OS os : values()) if(name.startsWith(os.key)) return os;
			throw new MojoExecutionException("Unsupported operating system: " + name);
		}

		private final String key, python, sdk;

		private OS(String key, String python, String sdk) {
			this.key = key;
			this.python = python;
			this.sdk = sdk;
		}

		public String pythonCommand(String commandOverride) {
			String command;
			if(commandOverride != null) command = commandOverride;
			else if(System.getenv("PYTHON_HOME") != null) {
				String[] prefix = System.getenv("PYTHON_HOME").split(":");
				command = new StringBuilder(prefix[prefix.length - 1])
						.append(File.separatorChar).append("bin")
						.append(File.separatorChar).append("python").toString();
			}
			else command = python;
			return command;
		}

		public String builderCommand(String sdkOverride, String version) {
			return new StringBuilder(sdkOverride == null ? sdk : sdkOverride)
					.append(File.separatorChar).append("sdk")
					.append(File.separatorChar).append(name())
					.append(File.separatorChar).append(version)
					.append(File.separatorChar).append("tidebuilder.py").toString();
		}
	}


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

	@Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources/tidesdk", required = true)
	private File outputDirectory;

	@Parameter(property = "name", defaultValue = "${project.name}", required = true)
	private String name;

	@Parameter(property = "sdkVersion", required = true)
	private String sdkVersion;

	@Parameter(property = "sdkHome")
	private String sdkHome;

	@Parameter(property = "pythonCommand")
	private String pythonCommand;

	@Parameter(property = "fileSets")
	private List<FileSet> fileSets;

	@Parameter(property = "icon")
	private String icon;

	@Parameter(property = "index", defaultValue = "index.html")
	private String index;

	@Parameter(property = "display")
	private Display display;

	private final Log log = getLog();



	public void setProject(MavenProject project) {
		this.project = project;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public void setSdkHome(String sdkHome) {
		this.sdkHome = sdkHome;
	}

	public void setPythonCommand(String pythonCommand) {
		this.pythonCommand = pythonCommand;
	}

	public void setFileSets(List<FileSet> fileSets) {
		this.fileSets = fileSets;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}


	protected File getTidesdkDirectory() {
		tidesdkDirectory.mkdirs();
		return tidesdkDirectory;
	}

	protected MavenProject getProject() {
		return project;
	}

	protected File getOutputDirectory() {
		return outputDirectory;
	}

	protected String getName() {
		return name;
	}

	protected String getEscapedName() {
		return getName().replaceAll("\\s", "_");
	}

	protected String getSdkVersion() {
		return sdkVersion;
	}

	protected String getSdkHome() {
		return sdkHome;
	}

	protected String getPythonCommand() {
		return pythonCommand;
	}

	protected List<FileSet> getFileSets() {
		if(fileSets == null || fileSets.isEmpty()) {
			FileSet r = new FileSet();
			r.setDirectory(defaultFileSet);
			fileSets = Collections.singletonList(r);
		}
		return fileSets;
	}

	protected String getIcon() {
		return icon;
	}

	protected String getIndex() {
		return index;
	}

	protected Display getDisplay() {
		return display;
	}

	protected void run(ProcessBuilder processBuilder, String goal) throws MojoExecutionException {
		final File out = new File(getLogsDirectory(), goal + OUT_LOG_SUFFIX);
		final File err = new File(getLogsDirectory(), goal + ERR_LOG_SUFFIX);

		try {
			notifyError(logCommand(processBuilder)
					.redirectOutput(out.exists() ? ProcessBuilder.Redirect.appendTo(out) : ProcessBuilder.Redirect.to(out))
					.redirectError(err.exists() ? ProcessBuilder.Redirect.appendTo(err) : ProcessBuilder.Redirect.to(err))
					.start().waitFor(), goal);
		} catch (IOException | InterruptedException e) {
			throw new MojoExecutionException(new StringBuilder("Failed to execute ")
					.append(goal).append(" goal.").toString(), e);
		}
	}


	private File getLogsDirectory() {
		File logsDirectory = new File(getTidesdkDirectory(), LOGS_DIRECTORY);
		logsDirectory.mkdirs();
		return logsDirectory;
	}

	private ProcessBuilder logCommand(ProcessBuilder processBuilder) {
		if(log.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder(COMMAND_MESSAGE_PREFIX);
			for(String s : processBuilder.command()) sb.append(' ').append(s);
			log.info(sb);
		}
		return processBuilder;
	}

	private void notifyError(int errorCode, String goal) throws MojoExecutionException {
		if(errorCode != 0) throw new MojoExecutionException(new StringBuilder("Failed to execute ")
				.append(goal).append(" goal. Details of the error can be found at ")
				.append(new File(getLogsDirectory(), goal + ERR_LOG_SUFFIX).getAbsolutePath()).toString());
	}
}
