package de.invesdwin.util.test;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

@ThreadSafe
public class ParallelTest1 extends AParallelTest {

    @Test
    public void test1() {
        super.test("test1");
    }

    @Test
    public void test2() {
        super.test("test2");
    }

    @Test
    public void test3() {
        super.test("test3");
    }

}
