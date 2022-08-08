package de.invesdwin.util.collections;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.AMapsStaticFacade;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.AMapsStaticFacade", targets = {
        org.apache.commons.collections4.MapUtils.class, com.google.common.collect.Maps.class })
public final class Maps extends AMapsStaticFacade {

    private Maps() {
    }

}