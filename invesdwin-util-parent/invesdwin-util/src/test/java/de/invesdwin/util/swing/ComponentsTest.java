package de.invesdwin.util.swing;

import javax.annotation.concurrent.ThreadSafe;
import javax.swing.JLabel;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FTimeUnit;

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

    //the repost throttle is probed with synthetic clocks, so it wants the interval in nanos
    private static final long TOOLTIP_INTERVAL_NANOS = Components.TOOLTIP_UPDATE_INTERVAL
            .longValue(FTimeUnit.NANOSECONDS);

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

    /**
     * Pins the repost throttle of a shown tooltip: the status bar task line and the heap
     * indicator change their tooltip text several times a second while a suite runs, and
     * the ToolTipManager re-posts a tooltip on every mouseMoved it gets with changed text
     * (hide plus a fresh JToolTip and HTML layout) or restarts its own initial delay while
     * the tooltip has not appeared yet. Reposts faster than that delay are what make the
     * tooltip blink or never show.
     */
    @Test
    public void testFirstToolTipUpdateIsDueAndRepeatsInsideTheIntervalAreNot() {
        final JLabel label = new JLabel();
        Assertions.assertThat(Components.isToolTipUpdateDue(label, 0L)).describedAs("first change").isTrue();
        Assertions.assertThat(Components.isToolTipUpdateDue(label, TOOLTIP_INTERVAL_NANOS - 1))
                .describedAs("just inside the interval")
                .isFalse();
        Assertions.assertThat(Components.isToolTipUpdateDue(label, TOOLTIP_INTERVAL_NANOS))
                .describedAs("exactly the interval")
                .isFalse();
        Assertions.assertThat(Components.isToolTipUpdateDue(label, TOOLTIP_INTERVAL_NANOS + 1))
                .describedAs("just past the interval")
                .isTrue();
        //the due one took a new slot, so the window starts over from there
        Assertions.assertThat(Components.isToolTipUpdateDue(label, 2 * TOOLTIP_INTERVAL_NANOS))
                .describedAs("one nanosecond short of the new window")
                .isFalse();
    }

    @Test
    public void testToolTipThrottleSlotsArePerComponent() {
        //the task line and the heap indicator sit on the same bar and both update
        //continuously - one must not consume the other's repost
        final JLabel taskLabel = new JLabel();
        final JLabel heapLabel = new JLabel();
        Assertions.assertThat(Components.isToolTipUpdateDue(taskLabel, 0L)).isTrue();
        Assertions.assertThat(Components.isToolTipUpdateDue(heapLabel, 1L)).isTrue();
    }

    @Test
    public void testThrottledToolTipStillCarriesTheLatestText() {
        //the gate may only hold back the repost: the text has to land on the component, or
        //the tooltip would freeze at whatever it first showed
        final JLabel label = new JLabel();
        Components.setToolTipText(label, "Loading chart data (57%): 12/21 | ETA: PT24S", true);
        Components.setToolTipText(label, "Loading chart data (61%): 13/21 | ETA: PT23S", true);
        Assertions.assertThat(label.getToolTipText())
                .isEqualTo("<html>Loading chart data (61%): 13/21 | ETA: PT23S");
        //a slot on the component proves setToolTipText goes through the gate at all - a
        //call that reposted without asking would leave the property unset
        Assertions.assertThat(label.getClientProperty(Components.CLIENTPROP_LAST_TOOLTIP_UPDATE_NANOS))
                .describedAs("the gate is wired into the tooltip update path")
                .isInstanceOf(Long.class);
    }

}
