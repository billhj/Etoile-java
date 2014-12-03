/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.animation.ode;

import org.ode4j.math.DVector3;
import org.ode4j.ode.DAMotorJoint;
import org.ode4j.ode.DAMotorJoint.AMotorMode;
import org.ode4j.ode.DBallJoint;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DFixedJoint;
import org.ode4j.ode.DHingeJoint;
import org.ode4j.ode.DJoint;
import org.ode4j.ode.DJoint.PARAM_N;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.DUniversalJoint;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

/**
 *
 * @author Jing Huang
 */
public class OdeJoint {

    protected DJoint _joint;
    protected DAMotorJoint _motor;
    protected JointType _type;
    protected String _name;
    protected int _id;
    protected DJointGroup _group;
    protected DWorld _world;

    private OdeRigidBody _body1;
    private OdeRigidBody _body2;

    protected DVector3 _axis0 = new DVector3(1, 0, 0);
    protected DVector3 _axis1 = new DVector3(0, 1, 0);
    protected DVector3 _axis2 = new DVector3(0, 0, 1);

    protected double[] color = new double[]{1, 0, 0, 1.0};

    public OdeJoint(String name, JointType type, DWorld world, DJointGroup group, int id) {
        _name = name;
        _type = type;
        _world = world;
        _group = group;
        _id = id;
        createJoint();
    }

    void createJoint() {
        switch (_type) {
            case FIXED:
                _joint = OdeHelper.createFixedJoint(_world, _group);
                break;
            case HINGE:
                _joint = OdeHelper.createHingeJoint(_world, _group);
                break;
            case UNIVERSAL:
                _joint = OdeHelper.createUniversalJoint(_world, _group);
                break;
            case BALL:
                _joint = OdeHelper.createBallJoint(_world, _group);
                break;
            default:
        }
        _motor = OdeHelper.createAMotorJoint(_world, _group);
    }

    public void setParameter(DJoint.PARAM_N p, double v) {
        _joint.setParam(p, v);
        _motor.setParam(p, v);
    }

    public double getParameter(DJoint.PARAM_N p) {
        return _joint.getParam(p);
    }

