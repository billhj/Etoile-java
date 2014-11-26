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
public class Matrix33 extends MatrixNN<Matrix33> {

    public Matrix33() {
        super(3);
    }

    public ColoneVector3 multiply(ColoneVector3 v){
        ColoneVector3 answer = new ColoneVector3();
        answer.set(super.add(v));
        return null;
    }
    
    @Override
    public Matrix33 copyData(MatrixBase arv) {
        Matrix33 m = new Matrix33();
        m.set(arv);
        return m;
    }
    
}
