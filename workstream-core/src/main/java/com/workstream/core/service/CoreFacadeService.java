package com.workstream.core.service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.activiti.engine.identity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.GroupX;
import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;
import com.workstream.core.model.UserX;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class CoreFacadeService {
	private final Logger log = LoggerFactory.getLogger(CoreFacadeService.class);

	@Autowired
	private OrganizationService orgSer;

	@Autowired
	private UserService uSer;

	@Autowired
	private ProjectService pSer;

	private static AtomicReference<CoreFacadeService> INSTANCE = new AtomicReference<CoreFacadeService>();

	protected CoreFacadeService() {
		final CoreFacadeService previous = INSTANCE.getAndSet(this);
		// if (previous != null)
		// throw new IllegalStateException("Second singleton " + this
		// + " created after " + previous);
		if (previous != null) {
			log.warn(
					"Creating a second instance of CoreFacadeService: old: {} new: {}",
					previous, this);
		}
	}

	public static CoreFacadeService getInstance() {
		return INSTANCE.get();
	}

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

	public Group getOrgAdminGroup(Organization org) {
		List<Group> groups = uSer.filterGroup(org);
		for (Group group : groups) {
			if ("Admin".equals(group.getName())) {
				return group;
			}
		}
		log.error("Org {} has no admin group!", org.getIdentifier());
		throw new RuntimeException("Org {} has no admin group!"
				+ org.getIdentifier());
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
		}

		// delete projects of the org
		Collection<Project> projects = pSer.filterProject(org);
		for (Project pro : projects) {
			pSer.deleteProject(pro);
		}

		orgSer.removeOrg(org); // cascade removes org & groupXes
		log.info("Cleared org id={} name={} identifier={}", org.getId(),
				org.getName(), org.getIdentifier());

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
