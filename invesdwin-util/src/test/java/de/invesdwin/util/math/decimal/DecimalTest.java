package de.invesdwin.util.math.decimal;

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
public class DecimalTest {

    @Test
    public void testGrowthRate() {
        Assertions.assertThat(Decimal.ONE.growthRate(Decimal.TWO).toString()).isEqualTo("1");
        Assertions.assertThat(Decimal.ONE.growthRate(Decimal.TWO).getRate()).isEqualTo(new Decimal("1"));

        Assertions.assertThat(Decimal.MINUS_ONE.growthRate(Decimal.TWO).toString()).isEqualTo("3");
        Assertions.assertThat(Decimal.MINUS_ONE.growthRate(Decimal.TWO).getRate()).isEqualTo(new Decimal("3"));

        Assertions.assertThat(Decimal.ONE.growthRate(Decimal.MINUS_TWO).toString()).isEqualTo("-3");
        Assertions.assertThat(Decimal.ONE.growthRate(Decimal.MINUS_TWO).getRate()).isEqualTo(new Decimal("-3"));

        Assertions.assertThat(Decimal.MINUS_ONE.growthRate(Decimal.MINUS_TWO).toString()).isEqualTo("-1");
        Assertions.assertThat(Decimal.MINUS_ONE.growthRate(Decimal.MINUS_TWO).getRate()).isEqualTo(new Decimal("-1"));

        Assertions.assertThat(Decimal.TWO.growthRate(Decimal.ONE).toString()).isEqualTo("-0.5");
        Assertions.assertThat(Decimal.TWO.growthRate(Decimal.ONE).getRate()).isEqualTo(new Decimal("-0.5"));

        Assertions.assertThat(Decimal.MINUS_TWO.growthRate(Decimal.ONE).toString()).isEqualTo("1.5");
        Assertions.assertThat(Decimal.MINUS_TWO.growthRate(Decimal.ONE).getRate()).isEqualTo(new Decimal("1.5"));

        Assertions.assertThat(Decimal.TWO.growthRate(Decimal.MINUS_ONE).toString()).isEqualTo("-1.5");
        Assertions.assertThat(Decimal.TWO.growthRate(Decimal.MINUS_ONE).getRate()).isEqualTo(new Decimal("-1.5"));

        Assertions.assertThat(Decimal.MINUS_TWO.growthRate(Decimal.MINUS_ONE).toString()).isEqualTo("0.5");
        Assertions.assertThat(Decimal.MINUS_TWO.growthRate(Decimal.MINUS_ONE).getRate()).isEqualTo(new Decimal("0.5"));

    }

    @Test
    public void testMultiply() {
        Assertions.assertThat(new Decimal("13191").multiply(new Decimal("1352255913000")).toString())
                .isIn("17837607748383000", /* double imprecision adds 2 */"17837607748383002");
        Assertions.assertThat(new Decimal("13191").multiply(new Decimal("1352255913000"))).isIn(
                new Decimal("17837607748383000"), /* double imprecision adds 2 */new Decimal("17837607748383002"));
    }

    @Test
    public void testDivide() {
        Assertions.assertThat(new Decimal("1").divide(new Decimal("3")).toString()).isEqualTo("0.333333333");
        Assertions.assertThat(new Decimal("1").divide(new Decimal("3"))).isEqualTo(new Decimal("0.333333333"));
        Assertions.assertThat(new Decimal("0").divide(new Decimal("3"))).isEqualTo(new Decimal("0"));
    }

