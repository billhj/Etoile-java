/*
 * This file is part of VIB (Virtual Interactive Behaviour).
 */

package vib.core.util.time;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines a time marker.<br/>
 * Time markers are a mapping between a time and a name of this time.<br/>
 * If a time marker has no time value, it is considered not concretized.<br/>
 * In this case, one (ore more) time marker must be add in reference to concretize it or use a Temporizer.
 * @see vib.core.util.time.Temporizer Temporizer
 * @author Andre-Marie Pez
 *
 * the following tags generate a warning in Javadoc generation because
 * they are UmlGraph tags, not javadoc tags.
 * @composed - - * vib.core.util.time.SynchPoint
 */
public class TimeMarker {

    /**
     * Constant to define an infinite time.
     */
    public static final TimeMarker INFINITY = new TimeMarker(""+Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);

    //TODO javadoc
    public static NumberFormat timeFormat = vib.core.util.IniManager.getNumberFormat();
    private static boolean useRelative = true;

    /**
     * Converts a TimeMarker to a String of the corresponding SynchPoint.<br/>
     * The result will depend also of the boolean set with the function {@code useRelativeTime}.
     * @param t TimeMarker to convert
     * @param nofound String to return if this function cannot convert
     * @param skipInfinity skip a value if it equals to TimeMarker.INFINITY
     * @return the String of the corresponding SynchPoint
     * @see #useRelativeTime(boolean)
     */
    public static String convertTimeMarkerToSynchPointString(TimeMarker t, String nofound, boolean skipInfinity){
        if(t==null) {
            return nofound;
        }
        if(useRelative){
            //here, we prefer target TimeMarkers
            SynchPoint synch = t.getFirstSynchPointWithTarget();
            if(synch!=null && !(skipInfinity && TimeMarker.INFINITY == synch.getTarget())) {
                return synch.toString();
            }
            //no target found so we try to use real value
            if(t.isConcretized() && !(skipInfinity && TimeMarker.INFINITY.equals(t))) {
                return timeFormat.format(t.getValue());
            }
            synch = t.getFirstConcreteSynchPoint();
            if(synch!=null){
                double value = synch.getValue();
                if(!(skipInfinity && TimeMarker.INFINITY.getValue() == value)) {
                    return timeFormat.format(value);
                }
            }
        }
        else{
            //here, we prefer a real value
            if(t.isConcretized() && !(skipInfinity && TimeMarker.INFINITY.equals(t))) {
                return timeFormat.format(t.getValue());
            }
            SynchPoint synch = t.getFirstConcreteSynchPoint();
            if(synch!=null){
                double value = synch.getValue();
                if(!(skipInfinity && TimeMarker.INFINITY.getValue() == value)) {
                    return timeFormat.format(value);
                }
            }
            //no real value found so we try to use taget
            synch = t.getFirstSynchPointWithTarget();
            if(synch!=null && !(skipInfinity && TimeMarker.INFINITY == synch.getTarget())) {
                return synch.toString();
            }
        }
        return nofound;
    }
    /**
     * Sets a parameter for the function {@code convertTimeMarkerToSynchPointString}.<br/>
     * If the parameter is true, the convertion will prefer relative value (with synch point), else it will prefer real value.<br/>
     * The original value is set to {@code true}.
     * @param relative the value to set
     * @see #convertTimeMarkerToSynchPointString(vib.core.util.time.TimeMarker, java.lang.String, boolean) convertTimeMarkerToSynchPointString
     */
    public static void useRelativeTime(boolean relative){
        useRelative = relative;
    }

    private String name;
    private double value;
    private boolean concretized;
    private ArrayList<SynchPoint> references;

    /**
     * Constructs a time marker with a name.<br/>
     * @param name the name of the marker
     */
    public TimeMarker(String name){
        this(name, 0, false);
    }

    /**
     * Constructs a time marker with a name and a time value.<br/>
     * @param name the name of the marker
     * @param value the time value of the marker
     */
    public TimeMarker(String name, double value){
        this(name, value, true);
    }

    private TimeMarker(String name, double value, boolean concretized){
        references = new ArrayList<SynchPoint>();
        this.name = name;
        this.value = value;
        this.concretized=concretized;
    }

