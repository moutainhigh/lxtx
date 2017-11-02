package com.lxtx.util.tencent;

import java.util.Map;

import com.lxtx.util.CommonUtil;

public class SignUtil {
  /**
   * 获取支付所需签名
   * @param ticket
   * @param timeStamp
   * @param card_id
   * @param code
   * @return
   * @throws Exception
   */
  public static String getPayCustomSign(Map<String, String> bizObj,String key) throws Exception {
      String bizString = CommonUtil.FormatBizQueryParaMap(bizObj,
              false);
      return MD5SignUtil.sign(bizString, key);
  }
 }
