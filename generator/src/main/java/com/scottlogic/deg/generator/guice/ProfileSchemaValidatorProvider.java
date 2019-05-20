package com.scottlogic.deg.generator.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.scottlogic.deg.generator.generation.GenerationConfigSource;
import com.scottlogic.deg.profile.v0_1.NoopProfileSchemaValidator;
import com.scottlogic.deg.profile.v0_1.ProfileSchemaValidator;
import com.scottlogic.deg.profile.v0_1.ProfileSchemaValidatorLeadPony;

public class ProfileSchemaValidatorProvider implements Provider<ProfileSchemaValidator> {

    private final GenerationConfigSource configSource;
    private final ProfileSchemaValidatorLeadPony leadPonyValidator;

    @Inject
    public ProfileSchemaValidatorProvider(GenerationConfigSource configSource, ProfileSchemaValidatorLeadPony leadPonyValidator) {
        this.configSource = configSource;
        this.leadPonyValidator = leadPonyValidator;
    }

    @Override
    public ProfileSchemaValidator get() {
        if (!configSource.isSchemaValidationEnabled()) {
            return new NoopProfileSchemaValidator();
        }

        return leadPonyValidator;
    }
}
