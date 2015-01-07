package org.jenkinsci.plugins.MaidsafeJenkins.github;

import groovy.json.JsonBuilder;

public class CommitStatusPayload {
	private String state;
	private String target_url;
	private String description;
	private String context;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTarget_url() {
		return target_url;
	}

	public void setTarget_url(String target_url) {
		this.target_url = target_url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return new JsonBuilder(this).toPrettyString();
	}

}