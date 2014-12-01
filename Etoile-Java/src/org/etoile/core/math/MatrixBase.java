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
 * MatrixBase is the basic class to build the matrix operation
 */
public class MatrixBase {

    protected double[][] _data;
    protected int _columns = 0;
    protected int _rows = 0;

    public MatrixBase(int rows, int colones) {
        _columns = colones;
        _rows = rows;
        rebuild();
    }

    public MatrixBase(double[][] data) {
        this(data.length, data[0].length);
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _columns; ++x) {
                _data[y][x] = data[y][x];
            }
        }
    }

    public MatrixBase(MatrixBase matrix) {
        this(matrix.getRows(), matrix.getColumns());
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _columns; ++x) {
                _data[y][x] = matrix.getData()[y][x];
            }
        }
    }

    /**
     * *
     * create new array
     */
    public void rebuild() {
        _data = new double[_rows][_columns];
    }

    public double[][] getData() {
        return _data;
    }

    public void setData(double[][] data) {
        this._data = data;
    }

    public int getColumns() {
        return _columns;
    }

    public void setColumns(int colone) {
        this._columns = colone;
    }

    public int getRows() {
        return _rows;
    }

    public void setRows(int row) {
        this._rows = row;
    }

    public double get(int row, int colone) {
        assert (_columns > colone && _rows > row);
        return _data[row][colone];
    }

    /**
     * *
     *
     * @param row
     * @param colone set row and colone
     */
    public void set(int row, int colone) {
        setColumns(colone);
        setRows(row);
        rebuild();
    }

    /**
     * *
     *
     * @param row
     * @param colone
     * @param v set value [row][colone] = v
     */
    public void set(int row, int colone, double v) {
        assert (_columns > colone && _rows > row);
        _data[row][colone] = v;
    }

    /**
     * *
     *
     * @param data set matrix
     */
    public void set(double[][] data) {
        set(data.length, data[0].length);
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _columns; ++x) {
                _data[y][x] = data[y][x];
            }
        }
    }

    /**
     * *
     *
     * @param matrix set matrix
     */
    public void set(MatrixBase matrix) {
        set(matrix.getRows(), matrix.getColumns());
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _columns; ++x) {
                _data[y][x] = matrix.getData()[y][x];
            }
        }
    }

    /**
     * *
     *
     * @param row
     * @param colone
     * @param v add [row][colone] + v
     */
    public void add(int row, int colone, double v) {
        assert (_columns > colone && _rows > row);
        _data[row][colone] += v;
    }

    /**
     * *
     *
     * @param row
     * @param colone
     * @param v sub [row][colone] - v
     */
    public void substract(int row, int colone, double v) {
        assert (_columns > colone && _rows > row);
        _data[row][colone] -= v;
    }

    /**
     * *
     *
     * @param row
     * @param colone
     * @param v multiply [row][colone] * v
     */
    public void multiply(int row, int colone, double v) {
        assert (_columns > colone && _rows > row);
        _data[row][colone] *= v;
    }

    /**
     * *
     *
     * @param row
     * @param colone
     * @param v divide [row][colone] / v
     */
    public void divide(int row, int colone, double v) {
        assert (_columns > colone && _rows > row);
        _data[row][colone] /= v;
    }

    /**
     * *
     *
     * @param v
     * @return m x v
     */
    public MatrixBase multiply(double v) {
        MatrixBase answer = new MatrixBase(_rows, _columns);
        for (int y = 0; y < answer.getRows(); y++) {
            for (int x = 0; x < answer.getColumns(); x++) {
                answer.set(y, x, _data[y][x] * v);
            }
        }
        return answer;
    }

    public void multiplySelf(double v) {
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColumns(); x++) {
                set(y, x, _data[y][x] * v);
            }
        }
    }

    /**
     * *
     *
     * @param v
     * @return m / v
     */
    public MatrixBase divide(double v) {
        MatrixBase answer = new MatrixBase(_rows, _columns);
        for (int y = 0; y < answer.getRows(); y++) {
            for (int x = 0; x < answer.getColumns(); x++) {
                answer.set(y, x, _data[y][x] / v);
            }
        }
        return answer;
    }

    public void divideSelf(double v) {
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColumns(); x++) {
                set(y, x, _data[y][x] / v);
            }
        }
    }

    /**
     * *
     *
     * @param matrix
     * @return multiplication
     */
    public MatrixBase multiply(MatrixBase matrix) {
        assert (_columns == matrix.getRows() && _rows == matrix.getColumns());
        MatrixBase answer = new MatrixBase(_rows, matrix.getColumns());
        for (int y = 0; y < answer.getRows(); y++) {
            for (int x = 0; x < answer.getColumns(); x++) {
                for (int i = 0; i < _columns; i++) {
                    answer.add(y, x, _data[y][i] * matrix.getData()[i][x]);
                }
            }
        }
        return answer;
    }

    /**
     * *
     *
     * @param matrix
     * @return addition
     */
    public MatrixBase add(MatrixBase matrix) {
        assert (_columns == matrix.getColumns() && _rows == matrix.getRows());
        MatrixBase answer = new MatrixBase(_rows, _columns);
        for (int y = 0; y < answer.getRows(); ++y) {
            for (int x = 0; x < answer.getColumns(); ++x) {
                answer.add(y, x, _data[y][x] + matrix.getData()[y][x]);
            }
        }
        return answer;
    }

    public void addSelf(MatrixBase matrix) {
        for (int y = 0; y < getRows(); ++y) {
            for (int x = 0; x < getColumns(); ++x) {
                _data[y][x] += matrix.getData()[y][x];
            }
        }
    }

    /**
     * *
     *
     * @param matrix
     * @return addition
     */
    public MatrixBase substract(MatrixBase matrix) {
        assert (_columns == matrix.getColumns() && _rows == matrix.getRows());
        MatrixBase answer = new MatrixBase(_rows, _columns);
        for (int y = 0; y < answer.getRows(); y++) {
            for (int x = 0; x < answer.getColumns(); x++) {
                answer.add(y, x, _data[y][x] - matrix.getData()[y][x]);
            }
        }
        return answer;
    }

    public void substractSelf(MatrixBase matrix) {
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColumns(); x++) {
                _data[y][x] -= matrix.getData()[y][x];
            }
        }
    }

    /**
     * *
     *
     * @param a
     * @param b
     * @return multiplication
     */
    public static double[][] multiply(double[][] a, double[][] b) {
        assert (a != null && b != null && a.length == b[0].length && a[0].length == b.length);
        double[][] answer = new double[a.length][b[0].length];
        for (int y = 0; y < answer.length; ++y) {
            for (int x = 0; x < answer[0].length; ++x) {
                for (int i = 0; i < a[0].length; ++i) {
                    answer[y][x] = a[y][i] * b[i][x];
                }
            }
        }
        return answer;
    }

    /**
     * *
     *
     * @return transposed matrix
     */
    public MatrixBase transpose() {
        MatrixBase answer = new MatrixBase(this._columns, this._rows);
        for (int y = 0; y < answer.getRows(); ++y) {
            for (int x = 0; x < answer.getColumns(); ++x) {
                answer.set(y, x, getData()[x][y]);
            }
        }
        return answer;
    }

    /**
     * *
     * do transpose to self
     */
    public void transposeSelf() {
        this.set(transpose());
    }

    /**
     * *
     * set Matrix trace to 1
     */
    public void setToIdentity() {
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _columns; ++x) {
                _data[y][x] = (x == y ? 1 : 0);
            }
        }
    }

    /**
     * *
     * set Matrix all to Zero
     */
    public void setToZero() {
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _columns; ++x) {
                _data[y][x] = 0;
            }
        }
    }

    /**
     * *
     * compare two Matrix if values are equal
     *
     * @param m
     * @return boolean true if equal
     */
    public boolean equals(MatrixBase m) {
        if (_rows != m.getRows() || _columns != m.getColumns()) {
            return false;
        }
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _columns; ++x) {
                if (_data[y][x] != m.getData()[y][x]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Calculates the matrix's Moore-Penrose pseudoinverse
     *
     * @return an MxN matrix which is the matrix's pseudoinverse.
     */
    public MatrixBase pseudoInverse() {

        int r, c;

        int k = 1;
        MatrixBase ak = new MatrixBase(_rows, 1);
        MatrixBase dk;
        MatrixBase ck, bk;

        MatrixBase R_plus;

        for (r = 0; r < _rows; r++) {
            ak._data[r][0] = this._data[r][0];
        }

        if (!ak.equals(0.0)) {
            R_plus = ak.transpose().multiply(1.0 / (ak.dot(ak)));
        } else {
            //R_plus= new MatrixMN(1, numCols);
            R_plus = new MatrixBase(1, _rows);  //I modified make 
        }

        while (k < this._columns) {

            for (r = 0; r < _rows; r++) {
                ak._data[r][0] = this._data[r][k];
            }

            dk = R_plus.multiply(ak);
            MatrixBase T = new MatrixBase(_rows, k);
            for (r = 0; r < _rows; r++) {
                for (c = 0; c < k; c++) {
                    T._data[r][c] = this._data[r][c];
                }
            }

            ck = ak.substract(T.multiply(dk));

            if (!ck.equals(0.0)) {
                bk = ck.transpose().multiply(1.0 / (ck.dot(ck)));
            } else {
                bk = dk.transpose().multiply(1.0 / (1.0 + dk.dot(dk))).multiply(R_plus);
            }

            MatrixBase N = R_plus.substract(dk.multiply(bk));
            R_plus = new MatrixBase(N._rows + 1, N._columns);

            for (r = 0; r < N._rows; r++) {
                for (c = 0; c < N._columns; c++) {
                    R_plus._data[r][c] = N._data[r][c];
                }
            }
            for (c = 0; c < N._columns; c++) {
                R_plus._data[R_plus._rows - 1][c] = bk._data[0][c];
            }

            k++;
        }
        return R_plus;
    }

    /**
     * Computes the dot product (or scalar product) of two matrices by
     * multiplying corresponding elements and summing all the products.
     *
     * @param m A Matrix with the same dimensions
     * @returns the dot product (scalar product)
     */
    public double dot(MatrixBase m) {
        if (_rows != m._rows || _columns != m._columns) {
            System.out.println("dimensions bad in dot()");
            System.exit(1);
        }
        double sum = 0;

        for (int r = 0; r < _rows; r++) {
            for (int c = 0; c < _columns; c++) {
                sum += this._data[r][c] * m._data[r][c];
            }
        }

        return sum;
    }
}
