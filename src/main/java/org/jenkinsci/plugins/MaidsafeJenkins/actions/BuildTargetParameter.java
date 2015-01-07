package org.jenkinsci.plugins.MaidsafeJenkins.actions;

public class BuildTargetParameter {
	private String repo;
	private String owner;
	private String branch;

	/**
	 * @return the repo
	 */
	public String getRepo() {
		return repo;
	}

	/**
	 * @param repo
	 *          the repo to set
	 */
	public void setRepo(String repo) {
		this.repo = repo;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *          the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the branch
	 */
	public String getBranch() {
		return branch;
	}

	/**
	 * @param branch
	 *          the branch to set
	 */
	public void setBranch(String branch) {
		this.branch = branch;
	}

	@Override
	public String toString() {
		return repo + " - " + owner + " " + branch;
	}
}
