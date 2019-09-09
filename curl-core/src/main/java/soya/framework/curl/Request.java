package soya.framework.curl;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class Request {
    private final Curl curl;
    private final String evaluator;
    private final ImmutableMap<String, String> renderers;

    Request(Curl curl, String evaluator, Map<String, String> renderers) {
        this.curl = curl;
        this.evaluator = evaluator;
        this.renderers = renderers == null? ImmutableMap.<String, String>builder().build(): ImmutableMap.copyOf(renderers);
    }

    public Curl getCurl() {
        return curl;
    }

    public String getEvaluator() {
        return evaluator;
    }

    public ImmutableMap<String, String> getRenderers() {
        return renderers;
    }

    public static Request fromCurl(String curl) {
        return new Request(Curl.fromCurl(curl), null, null);
    }
}
