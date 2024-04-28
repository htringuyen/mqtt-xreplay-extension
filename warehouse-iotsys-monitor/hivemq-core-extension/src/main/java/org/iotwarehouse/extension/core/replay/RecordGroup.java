package org.iotwarehouse.extension.core.replay;

import org.iotwarehouse.extension.core.param.ExplicitParams;

import java.util.*;

public interface RecordGroup {

    MessageRecord getRecordOrCreateIfAbsentWith(ExplicitParams recordParams);

    Collection<Map.Entry<RecordKey, MessageRecord>> findRecordsWith(ExplicitParams recordParams);
}
