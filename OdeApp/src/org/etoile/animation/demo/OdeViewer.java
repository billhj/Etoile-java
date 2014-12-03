/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.animation.demo;

import org.etoile.animation.ode.OdeHumanoid;
import org.etoile.animation.ode.OdeJoint;
import org.etoile.animation.ode.OdePhysicsEnvironment;
import org.etoile.animation.ode.OdeRigidBody;
import org.etoile.animation.ode.QuaternionD;
import java.util.List;
import org.ode4j.drawstuff.DrawStuff;
import static org.ode4j.drawstuff.DrawStuff.dsDrawBox;
import static org.ode4j.drawstuff.DrawStuff.dsDrawCapsule;
import static org.ode4j.drawstuff.DrawStuff.dsDrawCylinder;
import static org.ode4j.drawstuff.DrawStuff.dsDrawSphere;
import static org.ode4j.drawstuff.DrawStuff.dsSetColorAlpha;
import static org.ode4j.drawstuff.DrawStuff.dsSetViewpoint;
import static org.ode4j.drawstuff.DrawStuff.dsSimulationLoop;
import org.ode4j.math.DMatrix3;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBox;
import org.ode4j.ode.DCapsule;
import org.ode4j.ode.DCylinder;
import org.ode4j.ode.DGeom;
import static org.ode4j.ode.DGeom.dBoxClass;
import static org.ode4j.ode.DGeom.dCapsuleClass;
import static org.ode4j.ode.DGeom.dCylinderClass;
import static org.ode4j.ode.DGeom.dSphereClass;
import org.ode4j.ode.DSphere;

/**
 *
 * @author Jing Huang
 */
public class OdeViewer extends DrawStuff.dsFunctions {

    OdePhysicsEnvironment _env;
    OdeHumanoid _human;
    int fb = 0;
    boolean _rec = false;
    //DMatrix3 r = new DMatrix3();

    public OdeViewer() {
        super();
    }

    //draw order z x y in this 
    private void draw() {

        //DrawStuff.dsSetDrawMode(DS_WIREFRAME);
        if (_env == null) {
            return;
        }

        {
            DVector3 p = new DVector3(0, 0, 0);
            dsSetColorAlpha(1, 0, 0, 1);
            dsDrawBox(p.reAdd(new DVector3(0.3, 0, 0)), new DMatrix3().setIdentity(), new DVector3(0.3, 0.05, 0.05));
            dsSetColorAlpha(0, 1, 0, 1);
            dsDrawBox(p.reAdd(new DVector3(0, 0.3, 0)), new DMatrix3().setIdentity(), new DVector3(0.05, 0.3, 0.05));
            dsSetColorAlpha(0, 0, 1, 1);
            dsDrawBox(p.reAdd(new DVector3(0, 0, 0.3)), new DMatrix3().setIdentity(), new DVector3(0.05, 0.05, 0.3));
        }

        List<OdeJoint> joints = _env.getOdejoints();
        for (OdeJoint j : joints) {
            double[] pos = j.getJointAnchor(0);
            double[] axis = j.getJointAxis(0);
            double[] color = j.getColor();
            dsSetColorAlpha(color[0], color[1], color[2], color[3]);
            DVector3 p = new DVector3(pos[0], pos[1], pos[2]);
            DVector3 a = new DVector3(axis[0] * 0.05 + 0.02, axis[1] * 0.05 + 0.02, axis[2] * 0.05 + 0.02);
            //dsDrawSphere(p, new DMatrix3().setIdentity(), 0.03);
            //dsDrawLine(p.add(a), p.sub(a));
            dsDrawBox(p, new DMatrix3().setIdentity(), a);
            //j.setAngle(0, 1);
        }

        List<OdeRigidBody> bodys = _env.getRigidBodies();
        for (OdeRigidBody j : bodys) {
            DGeom geom = j.getGeom();
            DVector3C pos = j.getPosition();
            dsSetColorAlpha(0.1f, 0.2f, 0.1f, 1f);
            dsDrawSphere(pos, geom.getRotation(), j.getMass() * 0.003);

            double[] color = j.getColor();
            switch (geom.getClassID()) {
                case dSphereClass: {
                    dsSetColorAlpha(color[0], color[1], color[2], color[3]);
                    double radius = ((DSphere) geom).getRadius();
                    dsDrawSphere(pos, geom.getRotation(), radius);
                    break;
                }
                case dBoxClass: {
                    dsSetColorAlpha(color[0], color[1], color[2], color[3]);
                    DVector3C sides = ((DBox) geom).getLengths();
                    dsDrawBox(pos, geom.getRotation(), sides);
                    break;
                }
                case dCapsuleClass: {
                    dsSetColorAlpha(color[0], color[1], color[2], color[3]);
                    double radius = ((DCapsule) geom).getRadius();
                    double length = ((DCapsule) geom).getLength();
                    dsDrawCapsule(pos, geom.getRotation(), length, radius);
                    break;
                }
                case dCylinderClass: {
                    dsSetColorAlpha(color[0], color[1], color[2], color[3]);
                    double radius = ((DCylinder) geom).getRadius();
                    double length = ((DCylinder) geom).getLength();
                    dsDrawCylinder(pos, geom.getRotation(), length, radius);
                    break;
                }
            }

//            if(j.getName().equalsIgnoreCase("lower_torso")){
//                QuaternionD d = new QuaternionD();
//                d.setAxisAngle(new DVector3(0,0,1), 0.5);
//                //j.setRotation(d);
//                j.addTorque(-1000, 0, 0);
//            }
        }
    }

