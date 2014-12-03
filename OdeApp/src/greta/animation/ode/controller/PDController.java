/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package greta.animation.ode.controller;

/**
 *
 * @author Jing Huang
 */
public class PDController {
    public double _friction = 0;
    public double _desireAngle;
    public double _desireVelocity;
    public double _kP;  //propotional parameter
    public double _kD; //derivative parameter
    public double _torqueOutput;
    
    public double compute(double currentAngle, double currentvelocity){
        _torqueOutput = (_desireAngle - currentAngle) * _kP + (_desireVelocity - currentvelocity) * _kD - _friction * currentvelocity;
        return _torqueOutput;
    }
}
