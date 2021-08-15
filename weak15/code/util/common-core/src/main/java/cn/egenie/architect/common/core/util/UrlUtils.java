package cn.egenie.architect.common.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;

import cn.egenie.architect.common.core.constants.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lucien
 * @since 2021/01/05
 */
@Slf4j
public class UrlUtils {

    public static String buildUrl(String baseUrl, String k1, Object v1) {
        return buildUrl(baseUrl, ImmutableMap.of(k1, v1));
    }

    public static String buildUrl(String baseUrl, String k1, Object v1, String k2, Object v2) {
        return buildUrl(baseUrl, ImmutableMap.of(k1, v1, k2, v2));
    }

    public static String buildUrl(String baseUrl, Map<String, Object> paramMap) {
        String queryString = buildQueryString(paramMap);
        if (StringUtils.isBlank(queryString)) {
            return baseUrl;
        }

        StringBuilder sb = new StringBuilder(baseUrl);

        if (baseUrl.endsWith(Constants.QUESTION) || baseUrl.endsWith(Constants.AND)) {
            return sb.append(queryString).toString();
        }

        return baseUrl.contains(Constants.QUESTION)
                ? sb.append(Constants.AND).append(queryString).toString()
                : sb.append(Constants.QUESTION).append(queryString).toString();
    }

    private static String buildQueryString(Map<String, Object> paramMap) {
        if (MapUtils.isEmpty(paramMap)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        paramMap.forEach((key, value) -> {
            sb.append(key).append(Constants.EQUAL);

            if (value != null) {
                try {
                    String strValue = URLEncoder.encode(value.toString(), Constants.UTF8);
                    sb.append(strValue).append(Constants.AND);
                }
                catch (UnsupportedEncodingException e) {
                    log.warn("Cannot encode {} due to {}", value, e.getMessage(), e);
                }
            }
        });

        return sb.substring(0, sb.length() - 1);
    }
}
