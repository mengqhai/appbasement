package com.workstream.core.service;

import java.util.Collection;

import org.activiti.engine.identity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.GroupX;
import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;
import com.workstream.core.model.UserX;

@Service
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class CoreFacadeService {
	private final Logger log = LoggerFactory.getLogger(CoreFacadeService.class);

	@Autowired
	private OrganizationService orgSer;

	@Autowired
	private UserService uSer;

	@Autowired
	private ProjectService pSer;

	public Organization createInitOrg(UserX creator, String name,
			String identifier, String description) {
		Organization org = orgSer.createOrg(name, identifier, description);
		orgSer.userJoinOrg(creator, org);
		// create predefined groups
		Group group = uSer.createGroup(org, "Admin",
				"Adimistrators of the organization.");
		uSer.addUserToGroup(creator, group.getId());
		group = uSer.createGroup(org, "Process Designer",
				"Process Designers of the organization");
		uSer.addUserToGroup(creator, group.getId());
		return org;
	}

	public void userLeaveOrg(UserX userX, Organization org) {
		// TODO other things like check the process templates, etc.

		// user must firstly be removed from all the groups of the org
		Collection<GroupX> groupXList = uSer.filterGroupX(org);
		for (GroupX groupX : groupXList) {
			uSer.removeUserFromGroup(userX.getUserId(), groupX.getGroupId());
		}
		orgSer.userLeaveOrg(userX, org);
	}

	/**
	 * It's a big deal to clear an org.
	 * 
	 * @param org
	 */
	public void clearOrg(Organization org) {
		// TODO remove all the task, processes, projects of the org

		// delete groups of the org
		Collection<GroupX> groupXes = uSer.filterGroupX(org);
		for (GroupX groupX : groupXes) {
			uSer.removeGroup(groupX);
			log.info("Deleted group {}", groupX);
		}

		// delete projects of the org
		Collection<Project> projects = pSer.filterProject(org);
		for (Project pro : projects) {
			pSer.deleteProject(pro);
			log.info("Deleted project {}", pro);
		}

		orgSer.removeOrg(org); // cascade removes org & groupXes
		log.info("Deleted org {} ", org);

		// do users needs to leave the org? No, the relationship between org and
		// user is deleted by cascading
	}

	public OrganizationService getOrgService() {
		return orgSer;
	}

	public UserService getUserService() {
		return uSer;
	}

}
