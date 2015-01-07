package org.jenkinsci.plugins.MaidsafeJenkins.actions;

import java.util.Collection;
import java.util.Collections;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import hudson.model.AbstractProject;

@Extension
public class BuildForGithubTargets extends TransientProjectActionFactory {

	@Override
	public Collection<? extends Action> createFor(AbstractProject target) {
		return Collections.singleton(new GitHubTargetParameterAction(target));
	}

}
