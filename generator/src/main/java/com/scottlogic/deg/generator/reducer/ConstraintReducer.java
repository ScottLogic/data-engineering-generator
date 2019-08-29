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

package com.scottlogic.deg.generator.reducer;

import com.google.inject.Inject;
import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.ProfileFields;
import com.scottlogic.deg.common.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.deg.common.profile.constraints.delayed.DelayedAtomicConstraint;
import com.scottlogic.deg.generator.decisiontree.ConstraintNode;
import com.scottlogic.deg.generator.fieldspecs.*;
import com.scottlogic.deg.generator.fieldspecs.relations.FieldSpecRelations;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ConstraintReducer {
    private final FieldSpecFactory fieldSpecFactory;
    private final FieldSpecMerger fieldSpecMerger;
    private final FieldRelationsFactory fieldRelationsFactory;

    @Inject
    public ConstraintReducer(
        FieldSpecFactory fieldSpecFactory,
        FieldSpecMerger fieldSpecMerger
    ) {
        this.fieldSpecFactory = fieldSpecFactory;
        this.fieldSpecMerger = fieldSpecMerger;
        fieldRelationsFactory = new FieldRelationsFactory();
    }

    public Optional<RowSpec> reduceConstraintsToRowSpec(ProfileFields fields, ConstraintNode node) {
        Collection<AtomicConstraint> constraints = node.getAtomicConstraints();
        Collection<DelayedAtomicConstraint> delayedConstraints = node.getDelayedAtomicConstraints();

        final Map<Field, List<AtomicConstraint>> fieldToConstraints = constraints.stream()
            .collect(
                Collectors.groupingBy(
                    AtomicConstraint::getField,
                    Collectors.mapping(Function.identity(),
                        Collectors.toList())));

        final Map<Field, Optional<FieldSpec>> fieldToFieldSpec = fields.stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    field -> reduceConstraintsToFieldSpec(fieldToConstraints.get(field))));

        final Optional<Map<Field, FieldSpec>> optionalMap = Optional.of(fieldToFieldSpec)
            .filter(map -> map.values().stream().allMatch(Optional::isPresent))
            .map(map -> map
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get())));

        final List<FieldSpecRelations> relations = delayedConstraints.stream()
            .map(fieldRelationsFactory::construct)
            .collect(Collectors.toList());

        return optionalMap.map(
            map -> new RowSpec(
                fields,
                map,
                relations));
    }

    public Optional<FieldSpec> reduceConstraintsToFieldSpec(Iterable<AtomicConstraint> constraints) {
        return constraints == null
            ? Optional.of(FieldSpec.Empty)
            : getRootFieldSpec(constraints);
    }

    private Optional<FieldSpec> getRootFieldSpec(Iterable<AtomicConstraint> rootConstraints) {
        final Stream<FieldSpec> rootConstraintsStream =
            StreamSupport
                .stream(rootConstraints.spliterator(), false)
                .map(fieldSpecFactory::construct);

        return rootConstraintsStream
            .map(Optional::of)
            .reduce(
                Optional.of(FieldSpec.Empty),
                (optSpec1, optSpec2) -> optSpec1.flatMap(
                    spec1 -> optSpec2.flatMap(
                        spec2 -> fieldSpecMerger.merge(spec1, spec2))));
    }
}
