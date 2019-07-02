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
package com.scottlogic.deg.common.profile.constraints.atomic;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.RuleInformation;
import com.scottlogic.deg.common.profile.constraintdetail.ParsedGranularity;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

public class IsGranularToNumericConstraintTests {

    @Test
    public void testConstraintIsEqual() {
        Field field1 = new Field("TestField");
        Field field2 = new Field("TestField");
        IsGranularToNumericConstraint constraint1 = new IsGranularToNumericConstraint(field1, new ParsedGranularity(new BigDecimal(0.1)), rules());
        IsGranularToNumericConstraint constraint2 = new IsGranularToNumericConstraint(field2, new ParsedGranularity(new BigDecimal(0.1)), rules());
        Assert.assertThat(constraint1, Matchers.equalTo(constraint2));
    }

    @Test
    public void testConstraintIsNotEqualDueToField() {
        Field field1 = new Field("TestField");
        Field field2 = new Field("TestField2");
        IsGranularToNumericConstraint constraint1 = new IsGranularToNumericConstraint(field1, new ParsedGranularity(new BigDecimal(0.1)), rules());
        IsGranularToNumericConstraint constraint2 = new IsGranularToNumericConstraint(field2, new ParsedGranularity(new BigDecimal(0.1)), rules());
        Assert.assertNotEquals(constraint1, constraint2);
    }

    @Test
    public void testConstraintIsNotEqualDueToValue() {
        Field field1 = new Field("TestField");
        Field field2 = new Field("TestField");
        IsGranularToNumericConstraint constraint1 = new IsGranularToNumericConstraint(field1, new ParsedGranularity(new BigDecimal(0.1)), rules());
        IsGranularToNumericConstraint constraint2 = new IsGranularToNumericConstraint(field2, new ParsedGranularity(new BigDecimal(1.0)), rules());
        Assert.assertNotEquals(constraint1, constraint2);
    }

    private static Set<RuleInformation> rules(){
        return Collections.singleton(new RuleInformation());
    }
}
