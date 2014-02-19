package com.appbasement.email;

import static org.junit.Assert.*;

import java.util.List;

import javax.mail.internet.MimeMessage;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public class EmailServiceTest {

	protected static Wiser wiser;

	protected static String ctxPath = "context/context-email.xml";

	protected static ApplicationContext ctx;

	protected static EmailService emailService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ctx = new ClassPathXmlApplicationContext(ctxPath);
		emailService = ctx.getBean(EmailService.class);

		wiser = new Wiser();
		wiser.setPort(1025);
		wiser.start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		wiser.stop();
	}

	@Before
	public void setUp() {
		wiser.getMessages().clear();
	}

	@Test
	public void testSendSimpleMessage() throws Exception {
		String from = "testfrom@mail.com";
		String to = "testto@mail.com";
		String subject = "test mail subject";
		String content = "Hello there!";
		emailService.sendSimpleEmail(from, to, subject, content);
		List<WiserMessage> mList = wiser.getMessages();
		assertEquals(1, mList.size());
		WiserMessage m = mList.get(0);
		assertEquals(from, m.getEnvelopeSender());
		assertEquals(to, m.getEnvelopeReceiver());

		MimeMessage mm = m.getMimeMessage();
		assertEquals(subject, mm.getSubject());
		assertEquals(content, mm.getContent().toString().trim());
	}

	@Test
	public void testOther() {
		assertTrue(wiser.getMessages().isEmpty());
	}

}
