package com.scottlogic.deg.generator.walker;

import com.scottlogic.deg.common.output.DataBagValueSource;
import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.ProfileFields;
import com.scottlogic.deg.generator.builders.DataBagBuilder;
import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.decisiontree.TreeConstraintNode;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.fieldspecs.FieldSpecSource;
import com.scottlogic.deg.generator.generation.FieldSpecValueGenerator;
import com.scottlogic.deg.generator.generation.NoopDataGeneratorMonitor;
import com.scottlogic.deg.generator.generation.databags.DataBag;
import com.scottlogic.deg.generator.generation.databags.DataBagValue;
import com.scottlogic.deg.generator.restrictions.NullRestrictions;
import com.scottlogic.deg.common.profile.constraintdetail.Nullness;
import com.scottlogic.deg.generator.restrictions.set.SetRestrictions;
import com.scottlogic.deg.generator.walker.reductive.*;
import com.scottlogic.deg.generator.walker.reductive.fieldselectionstrategy.FixFieldStrategy;
import com.scottlogic.deg.generator.walker.reductive.fieldselectionstrategy.FixFieldStrategyFactory;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class ReductiveDecisionTreeWalkerTests {
    private TreeConstraintNode rootNode;
    private DecisionTree tree;
    private ReductiveFieldSpecBuilder reductiveFieldSpecBuilder;
    private ReductiveDecisionTreeWalker walker;
    private FixFieldStrategy fixFieldStrategy;
    private FixFieldStrategyFactory fixFieldStrategyFactory;
    private FieldSpecValueGenerator fieldSpecValueGenerator;
    private Field field1 = new Field("field1");
    private Field field2 = new Field("field2");

    @BeforeEach
    public void beforeEach(){
        ProfileFields fields = new ProfileFields(Arrays.asList(field1, field2));
        rootNode = new TreeConstraintNode();
        tree = new DecisionTree(rootNode, fields, "");
        ReductiveTreePruner treePruner = mock(ReductiveTreePruner.class);
        when(treePruner.pruneConstraintNode(eq(rootNode), any(), any())).thenReturn(Merged.of(rootNode));

        reductiveFieldSpecBuilder = mock(ReductiveFieldSpecBuilder.class);
        fieldSpecValueGenerator = mock(FieldSpecValueGenerator.class);
        fixFieldStrategy = mock(FixFieldStrategy.class);
        when(fixFieldStrategy.getNextFieldToFix(any())).thenReturn(field1, field2);
        fixFieldStrategyFactory = mock(FixFieldStrategyFactory.class);
        when(fixFieldStrategyFactory.create(any())).thenReturn(fixFieldStrategy);

        walker = new ReductiveDecisionTreeWalker(
            new NoOpIterationVisualiser(),
            reductiveFieldSpecBuilder,
            new NoopDataGeneratorMonitor(),
            treePruner,
            fieldSpecValueGenerator,
            fixFieldStrategyFactory
        );
    }

    /**
     * If no field can be fixed initially, the walker should exit early, with an empty stream of RowSpecs
     */
    @Test
    public void shouldReturnEmptyCollectionOfRowsWhenFirstFieldCannotBeFixed() {
        when(reductiveFieldSpecBuilder.getDecisionFieldSpecs(eq(rootNode), any())).thenReturn(Collections.EMPTY_SET);

        List<DataBag> result = walker.walk(tree).collect(Collectors.toList());

        verify(reductiveFieldSpecBuilder).getDecisionFieldSpecs(eq(rootNode), any());
        Assert.assertThat(result, empty());
    }

    /**
     * If a field can be fixed initially, but subsequently another one cannot be fixed then exit as early as possible
     * with an empty stream of RowSpecs
     */
    @Test
    public void shouldReturnEmptyCollectionOfRowsWhenSecondFieldCannotBeFixed() {
        DataBagValue dataBag = new DataBagValue(field1, "yes", DataBagValueSource.Empty);
        FieldSpec firstFieldSpec = FieldSpec.Empty.withSetRestrictions(SetRestrictions
                .fromWhitelist(Collections.singleton("yes")), FieldSpecSource.Empty)
            .withNullRestrictions(new NullRestrictions(Nullness.MUST_NOT_BE_NULL), FieldSpecSource.Empty);
        when(fieldSpecValueGenerator.generate(any(Set.class))).thenReturn(Stream.of(dataBag));

        when(reductiveFieldSpecBuilder.getDecisionFieldSpecs(any(), any())).thenReturn(Collections.singleton(firstFieldSpec), Collections.emptySet());

        List<DataBag> result = walker.walk(tree).collect(Collectors.toList());

        verify(reductiveFieldSpecBuilder, times(2)).getDecisionFieldSpecs(eq(rootNode), any());
        Assert.assertThat(result, empty());
    }
}