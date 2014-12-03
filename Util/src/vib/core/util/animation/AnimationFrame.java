/*
 *  This file is part of VIB (Virtual Interactive Behaviour).
 */

package vib.core.util.animation;

import java.util.ArrayList;
import java.util.List;
import vib.core.util.math.Quaternion;
import vib.core.util.math.Vec3d;

/**
 *
 * @author Andre-Marie Pez
 */
public class AnimationFrame {

    int frameNumber;
    private List<Quaternion> rotations;
    private List<Vec3d> translations;

    public AnimationFrame(int rotationsSize, int translationsSize){
        rotations = new ArrayList<Quaternion>(rotationsSize);
        for(int i=0; i<rotationsSize; ++i){
            rotations.add(new Quaternion());
        }

        translations = new ArrayList<Vec3d>(translationsSize);
        for(int i=0; i<translationsSize; ++i){
            translations.add(new Vec3d());
        }
    }

    public AnimationFrame(int size){
        this(size, size);
    }

    public void setRotation(int index, Quaternion rotation){
        rotations.set(index, rotation);
    }

    public void setTranslation(int index, Vec3d translation){
        translations.set(index, translation);
    }

    public Vec3d getTranslation(int index){
        return translations.get(index);
    }

    public Quaternion getRotation(int index){
        return rotations.get(index);
    }

    public void setFrameNumber(int num){
        frameNumber = num;
    }

    public int getFrameNumber(){
        return frameNumber;
    }

    public List<Quaternion> getRotations() {
        return rotations;
    }

    public List<Vec3d> getTranslations() {
        return translations;
    }
    
    
}
