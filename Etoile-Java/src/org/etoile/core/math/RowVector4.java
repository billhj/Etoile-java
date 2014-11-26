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
public class RowVector4 extends RowVector<RowVector4> {

    public RowVector4() {
        super(4);
    }

    public RowVector4(double x, double y, double z, double w) {
        this();
        set(0, x);
        set(1, y);
        set(2, z);
        set(3, w);
    }

    @Override
    public RowVector4 copyData(MatrixBase arv) {
        RowVector4 v = new RowVector4();
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
    
    public double w(){
        return get(3);
    }
}