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

public class NotConstraint implements AtomicConstraint {
    public final AtomicConstraint negatedConstraint;

    protected NotConstraint(AtomicConstraint negatedConstraint) {
        if (negatedConstraint instanceof NotConstraint)
            throw new IllegalArgumentException("Nested NotConstraint not allowed");
        this.negatedConstraint = negatedConstraint;
    }

    @Override
    public AtomicConstraint negate() {
        return this.negatedConstraint;
    }

    private AtomicConstraint getBaseConstraint(){
        return negatedConstraint;
    }

    @Override
    public String toDotLabel() {
        /*Use the encoded character code for the NOT (¬) symbol; leaving it un-encoded causes issues with visualisers*/
        return String.format("&#x00AC;(%s)", negatedConstraint.toDotLabel());
    }

    @Override
    public Field getField() {
        return negatedConstraint.getField();
    }

    public String toString(){
        return String.format(
                "NOT(%s)",
                negatedConstraint);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (o instanceof ViolatedAtomicConstraint) {
            return o.equals(this);
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotConstraint otherConstraint = (NotConstraint) o;
        return Objects.equals(getBaseConstraint(), otherConstraint.getBaseConstraint());
    }

    @Override
    public int hashCode(){
        return Objects.hash("NOT", negatedConstraint.hashCode());
    }


    @Override
    public Set<RuleInformation> getRules() {
        return negatedConstraint.getRules();
    }

    @Override
    public AtomicConstraint withRules(Set<RuleInformation> rules) {
        return new NotConstraint(this.negatedConstraint.withRules(rules));
    }
}
