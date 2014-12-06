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
package org.etoile.animation.ode.controller;

import org.etoile.animation.ode.OdeJoint;
import org.ode4j.ode.DUniversalJoint;

/**
 *
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class UniversalJointController implements BaseController {

    String _name;
    int _index;
    boolean _active = true;
    private static final float KP_DEFAULT = 1.5f;
    private static final float KD_DEFAULT = 0.01f;
    PDController _controller0 = new PDController();
    PDController _controller1 = new PDController();

    OdeJoint _joint;

    public UniversalJointController(String name, OdeJoint joint, int index, double desireAngle, double desireVelocity, double kP, double kD,
            double desireAngle1, double desireVelocity1, double kP1, double kD1) {
        this._name = name;
        this._index = index;
        _controller0._desireAngle = desireAngle;
        _controller0._desireVelocity = desireVelocity;
        _controller0._kP = kP;
        _controller0._kD = kD;
        _controller1._desireAngle = desireAngle1;
        _controller1._desireVelocity = desireVelocity1;
        _controller1._kP = kP1;
        _controller1._kD = kD1;
        _joint = joint;
    }

    public UniversalJointController(OdeJoint joint) {
        this(joint.getName(), joint, 0, 0, 0, KP_DEFAULT, KD_DEFAULT, 0, 0, KP_DEFAULT, KD_DEFAULT);
    }

    @Override
    public void update(double dt) {
        if (!_active) {
            return;
        }
        DUniversalJoint j = ((DUniversalJoint) _joint.getJoint());
        double velocity0 = j.getAngle1Rate();
        double currentAngle0 = j.getAngle1();
        _controller0._torqueOutput = _controller0.compute(currentAngle0, velocity0);

        double velocity1 = j.getAngle2Rate();
        double currentAngle1 = j.getAngle2();
        _controller1._torqueOutput = _controller1.compute(currentAngle1, velocity1);

        j.addTorques(_controller0._torqueOutput, _controller1._torqueOutput);
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

    public void setKParameter(int idController, double kP, double kD) {
        if (idController == 0) {
            _controller0._kP = kP;
            _controller0._kD = kD;
        } else {
            _controller1._kP = kP;
            _controller1._kD = kD;
        }
    }
    
    public void setDesireAngle(int idController, double angle){
        if (idController == 0) {
            _controller0._desireAngle = angle;
        } else {
            _controller1._desireAngle = angle;
        }
    }

    public OdeJoint getJoint() {
        return _joint;
    }

}
