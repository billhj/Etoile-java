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
public class Matrix44 extends AbstractMatrixNN<Matrix44> {

    public Matrix44() {
        super(4);
    }

    public ColumnVector4 multiply(ColumnVector4 v){
        ColumnVector4 answer = new ColumnVector4();
        answer.set(super.multiply(v));
        return answer;
    }
            
    @Override
    public Matrix44 copyData(MatrixBase arv) {
        Matrix44 m = new Matrix44();
        m.set(arv);
        return m;
    }
    
}
