package org.jenkinsci.plugins.MaidsafeJenkins.Exception;

public class TooManyPRForModule extends Exception {
	private String moduleName; 
	
	public TooManyPRForModule(String moduleName) {
		this.moduleName = moduleName;
	}	
		
	
	@Override
	public String getMessage() {	
		return "More than one matching Pull Request found in module - " + moduleName;
	}
	
}
