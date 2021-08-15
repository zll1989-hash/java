package cn.egenie.architect.common.core.exception;

import lombok.Getter;

/**
 * 微服务异常
 *
 * @author lucien
 * @since 2021/01/05
 */
@Getter
public class ServiceException extends BusinessException {
    /**
     * rpc interface name
     */
    private String interfacePath;
    private String exceptionClass;

    public ServiceException() {

    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException setInterfacePath(String interfacePath) {
        this.interfacePath = interfacePath;
        return this;
    }

    public ServiceException setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
        return this;
    }
}
