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

package com.scottlogic.datahelix.generator.core.walker.rowspec;

import com.google.inject.Inject;
import com.scottlogic.datahelix.generator.common.distribution.WeightedElement;
import com.scottlogic.datahelix.generator.core.decisiontree.DecisionTree;
import com.scottlogic.datahelix.generator.core.fieldspecs.RowSpec;
import com.scottlogic.datahelix.generator.core.generation.databags.DataBag;
import com.scottlogic.datahelix.generator.core.generation.databags.RowSpecDataBagGenerator;
import com.scottlogic.datahelix.generator.core.utils.JavaUtilRandomNumberGenerator;
import com.scottlogic.datahelix.generator.core.walker.DecisionTreeWalker;
import com.scottlogic.datahelix.generator.core.walker.decisionbased.RowSpecTreeSolver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomRowSpecDecisionTreeWalker implements DecisionTreeWalker {
    private final RowSpecTreeSolver rowSpecTreeSolver;
    private final RowSpecDataBagGenerator rowSpecDataBagGenerator;
    private PotentialRowSpecCount potentialRowSpecCount;
    private final JavaUtilRandomNumberGenerator random;

    @Inject
    public RandomRowSpecDecisionTreeWalker(RowSpecTreeSolver rowSpecTreeSolver,
                                           RowSpecDataBagGenerator rowSpecDataBagGenerator,
                                           PotentialRowSpecCount potentialRowSpecCount,
                                           JavaUtilRandomNumberGenerator random) {
        this.rowSpecTreeSolver = rowSpecTreeSolver;
        this.rowSpecDataBagGenerator = rowSpecDataBagGenerator;
        this.potentialRowSpecCount = potentialRowSpecCount;
        this.random = random;
    }

    @Override
    public Stream<DataBag> walk(DecisionTree tree) {
        if (tree.rootNode.getDecisions().isEmpty()) {
            return generateWithoutRestarting(tree);
        }
        boolean useCache = potentialRowSpecCount.lessThanMax(tree);
        Stream<RowSpec> rowSpecStream = useCache  ? getFromCachedRowSpecs(tree): getRowSpecAndRestart(tree);

        return rowSpecStream.map(this::createDataBag);
    }

    private Stream<RowSpec> getFromCachedRowSpecs(DecisionTree tree) {
        List<WeightedElement<RowSpec>> rowSpecCache = rowSpecTreeSolver.createRowSpecs(tree).collect(Collectors.toList());
        return Stream.generate(() -> getRandomRowSpec(rowSpecCache));
    }

    private Stream<DataBag> generateWithoutRestarting(DecisionTree tree) {
        RowSpec rowSpec = getFirstRowSpec(tree).get();
        return rowSpecDataBagGenerator.createDataBags(rowSpec);
    }

    private Stream<RowSpec> getRowSpecAndRestart(DecisionTree tree) {
        Optional<RowSpec> firstRowSpecOpt = getFirstRowSpec(tree);
        if (!firstRowSpecOpt.isPresent()) {
            return Stream.empty();
        }

        return Stream.generate(() -> getFirstRowSpec(tree))
            .map(Optional::get);
    }

    private Optional<RowSpec> getFirstRowSpec(DecisionTree tree) {
        return rowSpecTreeSolver.createRowSpecs(tree).findFirst().map(WeightedElement::element);
    }

    /**
     * Get a row spec from the rowSpecCache in a weighted manner.<br />
     * I.e. if there are 2 rowSpecs, one with a 70% weighting and the other with 30%.<br />
     * Calling this method 10 times should *ROUGHLY* emit 7 of the 70% weighted rowSpecs and 3 of the others.<br />
     * It does this by producing a virtual rowSpec 'range', i.e.<br />
     *  - values between 1 and 70 represent the 70% weighted rowSpec<br />
     *  - values between 71 and 100 represent the 30% weighted rowSpec<br />
     * <br />
     *  The function then picks a random number between 1 and 100 and yields the rowSpec that encapsulates that value.<br />
     * <br />
     *  As this method uses a random number generator, it will not ALWAYS yield a correct split, but it is more LIKELY than not.<br />
     * @param rowSpecCache a list of weighted rowSpecs (weighting is between 0 and 1)
     * @return a rowSpec picked from the list of weighted rowSpecs
     */
    private RowSpec getRandomRowSpec(List<WeightedElement<RowSpec>> rowSpecCache) {
        double totalRange = rowSpecCache.stream()
            .mapToDouble(WeightedElement::weight).sum();

        double nextRowSpecFromRange = random.nextInt((int)(totalRange * 100)) / 100d;

        double currentPosition = 0d;
        WeightedElement<RowSpec> lastRowSpec = null;

        for (WeightedElement<RowSpec> weightedRowSpec: rowSpecCache) {
            currentPosition += weightedRowSpec.weight();

            if (currentPosition >= nextRowSpecFromRange) {
                return weightedRowSpec.element();
            }

            lastRowSpec = weightedRowSpec;
        }

        return lastRowSpec.element();
    }

    private DataBag createDataBag(RowSpec rowSpec) {
        return rowSpecDataBagGenerator.createDataBags(rowSpec).findFirst().get();
    }
}