    @Test
    public void testPow() {
        Assertions.assertThat(Decimal.TWO.pow(2).toString()).isEqualTo("4");
        Assertions.assertThat(Decimal.TWO.pow(2)).isEqualTo(new Decimal("4"));

        Assertions.assertThat(Decimal.TWO.pow(3).toString()).isEqualTo("8");
        Assertions.assertThat(Decimal.TWO.pow(3)).isEqualTo(new Decimal("8"));

        Assertions.assertThat(new Decimal("-1000").pow(new Decimal("-0.1"))).isEqualTo(new Decimal("-0.501187234"));
        Assertions.assertThat(new Decimal("-1000").pow(new Decimal("0.1"))).isEqualTo(new Decimal("-1.995262315"));
        Assertions.assertThat(new Decimal("1000").pow(new Decimal("-0.1"))).isEqualTo(new Decimal("0.501187234"));
        Assertions.assertThat(new Decimal("1000").pow(new Decimal("0.1"))).isEqualTo(new Decimal("1.995262315"));

        Assertions.assertThat(new Decimal("-1000").pow(new Decimal("-0.2"))).isEqualTo(new Decimal("-0.251188643"));
        Assertions.assertThat(new Decimal("-1000").pow(new Decimal("0.2"))).isEqualTo(new Decimal("-3.981071706"));
        Assertions.assertThat(new Decimal("1000").pow(new Decimal("-0.2"))).isEqualTo(new Decimal("0.251188643"));
        Assertions.assertThat(new Decimal("1000").pow(new Decimal("0.2"))).isEqualTo(new Decimal("3.981071706"));

        Assertions.assertThat(new Decimal("-10").pow(new Decimal("-10"))).isEqualTo(new Decimal("0"));
        Assertions.assertThat(new Decimal("-10").pow(new Decimal("10"))).isEqualTo(new Decimal("10000000000"));
        Assertions.assertThat(new Decimal("10").pow(new Decimal("-10"))).isEqualTo(new Decimal("0"));
        Assertions.assertThat(new Decimal("10").pow(new Decimal("10"))).isEqualTo(new Decimal("10000000000"));

        Assertions.assertThat(new Decimal("-9").pow(new Decimal("-10"))).isEqualTo(new Decimal("0"));
        Assertions.assertThat(new Decimal("-9").pow(new Decimal("10"))).isEqualTo(new Decimal("3486784401"));
        Assertions.assertThat(new Decimal("9").pow(new Decimal("-10"))).isEqualTo(new Decimal("0"));
        Assertions.assertThat(new Decimal("9").pow(new Decimal("10"))).isEqualTo(new Decimal("3486784401"));

        Assertions.assertThat(new Decimal("-10").pow(new Decimal("-9"))).isEqualTo(new Decimal("-0.000000001"));
        Assertions.assertThat(new Decimal("-10").pow(new Decimal("9"))).isEqualTo(new Decimal("-1000000000"));
        Assertions.assertThat(new Decimal("10").pow(new Decimal("-9"))).isEqualTo(new Decimal("0.000000001"));
        Assertions.assertThat(new Decimal("10").pow(new Decimal("9"))).isEqualTo(new Decimal("1000000000"));

        Assertions.assertThat(new Decimal("-0.01").pow(new Decimal("-0.1"))).isEqualTo(new Decimal("-1.584893192"));
        Assertions.assertThat(new Decimal("-0.01").pow(new Decimal("0.1"))).isEqualTo(new Decimal("-0.630957344"));
        Assertions.assertThat(new Decimal("0.01").pow(new Decimal("-0.1"))).isEqualTo(new Decimal("1.584893192"));
        Assertions.assertThat(new Decimal("0.01").pow(new Decimal("0.1"))).isEqualTo(new Decimal("0.630957344"));
    }

    @Test
    public void testOrLowerOrHigher() {
        Assertions.assertThat(Decimal.TWO.orLower(Decimal.ONE)).isEqualByComparingTo(Decimal.ONE);
        Assertions.assertThat(Decimal.TWO.orLower(Decimal.THREE)).isEqualByComparingTo(Decimal.TWO);

        Assertions.assertThat(Decimal.TWO.orHigher(Decimal.ONE)).isEqualByComparingTo(Decimal.TWO);
        Assertions.assertThat(Decimal.TWO.orHigher(Decimal.THREE)).isEqualByComparingTo(Decimal.THREE);
    }

