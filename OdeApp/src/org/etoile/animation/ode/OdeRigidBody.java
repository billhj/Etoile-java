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

import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DMass;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

/**
 *
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class OdeRigidBody {

    protected DBody _body;
    protected DWorld _world;
    protected DSpace _space;
    protected DMass _mass;
    protected DGeom _geom = null;
    protected String _name;
    protected int _id;
    protected double[] _color = new double[]{1, 1, 0, 0.5};
    protected MotionType _motiontype = MotionType.DYNAMICS;
    protected double[] _com_localposition = new double[3];
    
    public enum MotionType {
        KINEMATICS,
        DYNAMICS
    };

    public OdeRigidBody(String name, DWorld world, DSpace space, int id) {
        _name = name;
        _world = world;
        _space = space;
        _body = OdeHelper.createBody(world);
        _mass = createMass();
        _mass.setMass(1);
        _mass.setBox(1, 0.5, 2, 0.75);
        _body.setMass(_mass);
        _id = id;
        setMotionType(_motiontype);
    }

    public void setPosition(double x, double y, double z) {
        _body.setPosition(x, y, z);
    }

    public DVector3C getPosition() {
        return _body.getPosition();
    }

    public void setRotationMatrix(DMatrix3C r) {
        _body.setRotation(r);
    }

    public void setRotation(QuaternionD r) {
        _body.setRotation(r.getRotationMatrix());
    }

    public DMatrix3C getRotationMatrix() {
        return _body.getRotation();
    }

    public QuaternionD getRotation() {
        QuaternionD q = new QuaternionD();
        q.setFromRotationMatrix(getRotationMatrix());
        return q;
    }

    public void setAngularVelocity(double x, double y, double z) {
        _body.setAngularVel(x, y, z);
    }

    public DVector3C getAngularVelocity() {
        return _body.getAngularVel();
    }

    public void setVelocity(double x, double y, double z) {
        _body.setLinearVel(x, y, z);
    }

    public DVector3C getVelocity() {
        return _body.getLinearVel();
    }

    public void setForce(double x, double y, double z) {
        checkForce(x, y, z);
        _body.setForce(x, y, z);
    }

    public void addForce(double x, double y, double z) {
        checkForce(x, y, z);
        _body.addForce(x, y, z);
    }

    public void addForceAtRelPos(double fx, double fy, double fz, double px, double py, double pz) {
        checkForce(fx, fy, fz);
        _body.addForceAtRelPos(fx, fy, fz, px, py, pz);
    }

    public void addForceAtPos(double fx, double fy, double fz, double px, double py, double pz) {
        checkForce(fx, fy, fz);
        _body.addForceAtPos(fx, fy, fz, px, py, pz);
    }

    public DVector3C getForce() {
        return _body.getForce();
    }

    public void setTorque(double x, double y, double z) {
        checkForce(x, y, z);
        _body.setTorque(x, y, z);
    }

    public void addTorque(double x, double y, double z) {
        checkForce(x, y, z);
        _body.addTorque(x, y, z);
    }

    public void addRelTorque(double x, double y, double z) {
        checkForce(x, y, z);
        _body.addRelTorque(x, y, z);
    }

    public DVector3C getTorque() {
        return _body.getTorque();
    }

    public void getPointRelPosition(DVector3C pos, DVector3 res) {
        _body.getRelPointPos(pos, res);
    }

    public void getPointVelocity(DVector3C pos, DVector3 res) {
        _body.getPointVel(pos, res);
    }

    public void getRelativePointVelocity(DVector3C pos, DVector3 res) {
        _body.getRelPointVel(pos, res);
    }

    public DBody getBody() {
        return _body;
    }

    public DWorld getWorld() {
        return _world;
    }

    public void setDMass(DMass mass) {
        _mass = mass;
    }

    public DMass getDMass() {
        return _mass;
    }

    public void setMass(double mass) {
        _mass.setMass(mass);
    }

    public double getMass() {
        return _mass.getMass();
    }

    public String getName() {
        return _name;
    }

    public void setGeom(DGeom geom) {
        _geom = geom;
        _geom.setBody(_body);
        _space.add(geom);
    }

    public DGeom getGeom() {
        return _geom;
    }

    public void rotateInertiaTensor(DMatrix3C q) {
        _mass.rotate(q);
    }

    public void rotateInertiaTensor(QuaternionD q) {
        _mass.rotate(q.getRotationMatrix());
    }

    public void translateInertiaTensor(DVector3C v) {
        _mass.translate(v);
    }

    protected void checkForce(double x, double y, double z) {
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
            throw new RuntimeException("Force componenent with NaN force (" + x + "," + y + "," + z + ")");
        }
        if (Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z)) {
            throw new RuntimeException("Force componenent with infinite force (" + x + "," + y + "," + z + ")");
        }
    }

    public DMass createMass() {
        return OdeHelper.createMass();
    }

    public void setCOM(DVector3C com) {
        _mass.setC(com);
    }

    public void setLocalCOM(double comx, double comy, double comz) {
        _mass.setC(new DVector3(comx, comy, comz));
    }

    public DVector3C getLocalCOM() {
        return _mass.getC();
    }

    public void setInertiaTensor(DMatrix3C I) {
        _mass.setI(I);
    }

    public DMatrix3C getInertiaTensor() {
        return _mass.getI();
    }

    public void clear() {
        DGeom g = _body.getFirstGeom();
        while (g != null) {
            _space.remove(g);
            g = _body.getNextGeom(g);
        }
    }

    public int getId() {
        return _id;
    }

    public double[] getColor() {
        return _color;
    }

    public void setColor(double[] color) {
        this._color = color;
    }

    public void setColor(double r, double g, double b, double a) {
        this._color[0] = r;
        this._color[1] = g;
        this._color[2] = b;
        this._color[3] = a;
    }
    
    public void setMotionType(MotionType type){
        _motiontype = type;
        if(type == MotionType.DYNAMICS){
            _body.setDynamic();
        }else{
            _body.setKinematic();
        }
    }

    public void enableCollision(){
        _geom.enable();
    }
    
    public void disableCollision(){
        _geom.disable();
    }
    
    public boolean isCollisionEnabled(){
        return _geom.isEnabled();
    }

    public double[] getCom_localposition() {
        return _com_localposition;
    }

    public void setCom_localposition(double x, double y, double z) {
        this._com_localposition[0] = x;
        this._com_localposition[1] = y;
        this._com_localposition[2] = z;
    }
    
}
