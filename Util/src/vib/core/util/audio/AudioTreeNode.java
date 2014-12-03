/*
 * This file is part of VIB (Virtual Interactive Behaviour).
 */
package vib.core.util.audio;

import vib.core.util.Constants;
import vib.core.util.Mode;
import vib.core.util.environment.TreeNode;
import vib.core.util.id.ID;
import vib.core.util.time.Timer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Andre-Marie Pez
 */
public class AudioTreeNode extends TreeNode implements AudioPerformer{

    //all audios
    private final LinkedList<Audio> audios;

    public AudioTreeNode() {
        audios = new LinkedList<Audio>();
/*
        Leaf l = new Leaf();
        l.setSize(0.3f, 0.3f, 0.3f);
        l.setReference("debug.audio");
        addChildNode(l);*/
    }

    public AudioTreeNode(String id){
        identifier = id;
        audios = new LinkedList<Audio>();
/*
        Leaf l = new Leaf();
        l.setSize(0.3f, 0.3f, 0.3f);
        l.setReference("debug.audio");
        addChildNode(l);*/
    }

    public void addAudio(Audio audio, Mode mode) {
        synchronized (audios) {
            if (mode == Mode.replace) {
                audios.clear();
            }

            // search for overlapping cases.
            ListIterator<Audio> iter = audios.listIterator();
            while (iter.hasNext()) {
                Audio presentAudio = iter.next();
                if (presentAudio.getTimeMillis() < audio.getTimeMillis() && presentAudio.getEndMillis() > audio.getTimeMillis()) {
                    //because we cannot blend audios, we force the end of the previous audio.
                    presentAudio.setEndMillis(audio.getTimeMillis());
                    if (presentAudio.getTimeMillis() > presentAudio.getEndMillis()) {
                        //the audio is ended before started, so it is useless.
                        iter.remove();
                    }
                }
            }
            audios.add(audio);
            Collections.sort(audios, Audio.audioComparator);
            cleanEndedAudios();
        }
    }

    private void cleanEndedAudios() {
        while (!audios.isEmpty() && audios.peek().getEndMillis() < Timer.getTimeMillis()) {
            audios.poll();
        }
    }

    public Audio getCurrentAudio() {
        cleanEndedAudios();
        if (!audios.isEmpty() && audios.peek().getTimeMillis() < Timer.getTimeMillis() + Constants.FRAME_DURATION_MILLIS) {
            return audios.peek();
        }
        return null;
    }

    @Override
    public void performAudios(List<Audio> audios, ID requestId, Mode mode) {
        Collections.sort(audios,Audio.audioComparator);
        for(Audio audio : audios) {
            addAudio(audio, mode);
        }
    }
}
