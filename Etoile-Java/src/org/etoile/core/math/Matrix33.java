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
public class Matrix33 extends AbstractMatrixNN<Matrix33> {

    public Matrix33() {
        super(3);
    }

    public ColumnVector3 multiply(ColumnVector3 v){
        ColumnVector3 answer = new ColumnVector3();
        answer.set(super.multiply(v));
        return answer;
    }
    
    @Override
    public Matrix33 copyData(MatrixBase arv) {
        Matrix33 m = new Matrix33();
        m.set(arv);
        return m;
    }
    
}
