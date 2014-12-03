/*
 * This file is part of VIB (Virtual Interactive Behaviour).
 */
package greta.animation.ode;

import org.ode4j.math.DMatrix3;
import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DVector3;

/**
 *
 * @author Jing Huang
 */
public class QuaternionD {

    private double q[] = new double[4];

    @Override
    public QuaternionD clone() {
        return new QuaternionD(this);
    }

    public QuaternionD(double q0, double q1, double q2, double q3) {
        q = new double[4];
        q[0] = q0;
        q[1] = q1;
        q[2] = q2;
        q[3] = q3;
    }

    public QuaternionD() {
        this(0, 0, 0, 1);
    }

    public QuaternionD(DVector3 axis, double angle) {
        q = new double[4];
        setAxisAngle(axis, angle);
    }


    /*
     * ! Copy ructor.
     */
    public QuaternionD(QuaternionD Q) {
        this(Q.q[0],Q.q[1],Q.q[2],Q.q[3]);
    }


    public boolean equalsTo(QuaternionD qi) {
        if (q[0] == qi.x() && q[1] == qi.y() && q[2] == qi.z() && q[3] == qi.w()) {
            return true;
        }
        return false;
    }
    /*
     * ! Sets the Quaternion as a rotation of axis \p axis and angle \p angle
     * (in radians). \p axis does not need to be normalized. A null \p axis will
     * result in an identity Quaternion.
     */

    public void setAxisAngle(DVector3 axis, double angle) {
        double norm = axis.length();
        if (norm < 1E-8) {
            // Null rotation
            q[0] = 0;
            q[1] = 0;
            q[2] = 0;
            q[3] = 1;
        } else {
            double sin_half_angle = (double) java.lang.Math.sin(angle / 2.0);
            q[0] = sin_half_angle * axis.get(0) / norm;
            q[1] = sin_half_angle * axis.get(1) / norm;
            q[2] = sin_half_angle * axis.get(2) / norm;
            q[3] = (double) java.lang.Math.cos(angle / 2.0);
        }
        this.normalize();
    }

    /*
     * ! Sets the Quaternion value. See the Quaternion(double , double , double ,
     * double ) ructor documentation.
     */
    public void setValue(double q0, double q1, double q2, double q3) {
        q[0] = q0;
        q[1] = q1;
        q[2] = q2;
        q[3] = q3;
    }

    public void setValue(QuaternionD Q) {
        for (int i = 0; i < 4; ++i) {
            q[i] = Q.q[i];
        }
    }

    public double get(int i) {
        return q[i];
    }

    public void setValue(int index, double value) {
        q[index] = value;
    }

    public void multiply(double scale) {
        q[0] = q[0] * scale;
        q[1] = q[1] * scale;
        q[2] = q[2] * scale;
        q[3] = q[3] * scale;
    }

    public void divide(double scale) {
        q[0] = q[0] / scale;
        q[1] = q[1] / scale;
        q[2] = q[2] / scale;
        q[3] = q[3] / scale;
    }

    public void add(QuaternionD v) {
        q[0] = q[0] + v.get(0);
        q[1] = q[1] + v.get(1);
        q[2] = q[2] + v.get(2);
        q[3] = q[3] + v.get(3);
    }

    public void minus(QuaternionD v) {
        q[0] = q[0] - v.get(0);
        q[1] = q[1] - v.get(1);
        q[2] = q[2] - v.get(2);
        q[3] = q[3] - v.get(3);
    }

    public static QuaternionD multiplication(QuaternionD v, double scale) {
        return new QuaternionD(
                v.get(0) * scale,
                v.get(1) * scale,
                v.get(2) * scale,
                v.get(3) * scale);
    }

    public static QuaternionD division(QuaternionD v, double scale) {
        return new QuaternionD(
                v.get(0) / scale,
                v.get(1) / scale,
                v.get(2) / scale,
                v.get(3) / scale);
    }

    public static QuaternionD addition(QuaternionD q, QuaternionD v) {
        return new QuaternionD(
                q.get(0) + v.get(0),
                q.get(1) + v.get(1),
                q.get(2) + v.get(2),
                q.get(3) + v.get(3));
    }

    public static QuaternionD substraction(QuaternionD q, QuaternionD v) {
        return new QuaternionD(
                q.get(0) - v.get(0),
                q.get(1) - v.get(1),
                q.get(2) - v.get(2),
                q.get(3) - v.get(3));
    }

