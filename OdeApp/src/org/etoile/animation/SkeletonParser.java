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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.etoile.core.animation.Joint;
import org.etoile.core.animation.Skeleton;
import org.etoile.core.math.ColumnVector3;
import vib.core.util.xml.XML;
import vib.core.util.xml.XMLParser;
import vib.core.util.xml.XMLTree;

/**
 *
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class SkeletonParser {

    String _filepath = "";
    XMLTree _tree;
    HashMap<String, Integer> _xmljointNameMap = new HashMap<>();
    ArrayList<String> _builtJointsNames = new ArrayList<>();

    public boolean loadFile(String filepath) {

        _filepath = filepath;

        XMLParser xmlparser = XML.createParser();
        xmlparser.setValidating(false);
        _tree = xmlparser.parseFile(filepath);
        return _tree != null;
    }

    public boolean readSkeletonInfo(Skeleton skeleton) {
        XMLTree rootNode = _tree.getRootNode();
        List<XMLTree> list = rootNode.getChildrenElement();
        skeleton.clearAll();
        for (XMLTree node : list) {
            if (node.getName().equals("bones")) {
                List<XMLTree> listBones = node.getChildrenElement();
                for (XMLTree nodebone : listBones) {
                    if (nodebone.getName().equals("bone")) {
                        readJoint(nodebone, skeleton);
                    }
                }
            }
            
            
            
            if (node.getName().equals("bonehierarchy")) {
                List<XMLTree> listBones = node.getChildrenElement();
                for (XMLTree nodebone : listBones) {
                    if (nodebone.getName().equals("boneparent")) {
                        readJointHierarchy(nodebone, skeleton);
                    }
                }

            }
        }

        for(Joint j : skeleton.getJoints()){
            j.update();
        }

        return true;
    }

    protected void readJoint(XMLTree current, Skeleton skeleton) {

        double id = current.getAttributeNumber("id");
        String name = current.getAttribute("name");
        Joint j = skeleton.createJoint(name, -1);
        List<XMLTree> lists = current.getChildrenElement();
        for (int i = 0; i < lists.size(); ++i) {
            XMLTree node = lists.get(i);
            if (node.getName().equals("position")) {
                double x = node.getAttributeNumber("x");
                double y = node.getAttributeNumber("y");
                double z = node.getAttributeNumber("z");
                j.setLocalPosition(new ColumnVector3((double) x, (double) y, (double) z));
            }

            if (node.getName().equals("rotation")) {
            }
        }
        
    }

    protected void readJointHierarchy(XMLTree current, Skeleton skeleton) {
        String jName = current.getAttribute("bone");
        String parentName = current.getAttribute("parent");
        Joint j = skeleton.getJoint(jName);
        Joint jp = skeleton.getJoint(parentName);
        j.setParent(jp);
    }

//    public static void main(String[] args) {
//
//        Skeleton skeleton = new Skeleton("test");
//        SkeletonParser parser = new SkeletonParser();
//        if (parser.loadFile("../bin/data/camille_skeleton.xml")) {
//            parser.readSkeletonInfo(skeleton);
//            System.out.println("finish loading skeleton");
//        }
//    }
}
