package cn.egenie.architect.trace.agent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import cn.egenie.architect.trace.agent.transform.ForkJoinTaskTransformer;
import cn.egenie.architect.trace.agent.transform.ThreadPoolTransformer;
import cn.egenie.architect.trace.agent.transform.TraceTransformer;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/10
 */
public class TraceClassFileTransformer implements ClassFileTransformer {
    List<TraceTransformer> transformerList = new ArrayList<>();

    public TraceClassFileTransformer() {
        transformerList.add(new ThreadPoolTransformer());
        transformerList.add(new ForkJoinTaskTransformer());
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (className == null) {
                return null;
            }

            className = toClassName(className);
            for (TraceTransformer transformer : transformerList) {
                if (transformer.needTransform(className)) {
                    CtClass clazz = getCtClass(classfileBuffer, loader);
                    transformer.doTransform(clazz);
                    return clazz.toBytecode();
                }
            }
        }
        catch (Throwable t) {
            String msg = "Fail to transform class " + className + ", cause: " + t.toString();
            System.out.println(msg);
            throw new IllegalStateException(msg, t);
        }

        return null;
    }

    private String toClassName(final String className) {
        return className.replace('/', '.');
    }

    private CtClass getCtClass(byte[] classFileBuffer, ClassLoader classLoader) throws IOException {
        ClassPool classPool = new ClassPool(true);
        if (null != classLoader) {
            classPool.appendClassPath(new LoaderClassPath(classLoader));
        }

        CtClass clazz = classPool.makeClass(new ByteArrayInputStream(classFileBuffer), false);
        clazz.defrost();
        return clazz;
    }
}
