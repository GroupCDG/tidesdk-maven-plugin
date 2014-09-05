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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.filefilter.FileFilterUtils.*;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class GenerateMojo extends AbstractTidesdkMojo {


	private static final String GENERATE = "generate";

	private static final String CREATE = "create";

	private static final String PLATFORM = "platform";

	private static final String PLUGIN = "plugin";

	private static final String ADD = "add";

	private static final String RESOURCES_DIRECTORY = "resources";

	private static final String GENERATE_RESOURCES_ERROR_MESSAGE = "Failed to generate resources";

	private static final String NO_VALUE_MESSAGE = "No value configured for field: ";

	private static final String UNKNOWN_VALUE = "unknown";



	public void execute() throws MojoExecutionException {
		try {
			final File outputDirectory = getOutputDirectory();

			if (!outputDirectory.exists() && !outputDirectory.mkdirs())
				throw new MojoExecutionException( CREATE_DIRECTORY_ERROR_MESSAGE + outputDirectory.getAbsolutePath());

			create(outputDirectory, prepare());
		} catch (IOException | InterruptedException e) {
			throw new MojoExecutionException(GENERATE_RESOURCES_ERROR_MESSAGE, e);
		}
	}



	private File prepare() throws IOException {
		File resourcesDirectory = new File(getTidesdkDirectory(), RESOURCES_DIRECTORY);
		resourcesDirectory.mkdir();

		for(FileSet fileSet : getFileSets())
			copyDirectory(new File(fileSet.getDirectory()), resourcesDirectory, createFilter(fileSet));

		return resourcesDirectory;
	}

	private void create(final File outputDirectory, final File resourcesDirectory) throws IOException, InterruptedException {
		// Create
		FileUtils.copyDirectory(resourcesDirectory, new File(outputDirectory, "Resources"));
		FileUtils.writeLines(new File(outputDirectory, "manifest"), createManifest());
		FileUtils.writeLines(new File(outputDirectory, "tiapp.xml"), createXml());
	}

	private IOFileFilter createFilter(final FileSet fileSet) {
		List<IOFileFilter> includes = new ArrayList<IOFileFilter>() {{
			for(String inc : fileSet.getIncludes()) add(new WildcardFileFilter(inc));
		}};
		List<IOFileFilter> excludes = new ArrayList<IOFileFilter>() {{
			for(String ex : fileSet.getExcludes()) add(new WildcardFileFilter(ex));
		}};
		IOFileFilter include = includes.isEmpty() ? trueFileFilter()
				: or(directoryFileFilter(), or(includes.toArray(new IOFileFilter[includes.size()])));
		return excludes.isEmpty() ? include
				: and(include, notFileFilter(or(excludes.toArray(new IOFileFilter[excludes.size()]))));
	}

	private Collection<String> createManifest() {
		MavenProject project = getProject();

		return Arrays.asList(
				"#appname: " + getEscapedName(),
				"#publisher: " + getPublisher(project),
				"#url: " + getUrl(project),
				"#image: " + getIcon(),
				"#appid: " + project.getGroupId() + '.' + project.getArtifactId(),
				"#desc: " + project.getDescription(),
				"#type: desktop",
				"#guid: " + UUID.randomUUID(),
				"runtime:" + getSdkVersion(),
				"app:" + getSdkVersion(),
				"codec:" + getSdkVersion(),
				"database:" + getSdkVersion(),
				"filesystem:" + getSdkVersion(),
				"media:" + getSdkVersion(),
				"monkey:" + getSdkVersion(),
				"network:" + getSdkVersion(),
				"platform:" + getSdkVersion(),
				"process:" + getSdkVersion(),
				"ui:" + getSdkVersion(),
				"worker:" + getSdkVersion()
		);
	}

	private Collection<String> createXml() {
		MavenProject project = getProject();

		return Arrays.asList(
				"<?xml version='1.0' encoding='UTF-8'?>",
				"<ti:app xmlns:ti='http://ti.appcelerator.org'>",
				"<id>" + project.getGroupId() + '.' + project.getArtifactId() + "</id>",
				"<name>" + getName() + "</name>",
				"<version>" + project.getVersion() + "</version>",
				"<publisher>" + getPublisher(project) + "</publisher>",
				"<url>" + getUrl(project) + "</url>",
				"<icon>" + getIcon() + "</icon>",
				"<copyright>" + Calendar.getInstance().get(Calendar.YEAR) + " " + getPublisher(project) + "</copyright>",
				"<window>",
				"\t<id>initial</id>",
				"\t<title>" + getName() + "</title>",
				"\t<url>app://index.html</url>",
				"\t<width>" + getWidth() + "</width>",
				"\t<max-width>" + getMaxWidth() + "</max-width>",
				"\t<min-width>" + getMinWidth() + "</min-width>",
				"\t<height>" + getHeight() + "</height>",
				"\t<max-height>" + getMaxHeight() + "</max-height>",
				"\t<min-height>" + getMinHeight() + "</min-height>",
				"\t<fullscreen>" + String.valueOf(getFullscreen()) + "</fullscreen>",
				"\t<resizable>" + String.valueOf(getResizable()) + "</resizable>",
				"\t<chrome scrollbars=\"" + String.valueOf(getScrollable()) + "\">" + String.valueOf(getChrome()) + "</chrome>",
				"\t<maximizable>" + String.valueOf(getMaximizable()) + "</maximizable>",
				"\t<minimizable>" + String.valueOf(getMinimizable()) + "</minimizable>",
				"\t<closeable>" + String.valueOf(getCloseable()) + "</closeable>",
				"</window>",
				"</ti:app>"
		);
	}

	private String getPublisher(MavenProject project) {
		return project.getOrganization() != null ? project.getOrganization().getName()
				: !project.getDevelopers().isEmpty() ? project.getDevelopers().get(0).getName()
				: unknown("Publisher");
	}

	private String getUrl(MavenProject project) {
		return project.getUrl() != null ? project.getUrl()
				: project.getOrganization() != null ? project.getOrganization().getUrl()
				: !project.getDevelopers().isEmpty() ? project.getDevelopers().get(0).getUrl()
				: unknown("Url");
	}

	private String unknown(String field) {
		getLog().warn(new StringBuilder(NO_VALUE_MESSAGE).append(field));
		return UNKNOWN_VALUE;
	}
}
