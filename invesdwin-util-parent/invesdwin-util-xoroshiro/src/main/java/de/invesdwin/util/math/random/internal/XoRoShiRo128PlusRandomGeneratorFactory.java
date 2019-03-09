package de.invesdwin.util.math.random.internal;

import javax.annotation.concurrent.Immutable;

import it.unimi.dsi.util.XoRoShiRo128PlusRandomGenerator;

@Immutable
public final class XoRoShiRo128PlusRandomGeneratorFactory {

    private XoRoShiRo128PlusRandomGeneratorFactory() {}

    public static XoRoShiRo128PlusRandomGenerator newInstance() {
        return new XoRoShiRo128PlusRandomGenerator();
    }

}
