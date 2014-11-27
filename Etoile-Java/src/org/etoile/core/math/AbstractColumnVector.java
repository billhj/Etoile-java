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
public abstract class AbstractColumnVector<V extends AbstractColumnVector> extends MatrixBase{

    public AbstractColumnVector(int rows) {
        super(rows, 1);
    }
    
    abstract V copyData(MatrixBase arv);
    
    public int getDimension(){
        return getRows();
    }
    
    public void set(int index, double v){
        this.set(index, 0, v);
    }
    
    public double get(int index){
        return super.get(index, 0);
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
    public V multiply(double v) {
        return copyData(super.multiply(v));
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
    
    public double length(){
        double length = 0;
        for(int i = 0; i < getDimension(); ++i){
            double v = get(i);
            length += (v * v);
        }
        return Math.sqrt(length);
    }
    
    public void normalize(){
        double length = length();
        for(int i = 0; i < getDimension(); ++i){
            set(i,get(i)/length);
        }
    }
    
    @Override
    public String toString(){
        String out = "[\n";
        for(int i = 0; i < getDimension(); ++i){
            out +=get(i) + "\n";
        }
        out += "]\n";
        return out;
    }
}
