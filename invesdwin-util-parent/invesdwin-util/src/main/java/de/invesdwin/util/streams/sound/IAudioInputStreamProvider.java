package de.invesdwin.util.streams.sound;

import javax.sound.sampled.AudioInputStream;

public interface IAudioInputStreamProvider {

    AudioInputStream getAudioInputStream();

    default SoundPlayerRun play() {
        return new SoundPlayerRun(this);
    }

}
