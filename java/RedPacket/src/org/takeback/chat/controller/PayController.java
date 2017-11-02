// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import cn.beecloud.BCCache;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.takeback.util.MD5StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import org.springframework.web.util.WebUtils;
import java.util.Date;
import org.takeback.chat.entity.PubRecharge;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.takeback.util.annotation.AuthPassport;
import cn.beecloud.bean.BCOrder;
import org.takeback.pay.PaymentException;
import org.takeback.mvc.ResponseUtils;
import java.util.HashMap;
import org.takeback.pay.PayOrderFactory;
import java.util.UUID;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import org.takeback.thirdparty.support.XinTongConfig;
import org.takeback.thirdparty.support.KouDaiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.PubRechargeService;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping({ "/pay" })
public class PayController
{
    public static final Logger LOGGER;
    @Autowired
    private PubRechargeService pubRechargeService;
    @Autowired
    private KouDaiConfig kouDaiConfig;
    @Autowired
    private XinTongConfig xinTongConfig;
    private static long orderNum;
    private static String date;
    
    @AuthPassport
    @RequestMapping(value = { "apply" }, method = { RequestMethod.POST })
    public ModelAndView payApply(@RequestBody final Map<String, String> data, final HttpSession session) {
        final String payChannel = data.get("payChannel");
        final String strTotalFee = data.get("totalFee");
        final String title = "充值";
        String identityId = null;
        if (payChannel.equals("YEE_WAP")) {
            identityId = UUID.randomUUID().toString().replace("-", "");
            session.setAttribute("identityId", (Object)identityId);
        }
        final Integer totalFee = (int)(Double.valueOf(strTotalFee) * 100.0);
        try {
            final BCOrder bcOrder = PayOrderFactory.getInstance().getPayOrder(payChannel, totalFee, title, identityId);
            final Map<String, String> result = new HashMap<String, String>();
            result.put("url", bcOrder.getUrl());
            result.put("html", bcOrder.getHtml());
            return ResponseUtils.jsonView(result);
        }
        catch (PaymentException e) {
            PayController.LOGGER.error("Failed to apply payment transaction", (Throwable)e);
            return ResponseUtils.jsonView(500, "支付失败.");
        }
    }
    
    @AuthPassport
    @RequestMapping(value = { "apply/wx" }, method = { RequestMethod.POST })
    public ModelAndView payApplyWx(@RequestBody final Map<String, String> data) {
        final String strTotalFee = data.get("totalFee");
        final String title = "充值";
        final double totalFee = Double.valueOf(strTotalFee);
        final String url = PayOrderFactory.getInstance().getWxAuthorizeUrl(title, totalFee);
        return ResponseUtils.jsonView(url);
    }
    
    @AuthPassport
    @RequestMapping(value = { "apply/wx" }, method = { RequestMethod.GET })
    public ModelAndView payApplyWxRedirected(@RequestParam final double totalFee, @RequestParam final String code, final HttpServletRequest request) {
        final String title = "充值";
        BCOrder bcOrder;
        try {
            bcOrder = PayOrderFactory.getInstance().getWxJSPayOrder((int)(totalFee * 100.0), title, code);
        }
        catch (PaymentException e) {
            PayController.LOGGER.error("Failed to apply payment transaction", (Throwable)e);
            return new ModelAndView("wxpay", (Map)new HashMap<String, Integer>() {
                {
                    this.put("code", 500);
                }
            });
        }
        final PubRecharge pubRecharge = new PubRecharge();
        pubRecharge.setStatus("1");
        pubRecharge.setDescpt(title);
        pubRecharge.setFee(totalFee);
        pubRecharge.setGoodsname(title);
        pubRecharge.setPayno(bcOrder.getObjectId());
        pubRecharge.setTradeno(bcOrder.getBillNo());
        pubRecharge.setTradetime(new Date());
        pubRecharge.setGift(0.0);
        pubRecharge.setRechargeType("1");
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        pubRecharge.setUid(uid);
        final PubUser u = this.pubRechargeService.get(PubUser.class, uid);
        pubRecharge.setUserIdText(u.getUserId());
        this.pubRechargeService.addRechargeRecord(pubRecharge);
        final Map<String, String> map = (Map<String, String>)bcOrder.getWxJSAPIMap();
        return new ModelAndView("wxpay", (Map)map);
    }
    
