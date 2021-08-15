package cn.egenie.architect.common.core.constants;

import java.util.regex.Pattern;

/**
 * @author lucien
 * @since 2021/01/07
 */
public class Constants {
    public static final String NEW_LINE = "\n";
    public static final String TAB = "\t";
    public static final String COLON = ":";
    public static final String SLASH = "/";
    public static final String COMMA = ",";
    public static final String POINT = ".";
    public static final String POINT_SPLITTER = "\\.";
    public static final String QUESTION = "?";
    public static final String AND = "&";
    public static final String EQUAL = "=";
    public static final String UTF8 = "UTF-8";
    public static final String MIDDLE_LINE = "-";
    public static final String UNDER_LINE = "_";
    public static final String EMPTY_STRING = "";


    private static final Pattern DOCUMENT_PATTERN = Pattern.compile(".*\\.(pdf|pptx|docx|ppt|doc|xls|xlsx|zip)$");

    /**
     * 有效
     */
    public final static int VALID = 1;
    /**
     * 无效
     */
    public final static int INVALID = 0;

    /**
     * 有效
     */
    public final static int SUCCESS = 0;
    /**
     * 无效
     */
    public final static int FAILED = 1;
}
