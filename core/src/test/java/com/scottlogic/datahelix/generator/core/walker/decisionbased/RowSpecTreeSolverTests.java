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
package com.scottlogic.datahelix.generator.core.walker.decisionbased;

import com.scottlogic.datahelix.generator.common.distribution.DistributedList;
import com.scottlogic.datahelix.generator.common.distribution.WeightedElement;
import com.scottlogic.datahelix.generator.common.profile.Field;
import com.scottlogic.datahelix.generator.common.profile.Fields;
import com.scottlogic.datahelix.generator.common.profile.InSetRecord;
import com.scottlogic.datahelix.generator.common.profile.ProfileFields;
import com.scottlogic.datahelix.generator.core.builders.TestConstraintNodeBuilder;
import com.scottlogic.datahelix.generator.core.decisiontree.ConstraintNode;
import com.scottlogic.datahelix.generator.core.decisiontree.DecisionTree;
import com.scottlogic.datahelix.generator.core.fieldspecs.*;
import com.scottlogic.datahelix.generator.core.reducer.ConstraintReducer;
import com.scottlogic.datahelix.generator.core.walker.pruner.TreePruner;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scottlogic.datahelix.generator.common.profile.FieldBuilder.createField;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.core.Is.is;

class RowSpecTreeSolverTests {
    private Field fieldA = createField("A");
    private Field fieldB = createField("B");
    private Fields fields = new ProfileFields(Arrays.asList(fieldA, fieldB));
    private FieldSpecMerger fieldSpecMerger = new FieldSpecMerger();
    private ConstraintReducer constraintReducer = new ConstraintReducer(fieldSpecMerger);
    private TreePruner pruner = new TreePruner(fieldSpecMerger, constraintReducer, new FieldSpecHelper());
    private OptionPicker optionPicker = new SequentialOptionPicker();
    private RowSpecTreeSolver rowSpecTreeSolver = new RowSpecTreeSolver(constraintReducer, pruner, optionPicker);

    @Test
    void createRowSpecs_whenRootNodeHasNoDecisions_returnsRowSpecOfRoot() {
        //Arrange
        ConstraintNode root = TestConstraintNodeBuilder.constraintNode().build();
        DecisionTree tree = new DecisionTree(root, fields);

        //Act
        Stream<WeightedElement<RowSpec>> rowSpecs = rowSpecTreeSolver.createRowSpecs(tree);

        //Assert
        List<RowSpec> expectedRowSpecs = new ArrayList<>();
        expectedRowSpecs.add(createRowSpec(
            FieldSpecFactory.fromType(fieldA.getType()),
            FieldSpecFactory.fromType(fieldB.getType())));

        assertThat(
            expectedRowSpecs,
            sameBeanAs(rowSpecs.map(WeightedElement::element).collect(Collectors.toList())));
    }

    @Test
    void createRowSpecs_whenRootNodeHasNoDecisionsButSomeConstraints_returnsRowSpecOfRoot() {
        //Arrange
        ConstraintNode root = TestConstraintNodeBuilder.constraintNode().where(fieldA).isInSet("1", "2", "3").build();
        DecisionTree tree = new DecisionTree(root, fields);

        //Act
        Stream<WeightedElement<RowSpec>> rowSpecs = rowSpecTreeSolver.createRowSpecs(tree);

        //Assert
        List<RowSpec> expectedRowSpecs = new ArrayList<>();
        expectedRowSpecs.add(createRowSpec(
            FieldSpecFactory.fromLegalValuesList(Arrays.asList("1", "2", "3")),
            FieldSpecFactory.fromType(fieldB.getType())));

        assertThat(
            rowSpecs.map(WeightedElement::element).collect(Collectors.toList()),
            sameBeanAs(expectedRowSpecs));
    }

    @Test
    void createRowSpecs_whenRootNodeHasSomeDecisions_returnsRowSpecOfRoot() {
        //Arrange
        ConstraintNode root = TestConstraintNodeBuilder.constraintNode()
            .withDecision(
                TestConstraintNodeBuilder.constraintNode()
                    .where(fieldB).isNull(),
                TestConstraintNodeBuilder.constraintNode()
                    .where(fieldB).isInSet("1", "2", "3"))
            .build();
        DecisionTree tree = new DecisionTree(root, fields);

        //Act
        Set<WeightedElement<RowSpec>> rowSpecs = rowSpecTreeSolver.createRowSpecs(tree).collect(Collectors.toSet());

        //Assert
        Set<RowSpec> expectedRowSpecs = new HashSet<>();
        expectedRowSpecs.add(createRowSpec(
            FieldSpecFactory.fromType(fieldA.getType()),
            FieldSpecFactory.nullOnly()));
        expectedRowSpecs.add(createRowSpec(
            FieldSpecFactory.fromType(fieldA.getType()),
            FieldSpecFactory.fromLegalValuesList(Arrays.asList("1","2","3"))));

        assertThat(
            rowSpecs.stream().map(WeightedElement::element).collect(Collectors.toSet()),
            sameBeanAs(expectedRowSpecs));
    }

