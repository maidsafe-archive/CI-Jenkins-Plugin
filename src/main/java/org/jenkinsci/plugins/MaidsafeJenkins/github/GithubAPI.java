package org.jenkinsci.plugins.MaidsafeJenkins.github;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jenkinsci.plugins.MaidsafeJenkins.util.JSONUtil;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GithubAPI {

	private String authToken;
	private final String LIST_BRANCHES = "https://api.github.com/repos/%s/%s/branches";
	private PrintStream logger;

	private String getRequest(String uri) {
		HttpClient client;
		GetMethod get;
		String response = null;
		int statusCode;
		try {
			client = new HttpClient();
			get = new GetMethod(uri);
			if (authToken != null && !authToken.isEmpty()) {
				get.addRequestHeader("Authorization", "token " + authToken);
			}
			statusCode = client.executeMethod(get);
			if (statusCode != HttpStatus.SC_OK) {
				logger.println("Github API Request failed with Error Code :: " + statusCode);
				logger.println(get.getResponseBodyAsString());
			} else {
				response = get.getResponseBodyAsString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public GithubAPI(String authToken, PrintStream logger) {
		this.authToken = authToken;
		this.logger = logger;
	}

	public List<String> getBranchList(String owner, String repository) {
		List<String> branches = null;
		List<Object> list;
		String response;
		logger.println("Fetching Branches for " + owner + "/" + repository);
		response = getRequest(String.format(LIST_BRANCHES, owner, repository));
		branches = new ArrayList<String>();
		if (response == null) {
			return branches;
		}
		try {
			list = JSONUtil.toList((JSONArray) new JSONParser().parse(response));
			for (Object branch : list) {
				branches.add((String)((Map<String, Object>) branch).get("name"));
			}
		} catch(Exception e) {
			logger.println(e.getLocalizedMessage());
		}		
		return branches;
	}

}
