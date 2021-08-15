package cn.egenie.architect.trace.agent.transform;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.egenie.architect.trace.core.util.TraceUtils;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/10
 */
public class ThreadPoolTransformer implements TraceTransformer {

    private static Set<String> TRANSFER_CLASS_SET = new HashSet<>();

    static {
        TRANSFER_CLASS_SET.add("java.util.concurrent.ThreadPoolExecutor");
        TRANSFER_CLASS_SET.add("java.util.concurrent.ScheduledThreadPoolExecutor");
        TRANSFER_CLASS_SET.add("java.util.concurrent.ForkJoinPool");
    }

    private static Map<String, String> CLASS_NAME_MAP = new HashMap<>();

    private static Set<String> METHOD_NAME_SET = new HashSet<>();

    static {
        METHOD_NAME_SET.add("execute");
        METHOD_NAME_SET.add("submit");
        METHOD_NAME_SET.add("schedule");
        METHOD_NAME_SET.add("scheduleAtFixedRate");
        METHOD_NAME_SET.add("scheduleWithFixedDelay");
    }

    static {
        CLASS_NAME_MAP.put("java.lang.Runnable", "cn.egenie.architect.trace.core.async.TraceRunnable");
        CLASS_NAME_MAP.put("java.util.concurrent.Callable", "cn.egenie.architect.trace.core.async.TraceCallable");
        CLASS_NAME_MAP.put("java.util.function.Supplier", "cn.egenie.architect.trace.core.async.TraceSupplier");
    }

    @Override
    public boolean needTransform(String className) {
        return TRANSFER_CLASS_SET.contains(className);
    }

    @Override
    public void doTransform(CtClass clazz) {
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(t -> METHOD_NAME_SET.contains(t.getName()))
                .forEach(ThreadPoolTransformer::transformMethod);
    }

    private static void transformMethod(CtMethod method) {
        if (method.isEmpty()) {
            return;
        }

        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            return;
        }

        try {
            String methodLongName = method.getLongName();
            methodLongName = methodLongName.substring(0, methodLongName.indexOf("("));
            String name = TraceUtils.getSimpleName(methodLongName);
            CtClass[] parameterTypes = method.getParameterTypes();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < parameterTypes.length; i++) {
                CtClass paraType = parameterTypes[i];
                String paraTypeName = paraType.getName();

                if (CLASS_NAME_MAP.containsKey(paraTypeName)) {
                    // 第一个参数是this，因此这里序号+1
                    int paramIndex = i + 1;
                    String replaceCode = String.format("$%d = %s.getInstance($%d, \"%s\");", paramIndex, CLASS_NAME_MAP.get(paraTypeName), paramIndex, name);
                    sb.append(replaceCode);
                }
            }

            if (sb.length() > 0) {
                method.insertBefore(sb.toString());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