    public void attach(OdeRigidBody b1, OdeRigidBody b2) {
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

    public void setJointAxis(int axis, double x, double y, double z) {
        switch (_type) {
            case FIXED:
                //((DFixedJoint)_joint).;
                break;
            case HINGE:
                if (axis == 0) {
                    ((DHingeJoint) _joint).setAxis(x, y, z);
                }
                break;
            case UNIVERSAL:
                if (axis == 0) {
                    ((DUniversalJoint) _joint).setAxis1(x, y, z);
                } else if (axis == 1) {
                    ((DUniversalJoint) _joint).setAxis2(x, y, z);
                }
                break;
            case BALL:
                //((DBallJoint)_joint);
                break;
            default:
        }
    }

    public double[] getJointAxis(int axis) {
        double[] res = new double[3];
        DVector3 v = new DVector3();
        switch (_type) {
            case FIXED:
                //(DFixedJoint)_joint.;
                break;
            case HINGE:
                ((DHingeJoint) _joint).getAxis(v);
                res[0] = v.get0();
                res[1] = v.get1();
                res[2] = v.get2();
                break;
            case UNIVERSAL:
                if (axis == 0) {
                    ((DUniversalJoint) _joint).getAxis1(v);
                } else if (axis == 1) {
                    ((DUniversalJoint) _joint).getAxis2(v);
                }
                res[0] = v.get0();
                res[1] = v.get1();
                res[2] = v.get2();
                break;
            case BALL:
                //((DBallJoint)_joint);
                break;
            default:
        }
        return res;
    }

    public void setJointAnchor(int anchor, double x, double y, double z) {
        switch (_type) {
            case FIXED:
                ((DFixedJoint) _joint).setFixed();
                break;
            case HINGE:
                ((DHingeJoint) _joint).setAnchor(x, y, z);
                break;
            case UNIVERSAL:
                ((DUniversalJoint) _joint).setAnchor(x, y, z);
                break;
            case BALL:
                if (anchor == 0) {
                    ((DBallJoint) _joint).setAnchor(x, y, z);
                } else if (anchor == 1) {
                    ((DBallJoint) _joint).setAnchor2(x, y, z);
                }
                break;
            default:
        }
        _motor.setMode(AMotorMode.dAMotorEuler);
        _motor.setNumAxes(3);
        _motor.setAxis(0, 1, _axis0.get0(), _axis0.get1(), _axis0.get2());
        _motor.setAxis(1, 1, _axis1.get0(), _axis1.get1(), _axis1.get2());
        _motor.setAxis(2, 1, _axis2.get0(), _axis2.get1(), _axis2.get2());

        // XXX: make these changeable, for example using the XML
        _motor.setParam(PARAM_N.dParamFudgeFactor1, 0.8f);
        _motor.setParam(PARAM_N.dParamFudgeFactor2, 0.8f);
        _motor.setParam(PARAM_N.dParamFudgeFactor3, 0.8f);

        _motor.setParam(PARAM_N.dParamStopCFM1, 0.2f);
        _motor.setParam(PARAM_N.dParamStopCFM2, 0.2f);
        _motor.setParam(PARAM_N.dParamStopCFM3, 0.2f);
    }

    public double[] getJointAnchor(int anchor) {
        double[] res = new double[3];
        DVector3 v = new DVector3();
        switch (_type) {
            case FIXED:
                v = (DVector3) ((DFixedJoint) _joint).getBody(0).getPosition();
                break;
            case HINGE:
                if (anchor == 0) {
                    ((DHingeJoint) _joint).getAnchor(v);
                } else if (anchor == 1) {
                    ((DHingeJoint) _joint).getAnchor2(v);
                }
                break;
            case UNIVERSAL:
                if (anchor == 0) {
                    ((DUniversalJoint) _joint).getAnchor(v);
                } else if (anchor == 1) {
                    ((DUniversalJoint) _joint).getAnchor2(v);
                }
                break;
            case BALL:
                if (anchor == 0) {
                    ((DBallJoint) _joint).getAnchor(v);
                } else if (anchor == 1) {
                    ((DBallJoint) _joint).getAnchor2(v);
                }
                break;
            default:
        }
        res[0] = v.get0();
        res[1] = v.get1();
        res[2] = v.get2();
        return res;
    }

    public void getTorqueBody1(double torque[]) {
        torque[0] = _motor.getFeedback().t1.get0();
        torque[1] = _motor.getFeedback().t1.get1();
        torque[2] = _motor.getFeedback().t1.get2();
    }

    public void getTorqueBody2(double torque[]) {
        torque[0] = _motor.getFeedback().t2.get0();
        torque[1] = _motor.getFeedback().t2.get1();
        torque[2] = _motor.getFeedback().t2.get2();
    }

    public void getForceBody2(double force[]) {
        force[0] = _motor.getFeedback().f2.get0();
        force[1] = _motor.getFeedback().f2.get1();
        force[2] = _motor.getFeedback().f2.get2();
    }

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

    public QuaternionD getRotation() {
        QuaternionD res;
        QuaternionD ax0 = new QuaternionD();
        ax0.setAxisAngle(_axis0, -getAngle(0));
        QuaternionD ax1 = new QuaternionD();
        ax1.setAxisAngle(_axis1, -getAngle(1));
        QuaternionD ax2 = new QuaternionD();
        ax2.setAxisAngle(_axis2, -getAngle(2));
        res = QuaternionD.multiplication(ax0, ax1);
        res = QuaternionD.multiplication(res, ax2);
        return res;
    }

    public DJoint getJoint() {
        return _joint;
    }

    public DAMotorJoint getMotor() {
        return _motor;
    }

    public JointType getType() {
        return _type;
    }

    public String getName() {
        return _name;
    }

    public DJointGroup getGroup() {
        return _group;
    }

    public DWorld getWorld() {
        return _world;
    }

    public DVector3 getOrderAxis0() {
        return _axis0;
    }

    public DVector3 getOrderAxis1() {
        return _axis1;
    }

    public DVector3 getOrderAxis2() {
        return _axis2;
    }

    public void setJointMin(int axis, double min) {
        switch (axis) {
            case 0:
                _motor.setParam(PARAM_N.dParamLoStop1, min);
                return;
            case 1:
                _motor.setParam(PARAM_N.dParamLoStop2, min);
                return;
            case 2:
                _motor.setParam(PARAM_N.dParamLoStop3, min);
                return;
            default:
                break;
        }
    }

    public void setJointMax(int axis, double max) {
        switch (axis) {
            case 0:
                _motor.setParam(PARAM_N.dParamHiStop1, max);
                return;
            case 1:
                _motor.setParam(PARAM_N.dParamHiStop2, max);
                return;
            case 2:
                _motor.setParam(PARAM_N.dParamHiStop3, max);
                return;
            default:
                break;
        }
    }

    public int getId() {
        return _id;
    }

    public double[] getColor() {
        return color;
    }

    public void setColor(double[] color) {
        this.color = color;
    }

    public void setColor(double r, double g, double b, double a) {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        this.color[3] = a;
    }
}
