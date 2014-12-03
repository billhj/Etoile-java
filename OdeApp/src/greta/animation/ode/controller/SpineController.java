/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package greta.animation.ode.controller;

import greta.animation.ode.OdeHumanSpine;
import greta.animation.ode.OdeJoint;

/**
 *
 * @author Jing Huang
 */
public class SpineController implements BaseController{
    String _name;
    int _index;
    UniversalJointController[] _controllers;
    boolean _active = true;
    double _kP = 360;
    double _kd = 2 * Math.sqrt(_kP);
    OdeHumanSpine _spine;
    
    double _intensity0 = 20;
    double _intensity1 = 60.1;
    double _intensity2 = 40.1;
    double _s = 0.1;
    public SpineController(OdeHumanSpine spine){
        _spine = spine;
        _name = "spine controller";
        spine.addController(this);
        _controllers = new UniversalJointController[3];
        OdeJoint j = spine.getJoint("vl1");
        OdeJoint j1 = spine.getJoint("vt7");
        OdeJoint j2 = spine.getJoint("vt4");
        _controllers[0] = new UniversalJointController(j);
        _controllers[1] = new UniversalJointController(j1);
        _controllers[2] = new UniversalJointController(j2);
         
        _controllers[0].setKParameter(0, _kP, _kd);
        _controllers[0].setKParameter(1, _kP, _kd);
        _controllers[1].setKParameter(0, _kP, _kd);
        _controllers[1].setKParameter(1, _kP, _kd);
        _controllers[2].setKParameter(0, _kP, _kd);
        _controllers[2].setKParameter(1, _kP, _kd);
    }
    
    double getKinematicsAngle(){
        return _spine.getBody("root").getRotation().getEulerAngleXYZ().get0();
    }
    
    @Override
    public void update(double dt) {
        if (!_active) {
            return;
        }
        double angle = getKinematicsAngle();
        angle = Math.max(angle, 0);
        OdeJoint j0 = _controllers[0].getJoint();
        double x0 = j0.getAngle(2);
        _controllers[0].setDesireAngle(0, -angle * _intensity0 * _s);
        
        OdeJoint j1 = _controllers[1].getJoint();
        double x1 = j1.getAngle(2);
        _controllers[1].setDesireAngle(0, -angle * _intensity1 * _s);
        
        OdeJoint j2 = _controllers[2].getJoint();
        double x2 = j2.getAngle(2);
        _controllers[2].setDesireAngle(0, -angle * _intensity2 * _s);
        _controllers[0].update(dt);
        _controllers[1].update(dt);
        _controllers[2].update(dt);
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

    public double getIntensity0() {
        return _intensity0;
    }

    public void setIntensity0(double intensity0) {
        this._intensity0 = intensity0;
    }

    public double getIntensity1() {
        return _intensity1;
    }

    public void setIntensity1(double intensity1) {
        this._intensity1 = intensity1;
    }

    public double getIntensity2() {
        return _intensity2;
    }

    public void setIntensity2(double intensity2) {
        this._intensity2 = intensity2;
    }
    
    
    
}
