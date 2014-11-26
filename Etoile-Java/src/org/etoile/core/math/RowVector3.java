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
public class RowVector3 extends RowVector<RowVector3> {

    public RowVector3() {
        super(3);
    }

    public RowVector3(double x, double y, double z) {
        this();
        set(0, x);
        set(1, y);
        set(2, z);
    }

    @Override
    RowVector3 copyData(MatrixBase arv) {
        RowVector3 v = new RowVector3();
        v.set(arv);
        return v;
    }
}
