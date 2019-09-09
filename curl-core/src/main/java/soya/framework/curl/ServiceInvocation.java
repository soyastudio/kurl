package soya.framework.curl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import soya.framework.curl.support.CurlUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ServiceInvocation {
    private String name;
    private CurlTemplate template;
    private String evaluator;
    private Map<String, String> renderers;

    private ServiceInvocation(String name, CurlTemplate template, String evaluator, Map<String, String> renderers) {
        this.name = name;
        this.template = template;
        this.evaluator = evaluator;
        this.renderers = renderers;
    }

    public Request createRequest(String json) {
        Curl curl = template.create(json);
        return new Request(template.create(json), evaluator, renderers);
    }

    public static ServiceInvocation[] fromYaml(String yaml) {
        List<ServiceInvocation> list = new ArrayList<>();
        String json = CurlUtils.yamlToJson(yaml);
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        jsonObject.entrySet().forEach(e -> {
            String name = e.getKey();
            JsonObject object = e.getValue().getAsJsonObject();
            String curl = object.get("curl").getAsString();
            JsonElement params = object.get("params");
            JsonElement evaluator = object.get("evaluator");
            JsonElement renders = object.get("renderers");

            ServiceInvocation.ServiceInvocationBuilder builder = ServiceInvocation.builder(name);
            builder.template(curl);
            if(params != null && params.isJsonObject()) {
                params.getAsJsonObject().entrySet().forEach(p -> {
                    builder.parameter(p.getKey(), p.getValue().getAsString());
                });
            }
            if(evaluator != null) {
                builder.resultEvaluator(evaluator.getAsString());
            }
            if(renders != null) {
                renders.getAsJsonObject().entrySet().forEach(i -> {
                    builder.renderer(i.getKey(), i.getValue().getAsString());
                });
            }
            list.add(builder.build());

        });

        return list.toArray(new ServiceInvocation[list.size()]);
    }

    public static ServiceInvocationBuilder builder(String name) {
        return new ServiceInvocationBuilder(name);
    }

    public static class ServiceInvocationBuilder {
        private String name;
        private CurlTemplate.CurlTemplateBuilder templateBuilder;
        private String evaluator;
        private Map<String, String> renderers = new LinkedHashMap<>();

        private ServiceInvocationBuilder(String name) {
            this.name = name;
        }

        public ServiceInvocationBuilder template(String template) throws InvalidFormatException {
            this.templateBuilder = CurlTemplate.builder(template);
            return this;
        }

        public ServiceInvocationBuilder parameter(String name, String exp) throws InvalidFormatException {
            this.templateBuilder.parameter(name, exp);
            return this;
        }

        public ServiceInvocationBuilder resultEvaluator(String evaluator) {
            this.evaluator = evaluator;
            return this;
        }

        public ServiceInvocationBuilder renderer(String name, String exp) {
            renderers.put(name, exp);
            return this;
        }

        public ServiceInvocation build() {
            return new ServiceInvocation(name, templateBuilder.create(), evaluator, renderers);
        }
    }
}
