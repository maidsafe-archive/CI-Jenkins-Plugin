package org.jenkinsci.plugins.MaidsafeJenkins.actions;

import java.util.HashMap;
import java.util.Map;
import org.jenkinsci.plugins.MaidsafeJenkins.util.ShellScript;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import hudson.Functions;
import hudson.model.Action;
import hudson.model.Api;

@ExportedBean(defaultVisibility = 999)
public class GithubCheckoutAction extends ActionSummary implements Action {
  private final String DISPLAY_NAME = "Github Checkout Summary";
  private final String URL = "checkoutSummary";
  private final String ICON = Functions.getResourcePath() + "/plugin/MaidsafeJenkins/icons/octocat.jpg";

  private transient ShellScript script;
  private String orgName;
  private Map<String, Map<String, Object>> actualPRList;

  public Api getApi() {
    return new Api(this);
  }

  public String getDisplayName() {
    return DISPLAY_NAME;
  }

  public String getIconFileName() {
    return ICON;
  }

  public String getUrlName() {
    return URL;
  }

  public ShellScript getScript() {
    return script;
  }

  public void setScript(ShellScript script) {
    this.script = script;
  }

  @Exported
  public HashMap<String, Object> getGithubCheckoutAction() {
    return getSummary();
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public Map<String, Map<String, Object>> getActualPRList() {
    return actualPRList;
  }

  public void setActualPRList(Map<String, Map<String, Object>> matchingPR) {
    this.actualPRList = matchingPR;
  }

}
