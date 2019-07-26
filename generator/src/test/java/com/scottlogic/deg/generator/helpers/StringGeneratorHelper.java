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

package com.scottlogic.deg.generator.helpers;

import com.scottlogic.deg.generator.generation.string.StringGenerator;
import org.junit.Assert;

import java.util.Iterator;

import static org.hamcrest.Matchers.*;

public enum StringGeneratorHelper {
    ;

    StringGeneratorHelper() {
    }

    public static void assertGeneratorCannotGenerateAnyStrings(StringGenerator generator) {
        Iterator<String> stringValueIterator = generator.generateAllValues().iterator();

        // TODO AF remove once fixed #1154 bug - useful for testing this bug
        int i = 0;
        while (stringValueIterator.hasNext() && i < 15) {
            System.out.println("" + i + ": '" + stringValueIterator.next() + "'");
            ++i;
        }

        Assert.assertThat(stringValueIterator.hasNext(), is(false));
    }

    public static void assertGeneratorCanGenerateAtLeastOneString(StringGenerator generator) {
        Iterator<String> stringValueIterator = generator.generateAllValues().iterator();
        Assert.assertThat(stringValueIterator.hasNext(), is(true));
    }

    public static void assertGeneratorCanGenerateAtLeastOneStringWithinLengthBounds(StringGenerator generator, int minLength, int maxLength) {
        Iterator<String> stringValueIterator = generator.generateAllValues().iterator();
        Assert.assertThat(stringValueIterator.hasNext(), is(true));
        String value = stringValueIterator.next();

        // TODO AF remove once fixed #1154 bug - useful for testing this bug
        System.out.println("'" + value + "'");
        int i = 1;
        while (stringValueIterator.hasNext() && i < 100) {
            String next = stringValueIterator.next();
            System.out.println("" + i + ": '" + next + "'");
            ++i;
        }

        Assert.assertThat(value.length(), greaterThanOrEqualTo(minLength));
        Assert.assertThat(value.length(), lessThanOrEqualTo(maxLength));
    }

}