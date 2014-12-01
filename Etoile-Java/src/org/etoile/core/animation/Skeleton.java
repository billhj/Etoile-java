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
package org.etoile.core.animation;

import java.util.ArrayList;
import java.util.HashMap;
import org.etoile.core.math.ColumnVector3;
import org.etoile.core.math.Matrix44;
import org.etoile.core.math.Quaternion;


/***
 *
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 * skeleton structure for updating  
 */
public class Skeleton {

    String _name = "no";

    ArrayList<Joint> _joints = new ArrayList<>();
    HashMap<String, Integer> _jointNameIds = new HashMap<>();
    ArrayList<Integer> _parentJ = new ArrayList<>();
    ArrayList<ArrayList<Integer>> _children = new ArrayList();
    ArrayList<Quaternion> _localRotations = new ArrayList<>();
    ArrayList<Quaternion> _globalRotations = new ArrayList<>();
    ArrayList<ColumnVector3> _localPosition = new ArrayList<>();
    ArrayList<ColumnVector3> _globalPosition = new ArrayList<>();
    ArrayList<Matrix44> _localMs = new ArrayList<>();
    ArrayList<Matrix44> _globalMs = new ArrayList<>();

    //body info
//    ArrayList<Body> _bodys = new ArrayList<Body>();
//    ArrayList<Integer> _connectedParentJoint = new ArrayList<Integer>();
//    ArrayList<ColumnVector3> _localCOM = new ArrayList<ColumnVector3>();
//    ArrayList<Float> _mass = new ArrayList<Float>();
    /**
     *
     * @param name
     */
    public Skeleton(String name) {
        _name = name;
    }

    public Skeleton(Skeleton s) {
        set(s);
    }

    public void set(Skeleton s){
        this._children = s._children;
        this._globalMs = s._globalMs;
        this._globalPosition = s._globalPosition;
        this._globalRotations = s._globalRotations;
        this._jointNameIds = s._jointNameIds;
        this._joints = s._joints;
        this._localMs = s._localMs;
        this._localPosition = s._localPosition;
        this._localRotations = s._localRotations;
        this._name = s._name;
        this._parentJ = s._parentJ;
    }
    
    @Override
    public Skeleton clone() {
        return new Skeleton(this);
    }
    
    public void reset() {
        for (Joint j : _joints) {
            j.setLocalRotation(new Quaternion());
        }
        update();
    }

    public void update() {
        for (Joint j : _joints) {
            j.update();
        }
    }

    public String getName() {
        return _name;
    }

    /**
     *
     * @param joint
     * @param parent defines parent id
     * @return index in joint list
     */
    public Joint createJoint(String name, int parentId) {
        int id = _joints.size();
        _parentJ.add(-1);
        _children.add(new ArrayList<Integer>());
        _localRotations.add(new Quaternion());
        _globalRotations.add(new Quaternion());
        _localPosition.add(new ColumnVector3());
        _globalPosition.add(new ColumnVector3());
        _localMs.add(new Matrix44());
        _globalMs.add(new Matrix44());

        Joint j = new Joint(name, id, parentId, this);
        _joints.add(j);
        _jointNameIds.put(name, id);
        return j;
    }

    /**
     * Removes the specified joint
     *
     * @param id the identifier of the joint
     * @return {@code true} if the remove succeed, {@code false} otherwise
     */
    public boolean removeJoint(int id) {
        if (0 < id && id < _joints.size()) {
            Joint j = _joints.get(id);
            int start = _parentJ.get(id);
            for (int i = start; i < _parentJ.size(); ++i) {
                if (_parentJ.get(i) > id) {
                    _parentJ.set(i, _parentJ.get(i) - 1);
                } else if (_parentJ.get(i) == id) {
                    _parentJ.set(i, _parentJ.get(id));
                }
            }
            _jointNameIds.remove(j.getName());
            _joints.remove(id);
            _parentJ.remove(id);
            _children.remove(id);
            _localRotations.remove(id);
            _globalRotations.remove(id);
            _localPosition.remove(id);
            _globalPosition.remove(id);
            _localMs.remove(id);
            _globalMs.remove(id);
            return true;
        }
        return false;
    }

