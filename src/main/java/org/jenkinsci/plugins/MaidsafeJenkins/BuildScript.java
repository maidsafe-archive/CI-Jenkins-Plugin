package org.jenkinsci.plugins.MaidsafeJenkins;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.jenkinsci.plugins.MaidsafeJenkins.MaidsafeJenkinsBuilder.DescriptorImpl;
import org.jenkinsci.plugins.MaidsafeJenkins.actions.GithubCheckoutAction;
import org.jenkinsci.plugins.MaidsafeJenkins.util.ShellScript;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.*;

/**
 * 
 * This Build step is used to execute the build script facilitating in passing the modules names to the build script  
 *
 */
public class BuildScript extends Builder {
  private final static String BUILD_STEP_NAME = "Build Script based on Modules";
  private final String buildCommand;
  
  @DataBoundConstructor
  public BuildScript(String buildCommand) {
    this.buildCommand = buildCommand;
  }
  
  public String getBuildCommand() {
    return buildCommand;
  }
  
  private String getModules(GithubCheckoutAction action) {
    StringBuilder builder = new StringBuilder();    
    for (String module : action.getAffectedModules()) {                 
      builder.append(" ").append(module);
    }
    return builder.toString();
  }
  
  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {
    EnvVars envVars;
    ShellScript shellScript;
    List<String> commands;
    int result;
    GithubCheckoutAction checkoutAction;
    checkoutAction = build.getAction(GithubCheckoutAction.class);
    if (checkoutAction == null) {
      listener.getLogger().println("Can not find GitCheckoutAction");
      return false;
    }
    commands = new ArrayList<String>();
    envVars = build.getEnvironment(listener);
    shellScript = new ShellScript(build.getWorkspace(), launcher, envVars);
    String[] cmds = buildCommand.split("\\n");              
    try {
      for (String cmd : cmds) {
        commands.add(cmd.replace("#MODULES#", getModules(checkoutAction)));
      }     
      result = shellScript.execute(commands);
    }catch(Exception e) {
      listener.getLogger().println("Exception :: " + e.getMessage());
      result = 1;
    }
    return result == 0;
  }
  
  @Extension
  public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
    
    public DescriptorImpl() {
      load();
    }
    
    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {     
      return true;
    }
    
    

    @Override
    public String getDisplayName() {    
      return BUILD_STEP_NAME;
    }
    
  }
  
  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }
  
}