    public static QuaternionD multiplication(QuaternionD a, QuaternionD b) {
        return new QuaternionD(a.q[3] * b.q[0] + b.q[3] * a.q[0] + a.q[1] * b.q[2] - a.q[2] * b.q[1],
                a.q[3] * b.q[1] + b.q[3] * a.q[1] + a.q[2] * b.q[0] - a.q[0] * b.q[2],
                a.q[3] * b.q[2] + b.q[3] * a.q[2] + a.q[0] * b.q[1] - a.q[1] * b.q[0],
                a.q[3] * b.q[3] - b.q[0] * a.q[0] - a.q[1] * b.q[1] - a.q[2] * b.q[2]);
    }

    public static QuaternionD division(QuaternionD a, QuaternionD b) {
        QuaternionD p = new QuaternionD(b);
        p.invert();
        return multiplication(a, p);
    }

    public boolean equals(QuaternionD a) {
        return q[3] == a.q[3] && q[2] == a.q[2] && q[1] == a.q[1] && q[0] == a.q[0];
    }

    public boolean notEquals(QuaternionD a) {
        return q[3] != a.q[3] || q[2] != a.q[2] || q[1] != a.q[1] || q[0] != a.q[0];
    }

    public void multiply(QuaternionD q) {
        setValue(multiplication(this, q));
    }

    public void divide(QuaternionD q) {
        this.multiply(q.inverse());
    }

    public static DVector3 multiplication(QuaternionD q, DVector3 v) {
        QuaternionD r = new QuaternionD(q);
        return r.rotate(v);
    }

    public double x() {
        return q[0];
    }

    public double y() {
        return q[1];
    }

    public double z() {
        return q[2];
    }

    public double w() {
        return q[3];
    }

    //Matrix
    void setFromRotationMatrix(DMatrix3C mat) {
        
        // Compute one plus the trace of the matrix
        double onePlusdoublerace = 1.0 + mat.get00() + mat.get11() + mat.get22();

        if (onePlusdoublerace > 1E-5) {
            // Direct computation
            double s = (double) (java.lang.Math.sqrt(onePlusdoublerace) * 2.0f);
            q[0] = (mat.get21() - mat.get12()) / s;
            q[1] = (mat.get02() - mat.get20()) / s;
            q[2] = (mat.get10() - mat.get01()) / s;
            q[3] = 0.25f * s;
        } else {
            // Computation depends on major diagonal term
            if ((mat.get00() > mat.get11()) & (mat.get00() > mat.get22())) {
                double s = (double) (java.lang.Math.sqrt(1.0 + mat.get00() - mat.get11() - mat.get22()) * 2.0);
                q[0] = 0.25f * s;
                q[1] = (mat.get01() + mat.get10()) / s;
                q[2] = (mat.get02() + mat.get20()) / s;
                q[3] = (mat.get12() - mat.get21()) / s;
            } else if (mat.get11() > mat.get22()) {
                double s = (double) (java.lang.Math.sqrt(1.0 + mat.get11() - mat.get00() - mat.get22()) * 2.0);
                q[0] = (mat.get01() + mat.get10()) / s;
                q[1] = 0.25f * s;
                q[2] = (mat.get12() + mat.get21()) / s;
                q[3] = (mat.get02() - mat.get20()) / s;
            } else {
                double s = (double) (java.lang.Math.sqrt(1.0 + mat.get22() - mat.get00() - mat.get11()) * 2.0);
                q[0] = (mat.get02() + mat.get20()) / s;
                q[1] = (mat.get12() + mat.get21()) / s;
                q[2] = 0.25f * s;
                q[3] = (mat.get01() - mat.get10()) / s;
            }
        }
        normalize();
    }

    void setFromRotatedBasis(DVector3 X, DVector3 Y, DVector3 Z) {
        double m[][] = new double[3][3];
        double normX = X.length();
        double normY = Y.length();
        double normZ = Z.length();
        for (int i = 0; i < 3; ++i) {
            m[i][0] = X.get(i) / normX;
            m[i][1] = Y.get(i) / normY;
            m[i][2] = Z.get(i) / normZ;
        }
        DMatrix3 mat = new DMatrix3();
        mat.set(m[0][0], m[0][1], m[0][2], m[1][0], m[1][1], m[1][2], m[2][0], m[2][1], m[2][2]);
        setFromRotationMatrix(mat);
    }

    //@{
    public DVector3 axis() {
        DVector3 res = new DVector3(q[0], q[1], q[2]);
        double sinus = res.length();
        if (sinus > 1E-8) {
           res = res.scale(1/sinus);
        }
        return (java.lang.Math.acos(q[3]) <= java.lang.Math.PI / 2.0) ? res : res.scale(-1);
    }

