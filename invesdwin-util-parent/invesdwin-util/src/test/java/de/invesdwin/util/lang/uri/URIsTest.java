package de.invesdwin.util.lang.uri;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class URIsTest {

    @Test
    public void testIsDownloadPossible() {
        Assertions.assertThat(URIs.connect("https://invesdwin.de").isDownloadPossible()).isTrue();
    }

    @Test
    public void testLastModified() {
        Assertions.assertThat(URIs.connect("https://invesdwin.de").lastModified() > 0).isTrue();
    }

    @Test(expected = FileNotFoundException.class)
    public void test404() throws IOException {
        URIs.connect("https://invesdwin.de/asdfasdf").downloadThrowing();
    }

    @Test
    public void testBaseUri() {
        final String baseUri = "http://localhost:8080";
        Assertions.assertThat(URIs.getBasis(baseUri + "/bla/some.wsdl")).isEqualTo(baseUri);
    }

}
