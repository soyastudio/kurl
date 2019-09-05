package soya.framework.curl;

public class ServiceInvocationException extends ServiceClientException {
    public ServiceInvocationException() {
    }

    public ServiceInvocationException(String message) {
        super(message);
    }

    public ServiceInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceInvocationException(Throwable cause) {
        super(cause);
    }

}
