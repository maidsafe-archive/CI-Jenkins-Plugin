package org.jenkinsci.plugins.MaidsafeJenkins.actions;

import java.util.HashMap;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import hudson.model.Action;
import hudson.model.Api;

@ExportedBean(defaultVisibility=999)
public class AggregatedCheckoutSummaryAction implements Action {
	private final String ACTION_NAME = "Aggregated Checkout Summary";
	private HashMap<String, Object> checkoutSummary = new HashMap<String, Object>();

    public Api getApi() {
        return new Api(this);
    }

	@Exported
	public HashMap<String, Object> getCheckoutSummary() {
		return checkoutSummary;
	}

	public void setCheckoutSummary(HashMap<String, Object> checkoutSummary) {
		this.checkoutSummary = checkoutSummary;
	}

	public String getIconFileName() {
		return "";
	}

	public String getDisplayName() {		
		return ACTION_NAME;
	}

	public String getUrlName() {	
		return "aggregatedCheckoutSummary";
	}
	

}
