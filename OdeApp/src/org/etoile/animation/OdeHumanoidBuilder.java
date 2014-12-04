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
package org.etoile.animation;

import java.util.List;
import org.etoile.animation.ode.JointType;
import org.etoile.animation.ode.OdeHumanoid;
import org.etoile.animation.ode.OdeJoint;
import org.etoile.animation.ode.OdeRigidBody;
import org.etoile.core.animation.Joint;
import org.etoile.core.animation.Skeleton;
import org.etoile.core.math.ColumnVector3;
import org.etoile.core.math.Matrix33;
import org.ode4j.math.DMatrix3;
import org.ode4j.ode.DBox;
import org.ode4j.ode.OdeHelper;
import vib.core.util.xml.XML;
import vib.core.util.xml.XMLParser;
import vib.core.util.xml.XMLTree;

/**
 *
 * @author Jing Huang
 * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class OdeHumanoidBuilder {

    String _filepath = "";
    XMLTree _tree;

    public boolean loadFile(String filepath) {
        _filepath = filepath;
        XMLParser xmlparser = XML.createParser();
        xmlparser.setValidating(false);
        _tree = xmlparser.parseFile(filepath);
        return _tree != null;
    }

    public boolean buildBodyFromSkeleton(Skeleton sk, OdeHumanoid human) {
        XMLTree rootNode = _tree.getRootNode();
        List<XMLTree> list = rootNode.getChildrenElement();
        for (XMLTree node : list) {
            if (node.getName().equals("bodys")) {
                List<XMLTree> listBodys = node.getChildrenElement();
                for (XMLTree nodebody : listBodys) {
                    if (nodebody.getName().equals("body")) {
                        buildBody(nodebody, sk, human);
                    }
                }
            }

            if (node.getName().equals("joints")) {
                List<XMLTree> listJoints = node.getChildrenElement();
                for (XMLTree nodejoint : listJoints) {
                    if (nodejoint.getName().equals("joint")) {
                        buildJoint(nodejoint, sk, human);
                    }
                }
            }
        }
        return true;
    }

    protected void buildBody(XMLTree current, Skeleton sk, OdeHumanoid human) {
        String name = current.getAttribute("name");
        String start = current.getAttribute("start");
        String end = current.getAttribute("end");
        double mass = current.getAttributeNumber("mass");
        double[] offset = getArray(current.getAttribute("offset"));
        String motiontype = current.getAttribute("motiontype");

        Joint startJ = sk.getJoint(start);
        Joint endJ = sk.getJoint(end);
        ColumnVector3 startP = startJ.getWorldPosition();
        ColumnVector3 endP = endJ.getWorldPosition();
        ColumnVector3 com = startP.add(endP);
        com.divideSelf(2);
        ColumnVector3 dir = endP.substract(startP);
        double[] box = {Math.abs(dir.x()), Math.abs(dir.y()), Math.abs(dir.z())};
        if (box[0] < 0.1) {
            box[0] = 0.1;
        }
        if (box[1] < 0.1) {
            box[1] = 0.1;
        }
        if (box[2] < 0.1) {
            box[2] = 0.1;
        }
        Matrix33 inertia = generateRotationInertia(box[0], box[1], box[2], mass, dir, new ColumnVector3(0, 1, 0));

        double[] color = getArray(current.getAttribute("color"));

        OdeRigidBody body = human.createBody(name);
        body.setMass(mass);
        body.setPosition(com.get(0) + offset[0], com.get(1) + offset[1], com.get(2) + offset[2]);
        double[] data = new double[9];
        inertia.getData(data);
        DMatrix3 I = new DMatrix3(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8]);
        body.setInertiaTensor(I);
        DBox boxD = OdeHelper.createBox(box[0], box[1], box[2]);
        body.setGeom(boxD);
        body.setColor(color);
        if (motiontype != null) {
            if (motiontype.equalsIgnoreCase("dynamics")) {
                body.setMotionType(OdeRigidBody.MotionType.DYNAMICS);
            } else if (motiontype.equalsIgnoreCase("kinematics")) {
                body.setMotionType(OdeRigidBody.MotionType.KINEMATICS);
            }
        }
        System.out.println(name + com);
    }

    protected void buildJoint(XMLTree current, Skeleton sk, OdeHumanoid human) {
        String name = current.getAttribute("name");
        String start = current.getAttribute("start");
        String end = current.getAttribute("end");
        String type = current.getAttribute("type");

        Joint joint = sk.getJoint(name);
        ColumnVector3 pos = joint.getPosition();
        double[] data = new double[3];
        pos.getData(data);
        JointType jointType = JointType.HINGE;
        if (type.equalsIgnoreCase("HINGE")) {
            jointType = JointType.HINGE;
        } else if (type.equalsIgnoreCase("UNIVERSAL")) {
            jointType = JointType.UNIVERSAL;
        } else if (type.equalsIgnoreCase("BALL")) {
            jointType = JointType.BALL;
        } else if (type.equalsIgnoreCase("FIXED")) {
            jointType = JointType.FIXED;
        }
        double[] axis0 = getArray(current.getAttribute("axis0"));
        double[] axis1 = getArray(current.getAttribute("axis1"));
        //double[] axis2 = getArray(current.getAttribute("axis2"));
        OdeJoint j = human.createJoint(name, jointType, start, end, data, axis0, axis1);

        {
            if (current.hasAttribute("loStop0")) {
                double low = current.getAttributeNumber("loStop0");
                j.setJointMin(0, low);
            }
            if (current.hasAttribute("hiStop0")) {
                double high = current.getAttributeNumber("hiStop0");
                j.setJointMax(0, high);
            }
        }
        {
            if (current.hasAttribute("loStop1")) {
                double low = current.getAttributeNumber("loStop1");
                j.setJointMin(1, low);
            }
            if (current.hasAttribute("hiStop1")) {
                double high = current.getAttributeNumber("hiStop1");
                j.setJointMax(1, high);
            }
        }
        {
            if (current.hasAttribute("loStop2")) {
                double low = current.getAttributeNumber("loStop2");
                j.setJointMin(2, low);
            }
            if (current.hasAttribute("hiStop2")) {
                double high = current.getAttributeNumber("hiStop2");
                j.setJointMax(2, high);
            }
        }

        if (current.hasAttribute("color")) {
            double[] color = getArray(current.getAttribute("color"));
            j.setColor(color);
        }
    }

    public double[] getArray(String str) {
        if (str.isEmpty()) {
            return null;
        }
        String[] parts = str.split(" ");
        double[] value = new double[parts.length];
        int i = 0;
        for (String part : parts) {
            value[i] = Double.valueOf(part);
            ++i;
        }
        return value;
    }

    public static Matrix33 generateRotationInertia(double x, double y, double z, double mass, ColumnVector3 dir, ColumnVector3 dirOriginal) {
        dir.normalize();
        dirOriginal.normalize();
        Matrix33 rotation = new Matrix33();
        rotation.set(0, 0, dirOriginal.dot(dir));
        rotation.set(0, 1, -dirOriginal.cross3(dir).length());
        rotation.set(1, 0, dirOriginal.cross3(dir).length());
        rotation.set(1, 1, dirOriginal.dot(dir));
        rotation.set(2, 2, 1);

        Matrix33 inertia = new Matrix33();
        inertia.set(0, 0, mass / 12.0f * (y * y + z * z));
        inertia.set(1, 1, mass / 12.0f * (z * z + x * x));
        inertia.set(2, 2, mass / 12.0f * (x * x + y * y));
        Matrix33 rotationT = new Matrix33();
        rotationT.set(rotation.transpose());
        inertia = rotation.multiply(inertia.multiply(rotationT));
        return inertia;
    }
}
