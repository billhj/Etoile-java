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
 * quaternion represent spatial rotations similar to matrix
 */
public class Quaternion extends RowVector4 {

    public Quaternion(double x, double y, double z, double w) {
        super(x, y, z, w);
    }

    public Quaternion() {
        super(0, 0, 0, 1);
    }

    public Quaternion(Quaternion q) {
        set(q);
    }

    public Quaternion(ColumnVector3 axis, double angle) {
        setAxisAngle(axis, angle);
    }

    public void setValue(double q0, double q1, double q2, double q3) {
        _data[0][0] = q0;
        _data[0][1] = q1;
        _data[0][2] = q2;
        _data[0][3] = q3;
    }

    public void setValue(Quaternion q) {
        set(q);
    }

    public void setAxisAngle(ColumnVector3 axis, double angle) {
        double norm = axis.length();
        if (norm < 1E-8) {
            // Null rotation
            _data[0][0] = 0;
            _data[0][1] = 0;
            _data[0][2] = 0;
            _data[0][3] = 1;
        } else {
            double sin_half_angle = (double) java.lang.Math.sin(angle / 2.0);
            _data[0][0] = sin_half_angle * axis.get(0) / norm;
            _data[0][1] = sin_half_angle * axis.get(1) / norm;
            _data[0][2] = sin_half_angle * axis.get(2) / norm;
            _data[0][3] = (double) java.lang.Math.cos(angle / 2.0);
        }
        this.normalize();
    }

    public Quaternion add(Quaternion a) {
        Quaternion q = new Quaternion();
        q.set(super.add(a));
        return q;
    }

    public Quaternion substract(Quaternion a) {
        Quaternion q = new Quaternion();
        q.set(super.substract(a));
        return q;
    }

    @Override
    public Quaternion multiply(double v) {
        Quaternion q = new Quaternion();
        q.set(super.multiply(v));
        return q;
    }

    @Override
    public Quaternion divide(double v) {
        Quaternion q = new Quaternion();
        q.set(super.divide(v));
        return q;
    }

    public Quaternion multiply(Quaternion q) {
        return new Quaternion(_data[0][3] * q._data[0][0] + q._data[0][3] * _data[0][0] + _data[0][1] * q._data[0][2] - _data[0][2] * q._data[0][1],
                _data[0][3] * q._data[0][1] + q._data[0][3] * _data[0][1] + _data[0][2] * q._data[0][0] - _data[0][0] * q._data[0][2],
                _data[0][3] * q._data[0][2] + q._data[0][3] * _data[0][2] + _data[0][0] * q._data[0][1] - _data[0][1] * q._data[0][0],
                _data[0][3] * q._data[0][3] - q._data[0][0] * _data[0][0] - _data[0][1] * q._data[0][1] - _data[0][2] * q._data[0][2]);
    }

    public Quaternion conjugate() {
        return new Quaternion(-_data[0][0], -_data[0][1], -_data[0][2], _data[0][3]);
    }

    public Quaternion inverse() {
        double scalar = 1 / (_data[0][0] * _data[0][0] + _data[0][1] * _data[0][1] + _data[0][2] * _data[0][2] + _data[0][3] * _data[0][3]);
        Quaternion res = conjugate();
        res.multiplySelf(scalar);
        return res;
    }

    public ColumnVector3 multiply(ColumnVector3 v) {
        Quaternion r = new Quaternion(this);
        return r.rotate(v);
    }

    public ColumnVector3 rotate(ColumnVector3 v) {
        Quaternion vecQuat = new Quaternion(v.x(), v.y(), v.z(), 0.0f);
        Quaternion resQuat = vecQuat.multiply(inverse());
        resQuat.set(this.multiply(resQuat));
        ColumnVector3 re = new ColumnVector3(resQuat.get(0), resQuat.get(1), resQuat.get(2));
        return re;
    }

    public ColumnVector3 axis() {
        ColumnVector3 res = new ColumnVector3(_data[0][0], _data[0][1], _data[0][2]);
        double sinus = res.length();
        if (sinus > 1E-8) {
            res.divide(sinus);
        }
        return (java.lang.Math.acos(_data[0][3]) <= java.lang.Math.PI / 2.0) ? res : res.multiply(-1);
    }

    public double angle() {
        double angle = 2.0 * java.lang.Math.acos(_data[0][3]);
        return (double) (angle <= java.lang.Math.PI ? angle : 2.0 * java.lang.Math.PI - angle);
    }

    public static Quaternion lerp(Quaternion q1, Quaternion q2, double t) {
        Quaternion res = new Quaternion();
        res.set(q1.multiply(1 - t));
        res.add(q2.multiply(t));
        res.normalize();
        return res;
    }

