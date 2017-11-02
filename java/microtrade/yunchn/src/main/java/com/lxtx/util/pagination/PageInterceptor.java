package com.lxtx.util.pagination;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.lxtx.util.pagination.common.DataSection;
import com.lxtx.util.pagination.common.PageParameter;

/**
 * @author 对controller进行拦截，处理分页相关的参数设置,基于CFOP源码处理
 *
 */
public class PageInterceptor implements HandlerInterceptor {
    /**
     * logger日志对象.
     */
    public static final Log logger = LogFactory.getLog(PageInterceptor.class);

    /*
     * 拦截器处理，在进入业务处理逻辑之前.
     * 
     * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (StringUtils.isNotBlank(request.getParameter("page"))) {
            String paramConvertor = "DefaultPageParameterConvertor";
            String convertorPackage = "com.lxtx.util.pagination";
            if (request.getParameter("pageParamConvertor") != null) {
                // 如果不带点，说明指定的convertor自带了包路径
                if (paramConvertor.indexOf(".") == -1) {
                    paramConvertor = convertorPackage + "." + paramConvertor;
                } else {
                    // 用户已经指定了类的全路径
                    paramConvertor = request.getParameter("pageParamConvertor");
                }
            } else {
                // 如果未指定convertor，则使用默认值
                paramConvertor = convertorPackage + "." + paramConvertor;
            }
            try {
                IPageParameterConvertor convertor =
                        (IPageParameterConvertor) Class.forName(paramConvertor).newInstance();
                PageParameter page = convertor.paramConvertor(request);
                DataSection.addInput("pages", page);
            } catch (Exception ex) {
                logger.error("+++++++++++++加载分页参数转换器，并进行参数转换出错++++++++++++++");
                throw (ex);
            }
        }else{
          DataSection.addInput("pages", null);
        }
        return true;
    }

    /*
     * 拦截器处理，在业务处理逻辑完成后.
     * 
     * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // 如果modelAndView不为空，则可以通过attribute将page信息返回，controller就不再需要处理page相关内容
        if (null != modelAndView && null == modelAndView.getModelMap().get("pages")) {
            // 将page返回
            modelAndView.getModelMap().addAttribute("pages", DataSection.getInput("pages"));
        }
    }

    /*
     * 拦截器处理，在DispatcherServlet完全处理完请求后被调用.
     * 
     * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub

    }

}
