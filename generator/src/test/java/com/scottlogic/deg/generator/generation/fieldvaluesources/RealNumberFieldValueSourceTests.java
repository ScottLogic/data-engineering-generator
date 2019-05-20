package com.scottlogic.deg.generator.generation.fieldvaluesources;

import com.scottlogic.deg.common.util.Defaults;
import com.scottlogic.deg.generator.restrictions.NumericLimit;
import com.scottlogic.deg.generator.restrictions.NumericRestrictions;
import com.scottlogic.deg.generator.utils.JavaUtilRandomNumberGenerator;
import com.scottlogic.deg.common.util.NumberUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;

class RealNumberFieldValueSourceTests {
    @ParameterizedTest
    @CsvSource({
        "-1,  1,    1, -1;-0.9;-0.8;-0.7;-0.6;-0.5;-0.4;-0.3;-0.2;-0.1;0.0;0.1;0.2;0.3;0.4;0.5;0.6;0.7;0.8;0.9;1",
        "0.65,1.1,  1, 0.7;0.8;0.9;1;1.1",
        "5,   35,  -1, 10;20;30",
        "10,  35,  -1, 10;20;30",
        "10,  40,  -1, 10;20;30;40",
        "5,   40,  -1, 10;20;30;40",
        "11,  40,  -1, 20;30;40",
        "10,  41,  -1, 10;20;30;40",
        "9,   41,  -1, 10;20;30;40",
        "11,  41,  -1, 20;30;40",
        "11,  49,  -1, 20;30;40",
        "-4,  5,    0, -4;-3;-2;-1;0;1;2;3;4;5",
        "0.9, 2.1,  0, 1;2",
        "0.1, 5.9,  0, 1;2;3;4;5",
        "1,   3,    0, 1;2;3"
    })
    void expectValuesInclusiveOfBounds(BigDecimal lowerBound, BigDecimal upperBound, int scale, String expectedResults) {
        givenLowerBound(lowerBound, true);
        givenUpperBound(upperBound, true);
        givenScale(scale);

        expectAllValues((Object[])expectedResults.split(";"));
    }

    @ParameterizedTest
    @CsvSource({
        "-1,   1,    1, -0.9;-0.8;-0.7;-0.6;-0.5;-0.4;-0.3;-0.2;-0.1;0.0;0.1;0.2;0.3;0.4;0.5;0.6;0.7;0.8;0.9",
        "0.65, 1.1,  1, 0.7;0.8;0.9;1",
        "5,    35,  -1, 10;20;30",
        "10,   35,  -1, 20;30",
        "10,   40,  -1, 20;30",
        "5,    40,  -1, 10;20;30",
        "11,   40,  -1, 20;30",
        "10,   41,  -1, 20;30;40",
        "9,    41,  -1, 10;20;30;40",
        "11,   41,  -1, 20;30;40",
        "11,   49,  -1, 20;30;40",
        "-4,   5,    0, -3;-2;-1;0;1;2;3;4",
        "0.9,  2.1,  0, 1;2",
        "0.1,  5.9,  0, 1;2;3;4;5",
        "1,    3,    0, 2"
    })
    void expectValuesExclusiveOfBounds(BigDecimal lowerBound, BigDecimal upperBound, int scale, String expectedResults) {
        givenLowerBound(lowerBound, false);
        givenUpperBound(upperBound, false);
        givenScale(scale);

        expectAllValues((Object[])expectedResults.split(";"));
    }

    @Test
    void whenBlacklistHasNoValuesInRange() {
        givenLowerBound(3, true);
        givenUpperBound(5, true);

        givenBlacklist(1);

        expectAllValues(3, 4, 5);
    }

    @Test
    void whenBlacklistContainsNonIntegralValues() {
        givenLowerBound(3, true);
        givenUpperBound(6, true);

        givenBlacklist("hello", 4, new BigDecimal(5));

        expectAllValues(3, 6);
    }

    @Test
    void whenBlacklistContainsAllValuesInRange() {
        givenLowerBound(3, true);
        givenUpperBound(5, true);

        givenBlacklist(3, 4, 5);

        expectNoValues();
    }

