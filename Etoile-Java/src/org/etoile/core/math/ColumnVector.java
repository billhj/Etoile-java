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
public class ColumnVector extends AbstractColumnVector<ColumnVector> {

    public ColumnVector(int rows) {
        super(rows);
    }

    @Override
    ColumnVector copyData(MatrixBase arv) {
        ColumnVector v = new ColumnVector(arv.getRows());
        v.set(arv);
        return v;
    }

}
