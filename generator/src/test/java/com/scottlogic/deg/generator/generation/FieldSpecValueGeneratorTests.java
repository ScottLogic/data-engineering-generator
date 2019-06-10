package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.common.profile.constraintdetail.Nullness;
import com.scottlogic.deg.common.profile.constraints.atomic.IsOfTypeConstraint;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.fieldspecs.FieldSpecSource;
import com.scottlogic.deg.generator.generation.databags.DataBagValue;
import com.scottlogic.deg.generator.restrictions.*;
import com.scottlogic.deg.generator.restrictions.set.SetRestrictions;
import com.scottlogic.deg.generator.utils.JavaUtilRandomNumberGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.scottlogic.deg.generator.config.detail.DataGenerationType.INTERESTING;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertTrue;

class FieldSpecValueGeneratorTests {
    private final NullRestrictions notNull = new NullRestrictions(Nullness.MUST_NOT_BE_NULL);
    private final FieldSpecSource fieldSpecSource = FieldSpecSource.Empty;

    @Test
    void generate_fieldSpecMustContainRestrictionNullAndSetRestrictionsHasValues_returnsDataBagsWithValuesInSetRestrictions() {
        FieldSpec fieldSpec = FieldSpec.Empty
            .withNullRestrictions(notNull, fieldSpecSource)
            .withSetRestrictions(
                SetRestrictions.fromWhitelist(
                    new HashSet<>(
                        Arrays.asList(10, 20, 30))),
                fieldSpecSource);
        FieldSpecValueGenerator fieldSpecFulfiller = new FieldSpecValueGenerator(
            INTERESTING,
            new StandardFieldValueSourceEvaluator(),
            new JavaUtilRandomNumberGenerator());

        final Set<DataBagValue> result = fieldSpecFulfiller.generate(fieldSpec).collect(Collectors.toSet());

        Set<DataBagValue> expectedDataBags = new HashSet<>(
            Arrays.asList(
                    new DataBagValue(10, fieldSpec.getFieldSpecSource().toDataBagValueSource()),
                    new DataBagValue(20, fieldSpec.getFieldSpecSource().toDataBagValueSource()),
                    new DataBagValue(30, fieldSpec.getFieldSpecSource().toDataBagValueSource())
            )
        );

        assertThat(result, sameBeanAs(expectedDataBags));
    }

    @Test
    void generate_fieldSpecMustContainRestrictionNullAndNumericRestrictionApplied_returnsExpectedDataBagsForNumericRestriction() {
        FieldSpec fieldSpec = FieldSpec.Empty
            .withNumericRestrictions(
                new NumericRestrictions() {{
                    min = new NumericLimit<>(new BigDecimal(10), false);
                    max = new NumericLimit<>(new BigDecimal(30), false);
                }},
                fieldSpecSource)
            .withTypeRestrictions(
                new DataTypeRestrictions(
                    Collections.singletonList(IsOfTypeConstraint.Types.NUMERIC)
                ),
                fieldSpecSource);
        FieldSpecValueGenerator fieldSpecFulfiller = new FieldSpecValueGenerator(
            INTERESTING,
            new StandardFieldValueSourceEvaluator(),
            new JavaUtilRandomNumberGenerator());

        final Set<DataBagValue> result =
            fieldSpecFulfiller.generate(fieldSpec).collect(Collectors.toSet());

        Set<DataBagValue> expectedDataBags = new HashSet<>(
            Arrays.asList(
                new DataBagValue(
                    new BigDecimal("10.00000000000000000001"),
                    fieldSpec.getFieldSpecSource().toDataBagValueSource()
                ),
                new DataBagValue(
                    new BigDecimal("10.00000000000000000002"),
                    fieldSpec.getFieldSpecSource().toDataBagValueSource()
                ),
                new DataBagValue(
                    new BigDecimal("29.99999999999999999998"),
                    fieldSpec.getFieldSpecSource().toDataBagValueSource()
                ),
                new DataBagValue(
                    new BigDecimal("29.99999999999999999999"),
                    fieldSpec.getFieldSpecSource().toDataBagValueSource()
                ),
                new DataBagValue(null, fieldSpec.getFieldSpecSource().toDataBagValueSource())
            )
        );

        assertThat(result, sameBeanAs(expectedDataBags));
    }
}