    /**
     * *
     * spherical linear Slerp interpolation
     */
    public static Quaternion slerp(Quaternion a, Quaternion b, double t, boolean allowFlip) {
        double cosAngle = a.dot(b);

        double c1, c2;
        // Linear interpolation for close orientations
        if ((1.0 - java.lang.Math.abs(cosAngle)) < 0.01) {
            c1 = (double) 1.0 - t;
            c2 = t;
        } else {
            // Spherical interpolation
            double angle = (double) java.lang.Math.acos(java.lang.Math.abs(cosAngle));
            double sinAngle = (double) java.lang.Math.sin(angle);
            c1 = (double) java.lang.Math.sin(angle * (1.0 - t)) / sinAngle;
            c2 = (double) java.lang.Math.sin(angle * t) / sinAngle;
        }

        // Use the shortest path
        if (allowFlip && (cosAngle < 0.0)) {
            c1 = -c1;
        }
        Quaternion res = new Quaternion(c1 * a.get(0) + c2 * b.get(0), c1 * a.get(1) + c2 * b.get(1), c1 * a.get(2) + c2 * b.get(2), c1 * a.get(3) + c2 * b.get(3));
        res.normalize();
        return res;
    }

    public static Quaternion squad(Quaternion a, Quaternion tgA, Quaternion tgB, Quaternion b, double t) {
        Quaternion ab = Quaternion.slerp(a, b, t, false);
        Quaternion tg = Quaternion.slerp(tgA, tgB, t, false);
        return Quaternion.slerp(ab, tg, 2.0f * t * (1.0f - t), false);
    }

    /**
     * *
     * //! Shoemake-Bezier interpolation using De Castlejau algorithm
     *
     * @param q1
     * @param q2
     * @param a
     * @param b
     * @param t
     * @return
     */
    public static Quaternion bezier(Quaternion q1, Quaternion q2, Quaternion a, Quaternion b, double t) {
        // level 1
        Quaternion q11 = Quaternion.slerp(q1, a, t, false),
                q12 = Quaternion.slerp(a, b, t, false),
                q13 = Quaternion.slerp(b, q2, t, false);
        // level 2 and 3
        return Quaternion.slerp(Quaternion.slerp(q11, q12, t, false), Quaternion.slerp(q12, q13, t, false), t, false);
    }

    /**
     * *
     *   //! Given 3 quaternions, qn-1,qn and qn+1, calculate a control point to
     * be used in spline interpolation
     *
     * @param qnm1
     * @param qn
     * @param qnp1
     * @return
     */
    public static Quaternion spline(Quaternion qnm1, Quaternion qn, Quaternion qnp1) {
        Quaternion qni = new Quaternion(qn.inverse());
        Quaternion a = qni.substract(qnm1).log();
        Quaternion b = qni.substract(qnp1).log();
        Quaternion c = a.add(b).divide(-4);
        return qn.multiply(c.exp());
    }

    public Quaternion log() {
        double len = (double) java.lang.Math.sqrt(_data[0][0] * _data[0][0] + _data[0][1] * _data[0][1] + _data[0][2] * _data[0][2]);
        if (len < 1E-6) {
            return new Quaternion(this.get(0), this.get(1), this.get(2), (double) 0.0);
        } else {
            double coef = (double) java.lang.Math.acos(_data[0][3]) / len;
            return new Quaternion(_data[0][0] * coef, _data[0][1] * coef, _data[0][2] * coef, (double) 0.0);
        }
    }

    public Quaternion exp() {
        double theta = (double) java.lang.Math.sqrt(_data[0][0] * _data[0][0] + _data[0][1] * _data[0][1] + _data[0][2] * _data[0][2]);

        if (theta < 1E-6) {
            return new Quaternion(_data[0][0], _data[0][1], _data[0][2], (double) java.lang.Math.cos(theta));
        } else {
            double coef = (double) java.lang.Math.sin(theta) / theta;
            return new Quaternion(_data[0][0] * coef, _data[0][1] * coef, _data[0][2] * coef, (double) java.lang.Math.cos(theta));
        }
    }

    public static Quaternion lnDif(Quaternion a, Quaternion b) {
        Quaternion dif = a.inverse().multiply(b);
        dif.normalize();
        return dif.log();
    }

    public static Quaternion squaddoubleangent(Quaternion before, Quaternion center, Quaternion after) {
        Quaternion l1 = Quaternion.lnDif(center, before);
        Quaternion l2 = Quaternion.lnDif(center, after);
        Quaternion e = new Quaternion();
        for (int i = 0; i < 4; ++i) {
            e.set(i, -0.25 * (l1._data[0][i] + l2._data[0][i]));
        }
        e = center.multiply(e.exp());

        // if (Quaternion::dot(e,b) < 0.0)
        // e.negate();
        return e;
    }

