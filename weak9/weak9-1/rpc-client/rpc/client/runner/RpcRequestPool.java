
/**
 * Rpc异步任务处理
 */
@Component
public class RpcRequestPool {

    private final ConcurrentHashMap<String, Promise<RpcResponse>> requestPool = new ConcurrentHashMap<>();

    public void submitRequest(String requestId, EventExecutor executor) {
        requestPool.put(requestId, new DefaultPromise<>(executor));
    }

    public RpcResponse fetchResponse(String requestId) throws Exception {
        Promise<RpcResponse> promise = requestPool.get(requestId);
        if (promise == null) {
            return null;
        }
        RpcResponse RpcResponse = promise.get(10, TimeUnit.SECONDS);
        requestPool.remove(requestId);

        RpcRequestManager.destroyChannelHolder(requestId);
        return RpcResponse;
    }

    public void notifyRequest(String requestId, RpcResponse RpcResponse) {
        Promise<RpcResponse> promise = requestPool.get(requestId);
        if (promise != null) {
            promise.setSuccess(RpcResponse);
        }
    }
}
