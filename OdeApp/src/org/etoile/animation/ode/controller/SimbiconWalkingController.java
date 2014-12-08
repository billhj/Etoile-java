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

import org.etoile.animation.ode.OdeHumanoid;
import org.etoile.animation.ode.OdeJoint;
import org.etoile.animation.ode.OdeRigidBody;
import org.etoile.animation.ode.QuaternionD;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;

/**
 *
 * @author Jing Huang * <gabriel.jing.huang@gmail.com or
 * jing.huang@telecom-paristech.fr>
 */
public class SimbiconWalkingController implements BaseController {

    String _name = "BalanceController";
    int _index;
    boolean _active = true;
    JSimbiconController _controller = new JSimbiconController();
    boolean _lostControl = false;
    double Md, Mdd;
    double DesVel = 0;
    //this is the number of states that the biped needs in order to be fully characterized:  - 7*2 for each link + 2*(root translation/vel)
    public final static int nrStates = 18;

    //this is the state of the biped
    public double State[] = new double[nrStates];
    //and we'll keep a copy of the default biped state, so that we can restart from it
    public double CopyState[] = new double[nrStates];
    //and this is the array of monitor point positions
    public double MonState[] = new double[50];
    public double FootState[] = new double[50];
    //and this is how many monitor states we have
    final public static int NMON = 12;

    OdeHumanoid _human;
    OdeRigidBody _lowertorso;
    OdeRigidBody _leftFoot;
    OdeRigidBody _rightFoot;
    double[] _com = new double[]{0, 0, 0};
    double[] _comdiff = new double[]{0, 0, 0};

    OdeJoint _leftAnkle;
    OdeJoint _rightAnkle;
    OdeJoint _rightHip;
    OdeJoint _leftHip;
    OdeJoint _rightKnee;
    OdeJoint _leftKnee;

    boolean _debug = true;

    public SimbiconWalkingController(OdeHumanoid human) {
        _human = human;
        _human.addController(this);
        _controller.addWalkingController();
        _controller.addRunningController();
        _controller.addCrouchWalkController();

        _lowertorso = _human.getBody("lower_torso");
        _leftFoot = _human.getBody("foot_l");
        _rightFoot = _human.getBody("foot_r");
        _leftAnkle = _human.getJoint("l_ankle");
        _rightAnkle = _human.getJoint("r_ankle");
        _rightHip = _human.getJoint("r_hip");
        _leftHip = _human.getJoint("l_hip");
        _rightKnee = _human.getJoint("r_knee");
        _leftKnee = _human.getJoint("l_knee");
        double[] state = {0.463f, 0.98f, 0.898f, -0.229f, 0.051f, 0.276f, -0.221f, -1.430f, -0.217f, 0.086f, 0.298f, -3.268f, -0.601f, 3.167f, 0.360f, 0.697f, 0.241f, 3.532f};
        setState(state);
        updateState(0);
    }

    /**
     * This method sets the state of the biped
     */
    public void setState(double[] newState) {
        for (int i = 0; i < nrStates; i++) {
            State[i] = newState[i];
            CopyState[i] = newState[i];
        }
    }

    @Override
    public void update(double dt) {

        updateState(dt);

        int body = 0, stanceHip, swingHip;
        double fallAngle = 60;
        double torq[] = new double[7];
        for (int n = 0; n < 7; n++) {
            torq[n] = 0;
        }

        // The following applies the control FSM.
        // As part of this, it computes the virtual fb torque for the
        // body, as implemented by a simple PD controller wrt to the world up vector
        if (!_lostControl) {
            bip7WalkFsm(torq, dt);
        }
            // now change torq[body], which is virtual, 
        // to include a FEL feed-forward component
        // compute stance leg torque based upon body and swing leg
        if (_controller.state[_controller.fsmState].leftStance) {
            stanceHip = 3;   // left hip
            swingHip = 1;   // right hip
        } else {
            stanceHip = 1;   // right hip
            swingHip = 3;   // left hip
        }

        if (!_controller.state[_controller.fsmState].poseStance) {
            torq[stanceHip] = -torq[body] - torq[swingHip];
        }
        torq[0] = 0;         // no external torque allowed !

        for (int n = 1; n < 7; n++) {
            torq[n] = boundRange(torq[n], _controller.torqLimit[0][n], _controller.torqLimit[1][n]);   // torq limits
            jointLimit(torq[n], n);		                                     // apply joint limits
        }
        applyTorque(torq);

    }

