package de.invesdwin.util.streams.sound;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.apache.commons.io.IOUtils;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

@Immutable
public class CachedDelegateAudioInputStreamProvider implements IAudioInputStreamProvider {

    private final byte[] bytes;
    private AudioFormat format;
    private long frameLength;

    public CachedDelegateAudioInputStreamProvider(final IAudioInputStreamProvider delegate) {
        try (AudioInputStream audioInputStream = delegate.getAudioInputStream()) {
            format = audioInputStream.getFormat();
            frameLength = audioInputStream.getFrameLength();
            bytes = IOUtils.toByteArray(audioInputStream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AudioInputStream getAudioInputStream() {
        return new AudioInputStream(new FastByteArrayInputStream(bytes), format, frameLength);
    }

}