    /**
     * Removes the specified joint
     *
     * @param name the name of the joint
     * @return {@code true} if the remove succeed, {@code false} otherwise
     */
    public boolean removeJoint(String name) {
        return removeJoint(_jointNameIds.get(name));
    }

    /**
     *
     * @param id
     * @return joint
     */
    public Joint getJoint(int id) {
        if (id < _joints.size() && id > -1) {
            Joint j = _joints.get(id);
            return j;
        }
        return null;
    }

    /**
     *
     * @param name
     * @return joint
     */
    public Joint getJoint(String name) {

        if (_jointNameIds.containsKey(name)) {
            int id = _jointNameIds.get(name);
            return _joints.get(id);
        }
        return null;
    }

    /**
     *
     * @param name
     * @return index
     */
    public int getJointIndex(String name) {
        if (_jointNameIds.containsKey(name)) {
            int id = _jointNameIds.get(name);
            return id;
        }
        return -1;
    }

    /**
     *
     * @return joint list
     */
    public ArrayList<Joint> getJoints() {
        return _joints;
    }

    /**
     * clear joint
     */
    public void clearAll() {
        _joints.clear();
        _jointNameIds.clear();
        _parentJ.clear();
        _children.clear();
        _localRotations.clear();
        _globalRotations.clear();
        _localPosition.clear();
        _globalPosition.clear();
        _localMs.clear();
        _globalMs.clear();
    }

    public HashMap<String, Integer> getJointNameIds() {
        return _jointNameIds;
    }

    public ArrayList<Integer> getParentJ() {
        return _parentJ;
    }
    
    public int getParent(int id){
        return _parentJ.get(id);
    }

    public ArrayList<ArrayList<Integer>> getChildren() {
        return _children;
    }
    
    public ArrayList<Integer> getChildren(int id) {
        return _children.get(id);
    }

    public ArrayList<Quaternion> getLocalRotations() {
        return _localRotations;
    }
    
    public Quaternion getLocalRotation(int id) {
        return _localRotations.get(id);
    }

    public ArrayList<Quaternion> getGlobalRotations() {
        return _globalRotations;
    }
    
    public Quaternion getGlobalRotation(int id) {
        return _globalRotations.get(id);
    }

    public ArrayList<ColumnVector3> getLocalPositions() {
        return _localPosition;
    }
    
    public ColumnVector3 getLocalPosition(int id) {
        return _localPosition.get(id);
    }

    public ArrayList<ColumnVector3> getGlobalPositions() {
        return _globalPosition;
    }
    
    public ColumnVector3 getGlobalPosition(int id) {
        return _globalPosition.get(id);
    }

    public ArrayList<Matrix44> getLocalMatrixs() {
        return _localMs;
    }
    
    public Matrix44 getLocalMatrix(int id) {
        return _localMs.get(id);
    }

    public ArrayList<Matrix44> getGlobalMatrixs() {
        return _globalMs;
    }
    
    public Matrix44 getGlobalMatrix(int id) {
        return _globalMs.get(id);
    }
    

    public void loadRotations(HashMap<String, Quaternion> current) {
        for (String name : current.keySet()) {
            Joint j = getJoint(name);
            if (j != null) {
                j.setLocalRotation(current.get(name));
            }
        }
    }

    // a little optimisation when doing loadRotations(Map) followed by update()
    public void loadRotationsAndUpdate(HashMap<String, Quaternion> current) {
        for (Joint j : _joints) {
            Quaternion rot = current.get(j.getName());
            if (rot != null) {
                j.setLocalRotation(rot);//do also the update
            } else {
                j.update();//parent may changed
            }
        }
    }

    public void updateMatrix() {
        for (Joint j : _joints) {
            j.updateMatrix();
        }
    }

}
