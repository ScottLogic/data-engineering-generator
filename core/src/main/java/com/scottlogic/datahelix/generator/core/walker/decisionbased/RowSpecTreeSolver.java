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

import com.google.inject.Inject;
import com.scottlogic.datahelix.generator.common.distribution.WeightedElement;
import com.scottlogic.datahelix.generator.common.profile.Field;
import com.scottlogic.datahelix.generator.common.profile.Fields;
import com.scottlogic.datahelix.generator.common.profile.InSetRecord;
import com.scottlogic.datahelix.generator.core.fieldspecs.*;
import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.datahelix.generator.core.decisiontree.ConstraintNode;
import com.scottlogic.datahelix.generator.core.decisiontree.DecisionNode;
import com.scottlogic.datahelix.generator.core.decisiontree.DecisionTree;
import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.InSetConstraint;
import com.scottlogic.datahelix.generator.core.reducer.ConstraintReducer;
import com.scottlogic.datahelix.generator.core.walker.pruner.Merged;
import com.scottlogic.datahelix.generator.core.walker.pruner.TreePruner;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scottlogic.datahelix.generator.common.util.FlatMappingSpliterator.flatMap;

public class RowSpecTreeSolver {
    private final ConstraintReducer constraintReducer;
    private final TreePruner treePruner;
    private final OptionPicker optionPicker;

    @Inject
    public RowSpecTreeSolver(ConstraintReducer constraintReducer,
                             TreePruner treePruner,
                             OptionPicker optionPicker) {
        this.constraintReducer = constraintReducer;
        this.treePruner = treePruner;
        this.optionPicker = optionPicker;
    }

    public Stream<WeightedElement<RowSpec>> createRowSpecs(DecisionTree tree) {
        return flatMap(reduceToRowNodes(tree.rootNode),
            rootNode -> toRowspec(tree.fields, rootNode));
    }

    private Stream<WeightedElement<RowSpec>> toRowspec(Fields fields, WeightedElement<ConstraintNode> rootNode) {
        Optional<RowSpec> result = constraintReducer.reduceConstraintsToRowSpec(fields, rootNode.element());
        return result
            .map(rowSpec -> new WeightedElement<>(rowSpec, rootNode.weight()))
            .map(Stream::of)
            .orElseGet(Stream::empty);
    }

    /**
     * a row node is a constraint node with no further decisions
     */
    private Stream<WeightedElement<ConstraintNode>> reduceToRowNodes(ConstraintNode rootNode) {
        return reduceToRowNodes(new WeightedElement<>(rootNode, 1));
    }

    private Stream<WeightedElement<ConstraintNode>> reduceToRowNodes(WeightedElement<ConstraintNode> rootNode) {
        if (rootNode.element().getDecisions().isEmpty()) {
            return Stream.of(rootNode);
        }

        DecisionNode decisionNode = optionPicker.pickDecision(rootNode.element());
        ConstraintNode rootWithoutDecision = rootNode.element().builder().removeDecision(decisionNode).build();

        Stream<WeightedElement<ConstraintNode>> rootOnlyConstraintNodes = optionPicker.streamOptions(decisionNode)
            .map(option -> combineWithRootNode(rootWithoutDecision, option))
            .filter(newNode -> !newNode.element().isContradictory())
            .map(weighted -> new WeightedElement<>(weighted.element().get(), weighted.weight()));

        return flatMap(
            rootOnlyConstraintNodes,
            weightedConstraintNode -> reduceToRowNodes(
                new WeightedElement<ConstraintNode>(
                    weightedConstraintNode.element(),
                    weightedConstraintNode.weight() / rootNode.weight())));
    }

    private WeightedElement<Merged<ConstraintNode>> combineWithRootNode(ConstraintNode rootNode, ConstraintNode option) {
        ConstraintNode constraintNode = rootNode.builder()
            .addDecisions(option.getDecisions())
            .addAtomicConstraints(option.getAtomicConstraints())
            .addRelations(option.getRelations())
            .build();

        /*
        Find the relevance of this option in the context of the the tree.
        i.e. if this option says that a field must be equal to A in the possible set of A (20%), B (30%) and C (50%) yield the weighing of A (20%)
        applicabilityOfThisOption should yield 0.2 in this case (20% of 1)
        */
        double applicabilityOfThisOption = option.getAtomicConstraints().stream()
            .mapToDouble(optionAtomicConstraint -> rootNode.getAtomicConstraints().stream()
                .filter(rootAtomicConstraint -> rootAtomicConstraint.getField().equals(optionAtomicConstraint.getField()))
                .filter(rootAtomicConstraint -> rootAtomicConstraint instanceof InSetConstraint)
                .map(rootAtomicConstraint -> (InSetConstraint)rootAtomicConstraint)
                .findFirst()
                .map(matchingRootAtomicConstraint -> {
                    double totalWeighting = getWeightOfAllLegalValues(matchingRootAtomicConstraint);
                    double relevantWeighting = getWeightOfAllPermittedLegalValues(matchingRootAtomicConstraint, optionAtomicConstraint);

                    return relevantWeighting / totalWeighting;
                })
                .orElse(1d))
            .sum();

        if (applicabilityOfThisOption > 1) {
            /*
            the applicability of this option (e.g. A) is greater than 100%, retrieve the fractional part of the number.
            if  there is no fractional part, treat the applicability as 1 otherwise use the fractional part as the percentage.

            This can happen when other fields in the option match an inSet constraint, if they also have a weighting it
            will be taken into account, otherwise 1 (100%) would be used, hence 100% (field1) + 20% (field2) = 1.2.
            */

            double applicabilityFraction = applicabilityOfThisOption - (int) applicabilityOfThisOption;
            applicabilityOfThisOption = applicabilityFraction == 0
                ? 1
                : applicabilityFraction;
        }

        if (applicabilityOfThisOption == 0){
            /*no options were applicable, maybe an option was given 0%, yield contradictory so the option isn't processed.*/

            return new WeightedElement<>(
                Merged.contradictory(),
                1
            );
        }

        /*Yield a weighted element to inform the caller of the weighting of this constraint node*/
        return new WeightedElement<>(
            treePruner.pruneConstraintNode(constraintNode, getFields(option)),
            applicabilityOfThisOption
        );
    }

    private static double getWeightOfAllLegalValues(InSetConstraint matchingRootAtomicConstraint){
        return matchingRootAtomicConstraint.legalValues.stream()
            .mapToDouble(InSetRecord::getWeightValueOrDefault).sum();
    }

    private static double getWeightOfAllPermittedLegalValues(InSetConstraint matchingRootAtomicConstraint, AtomicConstraint optionAtomicConstraint){
        return matchingRootAtomicConstraint.legalValues.stream()
            .filter(legalValue -> optionAtomicConstraint.toFieldSpec().canCombineWithLegalValue(legalValue.getElement()))
            .mapToDouble(InSetRecord::getWeightValueOrDefault).sum();
    }

    private Map<Field, FieldSpec> getFields(ConstraintNode option) {
        return option.getAtomicConstraints().stream()
            .map(AtomicConstraint::getField)
            .distinct()
            .collect(Collectors.toMap(
                Function.identity(),
                field -> FieldSpecFactory.fromType(field.getType())));
    }

}