    @Test
    public void testAvgGrowthRateUp() {
        final List<Decimal> values = new ArrayList<Decimal>();
        values.add(Decimal.ONE);
        values.add(Decimal.TWO);
        values.add(Decimal.THREE);
        values.add(new Decimal("4"));
        values.add(new Decimal("5"));
        final IDecimalAggregate<Decimal> growthRates = Decimal.valueOf(values).growthRates().defaultValues();
        Assertions.assertThat(growthRates.toString()).isEqualTo("[1, 0.5, 0.333333333, 0.25]");
        final List<Decimal> expectedList = Arrays.asList(new Decimal("1"), new Decimal("0.5"),
                new Decimal("0.333333333"), new Decimal("0.25"));
        Assertions.assertThat(Decimal.valueOf(values).growthRates().values().size())
                .isEqualTo(growthRates.values().size());
        for (int i = 0; i < expectedList.size(); i++) {
            Assertions.assertThat(growthRates.values().get(i)).isEqualTo(expectedList.get(i));
        }
        Assertions.assertThat(growthRates.values()).isEqualTo(expectedList);

        final Decimal avg = Decimal.valueOf(values).growthRates().avg().getRate();
        Assertions.assertThat(avg.toString()).isEqualTo("0.5208333333333333");
        Assertions.assertThat(avg).isEqualTo(new Decimal("0.5208333333333333"));

        Assertions.assertThat(values.get(0).growthRate(values.get(values.size() - 1)).toString()).isEqualTo("4");
        Assertions.assertThat(values.get(0).growthRate(values.get(values.size() - 1)).getRate())
                .isEqualTo(new Decimal("4"));
    }

    @Test
    public void testRoundToStep() {
        Assertions.assertThat(new Decimal("0.2").roundToStep(new Decimal("0.5")).toString()).isEqualTo("0");
        Assertions.assertThat(new Decimal("0.2").roundToStep(new Decimal("0.5"))).isEqualTo(new Decimal("0"));

        Assertions.assertThat(new Decimal("1.2").roundToStep(new Decimal("0.5")).toString()).isEqualTo("1");
        Assertions.assertThat(new Decimal("1.2").roundToStep(new Decimal("0.5"))).isEqualTo(new Decimal("1"));

        Assertions.assertThat(new Decimal("2.3").roundToStep(new Decimal("0.5")).toString()).isEqualTo("2.5");
        Assertions.assertThat(new Decimal("2.3").roundToStep(new Decimal("0.5"))).isEqualTo(new Decimal("2.5"));

        Assertions.assertThat(new Decimal("0.1").roundToStep(new Decimal("0.2")).toString()).isEqualTo("0.2");
        Assertions.assertThat(new Decimal("0.1").roundToStep(new Decimal("0.2"))).isEqualTo(new Decimal("0.2"));

        Assertions.assertThat(new Decimal("0.0").roundToStep(new Decimal("0.2")).toString()).isEqualTo("0");
        Assertions.assertThat(new Decimal("0.0").roundToStep(new Decimal("0.2"))).isEqualTo(new Decimal("0"));

        Assertions.assertThat(new Decimal("0.1").roundToStep(new Decimal("0.1")).toString()).isEqualTo("0.1");
        Assertions.assertThat(new Decimal("0.1").roundToStep(new Decimal("0.1"))).isEqualTo(new Decimal("0.1"));

        Assertions.assertThat(new Decimal("0.1").roundToStep(new Decimal("0.1"), RoundingMode.FLOOR).toString())
                .isEqualTo("0.1");
        Assertions.assertThat(new Decimal("0.1").roundToStep(new Decimal("0.1"), RoundingMode.FLOOR))
                .isEqualTo(new Decimal("0.1"));
    }

    @Test
    public void testReciprocal() {
        Assertions.assertThat(new Decimal("1").reciprocal().toString()).isEqualTo("1");
        Assertions.assertThat(new Decimal("2").reciprocal().toString()).isEqualTo("0.5");
    }

