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
public class Matrix44 extends MatrixNN<Matrix44> {

    public Matrix44() {
        super(4);
    }

    public ColoneVector4 multiple(ColoneVector4 v){
        ColoneVector4 answer = new ColoneVector4();
        answer.set(super.add(v));
        return null;
    }
            
    @Override
    Matrix44 copyData(MatrixBase arv) {
        Matrix44 m = new Matrix44();
        m.set(arv);
        return m;
    }
    
}
