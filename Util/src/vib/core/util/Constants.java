/*
 *  This file is part of VIB (Virtual Interactive Behaviour).
 */
package vib.core.util;

/**
 * This class contains several constants
 * @author Andre-Marie Pez
 */
public final class Constants {
    private Constants(){
        //can not be instanciate
    }

    public static final int FRAME_PER_SECOND = 25;
    public static final int FRAME_DURATION_MILLIS = 1000/FRAME_PER_SECOND;
    public static final double FRAME_DURATION_SECONDS = 1.0/FRAME_PER_SECOND;

    //TODO: move enums from vib.core.util.enums to here and perhaps simplify these enums

}
