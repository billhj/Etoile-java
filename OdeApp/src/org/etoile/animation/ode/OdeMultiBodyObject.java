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
import java.util.HashMap;
import java.util.List;
import org.etoile.animation.ode.controller.BaseController;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.OdeHelper;

/**
 *
 * @author Jing Huang
 * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class OdeMultiBodyObject {
    protected OdePhysicsEnvironment _env;
    protected HashMap<String, Integer> _bodyIds = new HashMap<>();
    protected HashMap<String, Integer> _jointIds = new HashMap<>();
    protected HashMap<String, Integer> _controllerIds = new HashMap<>();
    protected DJointGroup _jointGroup = OdeHelper.createJointGroup();
    
    public OdeMultiBodyObject(OdePhysicsEnvironment env) {
        _env = env;
    }

    public OdeRigidBody createBody(String name) {
        OdeRigidBody body = _env.createRigidBody(name);
        _bodyIds.put(name, body.getId());
        return body;
    }

    public OdeJoint createJoint(String name, JointType type, OdeRigidBody b1, OdeRigidBody b2, double[] anchor, double[] axis0, double[] axis1) {
        OdeJoint joint = _env.createJoint(name, type, _jointGroup);
        joint.attach(b1, b2);
        _jointIds.put(name, joint.getId());
        if (anchor != null) {
            joint.setJointAnchor(0, anchor[0], anchor[1], anchor[2]);
        }
        if (axis0 != null) {
            joint.setJointAxis(0, axis0[0], axis0[1], axis0[2]);
        }
        if(type == JointType.UNIVERSAL){
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
    
    public void addController(BaseController controller){
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
    
    public OdeRigidBody getBody(String name){
        return _env.getBody(_bodyIds.get(name));
    }
    
    public OdeJoint getJoint(String name){
        return _env.getJoint(_jointIds.get(name));
    }
    
   
    public ArrayList<QuaternionD> getRotations(List<String> jointNames){
        ArrayList<QuaternionD> rs = new ArrayList<>();
        for(String name : jointNames){
            rs.add(getJoint(name).getRotation());
        }
        return rs;
    }
    
    public HashMap<String, QuaternionD> getRotations(){
        HashMap<String, QuaternionD> rotation = new HashMap<>();
        for(String name : _jointIds.keySet()){
            OdeJoint joint = this.getJoint(name);
            rotation.put(name, joint.getRotation());
        }
        return rotation;
    }
  
}