    private void simLoop(boolean pause) {
        if (!pause) {
            if (_env != null) {
                _env.physicsTick();
            }
        }
        draw();
    }

    @Override
    public void start() {
        double[] xyz = {1.5, 1.0614, 2};
        double[] hpr = {8, -40, 0};
        dsSetViewpoint(xyz, hpr);
    }

    @Override
    public void step(boolean pause) {
        simLoop(pause);
    }

    @Override
    public void command(char cmd) {
        if (cmd == '1') {
            _env.setPause(false);
        } else if (cmd == 'w') {
//            OdeRigidBody upper = _human.getBody("upper_torso");
//            OdeRigidBody lower = _human.getBody("lower_torso");
            OdeRigidBody upper = _env.getBody(7);
            upper.addForce(0, 0, -1000);
//            QuaternionD q = upper.getRotation();
//            QuaternionD ql = lower.getRotation();
//            QuaternionD q2 = _human.getJoint("vl1").getRotation();
//            QuaternionD out = QuaternionD.multiplication(q, ql.inverse());
//            System.out.println(out+"  "+ q2 + " "+QuaternionD.multiplication(out,q2));
//            _human.getJoint("vl1").addTorque(0, 0, 1000);
            //upper.addForce(0, 0, -1000);
        } else if (cmd == 's') {
            OdeRigidBody upper = _env.getBody(7);
            upper.addForce(0, 0, 1000);
            //_human.getJoint("vl1").addTorque(0 , 0, 1000);
        } else if (cmd == 'a') {
            OdeRigidBody upper = _env.getBody(7);
            upper.addForce(-1000, 0, 0);
        } else if (cmd == 'd') {
            OdeRigidBody upper = _env.getBody(7);
            upper.addForce(1000, 0, 0);
        } else if (cmd == 'e') {
            _rec = false;
            System.out.println("bap reco end");
        } else if (cmd == 'b') {
            _rec = true;
            System.out.println("bap reco start");
            fb = 0;
        } else if(cmd == '3'){
            QuaternionD r = new QuaternionD();
            r.fromEulerXYZ(0.5,0,0);
            _env.getBody(8).setRotation(r);
        }
         else if(cmd == '4'){
            QuaternionD r = new QuaternionD();
            r.fromEulerXYZ(-0.5,0,0);
            _env.getBody(8).setRotation(r);
            //Syst
        }
    }

    @Override
    public void stop() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void demo(String[] args) {
        _env = new OdeDemoEnvironment();
        _env.setPause(true);
        ((OdeDemoEnvironment) _env).setSimpleOdeCollision(new DemoSimpleOdeCollision());
        dsSimulationLoop(args, 1024, 768, this);
        if (_env != null) {
            _env.release();
        }
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        new OdeViewer().demo(args);
    }

}
