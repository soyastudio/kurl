package soya.framework.curl.support;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.yaml.snakeyaml.Yaml;
import soya.framework.curl.ServiceMetadata;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class DefaultServiceMetadata implements ServiceMetadata {
    private static final String SWAGGER_CURL_METADATA = "x-curl-metadata";
    private static final String SWAGGER_CURL_PAYLOAD = "x-curl-payload";
    private static final String SWAGGER_CURL_EVALUATION = "x-curl-evaluation";
    private static final String SWAGGER_CURL_RENDERER = "x-curl-renderer";

    private String id;
    private String schema = "http";
    private Map<String, String> metadata = new LinkedHashMap<>();
    private List<String> options = new ArrayList<>();
    private Map<String, OperationModel> services = new HashMap<>();

    private DefaultServiceMetadata() {
    }

    private DefaultServiceMetadata(String id, String schema) {
        this.id = id;
        this.schema = schema;
    }

    public String getId() {
        return id;
    }

    public String getSchema() {
        return schema;
    }

    public List<String> getOptions() {
        return options;
    }

    protected void setMetadata(String key, String value) {
        metadata.put(key, value);
    }

    public static DefaultServiceMetadata fromInputStream(String id, String schema, InputStream is, Properties configProperties) {
        DefaultServiceMetadata model = new DefaultServiceMetadata(id, schema);
        Yaml yaml = new Yaml();
        Map<String, Object> configuration = (Map<String, Object>) yaml.load(is);

        Map<String, Object> metaMap = (Map<String, Object>) configuration.get("metadata");
        metaMap.entrySet().forEach(e -> {
            String propValue = configProperties.getProperty(e.getKey());
            if (propValue != null) {
                model.metadata.put(e.getKey(), propValue);
            } else {
                model.metadata.put(e.getKey(), e.getValue().toString());

            }
        });

        List<String> options = (List<String>) configuration.get("options");
        if (options != null) {
            options.forEach(opt -> {
                String op = StrSubstitutor.replace(opt, model.metadata);
                model.options.add(op);
            });
        }

        Map<String, Object> services = (Map<String, Object>) configuration.get("services");
        loadServices(services, model);

        List<String> includes = (List<String>) configuration.get("includes");
        if (includes != null) {
            includes.forEach(i -> {
                String path = StrSubstitutor.replace(i, model.metadata);
                if (path.endsWith("/")) {
                    URL url = getClassLoader().getResource(path);
                    File[] files = new File(url.getPath()).listFiles();
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".yaml")) {
                            String sp = path + file.getName();
                            loadServices(sp, model);
                        }
                    }

                } else {
                    loadServices(path, model);
                }
            });
        }

        return model;
    }

    private static void loadServices(String path, DefaultServiceMetadata model) {
        Map<String, Object> serviceMap = (Map<String, Object>) new Yaml().load(getClassLoader().getResourceAsStream(path));
        loadServices(serviceMap, model);
    }

    private static void loadServices(Map<String, Object> services, DefaultServiceMetadata model) {
        if (services != null) {
            services.entrySet().forEach(entry -> {
                String serviceName = entry.getKey();
                Map<String, Object> value = (Map<String, Object>) entry.getValue();
                model.services.put(serviceName, createOperationModel(value, model.metadata));
            });
        }
    }

    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = DefaultServiceMetadata.class.getClassLoader();
        }

        return classLoader;
    }

    private static OperationModel createOperationModel(Map<String, Object> value, Map<String, String> metadata) {
        OperationModel model = new OperationModel();
        String curl = (String) value.get("curl");
        if (curl != null) {
            model.curl = StrSubstitutor.replace(curl, metadata);
        }

        Map<String, String> params = (Map<String, String>) value.get("params");
        if (params != null) {
            params.entrySet().forEach(e -> {
                String k = e.getKey();
                String v = StrSubstitutor.replace(e.getValue(), metadata);
                model.getParams().put(k, v);
            });
        }

        Map<String, String> renderers = (Map<String, String>) value.get("renderers");
        if (renderers != null) {
            renderers.entrySet().forEach(e -> {
                String k = e.getKey();
                String v = StrSubstitutor.replace(e.getValue(), metadata);
                model.getRenderers().put(k, v);
            });
        }

        Map<String, String> examples = (Map<String, String>) value.get("examples");
        if (examples != null) {
            examples.entrySet().forEach(e -> {
                String k = e.getKey();
                String v = StrSubstitutor.replace(e.getValue(), metadata);

                model.getExamples().put(k, v);
            });
        }

        return model;
    }


    private static <T> T fromMap(Map<String, Object> map, Class<T> type, Map<String, String> metadata) {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        json = StrSubstitutor.replace(json, metadata);
        return gson.fromJson(json, type);
    }

    @Override
    public Map<String, String> settings() {
        return ImmutableMap.copyOf(metadata);
    }

    @Override
    public String[] getServiceEndpoints() {
        return new ArrayList<String>(services.keySet()).toArray(new String[services.size()]);
    }

    @Override
    public OperationModel getOperationInfo(String service) {
        return services.get(service);
    }

    public static class OptionModel {
        private String type;
        private String value;

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }

    public static class OperationModel {
        private String curl;
        private Map<String, String> params;
        private Map<String, String> renderers = new LinkedHashMap<>();
        private Map<String, String> examples = new LinkedHashMap<>();

        public String getCurl() {
            return curl;
        }

        public Map<String, String> getParams() {
            if (params == null) {
                params = new LinkedHashMap<>();
            }
            return params;
        }

        public Map<String, String> getRenderers() {
            return renderers;
        }

        public Map<String, String> getExamples() {
            return examples;
        }
    }

    public static class ParameterModel {

        private final String name;
        private final String type;

        private String dataType;
        private String dataFormat;
        private String pattern;
        private String collectionType;
        private Boolean required;
        private String description;

        private String value;

        public ParameterModel(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }


        public String getType() {
            return type;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getDataFormat() {
            return dataFormat;
        }

        public void setDataFormat(String dataFormat) {
            this.dataFormat = dataFormat;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getCollectionType() {
            return collectionType;
        }

        public void setCollectionType(String collectionType) {
            this.collectionType = collectionType;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
