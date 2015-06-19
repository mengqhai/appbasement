package com.workstream.core.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
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

	@Autowired
	private DiagramService diagramService;

	public enum UserProcessRole {
		STARTER, INVOLVED
	}

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

	protected ProcessInstanceQuery prepareProcessInstanceQueryForOrg(Long orgId) {
		return ruSer.createProcessInstanceQuery().processInstanceId(
				String.valueOf(orgId));
	}

	public List<ProcessInstance> filterProcess(Long orgId, int first, int max) {
		return prepareProcessInstanceQueryForOrg(orgId).listPage(first, max);
	}

	public long countProcess(Long orgId) {
		return prepareProcessInstanceQueryForOrg(orgId).count();
	}

	protected ProcessInstanceQuery prepareProcessInstanceQueryForTemplate(
			String templateId) {
		return ruSer.createProcessInstanceQuery()
				.processDefinitionId(templateId).orderByProcessInstanceId()
				.desc();
	}

	public List<ProcessInstance> filterProcessByTemplateId(String templateId,
			int first, int max) {
		return prepareProcessInstanceQueryForTemplate(templateId).listPage(
				first, max);
	}

	public long countProcessByTemplateId(String templateId) {
		return prepareProcessInstanceQueryForTemplate(templateId).count();
	}

	public ProcessInstance getProcess(String id) {
		return ruSer.createProcessInstanceQuery().processInstanceId(id)
				.singleResult();
	}

	public ProcessInstance getProcessWithVars(String id) {
		return ruSer.createProcessInstanceQuery().processInstanceId(id)
				.includeProcessVariables().singleResult();
	}

	/**
	 * Generate the process diagram for a running process instance
	 * 
	 * @param id
	 * @return
	 */
	public InputStream generateProcessDiagram(String id) {
		// http://forums.activiti.org/content/process-diagram-highlighting-current-process
		// http://forums.activiti.org/content/api-fetch-diagram-process-instance
		// maybe the activiti-diagram-rest-5.16.4.jar can also be referred to
		// (for example, how to find out highlighted flows, etc)
		ProcessInstance instance = getProcess(id);
		if (instance == null) {
			return null; // no such process, should be archived
		}
		// assuming the process definition has graphical notation defined
		// (pde.isGraphicalNotationDefined())
		BpmnModel bpmnModel = repoSer.getBpmnModel(instance
				.getProcessDefinitionId());
		InputStream stream = diagramService.generateProcessDiagram(bpmnModel,
				ruSer.getActiveActivityIds(id));
		return stream;
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

	protected HistoricProcessInstanceQuery prepareHiProcessQueryByOrg(
			Long orgId, boolean finished) {
		HistoricProcessInstanceQuery q = hiSer
				.createHistoricProcessInstanceQuery()
				.processInstanceTenantId(String.valueOf(orgId))
				.orderByProcessInstanceEndTime().desc();
		if (finished) {
			q.finished();
		} else {
			q.unfinished();
		}
		return q;
	}

	public List<HistoricProcessInstance> filterHiProcessByOrg(Long orgId,
			boolean finished, int first, int max) {
		HistoricProcessInstanceQuery q = prepareHiProcessQueryByOrg(orgId,
				finished);
		return q.listPage(first, max);
	}

	public long countHiProcessByOrg(Long orgId, boolean finished) {
		HistoricProcessInstanceQuery q = prepareHiProcessQueryByOrg(orgId,
				finished);
		return q.count();
	}

	public List<HistoricProcessInstance> filterHiProcessByUser(
			UserProcessRole role, String userId, boolean finished, int first,
			int max) {
		HistoricProcessInstanceQuery q = prepareHiProcessQueryByUser(role,
				userId, finished);
		return q.listPage(first, max);
	}

	public long countHiProcessByUser(UserProcessRole role, String userId,
			boolean finished) {
		HistoricProcessInstanceQuery q = prepareHiProcessQueryByUser(role,
				userId, finished);
		return q.count();
	}

	protected HistoricProcessInstanceQuery prepareHiProcessQueryByUser(
			UserProcessRole role, String userId, boolean finished) {
		HistoricProcessInstanceQuery q = hiSer
				.createHistoricProcessInstanceQuery();
		switch (role) {
		case STARTER:
			q.startedBy(userId);
			break;
		case INVOLVED:
			q.involvedUser(userId);
			break;
		default:
			throw new BadArgumentException("Unsupported user role");
		}
		if (finished) {
			q.finished();
			q.orderByProcessInstanceEndTime().desc();
		} else {
			q.unfinished();
			q.orderByProcessInstanceStartTime().desc();
		}
		return q;
	}

	public List<HistoricProcessInstance> filterHiProcessByInvolved(
			String userId, boolean finished, int first, int max) {
		return filterHiProcessByUser(UserProcessRole.INVOLVED, userId,
				finished, first, max);
	}

	public List<HistoricProcessInstance> filterHiProcessByStarter(
			String starterUserId, boolean finished, int first, int max) {
		return filterHiProcessByUser(UserProcessRole.STARTER, starterUserId,
				finished, first, max);
	}

	public List<HistoricFormProperty> filterHiProcessFormProperties(
			String processId) {
		List<HistoricDetail> details = hiSer.createHistoricDetailQuery()
				.formProperties().processInstanceId(processId).list();
		List<HistoricFormProperty> formProperties = new ArrayList<HistoricFormProperty>(
				details.size());
		for (HistoricDetail d : details) {
			formProperties.add((HistoricFormProperty) d);
		}
		return formProperties;
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
		try {
			ruSer.deleteProcessInstance(processInstanceId, deleteReason);
		} catch (ActivitiObjectNotFoundException e) {
			throw new ResourceNotFoundException("No such process");
		}
	}

	public List<Task> filterTaskByProcess(String processInstanceId, int first,
			int max) {
		return prepareTaskQueryByProcess(processInstanceId)
				.listPage(first, max);
	}

	public long countTaskByProcess(String processInstanceId) {
		return prepareTaskQueryByProcess(processInstanceId).count();
	}

	protected TaskQuery prepareTaskQueryByProcess(String processInstanceId) {
		return taskSer.createTaskQuery().processInstanceId(processInstanceId);
	}

	protected HistoricTaskInstanceQuery prepareArchTaskQueryByProcess(
			String processInstanceId) {
		return hiSer.createHistoricTaskInstanceQuery().processInstanceId(
				processInstanceId).orderByTaskCreateTime().desc();
	}

	public List<HistoricTaskInstance> filterArchTaskByProcess(
			String processInstanceId, int first, int max) {
		return prepareArchTaskQueryByProcess(processInstanceId).listPage(first,
				max);
	}

	public long countArchTaskByProcess(String processInstanceId) {
		return prepareArchTaskQueryByProcess(processInstanceId).count();
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
			log.debug("Process instance created id={} for template {}",
					p.getId(), processDefinitionId);

			if (!p.isEnded()) {
				ruSer.setProcessInstanceName(p.getProcessInstanceId(),
						def.getName());
			} else {
				log.debug(
						"Process instance {} seems already finished, and no longer exist in runtime service. (We've no chance to set its name.)",
						p.getProcessInstanceId());
				return p;
			}

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
