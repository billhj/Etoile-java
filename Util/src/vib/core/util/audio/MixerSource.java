/*
 * This file is part of VIB (Virtual Interactive Behaviour).
 */
package vib.core.util.audio;

import vib.core.util.time.Timer;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 *
 * @author Andre-Marie Pez
 */
public class MixerSource{


    //generated audio
    private boolean needToFill0;
    private double[] normalized;
    private byte[] frameBuffer;
    private DataInputStream dataInputStream;

    //current audio to play
    private Audio currentAudio;
    private AudioInputStream audioInputStream;
    private long audioTime;
    private long audioCurrentTime;

    //current source properties
    private double volumeLeft = 1;
    private double volumeRight = 1;

    //update it according to the audio node position and the owner mixer
    private SourceUpdater updater;


    //all audios
    private final AudioTreeNode audioNode;

    public MixerSource(AudioTreeNode audioNode) {
        frameBuffer = new byte[Mixer.BUFFER_SIZE];
        normalized = new double[Mixer.BUFFER_SIZE / 2];
        dataInputStream = new DataInputStream(new ByteArrayInputStream(frameBuffer));
        this.audioNode = audioNode;
        needToFill0=true;
    }

    public double[] getNormalizedFrame(){
        return normalized;
    }

    public void setVolume(double volume){
        volumeRight = volume;
        volumeLeft = volume;
    }

    public void setRightVolume(double volume){
        volumeRight = volume;
    }

    public void setLeftVolume(double volume){
        volumeLeft = volume;
    }
    public double getRightVolume(){
        return volumeRight;
    }

    public double getLeftVolume(){
        return volumeLeft;
    }
    private AudioInputStream convert(AudioFormat format, byte[] buffer) {
        return AudioSystem.getAudioInputStream(
                Audio.VIB_AUDIO_FORMAT,
                new AudioInputStream(
                    new ByteArrayInputStream(buffer),
                    format,
                    buffer.length / format.getFrameSize()));
    }

    public Audio getCurrentAudio(){
        return currentAudio;
    }

    public void setSourceUpdater(SourceUpdater updater){
        this.updater = updater;
    }
    private void setAudio(Audio audio) {
        currentAudio = audio;
        if(audio==null){
            synchronized (this){
                audioInputStream = null;
                needToFill0 = true;
            }
            return ;
        }
        AudioInputStream ais = convert(audio.getFormat(), audio.getBuffer());
        synchronized (this) {
            audioInputStream = ais;
            audioTime = audio.getTimeMillis();
            audioCurrentTime = 0;
        }
    }

    public AudioTreeNode getAudioNode(){
        return audioNode;
    }

    public boolean update() {
        synchronized (audioNode){
            Audio currentAudioFromNode = audioNode.getCurrentAudio();
            if(currentAudio != currentAudioFromNode){
                setAudio(currentAudioFromNode);
                if(updater!=null){
                    updater.updateSource(this);
                }
            }
            if(audioInputStream == null){
                if(!needToFill0){
                    return false;
                }
                needToFill0 = false;
            }
        }
        try {
            read();
            dataInputStream.reset();
            for (int i = 0; i < normalized.length; ++i) {
                normalized[i] = Audio.normalize(dataInputStream.readShort())
                        *((i&1)==0? volumeLeft : volumeRight);
            }
        } catch (Exception e) {}
        return true;
    }


    private synchronized void read() throws IOException{
        int pos = 0;
        long currentTime = Timer.getTimeMillis();
        if(currentTime<audioTime){
            pos = (int)(audioTime-currentTime)*Mixer.BYTES_PER_MILLIS;
            fill0(0, pos);
        }

        if(audioInputStream!=null  && pos< Mixer.BUFFER_SIZE) {
            synchronized (this){
                if(audioCurrentTime+audioTime < currentTime){
                    long millisToSkip = currentTime - (audioCurrentTime+audioTime);
                    audioInputStream.skip(millisToSkip*Mixer.BYTES_PER_MILLIS);
                    audioCurrentTime += millisToSkip;
                }
                else{
                    if(audioCurrentTime+audioTime > currentTime){
                        pos = (int)(((audioCurrentTime+audioTime) - currentTime)*Mixer.BYTES_PER_MILLIS);
                        shift(pos);
                    }
                }
                int read = audioInputStream.read(frameBuffer, pos, Mixer.BUFFER_SIZE-pos);
                audioCurrentTime += read/Mixer.BYTES_PER_MILLIS;
                pos += read;
                if(pos<Mixer.BUFFER_SIZE){
                    audioInputStream = null;
                    needToFill0 = true;
                }
                else{
                    return;
                }
            }
        }

        fill0(pos, Mixer.BUFFER_SIZE);
    }

    private synchronized void fill0(int offset, int length){
        for(int i=offset; i<offset+length && i<Mixer.BUFFER_SIZE; ++i){
            frameBuffer[i] = 0;
        }
    }

    private synchronized void shift(int offset){
        for(int i=0; i<offset && i<Mixer.BUFFER_SIZE-offset; ++i){
            frameBuffer[i] = frameBuffer[offset+i];
        }
    }

}
