package com.lxtx.util.pagination;




import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.lxtx.util.pagination.common.PageParameter;

public class DefaultPageParameterConvertor implements IPageParameterConvertor {
    /*
     * 实现参数转换的具体方法.
     * 
     * @see com.citics.cloud.page.IPageParameterConvertor#paramConvertor(javax.servlet.http.HttpServletRequest)
     */
    public PageParameter paramConvertor(HttpServletRequest request) throws Exception {
        PageParameter page = new PageParameter();
        page.setPagenum(Integer.parseInt(request.getParameter("page"))-1);
        page.setPagesize(Integer.parseInt(request.getParameter("rows")));
        page.setSortdatafield(camel4underline(request.getParameter("sidx")));
        page.setSortorder(request.getParameter("sord"));
        return page;
    }
    
    /**
     * 将字符串中的大写转换成“_小写”；如果首字母大写，则小写
     * @param param
     * @return
     */
    private static String camel4underline(String param) {
        Pattern p = Pattern.compile("[A-Z]");
        if (param == null || param.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = p.matcher(param);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i, mc.end() + i, "_" + mc.group().toLowerCase());
            i++;
        }

        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }
}
