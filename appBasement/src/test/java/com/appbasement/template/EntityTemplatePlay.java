package com.appbasement.template;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EntityTemplatePlay {
	protected static String ctxPath = "context/context-template.xml";

	protected static ApplicationContext ctx;

	protected static ITemplateService templateService;

	public EntityTemplatePlay() {

	}

	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext(ctxPath);
		templateService = ctx
				.getBean("templateService", ITemplateService.class);

		String username = "Nick";
		String words = "It's funny!";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("words", words);

		String result = templateService.mergeTemplateToString("testTemplate",
				"UTF-8", model);
		System.out.println(result);
	}

}
