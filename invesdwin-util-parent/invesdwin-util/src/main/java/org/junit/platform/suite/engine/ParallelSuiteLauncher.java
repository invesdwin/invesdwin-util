/*
 * Copyright 2015-2024 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v2.0 which accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.suite.engine;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.EngineDiscoveryOrchestrator;
import org.junit.platform.launcher.core.EngineDiscoveryOrchestrator.Phase;
import org.junit.platform.launcher.core.EngineExecutionOrchestrator;
import org.junit.platform.launcher.core.LauncherDiscoveryResult;
import org.junit.platform.launcher.core.ServiceLoaderTestEngineRegistry;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

// @Immutable
final class ParallelSuiteLauncher {

    private final EngineExecutionOrchestrator executionOrchestrator = new EngineExecutionOrchestrator();
    private final EngineDiscoveryOrchestrator discoveryOrchestrator;

    private ParallelSuiteLauncher(final Set<TestEngine> testEngines) {
        org.junit.platform.commons.util.Preconditions.condition(hasTestEngineOtherThanSuiteEngine(testEngines),
                () -> "Cannot create ParallelSuiteLauncher without at least one other TestEngine; "
                        + "consider adding an engine implementation JAR to the classpath");
        this.discoveryOrchestrator = new EngineDiscoveryOrchestrator(testEngines, java.util.Collections.emptyList());
    }

    static ParallelSuiteLauncher create() {
        final Set<TestEngine> engines = new LinkedHashSet<>();
        new ServiceLoaderTestEngineRegistry().loadTestEngines().forEach(engines::add);
        return new ParallelSuiteLauncher(engines);
    }

    private boolean hasTestEngineOtherThanSuiteEngine(final Set<TestEngine> testEngines) {
        return testEngines.stream()
                .anyMatch(testEngine -> !ParallelSuiteEngineDescriptor.ENGINE_ID.equals(testEngine.getId()));
    }

    LauncherDiscoveryResult discover(final LauncherDiscoveryRequest discoveryRequest, final UniqueId parentId) {
        return discoveryOrchestrator.discover(discoveryRequest, Phase.DISCOVERY, parentId);
    }

    TestExecutionSummary execute(final LauncherDiscoveryResult discoveryResult,
            final EngineExecutionListener parentEngineExecutionListener) {
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();
        executionOrchestrator.execute(discoveryResult, parentEngineExecutionListener, listener);
        return listener.getSummary();
    }

}
