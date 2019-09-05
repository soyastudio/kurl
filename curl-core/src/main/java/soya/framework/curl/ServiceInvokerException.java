package soya.framework.curl;

public class ServiceInvokerException extends RuntimeException {
    public ServiceInvokerException() {
    }

    public ServiceInvokerException(String message) {
        super(message);
    }

    public ServiceInvokerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceInvokerException(Throwable cause) {
        super(cause);
    }
}
