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
package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.generator.restrictions.BlacklistRestrictions;
import com.scottlogic.deg.generator.utils.SetUtils;

import java.util.Optional;

public class BlacklistRestictionsMergeOperation implements RestrictionMergeOperation {
    @Override
    public FieldSpec applyMergeOperation(FieldSpec left, FieldSpec right, FieldSpec merging) {
        BlacklistRestrictions newBlacklist;
        if (left.getBlacklistRestrictions() == null) {
            newBlacklist = right.getBlacklistRestrictions();
        }
        else if (right.getBlacklistRestrictions() == null) {
            newBlacklist = left.getBlacklistRestrictions();
        }
        else {
            newBlacklist = new BlacklistRestrictions(
                SetUtils.union(
                    left.getBlacklistRestrictions().getBlacklist(),
                    right.getBlacklistRestrictions().getBlacklist()
                )
            );
        }

        return merging.withBlacklistRestrictions(newBlacklist);
    }
}