    @AuthPassport
    @RequestMapping(value = { "apply/koudai" }, method = { RequestMethod.POST })
    public ModelAndView payApplyKoudai(@RequestBody final Map<String, String> params, final HttpServletRequest request) {
        final Object totalFee = params.get("totalFee");
        if (totalFee == null || !NumberUtils.isNumber(totalFee.toString())) {
            return ResponseUtils.jsonView(500, "充值金额不对。");
        }
        final Double fee = NumberUtils.createDouble(totalFee.toString());
        if (fee < 2.0) {
            return ResponseUtils.jsonView(500, "充值金额不能小于￥2.00。");
        }
        String title = "充值";
        if (params.get("title") != null) {
            title = params.get("title");
        }
        final PubRecharge pubRecharge = new PubRecharge();
        pubRecharge.setStatus("1");
        pubRecharge.setDescpt(title);
        pubRecharge.setFee(fee);
        pubRecharge.setGoodsname(title);
        pubRecharge.setTradeno(UUID.randomUUID().toString().replace("-", ""));
        pubRecharge.setTradetime(new Date());
        pubRecharge.setGift(0.0);
        pubRecharge.setRechargeType("1");
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        pubRecharge.setUid(uid);
        final PubUser u = this.pubRechargeService.get(PubUser.class, uid);
        pubRecharge.setUserIdText(u.getUserId());
        this.pubRechargeService.addRechargeRecord(pubRecharge);
        final String chanel = params.get("payChannel");
        final String price = String.format("%.2f", pubRecharge.getFee());
        String url = this.kouDaiConfig.getRestApiAddress() + "?P_UserId=" + this.kouDaiConfig.getPartnerId() + "&P_OrderId=" + pubRecharge.getTradeno() + "&P_FaceValue=" + price + "&P_Price=" + price + "&P_ChannelId=" + chanel + "&P_Quantity=1&P_Result_URL=" + this.kouDaiConfig.getGameServerBaseUrl() + "pay/feedback/koudai&P_PostKey=";
        final String raw = this.kouDaiConfig.getPartnerId() + "|" + pubRecharge.getTradeno() + "|||" + String.format("%.2f", pubRecharge.getFee()) + "|" + chanel + "|" + this.kouDaiConfig.getSecretCode();
        final String postKey = MD5StringUtil.MD5Encode(raw);
        url += postKey;
        return ResponseUtils.jsonView(url);
    }
    
    @RequestMapping(value = { "feedback/koudai" }, method = { RequestMethod.GET })
    public void koudaiCallback(@RequestParam("P_OrderId") final String tradeNo, @RequestParam("P_UserId") final String partnerId, @RequestParam("P_ErrCode") final int errorCode, @RequestParam(value = "P_ErrMsg", defaultValue = "") final String errorMsg, @RequestParam("P_FaceValue") final double totalFee, @RequestParam("P_ChannelId") final String chanelId, @RequestParam("P_PostKey") final String postKey0, @RequestParam("P_CardId") final String cardId, @RequestParam("P_CardPass") final String cardPass, final HttpServletResponse response) throws IOException {
        if (!partnerId.equals(this.kouDaiConfig.getPartnerId())) {
            PayController.LOGGER.error("Pay trade [{}] is not mine.", (Object)tradeNo);
            response.getOutputStream().println("errcode=0");
            return;
        }
        final PubRecharge pubRecharge = this.pubRechargeService.getRechargeRecordByTradeNo(tradeNo);
        if (errorCode != 0) {
            PayController.LOGGER.error("Pay trade [{}] failed: ", (Object)tradeNo, (Object)errorMsg);
        }
        else {
            if (pubRecharge.getStatus().equals("2")) {
                response.getOutputStream().println("errcode=0");
                return;
            }
            if (pubRecharge.getFee() != totalFee) {
                PayController.LOGGER.error("Total fee of trade [{}] is different with the pay trade, expect: {}, {} in fact", new Object[] { tradeNo, pubRecharge.getFee(), totalFee });
                return;
            }
            final String raw = partnerId + "|" + tradeNo + "|" + cardId + "|" + cardPass + "|" + String.format("%.5f", totalFee) + "|" + chanelId + "|" + this.kouDaiConfig.getSecretCode();
            final String postKey = MD5StringUtil.MD5Encode(raw);
            if (!postKey.equals(postKey0)) {
                PayController.LOGGER.error("Post key of trade [{}] not match, expected is: {}, {} in fact", new Object[] { tradeNo, postKey, postKey0 });
                return;
            }
            pubRecharge.setRealfee(totalFee);
            this.pubRechargeService.setRechargeFinished(pubRecharge);
        }
        response.getOutputStream().println("errcode=0");
    }
    
