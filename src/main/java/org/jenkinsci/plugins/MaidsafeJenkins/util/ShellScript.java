/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.MaidsafeJenkins.util;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.tasks.Shell;
import hudson.tasks.Shell.DescriptorImpl;
import hudson.util.ArgumentListBuilder;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import jenkins.model.Jenkins;

/**
 *
 * @author krishnakumarp
 */
public class ShellScript {
    private FilePath tempPath;
    private Launcher launcher;
    private PrintStream logger;
    private EnvVars env;
           
    public ShellScript(FilePath tempPath, Launcher launcher, EnvVars envVars) {
        this.tempPath = tempPath;
        this.launcher = launcher;
        this.logger = launcher.getListener().getLogger();
        this.env = envVars;
    }      
    
    private String prepareCommands(List<String> cmds, boolean echoHack) {
    	String echoCmd = "echo \"+ %s\"\n";
    	StringBuilder builder = new StringBuilder();
    	for (String cmd : cmds) {
    		if (echoHack) {
    			builder.append(String.format(echoCmd, cmd));
    		}    		
    		builder.append(cmd).append("\n");    		
    	}
    	return builder.toString();
    }
    
    public int execute(List<String> cmds) throws Exception {
    	return execute(cmds, logger);
    }    
       
    private int runShellCommands(List<String> cmds, OutputStream outputStream) throws Exception {    	
    	FilePath tempFile;
    	int result;
    	ArgumentListBuilder args = new ArgumentListBuilder();
    	tempFile = tempPath.createTextTempFile("script_" + tempPath.getBaseName() + new Date().getTime(), ".sh",
    			prepareCommands(cmds, false), !tempPath.isRemote());    	
    	final Shell.DescriptorImpl desciptor = (DescriptorImpl) Jenkins.getInstance().getDescriptor(Shell.class);
    	final String interpretor = desciptor.getShellOrDefault(tempPath.getChannel());
    	args.add(interpretor);
    	args.add("-xe");
    	args.add(tempFile.getRemote());    	
    	result = launcher.launch().cmds(args)
    			.envs(env).stderr(logger).stdout(outputStream).pwd(tempPath).join();
    	tempFile.delete();
    	return result;
    }
    
    private int runWinBatchCommands(List<String> cmds, OutputStream outputStream) throws Exception {
    	int status;
    	FilePath tempFile = tempPath.createTextTempFile("sricpt_"+ new Date().getTime(), ".bat", 
    			prepareCommands(cmds, true), !tempPath.isRemote());      
        ArgumentListBuilder command = new ArgumentListBuilder();
        command.addTokenized("sh --login " + tempFile.getRemote());        
        Launcher.ProcStarter ps = launcher.new ProcStarter();
        ps = ps.cmds(command).stdout(outputStream);
        ps = ps.pwd(tempPath).envs(env);
        Proc proc = launcher.launch(ps);                        
        status = proc.join();
        tempFile.delete();
        return status;
    }
    
    public int execute(List<String> cmds, OutputStream outputStream) throws Exception {  
    	if (outputStream == null) {
            outputStream = logger;
        }
    	return launcher.isUnix() ? runShellCommands(cmds, outputStream) : runWinBatchCommands(cmds, outputStream);
    }
    
}
