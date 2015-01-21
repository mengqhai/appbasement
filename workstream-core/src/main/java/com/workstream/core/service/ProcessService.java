package com.workstream.core.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.exception.AuthenticationNotSetException;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.ResourceNotFoundException;

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
	private FormService forSer;

	@Autowired
	private RepositoryService repoSer;

	/**
	 * 
	 * @param processDefinitionId
	 *            The process definition/template id.
	 * 
	 * @return
	 */
	public ProcessInstance startProcess(String processDefinitionId)
			throws AuthenticationNotSetException {
		if (Authentication.getAuthenticatedUserId() == null) {
			throw new AuthenticationNotSetException(
					"No authenticated user, no process can be started.");
		}
		try {
			ProcessDefinition def = repoSer
					.getProcessDefinition(processDefinitionId);
			ProcessInstance p = ruSer
					.startProcessInstanceById(processDefinitionId);
			ruSer.setProcessInstanceName(p.getProcessInstanceId(),
					def.getName());
			log.debug("Process instance created id={} for template {}",
					p.getId(), processDefinitionId);
			// information not complete, so refetch it again
			p = this.getProcess(p.getProcessInstanceId());
			return p;
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such template", e);
		}
	}

	public ProcessInstance startProcessByKey(String processDefinitionKey,
			Map<String, Object> vars) throws AuthenticationNotSetException {
		if (Authentication.getAuthenticatedUserId() == null) {
			throw new AuthenticationNotSetException(
					"No authenticated user, no process can be started.");
		}
		try {
			ProcessInstance p = ruSer.startProcessInstanceByKey(
					processDefinitionKey, vars);
			log.debug(
					"Process instance created id={} for template key={}, with vars: {}",
					p.getId(), vars, processDefinitionKey);
			return p;
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such process", e);
		}
	}

	public ProcessInstance startProcess(String processDefinitionId,
			Map<String, Object> vars) throws AuthenticationNotSetException {
		if (Authentication.getAuthenticatedUserId() == null) {
			throw new AuthenticationNotSetException(
					"No authenticated user, no process can be started.");
		}
		try {
			ProcessInstance p = ruSer.startProcessInstanceById(
					processDefinitionId, vars);
			log.debug(
					"Process instance created id={} for template {}, with vars: {}",
					p.getId(), processDefinitionId, vars);
			return p;
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such process", e);
		}
	}

	public List<ProcessInstance> filterProcess(Long orgId) {
		return ruSer.createProcessInstanceQuery()
				.processInstanceTenantId(String.valueOf(orgId)).list();
	}

	public List<ProcessInstance> filterProcessByTemplateId(String templateId) {
		return ruSer.createProcessInstanceQuery()
				.processDefinitionId(templateId).orderByProcessInstanceId()
				.desc().list();
	}

	public ProcessInstance getProcess(String id) {
		return ruSer.createProcessInstanceQuery().processInstanceId(id)
				.singleResult();
	}

	public ProcessInstance getProcessWithVars(String id) {
		return ruSer.createProcessInstanceQuery().processInstanceId(id)
				.includeProcessVariables().singleResult();
	}

	public HistoricProcessInstance getHiProcess(String processInstanceId) {
		return hiSer.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
	}

	public HistoricProcessInstance getHiProcessWithVars(String processInstanceId) {
		return hiSer.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).includeProcessVariables()
				.singleResult();
	}

	public List<HistoricProcessInstance> filterHiProcessByOrgStarter(
			Long orgId, String starterUserId, boolean finished) {
		HistoricProcessInstanceQuery q = hiSer
				.createHistoricProcessInstanceQuery().startedBy(starterUserId)
				.orderByProcessInstanceEndTime().desc();
		if (finished) {
			// has to directly touch the history table
			q.processInstanceTenantId(String.valueOf(orgId));
			q.finished();
		} else {
			// The historic process instance table can easily get extremely
			// large.
			// So need to firstly query a smaller table to get ids, then
			// query the historic process instance table with ids where index
			// has already been created.
			List<ProcessInstance> piList = ruSer.createProcessInstanceQuery()
					.processInstanceTenantId(String.valueOf(orgId)).list();
			if (piList.isEmpty()) {
				return Collections.emptyList();
			}
			Set<String> ids = new HashSet<String>(piList.size());
			for (ProcessInstance pi : piList) {
				ids.add(pi.getProcessInstanceId());
			}
			q.processInstanceIds(ids);
			q.unfinished();
		}
		return q.list();
	}

	public List<HistoricProcessInstance> filterHiProcessByOrg(Long orgId) {
		HistoricProcessInstanceQuery q = hiSer
				.createHistoricProcessInstanceQuery()
				.processInstanceTenantId(String.valueOf(orgId))
				.orderByProcessInstanceEndTime().desc();
		return q.list();
	}

	public List<HistoricProcessInstance> filterHiProcessByOrg(Long orgId,
			boolean finished) {
		HistoricProcessInstanceQuery q = hiSer
				.createHistoricProcessInstanceQuery()
				.processInstanceTenantId(String.valueOf(orgId))
				.orderByProcessInstanceEndTime().desc();
		if (finished) {
			// has to directly touch the history table
			q.finished();
		} else {
			// The historic process instance table can easily get extremely
			// large.
			// So need to firstly query a smaller table to get ids, then
			// query the historic process instance table with ids where index
			// has already been created.
			List<ProcessInstance> piList = ruSer.createProcessInstanceQuery()
					.processInstanceTenantId(String.valueOf(orgId)).list();
			if (piList.isEmpty()) {
				return Collections.emptyList();
			}
			Set<String> ids = new HashSet<String>(piList.size());
			for (ProcessInstance pi : piList) {
				ids.add(pi.getProcessInstanceId());
			}
			q.processInstanceIds(ids);
			q.unfinished();
		}
		return q.list();
	}

	public List<HistoricProcessInstance> filterHiProcessByStarter(
			String starterUserId, boolean finished) {
		HistoricProcessInstanceQuery q = hiSer
				.createHistoricProcessInstanceQuery().startedBy(starterUserId);
		if (finished) {
			// has to directly touch the history table
			q.finished();
			q.orderByProcessInstanceEndTime().desc();
		} else {
			// The historic process instance table can easily get extremely
			// large.
			// So need to firstly query a smaller table to get ids, then
			// query the historic process instance table with ids where index
			// has already been created.
			List<ProcessInstance> piList = ruSer.createProcessInstanceQuery()
					.involvedUser(starterUserId).list();
			if (piList.isEmpty()) {
				return Collections.emptyList();
			}
			Set<String> ids = new HashSet<String>(piList.size());
			for (ProcessInstance pi : piList) {
				ids.add(pi.getProcessInstanceId());
			}
			q.processInstanceIds(ids);
			q.unfinished();
			q.orderByProcessInstanceStartTime().desc();
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

	public List<Task> filterTaskByProcess(String processInstanceId) {
		return taskSer.createTaskQuery().processInstanceId(processInstanceId)
				.list();
	}

	public List<HistoricTaskInstance> filterArchTaskByProcess(
			String processInstanceId) {
		return hiSer.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).list();
	}

	public StartFormData getStartFormData(String processDefinitionId) {
		try {
			return forSer.getStartFormData(processDefinitionId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such task", e);
		}
	}

	public ProcessInstance submitStartFormData(String processDefinitionId,
			Map<String, String> properties) {
		try {
			ProcessDefinition def = repoSer
					.getProcessDefinition(processDefinitionId);
			ProcessInstance p = forSer.submitStartFormData(processDefinitionId,
					properties);

			ruSer.setProcessInstanceName(p.getProcessInstanceId(),
					def.getName());
			log.debug("Process instance created id={} for template {}",
					p.getId(), processDefinitionId);
			// information not complete, so refetch it again
			p = this.getProcess(p.getProcessInstanceId());
			return p;
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such template", e);
		} catch (ActivitiIllegalArgumentException ie) {
			throw new BadArgumentException(ie.getMessage());
		} catch (NumberFormatException nfe) {
			throw new BadArgumentException(nfe.getMessage());
		} catch (ActivitiException ae) {
			throw new BadArgumentException(ae.getMessage());
		}
	}

	public TaskFormData getTaskFormData(String taskId) {
		try {
			return forSer.getTaskFormData(taskId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such task", e);
		}
	}

	public void submitTaskFormData(String taskId, Map<String, String> properties) {
		try {
			forSer.submitTaskFormData(taskId, properties);
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such task", e);
		} catch (ActivitiIllegalArgumentException ie) {
			throw new BadArgumentException(ie.getMessage());
		} catch (NumberFormatException nfe) {
			throw new BadArgumentException(nfe.getMessage());
		} catch (ActivitiException ae) {
			throw new BadArgumentException(ae.getMessage());
		}
	}

}
