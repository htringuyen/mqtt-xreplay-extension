package org.iotwarehouse.extension.core.param;

import java.util.ArrayList;
import java.util.List;

public class PluralParam extends BaseExplicitParam {

    private final String[] values;

    private PluralParam(String name, String... values) {
        super(name);
        this.values = values;
    }

    public ValueSet getValueSet() {
        return ValueSet.of(values);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String paramName;
        private List<String> paramValues = new ArrayList<>();

        public Builder name(String name) {
            this.paramName = name;
            return this;
        }

        public Builder values(List<String> values) {
            this.paramValues = values;
            return this;
        }

        public Builder addValue(String value) {
            paramValues.add(value);
            return this;
        }

        public PluralParam build() {
            if (paramName == null) {
                throw new NullPointerException("Require param name non null");
            }
            return new PluralParam(paramName, paramValues.toArray(new String[0]));
        }

    }
}
