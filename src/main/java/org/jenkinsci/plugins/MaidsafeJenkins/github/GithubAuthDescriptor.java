package org.jenkinsci.plugins.MaidsafeJenkins.github;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

public abstract class GithubAuthDescriptor extends BuildStepDescriptor<Builder> {
  private String oauthAccessToken;

  public GithubAuthDescriptor() {
    load();
  }

  @Override
  public boolean isApplicable(Class<? extends AbstractProject> jobType) {
    return true;
  }

  @Override
  public boolean configure(StaplerRequest req, JSONObject json) throws hudson.model.Descriptor.FormException {
    oauthAccessToken = json.getString("githubToken");
    onFormSave(json);
    save();
    return super.configure(req, json);
  }

  public String getGithubAccessToken() {
    return oauthAccessToken;
  }

  public abstract void onFormSave(JSONObject json);

  @Override
  public abstract String getDisplayName();

}
