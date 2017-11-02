package com.baidu.alipay.script;

import java.util.List;

import com.baidu.alipay.LogUtil;

import android.content.Context;

public class Replace extends Tags {
	private static final String TAG = "Replace";

	private static final String PROP_FROM = "from";
	private static final String PROP_SRC = "src";
	private static final String PROP_DEST = "dest";
	private static final String PROP_RESULT = "result";
	private static final String PROP_COMPARE = "compare";

	private String from;
	private String src;
	private String dest;
	private String result;
	private String compare = null;

	public Replace(String xml, boolean dynamic) {
		this.from = getNodeValue(xml, PROP_FROM);
		this.src = getNodeValue(xml, PROP_SRC);
		this.dest = getNodeValue(xml, PROP_DEST);

		this.result = getNodeValue(xml, PROP_RESULT);
		this.compare = getNodeValue(xml, PROP_COMPARE);

		this.dynamic = dynamic;
	}

	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
//		LogUtil.e(TAG, "replace from: " + from);
		String data = sendSms.getDataValue(from);
//		LogUtil.e(TAG, "replace data：~~~~~~" + data);
		rStr = data;

		data = data.replace(src, dest);

		if (compare != null && compare.length() > 0) {
			if (!data.equals(compare)) {
				return "r_compare_f";
			}
		}

		sendSms.setDataValue(result, data);
//		LogUtil.e(TAG, "replace截取" + from + "的结果:" + data);

		return TAGS_EXEC_SUCC;
	}

	@Override
	public String getTag() {
		return TAGS_REPLACE;
	}

}
