/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.core.math;

/**
 *
 * @author Jing Huang MatrixBase is the basic class to build the matrix
 * operation
 */
public class MatrixBase {

    protected double[][] _data;
    protected int _colones = 0;
    protected int _rows = 0;

    public MatrixBase(int rows, int colones) {
        _colones = colones;
        _rows = rows;
        rebuild();
    }

    public MatrixBase(double[][] data) {
        this(data.length, data[0].length);
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _colones; ++x) {
                _data[y][x] = data[y][x];
            }
        }
    }

    public MatrixBase(MatrixBase matrix) {
        this(matrix.getRows(), matrix.getColones());
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _colones; ++x) {
                _data[y][x] = matrix.getData()[y][x];
            }
        }
    }

    /***
     * create new array
     */
    public void rebuild() {
        _data = new double[_rows][_colones];
    }

    public double[][] getData() {
        return _data;
    }

    public void setData(double[][] data) {
        this._data = data;
    }

    public int getColones() {
        return _colones;
    }

    public void setColones(int colone) {
        this._colones = colone;
    }

    public int getRows() {
        return _rows;
    }

    public void setRows(int row) {
        this._rows = row;
    }
    
    public double get(int row, int colone){
        assert (_colones > colone && _rows > row);
        return _data[row][colone];
    }

    /***
     * 
     * @param row
     * @param colone 
     * set row and colone
     */
    public void set(int row, int colone) {
        setColones(colone);
        setRows(row);
        rebuild();
    }

    /***
     * 
     * @param row
     * @param colone
     * @param v set value [row][colone] = v
     */
    public void set(int row, int colone, double v) {
        assert (_colones > colone && _rows > row);
        _data[row][colone] = v;
    }

    /***
     * 
     * @param data set matrix
     */
    public void set(double[][] data) {
        set(data.length, data[0].length);
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _colones; ++x) {
                _data[y][x] = data[y][x];
            }
        }
    }

    /***
     * 
     * @param matrix set matrix 
     */
    public void set(MatrixBase matrix) {
        set(matrix.getRows(), matrix.getColones());
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _colones; ++x) {
                _data[y][x] = matrix.getData()[y][x];
            }
        }
    }

    /***
     * 
     * @param row
     * @param colone
     * @param v add [row][colone] + v
     */
    public void add(int row, int colone, double v) {
        assert (_colones > colone && _rows > row);
        _data[row][colone] += v;
    }

    /***
     * 
     * @param row
     * @param colone
     * @param v sub [row][colone] - v
     */
    public void substract(int row, int colone, double v) {
        assert (_colones > colone && _rows > row);
        _data[row][colone] -= v;
    }

    /***
     * 
     * @param row
     * @param colone
     * @param v multiply [row][colone] * v
     */
    public void multiply(int row, int colone, double v) {
        assert (_colones > colone && _rows > row);
        _data[row][colone] *= v;
    }

    /***
     * 
     * @param row
     * @param colone
     * @param v divide [row][colone] / v
     */
    public void divide(int row, int colone, double v) {
        assert (_colones > colone && _rows > row);
        _data[row][colone] /= v;
    }
    
    /***
     * 
     * @param v
     * @return m x v
     */
    public MatrixBase multiply(double v) {
        MatrixBase answer = new MatrixBase(_rows, _colones);
        for (int y = 0; y < answer.getRows(); y++) {
            for (int x = 0; x < answer.getColones(); x++) {
               answer.set(y, x, _data[y][x] * v);
            }
        }
        return answer;
    }
    
    public void multiplySelf(double v) {
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColones(); x++) {
               set(y, x, _data[y][x] * v);
            }
        }
    }
    
    /***
     * 
     * @param v
     * @return m / v
     */
    public MatrixBase divide(double v) {
        MatrixBase answer = new MatrixBase(_rows, _colones);
        for (int y = 0; y < answer.getRows(); y++) {
            for (int x = 0; x < answer.getColones(); x++) {
               answer.set(y, x, _data[y][x] / v);
            }
        }
        return answer;
    }
    
    public void divideSelf(double v) {
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColones(); x++) {
               set(y, x, _data[y][x] / v);
            }
        }
    }

    /***
     * 
     * @param matrix
     * @return multiplication
     */
    public MatrixBase multiply(MatrixBase matrix) {
        assert (_colones == matrix.getRows() && _rows == matrix.getColones());
        MatrixBase answer = new MatrixBase(_rows, matrix.getColones());
        for (int y = 0; y < answer.getRows(); y++) {
            for (int x = 0; x < answer.getColones(); x++) {
                for (int i = 0; i < _colones; i++) {
                    answer.add(y, x, _data[y][i] * matrix.getData()[i][x]);
                }
            }
        }
        return answer;
    }
    
    /***
     * 
     * @param matrix
     * @return addition
     */
    public MatrixBase add(MatrixBase matrix) {
        assert (_colones == matrix.getColones() && _rows == matrix.getRows());
        MatrixBase answer = new MatrixBase(_rows, _colones);
        for (int y = 0; y < answer.getRows(); ++y) {
            for (int x = 0; x < answer.getColones(); ++x) {
                answer.add(y, x, _data[y][x] + matrix.getData()[y][x]); 
            }
        }
        return answer;
    }
    
    public void addSelf(MatrixBase matrix) {
        for (int y = 0; y < getRows(); ++y) {
            for (int x = 0; x < getColones(); ++x) {
                _data[y][x] += matrix.getData()[y][x]; 
            }
        }
    }
    
    /***
     * 
     * @param matrix
     * @return addition
     */
    public MatrixBase substract(MatrixBase matrix) {
        assert (_colones == matrix.getColones() && _rows == matrix.getRows());
        MatrixBase answer = new MatrixBase(_rows, _colones);
        for (int y = 0; y < answer.getRows(); y++) {
            for (int x = 0; x < answer.getColones(); x++) {
                answer.add(y, x, _data[y][x] - matrix.getData()[y][x]); 
            }
        }
        return answer;
    }
    
    public void substractSelf(MatrixBase matrix) {
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColones(); x++) {
                _data[y][x] -= matrix.getData()[y][x]; 
            }
        }
    }

    /***
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

    /***
     * 
     * @return transposed matrix
     */
    public MatrixBase transpose() {
        MatrixBase answer = new MatrixBase(this._colones, this._rows);
        for (int y = 0; y < answer.getRows(); ++y) {
            for (int x = 0; x < answer.getColones(); ++x) {
                answer.set(y, x, getData()[x][y]);
            }
        }
        return answer;
    }

    /***
     * do transpose to self
     */
    public void transposeSelf() {
        this.set(transpose());
    }

    /***
     * set Matrix trace to 1
     */
    public void setToIdentity() {
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _colones; ++x) {
                _data[y][x] = (x == y ? 1 : 0);
            }
        }
    }
    
    /***
     * set Matrix all to Zero
     */
    public void setToZero() {
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _colones; ++x) {
                _data[y][x] = 0;
            }
        }
    }
    
    /***
     * compare two Matrix if values are equal
     * @param m
     * @return boolean true if equal
     */
    public boolean equals(MatrixBase m) {
        if (_rows != m.getRows() || _colones != m.getColones()) {
            return false;
        }
        for (int y = 0; y < _rows; ++y) {
            for (int x = 0; x < _colones; ++x) {
                if (_data[y][x] != m.getData()[y][x]) {
                    return false;
                }
            }
        }
        return true;
    }
}