    public double angle() {
        double angle = 2.0 * java.lang.Math.acos(q[3]);
        return (double)(angle <= java.lang.Math.PI ? angle : 2.0 * java.lang.Math.PI - angle);
    }

    public DVector3 rotate(DVector3 v) {
        /*
         * glview code double q00 = 2.0l * q[0] * q[0]; double q11 = 2.0l * q[1] *
         * q[1]; double q22 = 2.0l * q[2] * q[2];
         *
         * double q01 = 2.0l * q[0] * q[1]; double q02 = 2.0l * q[0] * q[2]; double
         * q03 = 2.0l * q[0] * q[3];
         *
         * double q12 = 2.0l * q[1] * q[2]; double q13 = 2.0l * q[1] * q[3];
         *
         * double q23 = 2.0l * q[2] * q[3];
         *
         * DVector3 r = DVector3 ((1.0 - q11 - q22)*v[0] + ( q01 - q23)*v[1] + ( q02 +
         * q13)*v[2], ( q01 + q23)*v[0] + (1.0 - q22 - q00)*v[1] + ( q12 -
         * q03)*v[2], ( q02 - q13)*v[0] + ( q12 + q03)*v[1] + (1.0 - q11 -
         * q00)*v[2] );
         */
//        DVector3 vn = new DVector3(v);
        QuaternionD vecQuat = new QuaternionD(v.get0(), v.get1(), v.get2(), 0.0f);
        QuaternionD resQuat = multiplication(vecQuat, inverse());
        resQuat = multiplication(this, resQuat);
        DVector3 re = new DVector3(resQuat.get(0), resQuat.get(1), resQuat.get(2));
        return re;
    }

    public DVector3 inverseRotate(DVector3 v) {
        return inverse().rotate(v);
    }

    public QuaternionD conjugate() {
        return new QuaternionD(-q[0], -q[1], -q[2], q[3]);
    }

    public void invert() {
        q[0] = -q[0];
        q[1] = -q[1];
        q[2] = -q[2];
    }

