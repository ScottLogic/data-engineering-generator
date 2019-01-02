package com.scottlogic.deg.generator.cucumber.steps;

import com.scottlogic.deg.generator.cucumber.utils.GeneratorTestUtilities;
import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.schemas.v3.AtomicConstraintType;
import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.cucumberexpressions.Transformer;

import java.util.*;
import java.util.stream.Collectors;

public class TypeRegistryConfiguration implements TypeRegistryConfigurer {

    private final Set<AtomicConstraintType> allOperators = new HashSet<>(Arrays.asList(AtomicConstraintType.values()));

    @Override
    public Locale locale(){
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry tr) {
        this.defineDataGenerationStrategyType(tr);
        this.defineCombinationStrategyType(tr);
        this.defineOperationParameterType(tr);
        this.defineParameterType(tr,"fieldVar", "^(.+)");
        this.defineParameterType(tr,"regex", "/(.+)/$");

        tr.defineParameterType(new ParameterType<>(
            "number",
            "(-?\\d+(\\.\\d+)?)$",
            Number.class,
            (Transformer<Number>) value -> (Number) GeneratorTestUtilities.parseNumber(value)));

        tr.defineParameterType(new ParameterType<>(
            "boolean",
            "(true|false)$",
            Boolean.class,
            (Transformer<Boolean>) Boolean::valueOf));

        tr.defineParameterType(new ParameterType<>(
            "date",
            DateValueStep.DATE_REGEX,
            DateObject.class,
            DateObject::new));
    }

    private void defineOperationParameterType(TypeRegistry tr){
        tr.defineParameterType(new ParameterType<>(
            "operator",
            this.getHumanReadableOperationRegex(allOperators),
            String.class,
            this::extractConstraint
        ));
    }

    private void defineParameterType(TypeRegistry tr, String name, String regex) {
        tr.defineParameterType(new ParameterType<>(
            name,
            regex,
            String.class,
            (Transformer<String>)fieldName -> fieldName));
    }

    private void defineDataGenerationStrategyType(TypeRegistry tr){
        Transformer<GenerationConfig.DataGenerationType> transformer = strategyString ->
            Arrays.stream(GenerationConfig.DataGenerationType.values())
            .filter(val -> val.toString().equalsIgnoreCase(strategyString))
            .findFirst().orElse(GenerationConfig.DataGenerationType.FULL_SEQUENTIAL);

        tr.defineParameterType(new ParameterType<>(
            "generationStrategy",
            "(.*)$",
            GenerationConfig.DataGenerationType.class,
            transformer));
    }

    private void defineCombinationStrategyType(TypeRegistry tr){
        Transformer<GenerationConfig.CombinationStrategyType> transformer = strategyString ->
            Arrays.stream(GenerationConfig.CombinationStrategyType.values())
                .filter(val -> val.toString().equalsIgnoreCase(strategyString))
                .findFirst().orElse(GenerationConfig.CombinationStrategyType.PINNING);

        tr.defineParameterType(new ParameterType<>(
            "combinationStrategy",
            "(.*)$",
            GenerationConfig.CombinationStrategyType.class,
            transformer));
    }

    private String extractConstraint(String gherkinConstraint) {
        List<String> allConstraints = Arrays.asList(gherkinConstraint.split(" "));
        return allConstraints.get(0) + allConstraints
            .stream()
            .skip(1)
            .map(value -> value.substring(0, 1).toUpperCase() + value.substring(1))
            .collect(Collectors.joining());
    }

    private String getHumanReadableOperationRegex(Set<AtomicConstraintType> types){
        return
            types.stream()
            .map(act -> act.toString().replaceAll("([a-z])([A-Z]+)", "$1 $2").toLowerCase())
            .collect(Collectors.joining("|", "(", ")"));
    }

}
