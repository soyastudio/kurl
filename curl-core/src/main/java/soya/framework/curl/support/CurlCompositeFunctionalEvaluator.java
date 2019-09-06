package soya.framework.curl.support;

import soya.framework.curl.Evaluator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurlCompositeFunctionalEvaluator implements Evaluator {
    private static CurlCompositeFunctionalEvaluator me;
    private static Map<String, Evaluator> evaluators = new HashMap<>();

    static {
        me = new CurlCompositeFunctionalEvaluator();
        for (Method method : CurlEvaluationFunctions.class.getDeclaredMethods()) {
            evaluators.put(method.getName(), new MethodFunctionalEvaluator(method));
        }
    }

    private CurlCompositeFunctionalEvaluator() {
    }

    public static Evaluator getInstance() {
        return me;
    }

    private Evaluator getEvaluator(String exp) {
        if (exp.contains("(") && exp.endsWith(")")) {
            String methodName = exp.substring(0, exp.indexOf('('));
            return evaluators.get(methodName);
        }

        return null;
    }

    @Override
    public String evaluate(String exp, String json) {
        return getEvaluator(exp).evaluate(exp, json);
    }

    static class MethodFunctionalEvaluator implements Evaluator {
        private Method method;

        private MethodFunctionalEvaluator(Method method) {
            this.method = method;
        }

        @Override
        public String evaluate(String exp, String json) {
            List<String> list = new ArrayList<>();
            String paramPart = exp.substring(exp.indexOf('('));
            paramPart = paramPart.substring(1, paramPart.length() - 1);

            String[] params = paramPart.split(",");
            for (String param : params) {
                String pm = param.trim();
                if (pm.startsWith("\"") && pm.endsWith("\"") || pm.startsWith("'") && pm.endsWith("'")) {
                    pm = pm.substring(1, pm.length() - 1);
                }

                list.add(pm);
            }

            if (method.getParameterCount() > list.size()) {
                list.add(json);
            }

            try {
                return (String) method.invoke(null, list.toArray(new String[list.size()]));

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
