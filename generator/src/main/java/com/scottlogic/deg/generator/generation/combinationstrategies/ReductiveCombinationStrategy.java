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

package com.scottlogic.deg.generator.generation.combinationstrategies;

import com.scottlogic.deg.generator.generation.databags.DataBag;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scottlogic.deg.common.util.FlatMappingSpliterator.flatMap;

public class ReductiveCombinationStrategy implements CombinationStrategy {
    
    @Override
    public Stream<DataBag> permute(Stream<Supplier<Stream<DataBag>>> dataBagSequences) {
        List<Supplier<Stream<DataBag>>> bagsAsLists = dataBagSequences.collect(Collectors.toList());

        return next(DataBag.empty, bagsAsLists, 0);
    }

    public Stream<DataBag> next(DataBag accumulatingBag, List<Supplier<Stream<DataBag>>> bagSequences, int bagSequenceIndex) {
        if (bagSequenceIndex < bagSequences.size()) {
            Stream<DataBag> nextStream = bagSequences.get(bagSequenceIndex).get();

            return flatMap(
                nextStream.map(innerBag -> DataBag.merge(innerBag, accumulatingBag)),
                innerBag -> next(innerBag, bagSequences, bagSequenceIndex + 1));
        }
        else
            return Stream.of(accumulatingBag);
    }
}
