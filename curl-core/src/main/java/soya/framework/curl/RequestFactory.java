package soya.framework.curl;

public class RequestFactory {
    private ServiceClient client;

    public RequestFactory(ServiceClient client) {
        this.client = client;
    }

    public Request create(String name, String input) throws ServiceNotFoundException {
        return client.getServiceInvocation(name).createRequest(input);
    }
}
