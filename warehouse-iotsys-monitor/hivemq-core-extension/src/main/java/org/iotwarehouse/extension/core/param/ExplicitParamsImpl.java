package org.iotwarehouse.extension.core.param;

import java.util.*;

public class ExplicitParamsImpl implements ExplicitParams {

    private final Map<String, ExplicitParam> paramMap;

    ExplicitParamsImpl(Map<String, ExplicitParam> paramMap) {
        this.paramMap = paramMap;
    }

    ExplicitParamsImpl(ExplicitParam... params) {
        paramMap = new HashMap<>();
        for (var param: params) {
            paramMap.put(param.getName(), param);
        }
    }

    ExplicitParamsImpl(Collection<ExplicitParam> params) {
        paramMap = new HashMap<>();
        for (var param: params) {
            paramMap.put(param.getName(), param);
        }
    }

    @Override
    public boolean contains(String paramName) {
        return paramMap.containsKey(paramName);
    }

    @Override
    public Optional<ExplicitParam> get(String name) {
        if (paramMap.containsKey(name)) {
            return Optional.of(paramMap.get(name));
        }
        return Optional.empty();
    }

    @Override
    public boolean isSubsetOf(ExplicitParams otherParams) {
        return paramMap.entrySet()
                .stream()
                .allMatch(e -> {
                    var name = e.getKey();
                    var param = e.getValue();
                    var otherParam = otherParams.get(name).orElse(null);
                    if (otherParam == null) {
                        return false;
                    }
                    return otherParam.includeValuesFrom(param);
                });
    }

    @Override
    public String toString() {
        return paramMap.values().toString();
    }
}







































