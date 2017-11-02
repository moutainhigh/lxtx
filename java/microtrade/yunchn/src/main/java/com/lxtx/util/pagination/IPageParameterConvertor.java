package com.lxtx.util.pagination;

import javax.servlet.http.HttpServletRequest;

import com.lxtx.util.pagination.common.PageParameter;


/**
 * 用于定义page参数转换器.
 */
public interface IPageParameterConvertor {
    /**
     * 定义page参数转换方法，根据自己使用的前端控件，将其交互参数，转换为page拦截器可以使用的pageParameter类型变量.
     * 
     * @param request
     * @return
     */
    public PageParameter paramConvertor(HttpServletRequest request) throws Exception;
}
