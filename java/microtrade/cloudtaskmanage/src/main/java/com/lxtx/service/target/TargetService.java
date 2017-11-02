package com.lxtx.service.target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudOrderMapper;
import com.lxtx.dao.CloudTargetIndexChangeMapper;
import com.lxtx.dao.CloudTargetMapper;
import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudTarget;
import com.lxtx.model.CloudTargetIndexChange;

@Service
public class TargetService {
  @Autowired
  private CloudTargetMapper targetMapper;
  
  @Autowired
  private CloudOrderMapper orderMapper;
  
  @Autowired
  private CloudTargetIndexChangeMapper indexChangeMapper;
  
  public CloudTarget getTargetByName(String name) {
    return targetMapper.selectByName(name);
  }
  
  public Map<String, CloudTarget> listTargets() {
    List<CloudTarget> targetList = targetMapper.selectAll();
    Map<String, CloudTarget> targetMap = new HashMap<String, CloudTarget>();
    
    for (CloudTarget target : targetList) {
      targetMap.put(target.getName(), target);
    }
    return targetMap;
  }
  
  public void updateOrder(CloudOrder order) {
    orderMapper.updateByPrimaryKey(order);
  }
  
  public void updateTarget(CloudTarget target) {
    targetMapper.updateByPrimaryKey(target);
  }
  
  public List<CloudOrder> listOrderByLimit(String subject, int currentIndex) {
    CloudOrder cloudOrder = new CloudOrder();
    cloudOrder.setSubject(subject);
    cloudOrder.setOrderIndex(currentIndex);
    return orderMapper.selectByLimit(cloudOrder);
  }
  
  public List<CloudOrder> listOrderByUpperLimit(int indexValue) {
    return orderMapper.selectByUpperLimit(indexValue);
  }
  
  public List<CloudOrder> listOrderByDownLimit(int indexValue) {
    return orderMapper.selectByDownLimit(indexValue);
  }
  
  public void insertIndexChangeRecord(CloudTargetIndexChange record) {
    indexChangeMapper.insert(record);
  }
}