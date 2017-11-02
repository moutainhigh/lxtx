package org.takeback.mvc.controller;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.takeback.util.ApplicationContextHolder;
import org.takeback.util.context.Context;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.export.ExcelUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.takeback.mvc.ResponseUtils.jsonView;

@Controller
@RequestMapping("/**/file")
public class ResourceController {
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    private static final String SERVICE = "service";
    private static final String METHOD = "method";
    private static final String FILENAME = "filename";
    private String fileDirectory = "/resources/upload";

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public ModelAndView upload(@RequestParam("file") CommonsMultipartFile file, @RequestParam(required = false)String filePath, HttpServletRequest request){

        Object uid = WebUtils.getSessionAttribute(request, Context.UID);
        if(uid == null){
            return jsonView(403, "not login");
        }

        if(file.getSize() == 0l){
            return jsonView(404, "filesize is 0");
        }

        //后缀
        String oFilename = file.getOriginalFilename();
        String suffix = oFilename.contains(".") ? oFilename.substring(oFilename.lastIndexOf(".")) : "";
        String deskName = org.apache.commons.lang3.StringUtils.join(System.currentTimeMillis(), suffix); //文件名加时间戳
        File directory = new File(request.getSession().getServletContext().getRealPath(StringUtils.isEmpty(filePath)?fileDirectory:filePath));
        if(!directory.exists()){
            directory.mkdirs();
        }

        HashMap map = new HashMap();
        map.put("filename",deskName);

        if(file.getSize() < 10485f){
            map.put("size",decimalFormat.format((float)file.getSize()/1024)+"KB");
        }else{
            map.put("size",decimalFormat.format((float)file.getSize()/1024/1024)+"MB");
        }
        try {
            File deskFile = new File(directory.getAbsolutePath()+"/"+deskName);
            file.transferTo(deskFile);
            return jsonView((Map)ImmutableMap.of("code",200,"success",true,"body",map));
        } catch (IOException e) {
            return jsonView(500, "failure", deskName);
        }
    }


    @RequestMapping(value = "download", method = RequestMethod.POST)
    public void download(@RequestParam("file") CommonsMultipartFile file, HttpServletRequest request){

    }

    /**
     * execl导出
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "exportExcel", method = RequestMethod.GET)
    public ModelAndView exportExcel(HttpServletRequest request, HttpServletResponse response){

        String uid = (String) WebUtils.getSessionAttribute(request, Context.UID);
        Long urt = (Long) WebUtils.getSessionAttribute(request, Context.USER_ROLE_TOKEN);
        if(uid == null || urt == null){
            return jsonView(401, "notLogon");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Map properties = ParameterMap2Map(request.getParameterMap());

        if (StringUtils.isEmpty(properties.get(SERVICE)) || StringUtils.isEmpty(properties.get(METHOD))
                || StringUtils.isEmpty(properties.get(FILENAME))) {
            return jsonView(402, "missing service or method.");
        }

        String service = properties.get(SERVICE).toString();
        String method = properties.get(METHOD).toString();
        String filename = properties.get(FILENAME).toString();

        if (!ApplicationContextHolder.containBean(service)) {
            return jsonView(403, "service is not defined in spring.");
        }

        response.reset();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-disposition", "attachment;filename=" +filename+sdf.format(new Date())+ ".xls");
        OutputStream fOut = null;
        try {
            fOut = response.getOutputStream();
        } catch (IOException e) {
            return jsonView(404, "get OutputStream error!");
        }

        properties.remove(SERVICE);
        properties.remove(METHOD);
        properties.remove(FILENAME);

        LinkedHashMap<String, List<?>> datamap = new LinkedHashMap<String, List<?>>();
        Object s = ApplicationContextHolder.getBean(service);
        Method m = null;
        Object r = null;
        try {
            m = s.getClass().getDeclaredMethod(method,Map.class);
            r = m.invoke(s,properties);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return jsonView(405,"导出文件出错！"+ e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return jsonView(406,"导出文件出错！"+e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return jsonView(407,"导出文件出错！"+ e.getMessage());
        }

        ExcelUtil.ExcelExportData setInfo = ConversionUtils.convert(r,ExcelUtil.ExcelExportData.class);

        try {
            fOut.write(ExcelUtil.export2Stream(setInfo).toByteArray());
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map ParameterMap2Map(Map properties){
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }
}
