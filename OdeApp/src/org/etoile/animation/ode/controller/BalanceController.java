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
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class BalanceController implements BaseController {

    String _name = "BalanceController";
    int _index;
    boolean _active = true;
    double[] _com = new double[]{0, 0, 0};
    double[] _comdiff = new double[]{0, 0, 0};
    double[] _offset = new double[]{0.00, 0, 0};

    private double kpx = 280f;
    private double kvx = 28f;

    private double kpz = 280f;
    private double kvz = 28f;

    private double khx = 150f;
    private double khz = 150f;

    private double kpKnee = 150f;
    private double kvKnee = 15f;

    private double rotXLFoot = 0;
    private double rotXRFoot = 0;

    private double rotZLFoot = 0;
    private double rotZRFoot = 0;

    private double lKneeC = 0;
    private double rKneeC = 0;

    private double lHipC = 0;
    private double rHipC = 0;

    private double lHipCZ = 0;
    private double rHipCZ = 0;

    private double ul, ll, pelH = 0.865f;

    private double s = 1.0;

    OdeHumanoid _human;
    OdeRigidBody leftFoot;
    OdeRigidBody rightFoot;

    OdeJoint leftAnkle;
    OdeJoint rightAnkle;
    OdeJoint rightHip;
    OdeJoint leftHip;
    OdeJoint rightKnee;
    OdeJoint leftKnee;

    public BalanceController(OdeHumanoid human) {
        _human = human;
        human.glueFeetToFloor();
        _human.addController(this);
        leftFoot = _human.getBody("foot_l");
        rightFoot = _human.getBody("foot_r");

        leftAnkle = _human.getJoint("l_ankle");
        rightAnkle = _human.getJoint("r_ankle");
        rightHip = _human.getJoint("r_hip");
        leftHip = _human.getJoint("l_hip");
        rightKnee = _human.getJoint("r_knee");
        leftKnee = _human.getJoint("l_knee");
        
        if (leftHip != null && leftKnee != null && leftAnkle != null)
        {
            double hip[] = leftHip.getJointAnchor(0);
            double knee[] = leftKnee.getJointAnchor(0);
            double ankle[] = leftAnkle.getJointAnchor(0);
         
            DVector3 hipP = new DVector3(hip[0], hip[1], hip[2]);
            DVector3 kneeP = new DVector3(knee[0], knee[1], knee[2]);
            DVector3 ankleP = new DVector3(ankle[0], ankle[1], ankle[2]);
            ul = hipP.sub(kneeP).length();
            ll = kneeP.sub(ankleP).length();
            //logger.debug("setPhysicalHumanoid {} ul={} ll={}", new Object[]{p.getId(),ul, ll});            
        }
        _human.updateCOM(0);
    }

    @Override
    public void update(double dt) {

//        if(!_human.handleGroundCollisions()){
//            System.out.println("not on the ground");
//            return;
//        }
        
        _human.updateCOM(dt);
        _human.getCOM(_com);
        _human.getCOMDiff(_comdiff);
        //System.out.println(_com[0] + " " + _com[1] + " " + _com[2]);
        DVector3C leftFootPos = leftFoot.getPosition();
        DVector3C rightFootPos = rightFoot.getPosition();
        
        QuaternionD qLeftFoot = leftFoot.getRotation();
        QuaternionD qRightFoot = rightFoot.getRotation();
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
        double[] centerfeet = new double[]{0, 0, 0};
        centerfeet[0] = (leftFootPos.get0() + rightFootPos.get0()) * 0.5 + _offset[0];
        centerfeet[1] = (leftFootPos.get1() + rightFootPos.get1()) * 0.5 + _offset[1];
        centerfeet[2] = (leftFootPos.get2() + rightFootPos.get2()) * 0.5 + _offset[2];
        
        //System.out.println("COM: " + _com[0] + " " +_com[1] +" "+_com[2] + "DIF: "+_comdiff[0] + " " +_comdiff[1] +" "+_comdiff[2]);
        //System.out.println("feet C: " + centerfeet[0] + " " +centerfeet[1] +" "+centerfeet[2]);
        
        double offsetX = s * kpx * (centerfeet[0] - _com[0]) - kvx * _comdiff[0];
        double offsetZ = s * kpz * (centerfeet[2] - _com[2]) - kvz * _comdiff[2];
        //System.out.println(offsetZ);
        
        // keep balance
        //z y x torque   offsetX apply on Z axis, offsetZ apply on x axis
        leftHip.addTorque(-offsetZ, 0f, -offsetX);
        rightHip.addTorque(-offsetZ, 0f, -offsetX);

        double hipD;
        // System.out.println("pelvis height "+pelH+" "+(ul+ll));
        if (pelH >= ul + ll) {
            hipD = 0;
        } else {
            hipD = Math.acos((ul * ul - ll * ll + pelH * pelH) / (2 * ul * pelH));
        }
        //System.out.println("hipD " + hipD);

        //x is the 1st axis
        double dHipR = 0;
        double dHipL = 0;
        double rHipOld = rHipC;
        double lHipOld = lHipC;
        lHipC = -leftHip.getAngle(0);
        rHipC = -rightHip.getAngle(0);
        dHipR = (rHipC - rHipOld) / dt;
        dHipL = (lHipC - lHipOld) / dt;
        //System.out.println("lHipC " + lHipC);
        double torqueL = ((hipD - lHipC) * s * khx - dHipL * s * 15f);
        double torqueR = ((hipD - rHipC) * s * khx - dHipR * s * 15f);
        leftHip.addTorque(-torqueL, 0, 0);
        rightHip.addTorque(-torqueR, 0, 0);

        //z is the 3rd axis
        float hipDZ = 0;
        double dHipRZ = 0;
        double dHipLZ = 0;
        double rHipOldZ = rHipCZ;
        double lHipOldZ = lHipCZ;
        lHipCZ = -leftHip.getAngle(2);
        rHipCZ = -rightHip.getAngle(2);
        dHipRZ = (rHipCZ - rHipOldZ) / dt;
        dHipLZ = (lHipCZ - lHipOldZ) / dt;

        torqueL = (hipDZ - lHipCZ) * s * khz - dHipLZ * s * 15f;
        torqueR = (hipDZ - rHipCZ) * s * khz - dHipRZ * s * 15f;
        leftHip.addTorque(0, 0, -torqueL);
        rightHip.addTorque(0, 0, -torqueR);

        //x is the 3rd axis
        double kneeD;
        if (pelH >= ul + ll) {
            kneeD = 0;
        } else {
            kneeD = (Math.PI - Math.acos((ul * ul + ll * ll - pelH * pelH) / (2 * ul * ll)));
        }
        //System.out.println("kneeD " + kneeD);
        
        double dKneeR = 0;
        double dKneeL = 0;
        double rKneeOld = rKneeC;
        double lKneeOld = lKneeC;
        lKneeC = leftKnee.getAngle(0);
        rKneeC = rightKnee.getAngle(0);
        dKneeR = (rKneeC - rKneeOld) / dt;
        dKneeL = (lKneeC - lKneeOld) / dt;
        //System.out.println("lKneeC " + lKneeC);
        double lKneeT = (kneeD - lKneeC);
        double rKneeT = (kneeD - rKneeC);

        torqueL = (lKneeT * s * kpKnee - dKneeL * s * kvKnee);
        torqueR = (rKneeT * s * kpKnee - dKneeR * s * kvKnee);
        leftKnee.addTorque(torqueL, 0, 0);
        rightKnee.addTorque(torqueR, 0, 0);

        DVector3 leftFw = new DVector3(0, 0, 1);
        DVector3 rightFw = new DVector3(0, 0, 1);
        leftFw = qLeftFoot.rotate(leftFw);
        rightFw = qRightFoot.rotate(rightFw);
        leftFw = qBody.rotate(leftFw);
        rightFw = qBody.rotate(rightFw);

        double dRotLFoot = 0;
        double dRotRFoot = 0;
        DVector3 v1 = new DVector3(0, 0, 1);
        double lDot = leftFw.dot(v1);
        double rDot = rightFw.dot(v1);
        if (lDot < -1) {
            lDot = -1;
        }
        if (lDot > 1) {
            lDot = 1;
        }
        if (rDot < -1) {
            rDot = -1;
        }
        if (rDot > 1) {
            rDot = 1;
        }
        double oldRotRFoot;
        double oldRotLFoot;
        oldRotRFoot = rotXRFoot;
        oldRotLFoot = rotXLFoot;
        rotXLFoot = (float) Math.acos(lDot);
        rotXRFoot = (float) Math.acos(rDot);
        dRotLFoot = (rotXLFoot - oldRotRFoot) / dt;
        dRotRFoot = (rotXRFoot - oldRotLFoot) / dt;
        torqueL = rotXLFoot * s * 160 - dRotLFoot * s * 1.6f;
        torqueR = rotXRFoot * s * 160 - dRotRFoot * s * 1.6f;
        leftAnkle.addTorque(-torqueL, 0, 0);
        rightAnkle.addTorque(-torqueR, 0, 0);

        //tooo small ignore
        {
            v1.set(1, 0, 0);
            leftFw.set(1, 0, 0);
            rightFw.set(1, 0, 0);
            leftFw = qLeftFoot.rotate(leftFw);
            rightFw = qRightFoot.rotate(rightFw);
            lDot = leftFw.dot(v1);
            rDot = rightFw.dot(v1);
            if (lDot < -1) {
                lDot = -1;
            }
            if (lDot > 1) {
                lDot = 1;
            }
            if (rDot < -1) {
                rDot = -1;
            }
            if (rDot > 1) {
                rDot = 1;
            }
            dRotLFoot = 0;
            dRotRFoot = 0;
            oldRotRFoot = rotZRFoot;
            oldRotLFoot = rotZLFoot;
            rotZLFoot = (float) Math.acos(lDot);
            rotZRFoot = (float) Math.acos(rDot);
            dRotLFoot = (rotZLFoot - oldRotLFoot) / dt;
            dRotRFoot = (rotZRFoot - oldRotRFoot) / dt;
        }
        
        leftAnkle.addTorque(-offsetZ * s * 18, 0f, offsetX * s * 3);
        rightAnkle.addTorque(-offsetZ * s * 18, 0f, offsetX * s * 3);
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

    public double[] getCenterMassOffset() {
        return _offset;
    }

    public void setCenterMassOffset(double[] offset) {
        this._offset = offset;
    }

    public double getMultiplierS() {
        return s;
    }

    public void setMultiplierS(double s) {
        this.s = s;
    }
    
}
