package soya.framework.curl;

import java.util.Map;

public interface ServiceMetadata {
    Map<String, String> settings();

    String[] getServiceEndpoints();

    Object getOperationInfo(String service);
}
