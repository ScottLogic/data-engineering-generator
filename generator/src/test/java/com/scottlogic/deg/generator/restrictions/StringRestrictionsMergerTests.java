Copyright 2019 Scott Logic Ltd /
/
Licensed under the Apache License, Version 2.0 (the \"License\");/
you may not use this file except in compliance with the License./
You may obtain a copy of the License at/
/
    http://www.apache.org/licenses/LICENSE-2.0/
/
Unless required by applicable law or agreed to in writing, software/
distributed under the License is distributed on an \"AS IS\" BASIS,/
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied./
See the License for the specific language governing permissions and/
limitations under the License.
package com.scottlogic.deg.generator.restrictions;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StringRestrictionsMergerTests {
    @Test
    public void merge_withBothNull_shouldReturnSuccessWithNullRestrictions(){
        StringRestrictions left = null;
        StringRestrictions right = null;
        StringRestrictionsMerger merger = new StringRestrictionsMerger();

        MergeResult<StringRestrictions> result = merger.merge(left, right);

        Assert.assertThat(result.successful, is(true));
        Assert.assertThat(result.restrictions, is(nullValue()));
    }

    @Test
    public void merge_withLeftNull_shouldReturnSuccessWithRightRestrictions(){
        StringRestrictions left = null;
        StringRestrictions right = mock(StringRestrictions.class);
        StringRestrictionsMerger merger = new StringRestrictionsMerger();

        MergeResult<StringRestrictions> result = merger.merge(left, right);

        Assert.assertThat(result.successful, is(true));
        Assert.assertThat(result.restrictions, is(sameInstance(right)));
    }

    @Test
    public void merge_withRightNull_shouldReturnSuccessWithLeftRestrictions(){
        StringRestrictions left = mock(StringRestrictions.class);
        StringRestrictions right = null;
        StringRestrictionsMerger merger = new StringRestrictionsMerger();

        MergeResult<StringRestrictions> result = merger.merge(left, right);

        Assert.assertThat(result.successful, is(true));
        Assert.assertThat(result.restrictions, is(sameInstance(left)));
    }

    @Test
    public void merge_withBothNotNullAndNotContradictory_shouldReturnSuccessWithMergedRestrictions(){
        StringRestrictions left = mock(StringRestrictions.class);
        StringRestrictions right = mock(StringRestrictions.class);
        StringRestrictions merged = mock(StringRestrictions.class);
        StringRestrictionsMerger merger = new StringRestrictionsMerger();
        when(left.intersect(right)).thenReturn(new MergeResult<>(merged));

        MergeResult<StringRestrictions> result = merger.merge(left, right);

        Assert.assertThat(result.successful, is(true));
        Assert.assertThat(result.restrictions, is(sameInstance(merged)));
    }

    @Test
    public void merge_withBothNotNullAndContradictory_shouldReturnFailWithNullRestrictions(){
        StringRestrictions left = mock(StringRestrictions.class);
        StringRestrictions right = mock(StringRestrictions.class);
        MergeResult<StringRestrictions> merged = MergeResult.unsuccessful();
        StringRestrictionsMerger merger = new StringRestrictionsMerger();
        when(left.intersect(right)).thenReturn(merged);

        MergeResult<StringRestrictions> result = merger.merge(left, right);

        Assert.assertThat(result, is(sameInstance(merged)));
    }
}