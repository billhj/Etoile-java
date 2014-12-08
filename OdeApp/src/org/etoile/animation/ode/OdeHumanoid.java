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

import java.util.ArrayList;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DContact;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.DPlane;

/**
 *
 * @author Jing Huang
 *
 * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class OdeHumanoid extends OdeMultiBodyObject {

    protected double[] _COM = new double[3];
    protected double[] _COMDiff = new double[3];
    protected double[] _rootOffset = new double[3];
    protected double[] _roottranslation = new double[3];

    protected boolean _onGround = true;

    public OdeHumanoid(OdePhysicsEnvironment env) {
        super(env);
    }

    public void glueFeetToFloor() {
        OdeRigidBody fl = _env.getBody(_bodyIds.get("foot_l"));
        OdeRigidBody fr = _env.getBody(_bodyIds.get("foot_r"));
        //_env.getSpace().add(fl.getGeom());
        //_env.getSpace().add(fr.getGeom());
        double[] posl = {fl.getPosition().get0(), fl.getPosition().get1(), fl.getPosition().get2()};
        double[] posr = {fr.getPosition().get0(), fr.getPosition().get1(), fr.getPosition().get2()};
        OdeJoint fixl = createJoint("fix_l_foot", JointType.FIXED, fl, null, posl, null, null);
        OdeJoint fixr = createJoint("fix_r_foot", JointType.FIXED, fr, null, posr, null, null);
    }

    public void setRootOffset(double x, double y, double z) {
        _rootOffset[0] = x;
        _rootOffset[1] = y;
        _rootOffset[2] = z;
    }

    public double[] getRootOffset() {
        return _rootOffset;
    }

    public double[] computeRootTranslation() {
        OdeRigidBody root = _env.getBody(_bodyIds.get("lower_torso"));
        QuaternionD r = root.getRotation();
        DVector3 newoffset = QuaternionD.multiplication(r, new DVector3(_rootOffset[0], _rootOffset[1], _rootOffset[2]));
        DVector3C pos = root.getPosition();
        _roottranslation[0] = pos.get0() - newoffset.get0();
        _roottranslation[1] = pos.get1() - newoffset.get1();
        _roottranslation[2] = pos.get2() - newoffset.get2();
        return _roottranslation;
    }

    public QuaternionD getRootRotation() {
        OdeRigidBody root = _env.getBody(_bodyIds.get("lower_torso"));
        QuaternionD r = root.getRotation();
        return r;
    }

    public void updateCOM(double dt) {
        DVector3 center = new DVector3(0, 0, 0);
        double totalMass = 0;
        for (String name : _bodyIds.keySet()) {
            OdeRigidBody body = getBody(name);
            DVector3C pos = body.getPosition();
            double mass = body.getMass();
            center.add(pos.reScale(mass));
            totalMass += mass;
        }
        center.scale(1.0 / totalMass);

        if (dt == 0) {
            _COMDiff[0] = 0;
            _COMDiff[1] = 0;
            _COMDiff[2] = 0;
        } else {
            _COMDiff[0] = (center.get0() - _COM[0]) / dt;
            _COMDiff[1] = (center.get1() - _COM[1]) / dt;
            _COMDiff[2] = (center.get2() - _COM[2]) / dt;
        }
        _COM[0] = center.get0();
        _COM[1] = center.get1();
        _COM[2] = center.get2();
    }

    public void getCOM(double[] COM) {
        COM[0] = _COM[0];
        COM[1] = _COM[1];
        COM[2] = _COM[2];
    }

    public void getCOMDiff(double[] COMdiff) {
        COMdiff[0] = _COMDiff[0];
        COMdiff[1] = _COMDiff[1];
        COMdiff[2] = _COMDiff[2];
    }

    public double[] getTranslation() {
        return _roottranslation;
    }

    public boolean handleGroundCollisions() {
        SimpleOdeCollision col = _env.getCollision();
        DPlane ground = _env.getGround();
        ArrayList<DContact> contacts = col.getContacts();
        int collisions = contacts.size();
        if (collisions > 0) {
            for (int i = 0; i < collisions; i++) {
                DContact contact = contacts.get(i);
                if (contact.geom.g1 == ground) {
                    _onGround = true;
                } else if (contact.geom.g2 == ground) {
                    _onGround = true;
                } else {
                    _onGround = false;
                }
            }
        }else{
            _onGround = false;
        }
        return _onGround;
    }

    public boolean isOnGround() {
        return _onGround;
    }
    
}
