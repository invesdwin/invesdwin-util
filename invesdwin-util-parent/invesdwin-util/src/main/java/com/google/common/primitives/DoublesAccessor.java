package com.google.common.primitives;

import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DoublesAccessor {
    public static final Pattern FLOATING_POINT_PATTERN = Doubles.FLOATING_POINT_PATTERN;

    private DoublesAccessor() {}
}
