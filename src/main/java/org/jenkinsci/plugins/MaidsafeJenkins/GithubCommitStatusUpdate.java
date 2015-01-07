package org.jenkinsci.plugins.MaidsafeJenkins;

import java.io.IOException;
import java.io.PrintStream;

import org.jenkinsci.plugins.MaidsafeJenkins.actions.GithubInitializerAction;
import org.jenkinsci.plugins.MaidsafeJenkins.github.CommitStatus;
import org.jenkinsci.plugins.MaidsafeJenkins.github.CommitStatus.State;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;

/**
 * GithubCommitStatusUpdate is a publisher class which can be configured as a post build step.
 * This depends on {@link GithubInitializerAction}. {@link GithubInitializerAction} provides the information about the modules built and the matching 
 * Pull request. Thus based on the data received, the Commit Status API is used to update the Commit status with the appropriate Messages and Result.
 * The OAuth Token for the Commit Status API is also retrieved form the  {@link GithubInitializerAction}
 * 
 * @author krishna
 *
 */
public class GithubCommitStatusUpdate extends Publisher { 
  
  
  /*
   * Without the @DataBoundConstructor Annotation the Publisher step is not recognized by Jenkins     
   */
  @DataBoundConstructor
  public GithubCommitStatusUpdate() {
    
  }

  /*
   * Configuring NONE based on suggestion from API Docs
   * @see hudson.tasks.BuildStep#getRequiredMonitorService()
   */
  public BuildStepMonitor getRequiredMonitorService() {   
    return BuildStepMonitor.NONE;
  }
  
  /*
   * (non-Javadoc)
   * @see hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
   */
  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {
    GithubInitializerAction initializerAction;
    CommitStatus commitStatusApi;
    PrintStream logger;
    logger = listener.getLogger();        
    initializerAction = build.getAction(GithubInitializerAction.class);
    if (initializerAction == null) {
      logger.println("Pull Requests could not be found. Failed to update commit status in Github");
      return true;
    } 
    commitStatusApi = new CommitStatus(initializerAction.getOrgName(), logger,
        initializerAction.isTestingMode(), initializerAction.getOauthAccessToken());    
    if (build.getResult() == Result.SUCCESS) {
      commitStatusApi.updateAll(initializerAction.getPullRequests(), 
          State.SUCCESS, build.getUrl());
    } else {
      commitStatusApi.updateAll(initializerAction.getPullRequests(), 
          State.FAILURE, build.getUrl(), initializerAction.getFailureReason());
    }
      
    return true;
  }
  
  @Override
  public GithubCommitStatusUpdateDescriptor getDescriptor() { 
    return (GithubCommitStatusUpdateDescriptor) super.getDescriptor();
  }
  
  /*
   * A simple BuildStepDescriptor is created to expose the build step.
   * This does not take any parameter inputs.  
   */
  @Extension  
  public static class GithubCommitStatusUpdateDescriptor extends BuildStepDescriptor<Publisher> {
    private final String DISPLAY_NAME = "Set Commit Status for Pull Requests";
    
    public GithubCommitStatusUpdateDescriptor() {
      load();
    }     

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) { 
      return true;
    }

    @Override
    public String getDisplayName() {    
      return DISPLAY_NAME;
    }
    
  }

}
