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

import org.etoile.animation.ode.controller.BaseController;
import java.util.ArrayList;
import java.util.List;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.DPlane;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

/**
 *
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class OdePhysicsEnvironment {

    protected String _id = "odephysicsenvironment";
    protected float _timeStep = 0.003F;
    protected boolean _pause = false;
    protected boolean _doCollision = true;

    protected DWorld _world;
    protected DSpace _space;
    protected DPlane _ground;
    protected DJointGroup _joints;
    protected double[] _gravity = {0, -9.8, 0};
    protected List<OdeRigidBody> _rigidBodies = new ArrayList<>();
    protected List<OdeJoint> _odejoints = new ArrayList<>();
    protected List<BaseController> _controllers = new ArrayList<>();
    protected SimpleOdeCollision _collision;

    protected boolean _debug = false;
    long startTime = 0;
    long endTime = 0;

    public OdePhysicsEnvironment() {
        init();
    }

    private void init() {
        initWorldPhysics();
        initGroundPhysics();
    }

    public void release() {
        _joints.destroy();
        _space.destroy();
        _world.destroy();
        OdeHelper.closeODE();
    }

    private void initWorldPhysics() {
        OdeHelper.initODE2(0);
        _world = OdeHelper.createWorld();
        _world.setGravity(_gravity[0], _gravity[1], _gravity[2]);
        _space = OdeHelper.createHashSpace();
        _joints = OdeHelper.createJointGroup();
        _collision = new SimpleOdeCollision(_world, _space);
    }

    private void initGroundPhysics() {
        _ground = OdeHelper.createPlane(_space, 0, 1, 0, 0);
    }

    public void physicsTick() {
        if (!_pause) {
            if (_debug) {
                startTime = System.currentTimeMillis();
            }
            if (_doCollision) {
                _collision.startCollision();
            }
            physicsSimulationStep();
            _world.step(_timeStep);
            if (_doCollision) {
                _collision.endCollision();
            }

            if (_debug) {
                endTime = System.currentTimeMillis();
                long timeUsed = endTime - startTime;
                System.out.println("physics performance: " + 1 + " frames in " + (timeUsed / 1000f) + " seconds = " + (1 / (timeUsed / 1000f)));
            }
        }
    }

    public void physicsSimulationStep() {
        for (BaseController controller : _controllers) {
            controller.update(_timeStep);
        }
    }

    public DWorld getWorld() {
        return _world;
    }

    public DSpace getSpace() {
        return _space;
    }

    public String getId() {
        return _id;
    }

    public float getTimeStep() {
        return _timeStep;
    }

    public void setTimeStep(float timeStep) {
        this._timeStep = timeStep;
    }

    public boolean isPause() {
        return _pause;
    }

    public void setPause(boolean pause) {
        this._pause = pause;
    }

    public double[] getGravity() {
        return _gravity;
    }

    public void setGravity(double[] gravity) {
        this._gravity = gravity;
        _world.setGravity(_gravity[0], _gravity[1], _gravity[2]);
    }

    public OdeJoint createJoint(String name, JointType type, DJointGroup jointgroup) {
        OdeJoint joint = new OdeJoint(name, type, _world, jointgroup, _odejoints.size());
        _odejoints.add(joint);
        /*if(type == JointType.BALL){
            joint = createBallJoint(name, jointgroup);
        }else if(type == JointType.FIXED){
            joint = createFixedJoint(name, jointgroup);
        }else if(type == JointType.HINGE){
            joint = createHingeJoint(name, jointgroup);
        }else if(type == JointType.UNIVERSAL){
            joint = createUniversalJoint(name, jointgroup);
        }*/
        return joint;
    }
    
    /*public OdeBallJoint createBallJoint(String name, DJointGroup jointgroup) {
        OdeBallJoint joint = new OdeBallJoint(name, _world, jointgroup, _odejoints.size());
        _odejoints.add(joint);
        return joint;
    }

    public OdeHingeJoint createHingeJoint(String name, DJointGroup jointgroup) {
        OdeHingeJoint joint = new OdeHingeJoint(name, _world, jointgroup, _odejoints.size());
        _odejoints.add(joint);
        return joint;
    }

    public OdeUniversalJoint createUniversalJoint(String name, DJointGroup jointgroup) {
        OdeUniversalJoint joint = new OdeUniversalJoint(name, _world, jointgroup, _odejoints.size());
        _odejoints.add(joint);
        return joint;
    }

    public OdeFixedJoint createFixedJoint(String name, DJointGroup jointgroup) {
        OdeFixedJoint joint = new OdeFixedJoint(name, _world, jointgroup, _odejoints.size());
        _odejoints.add(joint);
        return joint;
    }*/

    public OdeRigidBody createRigidBody(String name) {
        OdeRigidBody body = new OdeRigidBody(name, _world, _space, _rigidBodies.size());
        _rigidBodies.add(body);
        return body;
    }

    public List<OdeRigidBody> getRigidBodies() {
        return _rigidBodies;
    }

    public List<OdeJoint> getOdejoints() {
        return _odejoints;
    }

    public OdeRigidBody getBody(int id) {
        return _rigidBodies.get(id);
    }

    public OdeJoint getJoint(int id) {
        return _odejoints.get(id);
    }

    public void addController(BaseController controller) {
        _controllers.add(controller);
    }

    public List<BaseController> getControllers() {
        return _controllers;
    }

    public BaseController getController(int id) {
        return _controllers.get(id);
    }

    public boolean isDoCollision() {
        return _doCollision;
    }

    public void setDoCollision(boolean doCollision) {
        this._doCollision = doCollision;
    }
    
    public void setSimpleOdeCollision(SimpleOdeCollision collision){
        collision.setSpace(_space);
        collision.setWorld(_world);
        _collision = collision;
    }

    public SimpleOdeCollision getCollision() {
        return _collision;
    }
    
    
}
