package de.invesdwin.util.streams.sound;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

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
            final AudioFormat outFormat = AudioSystems.getOutFormat(in.getFormat());
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(outFormat, in);
            return audioInputStream;
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
