package com.workstream.core.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.model.Project;

@Repository
@Transactional
public class ProjectJpaDAO extends GenericJpaDAO<Project, Long> implements
		IProjectDAO {

}
