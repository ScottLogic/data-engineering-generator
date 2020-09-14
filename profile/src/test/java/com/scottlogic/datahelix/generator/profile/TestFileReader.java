/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scottlogic.datahelix.generator.profile;

import com.scottlogic.datahelix.generator.common.whitelist.DistributedList;
import com.scottlogic.datahelix.generator.common.whitelist.WeightedElement;
import com.scottlogic.datahelix.generator.profile.reader.FileReader;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestFileReader extends FileReader {
    private final boolean weighted;

    public TestFileReader() {
        super(null);
        weighted = false;
    }

    public TestFileReader(boolean weighted) {
        super(null);
        this.weighted = weighted;
    }

    @Override
    public DistributedList<Object> setFromFile(File file) {
        return weighted
            ? this.getDistributedListWithWeights()
            : DistributedList.uniform(Collections.singleton("test"));
    }
    @Override
    public DistributedList<String> listFromMapFile(File file, String key) {
        return DistributedList.uniform(Collections.singleton("test"));
    }

    private static DistributedList<Object> getDistributedListWithWeights() {
        List<Object> elements = Arrays.asList(
            new WeightedElement<>("test1", 20),
            new WeightedElement<>("test2", 80)
        );
        return DistributedList.weightedOrDefault(elements);
    }
}

