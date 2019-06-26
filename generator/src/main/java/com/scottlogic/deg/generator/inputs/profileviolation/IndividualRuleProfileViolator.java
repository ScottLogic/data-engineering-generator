package com.scottlogic.deg.generator.inputs.profileviolation;

import com.google.inject.Inject;
import com.scottlogic.deg.common.profile.Profile;
import com.scottlogic.deg.common.profile.Rule;
import com.scottlogic.deg.common.profile.ViolatedProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines a profile violator which violates by violating each rule independently.
 * Within each violated ruleInformation we violate each constraint independently. This is consistent with the current
 * implementation of violation.
 */
public class IndividualRuleProfileViolator implements ProfileViolator {

    private final RuleViolator ruleViolator;

    @Inject
    public IndividualRuleProfileViolator(RuleViolator ruleViolator) {
        this.ruleViolator = ruleViolator;
    }

    /**
     * Takes an input profile and returns a list of violated profiles with each rule violated individually in its own
     * profile. Additionally writes the manifest of the produced violated profiles.
     *
     * Note that manifest writing happens here since the writer requires a list ViolatedProfile objects and the return
     * of this method loses this context to make the generation code cleaner afterwards.
     *
     * @param profile Input profile.
     * @return List of profiles each with a different rule violated.
     * @throws IOException if the manifest writer fails to write
     */
    @Override
    public List<ViolatedProfile> violate(Profile profile) throws IOException {
        // For each rule in the profile generate a profile with this one rule violated
        List<ViolatedProfile> violatedProfiles =
            profile.getRules().stream()
                .map(rule -> violateRuleOnProfile(profile, rule))
                .collect(Collectors.toList());

        // The following will allow the conversion to a List<Profile> from a List<ViolatedProfile>.
        return new ArrayList<>(violatedProfiles);
    }

    /**
     * Given a profile and the rule to violate on that profile produce a profile with this one rule violated.
     *
     * @param profile      Input un-violated profile
     * @param violatedRule Rule to violate
     * @return New profile with the specified rule violated and all other rules untouched.
     */
    private ViolatedProfile violateRuleOnProfile(Profile profile, Rule violatedRule) {
        Collection<Rule> newRules = profile.getRules().stream()
            .map(r -> r == violatedRule
                ? ruleViolator.violateRule(violatedRule)
                : r)
            .collect(Collectors.toList());

        return new ViolatedProfile(
            violatedRule,
            profile.getFields(),
            newRules,
            String.format("%s -- Violating: %s", profile.getDescription(), violatedRule.getRuleInformation().getDescription()));
    }
}
