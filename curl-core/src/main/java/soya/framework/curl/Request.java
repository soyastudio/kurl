package soya.framework.curl;

import java.util.Map;

public final class Request {
    private final String name;
    private final Curl curl;
    private Evaluator resultEvaluator;
    private Map<String, Renderer> renderers;

    public Request(String name, Curl curl) {
        this.name = name;
        this.curl = curl;
    }

    public String getName() {
        return name;
    }

    public Curl getCurl() {
        return curl;
    }
}
