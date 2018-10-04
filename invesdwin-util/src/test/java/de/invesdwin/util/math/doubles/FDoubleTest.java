package de.invesdwin.util.math.doubles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class FDoubleTest {

    @Test
    public void testGrowthRate() {
        Assertions.assertThat(FDouble.ONE.growthRate(FDouble.TWO).toString()).isEqualTo("1");
        Assertions.assertThat(new FDouble(FDouble.ONE.growthRate(FDouble.TWO).getRate())).isEqualTo(new FDouble("1"));

        Assertions.assertThat(FDouble.MINUS_ONE.growthRate(FDouble.TWO).toString()).isEqualTo("3");
        Assertions.assertThat(new FDouble(FDouble.MINUS_ONE.growthRate(FDouble.TWO).getRate()))
                .isEqualTo(new FDouble("3"));

        Assertions.assertThat(FDouble.ONE.growthRate(FDouble.MINUS_TWO).toString()).isEqualTo("-3");
        Assertions.assertThat(new FDouble(FDouble.ONE.growthRate(FDouble.MINUS_TWO).getRate()))
                .isEqualTo(new FDouble("-3"));

        Assertions.assertThat(FDouble.MINUS_ONE.growthRate(FDouble.MINUS_TWO).toString()).isEqualTo("-1");
        Assertions.assertThat(new FDouble(FDouble.MINUS_ONE.growthRate(FDouble.MINUS_TWO).getRate()))
                .isEqualTo(new FDouble("-1"));

        Assertions.assertThat(FDouble.TWO.growthRate(FDouble.ONE).toString()).isEqualTo("-0.5");
        Assertions.assertThat(new FDouble(FDouble.TWO.growthRate(FDouble.ONE).getRate()))
                .isEqualTo(new FDouble("-0.5"));

        Assertions.assertThat(FDouble.MINUS_TWO.growthRate(FDouble.ONE).toString()).isEqualTo("1.5");
        Assertions.assertThat(new FDouble(FDouble.MINUS_TWO.growthRate(FDouble.ONE).getRate()))
                .isEqualTo(new FDouble("1.5"));

        Assertions.assertThat(FDouble.TWO.growthRate(FDouble.MINUS_ONE).toString()).isEqualTo("-1.5");
        Assertions.assertThat(new FDouble(FDouble.TWO.growthRate(FDouble.MINUS_ONE).getRate()))
                .isEqualTo(new FDouble("-1.5"));

        Assertions.assertThat(FDouble.MINUS_TWO.growthRate(FDouble.MINUS_ONE).toString()).isEqualTo("0.5");
        Assertions.assertThat(new FDouble(FDouble.MINUS_TWO.growthRate(FDouble.MINUS_ONE).getRate()))
                .isEqualTo(new FDouble("0.5"));

    }

    @Test
    public void testMultiply() {
        Assertions.assertThat(new FDouble("13191").multiply(new FDouble("1352255913000")).toString())
                .isIn("17837607748383000", /* double imprecision adds 2 */"17837607748383002");
        Assertions.assertThat(new FDouble("13191").multiply(new FDouble("1352255913000"))).isIn(
                new FDouble("17837607748383000"), /* double imprecision adds 2 */new FDouble("17837607748383002"));
    }

    @Test
    public void testDivide() {
        Assertions.assertThat(new FDouble("1").divide(new FDouble("3")).toString()).isEqualTo("0.3333333333333333");
        Assertions.assertThat(new FDouble("1").divide(new FDouble("3"))).isEqualTo(new FDouble("0.333333333"));
        Assertions.assertThat(new FDouble("0").divide(new FDouble("3"))).isEqualTo(new FDouble("0"));
    }

    @Test
    public void testPow() {
        Assertions.assertThat(FDouble.TWO.pow(2).toString()).isEqualTo("4");
        Assertions.assertThat(FDouble.TWO.pow(2)).isEqualTo(new FDouble("4"));

        Assertions.assertThat(FDouble.TWO.pow(3).toString()).isEqualTo("8");
        Assertions.assertThat(FDouble.TWO.pow(3)).isEqualTo(new FDouble("8"));

        Assertions.assertThat(new FDouble("-1000").pow(new FDouble("-0.1"))).isEqualTo(new FDouble("-0.501187234"));
        Assertions.assertThat(new FDouble("-1000").pow(new FDouble("0.1"))).isEqualTo(new FDouble("-1.995262315"));
        Assertions.assertThat(new FDouble("1000").pow(new FDouble("-0.1"))).isEqualTo(new FDouble("0.501187234"));
        Assertions.assertThat(new FDouble("1000").pow(new FDouble("0.1"))).isEqualTo(new FDouble("1.995262315"));

        Assertions.assertThat(new FDouble("-1000").pow(new FDouble("-0.2"))).isEqualTo(new FDouble("-0.251188643"));
        Assertions.assertThat(new FDouble("-1000").pow(new FDouble("0.2"))).isEqualTo(new FDouble("-3.981071706"));
        Assertions.assertThat(new FDouble("1000").pow(new FDouble("-0.2"))).isEqualTo(new FDouble("0.251188643"));
        Assertions.assertThat(new FDouble("1000").pow(new FDouble("0.2"))).isEqualTo(new FDouble("3.981071706"));

        Assertions.assertThat(new FDouble("-10").pow(new FDouble("-10"))).isEqualTo(new FDouble("0"));
        Assertions.assertThat(new FDouble("-10").pow(new FDouble("10"))).isEqualTo(new FDouble("10000000000"));
        Assertions.assertThat(new FDouble("10").pow(new FDouble("-10"))).isEqualTo(new FDouble("0"));
        Assertions.assertThat(new FDouble("10").pow(new FDouble("10"))).isEqualTo(new FDouble("10000000000"));

        Assertions.assertThat(new FDouble("-9").pow(new FDouble("-10"))).isEqualTo(new FDouble("0"));
        Assertions.assertThat(new FDouble("-9").pow(new FDouble("10"))).isEqualTo(new FDouble("3486784401"));
        Assertions.assertThat(new FDouble("9").pow(new FDouble("-10"))).isEqualTo(new FDouble("0"));
        Assertions.assertThat(new FDouble("9").pow(new FDouble("10"))).isEqualTo(new FDouble("3486784401"));

        Assertions.assertThat(new FDouble("-10").pow(new FDouble("-9"))).isEqualTo(new FDouble("-0.000000001"));
        Assertions.assertThat(new FDouble("-10").pow(new FDouble("9"))).isEqualTo(new FDouble("-1000000000"));
        Assertions.assertThat(new FDouble("10").pow(new FDouble("-9"))).isEqualTo(new FDouble("0.000000001"));
        Assertions.assertThat(new FDouble("10").pow(new FDouble("9"))).isEqualTo(new FDouble("1000000000"));

        Assertions.assertThat(new FDouble("-0.01").pow(new FDouble("-0.1"))).isEqualTo(new FDouble("-1.584893192"));
        Assertions.assertThat(new FDouble("-0.01").pow(new FDouble("0.1"))).isEqualTo(new FDouble("-0.630957344"));
        Assertions.assertThat(new FDouble("0.01").pow(new FDouble("-0.1"))).isEqualTo(new FDouble("1.584893192"));
        Assertions.assertThat(new FDouble("0.01").pow(new FDouble("0.1"))).isEqualTo(new FDouble("0.630957344"));
    }

    @Test
    public void testOrLowerOrHigher() {
        Assertions.assertThat(FDouble.TWO.orLower(FDouble.ONE)).isEqualByComparingTo(FDouble.ONE);
        Assertions.assertThat(FDouble.TWO.orLower(FDouble.THREE)).isEqualByComparingTo(FDouble.TWO);

        Assertions.assertThat(FDouble.TWO.orHigher(FDouble.ONE)).isEqualByComparingTo(FDouble.TWO);
        Assertions.assertThat(FDouble.TWO.orHigher(FDouble.THREE)).isEqualByComparingTo(FDouble.THREE);
    }

    @Test
    public void testAvgGrowthRateUp() {
        final List<FDouble> values = new ArrayList<FDouble>();
        values.add(FDouble.ONE);
        values.add(FDouble.TWO);
        values.add(FDouble.THREE);
        values.add(new FDouble("4"));
        values.add(new FDouble("5"));
        final IFDoubleAggregate<FDouble> growthRates = FDouble.valueOf(values).growthRates().defaultValues();
        Assertions.assertThat(growthRates.toString()).isEqualTo("[1, 0.5, 0.3333333333333333, 0.25]");
        final List<FDouble> expectedList = Arrays.asList(new FDouble("1"), new FDouble("0.5"),
                new FDouble("0.333333333"), new FDouble("0.25"));
        Assertions.assertThat(FDouble.valueOf(values).growthRates().values().size())
                .isEqualTo(growthRates.values().size());
        for (int i = 0; i < expectedList.size(); i++) {
            Assertions.assertThat(growthRates.values().get(i)).isEqualTo(expectedList.get(i));
        }
        Assertions.assertThat(growthRates.values()).isEqualTo(expectedList);

        final FDouble avg = new FDouble(FDouble.valueOf(values).growthRates().avg().getRate());
        Assertions.assertThat(avg.round().toString()).isEqualTo("0.520833333");
        Assertions.assertThat(avg).isEqualTo(new FDouble("0.520833333"));

        Assertions.assertThat(values.get(0).growthRate(values.get(values.size() - 1)).toString()).isEqualTo("4");
        Assertions.assertThat(new FDouble(values.get(0).growthRate(values.get(values.size() - 1)).getRate()))
                .isEqualTo(new FDouble("4"));
    }

    @Test
    public void testRoundToStep() {
        Assertions.assertThat(new FDouble("0.2").roundToStep(new FDouble("0.5")).toString()).isEqualTo("0");
        Assertions.assertThat(new FDouble("0.2").roundToStep(new FDouble("0.5"))).isEqualTo(new FDouble("0"));

        Assertions.assertThat(new FDouble("1.2").roundToStep(new FDouble("0.5")).toString()).isEqualTo("1");
        Assertions.assertThat(new FDouble("1.2").roundToStep(new FDouble("0.5"))).isEqualTo(new FDouble("1"));

        Assertions.assertThat(new FDouble("2.3").roundToStep(new FDouble("0.5")).toString()).isEqualTo("2.5");
        Assertions.assertThat(new FDouble("2.3").roundToStep(new FDouble("0.5"))).isEqualTo(new FDouble("2.5"));

        Assertions.assertThat(new FDouble("0.1").roundToStep(new FDouble("0.2")).toString()).isEqualTo("0.2");
        Assertions.assertThat(new FDouble("0.1").roundToStep(new FDouble("0.2"))).isEqualTo(new FDouble("0.2"));

        Assertions.assertThat(new FDouble("0.0").roundToStep(new FDouble("0.2")).toString()).isEqualTo("0");
        Assertions.assertThat(new FDouble("0.0").roundToStep(new FDouble("0.2"))).isEqualTo(new FDouble("0"));

        Assertions.assertThat(new FDouble("0.1").roundToStep(new FDouble("0.1")).toString()).isEqualTo("0.1");
        Assertions.assertThat(new FDouble("0.1").roundToStep(new FDouble("0.1"))).isEqualTo(new FDouble("0.1"));

        Assertions.assertThat(new FDouble("0.1").roundToStep(new FDouble("0.1"), RoundingMode.FLOOR).toString())
                .isEqualTo("0.1");
        Assertions.assertThat(new FDouble("0.1").roundToStep(new FDouble("0.1"), RoundingMode.FLOOR))
                .isEqualTo(new FDouble("0.1"));
    }

    @Test
    public void testReciprocal() {
        Assertions.assertThat(new FDouble("1").reciprocal().toString()).isEqualTo("1");
        Assertions.assertThat(new FDouble("2").reciprocal().toString()).isEqualTo("0.5");
    }

    @Test
    public void testGeomAvg() {
        final List<FDouble> values = new ArrayList<FDouble>();
        for (int i = 0; i < 3000; i++) {
            values.add(new FDouble("5"));
            values.add(new FDouble("20"));
            values.add(new FDouble("40"));
            values.add(new FDouble("80"));
            values.add(new FDouble("100"));
        }
        final FDouble geomAvg = FDouble.valueOf(values).geomAvg();
        System.out.println(String.format("%s", geomAvg)); //SUPPRESS CHECKSTYLE single line
        Assertions.assertThat(geomAvg.toString()).startsWith("31.69");
    }

    @Test
    public void testRoot() {
        Assertions.assertThat(FDouble.TWO.root(3).toString()).startsWith("1.25");
        Assertions.assertThat(new FDouble("3795629787000").root(2).toString()).startsWith("1948237.6");
    }

    @Test
    public void testSqrt() {
        Assertions.assertThat(FDouble.TWO.sqrt().toString()).startsWith("1.4142");
        Assertions.assertThat(new FDouble("3795629787000").sqrt().toString()).startsWith("1948237.6");
    }

    @Test
    public void testRoundPositive() {
        Assertions.assertThat(new FDouble("3795629787000").round(2).toString()).isEqualTo("3795629787000");
        Assertions.assertThat(new FDouble("3795629787000").round(2)).isEqualTo(new FDouble("3795629787000"));

        Assertions.assertThat(new FDouble("37.95629787000").round(2).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.95629787000").round(2)).isEqualTo(new FDouble("37.96"));

        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.UP).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.UP)).isEqualTo(new FDouble("37.96"));
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.UP).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.UP)).isEqualTo(new FDouble("37.96"));
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.UP).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.UP)).isEqualTo(new FDouble("37.96"));

        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.CEILING).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.CEILING)).isEqualTo(new FDouble("37.96"));
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.CEILING).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.CEILING)).isEqualTo(new FDouble("37.96"));
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.CEILING).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.CEILING)).isEqualTo(new FDouble("37.96"));

        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("37.95");
        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.HALF_UP)).isEqualTo(new FDouble("37.95"));
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.HALF_UP)).isEqualTo(new FDouble("37.96"));
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.HALF_UP)).isEqualTo(new FDouble("37.96"));

        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.DOWN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.DOWN)).isEqualTo(new FDouble("37.95"));
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.DOWN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.DOWN)).isEqualTo(new FDouble("37.95"));
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.DOWN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.DOWN)).isEqualTo(new FDouble("37.95"));

        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.FLOOR).toString()).isEqualTo("37.95");
        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.FLOOR)).isEqualTo(new FDouble("37.95"));
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.FLOOR).toString()).isEqualTo("37.95");
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.FLOOR)).isEqualTo(new FDouble("37.95"));
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.FLOOR).toString()).isEqualTo("37.95");
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.FLOOR)).isEqualTo(new FDouble("37.95"));

        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new FDouble("37.95"));
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new FDouble("37.95"));
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new FDouble("37.96"));

        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new FDouble("37.954").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("37.95"));
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.955").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("37.96"));
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.956").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("37.96"));

        Assertions.assertThat(new FDouble("37.964").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.964").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("37.96"));
        Assertions.assertThat(new FDouble("37.965").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.96");
        Assertions.assertThat(new FDouble("37.965").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("37.96"));
        Assertions.assertThat(new FDouble("37.966").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.97");
        Assertions.assertThat(new FDouble("37.966").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("37.97"));

        Assertions.assertThat(new FDouble("37.95629787000").round(2, RoundingMode.UNNECESSARY).toString())
                .isEqualTo("37.95629787");
        Assertions.assertThat(new FDouble("37.95629787000").round(2, RoundingMode.UNNECESSARY))
                .isEqualTo(new FDouble("37.95629787000"));

        Assertions.assertThat(new FDouble(243841100000L).toString()).isEqualTo("243841100000");
    }

    @Test
    public void testRoundNegative() {
        Assertions.assertThat(new FDouble("-3795629787000").round(2).toString()).isEqualTo("-3795629787000");
        Assertions.assertThat(new FDouble("-3795629787000").round(2)).isEqualTo(new FDouble("-3795629787000"));

        Assertions.assertThat(new FDouble("-37.95629787000").round(2).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.95629787000").round(2)).isEqualTo(new FDouble("-37.96"));

        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.UP).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.UP)).isEqualTo(new FDouble("-37.96"));
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.UP).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.UP)).isEqualTo(new FDouble("-37.96"));
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.UP).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.UP)).isEqualTo(new FDouble("-37.96"));

        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.CEILING).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.CEILING)).isEqualTo(new FDouble("-37.95"));
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.CEILING).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.CEILING)).isEqualTo(new FDouble("-37.95"));
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.CEILING).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.CEILING)).isEqualTo(new FDouble("-37.95"));

        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.HALF_UP)).isEqualTo(new FDouble("-37.95"));
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.HALF_UP)).isEqualTo(new FDouble("-37.96"));
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.HALF_UP)).isEqualTo(new FDouble("-37.96"));

        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.DOWN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.DOWN)).isEqualTo(new FDouble("-37.95"));
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.DOWN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.DOWN)).isEqualTo(new FDouble("-37.95"));
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.DOWN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.DOWN)).isEqualTo(new FDouble("-37.95"));

        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.FLOOR).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.FLOOR)).isEqualTo(new FDouble("-37.96"));
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.FLOOR).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.FLOOR)).isEqualTo(new FDouble("-37.96"));
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.FLOOR).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.FLOOR)).isEqualTo(new FDouble("-37.96"));

        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new FDouble("-37.95"));
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new FDouble("-37.95"));
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new FDouble("-37.96"));

        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new FDouble("-37.954").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("-37.95"));
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.955").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("-37.96"));
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.956").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("-37.96"));

        Assertions.assertThat(new FDouble("-37.964").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.964").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("-37.96"));
        Assertions.assertThat(new FDouble("-37.965").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new FDouble("-37.965").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("-37.96"));
        Assertions.assertThat(new FDouble("-37.966").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.97");
        Assertions.assertThat(new FDouble("-37.966").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new FDouble("-37.97"));

        Assertions.assertThat(new FDouble("-37.95629787000").round(2, RoundingMode.UNNECESSARY).toString())
                .isEqualTo("-37.95629787");
        Assertions.assertThat(new FDouble("-37.95629787000").round(2, RoundingMode.UNNECESSARY))
                .isEqualTo(new FDouble("-37.95629787000"));

        Assertions.assertThat(new FDouble(-243841100000L).toString()).isEqualTo("-243841100000");
    }

    @Test
    public void testGetDigits() {
        Assertions.assertThat(new BigDecimal("37.95629787000").stripTrailingZeros().precision()).isEqualTo(10);
        Assertions.assertThat(new FDouble("37.95629787000").getDigits()).isEqualTo(10);

        Assertions.assertThat(new BigDecimal("0.95629787000").stripTrailingZeros().precision()).isEqualTo(8);
        Assertions.assertThat(new FDouble("0.95629787000").getDigits()).isEqualTo(9);

        Assertions.assertThat(new BigDecimal("0").stripTrailingZeros().precision()).isEqualTo(1);
        Assertions.assertThat(new FDouble("0").getDigits()).isEqualTo(1);

        Assertions.assertThat(new BigDecimal("0.01").stripTrailingZeros().precision()).isEqualTo(1);
        Assertions.assertThat(new FDouble("0.01").getDigits()).isEqualTo(3);
    }

    @Test
    public void testgetDecimalDigits() {
        Assertions.assertThat(new BigDecimal("37.95629787000").stripTrailingZeros().scale()).isEqualTo(8);
        Assertions.assertThat(new FDouble("37.95629787000").getDecimalDigits()).isEqualTo(8);

        Assertions.assertThat(new BigDecimal("0.95629787000").stripTrailingZeros().scale()).isEqualTo(8);
        Assertions.assertThat(new FDouble("0.95629787000").getDecimalDigits()).isEqualTo(8);

        Assertions.assertThat(new BigDecimal("0").stripTrailingZeros().scale()).isEqualTo(0);
        Assertions.assertThat(new FDouble("0").getDecimalDigits()).isEqualTo(0);

        Assertions.assertThat(new BigDecimal("0.01").stripTrailingZeros().scale()).isEqualTo(2);
        Assertions.assertThat(new FDouble("0.01").getDecimalDigits()).isEqualTo(2);
    }

    @Test
    public void testGetWholeNumberDigits() {
        final BigDecimal bd = new BigDecimal("37.95629787000").stripTrailingZeros();
        Assertions.assertThat(bd.precision() - bd.scale()).isEqualTo(2);
        Assertions.assertThat(new FDouble("37.95629787000").getWholeNumberDigits()).isEqualTo(2);

        final BigDecimal bdZeroPoint = new BigDecimal("0.95629787000").stripTrailingZeros();
        Assertions.assertThat(bdZeroPoint.precision() - bdZeroPoint.scale()).isEqualTo(0);
        Assertions.assertThat(new FDouble("0.95629787000").getWholeNumberDigits()).isEqualTo(1);

        final BigDecimal bdZero = new BigDecimal("0").stripTrailingZeros();
        Assertions.assertThat(bdZero.precision() - bdZero.scale()).isEqualTo(1);
        Assertions.assertThat(new FDouble("0").getWholeNumberDigits()).isEqualTo(1);

        final BigDecimal bd001 = new BigDecimal("0.01").stripTrailingZeros();
        Assertions.assertThat(bd001.precision() - bdZero.scale()).isEqualTo(1);
        Assertions.assertThat(new FDouble("0.01").getWholeNumberDigits()).isEqualTo(1);
    }

    @Test
    public void testEquals() {
        Assertions.assertThat(new FDouble("9.536743164E-7").toString())
                .isEqualTo(new FDouble("0.0000009536743164").toString());
        Assertions.assertThat(new FDouble("9.536743164E-7")).isEqualTo(new FDouble("0.0000009536743164"));
    }

    @Test
    public void testScaleByPowerOfTen() {
        final FDouble scaled100 = new FDouble("1").scaleByPowerOfTen(2);
        Assertions.assertThat(scaled100.toString()).isEqualTo("100");
        Assertions.assertThat(scaled100).isEqualTo(new FDouble("100"));
        Assertions.assertThat(scaled100.getDigits()).isEqualTo(3);
        Assertions.assertThat(scaled100.getWholeNumberDigits()).isEqualTo(3);
        Assertions.assertThat(scaled100.getDecimalDigits()).isEqualTo(0);

        final FDouble scaled001 = new FDouble("1").scaleByPowerOfTen(-2);
        Assertions.assertThat(scaled001.toString()).isEqualTo("0.01");
        Assertions.assertThat(scaled001).isEqualTo(new FDouble("0.01"));
        Assertions.assertThat(scaled001.getWholeNumberDigits()).isEqualTo(1);
        Assertions.assertThat(scaled001.getDecimalDigits()).isEqualTo(2);
        Assertions.assertThat(scaled001.getDigits()).isEqualTo(3);
    }

    @Test
    public void testDetrendAbsolutePositiveToNegative() {
        final List<FDouble> values = new ArrayList<FDouble>();
        for (int i = 10; i >= -10; i--) {
            values.add(new FDouble(i));
        }
        final List<FDouble> detrended = FDouble.valueOf(values).detrendAbsolute().values();
        //CHECKSTYLE:OFF
        System.out.println(values + " " + FDouble.valueOf(values).growthRates().avg());
        System.out.println(detrended + " " + FDouble.valueOf(detrended).growthRates().avg());
        //CHECKSTYLE:ON
    }

    @Test
    public void testDetrendAbsolutePositive() {
        final List<FDouble> values = new ArrayList<FDouble>();
        for (int i = 10; i >= 0; i--) {
            values.add(new FDouble(i));
        }
        final List<FDouble> detrended = FDouble.valueOf(values).detrendAbsolute().values();
        //CHECKSTYLE:OFF
        System.out.println(values + " " + FDouble.valueOf(values).growthRates().avg());
        System.out.println(detrended + " " + FDouble.valueOf(detrended).growthRates().avg());
        //CHECKSTYLE:ON
    }

    @Test
    public void testDetrendRelativePositiveToNegative() {
        final List<FDouble> values = new ArrayList<FDouble>();
        for (int i = 100; i < 120; i++) {
            values.add(new FDouble(i));
        }
        final List<FDouble> detrended = FDouble.valueOf(values).detrendRelative().values();
        final List<FDouble> detrendedWithAbsolute = FDouble.valueOf(detrended).detrendAbsolute().values();
        //CHECKSTYLE:OFF
        System.out.println(values + " " + FDouble.valueOf(values).growthRates().avg());
        System.out.println(detrended + " " + FDouble.valueOf(detrended).growthRates().avg());
        System.out.println(detrendedWithAbsolute + " " + FDouble.valueOf(detrendedWithAbsolute).growthRates().avg());
        //CHECKSTYLE:ON
    }

    @Test
    public void testDetrendRelativePositive() {
        final List<FDouble> values = new ArrayList<FDouble>();
        for (int i = 100; i >= 80; i--) {
            values.add(new FDouble(i));
        }
        final List<FDouble> detrended = FDouble.valueOf(values).detrendRelative().values();
        final List<FDouble> detrendedWithAbsolute = FDouble.valueOf(detrended).detrendAbsolute().values();
        //CHECKSTYLE:OFF
        System.out.println(values + " " + FDouble.valueOf(values).growthRates().avg());
        System.out.println(detrended + " " + FDouble.valueOf(detrended).growthRates().avg());
        System.out.println(detrendedWithAbsolute + " " + FDouble.valueOf(detrendedWithAbsolute).growthRates().avg());
        //CHECKSTYLE:ON
    }

    @Test
    public void testSerialize() {
        final FDouble positive = new FDouble("1");
        Assertions.checkTrue(positive.isPositive());
        final double roundedValue = positive.doubleValue();
        final FDouble deepClone = Objects.deepClone(positive);
        Assertions.checkTrue(deepClone.isPositive());
        Assertions.assertThat(deepClone.doubleValue()).isEqualTo(roundedValue);
    }

}
