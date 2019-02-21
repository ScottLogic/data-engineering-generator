package com.scottlogic.deg.generator.walker.reductive.field_selection_strategy;

import com.google.inject.Inject;
import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.Guice.CurrentProfileCache;
import com.scottlogic.deg.generator.analysis.FieldDependencyAnalyser;
import com.scottlogic.deg.generator.analysis.FieldDependencyAnalysisResult;

import java.util.Comparator;

public final class HierarchicalDependencyFixFieldStrategy extends ProfileBasedFixFieldStrategy {
    private final SetBasedFixFieldStrategy setBasedFixFieldStrategy;
    private final FieldDependencyAnalyser analyser;

    @Inject
    public HierarchicalDependencyFixFieldStrategy(FieldDependencyAnalyser analyser) {
        this.analyser = analyser;

        this.setBasedFixFieldStrategy = new SetBasedFixFieldStrategy();
    }

    Comparator<Field> getFieldOrderingStrategy() {
        FieldDependencyAnalysisResult result = this.analyser.analyse(profile);
        Comparator<Field> firstComparison = Comparator.comparingInt(field -> result.getDependenciesOf(field).size());
        Comparator<Field> secondComparison = Comparator.comparingInt((Field field) -> result.getDependentsOf(field).size()).reversed();
        return firstComparison.thenComparing(secondComparison)
            .thenComparing(setBasedFixFieldStrategy.getFieldOrderingStrategy())
            .thenComparing(field -> field.name);
    }

}