    //////////////////////////////////////////////////////////
    //	PROC:	bip7WalkFsm(torq)
    //	DOES:	walking control FSM
    //////////////////////////////////////////////////////////
    public void bip7WalkFsm(double torq[], double dt) {
        int torsoIndex = 0;
        int rhipIndex = 1;
        int rkneeIndex = 2;
        int lhipIndex = 3;
        int lkneeIndex = 4;
        int rankleIndex = 5;
        int lankleIndex = 6;
        boolean worldFrame[] = {
            false, // torso
            true, // rhip
            false, // rknee
            true, // lhip
            false, // lknee
            false, // rankle
            false // lankle
        };

        _controller.stateTime += dt;
        ConState s = _controller.state[_controller.fsmState];

        computeMdMdd();
        for (int n = 0; n < 7; n++) {         // compute target angles for each joint
            double target = s.th[n] + Md * s.thd[n] + Mdd * s.thdd[n];         // target state + fb actions
            target = boundRange(target, _controller.targetLimit[0][n], _controller.targetLimit[1][n]);    // limit range of target angle
            wPDtorq(torq, n, target, _controller.kp[n], _controller.kd[n], worldFrame[n]);  // compute torques
        }

        _controller.advance(FootState);   	// advance FSM to next state if needed
    }

    //////////////////////////////////////////////////////////
    //  PROC: wPDtorq()
    //  DOES: computes requires torque to move a joint wrt world frame
    //////////////////////////////////////////////////////////
    public void wPDtorq(double torq[], int joint, double dposn, double kp, double kd, boolean world) {
        double joint_posn = State[4 + joint * 2];
        double joint_vel = State[4 + joint * 2 + 1];
        if (world) {                   // control wrt world frame? (virtual)
            joint_posn += State[4];    // add body tilt
            joint_vel += State[5];    // add body angular velocity
        }
        torq[joint] = kp * (dposn - joint_posn) - kd * joint_vel;
    }

    private void computeMdMdd() {
        double stanceFootX = getStanceFootXPos(_controller);
        Mdd = State[1] - DesVel;          // center-of-mass velocity error
        Md = State[0] - stanceFootX;      // center-of-mass position error
    }

    private double getStanceFootXPos(JSimbiconController con) {
        int iLHeel = 8;   // x-coord of left-heel monitor point
        int iRHeel = 0;   // x-coord of right-heel monitor point
        if (con.state[con.fsmState].leftStance) {
            return MonState[iLHeel];
        } else {
            return MonState[iRHeel];
        }
    }

    //////////////////////////////////////////////////////////
    // PROC:  jointLimit()
    // DOES:  enforces joint limits
    //////////////////////////////////////////////////////////
    public double jointLimit(double torq, int joint) {
        double kpL = 800;
        double kdL = 80;
        double minAngle = _controller.jointLimit[0][joint];
        double maxAngle = _controller.jointLimit[1][joint];
        double currAngle = State[4 + joint * 2];
        double currOmega = State[4 + joint * 2 + 1];

        if (currAngle < minAngle) {
            torq = kpL * (minAngle - currAngle) - kdL * currOmega;
        } else if (currAngle > maxAngle) {
            torq = kpL * (maxAngle - currAngle) - kdL * currOmega;
        }
        return torq;
    }

    private double boundRange(double value, double min, double max) {
        if (value < min) {
            value = min;
        }

        if (value > max) {
            value = max;
        }
        return value;
    }

