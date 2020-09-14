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

package com.scottlogic.datahelix.generator.common.profile;

import com.scottlogic.datahelix.generator.common.RandomNumberGenerator;
import com.scottlogic.datahelix.generator.common.ValidationException;
import com.scottlogic.datahelix.generator.common.util.NumberUtils;
import com.scottlogic.datahelix.generator.common.util.defaults.NumericDefaults;
import com.scottlogic.datahelix.generator.common.validators.ValidationResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class NumericGranularity implements Granularity<BigDecimal> {
    public static final NumericGranularity DECIMAL_DEFAULT = new NumericGranularity(20);
    public static final NumericGranularity INTEGER_DEFAULT = new NumericGranularity(0);

    private final int decimalPlaces;

    public NumericGranularity(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public static NumericGranularity create(Object granularity)
    {
        BigDecimal asNumber = NumberUtils.coerceToBigDecimal(granularity);
        if (asNumber == null) {
            throw new ValidationException(String.format("Can't interpret numeric granularity expression: %s", ValidationResult.quote(granularity)));
        }
        if (asNumber.compareTo(BigDecimal.ONE) > 0) {
            throw new ValidationException("Numeric granularity must be <= 1");
        }
        if (!asNumber.equals(BigDecimal.ONE.scaleByPowerOfTen(-asNumber.scale()))) {
            throw new ValidationException("Numeric granularity must be fractional power of ten");
        }
        return new NumericGranularity(asNumber.scale());
    }

    @Override
    public Granularity<BigDecimal> getFinestGranularity() {
        return NumericDefaults.get().granularity();
    }

    @Override
    public boolean isCorrectScale(BigDecimal value) {
        return value.stripTrailingZeros().scale() <= decimalPlaces;
    }

    @Override
    public NumericGranularity merge(Granularity<BigDecimal> otherGranularity) {
        NumericGranularity other = (NumericGranularity) otherGranularity;
        return decimalPlaces <= other.decimalPlaces ? this : other;
    }

    @Override
    public BigDecimal getNext(BigDecimal value, int amount) {
        BigDecimal addAmount = BigDecimal.ONE.scaleByPowerOfTen(decimalPlaces * -1)
            .multiply(BigDecimal.valueOf(amount));
        return value.add(addAmount);
    }

    @Override
    public BigDecimal getNext(BigDecimal value) {
        return value.add(BigDecimal.ONE.scaleByPowerOfTen(decimalPlaces * -1));
    }

    @Override
    public BigDecimal getRandom(BigDecimal min, BigDecimal max, RandomNumberGenerator randomNumberGenerator) {
        BigDecimal value = randomNumberGenerator.nextBigDecimal(min, max);
        return trimToGranularity(value);
    }

    @Override
    public BigDecimal getPrevious(BigDecimal value, int amount) {
        if (isCorrectScale(value)){
            return value.subtract(BigDecimal.ONE.scaleByPowerOfTen(decimalPlaces * -1)
                .multiply(BigDecimal.valueOf(amount)));
        }

        return trimToGranularity(value);

    }

    @Override
    public BigDecimal trimToGranularity(BigDecimal value) {
        return value.setScale(decimalPlaces, RoundingMode.FLOOR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumericGranularity that = (NumericGranularity) o;
        return decimalPlaces == that.decimalPlaces;
    }

    @Override
    public String toString() {
        return decimalPlaces +
            " decimal places=";
    }

    @Override
    public int hashCode() {
        return Objects.hash(decimalPlaces);
    }
}
