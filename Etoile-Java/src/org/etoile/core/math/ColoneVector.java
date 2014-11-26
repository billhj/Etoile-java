/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.core.math;

/**
 *
 * @author Jing Huang
 *  [v;v;v]
 * @param <V>
 */
public abstract class ColoneVector<V extends ColoneVector> extends MatrixBase{

    public ColoneVector(int rows) {
        super(rows, 1);
    }
    
    abstract V copyData(MatrixBase arv);
    
    public int getDimension(){
        return getRows();
    }
    
    public void set(int index, double v){
        this.set(index, 1, v);
    }
    
    public void set(double[] v){
        for(int i = 0; i < _rows; ++i){
            set(i, v[i]);
        }
    }
    
    public V add(V a){
        return copyData(super.add(a));
    }
    
    public V substract(V a) {
        return copyData(super.substract(a));
    }

    @Override
    public V multiple(double v) {
        return copyData(super.multiple(v));
    }

    @Override
    public V divide(double v) {
        return copyData(super.divide(v));
    }
}
