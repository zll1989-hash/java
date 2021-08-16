package cn.egenie.architect.trace.core.constants;

import cn.egenie.architect.trace.core.Span;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/07
 */
public class TraceConstants {
    public static final String APP_KEY_CONFIG_FILE_NAME = "application.properties";
    public static final String APP_KEY_PROP_NAME = "dubbo.application.name";

    public static final Span DUMMY_SPAN = new Span();
    /**
     * 第一个span id
     */
    public static final String ROOT_SPAN_ID = "0";

    public static final String CONSUMER_CONTEXT = "consumerContext";

    public static final String SQL_TAG_KEY = "sql";

    public static final String SQL_RESULT_TOTAL_TAG_KEY = "totalRows";

    public static final String JDBC_REF_TAG_KEY = "jdbcRef";

    public static final String HTTP_PATH_TAG_KEY = "httpPath";

    public static final String HTTP_URL_TAG_KEY = "httpUrl";

    public static final String HTTP_METHOD_TAG_KEY = "httpMethod";

    public static final String REQUEST_TAG_KEY = "request";

    public static final String TRACE_ID = "traceId";

    public static final String POINT_SPLIT = "\\.";
}
