package soya.framework.curl;

public class ServiceClientException extends Exception {
    public ServiceClientException() {
    }

    public ServiceClientException(String message) {
        super(message);
    }

    public ServiceClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceClientException(Throwable cause) {
        super(cause);
    }
}
