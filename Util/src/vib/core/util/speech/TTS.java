/*
 * This file is part of VIB (Virtual Interactive Behaviour).
 */

package vib.core.util.speech;


import java.util.List;
import vib.core.util.audio.Audio;


/**
 * This interface describe how a TTS (Text To Speech) must operate
 * @author Andre-Marie Pez
 */
public interface TTS {

    /**
     * Sets a {@code Speech} to performe.
     * @param speech the {@code Speech} to performe
     * @see vib.core.util.speech.Speech Speech
     */
    public void setSpeech(Speech speech);

    /**
     * This function computes needed datas.<br/>
     * A {@code Speech} must be set before.<br/>
     * If {@code doTemporize == true} it computes the values of the {@code TimeMarkers} in the {@code Speech} set.<br/>
     * If {@code doAudio == true} it computes the audio buffer and format corresponding to the text of the {@code Speech} set.<br/>
     * The audio buffer and format can be claimed later by calling {@code getAudioBuffer()} and {@code getAudioFormat()}.<br/>
     * If {@code doPhonemes == true} it computes the list of {@code Phonems} corresponding to the {@code Speech} set.<br/>
     * This list can be claimed later by calling {@code getPhonems()}.
     * @param doTemporize {@code true} to compute the values of the {@code TimeMarkers}
     * @param doAudio {@code true} to compute the audio buffer and format
     * @param doPhonemes {@code true} to compute the list of {@code Phonemes}
     * @see #setSpeech(vib.core.util.speech.Speech) setSpeech(Speech)
     */
    public void compute(boolean doTemporize, boolean doAudio, boolean doPhonemes);

    /**
     * Returns the list of {@code Phonemes} computed by {@code compute()}.
     * @return the list of {@code Phonemes}
     */
    public List<Phoneme> getPhonemes();

    /**
     * Returns the audio computed by {@code compute()}.
     * @return the audio
     */
    public Audio getAudio();
}
