/*
 * The MIT License
 *
 * Copyright 2014 Jing Huang <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.etoile.core.math;

/**
 *
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 *          [v,v,v]
 * @param <V>
 */
public abstract class AbstractRowVector<V extends AbstractRowVector> extends MatrixBase{

    public AbstractRowVector(int colones) {
        super(1, colones);
    }
    
    public abstract V copyData(MatrixBase arv);
    
    public int getDimension(){
        return getColumns();
    }
    
    public void set(int index, double v){
        this.set(0, index, v);
    }
    
    public double get(int index){
        return super.get(0, index);
    }
    
    public void set(double[] v){
        for(int i = 0; i < _columns; ++i){
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
            out +=get(i) + " ";
        }
        out += "]\n";
        return out;
    }
}
