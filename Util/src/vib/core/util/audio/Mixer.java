/*
 * This file is part of VIB (Virtual Interactive Behaviour).
 */
package vib.core.util.audio;

import vib.core.util.IniManager;
import vib.core.util.environment.Environment;
import vib.core.util.environment.EnvironmentEventListener;
import vib.core.util.environment.LeafEvent;
import vib.core.util.environment.Node;
import vib.core.util.environment.NodeEvent;
import vib.core.util.environment.Root;
import vib.core.util.environment.TreeEvent;
import vib.core.util.environment.TreeNode;
import vib.core.util.time.Timer;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

/**
 *
 * @author Andre-Marie Pez
 */
public class Mixer extends TreeNode implements EnvironmentEventListener {

    protected static final int MIX_FRAME_RATE = 25;
    public static final int BUFFER_SIZE = Audio.VIB_AUDIO_FORMAT.getFrameSize() * (int) (Audio.VIB_AUDIO_FORMAT.getFrameRate() / MIX_FRAME_RATE);
    public static final int BYTES_PER_MILLIS = (int)(Audio.VIB_AUDIO_FORMAT.getFrameRate()/1000.0)*Audio.VIB_AUDIO_FORMAT.getFrameSize();

    private final ArrayList<MixerSource> sources;
    private double[] mixed; //mix buffer
    private byte[] currentAudioFrame;
    private ByteArrayOutputStream bos;
    private DataOutputStream dos;
    private SourceUpdater sourceUpdater;
    private Environment env;
    private ArrayList<AudioOutput> outputs;
    private ArrayList<NormalizedAudioOutput> normalizedOutputs;

    public Mixer() {
        this(null);
    }


    public Mixer(Environment env) {
        sources = new ArrayList<MixerSource>();
        mixed = new double[BUFFER_SIZE / 2];
        bos = new ByteArrayOutputStream(BUFFER_SIZE);
        dos = new DataOutputStream(bos);
        sourceUpdater = SourceUpdater.getSourceUpdater(IniManager.getGlobals().getValueString("AUDIO_3D"),this);
        setEnvironment(env);
        outputs = new ArrayList<AudioOutput>();
        normalizedOutputs = new ArrayList<NormalizedAudioOutput>();
    }

    public void addAudioOutput(AudioOutput output){
        if(output!=null){
            outputs.add(output);
        }
    }

    public void removeAudioOutput(AudioOutput output){
        outputs.remove(output);
    }

    public void addNormalizedAudioOutput(NormalizedAudioOutput output){
        if(output!=null){
            normalizedOutputs.add(output);
        }
    }

    public void removeNormalizedAudioOutput(NormalizedAudioOutput output){
        normalizedOutputs.remove(output);
    }

    /////////////////////////////////////////////////////
    //// Audio part
    /////////////////////////////////////////////////////
    private Thread player;

    public void startPlaying(){
        if(player==null){
            player = new Thread(){
                @Override
                public void run() {
                    long frameDuration = 1000/MIX_FRAME_RATE;
                    while(player==this){
                        long currentframe = Timer.getTimeMillis()/frameDuration;
                        Mixer.this.update();
                        long wait = Math.max(0, (currentframe+1)*frameDuration - Timer.getTimeMillis());
                        Timer.sleep(wait);
                    }
                }
            };
            player.setDaemon(true);
            player.start();
        }
    }

    public void stopPlaying(){
        if(player!=null){
            player = null;
        }
    }

    public void update() {
        //reset buffers
        bos.reset();
        Arrays.fill(mixed, 0);

        synchronized (sources) {
            for (MixerSource source : sources) {
                if (source.update()) {
                    for (int i = 0; i < mixed.length; ++i) {
                        mixed[i] += source.getNormalizedFrame()[i];
                    }
                }
            }
        }

        for (int i = 0; i < mixed.length; ++i) {
            try {
                dos.writeShort(Audio.unnomalize(mixed[i]));
            } catch (IOException ex) {
            }
        }

        currentAudioFrame = bos.toByteArray();

        for(AudioOutput out : outputs){
            out.setCurrentAudio(currentAudioFrame);
        }

        for(NormalizedAudioOutput out : normalizedOutputs){
            out.setCurrentNormalizedAudio(mixed);
        }
    }

    public byte[] getCurrentAudioFrame() {
        return currentAudioFrame;
    }

    public double[] getCurrentNormalizedAudioFrame() {
        return mixed;
    }

    /////////////////////////////////////////////////////
    //// Environment part
    /////////////////////////////////////////////////////
    public final void setEnvironment(Environment env) {
        if (this.env != env) {
            synchronized (sources) {
                sources.clear();
            }
            if (this.env != null) {
                this.env.removeEnvironementListener(this);
                this.env.removeNode(this);
            }
            this.env = env;
            if (this.env != null) {
                this.env.addNode(this);
                this.env.addEnvironementListener(this);
                sourceUpdater.updateMixer();
                parseAndCreateSources(this.env.getRoot());
            }
        }
    }

    private boolean itIsInThatNode(Node it, Node thatNode) {
        if (it == null) {
            return false;
        }
        if (it == thatNode) {
            return true;
        }
        if (thatNode instanceof Root) {
            return false;
        }
        return itIsInThatNode(it.getParent(), thatNode);
    }

    private boolean iAmInThatNode(Node thatNode) {
        return itIsInThatNode(this, thatNode);
    }

    private void oneNodeChanges(Node changed) {
        if (iAmInThatNode(changed)) {
            //The mixer is affected by changes: all sources are updated
            updatePlayerAndSources();
        } else {
            //perhaps one source is affected:
            synchronized (sources) {
                for (MixerSource source : sources) {
                    if (itIsInThatNode(source.getAudioNode(), changed)) {
                        //Yes it is. The source needs to be updated
                        sourceUpdater.updateSource(source);
                    }
                }
            }
        }
    }

    private void parseAndCreateSources(Node node) {
        if (node instanceof AudioTreeNode) {
            MixerSource source = new MixerSource((AudioTreeNode) node);
            source.setSourceUpdater(sourceUpdater);
            sources.add(source);
            sourceUpdater.updateSource(source);
        }
        if (node instanceof TreeNode) {
            for (Node child : ((TreeNode) node).getChildren()) {
                parseAndCreateSources(child);
            }
        }
    }

    @Override
    public void onTreeChange(TreeEvent te) {
        if (te.modifType == TreeEvent.MODIF_MOVE) {
            oneNodeChanges(te.childNode);
        } else {
            if (te.modifType == TreeEvent.MODIF_ADD) {
                parseAndCreateSources(te.childNode);
            } else {
                if (te.modifType == TreeEvent.MODIF_REMOVE) {
                    Node changed = te.childNode;
                    if (iAmInThatNode(changed)) {
                        setEnvironment(null);
                    } else {
                        synchronized (sources) {
                            ListIterator<MixerSource> sourceIterator = sources.listIterator();
                            while(sourceIterator.hasNext()) {
                                MixerSource source = sourceIterator.next();
                                if (itIsInThatNode(source.getAudioNode(), changed)) {
                                    sourceIterator.remove();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onNodeChange(NodeEvent ne) {
        oneNodeChanges(ne.node);
    }

    @Override
    public void onLeafChange(LeafEvent le) {
        //we don't care of it
    }

    private void updatePlayerAndSources() {
        //notify the SourceUpdater tha the mixer has changed
        sourceUpdater.updateMixer();

        //update all sources
        for (MixerSource source : sources) {
            sourceUpdater.updateSource(source);
        }
    }
}
