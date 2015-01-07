package com.jenkinsci.plugins.MaidsafeJenkins.Exception;

public class BaseBranchMisMatchException extends Exception {
	
	private static final long serialVersionUID = 1L;
	// ERROR MESSAGES //
	private final String BASE_BRANCH_MIS_MATCH_ERR = "Pull Request have different base Branches."
			+ " %s uses %s as its base branch, while %s has %s as its base branch ";
	private String initialRepo;
	private String initialBaseBranch;
	private String remoteRepo;
	private String remoteRepoBaseBranch;
	
	public BaseBranchMisMatchException( String remoteRepo, String remoteRepoBaseBranch, String referenceRepo, String referenceRepoBaseBranch) {
		this.initialRepo = referenceRepo;
		this.initialBaseBranch = referenceRepoBaseBranch;
		this.remoteRepo = remoteRepo;
		this.remoteRepoBaseBranch = remoteRepoBaseBranch;
	}
	
	@Override
	public String getMessage() {	
		return String.format(BASE_BRANCH_MIS_MATCH_ERR, initialRepo, initialBaseBranch, remoteRepo, remoteRepoBaseBranch);
	}
	
}
