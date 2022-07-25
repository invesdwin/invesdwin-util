package de.invesdwin.util.streams.sound;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public class FileAudioInputStreamProvider implements IAudioInputStreamProvider {

    private final File soundFile;

    public FileAudioInputStreamProvider(final File soundFile) {
        this.soundFile = soundFile;
    }

    @Override
    public final AudioInputStream getAudioInputStream() {
        try {
            final AudioInputStream in = AudioSystem.getAudioInputStream(soundFile);
            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(outFormat, in);
            return audioInputStream;
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * https://stackoverflow.com/questions/6045384/playing-mp3-and-wav-in-java
     */
    public static AudioFormat getOutFormat(final AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    public static void validateSound(final File file) throws Exception {
        try {
            try (AudioInputStream in = AudioSystem.getAudioInputStream(file)) {
                final AudioFormat outFormat = FileAudioInputStreamProvider.getOutFormat(in.getFormat());
                final DataLine.Info info = new DataLine.Info(Clip.class, outFormat);
                try (Clip clip = (Clip) AudioSystem.getLine(info)) {
                    clip.open(AudioSystem.getAudioInputStream(outFormat, in));
                }
            } catch (final Throwable e) {
                throw new UnsupportedOperationException(e);
            }
        } catch (final Throwable e) {
            final Throwable cause;
            final UnsupportedOperationException unwrapped = Throwables.getCauseByType(e,
                    UnsupportedOperationException.class);
            if (unwrapped != null) {
                cause = unwrapped.getCause();
            } else {
                cause = e;
            }
            throw new Exception("Invalid Audio File [" + file.getName() + "]: " + cause.toString(), cause);
        }
    }
}
