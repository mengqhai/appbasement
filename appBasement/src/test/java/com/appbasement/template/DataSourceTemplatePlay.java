package com.appbasement.template;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class DataSourceTemplatePlay {
	protected static String ctxPath = "context/context-template.xml";

	protected static ApplicationContext ctx;

	public DataSourceTemplatePlay() {

	}

	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext(ctxPath);

//		final Template template = new Template();
//		template.setName("abc");
//		template.setDefinition("<html><body><img src='cid:spitterLogo'><h4>${username} says...</h4><i>${words}</i></body></html>");
//		EmfHelper.initEmf();
//		final TemplateJpaDAO dao = new TemplateJpaDAO(EmfHelper.getEmf()
//				.createEntityManager());
//		new TemplateWorker<Template>(dao.getEm()) {
//
//			@Override
//			protected void doIt() {
//				dao.persist(template);
//			}
//		};


		VelocityEngine engine = ctx.getBean("velocityEngine",
				VelocityEngine.class);
		String username = "Nick";
		String words = "It's funny!";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("words", words);

		String result = VelocityEngineUtils.mergeTemplateIntoString(engine,
				"testTemplate", "UTF-8", model);
		System.out.println(result);
		
//		dao.getEm().close();
//		EmfHelper.closeEmf();
	}

}