    @Test
    void whenBlacklistContainsAllValuesInExclusiveRange() {
        givenLowerBound("0.9", false);
        givenUpperBound("2.1", false);
        givenScale(0);

        givenBlacklist(1, 2);

        expectNoValues();
    }

    @Test
    void whenBlacklistRounded() {
        givenLowerBound(0, true);
        givenUpperBound(100, true);
        givenScale(-1);

        givenBlacklist(8, 31, 56, 64);
        // should filter out 10, 30, 60 (twice)

        expectAllValues(0, 20, 40, 50, 70, 80, 90, 100);
    }

    @Test
    void whenSmallWithBlacklist() {
        givenLowerBound("-0.05", false);
        givenUpperBound("0.05", false);
        givenScale(2);

        givenBlacklist("-0.03", "-0", "0.021", 4);

        expectAllValues("-0.04", "-0.02", "-0.01", "0.01", "0.03", "0.04");
    }

    @Test
    void shouldSupplyInterestingValues() {
        givenLowerBound(-10, true);
        givenUpperBound(10, true);
        givenScale(1);

        expectInterestingValues("-10", "-9.9", "0", "9.9", "10");
    }

    @Test
    void shouldSupplySmallNonZeroInterestingValues() {
        givenLowerBound("1.9", true);
        givenUpperBound("2.59", true);
        givenScale(2);

        expectInterestingValues("1.9", "1.91", "2.58", "2.59");
    }

    @Test
    void shouldSupplyInterestingValuesWhenBoundariesAreInclusiveAndClose() {
        givenLowerBound("1.55555", true);
        givenUpperBound("1.55555", true);
        givenScale(5);

        expectInterestingValues("1.55555");
    }

    @Test
    void shouldSupplyInterestingValuesWhenBoundariesAreExclusiveAndClose() {
        givenLowerBound("1.55555", false);
        givenUpperBound("1.55557", false);
        givenScale(5);

        expectInterestingValues("1.55556");
    }

    @Test
    void shouldSupplyInterestingNonBlacklistedValues() {
        givenLowerBound(-10, true);
        givenUpperBound(10, true);
        givenScale(1);

        givenBlacklist(-10, 0, 9.9);

        expectInterestingValues("-9.9", "-9.8", "0.1", "10");
    }

    @Test
    void shouldGenerateRandomValues() {
        givenLowerBound(-10, true);
        givenUpperBound(10, true);
        givenScale(5);

        expectCorrectRandomValues();
    }

    @Test
    void shouldGenerateLargeInclusiveRandomValues() {
        givenLowerBound(-100, true);
        givenUpperBound(100, true);
        givenScale(-1);

        expectCorrectRandomValues();
    }

    @Test
    void shouldGenerateLargeExclusiveRandomValues() {
        givenLowerBound(-100, false);
        givenUpperBound(100, false);
        givenScale(-1);

        expectCorrectRandomValues();
    }

    @Test
    void shouldGenerateNonBlacklistedValues() {
        givenLowerBound(5, true);
        givenUpperBound(10, false);
        givenScale(0);

        givenBlacklist(6, 8);

        expectCorrectRandomValues();
    }

    @Test
    void shouldSupplyToUpperBoundary() {
        givenLowerBound(4, true);

        expectInterestingValues(
            4, 5,
            Defaults.NUMERIC_MAX.subtract(BigDecimal.ONE),
            Defaults.NUMERIC_MAX);
    }

    @Test
    void shouldSupplyToLowerBoundary() {
        givenUpperBound(4, true);

        expectInterestingValues(
            Defaults.NUMERIC_MIN,
            Defaults.NUMERIC_MIN.add(BigDecimal.ONE),
            0, 3, 4);
    }

    @Test
    void shouldSupplyToBoundary() {
        expectInterestingValues(
            Defaults.NUMERIC_MIN,
            Defaults.NUMERIC_MIN.add(BigDecimal.ONE),
            0,
            Defaults.NUMERIC_MAX.subtract(BigDecimal.ONE),
            Defaults.NUMERIC_MAX
        );
    }

