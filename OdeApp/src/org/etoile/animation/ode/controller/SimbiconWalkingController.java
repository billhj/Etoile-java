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

/**
 *
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class SimbiconWalkingController implements BaseController {
    String _name = "BalanceController";
    int _index;
    boolean _active = true;
    JSimbiconController _controller = new JSimbiconController();
    boolean _lostControl = false;
    double Md, Mdd;
    
    public SimbiconWalkingController(OdeHumanoid human) {
        _controller.addWalkingController();
        _controller.addRunningController();
        _controller.addCrouchWalkController();
    }
    
    @Override
    public void update(double dt) {
        int body=0, stanceHip, swingHip;
        double fallAngle = 60;
        double torq[] = new double[7];
        for (int n=0; n<7; n++)
                torq[n] = 0;

            // The following applies the control FSM.
            // As part of this, it computes the virtual fb torque for the
            // body, as implemented by a simple PD controller wrt to the world up vector
            if (_lostControl)  
                bip7WalkFsm(torq, dt);

        // now change torq[body], which is virtual, 
        // to include a FEL feed-forward component

          // compute stance leg torque based upon body and swing leg
            if (_controller.state[_controller.fsmState].leftStance) {
                    stanceHip = 3;   // left hip
                    swingHip  = 1;   // right hip
            } else {
                    stanceHip = 1;   // right hip
                    swingHip  = 3;   // left hip
            }

            if (!_controller.state[_controller.fsmState].poseStance) {
                    torq[stanceHip] = -torq[body] - torq[swingHip];
            }
            torq[0] = 0;         // no external torque allowed !

            for (int n=1; n<7; n++) {  
                  torq[n] = boundRange(torq[n], _controller.torqLimit[0][n], _controller.torqLimit[1][n]);   // torq limits
                  jointLimit(torq[n],n);		                                     // apply joint limits
            }
    }
    
    
    //////////////////////////////////////////////////////////
    //	PROC:	bip7WalkFsm(torq)
    //	DOES:	walking control FSM
    //////////////////////////////////////////////////////////
    public void bip7WalkFsm(double torq[], double dt){
            int torsoIndex  = 0;
            int rhipIndex   = 1;
            int rkneeIndex  = 2;
            int lhipIndex   = 3;
            int lkneeIndex  = 4;
            int rankleIndex = 5;
            int lankleIndex = 6;
            boolean worldFrame[] = {
                    false,  // torso
                    true,   // rhip
                    false,  // rknee
                    true,   // lhip
                    false,  // lknee
                    false,  // rankle
                    false   // lankle
            };

            _controller.stateTime += dt;
            ConState s = _controller.state[_controller.fsmState];

            computeMdMdd();
            for (int n=0; n<7; n++) {         // compute target angles for each joint
                    double target  = s.th[n] + Md*s.thd[n] + Mdd*s.thdd[n];         // target state + fb actions
                    target = boundRange(target, _controller.targetLimit[0][n], _controller.targetLimit[1][n]);    // limit range of target angle
                    wPDtorq(torq, n, target, _controller.kp[n], _controller.kd[n], worldFrame[n]);  // compute torques
            }

            _controller.advance();   	// advance FSM to next state if needed
    }
    
     public void computeMdMdd(){
        double stanceFootX = bip7.getStanceFootXPos(con);
        Mdd = bip7.State[1] - DesVel;          // center-of-mass velocity error
        Md = bip7.State[0] - stanceFootX;      // center-of-mass position error
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
