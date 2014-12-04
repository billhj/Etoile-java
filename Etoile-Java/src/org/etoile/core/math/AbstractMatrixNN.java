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
 * @param <M>
 */
public abstract class AbstractMatrixNN <M extends AbstractMatrixNN> extends MatrixBase{

    public AbstractMatrixNN(int dimension) {
        super(dimension, dimension);
    }
    
    public AbstractMatrixNN(MatrixBase arv) {
        this(arv.getRows());
        set(arv);
    }
    
    protected abstract M copyData(MatrixBase arv);
    
    public int getDimension(){
        return getColumns();
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
