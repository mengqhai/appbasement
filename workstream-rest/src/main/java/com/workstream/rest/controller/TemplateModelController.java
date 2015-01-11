package com.workstream.rest.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;

@Api(value = "template models", description = "Process template model related operations")
@RestController
@RequestMapping(value = "/templatemodels", produces = MediaType.APPLICATION_JSON_VALUE)
public class TemplateModelController {

}
