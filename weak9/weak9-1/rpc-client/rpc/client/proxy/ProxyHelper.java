
/**
 * 动态代理拦截处理
 */
@Component
public class ProxyHelper {

    @Autowired
    private RpcRequestPool rpcRequestPool;

    public <T> T newProxyInstance(Class<T> cls) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(new ProxyCallBackHandler());
        return (T) enhancer.create();
    }

    class ProxyCallBackHandler implements MethodInterceptor {

        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return doIntercept(method, args);
        }

        private Object doIntercept(Method method, Object[] parameters) throws Throwable {
            String requestId = RequestIdUtil.requestId();
            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();

            RpcRequest znsRequest = RpcRequest.builder()
                    .requestId(requestId)
                    .className(className)
                    .methodName(methodName)
                    .parameterTypes(parameterTypes)
                    .parameters(parameters)
                    .build();

            RpcRequestManager.sendRequest(znsRequest);
            RpcResponse znsResponse = rpcRequestPool.fetchResponse(requestId);
            if (znsResponse == null) {
                return null;
            }

            if (znsResponse.isError()) {
                throw znsResponse.getCause();
            }
            return znsResponse.getResult();
        }
    }
}
