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

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class IsOfTypeConstraint implements AtomicConstraint {
    public final Field field;
    public final Types requiredType;
    private final Set<RuleInformation> rules;

    public IsOfTypeConstraint(Field field, Types requiredType, Set<RuleInformation> rules) {
        this.field = field;
        this.requiredType = requiredType;
        this.rules = rules;
    }

    public enum Types {
        NUMERIC(o -> o instanceof Number),
        STRING(o -> o instanceof String),
        DATETIME(o -> o instanceof OffsetDateTime);

        private final Function<Object, Boolean> isInstanceOf;

        Types(final Function<Object, Boolean> isInstanceOf) {
            this.isInstanceOf = isInstanceOf;
        }

        public boolean isInstanceOf(Object o) {
            return isInstanceOf.apply(o);
        }
    }

    @Override
    public String toDotLabel() {
        return String.format("%s is %s", field.name, requiredType.name());
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof ViolatedAtomicConstraint) {
            return o.equals(this);
        }
        if (o == null || getClass() != o.getClass()) return false;
        IsOfTypeConstraint constraint = (IsOfTypeConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(requiredType, constraint.requiredType);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, requiredType);
    }

    @Override
    public String toString() { return String.format("`%s` is %s", field.name, requiredType.name()); }

    @Override
    public Set<RuleInformation> getRules() {
        return rules;
    }

    @Override
    public AtomicConstraint withRules(Set<RuleInformation> rules) {
        return new IsOfTypeConstraint(this.field, this.requiredType, rules);
    }
}
