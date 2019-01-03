package com.scottlogic.deg.generator.walker.reductive;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.generation.ReductiveDataGeneratorMonitor;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.restrictions.NullRestrictions;
import com.scottlogic.deg.generator.restrictions.Nullness;
import com.scottlogic.deg.generator.restrictions.SetRestrictions;
import com.scottlogic.deg.generator.walker.ReductiveDecisionTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;

public class FixedField {
    Logger logger = LogManager.getLogger(FixedField.class);
    private static final Object NOT_ITERATED = new NotIterated();

    public final Field field;

    private final Stream<Object> values;
    private final FieldSpec valuesFieldSpec;

    private Object current = NOT_ITERATED;
    private FieldSpec fieldSpec;

    FixedField(
        Field field,
        Stream<Object> values,
        FieldSpec valuesFieldSpec) {
        this.field = field;
        this.values = values;
        this.valuesFieldSpec = valuesFieldSpec;
    }

    public Stream<Object> getStream() {
        return this.values
            .peek(value -> {
                this.current = value;
                this.fieldSpec = null;

                logger.debug("Field [{}] = {}", field.name, current);
            });
    }

    public FieldSpec getFieldSpecForValues(){
        return this.valuesFieldSpec;
    }

    @Override
    public String toString() {
        return this.current == NOT_ITERATED
            ? this.field.name
            : String.format("[%s] = %s", this.field.name, this.current);
    }

    FieldSpec getFieldSpecForCurrentValue(){
        if (this.fieldSpec != null) {
            return this.fieldSpec;
        }

        return this.fieldSpec = current == null
            ? getNullRequiredFieldSpec()
            : getFieldSpecForCurrentValue(current);
    }

    private FieldSpec getFieldSpecForCurrentValue(Object currentValue) {
        if (currentValue == NOT_ITERATED){
            throw new UnsupportedOperationException("FixedField has not iterated yet");
        }

        return FieldSpec.Empty.withSetRestrictions(
            new SetRestrictions(new HashSet<>(Collections.singletonList(currentValue)), null),
            this.valuesFieldSpec.getFieldSpecSource()
        );
    }

    private FieldSpec getNullRequiredFieldSpec() {
        return FieldSpec.Empty
        .withNullRestrictions(
            new NullRestrictions(Nullness.MUST_BE_NULL),
            this.valuesFieldSpec.getFieldSpecSource()
        );
    }

    private static class NotIterated { }
}
