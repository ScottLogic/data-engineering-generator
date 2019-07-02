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
package com.scottlogic.deg.common.profile.constraintdetail;

import com.scottlogic.deg.common.util.NumberUtils;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Granularity expressions could be interpreted differently depending on other constraints on a field (eg, type constraints),
 * so we store all possible parsings in this class, ready to make a GranularityRestrictions object
 * */
public class ParsedGranularity {
    private final BigDecimal numericGranularity;

    public ParsedGranularity(BigDecimal numericGranularity) {
        this.numericGranularity = numericGranularity;
    }

    public static ParsedGranularity parse(Object granularityExpression) {
        if (granularityExpression instanceof Number) {
            BigDecimal asNumber = NumberUtils.coerceToBigDecimal(granularityExpression);

            if (asNumber == null){
                throw new IllegalArgumentException("Numeric granularity input type is not supported");
            }

            if (asNumber.compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException("Numeric granularity must be <= 1");
            }

            if (!asNumber.equals(BigDecimal.ONE.scaleByPowerOfTen(-asNumber.scale()))) {
                throw new IllegalArgumentException("Numeric granularity must be fractional power of ten");
            }

            return new ParsedGranularity(asNumber);
        }

        throw new IllegalArgumentException("Can't interpret granularity expression: " + granularityExpression);
    }

    public static Optional<ParsedGranularity> tryParse(Object granularityExpression){
        try{
            ParsedGranularity parsedGranularityToReturn = parse(granularityExpression);
            return Optional.of(parsedGranularityToReturn);
        }
        catch(Exception exp){
            return Optional.empty();
        }
    }

    public BigDecimal getNumericGranularity() {
        return this.numericGranularity;
    }
}
