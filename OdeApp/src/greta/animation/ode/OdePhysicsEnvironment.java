/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package greta.animation.ode;

import greta.animation.ode.controller.BaseController;
import java.util.ArrayList;
import java.util.List;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.DPlane;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

/**
 *
 * @author Jing Huang
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
    protected List<OdeRigidBody> _rigidBodies = new ArrayList<OdeRigidBody>();
    protected List<OdeJoint> _odejoints = new ArrayList<OdeJoint>();
    protected List<BaseController> _controllers = new ArrayList<BaseController>();
    protected BaseCollision _collision;

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
                long startTime = System.currentTimeMillis();
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
        return joint;
    }

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
}
