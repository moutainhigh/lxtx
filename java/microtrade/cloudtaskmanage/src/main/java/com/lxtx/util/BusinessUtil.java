package com.lxtx.util;

import com.lxtx.model.CloudTarget;

public class BusinessUtil {
  
  public static float getCommissionRate(CloudTarget target, int currentLimit) {
    if (currentLimit == target.getLimit1()) {
      return 0.13f;
    } else if (currentLimit == target.getLimit2()) {
      return 0.11f;
    } else {
      return 0.075f;
    }
  }
}
