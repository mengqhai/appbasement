package com.workstream.core.service;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;

/**
 * Process template(deployment & process definitions) related service.
 * 
 * @author qinghai
 * 
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class TemplateService {

	@Autowired
	private RepositoryService repoSer;

	public Deployment createDeployment(Long orgId, String fileName,
			InputStream in) {
		DeploymentBuilder deploymentBuilder = repoSer.createDeployment()
				.name(fileName).tenantId(String.valueOf(orgId));
		if (fileName.endsWith(".bpmn20.xml") || fileName.endsWith(".bpmn")) {
			deploymentBuilder.addInputStream(fileName, in);
		} else if (fileName.endsWith(".bar") || fileName.endsWith(".zip")) {
			deploymentBuilder.addZipInputStream(new ZipInputStream(in));
		} else {
			throw new RuntimeException(
					"Unable to deside the deployment type, have you correctly set the file extension? "
							+ fileName);
		}
		return deploymentBuilder.deploy();
	}

	public Deployment getDeployment(String deploymentId) {
		return repoSer.createDeploymentQuery().deploymentId(deploymentId)
				.singleResult();
	}

	public List<Deployment> filterDeployment(Long orgId) {
		return repoSer.createDeploymentQuery()
				.deploymentTenantId(String.valueOf(orgId)).list();
	}

	/**
	 * non-cascading deletion
	 * 
	 * @param deploymentId
	 */
	public void removeDeployment(String deploymentId) {
		repoSer.deleteDeployment(deploymentId);
	}

	public List<ProcessDefinition> filterProcessTemplate(Long orgId) {
		return repoSer.createProcessDefinitionQuery()
				.processDefinitionTenantId(String.valueOf(orgId)).list();
	}

	public List<ProcessDefinition> filterProcessTemplate(String deploymentId) {
		return repoSer.createProcessDefinitionQuery()
				.deploymentId(deploymentId).list();
	}

	public Model createModel(Long orgId, String name) {
		Model model = repoSer.newModel();
		model.setName(name);
		model.setTenantId(String.valueOf(orgId));
		repoSer.saveModel(model);
		return model;
	}

	public Model getModel(String modelId) {
		return repoSer.createModelQuery().modelId(modelId).singleResult();
	}

	public List<Model> filterModel(Long orgId) {
		return repoSer.createModelQuery().modelTenantId(String.valueOf(orgId))
				.list();
	}

	public void removeModel(String modelId) {
		repoSer.deleteModel(modelId);
	}

}
