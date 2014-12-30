package com.workstream.core.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;

/**
 * A service related with process instances.
 * 
 * @author qinghai
 * 
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class ProcessService extends TaskCapable {

	private final Logger log = LoggerFactory.getLogger(ProcessService.class);

	@Autowired
	private RuntimeService ruSer;

	@Autowired
	private HistoryService hiSer;

	/**
	 * 
	 * @param processDefinitionId
	 *            The process definition/template id.
	 * 
	 * @return
	 */
	public ProcessInstance startProcess(String processDefinitionId) {
		if (Authentication.getAuthenticatedUserId() == null) {
			throw new RuntimeException(
					"No authenticated user, no process can be started.");
		}
		ProcessInstance p = ruSer.startProcessInstanceById(processDefinitionId);
		log.debug("Process instance created id={} for template {}", p.getId(),
				processDefinitionId);
		return p;
	}

	public ProcessInstance startProcess(String processDefinitionId,
			Map<String, Object> vars) {
		if (Authentication.getAuthenticatedUserId() == null) {
			throw new RuntimeException(
					"No authenticated user, no process can be started.");
		}
		ProcessInstance p = ruSer.startProcessInstanceById(processDefinitionId,
				vars);
		log.debug(
				"Process instance created id={} for template {}, with vars: {}",
				p.getId(), processDefinitionId, vars);
		return p;
	}

	public List<ProcessInstance> filterProcess(Long orgId) {
		return ruSer.createProcessInstanceQuery()
				.processInstanceTenantId(String.valueOf(orgId)).list();
	}

	public ProcessInstance getProcess(String id) {
		return ruSer.createProcessInstanceQuery().processInstanceId(id)
				.singleResult();
	}

	public HistoricProcessInstance getHiProcess(String processInstanceId) {
		return hiSer.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
	}

	public List<HistoricProcessInstance> filterHiProcessByOrgStarter(
			Long orgId, String starterUserId, boolean finished) {
		HistoricProcessInstanceQuery q = hiSer
				.createHistoricProcessInstanceQuery()
				.processInstanceTenantId(String.valueOf(orgId))
				.startedBy(starterUserId);
		if (finished) {
			q.finished();
		} else {
			q.unfinished();
		}
		return q.list();
	}

	public List<HistoricProcessInstance> filterHiProcessByOrg(Long orgId) {
		HistoricProcessInstanceQuery q = hiSer
				.createHistoricProcessInstanceQuery().processInstanceTenantId(
						String.valueOf(orgId));
		return q.list();
	}

	public List<HistoricProcessInstance> filterHiProcessByOrg(Long orgId,
			boolean finished) {
		HistoricProcessInstanceQuery q = hiSer
				.createHistoricProcessInstanceQuery().processInstanceTenantId(
						String.valueOf(orgId));
		if (finished) {
			q.finished();
		} else {
			q.unfinished();
		}
		return q.list();
	}

	public List<HistoricProcessInstance> filterHiProcessByStarter(
			String starterUserId, boolean finished) {
		HistoricProcessInstanceQuery q = hiSer
				.createHistoricProcessInstanceQuery().startedBy(starterUserId);
		if (finished) {
			q.finished();
		} else {
			q.unfinished();
		}
		return q.list();
	}

	/**
	 * Deletes historic process instance. All historic activities, historic task
	 * and historic details (variable updates, form properties) are deleted as
	 * well. The process instance must not be running, otherwise an
	 * ActivitiException will be thrown like <br/>
	 * Process instance is still running, cannot delete historic process
	 * instance: 145007
	 * 
	 * @param processInstanceId
	 */
	public void removeHiProcess(String processInstanceId) {
		hiSer.deleteHistoricProcessInstance(processInstanceId);
	}

	/**
	 * Delete an existing runtime process instance.
	 * 
	 * @param processInstanceId
	 */
	public void removeProcess(String processInstanceId) {
		removeProcess(processInstanceId, null);
	}

	public void removeProcess(String processInstanceId, String deleteReason) {
		ruSer.deleteProcessInstance(processInstanceId, deleteReason);
	}

}
