package com.appbasement.service.template;

import java.util.HashMap;
import java.util.Map;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import static org.junit.Assert.*;
import static junitparams.JUnitParamsRunner.$;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@RunWith(JUnitParamsRunner.class)
public class TemplateServiceTest {

	protected static String[] ctxPath = { "context/context-placeholder.xml",
			"context/context-persistence.xml",
			"test-context/context-service-template.xml" };

	protected static ApplicationContext ctx;

	protected static ITemplateService templateService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ctx = new ClassPathXmlApplicationContext(ctxPath);
		templateService = ctx
				.getBean("templateService", ITemplateService.class);
	}

	protected Object[] getTemplateNames() {
		// test both the entity and the class path resource loader
		return $($("testTemplate"), $("velocity/emailTemplate.vm"));
	}

	@Test
	@Parameters(method = "getTemplateNames")
	public void testMergeTemplateToString(String templateName) {
		String username = "Nick";
		String words = "It's funny!";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("words", words);

		String result = templateService.mergeTemplateToString(templateName,
				"UTF-8", model);
		assertTrue(result.contains(username));
		assertTrue(result.contains(words));
	}

	protected Object[] getTemplateNamesIllegal() {
		return $($(null, ""), $(null, null));
	}

	@Test(expected = IllegalArgumentException.class)
	@Parameters(method = "getTemplateNamesIllegal")
	public void testMergeTemplateToStringIllegal(String name, String encoding) {
		templateService.mergeTemplateToString(name, encoding, null);
	}
}
