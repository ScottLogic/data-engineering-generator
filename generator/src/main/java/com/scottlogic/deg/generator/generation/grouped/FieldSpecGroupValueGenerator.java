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


package com.scottlogic.deg.generator.generation.grouped;

import com.google.inject.Inject;
import com.scottlogic.deg.common.profile.fields.Field;
import com.scottlogic.deg.generator.config.detail.CombinationStrategyType;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.fieldspecs.FieldSpecGroup;
import com.scottlogic.deg.generator.fieldspecs.FieldSpecMerger;
import com.scottlogic.deg.generator.fieldspecs.relations.FieldSpecRelations;
import com.scottlogic.deg.generator.generation.FieldSpecValueGenerator;
import com.scottlogic.deg.generator.generation.databags.*;
import com.scottlogic.deg.generator.utils.SetUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scottlogic.deg.common.util.FlatMappingSpliterator.flatMap;

public class FieldSpecGroupValueGenerator {

    private final CombinationStrategyType combinationStrategy;
    private final FieldSpecValueGenerator underlyingGenerator;
    private final FieldSpecMerger fieldSpecMerger = new FieldSpecMerger();

    @Inject
    public FieldSpecGroupValueGenerator(FieldSpecValueGenerator underlyingGenerator, CombinationStrategyType combinationStrategy) {
        this.underlyingGenerator = underlyingGenerator;
        this.combinationStrategy = combinationStrategy;
    }

    public Stream<DataBag> generate(FieldSpecGroup group) {
        Field first = getFirst(group);

        if (group.fieldSpecs().size() == 1) {
            return underlyingGenerator.generate(first, group.fieldSpecs().get(first))
                .map(val -> toDataBag(first, val));
        }

        FieldSpec firstSpec = updateFirstSpecFromRelations(first, group);

        Stream<DataBag> firstDataBagValues = underlyingGenerator.generate(first, firstSpec)
            .map(value -> toDataBag(first, value));

        return flatMap(
            firstDataBagValues,
            dataBag -> generateRemainingData(first, dataBag, removeSpecFromGroup(first, group)));
    }

    private Field getFirst(FieldSpecGroup keySet) {
        Stream<FieldSpecRelations> relations = Stream.concat(
            keySet.relations().stream(),
            keySet.relations().stream().map(FieldSpecRelations::inverse)
        );
        List<Map.Entry<Field, Integer>> list = new ArrayList<>(relations
            .collect(Collectors.toMap(
                FieldSpecRelations::other,
                r -> 1, Integer::sum)).entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
        return list.isEmpty() ? SetUtils.firstIteratorElement(keySet.fieldSpecs().keySet()) : list.get(0).getKey();
    }

    private FieldSpec updateFirstSpecFromRelations(Field first, FieldSpecGroup group) {
        FieldSpec firstFieldSpec = group.fieldSpecs().get(first);

        return group.relations().stream()
            .filter(relation -> isRelatedToField(first, relation))
            .map(relation -> createModifierForField(first, relation, group))
            .map(Optional::of)
            .reduce(Optional.of(firstFieldSpec), this::mergeOptionalFieldspecs)
            .orElseThrow(() -> new IllegalStateException("Failed to merge field specs in related fields"));
    }

    private Optional<FieldSpec> mergeOptionalFieldspecs(Optional<FieldSpec> left, Optional<FieldSpec> right) {
        return left.flatMap(
            (leftFieldSpec) -> fieldSpecMerger.merge(leftFieldSpec, right.get()));
    }

    private boolean isRelatedToField(Field field, FieldSpecRelations relation) {
        return relation.main().equals(field) || relation.other().equals(field);
    }

    private FieldSpec createModifierForField(Field first,
                                             FieldSpecRelations relation,
                                             FieldSpecGroup group) {
        if (!relation.main().equals(first)){
            relation = relation.inverse();
        }

        FieldSpec otherFieldSpec = group.fieldSpecs().get(relation.other());
        return relation.createModifierFromOtherFieldSpec(otherFieldSpec);
    }

    private Stream<DataBag> generateRemainingData(Field generatedField, DataBag dataBag, FieldSpecGroup group) {
        FieldSpecGroup newGroup = updateRelatedFieldSpecs(generatedField, dataBag.getDataBagValue(generatedField), group);

        Stream<DataBag> dataBagStream = generate(newGroup)
            .map(otherData -> DataBag.merge(dataBag, otherData));

        return applyCombinationStrategy(dataBagStream);
    }

    private FieldSpecGroup updateRelatedFieldSpecs(Field generatedField, DataBagValue generatedValue, FieldSpecGroup group) {
        Set<FieldSpecRelations> nonUpdatedRelations = group.relations().stream()
            .filter(relation -> !isRelatedToField(generatedField, relation))
            .collect(Collectors.toSet());

        List<FieldSpecRelations> updatableRelations = group.relations().stream()
            .filter(relation -> isRelatedToField(generatedField, relation))
            .map(relations -> relations.other().equals(generatedField) ? relations : relations.inverse())
            .collect(Collectors.toList());

        Map<Field, FieldSpec> fieldUpdates = updatableRelations.stream()
            .collect(Collectors.toMap(
                FieldSpecRelations::main,
                relation -> relation.createModifierFromOtherValue(generatedValue),
                (l, r) -> fieldSpecMerger.merge(l, r)
                    .orElseThrow(() -> new IllegalStateException("Failed to merge field specs in related fields"))));

        Map<Field, FieldSpec> newFieldSpecs = group.fieldSpecs().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> updateSpec(e.getKey(), e.getValue(), fieldUpdates)));

        return new FieldSpecGroup(newFieldSpecs, nonUpdatedRelations);
    }

    private FieldSpec updateSpec(Field key, FieldSpec previous, Map<Field, FieldSpec> fieldUpdates){
        if (!fieldUpdates.containsKey(key)){
            return previous;
        }

        return fieldSpecMerger.merge(previous, fieldUpdates.get(key))
            .orElseThrow(() ->
                new IllegalStateException("Failed to merge field specs in related fields"));
    }

    private Stream<DataBag> applyCombinationStrategy(Stream<DataBag> dataBagStream) {
        switch (combinationStrategy) {
            case EXHAUSTIVE:
                return dataBagStream;
            case MINIMAL:
            case PINNING:
                return dataBagStream.limit(1);
            default:
                throw new UnsupportedOperationException("no combination strategy provided");
        }
    }

    private DataBag toDataBag(Field field, DataBagValue value) {
        Map<Field, DataBagValue> map = new HashMap<>();
        map.put(field, value);
        return new DataBag(map);
    }

    private FieldSpecGroup removeSpecFromGroup(Field first, FieldSpecGroup group) {
        HashMap<Field, FieldSpec> newFieldSpecs = new HashMap<>(group.fieldSpecs());
        newFieldSpecs.remove(first);
        return new FieldSpecGroup(newFieldSpecs, group.relations());
    }
}
