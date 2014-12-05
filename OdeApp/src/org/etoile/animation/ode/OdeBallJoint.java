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
package org.etoile.animation.ode;

import org.ode4j.math.DVector3;
import org.ode4j.ode.DAMotorJoint;
import org.ode4j.ode.DAMotorJoint.AMotorMode;
import org.ode4j.ode.DBallJoint;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DJoint;
import org.ode4j.ode.DJoint.PARAM_N;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

/**
 *
 * @author Jing Huang
 * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class OdeBallJoint /*extends OdeJoint*/ {
/*
    protected DAMotorJoint _motor;
    protected DVector3 _axis1 = new DVector3(1, 0, 0);
    protected DVector3 _axis2 = new DVector3(0, 1, 0);
    protected DVector3 _axis3 = new DVector3(0, 0, 1);

    public OdeBallJoint(String name, DWorld world, DJointGroup group, int id) {
        super(name, world, group, id);
        _jointype = JointType.BALL;
    }

    @Override
    protected void createJoint() {
        _joint = OdeHelper.createBallJoint(_world, _group);
        _motor = OdeHelper.createAMotorJoint(_world, _group);
    }

    @Override
    public void setParameter(DJoint.PARAM_N p, double v) {
        super.setParameter(p, v);
        _motor.setParam(p, v);
    }

    @Override
    public void attachChildToParent(OdeRigidBody b1, OdeRigidBody b2) {
        _body1 = b1;
        _body2 = b2;
        DBody box1 = null;
        DBody box2 = null;
        if (b1 != null) {
            box1 = b1.getBody();
        } else {
            box2 = null;
        }
        if (b2 != null) {
            box2 = b2.getBody();
        } else {
            box2 = null;
        }
        _joint.attach(box1, box2);
        _motor.attach(box1, box2);
    }

    @Override
    public double[] getJointAxis(int axis) {
        double[] res = new double[3];
        DVector3 v = null;
        if (axis == 0) {
            v = _axis1;
        } else if (axis == 1) {
            v = _axis2;
        } else {
            v = _axis3;
        }
        res[0] = v.get0();
        res[1] = v.get1();
        res[2] = v.get2();
        return res;
    }

    @Override
    public void setJointAnchor(int anchor, double x, double y, double z) {
        if (anchor == 0) {
            ((DBallJoint) _joint).setAnchor(x, y, z);
        } else if (anchor == 1) {
            ((DBallJoint) _joint).setAnchor2(x, y, z);
        }
        _motor.setMode(AMotorMode.dAMotorEuler);
        _motor.setNumAxes(3);
        _motor.setAxis(0, 1, _axis1.get0(), _axis1.get1(), _axis1.get2());
        _motor.setAxis(1, 1, _axis2.get0(), _axis2.get1(), _axis2.get2());
        _motor.setAxis(2, 1, _axis3.get0(), _axis3.get1(), _axis3.get2());

        // XXX: make these changeable, for example using the XML
        _motor.setParam(PARAM_N.dParamFudgeFactor1, 0.8f);
        _motor.setParam(PARAM_N.dParamFudgeFactor2, 0.8f);
        _motor.setParam(PARAM_N.dParamFudgeFactor3, 0.8f);

        _motor.setParam(PARAM_N.dParamStopCFM1, 0.2f);
        _motor.setParam(PARAM_N.dParamStopCFM2, 0.2f);
        _motor.setParam(PARAM_N.dParamStopCFM3, 0.2f);
    }

    @Override
    public double[] getJointAnchor(int anchor) {
        double[] res = new double[3];
        DVector3 v = new DVector3();

        if (anchor == 0) {
            ((DBallJoint) _joint).getAnchor(v);
        } else if (anchor == 1) {
            ((DBallJoint) _joint).getAnchor2(v);
        }
        res[0] = v.get0();
        res[1] = v.get1();
        res[2] = v.get2();
        return res;
    }
    
    @Override
    public void getTorqueBody1(double torque[]) {
        torque[0] = _motor.getFeedback().t1.get0();
        torque[1] = _motor.getFeedback().t1.get1();
        torque[2] = _motor.getFeedback().t1.get2();
    }

    @Override
    public void getTorqueBody2(double torque[]) {
        torque[0] = _motor.getFeedback().t2.get0();
        torque[1] = _motor.getFeedback().t2.get1();
        torque[2] = _motor.getFeedback().t2.get2();
    }

    @Override
    public void getForceBody2(double force[]) {
        force[0] = _motor.getFeedback().f2.get0();
        force[1] = _motor.getFeedback().f2.get1();
        force[2] = _motor.getFeedback().f2.get2();
    }

    @Override
    public void getForceBody1(double force[]) {
        force[0] = _motor.getFeedback().f1.get0();
        force[1] = _motor.getFeedback().f1.get1();
        force[2] = _motor.getFeedback().f1.get2();
    }

    public void addTorque(double torque0, double torque1, double torque2) {
        if (Double.isNaN(torque0) || Double.isNaN(torque1) || Double.isNaN(torque2)) {
            throw new RuntimeException("Adding torque componenent with NaN force (" + torque0 + "," + torque1 + "," + torque2 + ")");
        }
        if (Double.isInfinite(torque0) || Double.isInfinite(torque1) || Double.isInfinite(torque2)) {
            throw new RuntimeException("Adding torque componenent with infinite force (" + torque0 + "," + torque1 + "," + torque2 + ")");
        }
        _motor.addTorques(torque0, torque1, torque2);
    }

    public double getAngle(int axis) {
        return _motor.getAngle(axis);
    }

    public void setAngle(int axis, double v) {
        _motor.setAngle(axis, v);
    }

    @Override
    public QuaternionD getRotation() {
        QuaternionD res;
        QuaternionD ax0 = new QuaternionD();
        //order inverse solution, not sure which order it is in ode for euler
        ax0.setAxisAngle(_axis1, getAngle(0));
        QuaternionD ax1 = new QuaternionD();
        ax1.setAxisAngle(_axis2, getAngle(1));
        QuaternionD ax2 = new QuaternionD();
        ax2.setAxisAngle(_axis3, getAngle(2));
        res = QuaternionD.multiplication(ax0, ax1);
        res = QuaternionD.multiplication(res, ax2);
        return res;
    }

    public DAMotorJoint getMotor() {
        return _motor;
    }
  
    public DVector3 getAxis1() {
        return _axis1;
    }

    public DVector3 getAxis2() {
        return _axis2;
    }

    public DVector3 getAxis3() {
        return _axis3;
    }

    @Override
    public void setJointMin(int axis, double min) {
        switch (axis) {
            case 0:
                _motor.setParam(PARAM_N.dParamLoStop1, min);
                break;
            case 1:
                _motor.setParam(PARAM_N.dParamLoStop2, min);
                break;
            case 2:
                _motor.setParam(PARAM_N.dParamLoStop3, min);
                break;
            default:
                break;
        }
    }

    @Override
    public void setJointMax(int axis, double max) {
        switch (axis) {
            case 0:
                _motor.setParam(PARAM_N.dParamHiStop1, max);
                break;
            case 1:
                _motor.setParam(PARAM_N.dParamHiStop2, max);
                break;
            case 2:
                _motor.setParam(PARAM_N.dParamHiStop3, max);
                break;
            default:
                break;
        }
    }

    @Override
    public DBallJoint getJoint() {
        return (DBallJoint)_joint;
    }

    @Override
    public void setJointAxis(int axis, double x, double y, double z) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/
}
