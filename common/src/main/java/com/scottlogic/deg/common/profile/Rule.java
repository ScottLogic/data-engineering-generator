package com.scottlogic.deg.common.profile;

import com.scottlogic.deg.common.profile.constraints.Constraint;

import java.util.Collection;

public class Rule
{
    private final RuleInformation ruleInformation;
    private final Collection<Constraint> constraints;

    public Rule(RuleInformation ruleInformation, Collection<Constraint> constraints)
    {
        this.ruleInformation = ruleInformation;
        this.constraints = constraints;
    }

    public RuleInformation getRuleInformation() {
        return ruleInformation;
    }

    public Collection<Constraint> getConstraints() {
        return constraints;
    }

}

