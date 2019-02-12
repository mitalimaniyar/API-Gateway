package org.jeavio.apigateway;

public class Parameter {
	
	private String name;
	private String in;
	private String description;
	private boolean required;
	private String type;
	private String format;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIn() {
		return in;
	}
	public void setIn(String in) {
		this.in = in;
	}
	@Override
	public String toString() {
		return "Parameter [name=" + name + ", in=" + in + ", description=" + description + ", required=" + required
				+ ", type=" + type + ", format=" + format + "]";
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	
}
