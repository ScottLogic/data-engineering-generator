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
package com.scottlogic.deg.generator.walker.reductive.fieldselectionstrategy;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.deg.generator.decisiontree.ConstraintNode;
import com.scottlogic.deg.generator.decisiontree.visualisation.BaseVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldAppearanceAnalyser extends BaseVisitor {

    Map<Field, Integer> fieldAppearances = new HashMap<>();

    @Override
    public ConstraintNode visit(ConstraintNode constraintNode){
        constraintNode.getAtomicConstraints().stream()
            .map(AtomicConstraint::getField)
            .distinct()
            .forEach(this::countField);
        return constraintNode;
    }

    private void countField(Field field) {
        fieldAppearances.compute(field, (k, count) -> count == null ? 1 : count+1);
    }
}
