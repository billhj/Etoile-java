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
import org.ode4j.ode.DFixedJoint;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

/**
 *
 * @author Jing Huang
 * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class OdeFixedJoint /*extends OdeJoint*/ {
/*
    public OdeFixedJoint(String name, DWorld world, DJointGroup group, int id) {
        super(name, world, group, id);
        _jointype = JointType.FIXED;
    }

    @Override
    protected void createJoint() {
        _joint = OdeHelper.createFixedJoint(_world, _group);
    }

    @Override
    public void setJointAnchor(int anchor, double x, double y, double z) {
        ((DFixedJoint) _joint).setFixed();
    }

    @Override
    public double[] getJointAnchor(int anchor) {
        double[] res = new double[3];
        DVector3 v = (DVector3) ((DFixedJoint) _joint).getBody(0).getPosition();
        res[0] = v.get0();
        res[1] = v.get1();
        res[2] = v.get2();
        return res;
    }
    
    @Override
    public DFixedJoint getJoint() {
        return (DFixedJoint)_joint;
    }

    @Override
    public QuaternionD getRotation() {
        return new QuaternionD();
    }

    @Override
    public double[] getJointAxis(int axis) {
        return null;
    }

    @Override
    public void setJointAxis(int axis, double x, double y, double z) {
      
    }
*/
}
