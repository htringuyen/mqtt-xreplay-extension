package org.iotwarehouse.extension.core.param;

import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultVagueParams implements VagueParams {

    public static final String DEFAULT_INFINITE_MARK = "ALL";

    public static final String DEFAULT_NOT_APPLICABLE_MARK = "N/A";

    private final String infiniteMark;

    private final String notApplicableMark;

    private final Map<String, List<String>> paramMap;

    public static Builder builder() {
        return new Builder();
    }

    private DefaultVagueParams(@NonNull String infiniteMark, @NonNull String notApplicableMark,
                               @NonNull Map<String, List<String>> paramMap) {
        this.infiniteMark = infiniteMark;
        this.notApplicableMark = notApplicableMark;
        this.paramMap = paramMap;
    }

    public ExplicitParams toExplicitParams() {
        var params = paramMap.entrySet()
                .stream()
                .map(e -> createExplicitParamFrom(e.getKey(), e.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return ExplicitParams.from(params);
    }

    protected ExplicitParam createExplicitParamFrom(String name, List<String> values) {
        var containsInfiniteMark = values.contains(infiniteMark);
        var containsNotApplicableMark = values.contains(notApplicableMark);

        if (containsInfiniteMark) {
            return InfiniteParam.builder()
                    .name(name).build();
        } else if (containsNotApplicableMark) {
            return null;
        } else if (values.size() == 1) {
            return SingularParam.builder()
                    .name(name)
                    .value(values.get(0))
                    .build();
        } else if (values.size() > 1) {
            var builder = PluralParam.builder()
                    .name(name);
            values.forEach(builder::addValue);
            return builder.build();
        }
        return null;
    }

    public static class Builder {

        private String infiniteMark;
        private String notApplicableMark;
        private final Map<String, List<String>> paramMap = new HashMap<>();

        private Builder() {

        }

        public Builder add(String name, String value) {
            if (!paramMap.containsKey(name)) {
                paramMap.put(name, new ArrayList<>());
            }
            paramMap.get(name).add(value);
            return this;
        }

        public Builder infiniteMark(String infiniteMark) {
            this.infiniteMark = infiniteMark;
            return this;
        }

        public Builder notApplicableMark(String notApplicableMark) {
            this.notApplicableMark = notApplicableMark;
            return this;
        }

        public VagueParams build() {
            return new DefaultVagueParams(
                    infiniteMark == null ? DEFAULT_INFINITE_MARK : infiniteMark,
                    notApplicableMark == null ? DEFAULT_NOT_APPLICABLE_MARK : notApplicableMark,
                    paramMap);
        }
    }

}






























