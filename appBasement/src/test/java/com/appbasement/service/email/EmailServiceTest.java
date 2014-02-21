package com.appbasement.service.email;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

@RunWith(JUnitParamsRunner.class)
public class EmailServiceTest {

	protected static Wiser wiser;

	protected static String ctxPath = "context/context-email.xml";

	protected static ApplicationContext ctx;

	protected static IEmailService emailService;

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

	protected Object[] getSimpleMessageArguments() {
		String from = "testfrom@mail.com";
		String to = "testto@mail.com";
		String subject = "test mail subject";
		String content = "Hello there!";
		// from & subject can be null
		return $($(from, to, subject, content));
	}

	@Test
	@Parameters(method = "getSimpleMessageArguments")
	public void testSendSimpleMessage(String from, String to, String subject,
			String content) throws Exception {
		emailService.sendSimpleEmail(from, to, subject, content);
		List<WiserMessage> mList = wiser.getMessages();
		assertEquals(1, mList.size());
		WiserMessage m = mList.get(0);

		if (from != null) {
			assertEquals(from, m.getEnvelopeSender());
		}

		assertEquals(to, m.getEnvelopeReceiver());

		MimeMessage mm = m.getMimeMessage();
		assertEquals(subject, mm.getSubject());
		assertEquals(content, mm.getContent().toString().trim());
		assertEquals("text/plain; charset=us-ascii", mm.getContentType());
	}

	protected Object[] getSimpleMessageInvalidArguments() {
		String from = "testfrom@mail.com";
		String to = "testto@mail.com";
		String subject = "test mail subject";
		String content = "Hello there!";
		return $($(from, null, subject, content), $(from, to, subject, null),
				$("", to, subject, content), $(from, "", subject, content),
				$(null, to, subject, content), $(from, to, null, content),
				$(from, to, subject, ""), $(from, to, "", content));
	}

	@Test(expected = IllegalArgumentException.class)
	@Parameters(method = "getSimpleMessageInvalidArguments")
	public void testSendSimpleMessageInvalidArguments(String from, String to,
			String subject, String content) throws Exception {
		emailService.sendSimpleEmail(from, to, subject, content);
	}

	protected Object[] getRichMessageInvalidArguments() {
		String from = "testfrom@mail.com";
		String to = "testto@mail.com";
		String subject = "test mail subject";
		String content = "<html><body><h4>Hello there!</h4></body></html>";
		return $($(from, null, subject, content),
				$(from, "", subject, content), $(from, to, subject, null),
				$("", to, subject, content), $(null, to, subject, content),
				$(from, to, null, content));
	}

	@Test(expected = IllegalArgumentException.class)
	@Parameters(method = "getRichMessageInvalidArguments")
	public void testSendRichMessageInvalidArguments(String from, String to,
			String subject, String content) {
		emailService.sendRichEmail(from, to, subject, content);
	}

	protected Object[] getRichMessageArguments() {
		String from = "testfrom@mail.com";
		String to = "testto@mail.com";
		String subject = "test mail subject";
		String content = "<html><body><h4>Hello there!</h4></body></html>";
		return $($(from, to, subject, content));
	}

	@Test
	@Parameters(method = "getRichMessageArguments")
	public void testSendRichMessage(String from, String to, String subject,
			String content) throws Exception {
		emailService.sendRichEmail(from, to, subject, content);

		List<WiserMessage> mList = wiser.getMessages();
		assertEquals(1, mList.size());
		WiserMessage m = mList.get(0);
		MimeMessage mm = m.getMimeMessage();
		assertTrue(mm.getContentType().startsWith("multipart/mixed;"));
		assertTrue(mm.getContentType().contains("boundary="));
		assertTrue(mm.getContent() instanceof MimeMultipart);
		MimeMultipart part = (MimeMultipart) mm.getContent();

		assertTrue(part.getBodyPart(0) instanceof MimeBodyPart);
		MimeBodyPart body = (MimeBodyPart) part.getBodyPart(0);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				body.getInputStream()));
		String line = reader.readLine();
		while (line != null) {
			if (line.equals(content))
				break;

			line = reader.readLine();
		}
		assertEquals(content, line);
	}

}
