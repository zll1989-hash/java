package cn.egenie.architect.trace.collect.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import cn.egenie.architect.trace.collect.constants.MdcTraceConstants;
import cn.egenie.architect.trace.core.Span;
import cn.egenie.architect.trace.core.TraceContext;
import cn.egenie.architect.trace.core.enums.ServiceType;
import cn.egenie.architect.trace.core.manager.TraceManager;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/08
 */
@Aspect
public class TraceAspect {

    @Pointcut("@annotation(cn.egenie.architect.trace.core.annotation.Tracing) || @within(cn.egenie.architect.trace.core.annotation.Tracing)")
    public void tracePointCut() {

    }

    @Around("tracePointCut()")
    public Object traceAdvice(ProceedingJoinPoint point) throws Throwable {
        // 内部调用时，验证是否在Trace context下
        Span parentSpan = TraceContext.peek();
        if (parentSpan == null) {
            return point.proceed();
        }
        else {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            String name = method.getDeclaringClass().getSimpleName() + "." + method.getName();

            return TraceManager.tracingWithReturn(
                    ServiceType.INNER_CALL,
                    name,
                    point::proceed,
                    MdcTraceConstants.MDC_RUNNABLE_LIST);
        }
    }
}
