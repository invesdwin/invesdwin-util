package de.invesdwin.util.swing;

import javax.annotation.concurrent.ThreadSafe;
import javax.swing.JLabel;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

/**
 * Pins Components.abbreviateToFit: the text must never change when it fits, and when it
 * does not, the abbreviation must fit the given pixel width while keeping both the
 * leading identity and a meaningful trailing part.
 */
@ThreadSafe
public class ComponentsTest {

    private static final JLabel LABEL = new JLabel();

    //the shape of a long machine-generated label: a key string carrying a settings dump
    private static final String LONG_TEXT = "Loading precalculated values for MODEL_InnerEvaluator"
            + "/SESSION[08:00:00.000.000.000.000-22:00:00.000.000.000.000 | Europe/Berlin]:FEED:SYMBOL"
            + "/Time[FIVE_MINS]_OHLC_null/TIME_BARS_generic/EffectBarCache["
            + "settings:EffectSettings[config:EffectDPConfig{filterRedundantPositions:true,"
            + "redundantPositionChecksAllSignals:true,smallerBarConfig:Time[ONE_MIN],barConfig:Time[FIVE_MINS],"
            + "useSessionFilter:true,spaceLeftBars:0,spaceRightBars:15,noise1Pips:10.0,cutOffPeriod:2000,"
            + "slopeEnabled:true,slopeBasePriceAndStepAbsolute:500.0,slopeBasePipsLong:2.1,slopeBasePipsShort:2.6,"
            + "slopeIncrementPips:0.1,slopeRounded:true,slopeModelIdOverride:null},"
            + "noise1Function:TableRootBarNoiseFunction[noiseTable:default@-1276938141],"
            + "scaling:ScalingSettings[config:ScalingDPConfig{noise:2.5,table:default,cutOffPeriod:2000,"
            + "atr:barsCount:14,barConfig:Time[ONE_HOUR],useSessionFilter:true,smoothingType:Smoothed or Modified"
            + " Moving Average},ignoreSignalBelowOrEqualToScaling:0.0,ignoreSignalAboveOrEqualToScaling:500.0],"
            + "ignoreOrderCommentEntryFilter:VolatilityEntryFilter[VolatilityFilterDPConfig{barConfig:"
            + "Time[DAILY],useSessionFilter:true,mode:MiddleEvaluator,bars:10,minimumAvgHighLowRange:80.0,"
            + "maximumAvgHighLowRange:NaN}]] | calculationEnabled:true"
            + " | defaultCalculationEnabled:true]_snapshotCache (23%) | ETA: PT14M47S";

    private static final String TRAILING_PROGRESS = "(23%) | ETA: PT14M47S";

    @Test
    public void testTextThatFitsIsNotChanged() {
        Assertions.assertThat(Components.abbreviateToFit(LABEL, "Initializing Strategy (64%)", 10000))
                .isEqualTo("Initializing Strategy (64%)");
        Assertions.assertThat(Components.abbreviateToFit(LABEL, LONG_TEXT, Integer.MAX_VALUE))
                .isEqualTo(LONG_TEXT);
    }

    @Test
    public void testUnknownLayoutKeepsTextUnchanged() {
        //maxWidth 0 means the layout is not known yet - the caller must not abbreviate
        //against a guessed width then
        Assertions.assertThat(Components.abbreviateToFit(LABEL, LONG_TEXT, 0)).isEqualTo(LONG_TEXT);
        Assertions.assertThat(Components.abbreviateToFit(LABEL, LONG_TEXT, -1)).isEqualTo(LONG_TEXT);
    }

    @Test
    public void testAbbreviationKeepsPrefixAndTrailingPart() {
        final String abbreviated = Components.abbreviateToFit(LABEL, LONG_TEXT, 600);
        Assertions.assertThat(abbreviated.length()).isLessThan(LONG_TEXT.length());
        Assertions.assertThat(abbreviated).contains("...");
        Assertions.assertThat(abbreviated).startsWith("Loading precalculated");
        Assertions.assertThat(abbreviated).endsWith(TRAILING_PROGRESS);
    }

    @Test
    public void testAbbreviationNeverExceedsTheGivenWidth() {
        for (int width = 80; width <= 1200; width += 20) {
            final String abbreviated = Components.abbreviateToFit(LABEL, LONG_TEXT, width);
            Assertions.assertThat(LABEL.getFontMetrics(LABEL.getFont()).stringWidth(abbreviated))
                    .describedAs("abbreviated to width " + width + ": " + abbreviated)
                    .isLessThanOrEqualTo(width);
        }
    }

    @Test
    public void testTextShorterThanTheMarkerIsReturnedUnchanged() {
        //there is nothing to cut - the abbreviation must never make text longer
        Assertions.assertThat(Components.abbreviateToFit(LABEL, "ab", 1)).isEqualTo("ab");
        Assertions.assertThat(Components.abbreviateToFit(LABEL, "", 1)).isEmpty();
    }

    @Test
    public void testDegenerateWidthsNeitherThrowNorReturnEmpty() {
        for (int width = -10; width <= 79; width++) {
            final String abbreviated = Components.abbreviateToFit(LABEL, LONG_TEXT, width);
            Assertions.assertThat(abbreviated).describedAs("width " + width).isNotEmpty();
        }
    }

}