    public Matrix33 getRotationMatrix() {
        Matrix33 m = new Matrix33();
        double q00 = 2.0f * _data[0][0] * _data[0][0];
        double q11 = 2.0f * _data[0][1] * _data[0][1];
        double q22 = 2.0f * _data[0][2] * _data[0][2];

        double q01 = 2.0f * _data[0][0] * _data[0][1];
        double q02 = 2.0f * _data[0][0] * _data[0][2];
        double q03 = 2.0f * _data[0][0] * _data[0][3];

        double q12 = 2.0f * _data[0][1] * _data[0][2];
        double q13 = 2.0f * _data[0][1] * _data[0][3];

        double q23 = 2.0f * _data[0][2] * _data[0][3];

        m.set(0, 0, 1.0 - q11 - q22);
        m.set(0, 1, q01 - q23);
        m.set(0, 2, q02 + q13);
        m.set(1, 0, q01 + q23);
        m.set(1, 1, 1.0 - q22 - q00);
        m.set(1, 2, q12 - q03);
        m.set(2, 0, q02 - q13);
        m.set(2, 1, q12 + q03);
        m.set(2, 2, 1.0 - q11 - q00);
        return m;
    }

    public Matrix44 getMatrix() {
        Matrix44 m = new Matrix44();
        double q00 = 2.0f * _data[0][0] * _data[0][0];
        double q11 = 2.0f * _data[0][1] * _data[0][1];
        double q22 = 2.0f * _data[0][2] * _data[0][2];

        double q01 = 2.0f * _data[0][0] * _data[0][1];
        double q02 = 2.0f * _data[0][0] * _data[0][2];
        double q03 = 2.0f * _data[0][0] * _data[0][3];

        double q12 = 2.0f * _data[0][1] * _data[0][2];
        double q13 = 2.0f * _data[0][1] * _data[0][3];

        double q23 = 2.0f * _data[0][2] * _data[0][3];

        m.set(0, 0, 1.0 - q11 - q22);
        m.set(0, 1, q01 - q23);
        m.set(0, 2, q02 + q13);
        m.set(1, 0, q01 + q23);
        m.set(1, 1, 1.0 - q22 - q00);
        m.set(1, 2, q12 - q03);
        m.set(2, 0, q02 - q13);
        m.set(2, 1, q12 + q03);
        m.set(2, 2, 1.0 - q11 - q00);
        m.set(3, 3, 1);
        return m;
    }

    public void setFromRotationMatrix(Matrix33 mat) {
        double m[][] = mat.getData();
        double tr = m[0][0] + m[1][1] + m[2][2];

        if (tr > 0) {
            double S = Math.sqrt(tr + 1.0) * 2; // S=4*qw 
            _data[0][3] = 0.25 * S;
            _data[0][0] = (m[2][1] - m[1][2]) / S;
            _data[0][1] = (m[0][2] - m[2][0]) / S;
            _data[0][2] = (m[1][0] - m[0][1]) / S;
        } else if ((m[0][0] > m[1][1]) & (m[0][0] > m[2][2])) {
            double S = Math.sqrt(1.0 + m[0][0] - m[1][1] - m[2][2]) * 2; // S=4*qx 
            _data[0][3] = (m[2][1] - m[1][2]) / S;
            _data[0][0] = 0.25 * S;
            _data[0][1] = (m[0][1] + m[1][0]) / S;
            _data[0][2] = (m[0][2] + m[2][0]) / S;
        } else if (m[1][1] > m[2][2]) {
            double S = Math.sqrt(1.0 + m[1][1] - m[0][0] - m[2][2]) * 2; // S=4*qy
            _data[0][3] = (m[0][2] - m[2][0]) / S;
            _data[0][0] = (m[0][1] + m[1][0]) / S;
            _data[0][1] = 0.25 * S;
            _data[0][2] = (m[1][2] + m[2][1]) / S;
        } else {
            double S = Math.sqrt(1.0 + m[2][2] - m[0][0] - m[1][1]) * 2; // S=4*qz
            _data[0][3] = (m[1][0] - m[0][1]) / S;
            _data[0][0] = (m[0][2] + m[2][0]) / S;
            _data[0][1] = (m[1][2] + m[2][1]) / S;
            _data[0][2] = 0.25 * S;
        }
    }
    
    public void setFromRotatedBasis(ColumnVector3 X, ColumnVector3 Y, ColumnVector3 Z) {
        double m[][] = new double[3][3];
        double normX = X.length();
        double normY = Y.length();
        double normZ = Z.length();
        for (int i = 0; i < 3; ++i) {
            m[i][0] = X.get(i) / normX;
            m[i][1] = Y.get(i) / normY;
            m[i][2] = Z.get(i) / normZ;
        }
        Matrix33 mat = new Matrix33();
        mat.set(m);
        setFromRotationMatrix(mat);
    }
}
