package com.scottlogic.deg.generator.cucumber.steps;

import cucumber.api.java.en.When;

import java.math.BigDecimal;

public class NumericValueStep {

    private DegTestState state;
    public NumericValueStep(DegTestState state){
        this.state = state;
    }

    @When("{fieldVar} is {numericValueOperation} {int}")
    public void whenFieldIsConstrainedByNumericValue(String fieldName, String constraintName, int value) throws Exception {
        this.state.addConstraint(fieldName, constraintName, value);
    }

    @When("{fieldVar} is not {numericValueOperation} {int}")
    public void whenFieldIsNotConstrainedByNumericValue(String fieldName, String constraintName, int value) throws Exception {
        this.state.addNotConstraint(fieldName, constraintName, value);
    }

    @When("{fieldVar} is {numericValueOperation} {double}")
    public void whenFieldIsConstrainedByNumericValue(String fieldName, String constraintName, double value) throws Exception {
        this.state.addConstraint(fieldName, constraintName, convertDoubleToBigDecimal(value));
    }

    @When("{fieldVar} is not {numericValueOperation} {double}")
    public void whenFieldIsNotConstrainedByNumericValue(String fieldName, String constraintName, double value) throws Exception {
        this.state.addNotConstraint(fieldName, constraintName, convertDoubleToBigDecimal(value));
    }

    private BigDecimal convertDoubleToBigDecimal(double value){
        return new BigDecimal(String.format("%f", value)).stripTrailingZeros();
    }
}
