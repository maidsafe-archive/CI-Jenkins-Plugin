package org.jenkinsci.plugins.MaidsafeJenkins.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jenkinsci.plugins.MaidsafeJenkins.actions.BuildTargetParameter;

public class TargetBuildParameterUtil {
	private final String ROOT_KEY = "targets";
	private final String BRANCH_PARAM_KEY = "branch";
	private final String OWNER_PARAM_KEY = "owner";
	private final String REPO_PARAM_KEY = "repo";
	
	private BuildTargetParameter convert(JSONObject param) {
		BuildTargetParameter targetParam;
		targetParam = new BuildTargetParameter();
		targetParam.setBranch(param.getString(BRANCH_PARAM_KEY));
		targetParam.setOwner(param.getString(OWNER_PARAM_KEY));
		targetParam.setRepo(param.getString(REPO_PARAM_KEY));
		return targetParam;
	}
	
	private boolean isJSONObjectValid(JSONObject param) {
		return param.getString(BRANCH_PARAM_KEY) != null && param.getString(OWNER_PARAM_KEY) != null && param.getString(REPO_PARAM_KEY) != null
				&& !param.getString(BRANCH_PARAM_KEY).isEmpty() && !param.getString(OWNER_PARAM_KEY).isEmpty() && !param.getString(REPO_PARAM_KEY).isEmpty();
	}
	
	public List<BuildTargetParameter> parse(JSONObject json) {
		List<BuildTargetParameter> params;
		JSONArray targetParams;
		JSONObject tempObject;
		params = new ArrayList<BuildTargetParameter>();
		if (json.get(ROOT_KEY) instanceof JSONArray) {
			targetParams = json.getJSONArray(ROOT_KEY);
			for (Object param : targetParams.toArray()) {
				tempObject = (JSONObject) param;
				if (!isJSONObjectValid(tempObject)) {
					return null;
				}	
				params.add(convert(tempObject));
			}
		} else {
			tempObject = json.getJSONObject(ROOT_KEY);
			if (!isJSONObjectValid(tempObject)) {
				return null;
			}			
			params.add(convert(tempObject));
		}
		return params;
	}
	
	
}
