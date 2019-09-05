package soya.framework.curl;

public class ServiceProviderNotFoundException extends ServiceClientException {
    public ServiceProviderNotFoundException() {
    }

    public ServiceProviderNotFoundException(String message) {
        super(message);
    }

    public ServiceProviderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceProviderNotFoundException(Throwable cause) {
        super(cause);
    }
}
