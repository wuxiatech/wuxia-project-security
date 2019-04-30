/*
 * Created on :2013年8月5日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import cn.wuxia.common.util.ServletUtils;

public class MySecurityHttpServeltRequestWrapper extends HttpServletRequestWrapper {
    public MySecurityHttpServeltRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (ArrayUtils.isEmpty(values)) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return cleanXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (StringUtils.isBlank(value))
            return null;
        return cleanXSS(value);
    }

    private String cleanXSS(String value) {
        //         You'll need to remove the spaces from the html entities below
//        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
//        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
//        value = value.replaceAll("'", "&#39;");
        //value = value.replaceAll("eval\\((.*)\\)", "");
        //value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("<script>", "&lt;script&gt;").replaceAll("</script>", "&lt;/script&gt;");
        value = value.replaceAll("<javascript>", "&lt;javascript&gt;").replaceAll("</javascript>", "&lt;/javascript&gt;");
        return value;
    }

    @Override
    public String getRemoteAddr() {
        return ServletUtils.getRemoteIP((HttpServletRequest) super.getRequest());
    }
}