    @Test
    void createRowSpecs_whenRootNodeHasSomeWeightedDecisions_returnsCorrectlyWeightedRowSpecs() {
        //Arrange
        ConstraintNode root = TestConstraintNodeBuilder.constraintNode()
            .where(fieldB).isInSet(
                new InSetRecord("1", 0.25d),
                new InSetRecord("2", 0.75d))
            .withDecision(
                TestConstraintNodeBuilder.constraintNode()
                    .where(fieldB).isInSet("1"),
                TestConstraintNodeBuilder.constraintNode()
                    .where(fieldB).isInSet("2"))
            .build();
        DecisionTree tree = new DecisionTree(root, fields);

        //Act
        Set<WeightedElement<RowSpec>> rowSpecs = rowSpecTreeSolver.createRowSpecs(tree).collect(Collectors.toSet());

        //Assert
        Set<WeightedElement<RowSpec>> expectedRowSpecs = new HashSet<>();
        expectedRowSpecs.add(new WeightedElement<>(
            createRowSpec(
                FieldSpecFactory.fromType(fieldA.getType()),
                FieldSpecFactory.fromList(distributedListOfOneItem("1", 0.25))),
            0.25));
        expectedRowSpecs.add(new WeightedElement<>(
            createRowSpec(
                FieldSpecFactory.fromType(fieldA.getType()),
                FieldSpecFactory.fromList(distributedListOfOneItem("2", 0.75))),
            0.75));

        assertThat(
            rowSpecs,
            sameBeanAs(expectedRowSpecs));
    }

    @Test
    void createRowSpecs_whenRootNodeHasSomeWeightedDecisionsAndAllValuesAreExcluded_returnsCorrectlyWeightedRowSpecs() {
        //Arrange
        ConstraintNode root = TestConstraintNodeBuilder.constraintNode()
            .where(fieldB).isInSet(
                new InSetRecord("1", 0.25d),
                new InSetRecord("2", 0.75d))
            .withDecision(
                TestConstraintNodeBuilder.constraintNode()
                    .where(fieldB).isInSet("3"),
                TestConstraintNodeBuilder.constraintNode()
                    .where(fieldB).isInSet("4"))
            .build();
        DecisionTree tree = new DecisionTree(root, fields);

        //Act
        Set<WeightedElement<RowSpec>> rowSpecs = rowSpecTreeSolver.createRowSpecs(tree).collect(Collectors.toSet());

        //Assert
        assertThat(
            rowSpecs.isEmpty(),
            is(true));
    }

    @Test
    void createRowSpecs_whenRootNodeHasUnweightedDecisionsAndAllValuesAreAllowed_returnsCorrectlyWeightedRowSpecs() {
        //Arrange
        ConstraintNode root = TestConstraintNodeBuilder.constraintNode()
            .where(fieldB).isInSet("1", "2")
            .withDecision(
                TestConstraintNodeBuilder.constraintNode()
                    .where(fieldB).isInSet("1", "2", "3"),
                TestConstraintNodeBuilder.constraintNode()
                    .where(fieldB).isInSet("1", "2", "4"))
            .build();
        DecisionTree tree = new DecisionTree(root, fields);

        //Act
        Set<WeightedElement<RowSpec>> rowSpecs = rowSpecTreeSolver.createRowSpecs(tree).collect(Collectors.toSet());

        //Assert
        Set<WeightedElement<RowSpec>> expectedRowSpecs = new HashSet<>();
        expectedRowSpecs.add(
            new WeightedElement<>(
                createRowSpec(
                    FieldSpecFactory.fromType(fieldA.getType()),
                    FieldSpecFactory.fromLegalValuesList(Arrays.asList("1", "2"))),
                1));
        expectedRowSpecs.add(new WeightedElement<>(
            createRowSpec(
                FieldSpecFactory.fromType(fieldA.getType()),
                FieldSpecFactory.fromLegalValuesList(Arrays.asList("1", "2"))),
            1));

        assertThat(
            rowSpecs,
            sameBeanAs(expectedRowSpecs));
    }

    @Test
    void createRowSpecs_whenRootNodeHasNoWeightedDecisions_returnsCorrectlyWeightedRowSpecs() {
        //Arrange
        ConstraintNode root = TestConstraintNodeBuilder.constraintNode()
            .where(fieldB).isNotNull()
            .withDecision(
                TestConstraintNodeBuilder.constraintNode()
                    .where(fieldB).isInSet("1"),
                TestConstraintNodeBuilder.constraintNode()
                    .where(fieldB).isInSet("2"))
            .build();
        DecisionTree tree = new DecisionTree(root, fields);

        //Act
        Set<WeightedElement<RowSpec>> rowSpecs = rowSpecTreeSolver.createRowSpecs(tree).collect(Collectors.toSet());

        //Assert
        Set<WeightedElement<RowSpec>> expectedRowSpecs = new HashSet<>();
        expectedRowSpecs.add(new WeightedElement<>(
            createRowSpec(
                FieldSpecFactory.fromType(fieldA.getType()),
                FieldSpecFactory.fromLegalValuesList(Collections.singletonList("1")).withNotNull()),
            1));
        expectedRowSpecs.add(new WeightedElement<>(
            createRowSpec(
                FieldSpecFactory.fromType(fieldA.getType()),
                FieldSpecFactory.fromLegalValuesList(Collections.singletonList("2")).withNotNull()),
            1));

        assertThat(
            rowSpecs,
            sameBeanAs(expectedRowSpecs));
    }

    private RowSpec createRowSpec(FieldSpec fieldSpecA, FieldSpec fieldSpecB){
        Map<Field, FieldSpec> option = new HashMap<>();
        option.put(fieldA, fieldSpecA);
        option.put(fieldB, fieldSpecB);
        return new RowSpec(fields, option, Collections.emptyList());
    }

    private static <T> DistributedList<T> distributedListOfOneItem(T item, double weight) {
        ArrayList<WeightedElement<T>> list = new ArrayList<>();
        list.add(new WeightedElement<>(item, weight));

        return new DistributedList<>(list);
    }
}