    /**
     * Sets the time value.<br/>
     * Then this will be considered concretized.
     * @param value the time value
     */
    public void setValue(double value){
        this.value = value;
        concretized = true;
    }

    /**
     * Returns the time value.<br/>
     * Be carefull : if this is not concretized, the value is wrong !
     * @see #isConcretized() isConcretized()
     * @return the time value
     */
    public double getValue(){
        return value;
    }

    /**
     * Says if this is concretized.<br/>
     * Return true if the value is set. Else, it returns false.
     * @return if the time value is set
     */
    public boolean isConcretized(){
        return concretized;
    }

    /**
     * Sets a name to this time marker.
     * @param name the name of the marker
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Returns the name of this marker.
     * @return the name of this marker
     */
    public String getName(){
        return name;
    }

    /**
     * Returns if this equals an other TimeMarker<br/>
     * Two time markers are similar if they are the same object or if their time value are the same.
     * @param other an other TimeMarker
     * @return true if this equals other
     */
    public boolean equals(TimeMarker other){
        return this==other || (other!=null && concretized && other.concretized && value==other.value);
    }

    /**
     * Adds a reference to an other time marker.<br/>
     * The time of this will be considered the time of the other marker.
     * @param other the TimeMarker to refer
     * @see #concretizeByReferences()
     */
    public void addReference(TimeMarker other){
        addReference(other, 0);
    }

    /**
     * Adds a reference to an other time marker.<br/>
     * The time of this will be considered the time of the other marker plus an offset.
     * @param other the TimeMarker to refer
     * @param offset the offset from the other TimeMarker
     * @see #concretizeByReferences()
     */
    public void addReference(TimeMarker other, double offset){
        if(other != this) {
            references.add(new SynchPoint(other,offset));
        }
    }

    /**
     * Adds a reference with a synch point String.<br/>
     * This string describes a reference to an other TimeMarker.<br/>
     * four forms are aviable:<br/>
     * an absolute time : {@code 5.2}<br/>
     * only the name of the target TimeMarker : {@code "timeMarkerName"}<br/>
     * the name of the target TimeMarker plus or minus an offset : {@code "timeMarkerName + 0.53"}<br/>
     * a synch point with a time constraint :
     * {@code "after(aSynchPoint)"} (means that add an unknown offset upper or equals 0),
     * or {@code "before(aSynchPoint)"} (means that add an unknown offset lower or equals 0)<br/>
     * @param synchPoint the synch point to refer
     */
    public void addReference(String synchPoint){
        references.add(new SynchPoint(synchPoint));
    }

    /**
     * Returns the list of all SynchPoint of this.
     * @return the list of all SynchPoint
     */
    public List<SynchPoint> getReferences(){
        return references;
    }

    /**
     * Returns the first synch point that target to an other {@code TimeMarker}.<br/>
     * If this refers only to absolute time, it returns {@code null}.
     * @return the first synch point reference
     */
    public SynchPoint getFirstSynchPointWithTarget(){
        for(SynchPoint ref : references) {
            if(ref.hasTargetTimeMarker()) {
                return ref;
            }
        }
        return null;
    }
    /**
     * Returns the first concrete synch point.
     * @return the first concrete synch point
     */
    public SynchPoint getFirstConcreteSynchPoint(){
        for(SynchPoint ref : references) {
            if(ref.isConcretized()) {
                return ref;
            }
        }
        return null;
    }

    /**
     * Tries to concretize the value of this by TimeMarkers added in reference.<br/>
     * If this is not already concretised,
     * the time value of this will be the time of the first concretized reference found (plus an offset).
     * @return {@code true} if this is concretized, {@code false} otherwise
     * @see #addReference(vib.core.util.time.TimeMarker) addReference(TimeMarker)
     * @see #addReference(vib.core.util.time.TimeMarker, double) addReference(TimeMarker,double)
     * @see vib.core.util.time.SynchPoint#isConcretized() SynchPoint.isConcretized()
     */
    public boolean concretizeByReferences(){
        if(!concretized) {
            for(SynchPoint ref : references) {
                if(ref.isConcretized()){
                    this.setValue(ref.getValue());
                    break;
                }
            }
        }
        return concretized;
    }
}