    void updateState(double dt) {
        _human.updateCOM(dt);
        _human.getCOM(_com);
        _human.getCOMDiff(_comdiff);
        DVector3C leftFootPos = _leftFoot.getPosition();
        DVector3C rightFootPos = _rightFoot.getPosition();

        QuaternionD qLeftFoot = _leftFoot.getRotation();
        QuaternionD qRightFoot = _rightFoot.getRotation();
        QuaternionD qBody = QuaternionD.slerp(qLeftFoot, qRightFoot, 0.5, true);
        qBody.invert();

        DVector3 com = new DVector3(_com[0], _com[1], _com[2]);
        DVector3 comdiff = new DVector3(_comdiff[0], _comdiff[1], _comdiff[2]);
        com = qBody.rotate(com);
        comdiff = qBody.rotate(comdiff);
        _com[0] = com.get0();
        _com[1] = com.get1();
        _com[2] = com.get2();
        _comdiff[0] = comdiff.get0();
        _comdiff[1] = comdiff.get1();
        _comdiff[2] = comdiff.get2();
        State[1] = _comdiff[2];
        State[0] = _com[2];
        int iLHeel = 8;   // x-coord of left-heel monitor point
        int iRHeel = 0;   // x-coord of right-heel monitor point
        MonState[iLHeel] = qBody.rotate((DVector3) leftFootPos).get2();
        MonState[iRHeel] = qBody.rotate((DVector3) rightFootPos).get2();

        int index = 4;
        {
            double r = _lowertorso.getRotation().getEulerAngleXYZ().get0();
            State[index + 1] = (r - State[index]) / dt;
            State[index] = r;
            index += 2;
            //System.out.println("hip: " + r);
        }
        {
            double r = _rightHip.getAngle(0);
            State[index + 1] = (r - State[index]) / dt;
            State[index] = r;
            index += 2;
            //System.out.println("hip: " + r);
        }
        {
            double r = _rightKnee.getAngle(0);
            State[index + 1] = (r - State[index]) / dt;
            State[index] = r;
            index += 2;
        }
        {
            double r = _leftHip.getAngle(0);
            State[index + 1] = (r - State[index]) / dt;
            State[index] = r;
            index += 2;
        }
        {
            double r = _leftKnee.getAngle(0);
            State[index + 1] = (r - State[index]) / dt;
            State[index] = r;
            index += 2;
        }
        {
            double r = _rightAnkle.getAngle(0);
            State[index + 1] = (r - State[index]) / dt;
            State[index] = r;
            index += 2;
        }
        {
            double r = _leftAnkle.getAngle(0);
            State[index + 1] = (r - State[index]) / dt;
            State[index] = r;
            index += 2;
        }

//        int torsoIndex = 0;
//        int rhipIndex = 1;
//        int rkneeIndex = 2;
//        int lhipIndex = 3;
//        int lkneeIndex = 4;
//        int rankleIndex = 5;
//        int lankleIndex = 6;
        if (State[4] > 2 || State[4] < -2 || State[6] > 2.3 || State[6] < -2.3 || State[10] > 2.3 || State[10] < -2.3) {
            loseControl();
            if (_debug) {
                System.out.println("loseControl");
            }
        }

    }

    void applyTorque(double torq[]) {
        int torsoIndex = 0;
        int rhipIndex = 1;
        int rkneeIndex = 2;
        int lhipIndex = 3;
        int lkneeIndex = 4;
        int rankleIndex = 5;
        int lankleIndex = 6;

        _rightHip.addTorque(torq[1], 0, 0);
        _rightKnee.addTorque(torq[2], 0, 0);
        _leftHip.addTorque(torq[3], 0, 0);
        _leftKnee.addTorque(torq[4], 0, 0);
        _rightAnkle.addTorque(torq[5], 0, 0);
        _leftAnkle.addTorque(torq[5], 0, 0);
        if (_debug) {
            int i = 0;
            for (double t : torq) {
                System.out.println("torque: [" + i + "] " + t);
                ++i;
            }
        }
    }

    public void loseControl() {
        _lostControl = true;
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