    @Test
    void shouldNotEmitInterestingValueTwiceWhenBoundsPermitManyValuesIncluding0(){
        givenLowerBound(0, true);
        givenUpperBound(Integer.MAX_VALUE, false);

        expectInterestingValues(0, 1, Integer.MAX_VALUE - 2, Integer.MAX_VALUE - 1);
    }

    @Test
    void shouldNotEmitInterestingValueTwiceWhenBoundsPermitOnlyTwoValuesIncluding0(){
        givenLowerBound(-1, true);
        givenUpperBound(1, false);

        expectInterestingValues(-1, 0);
    }

    @Test
    void shouldNotEmitInterestingValueTwiceWhenBoundsPermitOnlyOneValueIncluding0(){
        givenLowerBound(0, true);
        givenUpperBound(1, false);

        expectInterestingValues(0);
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesMatch(){
        RealNumberFieldValueSource a = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            new HashSet<>(Arrays.asList(1, 2)));
        RealNumberFieldValueSource b = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            new HashSet<>(Arrays.asList(1, 2)));

        Assert.assertThat(a, equalTo(b));
        Assert.assertThat(a.hashCode(), equalTo(b.hashCode()));
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesMatchBlacklistInDifferentOrder(){
        RealNumberFieldValueSource a = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            new HashSet<>(Arrays.asList(1, 2)));
        RealNumberFieldValueSource b = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            new HashSet<>(Arrays.asList(2, 1)));

        Assert.assertThat(a, equalTo(b));
        Assert.assertThat(a.hashCode(), equalTo(b.hashCode()));
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesMatchBlacklistEmpty(){
        RealNumberFieldValueSource a = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            Collections.emptySet());
        RealNumberFieldValueSource b = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            Collections.emptySet());

        Assert.assertThat(a, equalTo(b));
        Assert.assertThat(a.hashCode(), equalTo(b.hashCode()));
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesExceptMinMatch(){
        RealNumberFieldValueSource a = new RealNumberFieldValueSource(
            numericRestrictions(5, 10, 1),
            new HashSet<>(Arrays.asList(1, 2)));
        RealNumberFieldValueSource b = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            new HashSet<>(Arrays.asList(1, 2)));

        Assert.assertThat(a, not(equalTo(b)));
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesExceptMaxMatch(){
        RealNumberFieldValueSource a = new RealNumberFieldValueSource(
            numericRestrictions(1, 20, 1),
            new HashSet<>(Arrays.asList(1, 2)));
        RealNumberFieldValueSource b = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            new HashSet<>(Arrays.asList(1, 2)));

        Assert.assertThat(a, not(equalTo(b)));
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesExceptBlacklistMatch(){
        RealNumberFieldValueSource a = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            new HashSet<>(Arrays.asList(1, 2)));
        RealNumberFieldValueSource b = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            new HashSet<>(Arrays.asList(3, 4)));

        Assert.assertThat(a, not(equalTo(b)));
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesExceptScaleMatch(){
        RealNumberFieldValueSource a = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            new HashSet<>(Arrays.asList(1, 2)));
        RealNumberFieldValueSource b = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 2),
            new HashSet<>(Arrays.asList(1, 2)));

        Assert.assertThat(a, not(equalTo(b)));
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesExceptMinAndMaxMatch(){
        RealNumberFieldValueSource a = new RealNumberFieldValueSource(
            numericRestrictions(5, 20, 1),
            new HashSet<>(Arrays.asList(1, 2)));
        RealNumberFieldValueSource b = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 1),
            new HashSet<>(Arrays.asList(1, 2)));

        Assert.assertThat(a, not(equalTo(b)));
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesDontMatch(){
        RealNumberFieldValueSource a = new RealNumberFieldValueSource(
            numericRestrictions(5, 20, 1),
            new HashSet<>(Arrays.asList(1, 2)));
        RealNumberFieldValueSource b = new RealNumberFieldValueSource(
            numericRestrictions(1, 10, 2),
            new HashSet<>(Arrays.asList(3, 4)));

        Assert.assertThat(a, not(equalTo(b)));
    }

    @Test
    public void interestingValuesInclusively_UpperLimitLargerThanConfig_IncludesConfigMax() {
        givenLowerBound(-10, true);
        givenUpperBound(1e30, true);
        givenScale(0);

        expectInterestingValues(new BigDecimal("1e20"), "99999999999999999999", "0", "-9", "-10");
    }

    @Test
    public void exhaustiveValuesInclusively_UpperLimitLargerThanConfig_IncludesConfigMax() {
        givenLowerBound(new BigDecimal("99999999999999999995"), true);
        givenUpperBound(1e30, true);
        givenScale(0);

        expectAllValues(new BigDecimal("1e20"), "99999999999999999999",
            "99999999999999999998", "99999999999999999997", "99999999999999999996", "99999999999999999995");
    }

    @Test
    public void interestingValuesExclusively_UpperLimitLargerThanConfig_IncludesConfigMaxMinusOne() {
        givenLowerBound(-10, false);
        givenUpperBound(1e30, false);
        givenScale(0);

        expectInterestingValues("99999999999999999999", "99999999999999999998", "0", "-8", "-9");
    }

    @Test
    public void exhaustiveValuesExclusively_UpperLimitLargerThanConfig_IncludesConfigMaxMinusOne() {
        givenLowerBound(new BigDecimal("99999999999999999995"), false);
        givenUpperBound(1e30, false);
        givenScale(0);

        expectAllValues("99999999999999999999", "99999999999999999998", "99999999999999999997",
            "99999999999999999996");
    }

    @Test
    public void interestingValuesInclusively_LowerLimitSmallerThanConfig_IncludesConfigMin() {
        givenLowerBound(-1e30, true);
        givenUpperBound(10, true);
        givenScale(0);

        expectInterestingValues(new BigDecimal("-1e20"), "-99999999999999999999", "0", "9", "10");
    }

    @Test
    public void exhaustiveValuesInclusively_LowerLimitSmallerThanConfig_IncludesConfigMin() {
        givenLowerBound(-1e30, true);
        givenUpperBound(new BigDecimal("-99999999999999999995"), true);
        givenScale(0);

        expectAllValues(new BigDecimal("-1e20"), "-99999999999999999999",
            "-99999999999999999998", "-99999999999999999997", "-99999999999999999996", "-99999999999999999995");
    }

    @Test
    public void interestingValuesExclusively_LowerLimitSmallerThanConfig_IncludesConfigMinPlusOne() {
        givenLowerBound(-1e30, false);
        givenUpperBound(10, false);
        givenScale(0);

        expectInterestingValues("-99999999999999999999", "-99999999999999999998", "0", "8", "9");
    }

    @Test
    public void exhaustiveValuesExclusively_LowerLimitSmallerThanConfig_IncludesConfigMinPlusOne() {
        givenLowerBound(-1e30, false);
        givenUpperBound(new BigDecimal("-99999999999999999995"), false);
        givenScale(0);

        expectAllValues( "-99999999999999999999", "-99999999999999999998", "-99999999999999999997",
            "-99999999999999999996");
    }

    private NumericRestrictions numericRestrictions(Integer min, Integer max, int scale){
        NumericRestrictions restrictions = new NumericRestrictions(scale);
        restrictions.min = min == null ? null : new NumericLimit<>(BigDecimal.valueOf(min), true);
        restrictions.max = max == null ? null : new NumericLimit<>(BigDecimal.valueOf(max), true);
        return restrictions;
    }

    private NumericLimit<BigDecimal> upperLimit;
    private NumericLimit<BigDecimal> lowerLimit;
    private int scale;
    private Set<Object> blacklist;
    private RealNumberFieldValueSource objectUnderTest;

    private void givenLowerBound(Object limit, boolean isInclusive) {
        givenLowerBound(NumberUtils.coerceToBigDecimal(limit), isInclusive);
    }
    private void givenLowerBound(BigDecimal limit, boolean isInclusive) {
        this.lowerLimit = new NumericLimit<>(limit, isInclusive);
    }

    private void givenUpperBound(Object limit, boolean isInclusive) {
        givenUpperBound(NumberUtils.coerceToBigDecimal(limit), isInclusive);
    }
    private void givenUpperBound(BigDecimal limit, boolean isInclusive) {
        this.upperLimit = new NumericLimit<>(limit, isInclusive);
    }

    private void givenScale(int scale) {
        this.scale = scale;
    }

    private void givenBlacklist(Object... values) {
        blacklist = new HashSet<>(Arrays.asList(values));
    }
    private void expectAllValues(Object... expectedValuesArray) {
        expectValues(getObjectUnderTest().generateAllValues(), true, expectedValuesArray);
    }

    private void expectInterestingValues(Object... expectedValuesArray) {
        expectValues(getObjectUnderTest().generateInterestingValues(), false, expectedValuesArray);
    }

    private void expectValues(Iterable<Object> values, boolean assertCount, Object... expectedValuesArray) {
        Collection<Matcher<? super BigDecimal>> expectedValuesMatchers = Stream.of(expectedValuesArray)
            .map(NumberUtils::coerceToBigDecimal)
            .map(Matchers::comparesEqualTo) // we have to use compare otherwise it fails if the scale is different
            .collect(Collectors.toList());

        BigDecimal[] actualValues = StreamSupport
            .stream(values.spliterator(), false)
            .toArray(BigDecimal[]::new);

        // ASSERT
        if (assertCount) {
            expectValueCount(expectedValuesArray.length);
            expectFinite();
        }

        Assert.assertThat(actualValues, arrayContainingInAnyOrder(expectedValuesMatchers));
    }

    private void expectNoValues() {
        expectAllValues();
    }

    private FieldValueSource getObjectUnderTest() {
        if (objectUnderTest == null) {
            NumericRestrictions restrictions = new NumericRestrictions(scale);
            restrictions.max = upperLimit;
            restrictions.min = lowerLimit;
            objectUnderTest = new RealNumberFieldValueSource(restrictions, blacklist);
        }

        return objectUnderTest;
    }

    private void expectFinite() {
        Assert.assertTrue(getObjectUnderTest().isFinite());
    }

    private void expectValueCount(int expectedCount) {
        Assert.assertThat(
            getObjectUnderTest().getValueCount(),
            equalTo((long)expectedCount));
    }

    private void expectCorrectRandomValues() {
        Iterable<Object> resultsIterable = getObjectUnderTest().generateRandomValues(new JavaUtilRandomNumberGenerator(0));

        Set<BigDecimal> decimalBlacklist = blacklist
            .stream()
            .map(NumberUtils::coerceToBigDecimal)
            .map(value -> value.setScale(scale, RoundingMode.HALF_UP))
            .collect(Collectors.toCollection(TreeSet::new));

        StreamSupport
            .stream(resultsIterable.spliterator(), false)
            .limit(1000)
            .map(value -> (BigDecimal)value)
            .forEach(value ->
            {
                // Not sure if this is the most efficient way to test all these values,
                // I think it'll do for now though.
                Assert.assertThat(
                    lowerLimit.getLimit(),
                    lowerLimit.isInclusive()
                        ? lessThanOrEqualTo(value)
                        : lessThan(value));

                Assert.assertThat(
                    value,
                    upperLimit.isInclusive()
                        ? lessThanOrEqualTo(upperLimit.getLimit())
                        : lessThan(upperLimit.getLimit()));

                if (decimalBlacklist.size() != 0) {
                    Assert.assertFalse(decimalBlacklist.contains(value));
                }

                Assert.assertThat(value.scale(), equalTo(scale));
            });
    }

    @BeforeEach
    void beforeEach() {
        this.upperLimit = null;
        this.lowerLimit = null;
        this.scale = 0;
        this.blacklist = Collections.emptySet();
        this.objectUnderTest = null;
    }
}

