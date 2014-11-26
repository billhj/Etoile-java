/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.core.math;

/**
 *
 * @author Jing Huang
 * @param <M>
 */
public abstract class MatrixNN <M extends MatrixNN> extends MatrixBase{

    public MatrixNN(int dimension) {
        super(dimension, dimension);
    }
    
    public abstract M copyData(MatrixBase arv);
    
    public int getDimension(){
        return getColones();
    }
    
    public M add(M m){
        return copyData(super.add(m));
    }
    
    public M substract(M m) {
        return copyData(super.substract(m));
    }

    public M multiply(M m) {
        return copyData(super.multiply(m));
    }
    
    @Override
    public M multiply(double v) {
        return copyData(super.multiply(v));
    }

    @Override
    public M divide(double v) {
        return copyData(super.divide(v));
    }
}
