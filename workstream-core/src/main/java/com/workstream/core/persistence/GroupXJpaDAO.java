package com.workstream.core.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.model.GroupX;

@Repository
@Transactional
public class GroupXJpaDAO extends GenericJpaDAO<GroupX, Long> implements
		IGroupXDAO {

}