    @RequestMapping(value = { "feedback/koudai" }, method = { RequestMethod.POST })
    public void koudaiCallback0(@RequestBody final Map<String, Object> data, final HttpServletResponse response) throws IOException {
        final String tradeNo = (String)data.get("P_OrderId");
        final String partnerId = (String)data.get("P_UserId");
        if (!partnerId.equals(this.kouDaiConfig.getPartnerId())) {
            PayController.LOGGER.error("Pay trade is not mine.");
            response.getOutputStream().println("errcode=0");
            return;
        }
        final int errorCode = (Integer)data.get("P_ErrCode");
        final PubRecharge pubRecharge = this.pubRechargeService.getRechargeRecordByTradeNo(tradeNo);
        if (errorCode != 0) {
            PayController.LOGGER.error("Pay trade failed: ", data.get("P_ErrMsg"));
        }
        else {
            if (pubRecharge.getStatus().equals("2")) {
                response.getOutputStream().println("errcode=0");
                return;
            }
            final double totalFee = (Double)data.get("P_FaceValue");
            final String chanelId = (String)data.get("P_ChannelId");
            if (pubRecharge.getFee() != totalFee) {
                PayController.LOGGER.error("Total fee is different with the pay trade, expect: {}, {} in fact", (Object)pubRecharge.getFee(), (Object)totalFee);
                return;
            }
            final String raw = partnerId + "|" + tradeNo + "|||" + String.format("%.2f", totalFee) + "|" + chanelId + "|" + this.kouDaiConfig.getSecretCode();
            final String postKey = MD5StringUtil.MD5Encode(raw);
            if (!postKey.equals(data.get("P_PostKey"))) {
                PayController.LOGGER.error("Post key not match, expected is: {}, {} in fact", (Object)postKey, data.get("P_PostKey"));
                return;
            }
            pubRecharge.setRealfee(totalFee);
            this.pubRechargeService.setRechargeFinished(pubRecharge);
        }
        response.getOutputStream().println("errcode=0");
    }
    
    @RequestMapping(value = { "webhook" }, method = { RequestMethod.POST })
    public void callback(@RequestBody final Map<String, Object> data, final HttpServletResponse response) throws IOException {
        final String sign = (String)data.get("sign");
        final Long timestamp = (Long)data.get("timestamp");
        final String transactionId = (String)data.get("transaction_id");
        final String text = BCCache.getAppID() + BCCache.getAppSecret() + timestamp;
        final String mySign = MD5StringUtil.MD5EncodeUTF8(text);
        final long timeDifference = System.currentTimeMillis() - timestamp;
        if (!mySign.equals(sign) || timeDifference > 300000L) {
            PayController.LOGGER.error("Sign validation failed: {}.", (Object)transactionId);
            response.getOutputStream().println("fail");
            return;
        }
        final Boolean success = (Boolean)data.get("trade_success");
        if (success) {
            final String transactionType = (String)data.get("transaction_type");
            if (transactionType.equals("PAY")) {
                final Integer transactionFee = (Integer)data.get("transaction_fee");
                final PubRecharge pubRecharge = this.pubRechargeService.getRechargeRecordByTradeNo(transactionId);
                if (pubRecharge == null) {
                    response.getOutputStream().println("fail");
                    return;
                }
                if (pubRecharge.getStatus().equals("2")) {
                    response.getOutputStream().println("success");
                    return;
                }
                pubRecharge.setRealfee(transactionFee / 100.0);
                this.pubRechargeService.setRechargeFinished(pubRecharge);
                response.getOutputStream().println("success");
            }
        }
    }
    
