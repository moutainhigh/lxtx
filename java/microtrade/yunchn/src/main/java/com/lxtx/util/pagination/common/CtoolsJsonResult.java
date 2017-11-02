package com.lxtx.util.pagination.common;

import java.util.HashMap;

public class CtoolsJsonResult extends HashMap<String, Object> {

	private static final long serialVersionUID = -9139237223616691067L;
	private static final String RET_CODE = "RET_CODE";
	private static final String RET_MESSAGE = "RET_MESSAGE";
	private static final String RET_CONTENT = "RET_CONTENT";

	public CtoolsJsonResult() {
		this(CtoolsErrorCode.SUCCESS);
	}

	public CtoolsJsonResult(Integer ret_code) {
		this(ret_code, CtoolsErrorCode.getErrInfo(ret_code));
	}

	public CtoolsJsonResult(Integer ret_code, String ret_message) {
		super.put(RET_CODE, ret_code);
		super.put(RET_MESSAGE, ret_message);
	}

	public CtoolsJsonResult save(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public CtoolsJsonResult setRetCode(int ret_code) {
		super.put(RET_CODE, ret_code);
		return this;
	}

	public CtoolsJsonResult setRetMsg(String ret_message) {
		super.put(RET_MESSAGE, ret_message);
		return this;
	}

	public int getRetCode() {
		return (Integer) super.get(RET_CODE);
	}

	public String getRetMessage() {
		return (String) super.get(RET_MESSAGE);
	}

	public CtoolsJsonResult saveRetContent(Object obj) {
		super.put(RET_CONTENT, obj);
		return this;
	}

	/**
	 * 返回类型需要自己指定，like the following ArrayList<LinkedHashMap<String,Object>>
	 * almp = result.get("student_info");
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) throws Exception {
		return (T) super.get(key);
	}
}
