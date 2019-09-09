package soya.framework.curl;

import java.util.Properties;

public interface ServiceClient {
    String getId();

    Properties getConfiguration();

    String[] getServices();

    ServiceInvocation getServiceInvocation(String service) throws ServiceNotFoundException;
}
