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
package com.scottlogic.deg.generator.restrictions;

public class TypeRestrictionsMerger {
    public MergeResult<TypeRestrictions> merge(TypeRestrictions left, TypeRestrictions right) {
        if (left == null && right == null)
            return new MergeResult<>(null);
        if (left == null)
            return new MergeResult<>(right);
        if (right == null)
            return new MergeResult<>(left);

        final TypeRestrictions merged = left.intersect(right);

        if (merged == null) {
            return new MergeResult<>(DataTypeRestrictions.NO_TYPES_PERMITTED);
        }

        return new MergeResult<>(merged);
    }
}
