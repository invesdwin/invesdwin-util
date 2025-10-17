package de.invesdwin.util.test;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

@ThreadSafe
public class ParallelTest2 extends AParallelTest {

    @Test
    public void test4() {
        super.test("test4");
    }

    @Test
    public void test5() {
        super.test("test5");
    }

    @Test
    public void test6() {
        super.test("test6");
    }

}
