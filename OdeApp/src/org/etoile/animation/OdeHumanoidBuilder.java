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
import org.etoile.core.animation.Skeleton;
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
    
    public boolean buildBodyFromSkeleton(Skeleton sk, OdeHumanoid human){
        XMLTree rootNode = _tree.getRootNode();
        List<XMLTree> list = rootNode.getChildrenElement();
        for (XMLTree node : list) {
            if (node.getName().equals("bodys")) {
                List<XMLTree> listBodys = node.getChildrenElement();
                for (XMLTree nodebone : listBodys) {
                    if (nodebone.getName().equals("body")) {
                        
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
}
