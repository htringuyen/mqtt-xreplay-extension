package org.iotwarehouse.extension.core.replay;

import org.iotwarehouse.extension.core.param.ExplicitParams;

import java.util.*;
import java.util.stream.Collectors;

public class RecordGroupImpl implements RecordGroup {

    private final NavigableMap<RecordKey, MessageRecord> keyRecordMap =
            Collections.synchronizedNavigableMap(new TreeMap<RecordKey, MessageRecord>());

    private final String[] paramNames;

    private final MessageRecordFactory recordFactory;


    public RecordGroupImpl(MessageRecordFactory recordFactory, String... paramNames) {
        this.paramNames = paramNames;
        this.recordFactory = recordFactory;
    }

    @Override
    public MessageRecord getRecordOrCreateIfAbsentWith(ExplicitParams params) {
        return keyRecordMap.computeIfAbsent(
                helpCreateRecordKeyFrom(params), key -> recordFactory.getInstance());
    }

    @Override
    public List<Map.Entry<RecordKey, MessageRecord>> findRecordsWith(ExplicitParams params) {

        var infIndexes = new LinkedList<Integer>();
        var rangeUpperSegments = new String[paramNames.length];
        var rangeLowerSegments = new String[paramNames.length];

        for (int i = 0; i < paramNames.length; i++) {

            var paramName = paramNames[i];
            var paramOpt = params.get(paramName);
            if (paramOpt.isEmpty()) {
                throw new IllegalArgumentException("Required param [" + paramName + "] absent.");
            }

            var valueSet = paramOpt.get().getValueSet();

            if (valueSet.isInfinite()) {
                infIndexes.add(i);
                rangeUpperSegments[i] = RecordKey.HIGHEST_SEGMENT_VALUE;
                rangeLowerSegments[i] = RecordKey.LOWEST_SEGMENT_VALUE;

            } else {

                if (valueSet.size() != 1) {
                    throw new IllegalArgumentException("Does not support plural or empty value params when creating record key");
                }

                rangeUpperSegments[i] = valueSet.firstValue();
                rangeLowerSegments[i] = valueSet.firstValue();
            }


        }

        if (!infIndexes.isEmpty()) {
            var subMap = keyRecordMap.subMap(new RecordKey(rangeLowerSegments) , new RecordKey(rangeUpperSegments));

            var toCheckIndexes = new LinkedList<Integer>();
            for (int i = infIndexes.getFirst(); i < paramNames.length; i++) {
                if (!infIndexes.contains(i)) {
                    toCheckIndexes.add(i);
                }
            }

            if (toCheckIndexes.isEmpty()) {
                return new ArrayList<>(subMap.entrySet());
            } else {
                return subMap.entrySet()
                        .stream()
                        .filter(e -> {
                            var recordKey = e.getKey();
                            for (var index: toCheckIndexes) {
                                if (!recordKey.getSegment(index).equals(rangeUpperSegments[index])) {
                                    return false;
                                }
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
            }

        } else {

            var tempKey = new RecordKey(rangeLowerSegments);
            var record = keyRecordMap.get(tempKey);
            if (record == null) {
                //throw new IllegalStateException();
                return new ArrayList<>();
            }

            var key = keyRecordMap.ceilingKey(tempKey);
            var result = new LinkedList<Map.Entry<RecordKey, MessageRecord>>();
            result.add(Map.entry(key, record));
            return result;
        }
    }

    private RecordKey helpCreateRecordKeyFrom(ExplicitParams params) {
        var segmentValues = new String[paramNames.length];
        for (int i = 0; i < paramNames.length; i++) {
            var paramName = paramNames[i];
            var paramOpt = params.get(paramNames[i]);
            if (paramOpt.isEmpty()) {
                throw new IllegalArgumentException("Required param [" + paramName + "] absent.");
            }
            var valueSet = paramOpt.get().getValueSet();
            if (valueSet.isInfinite() || valueSet.values().size() != 1) {
                throw new IllegalArgumentException("Does not support infinite, plural or empty value set when creating record key");
            }
            segmentValues[i] = valueSet.firstValue();
        }
        return new RecordKey(segmentValues);
    }
}
