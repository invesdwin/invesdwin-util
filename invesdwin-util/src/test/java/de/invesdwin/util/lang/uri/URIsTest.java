package de.invesdwin.util.lang.uri;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class URIsTest {

    @Test
    public void testIsDownloadPossible() {
        Assertions.assertThat(URIs.connect("http://google.de").isDownloadPossible()).isTrue();
    }

    @Test
    public void testBaseUri() {
        final String baseUri = "http://localhost:8080";
        Assertions.assertThat(URIs.getBasis(baseUri + "/bla/some.wsdl")).isEqualTo(baseUri);
    }

}
