/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.MaidsafeJenkins.github;

import java.io.PrintStream;
import java.util.*;

import net.sf.json.JSONException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jenkinsci.plugins.MaidsafeJenkins.Exception.BaseBranchMisMatchException;
import org.jenkinsci.plugins.MaidsafeJenkins.Exception.TooManyPRForModule;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author krishnakumarp
 */
public class GitHubPullRequestHelper {

	private String org;
	private List<String> repositories;
	private PrintStream logger;

	
	public static enum PR_MATCH_STRATERGY {
		BRANCH_NAME_STARTS_WITH, BRANCH_NAME_STARTS_WITH_IGNORE_CASE
	}

	public static enum Filter {
		NONE, OPEN
	}

	private final String PR_REQUEST = "https://api.github.com/repos/%s/%s/pulls?";
	private final String PR_HEAD_REPO_KEY = "head";
	private final String PR_BRANCH_KEY = "ref";
	private String accessToken;

	public GitHubPullRequestHelper(String orgName, List<String> repositories, PrintStream logger) {
		this.org = orgName;
		this.repositories = repositories;
		this.logger = logger;
	}
	
	// TODO multi thread the PR requests
	public Map<String, Map<String, Object>> getMatchingPR(String text, Filter filter, PR_MATCH_STRATERGY stratergy)
			throws Exception {
		Map<String, Map<String, Object>> matchingPRForModule = new HashMap<String, Map<String, Object>>();
		JSONObject tempObj = null;
		String tempBaseBranch;
		String repoBaseBranch = null;
		String baseBranchforPR = null;
		JSONArray prListforRepo;
		if (filter == null) {
			filter = Filter.NONE;
		}
		if (stratergy == null) {
			stratergy = PR_MATCH_STRATERGY.BRANCH_NAME_STARTS_WITH_IGNORE_CASE;
		}
		for (String repo : repositories) {
			prListforRepo = getPRListFromGithub(org, repo, filter);			
			if (prListforRepo == null || prListforRepo.size() == 0) {
				continue;
			}		
			tempObj = findMatchingPR(text, stratergy, prListforRepo, repo);		
			if (tempObj != null) {
				tempBaseBranch = (String) ((JSONObject) tempObj.get("base")).get("ref");
				if (baseBranchforPR == null) {
					repoBaseBranch = repo;
					baseBranchforPR = tempBaseBranch;
				} else if (!baseBranchforPR.equals(tempBaseBranch)) { 
					throw new BaseBranchMisMatchException(repo, tempBaseBranch, repoBaseBranch, baseBranchforPR);					
				}
				matchingPRForModule.put(repo, toMap(tempObj));
			}
		}
		logger.println(matchingPRForModule.size() + " modules have matching Pull Requests");
		return matchingPRForModule;
	}
	
	public void setAccessToken(String token) {
		accessToken = token;
	}
		
	
	private boolean isMatchFound(PR_MATCH_STRATERGY stratergy, String prBranchRef, String key) {		
		boolean matched;		
		switch (stratergy) {
			case BRANCH_NAME_STARTS_WITH:
				matched = prBranchRef.startsWith(key);
				break;
	
			default: // BRANCH_NAME_STARTS_WITH_IGNORE_CASE
				matched = prBranchRef.toLowerCase().startsWith(key.toLowerCase());
				break;
		}	
		return matched;
	}

	private JSONObject findMatchingPR(String key, PR_MATCH_STRATERGY stratergy, JSONArray prList, String repo) throws Exception {
		JSONObject pullRequest;
		JSONObject prHead;
		JSONObject lastMatchedPR = null;
		boolean matched;
		for (Object obj : prList) {
			matched = false;
			pullRequest = (JSONObject) obj;
			prHead = (JSONObject) pullRequest.get(PR_HEAD_REPO_KEY);			
			matched = isMatchFound(stratergy, prHead.get(PR_BRANCH_KEY).toString().replace("-", "_"), key.replace("-", "_"));// Replacing the characters to ensure uniformity 
			if (matched && lastMatchedPR != null) { // Only one PR should match, thus a validation to check the	condition				
				throw new TooManyPRForModule(repo);
			} else if (matched) {
				lastMatchedPR = pullRequest;
			}
		}				
		return lastMatchedPR;
	}

	private String prepareURL(String org, String repo, Filter filter) {
		StringBuilder endPoint = new StringBuilder(String.format(PR_REQUEST, org, repo));		
		switch (filter) {
		case OPEN:
			endPoint.append("state=open");
			break;

		default:
			break;
		}		
		return endPoint.toString();
	}

	private JSONArray getPRListFromGithub(String org, String repo, Filter filter) {
		logger.println("Fetching PR from " + org + "/" + repo);
		JSONArray openPrs = null;		
		try {
			HttpClient client = new HttpClient();						
			GetMethod prListRequest = new GetMethod(prepareURL(org, repo, filter));			
			if (accessToken != null && !accessToken.isEmpty()) {
				prListRequest.addRequestHeader("Authorization", "token " + accessToken);				
			}			
			
			int statusCode = client.executeMethod(prListRequest);
			if (statusCode != HttpStatus.SC_OK) {
				logger.println("Pull Request API failed with Error Code :: " + statusCode);
				logger.println(prListRequest.getResponseBodyAsString());
				return openPrs;
			}
			openPrs = (JSONArray) new JSONParser().parse(prListRequest.getResponseBodyAsString());			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.println(ex);
		}
		return openPrs;
	}

	private Map<String, Object> toMap(JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> keysItr = object.keySet().iterator();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	private List toList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}
}
