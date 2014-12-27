package com.workstream.core.sysprocess;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;
import com.workstream.core.service.CoreFacadeService;

public class AddUserToOrg implements JavaDelegate {

	private final static Logger log = LoggerFactory
			.getLogger(AddUserToOrg.class);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String userId = execution.getVariable("userId", String.class);
		Long orgId = execution.getVariable("orgId", Long.class);
		Organization org = CoreFacadeService.getInstance().getOrgService()
				.findOrgById(orgId);
		UserX userX = CoreFacadeService.getInstance().getUserService()
				.getUserX(userId);
		CoreFacadeService.getInstance().getOrgService().userJoinOrg(userX, org);
		log.info("User {} successfully added to org {}", userId, orgId);
	}
}
