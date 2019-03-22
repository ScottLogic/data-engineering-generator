package com.scottlogic.deg.generator.generation.fieldvaluesources.DateTime;

import com.scottlogic.deg.generator.generation.fieldvaluesources.FieldValueSource;
import com.scottlogic.deg.generator.restrictions.DateTimeRestrictions;
import com.scottlogic.deg.generator.utils.FilteringIterator;
import com.scottlogic.deg.generator.utils.RandomNumberGenerator;
import com.scottlogic.deg.generator.utils.UpCastingIterator;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TemporalFieldValueSource implements FieldValueSource {

    public static final LocalDateTime ISO_MAX_DATE = LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999_999_999);
    public static final LocalDateTime ISO_MIN_DATE = LocalDateTime.of(1, 1, 1, 0, 0);

    private final ChronoUnit granularity = ChronoUnit.MILLIS;
    private final DateTimeRestrictions restrictions;
    private final Set<Object> blacklist;
    private final LocalDateTime inclusiveLower;
    private final LocalDateTime exclusiveUpper;

    public TemporalFieldValueSource(
            DateTimeRestrictions restrictions,
            Set<Object> blacklist) {

        this.restrictions = restrictions;

        this.inclusiveLower = getInclusiveLowerBounds(restrictions);
        this.exclusiveUpper = getExclusiveUpperBound(restrictions);

        this.blacklist = blacklist;
    }

    @Override
    public boolean isFinite() {
        return restrictions.min != null &&
                restrictions.min.getLimit() != null &&
                restrictions.max != null &&
                restrictions.max.getLimit() != null;
    }

    @Override
    public long getValueCount() {

        if (isFinite()) {
            Duration duration = Duration.between(inclusiveLower, exclusiveUpper);
            Period period = Period.between(inclusiveLower.toLocalDate(), exclusiveUpper.toLocalDate());

            if (granularity == ChronoUnit.MILLIS) return (duration.getNano() / 1000000) +1;
            if (granularity == ChronoUnit.SECONDS) return duration.getSeconds() + 1;
            if (granularity == ChronoUnit.MINUTES) return (duration.getSeconds() / 60) + 1;
            if (granularity == ChronoUnit.HOURS) return (duration.getSeconds() / 360) + 1;
            if (granularity == ChronoUnit.DAYS) return (period.getDays() + 1);
            if (granularity == ChronoUnit.MONTHS) return (period.getMonths() + 1);
            if (granularity == ChronoUnit.YEARS) return (period.getYears() + 1);
        }

        throw new IllegalStateException("Cannot get count of an infinite series");
    }

    @Override
    public Iterable<Object> generateAllValues() {
        return () -> new UpCastingIterator<>(
            new FilteringIterator<>(
                new SequentialDateIterator(
                    inclusiveLower != null ? inclusiveLower : ISO_MIN_DATE,
                    exclusiveUpper != null ? exclusiveUpper : ISO_MAX_DATE,
                    granularity),
                i -> !blacklist.contains(i)));
    }

    @Override
    public Iterable<Object> generateInterestingValues() {

        ArrayList<Object> interestingValues = new ArrayList<>();

        if (restrictions.min != null && restrictions.min.getLimit() != null) {
            LocalDateTime min = restrictions.min.getLimit();
            interestingValues.add(restrictions.min.isInclusive() ? min : min.plusNanos(1_000_000));
        } else {
            interestingValues.add(LocalDateTime.of(
                    LocalDate.of(1900, 01, 01),
                    LocalTime.MIDNIGHT));
        }

        if (restrictions.max != null && restrictions.max.getLimit() != null) {
            LocalDateTime max = restrictions.max.getLimit();
            interestingValues.add(restrictions.max.isInclusive() ? max : max.minusNanos(1_000_000));
        } else {
            interestingValues.add(LocalDateTime.of(
                    LocalDate.of(2100, 01, 01),
                    LocalTime.MIDNIGHT));
        }

        return () -> new UpCastingIterator<>(
                new FilteringIterator<>(interestingValues.iterator(),
                        i -> !blacklist.contains(i)));
    }

    @Override
    public Iterable<Object> generateRandomValues(RandomNumberGenerator randomNumberGenerator) {

        LocalDateTime lower = inclusiveLower != null
                ? inclusiveLower
                : ISO_MIN_DATE;


        LocalDateTime upper = exclusiveUpper != null
                ? exclusiveUpper
                : ISO_MAX_DATE.plusNanos(1_000_000);


        return () -> new UpCastingIterator<>(
                new FilteringIterator<>(new RandomDateIterator(lower, upper, randomNumberGenerator),
                        i -> !blacklist.contains(i)));

    }

    private LocalDateTime getExclusiveUpperBound(DateTimeRestrictions upper) {
        if (upper.max == null || upper.max.getLimit() == null) return null;
        return upper.max.isInclusive() ? upper.max.getLimit().plusNanos(1_000_000) : upper.max.getLimit();
    }

    private LocalDateTime getInclusiveLowerBounds(DateTimeRestrictions lower) {
        if (lower.min == null || lower.min.getLimit() == null) return null;
        return lower.min.isInclusive() ? lower.min.getLimit() : lower.min.getLimit().plusNanos(1_000_000);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TemporalFieldValueSource otherSource = (TemporalFieldValueSource) obj;
        return restrictions.equals(otherSource.restrictions) &&
            blacklist.equals(otherSource.blacklist) &&
            equals(inclusiveLower, otherSource.inclusiveLower) &&
            equals(exclusiveUpper, otherSource.exclusiveUpper);
    }

    private static boolean equals(LocalDateTime x, LocalDateTime y){
        if (x == null && y == null) {
            return true;
        }

        if (x == null || y == null) {
            return false; //either x OR y is null, but not both (XOR)
        }

        return x.equals(y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restrictions, blacklist, inclusiveLower, exclusiveUpper);
    }
}
