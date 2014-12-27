package com.workstream.core.sysprocess;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.identity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workstream.core.model.Organization;
import com.workstream.core.service.CoreFacadeService;

public class LookUpAdminGroupId implements JavaDelegate {

	private static final Logger log = LoggerFactory
			.getLogger(LookUpAdminGroupId.class);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String adminGroupId = execution.getVariable("adminGroupId",
				String.class);
		Long orgId = execution.getVariable("orgId", Long.class);
		if (adminGroupId == null) {
			log.info("Looking up groupId for {}", orgId);
			Organization org = CoreFacadeService.getInstance().getOrgService()
					.findOrgById(orgId);
			Group adminGroup = CoreFacadeService.getInstance()
					.getOrgAdminGroup(org);
			adminGroupId = adminGroup.getId();
		}
		execution.setVariable("adminGroupId", adminGroupId);
		log.info("Admin Group of org {} is {}", orgId, adminGroupId);

	}
}
