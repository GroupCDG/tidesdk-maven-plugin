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
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

public class GenerateMojoTest {

	private static final String PLUGIN_NAME = "tidesdk-maven-plugin";

	private static final String GENERATE_GOAL = "generate";

	private static final String CONFIG_TARGET = "target/generated-sources/tidesdk";

	private static final String ASSETS_TARGET = "target/generated-sources/tidesdk/Resources";

	private static final File DEFAULT_PROJECT = new File("src/test/resources/unit/default");

	private static final File CUSTOM_RESOURCES_PROJECT = new File("src/test/resources/unit/custom-resources");

	private static final File CUSTOM_SETTINGS_PROJECT = new File("src/test/resources/unit/custom-settings");

	private static final String DEFAULT_ASSET_CONTENT = "Assets taken from default webapp directory";

	private static final String CUSTOM_ASSET_CONTENT = "Assets taken from custom assets directory";

	private static final String OVERRIDEN_ASSET_CONTENT = "Assets taken from custom overrides directory";


	@Rule
	public MojoRule rule = new MojoRule();



	@Test
	public void testDefaultGeneration() throws Exception {
		File target = new File(DEFAULT_PROJECT, "target");
		if (target.exists()) FileUtils.cleanDirectory(target);

		rule.configureMojo(new GenerateMojo(), PLUGIN_NAME, pom(DEFAULT_PROJECT));
		rule.executeMojo(DEFAULT_PROJECT, GENERATE_GOAL);

		assertIncluded(config(DEFAULT_PROJECT, "manifest"), "appname: Default_Project");
		assertIncluded(config(DEFAULT_PROJECT, "tiapp.xml"), "<name>Default Project</name>");
		assertIncluded(asset(DEFAULT_PROJECT, "index.html"), DEFAULT_ASSET_CONTENT);
		assertIncluded(asset(DEFAULT_PROJECT, "css/style.css"), DEFAULT_ASSET_CONTENT);
	}

	@Test
	public void testCustomResourcesGeneration() throws Exception {
		File target = new File(CUSTOM_RESOURCES_PROJECT, "target");
		if (target.exists()) FileUtils.cleanDirectory(target);

		rule.configureMojo(new GenerateMojo(), PLUGIN_NAME, pom(CUSTOM_RESOURCES_PROJECT));
		rule.executeMojo(CUSTOM_RESOURCES_PROJECT, GENERATE_GOAL);

		assertIncluded(config(CUSTOM_RESOURCES_PROJECT, "manifest"), "appname: Custom_Resources_Project");
		assertIncluded(config(CUSTOM_RESOURCES_PROJECT, "tiapp.xml"), "<name>Custom Resources Project</name>");
		assertIncluded(asset(CUSTOM_RESOURCES_PROJECT, "index.html"), CUSTOM_ASSET_CONTENT);
		assertIncluded(asset(CUSTOM_RESOURCES_PROJECT, "css/style.css"), CUSTOM_ASSET_CONTENT);
		assertIncluded(asset(CUSTOM_RESOURCES_PROJECT, "other.html"), OVERRIDEN_ASSET_CONTENT);
		assertExcluded(asset(CUSTOM_RESOURCES_PROJECT, "excluded.txt"));
	}

	@Test
	public void testCustomSettingsGeneration() throws Exception {
		File target = new File(CUSTOM_SETTINGS_PROJECT, "target");
		if (target.exists()) FileUtils.cleanDirectory(target);

		rule.configureMojo(new GenerateMojo(), PLUGIN_NAME, pom(CUSTOM_SETTINGS_PROJECT));
		rule.executeMojo(CUSTOM_SETTINGS_PROJECT, GENERATE_GOAL);

		assertIncluded(config(CUSTOM_SETTINGS_PROJECT, "manifest"),
				"appname: Custom_Settings_Project",
				"publisher: Computing Distribution Group Ltd.",
				"url: http://groupcdg.com/CustomSettingsProject",
				"image: img/icon.png");
		assertIncluded(config(CUSTOM_SETTINGS_PROJECT, "tiapp.xml"),
				"<name>Custom Settings Project</name>",
				"<copyright>2014 Computing Distribution Group Ltd.</copyright>",
				"<icon>img/icon.png</icon>",
				"<width>1024</width>",
				"<height>768</height>",
				"<resizable>false</resizable>");
	}


	private File pom(File projectDir) {
		return new File(projectDir, "pom.xml");
	}

	private File config(File projectDir, String fileName) {
		return new File(new File(projectDir, CONFIG_TARGET), fileName);
	}

	private File asset(File projectDir, String fileName) {
		return new File(new File(projectDir, ASSETS_TARGET), fileName);
	}

	private void assertIncluded(File file, String... content) throws IOException {
		assertTrue("File should be included: " + file.getAbsolutePath(), file.exists());
		for(String entry : content) assertThat(FileUtils.readFileToString(file), containsString(entry));
	}

	private void assertExcluded(File file) {
		assertFalse("File should be excluded: " + file.getAbsolutePath(), file.exists());
	}
}
