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
public class MatrixNN extends AbstractMatrixNN<MatrixNN>{

    public MatrixNN(int dimension) {
        super(dimension);
    }

    @Override
    public MatrixNN copyData(MatrixBase arv) {
        MatrixNN m = new MatrixNN(arv.getRows());
        m.set(arv);
        return m;
    }
    
}
