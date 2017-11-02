package com.lxtx.util.tool;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**  
 * @description: 继承至SimpleMappingExceptionResolver的自定义的统一异常处理 
 */  
public class CustomMappingExceptionResolver extends SimpleMappingExceptionResolver{  
    private final static Logger log = LoggerFactory.getLogger(CustomMappingExceptionResolver.class);   
              
    @Override  
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,  
            Object handler, Exception ex) {  
        Map<String, Exception> model = new HashMap<String, Exception>();  
        model.put("ex", ex);  
        ModelAndView modelAndView = new ModelAndView("/error/exception",model);  
          
        /*错误日志输出到控制台*/  
        log.error(ex.getMessage());  
                  
        return modelAndView;  
    }  
}  