package com.baidu.third.jxt.sdk.aa;

public class RequestResult {
	public Integer resonseCode;
	public String content = "";

    public RequestResult() {
        super();
    }

    public RequestResult(Integer resonseCode, String content) {
        super();
        this.resonseCode = resonseCode;
        this.content = content;
    }

    public static RequestResult a(String content) {
        return new RequestResult(Integer.valueOf(-1), content);
    }
}
