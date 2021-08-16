package cn.egenie.architect.trace.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;


/**
 * 利用JDK1.6的动态代理
 *
 * @author lucien
 * @since 2021/08/06 2021/01/10
 */
public class JvmAgent {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        ClassFileTransformer transformer = new TraceClassFileTransformer();
        instrumentation.addTransformer(transformer, true);
    }
}
