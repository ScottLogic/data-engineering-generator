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

package com.scottlogic.datahelix.generator.core.builders;

import com.scottlogic.datahelix.generator.common.SetUtils;
import com.scottlogic.datahelix.generator.common.profile.Field;
import com.scottlogic.datahelix.generator.common.profile.InSetRecord;
import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.InSetConstraint;
import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.IsNullConstraint;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestAtomicConstraintBuilder {
    private TestConstraintNodeBuilder testConstraintNodeBuilder;
    private Field field;

    protected TestAtomicConstraintBuilder(TestConstraintNodeBuilder testConstraintNodeBuilder, Field field) {
        this.testConstraintNodeBuilder = testConstraintNodeBuilder;
        this.field = field;
    }

    public static List<InSetRecord> inSetRecordsFromList(List<Object> values) {
        return inSetRecordsFromSet(SetUtils.setOf(values));
    }

    public static List<InSetRecord> inSetRecordsFrom(Object... values) {
        return inSetRecordsFromSet(SetUtils.setOf(values));
    }

    private static List<InSetRecord> inSetRecordsFromSet(Set<Object> values) {
        return values.stream().map(InSetRecord::new).collect(Collectors.toList());
    }

    public TestConstraintNodeBuilder isInSet(Object... legalValues) {
        InSetConstraint inSetConstraint = new InSetConstraint(
            field,
            inSetRecordsFrom(legalValues));
        testConstraintNodeBuilder.constraints.add(inSetConstraint);
        return testConstraintNodeBuilder;
    }

    public TestConstraintNodeBuilder isInSet(InSetRecord... weightedValues) {
        InSetConstraint inSetConstraint = new InSetConstraint(
            field,
            Stream.of(weightedValues).collect(Collectors.toList()));
        testConstraintNodeBuilder.constraints.add(inSetConstraint);
        return testConstraintNodeBuilder;
    }

    public TestConstraintNodeBuilder isNotInSet(Object... legalValues) {
        AtomicConstraint isInSetConstraint = new InSetConstraint(
            field,
            inSetRecordsFrom(legalValues)
        ).negate();
        testConstraintNodeBuilder.constraints.add(isInSetConstraint);
        return testConstraintNodeBuilder;
    }

    public TestConstraintNodeBuilder isNull() {
        IsNullConstraint isNullConstraint = new IsNullConstraint(field);
        testConstraintNodeBuilder.constraints.add(isNullConstraint);
        return testConstraintNodeBuilder;
    }

    public TestConstraintNodeBuilder isNotNull() {
        AtomicConstraint isNotNullConstraint = new IsNullConstraint(field).negate();
        testConstraintNodeBuilder.constraints.add(isNotNullConstraint);
        return testConstraintNodeBuilder;
    }

    public TestConstraintNodeBuilder isSelfContradictory() {
        IsNullConstraint isNullConstraint = new IsNullConstraint(field);
        AtomicConstraint isNotNullConstraint = new IsNullConstraint(field).negate();
        testConstraintNodeBuilder.constraints.add(isNullConstraint);
        testConstraintNodeBuilder.constraints.add(isNotNullConstraint);
        return testConstraintNodeBuilder;
    }
}