    @AuthPassport
    @RequestMapping(value = { "apply/xintong" }, method = { RequestMethod.POST })
    public ModelAndView payApplyXintong(@RequestBody final Map<String, String> params, final HttpServletRequest request) {
        final Object totalFee = params.get("totalFee");
        if (totalFee == null || !NumberUtils.isNumber(totalFee.toString())) {
            return ResponseUtils.jsonView(500, "充值金额不对。");
        }
        final Double fee = NumberUtils.createDouble(totalFee.toString());
        String title = "充值";
        if (params.get("title") != null) {
            title = params.get("title");
        }
        final PubRecharge pubRecharge = new PubRecharge();
        pubRecharge.setStatus("1");
        pubRecharge.setDescpt(title);
        pubRecharge.setFee(fee);
        pubRecharge.setGoodsname(title);
        pubRecharge.setTradeno(getOrderNo());
        pubRecharge.setTradetime(new Date());
        pubRecharge.setGift(0.0);
        pubRecharge.setRechargeType("1");
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        pubRecharge.setUid(uid);
        final PubUser u = this.pubRechargeService.get(PubUser.class, uid);
        pubRecharge.setUserIdText(u.getUserId());
        this.pubRechargeService.addRechargeRecord(pubRecharge);
        final String price = String.format("%.2f", pubRecharge.getFee());
        final String type = params.get("payChannel");
        final String sign = "parter=" + this.xinTongConfig.getPartnerId() + "&type=" + type + "&value=" + price + "&orderid=" + pubRecharge.getTradeno() + "&callbackurl=" + this.xinTongConfig.getGameServerBaseUrl() + "pay/feedback/xintong";
        System.out.println(sign + this.xinTongConfig.getSecretCode());
        final String signKey = MD5StringUtil.MD5Encode(sign + this.xinTongConfig.getSecretCode());
        final String url = this.xinTongConfig.getRestApiAddress() + "?" + sign + "&hrefbackurl=" + this.xinTongConfig.getGameServerBaseUrl() + "&agent=&sign=" + signKey;
        System.out.println(url);
        return ResponseUtils.jsonView(url);
    }
    
    @RequestMapping(value = { "feedback/xintong" }, method = { RequestMethod.GET })
    public void xintongCallback(@RequestParam("orderid") final String tradeNo, @RequestParam("opstate") final int opstate, @RequestParam("ovalue") final double totalFee, @RequestParam("sign") final String postKey0, final HttpServletResponse response) throws IOException {
        final String raw = "orderid=" + tradeNo + "&opstate=" + opstate + "&ovalue=" + String.format("%.2f", totalFee) + this.xinTongConfig.getSecretCode();
        final String postKey = MD5StringUtil.MD5Encode(raw);
        if (!postKey.equals(postKey0)) {
            PayController.LOGGER.error("Post key of trade [{}] not match, expected is: {}, {} in fact", new Object[] { tradeNo, postKey, postKey0 });
            response.getOutputStream().println("opstate=-2");
            return;
        }
        final PubRecharge pubRecharge = this.pubRechargeService.getRechargeRecordByTradeNo(tradeNo);
        if (opstate != 0) {
            PayController.LOGGER.error("Pay trade [{}] failed: ", (Object)tradeNo, (Object)"参数无效或签名错误！");
        }
        else {
            if (pubRecharge.getStatus().equals("2")) {
                response.getOutputStream().println("opstate=0");
                return;
            }
            if (pubRecharge.getFee() != totalFee) {
                PayController.LOGGER.error("Total fee of trade [{}] is different with the pay trade, expect: {}, {} in fact", new Object[] { tradeNo, pubRecharge.getFee(), totalFee });
                return;
            }
            pubRecharge.setRealfee(totalFee);
            this.pubRechargeService.setRechargeFinished(pubRecharge);
        }
        response.getOutputStream().println("opstate=0");
    }
    
    public static void main(final String[] args) {
        final Double d = 0.1;
        System.out.println(String.format("%.2f", d));
    }
    
    public static synchronized String getOrderNo() {
        final String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        if (PayController.date == null || !PayController.date.equals(str)) {
            PayController.date = str;
            PayController.orderNum = 0L;
        }
        ++PayController.orderNum;
        long orderNo = Long.parseLong(PayController.date) * 10000L;
        orderNo += PayController.orderNum;
        return orderNo + "";
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PayController.class);
        PayController.orderNum = 0L;
    }
}
