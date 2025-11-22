package de.invesdwin.util.test;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

@ThreadSafe
public class ParallelTest3 extends AParallelTest {

    @Test
    public void test7() {
        super.test("test7");
    }

    @Test
    public void test8() {
        super.test("test8");
    }

    @Test
    public void test9() {
        super.test("test9");
    }

}
