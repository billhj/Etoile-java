/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.core.math;

/**
 *
 * @author Jing Huang
 *          [v,v,v]
 * @param <V>
 */
public abstract class RowVector<V extends RowVector> extends MatrixBase{

    public RowVector(int colones) {
        super(1, colones);
    }
    
    public abstract V copyData(MatrixBase arv);
    
    public int getDimension(){
        return getColones();
    }
    
    public void set(int index, double v){
        this.set(1, index, v);
    }
    
    public double get(int index){
        return super.get(1, index);
    }
    
    public void set(double[] v){
        for(int i = 0; i < _colones; ++i){
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
    
    public double dot(V v) {
        assert(this.getDimension() == v.getDimension());
        double answer = 0;
        for(int i = 0; i < v.getDimension(); ++i){
            answer += get(i) * v.get(i);
        }
        return answer;
    }
}
