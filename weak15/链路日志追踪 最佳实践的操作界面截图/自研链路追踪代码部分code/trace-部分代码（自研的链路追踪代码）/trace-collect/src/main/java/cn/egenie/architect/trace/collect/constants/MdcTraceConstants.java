package cn.egenie.architect.trace.collect.constants;

import java.util.Arrays;
import java.util.List;

import org.slf4j.MDC;

import cn.egenie.architect.trace.core.TraceContext;
import cn.egenie.architect.trace.core.constants.TraceConstants;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/26
 */
public class MdcTraceConstants {

    /**
     * 使用的地方一定有span，不会NPE
     */
    public static List<Runnable> MDC_RUNNABLE_LIST = Arrays.asList(
            () -> MDC.put(TraceConstants.TRACE_ID, TraceContext.peek().getTraceId()),
            () -> MDC.remove(TraceConstants.TRACE_ID));
}
