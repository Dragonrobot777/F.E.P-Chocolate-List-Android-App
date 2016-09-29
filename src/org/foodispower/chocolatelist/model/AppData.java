package org.foodispower.chocolatelist.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppData {

	private List< Company > companies;
	private List< Feature > features;
	private Date lastUpdated;

	public List< Company > getCompanies() {
		return companies;
	}

	public void setCompanies(List< Company > companies) {
		this.companies = companies;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
