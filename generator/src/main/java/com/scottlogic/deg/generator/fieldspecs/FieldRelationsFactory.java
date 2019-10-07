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


package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.generator.fieldspecs.relations.*;
import com.scottlogic.deg.generator.profile.constraints.delayed.DelayedAtomicConstraint;
import com.scottlogic.deg.generator.profile.constraints.delayed.DelayedDateAtomicConstraint;
import com.scottlogic.deg.generator.profile.constraints.delayed.DelayedInMapAtomicConstraint;

public class FieldRelationsFactory {

   public FieldSpecRelations construct(DelayedAtomicConstraint constraint) {
       switch (constraint.getUnderlyingConstraint()) {
           case IS_EQUAL_TO_CONSTANT:
               return constructEqualToDate((DelayedDateAtomicConstraint)constraint);
           case IS_BEFORE_CONSTANT_DATE_TIME:
               return constructBeforeDate(constraint, false);
           case IS_BEFORE_OR_EQUAL_TO_CONSTANT_DATE_TIME:
               return constructBeforeDate(constraint, true);
           case IS_AFTER_CONSTANT_DATE_TIME:
               return constructAfterDate(constraint, false);
           case IS_AFTER_OR_EQUAL_TO_CONSTANT_DATE_TIME:
               return constructAfterDate(constraint, true);
           case IS_IN_MAP:
               return constructInMap((DelayedInMapAtomicConstraint) constraint);
       }

       throw new IllegalArgumentException("Unsupported field spec relations: " + constraint.getUnderlyingConstraint());

   }

   private FieldSpecRelations constructBeforeDate(DelayedAtomicConstraint constraint, boolean inclusive) {
       return new BeforeDateRelation(
           constraint.getField(),
           constraint.getOtherField(),
           inclusive);
   }

   private FieldSpecRelations constructAfterDate(DelayedAtomicConstraint constraint, boolean inclusive) {
       return new AfterDateRelation(
           constraint.getField(),
           constraint.getOtherField(),
           inclusive);
   }

   private FieldSpecRelations constructEqualToDate(DelayedDateAtomicConstraint constraint) {
       if (constraint.getOffsetUnit() != null) {
           return new EqualToOffsetDateRelation(
               constraint.getField(),
               constraint.getOtherField(),
               constraint.getOffsetGranularity(),
               constraint.getOffsetUnit());
       } else {
           return new EqualToDateRelation(
               constraint.getField(),
               constraint.getOtherField());
       }
   }

   private FieldSpecRelations constructInMap(DelayedInMapAtomicConstraint constraint) {
       return new InMapRelation(constraint.getField(), constraint.getOtherField(), constraint.getUnderlyingList());
   }
}
