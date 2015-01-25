package com.workstream.core.service.cmd;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;

public class DeleteHistoricTaskNoCascadeCmd implements Command<Object> {
	private HistoricTaskInstance hiTask;

	public DeleteHistoricTaskNoCascadeCmd(HistoricTaskInstance hiTask) {
		this.hiTask = hiTask;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		DbSqlSession dbSqlSession = commandContext.getDbSqlSession();
		dbSqlSession.delete((HistoricTaskInstanceEntity)hiTask);
		return null;
	}

}
