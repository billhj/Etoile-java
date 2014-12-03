/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.etoile.animation.ode.controller;

import org.etoile.animation.ode.OdeJoint;
import org.ode4j.ode.DHingeJoint;

/**
 *
 * @author Jing Huang
 */
public class HingeJoinController implements BaseController{
    String _name;
    int _index;
    PDController _controller = new PDController();
    boolean _active = true;
    
    private static final float KP_DEFAULT = 1.5f;
    private static final float KD_DEFAULT = 0.01f;
    OdeJoint _joint;

    public HingeJoinController(String name, OdeJoint joint, int index, double desireAngle, double desireVelocity, double kP, double kD) {
        this._name = name;
        this._index = index;
        _controller._desireAngle = desireAngle;
        _controller._desireVelocity = desireVelocity;
        _controller._kP = kP;
        _controller._kD = kD;
        _joint = joint;
    }
    
    
    public HingeJoinController(OdeJoint joint){
        this(joint.getName(),joint,0,0,0,KP_DEFAULT,KD_DEFAULT);
    }
    
    public HingeJoinController(OdeJoint joint, double desireAngle){
        this(joint.getName(),joint,0,desireAngle,0,KP_DEFAULT,KD_DEFAULT);
    }
    
    @Override
    public void update(double dt) {
        if(!_active) return;
        DHingeJoint j = ((DHingeJoint)_joint.getJoint());
        double velocity = j.getAngleRate();
        double currentAngle = j.getAngle();
        //_torque = (_desireAngle - _currentAngle) * _kP - (velocity - _desireVelocity) * _kD;
        _controller._torqueOutput = _controller.compute(currentAngle, velocity);
        j.addTorque(_controller._torqueOutput);
    }
    
    public double getDesireAngle() {
        return _controller._desireAngle;
    }

    public void setDesireAngle(double _desireAngle) {
        _controller._desireAngle = _desireAngle;
    }

    public double getDesireVelocity() {
        return _controller._desireVelocity;
    }

    public void setDesireVelocity(double _desireVelocity) {
        _controller._desireVelocity = _desireVelocity;
    }


    public double getkProportional() {
        return _controller._kP;
    }

    public void setkProportional(double _kP) {
        _controller._kP = _kP;
    }

    public double getkDerivative() {
        return _controller._kD;
    }

    public void setkDerivative(double _kD) {
        _controller._kD = _kD;
    }

    public double getTorqueOutput() {
        return _controller._torqueOutput;
    } 

    public double getFriction() {
        return _controller._friction;
    }

    public void setFriction(double friction) {
        _controller._friction = friction;
    }
    
    
    
    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void setName(String name) {
        _name = name;
    }

    @Override
    public int getIndex() {
        return _index;
    }

    @Override
    public void setIndex(int index) {
        _index = index;
    }

    @Override
    public void setActive(boolean active) {
        _active = active;
    }

    @Override
    public boolean isActive() {
        return _active;
    }
    
}
