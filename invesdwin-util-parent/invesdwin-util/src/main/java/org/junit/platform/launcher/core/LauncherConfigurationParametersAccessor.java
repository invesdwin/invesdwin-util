package org.junit.platform.launcher.core;

import java.util.Map;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.launcher.core.LauncherConfigurationParameters.Builder;

// @Immutable
public final class LauncherConfigurationParametersAccessor {

    private LauncherConfigurationParametersAccessor() {}

    public static LauncherConfigurationParameters overrideParamters(final ConfigurationParameters parent,
            final Map<String, String> overrideParameters) {
        final Builder builder = LauncherConfigurationParameters.builder();
        if (parent != null) {
            builder.parentConfigurationParameters(parent);
        }
        return builder.explicitParameters(overrideParameters).enableImplicitProviders(false).build();
    }

}
