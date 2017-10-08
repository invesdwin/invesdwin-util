package de.invesdwin.util.lang.uri;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class AddressesTest {

    @Test
    public void testAsAddress() {
        Assertions.assertThat(Addresses.asAddress("localhost").isLoopbackAddress()).isTrue();
        Assertions.assertThat(Addresses.asAddress("127.0.0.1").isLoopbackAddress()).isTrue();
    }

}
