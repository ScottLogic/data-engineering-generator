package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.generator.constraints.atomic.IsOfTypeConstraint;
import com.scottlogic.deg.generator.restrictions.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetRestrictionsMergeOperation implements RestrictionMergeOperation {
    private static final SetRestrictionsMerger setRestrictionsMerger = new SetRestrictionsMerger();

    @Override
    public Optional<FieldSpec> applyMergeOperation(FieldSpec left, FieldSpec right, FieldSpec merged) {
        MergeResult<SetRestrictions> mergeResult =
            setRestrictionsMerger.merge(left.getSetRestrictions(), right.getSetRestrictions());

        if (!mergeResult.successful){
            if (left.getNullRestrictions() != null && left.getNullRestrictions().nullness.equals(Nullness.MUST_NOT_BE_NULL)
                || right.getNullRestrictions() != null && right.getNullRestrictions().nullness.equals(Nullness.MUST_NOT_BE_NULL)) {
                return Optional.empty();
            }
            return Optional.of(merged.withSetRestrictions(
                SetRestrictions.fromWhitelist(Collections.emptySet()),
                FieldSpecSource.fromFieldSpecs(left, right)));
        }

        SetRestrictions setRestrictions = mergeResult.restrictions;

        // Filter the set to match any new restrictions
        if (setRestrictions != null &&
            setRestrictions.getWhitelist() != null &&
            !setRestrictions.getWhitelist().isEmpty()) {

            Stream<?> filterStream = setRestrictions.getWhitelist().stream();
            TypeRestrictions typeRestrictions = merged.getTypeRestrictions();

            if (!typeRestrictions.isTypeAllowed(IsOfTypeConstraint.Types.NUMERIC)) {
                filterStream = filterStream.filter(x -> !NumericRestrictions.isNumeric(x));
            }

            if (!typeRestrictions.isTypeAllowed(IsOfTypeConstraint.Types.STRING)) {
                filterStream = filterStream.filter(x -> !StringRestrictions.isString(x));
            }

            if (!typeRestrictions.isTypeAllowed(IsOfTypeConstraint.Types.TEMPORAL)) {
                filterStream = filterStream.filter(x -> !DateTimeRestrictions.isDateTime(x));
            }

            StringRestrictions stringRestrictions = merged.getStringRestrictions();
            if(stringRestrictions != null){
                filterStream = filterStream.filter(stringRestrictions::match);
            }

            NumericRestrictions numberRestrictions = merged.getNumericRestrictions();
            if(numberRestrictions != null){
                filterStream = filterStream.filter(numberRestrictions::match);
            }

            DateTimeRestrictions dateTimeRestrictions = merged.getDateTimeRestrictions();
            if(dateTimeRestrictions != null){
                filterStream = filterStream.filter(dateTimeRestrictions::match);
            }

            SetRestrictions newSetRestrictions = new SetRestrictions(filterStream.collect(Collectors.toCollection(HashSet::new)),
                setRestrictions.getBlacklist());

            setRestrictions = newSetRestrictions;
        }

        return Optional.of(merged.withSetRestrictions(
            setRestrictions,
            FieldSpecSource.fromFieldSpecs(left, right)));
    }
}

