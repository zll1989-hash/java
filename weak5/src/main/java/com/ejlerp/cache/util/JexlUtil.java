package com.ejlerp.cache.util;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.util.Map;

/**
 * JexlUtil
 *
 * @author Eric
 * @date 16/6/3
 */
public class JexlUtil {
    private final static JexlEngine jexl = new JexlEngine();

    public static Object evaluate(String jexlExp, Map<String, Object> params) {
        Expression e = jexl.createExpression(jexlExp);

        JexlContext context = new MapContext(params);
        return e.evaluate(context);
    }

}
