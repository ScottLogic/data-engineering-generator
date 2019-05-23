package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.common.output.DataBagValueSource;
import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.generator.generation.databags.DataBagValue;
import com.scottlogic.deg.generator.restrictions.NullRestrictions;
import com.scottlogic.deg.common.profile.constraintdetail.Nullness;
import com.scottlogic.deg.generator.restrictions.SetRestrictions;
import com.scottlogic.deg.generator.walker.reductive.fieldselectionstrategy.FieldValue;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

class FieldSpecHelperTests {

    FieldSpecHelper fieldSpecHelper = new FieldSpecHelper();
    private final Field field = new Field("field");

    @Test
    void getFieldSpecForValue() {
        FieldValue input = new FieldValue(field, new DataBagValue("value", DataBagValueSource.Empty));

        FieldSpec actual = fieldSpecHelper.getFieldSpecForValue(input);

        FieldSpec expected = FieldSpec.Empty
            .withSetRestrictions(SetRestrictions.fromWhitelist(Collections.singleton("value")), FieldSpecSource.Empty)
            .withNullRestrictions(new NullRestrictions(Nullness.MUST_NOT_BE_NULL), FieldSpecSource.Empty);

        assertThat(actual, sameBeanAs(expected).ignoring(FieldSpecSource.class));
    }

    @Test
    void getFieldSpecForNullValue() {
        FieldValue input = new FieldValue(field, new DataBagValue(null, DataBagValueSource.Empty));

        FieldSpec actual = fieldSpecHelper.getFieldSpecForValue(input);

        FieldSpec expected = FieldSpec.Empty
            .withNullRestrictions(new NullRestrictions(Nullness.MUST_BE_NULL), FieldSpecSource.Empty);

        assertThat(actual, sameBeanAs(expected).ignoring(FieldSpecSource.class));
    }
}