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
import org.etoile.animation.ode.OdeHumanoid;
import org.etoile.animation.ode.OdeRigidBody;
import org.etoile.core.animation.Joint;
import org.etoile.core.animation.Skeleton;
import org.etoile.core.math.ColumnVector3;
import org.etoile.core.math.Matrix33;
import org.ode4j.math.DMatrix3;
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
        
        Joint startJ = sk.getJoint(start);
        Joint endJ = sk.getJoint(end);
        ColumnVector3 startP = startJ.getWorldPosition();
        ColumnVector3 endP = endJ.getWorldPosition();
        ColumnVector3 com = startP.add(endP);
        com.divideSelf(2);
        ColumnVector3 dir = endP.substract(startP);
        double[] box = {Math.abs(dir.x()),Math.abs(dir.y()),Math.abs(dir.z())};
        if (box[0] < 0.1) {
            box[0] = 0.1;
        } 
        if (box[1] < 0.1) {
            box[1] = 0.1;
        } 
        if (box[2] < 0.1) {
            box[2] = 0.1;
        } 
        Matrix33 inertia = generateRotationInertia(box[0], box[1], box[2], mass, dir, new ColumnVector3(0,1,0));
        
        double[] color = getArray(current.getAttribute("color"));
        
        OdeRigidBody body = human.createBody(name);
        body.setMass(mass);
        body.setPosition(com.get(0),com.get(1),com.get(2));
        DMatrix3 I = new DMatrix3();
        inertia.
        I.set(bi._I[0], bi._I[1], bi._I[2], bi._I[3], bi._I[4], bi._I[5], bi._I[6], bi._I[7], bi._I[8]);
            body.setInertiaTensor(I);
            DBox box = OdeHelper.createBox(bi._box[0], bi._box[1], bi._box[2]);
            body.setGeom(box);
            body.setColor(bi._color);
            body.setMotionType(bi._motiontype);
        
        
    }
    
    protected void buildJoint(XMLTree current, Skeleton sk, OdeHumanoid human) {
        String name = current.getAttribute("name");
        String start = current.getAttribute("start");
        String end = current.getAttribute("end");
        String type = current.getAttribute("type");
        
        
        
        double[] color = getArray(current.getAttribute("color"));
        
    }

    public double[] getArray(String str) {
        if(str.isEmpty()) return null;
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
        rotationT.copyData(rotation.transpose());
        inertia = rotation.multiply(inertia.multiply(rotationT));
        return inertia;
    }
}
