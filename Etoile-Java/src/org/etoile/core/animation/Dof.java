/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.etoile.core.animation;

import org.etoile.core.math.ColumnVector3;

/**
 *
 * @author Jing Huang
 */
public class Dof {
    
    public Dof(){
    }
    
    public Dof(double value){
        _value = value;
    }
    
    public Dof(double value, double minV, double maxV){
        _value = value;
        _minValue = minV;
        _maxValue = maxV;
    }
    
    void setName(String name){
        _name = name;
    }

    public String getName() {
        return _name;
    }
    
    public double getValue() {
        return _value;
    }

    public void setValue(double _value) {
        this._value = _value;
    }

    public double getMinValue() {
        return _minValue;
    }

    public void setMinValue(double _minValue) {
        this._minValue = _minValue;
    }

    public double getMaxValue() {
        return _maxValue;
    }

    public void setMaxValue(double _maxValue) {
        this._maxValue = _maxValue;
    }

    public double getTorque() {
        return _torque;
    }

    public void setTorque(double _torque) {
        this._torque = _torque;
    }

    public double getMinTorque() {
        return _minTorque;
    }

    public void setMinTorque(double _minTorque) {
        this._minTorque = _minTorque;
    }

    public double getMaxTorque() {
        return _maxTorque;
    }

    public void setMaxTorque(double _maxTorque) {
        this._maxTorque = _maxTorque;
    }

    public ColumnVector3 getAxis() {
        return _axis;
    }

    public void setAxis(ColumnVector3 axis) {
        this._axis.set(axis.get(0), axis.get(1), axis.get(2));
    }
    
    public static Dof X(){
        Dof dof = new Dof();
        dof._axis.set(1, 0, 0);
        return dof;
    }
    
    public static Dof Y(){
        Dof dof = new Dof();
        dof._axis.set(0, 1, 0);
        return dof;
    }
    
    public static Dof Z(){
        Dof dof = new Dof();
        dof._axis.set(0, 0, 1);
        return dof;
    }
    
    public void copy(Dof dof){
        setValue(dof._value);
        setMaxValue(dof._maxValue);
        setMinValue(dof._minValue);
        setAxis(dof._axis);
        setTorque(dof._torque);
        setMaxTorque(dof._maxTorque);
        setMinTorque(dof._minTorque);
    }
    
    @Override
    public Dof clone(){
        Dof dof = new Dof(_value, _minValue, _maxValue);
        dof.setAxis(_axis);
        dof.setTorque(_torque);
        dof.setMaxTorque(_maxTorque);
        dof.setMinTorque(_minTorque);
        return dof;
    }
    
    protected String _name;
    protected double _value;
    protected double _minValue = Double.NEGATIVE_INFINITY;
    protected double _maxValue = Double.POSITIVE_INFINITY;
    protected double _torque;
    protected double _minTorque = Double.NEGATIVE_INFINITY;
    protected double _maxTorque = Double.POSITIVE_INFINITY;
    protected ColumnVector3 _axis = new ColumnVector3();
}
