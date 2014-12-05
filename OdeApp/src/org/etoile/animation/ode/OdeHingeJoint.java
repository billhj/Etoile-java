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
import org.ode4j.ode.DHingeJoint;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

/**
 *
 * @author Jing Huang
 * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class OdeHingeJoint /*extends OdeJoint*/ {
/*
    public OdeHingeJoint(String name, DWorld world, DJointGroup group, int id) {
        super(name, world, group, id);
        _jointype = JointType.HINGE;
    }

    @Override
    protected void createJoint() {
        _joint = OdeHelper.createHingeJoint(_world, _group);
    }

    @Override
    public void setJointAxis(int axis, double x, double y, double z) {
        ((DHingeJoint) _joint).setAxis(x, y, z);
    }

    @Override
    public double[] getJointAxis(int axis) {
        double[] res = new double[3];
        DVector3 v = new DVector3();
        ((DHingeJoint) _joint).getAxis(v);
        res[0] = v.get0();
        res[1] = v.get1();
        res[2] = v.get2();
        return res;
    }

    @Override
    public void setJointAnchor(int anchor, double x, double y, double z) {
        ((DHingeJoint) _joint).setAnchor(x, y, z);
    }

    @Override
    public double[] getJointAnchor(int anchor) {
        double[] res = new double[3];
        DVector3 v = new DVector3();
        if (anchor == 0) {
            ((DHingeJoint) _joint).getAnchor(v);
        } else if (anchor == 1) {
            ((DHingeJoint) _joint).getAnchor2(v);
        }
        res[0] = v.get0();
        res[1] = v.get1();
        res[2] = v.get2();
        return res;
    }
    
    @Override
    public DHingeJoint getJoint() {
        return (DHingeJoint)_joint;
    }

    public void addTorque(double torque0) {
        if (Double.isNaN(torque0)) {
            throw new RuntimeException("Adding torque componenent with NaN force (" + torque0 + ")");
        }
        if (Double.isInfinite(torque0)) {
            throw new RuntimeException("Adding torque componenent with infinite force (" + torque0+ ")");
        }
        ((DHingeJoint) _joint).addTorque(torque0);
    }

    public double getAngle() {
        return ((DHingeJoint) _joint).getAngle();
    }

    @Override
    public QuaternionD getRotation() {
        QuaternionD ax0 = new QuaternionD();
        ax0.setAxisAngle(getAxis(), getAngle());
        return ax0;
    }

    public DVector3 getAxis() {
        DVector3 axis = new DVector3();
        ((DHingeJoint) _joint).getAxis(axis);
        return axis;
    }*/
}
