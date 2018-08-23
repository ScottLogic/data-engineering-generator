package com.scottlogic.deg.generator.restrictions;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RowSpecMerger {
    private final FieldSpecMerger fieldSpecMerger;

    public RowSpecMerger(FieldSpecMerger fieldSpecMerger) {
        this.fieldSpecMerger = fieldSpecMerger;
    }

    public Optional<RowSpec> merge(Collection<RowSpec> rowSpecs) {
        return RowSpec.merge(
                fieldSpecMerger,
                rowSpecs
        );
    }
}
