package de.invesdwin.util.streams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.string.Strings;

@NotThreadSafe
public class NormalizedPrintStream extends PrintStream {

    public NormalizedPrintStream(final OutputStream out) {
        super(out);
    }

    public NormalizedPrintStream(final OutputStream out, final boolean autoFlush) {
        super(out, autoFlush);
    }

    public NormalizedPrintStream(final OutputStream out, final boolean autoFlush, final String encoding)
            throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    public NormalizedPrintStream(final OutputStream out, final boolean autoFlush, final Charset charset) {
        super(out, autoFlush, charset);
    }

    public NormalizedPrintStream(final String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public NormalizedPrintStream(final String fileName, final String csn)
            throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public NormalizedPrintStream(final String fileName, final Charset charset) throws IOException {
        super(fileName, charset);
    }

    public NormalizedPrintStream(final File file) throws FileNotFoundException {
        super(file);
    }

    public NormalizedPrintStream(final File file, final String csn)
            throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    public NormalizedPrintStream(final File file, final Charset charset) throws IOException {
        super(file, charset);
    }

    @Override
    public void println() {
        super.print(Strings.NEWLINE_CHAR);
    }

    @Override
    public void println(final boolean x) {
        super.print(x);
        println();
    }

    @Override
    public void println(final char x) {
        super.print(x);
        println();
    }

    @Override
    public void println(final char[] x) {
        super.print(x);
        println();
    }

    @Override
    public void println(final double x) {
        super.print(x);
        println();
    }

    @Override
    public void println(final float x) {
        super.print(x);
        println();
    }

    @Override
    public void println(final int x) {
        super.print(x);
        println();
    }

    @Override
    public void println(final long x) {
        super.print(x);
        println();
    }

    @Override
    public void println(final Object x) {
        super.print(x);
        println();
    }

    @Override
    public void println(final String x) {
        super.print(x);
        println();
    }

}
