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

package com.scottlogic.deg.generator.generation.fieldvaluesources.datetime;

import com.scottlogic.deg.generator.generation.fieldvaluesources.FieldValueSource;
import com.scottlogic.deg.generator.generation.fieldvaluesources.LinearIterator;
import com.scottlogic.deg.generator.restrictions.linear.LinearRestrictions;
import com.scottlogic.deg.generator.utils.RandomNumberGenerator;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.scottlogic.deg.generator.utils.SetUtils.stream;

public class DateTimeFieldValueSource implements FieldValueSource<OffsetDateTime> {

    private final LinearRestrictions<OffsetDateTime> restrictions;
    private final Set<Object> blacklist;

    private final RandomDateGenerator randomDateGenerator;

    public DateTimeFieldValueSource(
        LinearRestrictions<OffsetDateTime> restrictions,
        Set<Object> blacklist) {
        this.restrictions = restrictions;
        this.blacklist = blacklist;

        this.randomDateGenerator = new RandomDateGenerator(restrictions);
    }

    @Override
    public Stream<OffsetDateTime> generateAllValues() {
        return stream(new LinearIterator<>(restrictions))
            .filter(i -> !blacklist.contains(i));
    }

    @Override
    public Stream<OffsetDateTime> generateInterestingValues() {
        return Stream.of(restrictions.getMin(), restrictions.getMax())
            .distinct()
            .filter(i -> !blacklist.contains(i));
    }

    @Override
    public Stream<OffsetDateTime> generateRandomValues(RandomNumberGenerator randomNumberGenerator) {
        return Stream.generate(() -> randomDateGenerator.next(randomNumberGenerator))
            .filter(i -> !blacklist.contains(i));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DateTimeFieldValueSource otherSource = (DateTimeFieldValueSource) obj;
        return restrictions.equals(otherSource.restrictions) &&
            blacklist.equals(otherSource.blacklist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restrictions, blacklist);
    }
}
