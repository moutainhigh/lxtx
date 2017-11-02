package com.lxtx.util.pagination.common;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

public class DataSection {
    private static final ThreadLocal<String> _uri = new ThreadLocal<String>();
    private static final ThreadLocal<HttpServletRequest> _request = new ThreadLocal<HttpServletRequest>();
    private static final ThreadLocal<HttpServletResponse> _response = new ThreadLocal<HttpServletResponse>();
    private static final ThreadLocal<BizException> _exception = new ThreadLocal<BizException>();
    private static final ThreadLocal<String> _view = new ThreadLocal<String>();
    private static final ThreadLocal<ModelMap> _inputs = new ThreadLocal<ModelMap>() {
        @Override
        protected ModelMap initialValue() {
            return new ModelMap();
        }
    };

    private static final ThreadLocal<ModelMap> _varputs = new ThreadLocal<ModelMap>() {
        @Override
        protected ModelMap initialValue() {
            return new ModelMap();
        }
    };


    private static final ThreadLocal<ModelMap> _outputs = new ThreadLocal<ModelMap>() {
        @Override
        protected ModelMap initialValue() {
            return new ModelMap();
        }
    };

    private static final ThreadLocal<ModelMap> _error = new ThreadLocal<ModelMap>() {
        @Override
        protected ModelMap initialValue() {
            return new ModelMap();
        }
    };


    public static String getUri() {
        return _uri.get();
    }

    public static void setUri(String uri) {
        _uri.set(uri);
    }

    public static String getView() {
        return _view.get();
    }

    public static void setView(String view) {
        _view.set(view);
    }

    public static HttpServletRequest getRequest() {
        return _request.get();
    }

    public static void setRequest(HttpServletRequest request) {
        _request.set(request);
    }

    public static HttpServletResponse getResponse() {
        return _response.get();
    }

    public static void setResponse(HttpServletResponse response) {
        _response.set(response);
    }

    public static BizException getException() {
        return _exception.get();
    }

    public static void setException(BizException ex) {
        _exception.set(ex);
        _error.get().put("ERROR_CODE", ex.getErrcode());
        _error.get().put("ERROR_MESSAGE", ex.getMessage());
    }

    public static void addInput(String key, Object value) {
        _inputs.get().put(key, value);
    }

    public static void addInput(Map<String, Object> attributes) {
        _inputs.get().addAllAttributes(attributes);
    }

    public static Object getInput(String key) {
        return _inputs.get().get(key);
    }

    public static ModelMap input() {
        return _inputs.get();
    }

    public static void addVarput(String key, Object value) {
        _varputs.get().put(key, value);
    }

    public static void addVarput(Map<String, Object> attributes) {
        _varputs.get().addAllAttributes(attributes);
    }

    public static Object getVarput(String key) {
        return _varputs.get().get(key);
    }

    public static ModelMap varput() {
        return _varputs.get();
    }


    public static void addOutput(String key, Object value) {
        _outputs.get().put(key, value);
    }

    public static void addOutput(Map<String, Object> attributes) {
        _outputs.get().addAllAttributes(attributes);
    }

    public static Object getOutput(String key) {
        return _outputs.get().get(key);
    }

    public static ModelMap output() {
        return _outputs.get();
    }

    public static ModelMap error() {
        return _error.get();
    }

    public static Object get(String key) {
        Object value = getOutput(key);
        if (value != null) {
            return value;
        }

        value = getVarput(key);
        if (value != null) {
            return value;
        }

        return getInput(key);

    }

    public static Object get(ApiParam param) {
        String mapping = param.getMapping() == null ? param.getName() : param.getMapping();
        Object value = get(mapping);
        if (value != null) {
            return value;
        }

        if (param.getDefaultValue() != null) {
            switch (param.getType()) {
                case "int":
                    return Integer.parseInt(param.getDefaultValue());
                case "long":
                    return Long.parseLong(param.getDefaultValue());
                case "double":
                    return Double.parseDouble(param.getDefaultValue());
                case "float":
                    return Float.parseFloat(param.getDefaultValue());
                default:
                    return param.getDefaultValue();
            }
        }

        if (param.isRequired()) {
            throw new BizException(1000, "缺乏属性[" + mapping + "]的值");
        }

        return null;

    }

    public static void clear() {
        _uri.remove();
        _request.remove();
        _response.remove();
        _exception.remove();
        _inputs.get().clear();
        _varputs.get().clear();
        _outputs.get().clear();
    }

}
