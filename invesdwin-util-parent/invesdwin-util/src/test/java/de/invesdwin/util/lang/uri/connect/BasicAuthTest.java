package de.invesdwin.util.lang.uri.connect;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.uri.header.BasicAuth;

@NotThreadSafe
public class BasicAuthTest {

    @Test
    public void testEncodeDecode() {
        final String username = "username";
        final String password = "password";
        final String encode = BasicAuth.encode(username, password);
        final BasicAuth decode = BasicAuth.decode(encode);
        Assertions.checkEquals(username, decode.getUsername());
        Assertions.checkEquals(password, decode.getPassword());
    }

}
