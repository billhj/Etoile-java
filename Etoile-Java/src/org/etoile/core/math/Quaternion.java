/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.core.math;

/**
 *
 * @author Jing Huang
 */
public class Quaternion extends RowVector4 {
    public Quaternion(double x, double y, double z, double w){
        super(x, y, z, w);
    }
    
    public Quaternion(){
        super(0, 0, 0, 1);
    }
    
    public Quaternion(Quaternion q){
        set(q);
    }
    
    public Quaternion(ColoneVector3 axis, double angle) {
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
    
    public void setAxisAngle(ColoneVector3 axis, double angle) {
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
    
    public Quaternion multiply(Quaternion q){
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
        res.multiply(scalar);
        return res;
    }
    
    public ColoneVector3 multiply(ColoneVector3 v){
        return rotate(v);
    }
    
    public ColoneVector3 rotate(ColoneVector3 v) {
        Quaternion vecQuat = new Quaternion(v.x(), v.y(), v.z(), 0.0f);
        Quaternion resQuat = vecQuat.multiply(inverse());
        resQuat = this.multiply(resQuat);
        ColoneVector3 re = new ColoneVector3(resQuat.get(0), resQuat.get(1), resQuat.get(2));
        return re;
    }
    
    public ColoneVector3 axis() {
        ColoneVector3 res = new ColoneVector3(_data[0][0], _data[0][1], _data[0][2]);
        double sinus = res.length();
        if (sinus > 1E-8) {
            res.divide(sinus);
        }
        return (java.lang.Math.acos(_data[0][3]) <= java.lang.Math.PI / 2.0) ? res : res.multiply(-1);
    }

    public double angle() {
        double angle = 2.0 * java.lang.Math.acos(_data[0][3]);
        return (double)(angle <= java.lang.Math.PI ? angle : 2.0 * java.lang.Math.PI - angle);
    }
}