    @Test
    public void testGeomAvg() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 0; i < 3000; i++) {
            values.add(new Decimal("5"));
            values.add(new Decimal("20"));
            values.add(new Decimal("40"));
            values.add(new Decimal("80"));
            values.add(new Decimal("100"));
        }
        final Decimal geomAvg = Decimal.valueOf(values).geomAvg();
        System.out.println(String.format("%s", geomAvg)); //SUPPRESS CHECKSTYLE single line
        Assertions.assertThat(geomAvg.toString()).startsWith("31.69");
    }

    @Test
    public void testRoot() {
        Assertions.assertThat(Decimal.TWO.root(3).toString()).startsWith("1.25");
        Assertions.assertThat(new Decimal("3795629787000").root(2).toString()).startsWith("1948237.6");
    }

    @Test
    public void testSqrt() {
        Assertions.assertThat(Decimal.TWO.sqrt().toString()).startsWith("1.4142");
        Assertions.assertThat(new Decimal("3795629787000").sqrt().toString()).startsWith("1948237.6");
    }

    @Test
    public void testRoundPositive() {
        Assertions.assertThat(new Decimal("3795629787000").round(2).toString()).isEqualTo("3795629787000");
        Assertions.assertThat(new Decimal("3795629787000").round(2)).isEqualTo(new Decimal("3795629787000"));

        Assertions.assertThat(new Decimal("37.95629787000").round(2).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.95629787000").round(2)).isEqualTo(new Decimal("37.96"));

        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.UP).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.UP)).isEqualTo(new Decimal("37.96"));
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.UP).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.UP)).isEqualTo(new Decimal("37.96"));
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.UP).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.UP)).isEqualTo(new Decimal("37.96"));

        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.CEILING).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.CEILING)).isEqualTo(new Decimal("37.96"));
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.CEILING).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.CEILING)).isEqualTo(new Decimal("37.96"));
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.CEILING).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.CEILING)).isEqualTo(new Decimal("37.96"));

        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("37.95");
        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.HALF_UP)).isEqualTo(new Decimal("37.95"));
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.HALF_UP)).isEqualTo(new Decimal("37.96"));
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.HALF_UP)).isEqualTo(new Decimal("37.96"));

        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.DOWN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.DOWN)).isEqualTo(new Decimal("37.95"));
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.DOWN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.DOWN)).isEqualTo(new Decimal("37.95"));
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.DOWN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.DOWN)).isEqualTo(new Decimal("37.95"));

        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.FLOOR).toString()).isEqualTo("37.95");
        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.FLOOR)).isEqualTo(new Decimal("37.95"));
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.FLOOR).toString()).isEqualTo("37.95");
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.FLOOR)).isEqualTo(new Decimal("37.95"));
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.FLOOR).toString()).isEqualTo("37.95");
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.FLOOR)).isEqualTo(new Decimal("37.95"));

        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new Decimal("37.95"));
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new Decimal("37.95"));
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new Decimal("37.96"));

        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.95");
        Assertions.assertThat(new Decimal("37.954").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("37.95"));
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.955").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("37.96"));
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.956").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("37.96"));

        Assertions.assertThat(new Decimal("37.964").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.964").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("37.96"));
        Assertions.assertThat(new Decimal("37.965").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.96");
        Assertions.assertThat(new Decimal("37.965").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("37.96"));
        Assertions.assertThat(new Decimal("37.966").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("37.97");
        Assertions.assertThat(new Decimal("37.966").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("37.97"));

        Assertions.assertThat(new Decimal("37.95629787000").round(2, RoundingMode.UNNECESSARY).toString())
                .isEqualTo("37.95629787");
        Assertions.assertThat(new Decimal("37.95629787000").round(2, RoundingMode.UNNECESSARY))
                .isEqualTo(new Decimal("37.95629787000"));
    }

    @Test
    public void testRoundNegative() {
        Assertions.assertThat(new Decimal("-3795629787000").round(2).toString()).isEqualTo("-3795629787000");
        Assertions.assertThat(new Decimal("-3795629787000").round(2)).isEqualTo(new Decimal("-3795629787000"));

        Assertions.assertThat(new Decimal("-37.95629787000").round(2).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.95629787000").round(2)).isEqualTo(new Decimal("-37.96"));

        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.UP).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.UP)).isEqualTo(new Decimal("-37.96"));
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.UP).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.UP)).isEqualTo(new Decimal("-37.96"));
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.UP).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.UP)).isEqualTo(new Decimal("-37.96"));

        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.CEILING).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.CEILING)).isEqualTo(new Decimal("-37.95"));
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.CEILING).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.CEILING)).isEqualTo(new Decimal("-37.95"));
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.CEILING).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.CEILING)).isEqualTo(new Decimal("-37.95"));

        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.HALF_UP)).isEqualTo(new Decimal("-37.95"));
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.HALF_UP)).isEqualTo(new Decimal("-37.96"));
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.HALF_UP).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.HALF_UP)).isEqualTo(new Decimal("-37.96"));

        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.DOWN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.DOWN)).isEqualTo(new Decimal("-37.95"));
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.DOWN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.DOWN)).isEqualTo(new Decimal("-37.95"));
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.DOWN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.DOWN)).isEqualTo(new Decimal("-37.95"));

        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.FLOOR).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.FLOOR)).isEqualTo(new Decimal("-37.96"));
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.FLOOR).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.FLOOR)).isEqualTo(new Decimal("-37.96"));
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.FLOOR).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.FLOOR)).isEqualTo(new Decimal("-37.96"));

        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new Decimal("-37.95"));
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new Decimal("-37.95"));
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.HALF_DOWN).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.HALF_DOWN)).isEqualTo(new Decimal("-37.96"));

        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.95");
        Assertions.assertThat(new Decimal("-37.954").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("-37.95"));
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.955").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("-37.96"));
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.956").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("-37.96"));

        Assertions.assertThat(new Decimal("-37.964").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.964").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("-37.96"));
        Assertions.assertThat(new Decimal("-37.965").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.96");
        Assertions.assertThat(new Decimal("-37.965").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("-37.96"));
        Assertions.assertThat(new Decimal("-37.966").round(2, RoundingMode.HALF_EVEN).toString()).isEqualTo("-37.97");
        Assertions.assertThat(new Decimal("-37.966").round(2, RoundingMode.HALF_EVEN)).isEqualTo(new Decimal("-37.97"));

        Assertions.assertThat(new Decimal("-37.95629787000").round(2, RoundingMode.UNNECESSARY).toString())
                .isEqualTo("-37.95629787");
        Assertions.assertThat(new Decimal("-37.95629787000").round(2, RoundingMode.UNNECESSARY))
                .isEqualTo(new Decimal("-37.95629787000"));
    }

    @Test
    public void testGetDigits() {
        Assertions.assertThat(new BigDecimal("37.95629787000").stripTrailingZeros().precision()).isEqualTo(10);
        Assertions.assertThat(new Decimal("37.95629787000").getDigits()).isEqualTo(10);

        Assertions.assertThat(new BigDecimal("0.95629787000").stripTrailingZeros().precision()).isEqualTo(8);
        Assertions.assertThat(new Decimal("0.95629787000").getDigits()).isEqualTo(9);

        Assertions.assertThat(new BigDecimal("0").stripTrailingZeros().precision()).isEqualTo(1);
        Assertions.assertThat(new Decimal("0").getDigits()).isEqualTo(1);

        Assertions.assertThat(new BigDecimal("0.01").stripTrailingZeros().precision()).isEqualTo(1);
        Assertions.assertThat(new Decimal("0.01").getDigits()).isEqualTo(3);
    }

    @Test
    public void testGetDecimalDigits() {
        Assertions.assertThat(new BigDecimal("37.95629787000").stripTrailingZeros().scale()).isEqualTo(8);
        Assertions.assertThat(new Decimal("37.95629787000").getDecimalDigits()).isEqualTo(8);

        Assertions.assertThat(new BigDecimal("0.95629787000").stripTrailingZeros().scale()).isEqualTo(8);
        Assertions.assertThat(new Decimal("0.95629787000").getDecimalDigits()).isEqualTo(8);

        Assertions.assertThat(new BigDecimal("0").stripTrailingZeros().scale()).isEqualTo(0);
        Assertions.assertThat(new Decimal("0").getDecimalDigits()).isEqualTo(0);

        Assertions.assertThat(new BigDecimal("0.01").stripTrailingZeros().scale()).isEqualTo(2);
        Assertions.assertThat(new Decimal("0.01").getDecimalDigits()).isEqualTo(2);
    }

    @Test
    public void testGetWholeNumberDigits() {
        final BigDecimal bd = new BigDecimal("37.95629787000").stripTrailingZeros();
        Assertions.assertThat(bd.precision() - bd.scale()).isEqualTo(2);
        Assertions.assertThat(new Decimal("37.95629787000").getWholeNumberDigits()).isEqualTo(2);

        final BigDecimal bdZeroPoint = new BigDecimal("0.95629787000").stripTrailingZeros();
        Assertions.assertThat(bdZeroPoint.precision() - bdZeroPoint.scale()).isEqualTo(0);
        Assertions.assertThat(new Decimal("0.95629787000").getWholeNumberDigits()).isEqualTo(1);

        final BigDecimal bdZero = new BigDecimal("0").stripTrailingZeros();
        Assertions.assertThat(bdZero.precision() - bdZero.scale()).isEqualTo(1);
        Assertions.assertThat(new Decimal("0").getWholeNumberDigits()).isEqualTo(1);

        final BigDecimal bd001 = new BigDecimal("0.01").stripTrailingZeros();
        Assertions.assertThat(bd001.precision() - bdZero.scale()).isEqualTo(1);
        Assertions.assertThat(new Decimal("0.01").getWholeNumberDigits()).isEqualTo(1);
    }

    @Test
    public void testEquals() {
        Assertions.assertThat(new Decimal("9.536743164E-7").toString())
                .isEqualTo(new Decimal("0.0000009536743164").toString());
        Assertions.assertThat(new Decimal("9.536743164E-7")).isEqualTo(new Decimal("0.0000009536743164"));
    }

    @Test
    public void testScaleByPowerOfTen() {
        final Decimal scaled100 = new Decimal("1").scaleByPowerOfTen(2);
        Assertions.assertThat(scaled100.toString()).isEqualTo("100");
        Assertions.assertThat(scaled100).isEqualTo(new Decimal("100"));
        Assertions.assertThat(scaled100.getDigits()).isEqualTo(3);
        Assertions.assertThat(scaled100.getWholeNumberDigits()).isEqualTo(3);
        Assertions.assertThat(scaled100.getDecimalDigits()).isEqualTo(0);

        final Decimal scaled001 = new Decimal("1").scaleByPowerOfTen(-2);
        Assertions.assertThat(scaled001.toString()).isEqualTo("0.01");
        Assertions.assertThat(scaled001).isEqualTo(new Decimal("0.01"));
        Assertions.assertThat(scaled001.getWholeNumberDigits()).isEqualTo(1);
        Assertions.assertThat(scaled001.getDecimalDigits()).isEqualTo(2);
        Assertions.assertThat(scaled001.getDigits()).isEqualTo(3);
    }

    @Test
    public void testDetrendAbsolutePositiveToNegative() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 10; i >= -10; i--) {
            values.add(new Decimal(i));
        }
        final List<Decimal> detrended = Decimal.valueOf(values).detrendAbsolute().values();
        //CHECKSTYLE:OFF
        System.out.println(values + " " + Decimal.valueOf(values).growthRates().avg());
        System.out.println(detrended + " " + Decimal.valueOf(detrended).growthRates().avg());
        //CHECKSTYLE:ON
    }

    @Test
    public void testDetrendAbsolutePositive() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 10; i >= 0; i--) {
            values.add(new Decimal(i));
        }
        final List<Decimal> detrended = Decimal.valueOf(values).detrendAbsolute().values();
        //CHECKSTYLE:OFF
        System.out.println(values + " " + Decimal.valueOf(values).growthRates().avg());
        System.out.println(detrended + " " + Decimal.valueOf(detrended).growthRates().avg());
        //CHECKSTYLE:ON
    }

    @Test
    public void testDetrendRelativePositiveToNegative() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 100; i < 120; i++) {
            values.add(new Decimal(i));
        }
        final List<Decimal> detrended = Decimal.valueOf(values).detrendRelative().values();
        final List<Decimal> detrendedWithAbsolute = Decimal.valueOf(detrended).detrendAbsolute().values();
        //CHECKSTYLE:OFF
        System.out.println(values + " " + Decimal.valueOf(values).growthRates().avg());
        System.out.println(detrended + " " + Decimal.valueOf(detrended).growthRates().avg());
        System.out.println(detrendedWithAbsolute + " " + Decimal.valueOf(detrendedWithAbsolute).growthRates().avg());
        //CHECKSTYLE:ON
    }

    @Test
    public void testDetrendRelativePositive() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 100; i >= 80; i--) {
            values.add(new Decimal(i));
        }
        final List<Decimal> detrended = Decimal.valueOf(values).detrendRelative().values();
        final List<Decimal> detrendedWithAbsolute = Decimal.valueOf(detrended).detrendAbsolute().values();
        //CHECKSTYLE:OFF
        System.out.println(values + " " + Decimal.valueOf(values).growthRates().avg());
        System.out.println(detrended + " " + Decimal.valueOf(detrended).growthRates().avg());
        System.out.println(detrendedWithAbsolute + " " + Decimal.valueOf(detrendedWithAbsolute).growthRates().avg());
        //CHECKSTYLE:ON
    }

    @Test
    public void testSerialize() {
        final Decimal positive = new Decimal("1");
        Assertions.checkTrue(positive.isPositive());
        final Decimal deepClone = Objects.deepClone(positive);
        Assertions.checkTrue(deepClone.isPositive());
    }

}
