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

package com.scottlogic.deg.generator.fieldspecs.relations;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.restrictions.DateTimeRestrictions;
import com.scottlogic.deg.generator.restrictions.DateTimeRestrictions.DateTimeLimit;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjuster;

public class EqualToOffsetDateRelation implements FieldSpecRelations {
    private Field main;
    private Field other;
    private TemporalAdjuster adjuster;
    private int offset;

    public EqualToOffsetDateRelation(Field main,
                                     Field other,
                                     TemporalAdjuster adjuster,
                                     int offset) {
        this.main = main;
        this.other = other;
        this.adjuster = adjuster;
        this.offset = offset;
    }

    @Override
    public FieldSpec reduceToRelatedFieldSpec(FieldSpec otherValue) {
        if (otherValue.getDateTimeRestrictions() != null) {
            DateTimeLimit limit = otherValue.getDateTimeRestrictions().min;
            OffsetDateTime time = limit.getLimit();
            OffsetDateTime newTime = time;
            for (int i=0; i < offset; i++) {
                newTime = newTime.with(adjuster);
            }
            DateTimeLimit newLimit = new DateTimeLimit(newTime, true);
            DateTimeRestrictions newRestrictions = new DateTimeRestrictions();
            newRestrictions.min = newLimit;
            newRestrictions.max = newLimit;
            return FieldSpec.Empty.withDateTimeRestrictions(newRestrictions);
        } else {
            return FieldSpec.Empty;
        }
    }

    @Override
    public FieldSpecRelations inverse() {
        return new EqualToOffsetDateRelation(other, main, adjuster, offset);
    }

    @Override
    public Field main() {
        return main;
    }

    @Override
    public Field other() {
        return other;
    }
}