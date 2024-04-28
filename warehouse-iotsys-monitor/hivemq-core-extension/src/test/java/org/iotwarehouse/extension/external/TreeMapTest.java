package org.iotwarehouse.extension.external;

import org.junit.jupiter.api.Test;

import java.util.TreeMap;

public class TreeMapTest {

    @Test
    void testTreeMapSubMap() {
        var treeMap = new TreeMap<Integer, String>();
        // import 10 values
        treeMap.put(1, "one");
        treeMap.put(3, "three");
        treeMap.put(5, "five");
        treeMap.put(7, "seven");
        treeMap.put(9, "nine");

        // get submap from 3 to 7
        treeMap.subMap(2, 8).forEach((k, v) -> System.out.println(k + " -> " + v));

    }
}
