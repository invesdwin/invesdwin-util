package de.invesdwin.util.test;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

@ThreadSafe
public class ParallelTest4 extends AParallelTest {

    @Test
    public void test10() {
        super.test("test10");
    }

    @Test
    public void test11() {
        super.test("test11");
    }

    @Test
    public void test12() {
        super.test("test12");
    }

}
