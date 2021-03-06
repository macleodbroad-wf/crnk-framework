package io.crnk.gen.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class OpenAPIGeneratorConfigTest {
  @Before
  public void setup() throws IOException {
    File buildDir = new File("build/tmp/openapi");

    InputStream xmlDoc = getClass().getClassLoader().getResourceAsStream("openapi-template.yml");
    FileUtils.copyInputStreamToFile(xmlDoc, new File(buildDir, "openapi-template.yml"));
  }

  @Test
  public void test() {
    OpenAPIGeneratorConfig config = new OpenAPIGeneratorConfig();

    // Default
    OpenAPI openApi = config.getOpenAPI();
    Assert.assertSame("Generated Title", openApi.getInfo().getTitle());
    Assert.assertSame("0.1.0", openApi.getInfo().getVersion());
    Assert.assertSame("Generated Description", openApi.getInfo().getDescription());

    config.setProjectName("My custom title");
    Assert.assertEquals("My custom title", config.getProjectName());

    config.setProjectVersion("7.7.7");
    Assert.assertEquals("7.7.7", config.getProjectVersion());

    config.setProjectDescription("My custom description");
    Assert.assertEquals("My custom description", config.getProjectDescription());

    openApi = config.getOpenAPI();
    Assert.assertSame("My custom title", openApi.getInfo().getTitle());
    Assert.assertSame("7.7.7", openApi.getInfo().getVersion());
    Assert.assertSame("My custom description", openApi.getInfo().getDescription());

    File buildDir = new File("build/tmp/openapi");
    config.setBuildDir(buildDir);
    Assert.assertSame(buildDir, config.getBuildDir());
    config.setTemplateName("openapi-template.yml");
    Assert.assertSame("openapi-template.yml", config.getTemplateName());

    openApi = config.getOpenAPI();
    Assert.assertEquals("Title From Template", openApi.getInfo().getTitle());
    Assert.assertEquals("0.1.0", openApi.getInfo().getVersion());
    Assert.assertEquals("OpenAPI document with autogenerated paths, components, responses, etc.", openApi.getInfo().getDescription());
  }
}
