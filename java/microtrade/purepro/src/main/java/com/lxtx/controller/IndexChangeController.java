package com.lxtx.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudTarget;
import com.lxtx.model.CloudTargetIndexChange;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.target.TargetService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.BusinessUtil;
import com.lxtx.util.tool.JsonUtil;
/**
 * This happens when the index value changes for a specific target  
 *
 */
@Controller
@RequestMapping(value = "/index")
public class IndexChangeController {
	
  private static Map<String, IndexChange> indexChangeMap = new HashMap<String, IndexChange>();	
  
  @Autowired
  private TargetService targetService;
  @Autowired
  private UserService userService;
  @Autowired
  private OrderService orderService;
  
  private static final Logger logger = LoggerFactory.getLogger(IndexChangeController.class);

  @RequestMapping(value = "/update")
  @ResponseBody
//  public Object index(@RequestParam(value = "code", required = true) String code, @RequestParam(value = "value", required = true) String value) {
  /*public Object index(@RequestParam(value = "data", required = true) String data) {
	  AjaxJson json = new AjaxJson();
	  Map<String, Object> map = (Map<String, Object>) JsonUtil.convertStringToObject(data);
	  List<Map<String, Object>> mapList = (List<Map<String, Object>>)map.get("Symbol");
	  for (Map<String, Object> m : mapList) {
		  String code = m.get("Code") + "";
		  String now = ((Double)m.get("Now")).intValue() +"";
		  String time = m.get("Time") + "";
		  IndexChange c = indexChangeMap.get(code);
		  if (c != null && c.getTime().equals(time) && c.getIndex() == Integer.valueOf(now)) {
			  logger.info("The index update message has been saved.");
			  json.setMessage("Already saved.");
			  return json;
		  } else {
			  indexChangeMap.put(code, new IndexChange(time, Integer.valueOf(now)));
		  }
		  
		  updateForCode(code, now, time);
	  }
    
      return json;
  }*/
  
  private boolean updateForCode(String code, String value, String time) {
	  //firstly, find the record in the target table
      CloudTarget target = targetService.getTargetByName(code);
      int v = Integer.valueOf(value);
      
      if (target == null) {
    	  return false;
      } else {
        if (target.getCurrentIndex() ==  v) {
        	return false;
        } else {
          //firstly, save the updated index value
          target.setCurrentIndex(v);
          targetService.updateTarget(target);
          //add a piece of record to the target index change table 
          CloudTargetIndexChange record = new CloudTargetIndexChange();
          record.setIndex(v);
          record.setSubject(code);
          //get the date object
          DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"); 
          DateTime dt = DateTime.parse(time, format);
          //server is using GMT time zone
          record.setTime(new Date(dt.getMillis() + 8 * 3600 * 1000));
          targetService.insertIndexChangeRecord(record);
          orderService.markOrderInProcess(code, v);
        }
      }
      return true;
  }
  
}
