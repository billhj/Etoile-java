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
public class RowVector extends AbstractRowVector<RowVector> {

    public RowVector(int colones) {
        super(colones);
    }

    @Override
    public RowVector copyData(MatrixBase arv) {
        RowVector v = new RowVector(arv.getColumns());
        v.set(arv);
        return v;
    }

}
