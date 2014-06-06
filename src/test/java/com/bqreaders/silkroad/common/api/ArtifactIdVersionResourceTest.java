/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.api;

import com.yammer.dropwizard.testing.ResourceTest;
import org.junit.Test;

import java.util.Properties;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Alexander De Leon
 * 
 */
public class ArtifactIdVersionResourceTest extends ResourceTest {

	@Override
	protected void setUpResources() throws Exception {
		addResource(new ArtifactIdVersionResource("artifact"));
	}

	@Test
	public void test() {
		Properties prop = new Properties();
		prop.setProperty("build.a", "1");
		prop.setProperty("build.b", "2");
		assertThat(client().resource("/version").get(Properties.class)).isEqualTo(prop);
	}
}
