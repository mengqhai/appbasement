package com.workstream.rest.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.cage.Cage;
import com.github.cage.YCage;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.workstream.rest.RestConstants;

@Api(value = "Captcha", description = "Captcha image service", position=3)
@RestController
@RequestMapping(value = "/captcha")
public class CaptchaController {
	private static final Logger log = LoggerFactory
			.getLogger(CaptchaController.class);

	private final Cage cage = new YCage();

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getCaptcha(@ApiIgnore HttpSession session) {
		String token = cage.getTokenGenerator().next();
		session.setAttribute(RestConstants.CAPTCHA_TOKEN, token);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			cage.draw(token, bos);
		} catch (IOException e) {
			log.error("Failed to generate captcha token", e);
			return new byte[0];
		}
		return bos.toByteArray();
	}

}
