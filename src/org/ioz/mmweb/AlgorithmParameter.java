package org.ioz.mmweb;

public class AlgorithmParameter {
	private String id;
	private String algorithmparameterid;
	private String name;
	private String type;
	private String overview;
	private String description;
	private int sequence=0;
	private String not_zero_if_the_parameter_has_lower_limit;
	private String parameter_lower_limit;
	private String not_zero_if_the_parameter_has_upper_limit;
	private String parameter_upper_limit;
	private String defaultvalue;
	private boolean enabled;
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getNot_zero_if_the_parameter_has_lower_limit() {
		return not_zero_if_the_parameter_has_lower_limit;
	}
	public void setNot_zero_if_the_parameter_has_lower_limit(
			String not_zero_if_the_parameter_has_lower_limit) {
		this.not_zero_if_the_parameter_has_lower_limit = not_zero_if_the_parameter_has_lower_limit;
	}
	public String getNot_zero_if_the_parameter_has_upper_limit() {
		return not_zero_if_the_parameter_has_upper_limit;
	}
	public void setNot_zero_if_the_parameter_has_upper_limit(
			String not_zero_if_the_parameter_has_upper_limit) {
		this.not_zero_if_the_parameter_has_upper_limit = not_zero_if_the_parameter_has_upper_limit;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getAlgorithmparameterid() {
		return algorithmparameterid;
	}
	public void setAlgorithmparameterid(String algorithmparameterid) {
		this.algorithmparameterid = algorithmparameterid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	public String getParameter_lower_limit() {
		return parameter_lower_limit;
	}
	public void setParameter_lower_limit(String parameter_lower_limit) {
		this.parameter_lower_limit = parameter_lower_limit;
	}
	
	public String getParameter_upper_limit() {
		return parameter_upper_limit;
	}
	public void setParameter_upper_limit(String parameter_upper_limit) {
		this.parameter_upper_limit = parameter_upper_limit;
	}
	public String getDefaultvalue() {
		return defaultvalue;
	}
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}
	
	
}
