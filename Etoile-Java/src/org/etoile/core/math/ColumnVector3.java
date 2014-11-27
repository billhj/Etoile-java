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
public class ColumnVector3 extends AbstractColumnVector<ColumnVector3> {

    public ColumnVector3() {
        super(3);
    }

    public ColumnVector3(double x, double y, double z) {
        this();
        set(0, x);
        set(1, y);
        set(2, z);
    }

    @Override
    ColumnVector3 copyData(MatrixBase arv) {
        ColumnVector3 v = new ColumnVector3();
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
    
    public ColumnVector3 cross3(ColumnVector3 v){
        ColumnVector3 c = new ColumnVector3();
        double x = y() * v.z() - z() * v.y();
        double y = z() * v.x() - x() * v.z();
        double z = x() * v.y() - y() * v.x();
        c.set(0, x);
        c.set(1, y);
        c.set(2, z);
        return c;
    }
}
