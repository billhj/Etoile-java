/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.core.math;

/**
 *
 * @author Jing Huang
 */
public class ColoneVector3 extends ColoneVector<ColoneVector3> {

    public ColoneVector3() {
        super(3);
    }

    public ColoneVector3(double x, double y, double z) {
        this();
        set(0, x);
        set(1, y);
        set(2, z);
    }

    @Override
    ColoneVector3 copyData(MatrixBase arv) {
        ColoneVector3 v = new ColoneVector3();
        v.set(arv);
        return v;
    }

    public double x(){
        return get(0);
    }
    
    public double y(){
        return get(1);
    }
    
    public double z(){
        return get(2);
    }
    
    public ColoneVector3 cross3(ColoneVector3 v){
        ColoneVector3 c = new ColoneVector3();
        double x = y() * v.z() - z() * v.y();
        double y = z() * v.x() - x() * v.z();
        double z = x() * v.y() - y() * v.x();
        c.set(0, x);
        c.set(1, y);
        c.set(2, z);
        return c;
    }
}
