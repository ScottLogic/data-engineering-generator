package com.scottlogic.deg.schemas.v3;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collection;

@JsonDeserialize(using = RuleDeserializer.class)
@JsonSerialize(using = RuleSerializer.class)
public class RuleDTO {
    public String rule;
    public Collection<ConstraintDTO> constraints;

    public RuleDTO() {}

    public RuleDTO(String rule, Collection<ConstraintDTO> constraints){
        this.rule = rule;
        this.constraints = constraints;
    }
}