    public QuaternionD inverse() {
        double scalar = 1 / (q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
        QuaternionD res = conjugate();
        res.multiply(scalar);
        return res;
    }

    public void negate() {
        invert();
        q[3] = -q[3];
    }

    public double norm() {
        return (double) (java.lang.Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]));
    }

    public double normalize() {
        double norm = (double) java.lang.Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
        for (int i = 0; i < 4; ++i) {
            q[i] /= norm;
        }
        return norm;
    }

    public QuaternionD normalized() {
        double Q[] = new double[4];
        double norm = (double) java.lang.Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
        for (int i = 0; i < 4; ++i) {
            Q[i] = q[i] / norm;
        }
        return new QuaternionD(Q[0], Q[1], Q[2], Q[3]);
    }



    public void getMatrix(double m[][]) {
        double q00 = 2.0f * q[0] * q[0];
        double q11 = 2.0f * q[1] * q[1];
        double q22 = 2.0f * q[2] * q[2];

        double q01 = 2.0f * q[0] * q[1];
        double q02 = 2.0f * q[0] * q[2];
        double q03 = 2.0f * q[0] * q[3];

        double q12 = 2.0f * q[1] * q[2];
        double q13 = 2.0f * q[1] * q[3];

        double q23 = 2.0f * q[2] * q[3];

        m[0][0] = 1.0 - q11 - q22;
        m[1][0] = q01 + q23;
        m[2][0] = q02 - q13;

        m[0][1] = q01 - q23;
        m[1][1] = 1.0 - q22 - q00;
        m[2][1] = q12 + q03;

        m[0][2] = q02 + q13;
        m[1][2] = q12 - q03;
        m[2][2] = 1.0 - q11 - q00;

        m[0][3] = 0.0f;
        m[1][3] = 0.0f;
        m[2][3] = 0.0f;

        m[3][0] = 0.0f;
        m[3][1] = 0.0f;
        m[3][2] = 0.0f;
        m[3][3] = 1.0f;
    }

    public DMatrix3 getRotationMatrix() {
        double q00 = 2.0f * q[0] * q[0];
        double q11 = 2.0f * q[1] * q[1];
        double q22 = 2.0f * q[2] * q[2];

        double q01 = 2.0f * q[0] * q[1];
        double q02 = 2.0f * q[0] * q[2];
        double q03 = 2.0f * q[0] * q[3];

        double q12 = 2.0f * q[1] * q[2];
        double q13 = 2.0f * q[1] * q[3];

        double q23 = 2.0f * q[2] * q[3];
        DMatrix3 m = new DMatrix3();
        m.set(1.0 - q11 - q22, q01 - q23, q02 + q13, 
                q01 + q23, 1.0 - q22 - q00, q12 - q03, 
                q02 - q13, q12 + q03, 1.0 - q11 - q00);
        return (m);
    }

    public void getRotationMatrix(double m[][]) {

        double mat[][] = new double[4][4];
        getMatrix(mat);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                m[i][j] = mat[j][i];
            }
        }
    }
 
    public static QuaternionD multipleRotation(QuaternionD q, double times) {
        DVector3 axis = q.axis();
        double angle = q.angle();
        QuaternionD q2 = new QuaternionD();
        q2.setAxisAngle(axis, angle * times);
        return q2;
    }

    public static QuaternionD divideRotation(QuaternionD q, double times) {
        DVector3 axis = q.axis();
        double angle = q.angle();
        QuaternionD q2 = new QuaternionD();
        q2.setAxisAngle(axis, angle / times);
        return q2;
    }

    /*
     * ! @name linear lerp interpolation
     */
    public static QuaternionD lerp(QuaternionD q1, QuaternionD q2, double t) {
        QuaternionD res = QuaternionD.multiplication(q1, 1 - t);
        res.add(QuaternionD.multiplication(q2, (t)));
        res.normalize();
        return res;
    }

    /*
     * ! @name spherical linear Slerp interpolation
     */
    public static QuaternionD slerp(QuaternionD a, QuaternionD b, double t, boolean allowFlip) {
        double cosAngle = QuaternionD.dot(a, b);

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
        QuaternionD res = new QuaternionD(c1 * a.get(0) + c2 * b.get(0), c1 * a.get(1) + c2 * b.get(1), c1 * a.get(2) + c2 * b.get(2), c1 * a.get(3) + c2 * b.get(3));
        res.normalize();
        return res;
    }

    public static QuaternionD squad(QuaternionD a, QuaternionD tgA, QuaternionD tgB, QuaternionD b, double t) {
        QuaternionD ab = QuaternionD.slerp(a, b, t, false);
        QuaternionD tg = QuaternionD.slerp(tgA, tgB, t, false);
        return QuaternionD.slerp(ab, tg, 2.0f * t * (1.0f - t), false);
    }


    //! Given 3 quaternions, qn-1,qn and qn+1, calculate a control point to be used in spline interpolation
    public static QuaternionD spline(QuaternionD qnm1, QuaternionD qn, QuaternionD qnp1) {
        QuaternionD qni = new QuaternionD(qn.inverse());
        QuaternionD a = QuaternionD.substraction(qni, qnm1).log();
        QuaternionD b = QuaternionD.substraction(qni, qnp1).log();
        QuaternionD c = QuaternionD.division(QuaternionD.addition(a, b), -4);
        return multiplication(qn, c.exp());
    }

    public static double dot(QuaternionD a, QuaternionD b) {
        return (double) (a.get(0) * b.get(0) + a.get(1) * b.get(1) + a.get(2) * b.get(2) + a.get(3) * b.get(3));
    }

    public QuaternionD log() {
        double len = (double) java.lang.Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2]);
        if (len < 1E-6) {
            return new QuaternionD(this.get(0), this.get(1), this.get(2), (double) 0.0);
        } else {
            double coef = (double) java.lang.Math.acos(q[3]) / len;
            return new QuaternionD(q[0] * coef, q[1] * coef, q[2] * coef, (double) 0.0);
        }
    }

    public QuaternionD exp() {
        double theta = (double) java.lang.Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2]);

        if (theta < 1E-6) {
            return new QuaternionD(q[0], q[1], q[2], (double) java.lang.Math.cos(theta));
        } else {
            double coef = (double) java.lang.Math.sin(theta) / theta;
            return new QuaternionD(q[0] * coef, q[1] * coef, q[2] * coef, (double) java.lang.Math.cos(theta));
        }
    }

    public static QuaternionD lnDif(QuaternionD a, QuaternionD b) {
        QuaternionD dif = multiplication(a.inverse(), b);
        dif.normalize();
        return dif.log();
    }

    public static QuaternionD squaddoubleangent(QuaternionD before, QuaternionD center, QuaternionD after) {
        QuaternionD l1 = QuaternionD.lnDif(center, before);
        QuaternionD l2 = QuaternionD.lnDif(center, after);
        QuaternionD e = new QuaternionD();
        for (int i = 0; i < 4; ++i) {
            e.setValue(i, (double) (-0.25 * (l1.q[i] + l2.q[i])));
        }
        e = multiplication(center, e.exp());

        // if (Quaternion::dot(e,b) < 0.0)
        // e.negate();

        return e;
    }

    //// euler x - y - z  =>  z * y * x * v
    // this may namely run xyz or zyx  in greta z * y * x * v
    public void fromEulerXYZ(double roll, double pitch, double yaw) {
        double y = yaw / (double) 2.0;
        double p = pitch / (double) 2.0;
        double r = roll / (double) 2.0;

        double sinp = (double) java.lang.Math.sin(p);
        double siny = (double) java.lang.Math.sin(y);
        double sinr = (double) java.lang.Math.sin(r);
        double cosp = (double) java.lang.Math.cos(p);
        double cosy = (double) java.lang.Math.cos(y);
        double cosr = (double) java.lang.Math.cos(r);

        q[0] = sinr * cosp * cosy - cosr * sinp * siny;
        q[1] = cosr * sinp * cosy + sinr * cosp * siny;
        q[2] = cosr * cosp * siny - sinr * sinp * cosy;
        q[3] = cosr * cosp * cosy + sinr * sinp * siny;

        normalize();

    }

    public void fromEulerXYZByAngle(double roll, double pitch, double yaw) {
        fromEulerXYZ((double) Math.toRadians(roll), (double) Math.toRadians(pitch), (double) Math.toRadians(yaw));
    }

    // z y x EULER angle,  OR x y z fixed angle checked from robotic definition
    //RX->RY->RZ  RZ * RY * RX
    public DVector3 getEulerAngleXYZ() {
        //atan2
        double roll = (double) java.lang.Math.atan2(2 * (q[3] * q[0] + q[1] * q[2]), 1 - 2 * (q[0] * q[0] + q[1] * q[1]));
        double valueP = Math.max(-1, Math.min(1, 2 * (q[3] * q[1] - q[2] * q[0])));
        double pitch = (double) java.lang.Math.asin(valueP);
        double yaw = (double) java.lang.Math.atan2(2 * (q[3] * q[2] + q[0] * q[1]), 1 - 2 * (q[1] * q[1] + q[2] * q[2]));
        double v = q[3] * q[1] - q[2] * q[0];
        if (v > 0.5) {
            v = 0.5f;
        }
        if (v < -0.5) {
            v = -0.5f;
        }

        if (v == 0.5) {
            roll = 2 * (double) java.lang.Math.atan2(q[0], q[3]);
            yaw = 0;
        } else if (v == -0.5) {
            roll = -2 * (double) java.lang.Math.atan2(q[0], q[3]);
            yaw = 0;
        }
        return new DVector3(roll, pitch, yaw);
        //arctan
        //double  roll = atan(   (2 * (q[3]* q[0] + q[1]* q[2]))  /   (1 - 2*( q[0] * q[0] + q[1] * q[1]))  );
        //double  pitch = asin(2* ( q[3] * q[1] - q[2] * q[0]  ));
        //double  yaw = atan( (2 * ( q[3] * q[2] +  q[0] * q[1]))  / (1 - 2*( q[1] * q[1] + q[2] * q[2]))   );

        //return DVector3 ( yaw, pitch ,roll);
    }

    public DVector3 getEulerAngleXYZByAngle() {
        DVector3 xyz = getEulerAngleXYZ();
        double roll = (double) Math.toDegrees(xyz.get0());
        double pitch = (double) Math.toDegrees(xyz.get1());
        double yaw = (double) Math.toDegrees(xyz.get2());
        return new DVector3(roll, pitch, yaw);
    }

    public void fromEulerZYZ(double a, double b, double c) {
        q[3] = -(double) java.lang.Math.cos((a - c) * 0.5) * (double) java.lang.Math.sin(b * 0.5);
        q[0] = -(double) java.lang.Math.sin((a - c) * 0.5) * (double) java.lang.Math.sin(b * 0.5);
        q[1] = -(double) java.lang.Math.sin((a + c) * 0.5) * (double) java.lang.Math.cos(b * 0.5);
        q[2] = (double) java.lang.Math.sin((a + c) * 0.5) * (double) java.lang.Math.cos(b * 0.5);
    }

    public DVector3 getEulerAngleZYZ() {
        double a = (double) java.lang.Math.atan2((q[3] * q[1] + q[0] * q[2]), (q[0] * q[1] - q[3] * q[2]));
        double b = (double) java.lang.Math.acos(-q[3] * q[3] - q[0] * q[0] + q[1] * q[1] + q[2] * q[2]);
        double c = -(double) java.lang.Math.atan2((q[3] * q[1] - q[0] * q[2]), (q[0] * q[1] + q[3] * q[2]));
        return new DVector3(a, b, c);
    }

    @Override
    public String toString() {
        return "  x: " + this.x() + "  y: " + this.y() + "  z: " + this.z() + "  w: " + this.w();
    }
}
