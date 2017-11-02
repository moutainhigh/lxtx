package com.lxtx.util.pagination.common;

public class ApiParam {
	private String name;
	private String mapping;
	private String type;
	private boolean required;
	private String defaultValue;

	/**
	* 
	*/
	public ApiParam() {
		super();
	}

	/**
	 * @param name
	 * @param defaultValue
	 */
	public ApiParam(String name) {
		super();
		this.name = name;
	}

	/**
	 * @param name
	 * @param defaultValue
	 */
	public ApiParam(String name, String defaultValue) {
		super();
		this.name = name;
		this.type = "string";
		required = false;
		this.defaultValue = defaultValue;
	}

	/**
	 * @param name
	 * @param mapping
	 * @param type
	 * @param required
	 * @param defaultValue
	 */
	public ApiParam(String name, String mapping, String type, boolean required, String defaultValue) {
		super();
		this.name = name;
		this.mapping = mapping;
		this.type = type;
		this.required = required;
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the mapping
	 */
	public String getMapping() {
		return mapping;
	}

	/**
	 * @param mapping
	 *            the mapping to set
	 */
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required
	 *            the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
