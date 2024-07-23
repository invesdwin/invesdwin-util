package de.invesdwin.util.streams.sound;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import de.invesdwin.util.error.Throwables;

@ThreadSafe
public final class AudioSystems {

    /**
     * https://gist.github.com/finnkuusisto/2521119
     */
    private static final AudioFormat TEST_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, //linear signed PCM
            44100, //44.1kHz sampling rate
            16, //16-bit
            2, //2 channels fool
            4, //frame size 4 bytes (16-bit, 2 channel)
            44100, //same as sampling rate
            false //little-endian
    );
    private static Boolean audioAvailable;

    private AudioSystems() {}

    public static boolean isAudioAvailable() {
        if (audioAvailable == null) {
            synchronized (TEST_FORMAT) {
                if (audioAvailable == null) {
                    audioAvailable = determineAudioAvailable();
                }
            }
        }
        return audioAvailable;
    }

    private static boolean determineAudioAvailable() {
        if (!AudioSystem.isFileTypeSupported(Type.WAVE)) {
            return false;
        }
        final AudioFormat outFormat = getOutFormat(TEST_FORMAT);
        final DataLine.Info info = new DataLine.Info(Clip.class, outFormat);
        if (!AudioSystem.isLineSupported(info)) {
            return false;
        }
        return true;
    }

    /**
     * https://stackoverflow.com/questions/6045384/playing-mp3-and-wav-in-java
     */
    public static AudioFormat getOutFormat(final AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    /**
     * Throws an exception when file can not be played (might not be an audio file). Returns false if audio is not
     * supported on the operating system. Returns true if everything went well.
     */
    public static boolean validateSound(final File file) throws Exception {
        if (!isAudioAvailable()) {
            return false;
        }
        try {
            try (AudioInputStream in = AudioSystem.getAudioInputStream(file)) {
                final AudioFormat outFormat = getOutFormat(in.getFormat());
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
        return true;
    }

}
