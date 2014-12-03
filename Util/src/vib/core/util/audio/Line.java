/*
 * This file is part of VIB (Virtual Interactive Behaviour).
 */
package vib.core.util.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Andre-Marie Pez
 */
public class Line implements AudioOutput{

    private SourceDataLine sdl;
    private byte[] currentBuffer;
    private Thread lineUpdater;
    public Line() {
        try {
            sdl = AudioSystem.getSourceDataLine(Audio.VIB_AUDIO_FORMAT);
            sdl.open(Audio.VIB_AUDIO_FORMAT, Mixer.BUFFER_SIZE);
            currentBuffer = new byte[Mixer.BUFFER_SIZE];
            lineUpdater = new Thread() {
                @Override
                public void run() {
                    sdl.start();
                    while (true) {
                        sdl.write(currentBuffer, 0, Mixer.BUFFER_SIZE);
                    }
                }
            };
            lineUpdater.setDaemon(true);
            lineUpdater.start();
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
    }
    @Override
    public void setCurrentAudio(byte[] current) {
        this.currentBuffer = current;
    }

    @Override
    protected void finalize() throws Throwable {
        lineUpdater.interrupt();
        sdl.stop();
        sdl.close();
        super.finalize();
    }

}
