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


package com.scottlogic.deg.common.profile.constraints.delayed;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.constraints.atomic.AtomicConstraint;

public class IsAfterDynamicDateConstraint implements DelayedAtomicConstraint {

    private final AtomicConstraint underlyingConstraint;

    private final Field field;

    private final boolean inclusive;

    public IsAfterDynamicDateConstraint(AtomicConstraint underlyingConstraint,
                                        Field field,
                                        boolean inclusive) {
        DelayedAtomicConstraint.validateFieldsAreDifferent(underlyingConstraint.getField(), field);
        this.underlyingConstraint = underlyingConstraint;
        this.field = field;
        this.inclusive = inclusive;
    }

    @Override
    public AtomicConstraint underlyingConstraint() {
        return underlyingConstraint;
    }

    @Override
    public Field field() {
        return field;
    }

    public boolean inclusive() {
        return inclusive;
    }

}
