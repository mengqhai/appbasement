package com.workstream.rest.advices;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeZoneConvertionAdvice {

	@Pointcut("execution(* com.workstream.rest.model.TaskResponse.getCreateTime(..))")
	public void getTime() {
	}
	
	@AfterReturning("getTime()")
	public void convertTimeZone() {
		System.out.println("getTime");
	}
	
	@Pointcut("execution(* com.workstream.rest.controller.TaskController.getMyAssigneeTasks(..))")
	public void aBeanMethod() {
	}
	
	@AfterReturning("aBeanMethod()")
	public void afterABeanMethod() {
		System.out.println("aBeanMethod");
	}

}
