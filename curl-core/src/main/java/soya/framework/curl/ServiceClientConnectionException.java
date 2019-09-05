package soya.framework.curl;

public class ServiceClientConnectionException extends ServiceClientException {
    public ServiceClientConnectionException() {
    }

    public ServiceClientConnectionException(String message) {
        super(message);
    }

    public ServiceClientConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceClientConnectionException(Throwable cause) {
        super(cause);
    }
}
