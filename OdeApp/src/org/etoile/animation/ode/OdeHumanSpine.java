/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.animation.ode;

import org.etoile.animation.ode.controller.BaseController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.OdeHelper;

/**
 *
 * @author Jing Huang
 */
public class OdeHumanSpine {

    protected OdePhysicsEnvironment _env;
    protected HashMap<String, Integer> _bodyIds = new HashMap<String, Integer>();
    protected HashMap<String, Integer> _jointIds = new HashMap<String, Integer>();
    protected HashMap<String, Integer> _controllerIds = new HashMap<String, Integer>();
    protected DJointGroup _jointGroup = OdeHelper.createJointGroup();

    public OdeHumanSpine(OdePhysicsEnvironment env) {
        _env = env;
    }

    public OdeRigidBody createBody(String name) {
        OdeRigidBody body = _env.createRigidBody(name);
        _bodyIds.put(name, body.getId());
        return body;
    }

    public OdeJoint createJoint(String name, JointType type, OdeRigidBody child, OdeRigidBody parent, double[] anchor, double[] axis0, double[] axis1) {
        OdeJoint joint = _env.createJoint(name, type, _jointGroup);
        joint.attachChildToParent(child, parent);
        _jointIds.put(name, joint.getId());
        if (anchor != null) {
            joint.setJointAnchor(0, anchor[0], anchor[1], anchor[2]);
        }
        if (axis0 != null) {
            joint.setJointAxis(0, axis0[0], axis0[1], axis0[2]);
        }
        if (type == JointType.UNIVERSAL) {
            if (axis1 != null) {
                joint.setJointAxis(1, axis1[0], axis1[1], axis1[2]);
            }
        }
        return joint;
    }

    public OdeJoint createJoint(String name, JointType type, String b1name, String b2name, double[] anchor, double[] axis0, double[] axis1) {
        Integer bn1 = _bodyIds.get(b1name);
        Integer bn2 = _bodyIds.get(b2name);
        OdeRigidBody b1 = null;
        OdeRigidBody b2 = null;
        if (bn1 != null) {
            b1 = _env.getBody(bn1);
        }
        if (bn2 != null) {
            b2 = _env.getBody(bn2);
        }
        return createJoint(name, type, b1, b2, anchor, axis0, axis1);
    }

    public void addController(BaseController controller) {
        int id = _env.getControllers().size();
        _env.addController(controller);
        _controllerIds.put(controller.getName(), id);
    }

    public OdePhysicsEnvironment getEnvironment() {
        return _env;
    }

    public HashMap<String, Integer> getBodyIds() {
        return _bodyIds;
    }

    public HashMap<String, Integer> getJointIds() {
        return _jointIds;
    }

    public OdeRigidBody getBody(String name) {
        return _env.getBody(_bodyIds.get(name));
    }

    public OdeJoint getJoint(String name) {
        return _env.getJoint(_jointIds.get(name));
    }

    public ArrayList<QuaternionD> getRotations(List<String> jointNames) {
        ArrayList<QuaternionD> rs = new ArrayList<QuaternionD>();
        for (String name : jointNames) {
            rs.add(getJoint(name).getRotation());
        }
        return rs;
    }

    public HashMap<String, QuaternionD> getRotations() {
        HashMap<String, QuaternionD> rotation = new HashMap<String, QuaternionD>();
        for (String name : _jointIds.keySet()) {
            OdeJoint joint = this.getJoint(name);
            rotation.put(name, joint.getRotation());
        }
        return rotation;
    }
}
