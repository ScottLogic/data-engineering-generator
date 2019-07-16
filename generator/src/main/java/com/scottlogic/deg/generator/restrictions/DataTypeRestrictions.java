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

package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.common.profile.constraints.atomic.IsOfTypeConstraint;
import com.scottlogic.deg.generator.utils.SetUtils;

import java.util.*;

public class DataTypeRestrictions implements TypeRestrictions {

    public static final TypeRestrictions ALL_TYPES_PERMITTED = new AnyTypeRestriction();
    public static final TypeRestrictions NO_TYPES_PERMITTED = new NoAllowedTypesRestriction();

    public DataTypeRestrictions(Collection<IsOfTypeConstraint.Types> allowedTypes) {
        if (allowedTypes.isEmpty())
            throw new UnsupportedOperationException("Cannot have a type restriction with no types");

        this.allowedTypes = new HashSet<>(allowedTypes);
    }

    public static TypeRestrictions createFromWhiteList(IsOfTypeConstraint.Types... types) {
        return new DataTypeRestrictions(Arrays.asList(types));
    }

    public TypeRestrictions except(IsOfTypeConstraint.Types... types) {
        if (types.length == 0)
            return this;

        ArrayList<IsOfTypeConstraint.Types> allowedTypes = new ArrayList<>(this.allowedTypes);
        allowedTypes.removeAll(Arrays.asList(types));

        if (allowedTypes.isEmpty()){
            return NO_TYPES_PERMITTED;
        }

        return new DataTypeRestrictions(allowedTypes);
    }

    private final Set<IsOfTypeConstraint.Types> allowedTypes;

    public boolean isTypeAllowed(IsOfTypeConstraint.Types type){
        return allowedTypes.contains(type);
    }

    public String toString() {
        if (allowedTypes.size() == 1)
            return String.format("Type = %s", allowedTypes.toArray()[0]);

        return String.format(
                "Types: %s",
                Objects.toString(allowedTypes));
    }

    public TypeRestrictions intersect(TypeRestrictions other) {
        if (other == ALL_TYPES_PERMITTED)
            return this;

        Set<IsOfTypeConstraint.Types> intersection = SetUtils.intersect(allowedTypes, other.getAllowedTypes());

        if (intersection.isEmpty())
            return null;

        return new DataTypeRestrictions(intersection);
    }

    public Set<IsOfTypeConstraint.Types> getAllowedTypes() {
        return allowedTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTypeRestrictions that = (DataTypeRestrictions) o;
        return Objects.equals(allowedTypes, that.allowedTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowedTypes);
    }
}

