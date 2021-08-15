package cn.egenie.architect.trace.agent.transform;

import java.lang.reflect.Modifier;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/11
 */
public class ForkJoinTaskTransformer implements TraceTransformer {
    private static final String FORK_JOIN_TASK_CLASS_NAME = "java.util.concurrent.ForkJoinTask";
    private static final String TRACE_RECURSIVE_TASK_CLASS_NAME = "cn.egenie.architect.trace.core.async.TraceRecursiveTask";
    private static final String TRACE_RECURSIVE_ACTION_CLASS_NAME = "cn.egenie.architect.trace.core.async.TraceRecursiveAction";

    @Override
    public boolean needTransform(String className) {
        return FORK_JOIN_TASK_CLASS_NAME.equals(className);
    }

    @Override
    public void doTransform(CtClass clazz) {
        try {
            String addFieldName = "asyncParent";
            CtField addField = CtField.make("private cn.egenie.architect.trace.core.Span " + addFieldName + ";", clazz);
            clazz.addField(addField, "cn.egenie.architect.trace.core.Span.copyAsAsyncParent(cn.egenie.architect.trace.core.TraceContext.peek(), \"ForkJoinTask.doExec\");");

            String doExecMethodName = "doExec";
            CtMethod doExecMethod = clazz.getDeclaredMethod(doExecMethodName);
            CtMethod newDoExecMethod = CtNewMethod.copy(doExecMethod, doExecMethodName, clazz, null);

            String originalDoExecMethodName = "originalDoExec";
            doExecMethod.setName(originalDoExecMethodName);
            // 原来是public，则改成private，默认方法则不改
            doExecMethod.setModifiers(doExecMethod.getModifiers() & ~Modifier.PUBLIC | Modifier.PRIVATE);

            final String code = "{\n"
                    + "if (this instanceof " + TRACE_RECURSIVE_TASK_CLASS_NAME + " || this instanceof " + TRACE_RECURSIVE_ACTION_CLASS_NAME + ") {\n"
                    + "    return " + originalDoExecMethodName + "($$);\n"
                    + "}\n"
                    + "if (" + addFieldName + " == null ) {\n"
                    + "   return " + originalDoExecMethodName + "($$);\n"
                    + "}\n"
                    + "cn.egenie.architect.trace.core.TraceContext.push(" + addFieldName + ");\n"
                    + "try {\n"
                    + "    return " + originalDoExecMethodName + "($$);\n"
                    + "}\n"
                    + "finally {\n"
                    + "    cn.egenie.architect.trace.core.manager.TraceManager.endSpan();\n"
                    + "}\n"
                    + "}";
            newDoExecMethod.setBody(code);
            clazz.addMethod(newDoExecMethod);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
