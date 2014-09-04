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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;


@Mojo(name = "build", defaultPhase = LifecyclePhase.PACKAGE)
public class BuildMojo extends AbstractTidesdkMojo {


	private static final String BUILD = "build";

	private static final String BUILD_RESOURCES_ERROR_MESSAGE = "Failed to build resources";



	public void execute() throws MojoExecutionException {
		try {
			final File outputDirectory = getOutputDirectory();

			if (!outputDirectory.exists() && !outputDirectory.mkdirs())
				throw new MojoExecutionException(CREATE_DIRECTORY_ERROR_MESSAGE + outputDirectory.getAbsolutePath());

			build(outputDirectory);
		} catch (IOException | InterruptedException e) {
			throw new MojoExecutionException(BUILD_RESOURCES_ERROR_MESSAGE, e);
		}
	}

	private void build(final File outputDirectory) throws IOException, InterruptedException {
		File buildDirectory = new File(outputDirectory, "packages/osx/bundle");
		buildDirectory.mkdirs();
		run(new ProcessBuilder(getPythonCommand(), getCommand(),
				"-d", buildDirectory.getAbsolutePath(),
				"-i", "'dist,packages'",
				"-t", "bundle",
				"-p", "-v",
				outputDirectory.getAbsolutePath()), BUILD);
	}
}
