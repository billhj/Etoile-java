/*
 * This file is part of VIB (Virtual Interactive Behaviour).
 */
package vib.core.util.id;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andre-Marie Pez
 * @author Ken Prepin
 */


public final class ID {

    private final long unicNumber;
    private final long time;
    private final String source;
    private final List<ID> parents;
    private final String pid;

    protected ID(long unicNumber, long time, String source, String pid){
        this.unicNumber = unicNumber;
        this.time = time;
        this.source = source;
        this.pid = pid;
        parents = new ArrayList<ID>();
    }

    protected void addParent(ID parent){
        parents.add(parent);
    }

    protected void addParents(List<ID> parents){
        this.parents.addAll(parents);
    }

    public boolean hasParent(ID parent){
        if(this==parent){
            return true;
        }
        for(ID idParent : parents){
            if(idParent.hasParent(parent)){
                return true;
            }
        }
        return false;
    }

    public long getNumber() {
        return unicNumber;
    }

    public long getTime() {
        return time;
    }

    public String getPID(){
        return pid;
    }

    public String getSource() {
        return source;
    }

    public List<ID> getParents() {
        return new ArrayList<ID>(parents);
    }

    public List<ID> getAllParents(){
        List<ID> allParents = new ArrayList<ID>(parents);
        for(ID parent : parents){
            List<ID> allGrandParents = parent.getAllParents();
            for(ID grandParent : allGrandParents){
                if( ! allParents.contains(grandParent)){
                    allParents.add(grandParent);
                }
            }
        }
        return allParents;
    }

    @Override
    public String toString() {
        return source+"_"+unicNumber;
    }

    public String getParentsToString(){
        String parentsString = "";
        for(ID parent : parents){
            parentsString += "@"+parent.toString();
        }
        return parentsString;
    }

     public String getAllParentsToString(){
        String allParentsString = "";
        for(ID parent : getAllParents()){
            allParentsString += "@"+parent.toString();
        }
        return allParentsString;
    }

}
