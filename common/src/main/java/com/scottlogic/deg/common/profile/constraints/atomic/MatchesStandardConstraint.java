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

import java.util.Objects;
import java.util.Set;

public class MatchesStandardConstraint implements AtomicConstraint {
    public final Field field;
    public final StandardConstraintTypes standard;
    private final Set<RuleInformation> rules;

    public MatchesStandardConstraint(Field field, StandardConstraintTypes standard, Set<RuleInformation> rules) {
        this.field = field;
        this.standard = standard;
        this.rules = rules;
    }

    @Override
    public String toDotLabel(){
        return String.format("%s is a %s", field.name, standard.getClass().getName());
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public Set<RuleInformation> getRules() {
        return rules;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof ViolatedAtomicConstraint) {
            return o.equals(this);
        }
        if (o == null || getClass() != o.getClass()) return false;
        MatchesStandardConstraint constraint = (MatchesStandardConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(standard, constraint.standard);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, standard);
    }

    @Override
    public AtomicConstraint withRules(Set<RuleInformation> rules) {
        return new MatchesStandardConstraint(this.field, this.standard, rules);
    }
}

