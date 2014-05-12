package org.rchies.bpm.task.security;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.task.service.UserGroupCallback;

public class CustomUserCallback implements UserGroupCallback {
	
	
	@Override
	public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
		ArrayList<String> result = new ArrayList<String>();
		return result;
	}

	@Override
	public boolean existsUser(String userId) {
		return true;
	}

	@Override
	public boolean existsGroup(String groupId) {
		return true;
	}
}