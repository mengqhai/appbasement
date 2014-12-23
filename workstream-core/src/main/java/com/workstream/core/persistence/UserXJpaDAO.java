package com.workstream.core.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.model.UserX;

@Repository
@Transactional
public class UserXJpaDAO extends GenericJpaDAO<UserX, Long> implements
		IUserXDAO {

}
