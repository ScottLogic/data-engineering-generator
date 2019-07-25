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

package com.scottlogic.deg.generator.fieldspecs.whitelist;

import com.scottlogic.deg.generator.utils.RandomNumberGenerator;

import java.util.Collections;
import java.util.Set;

public class NullDistributedSet<T> implements DistributedSet<T> {

    private final Set<WeightedElement<T>> underlyingSet;

    public NullDistributedSet() {
        underlyingSet = Collections.unmodifiableSet(Collections.singleton(WeightedElement.ofNull()));
    }

    @Override
    public Set<WeightedElement<T>> distributedSet() {
        return underlyingSet;
    }

    @Override
    public T pickRandomly(RandomNumberGenerator randomValue) {
        return null;
    }
}
