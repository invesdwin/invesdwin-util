package de.invesdwin.util.streams.sound;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.streams.closeable.Closeables;

@ThreadSafe
public class SoundPlayerRun implements Closeable {

    private volatile SoundPlayerRunFinalizer finalizer;

    public SoundPlayerRun(final IAudioInputStreamProvider provider) {
        try {
            final AudioInputStream in = provider.getAudioInputStream();
            final DataLine.Info info = new DataLine.Info(Clip.class, in.getFormat());
            final Clip clip = (Clip) AudioSystem.getLine(info);

            finalizer = new SoundPlayerRunFinalizer(in, clip);
            finalizer.register(this);

            clip.open(in);
            clip.start();
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        final SoundPlayerRunFinalizer finalizerCopy = finalizer;
        if (finalizerCopy == null) {
            return false;
        }
        final Clip clipCopy = finalizerCopy.clip;
        if (clipCopy == null) {
            return false;
        }
        return clipCopy.isRunning();
    }

    public void stop() {
        final SoundPlayerRunFinalizer finalizerCopy = finalizer;
        if (finalizerCopy == null) {
            return;
        }
        final Clip clipCopy = finalizerCopy.clip;
        if (clipCopy == null) {
            return;
        }
        clipCopy.stop();
    }

    @Override
    public void close() {
        final SoundPlayerRunFinalizer finalizerCopy;
        synchronized (this) {
            finalizerCopy = finalizer;
            if (finalizerCopy == null) {
                return;
            }
            finalizer = null;
        }
        final Clip clipCopy = finalizerCopy.clip;
        clipCopy.stop();
        Closeables.closeAsync(finalizerCopy);
    }

    private static final class SoundPlayerRunFinalizer extends AFinalizer {

        private AudioInputStream in;
        private Clip clip;

        private SoundPlayerRunFinalizer(final AudioInputStream in, final Clip clip) {
            this.in = in;
            this.clip = clip;
        }

        @Override
        protected void clean() {
            if (clip != null) {
                clip.close();
                clip = null;
            }
            if (in != null) {
                Closeables.closeQuietly(in);
                in = null;
            }
        }

        @Override
        protected boolean isCleaned() {
            return in == null;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

    }

}
