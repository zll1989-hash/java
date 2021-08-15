package cn.egenie.architect.trace.agent.transform;

import javassist.CtClass;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/10
 */
public interface TraceTransformer {

    boolean needTransform(String className);

    void doTransform(CtClass clazz);
}
