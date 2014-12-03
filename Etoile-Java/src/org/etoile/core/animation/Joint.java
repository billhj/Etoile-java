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
import org.etoile.core.math.ColumnVector3;
import org.etoile.core.math.Matrix44;
import org.etoile.core.math.Quaternion;

/**
 *
 * @author Jing Huang
 *
 * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class Joint {

    public static enum JointType {

        J_UNKNOWN,
        J_FREEEULER,
        J_FREEEXPMAP,
        J_BALLEULER,
        J_BALLEXPMAP,
        J_HINGE,
        J_UNIVERSAL,
        J_TRANS
    };

    String _name;
    int _id = -1;
    Skeleton _skeleton;
    JointType _type = JointType.J_UNKNOWN;
    Dof[] _dofs = new Dof[3];

    public Joint(String name, int id, int parent, Skeleton skeleton) {
        _skeleton = skeleton;
        _name = name;
        _id = id;
        _skeleton._parentJ.set(_id, parent);
    }

    public Joint(Joint j) {
        _skeleton = j._skeleton;
        _name = j._name;
        _id = j._id;
        _type = j._type;
        if (j._dofs[0] != null) {
            _dofs[0].copy(j._dofs[0]);
        }
        if (j._dofs[1] != null) {
            _dofs[1].copy(j._dofs[1]);
        }
        if (j._dofs[2] != null) {
            _dofs[2].copy(j._dofs[2]);
        }
    }

    public void setSkeleton(Skeleton skeleton) {
        _skeleton = skeleton;
    }

    @Override
    public Joint clone() {
        return new Joint(this);
    }

    public Joint(String name) {
        this(name, -1, -1, null);
    }

    public void setDofs(JointType type, Dof[] dofs) {
        if (dofs[0] != null) {
            _dofs[0].copy(dofs[0]);
        }
        if (dofs[1] != null) {
            _dofs[1].copy(dofs[1]);
        }
        if (dofs[2] != null) {
            _dofs[2].copy(dofs[2]);
        }
        _type = type;
    }

    public void updateDof(int idx, double value) {
        _dofs[idx].setValue(value);
    }

    public void setParent(Joint parent) {
        if (parent != null) {
            _skeleton._parentJ.set(_id, parent._id);
            if (_skeleton._children.get(parent._id) == null) {
                _skeleton._children.set(parent._id, new ArrayList<Integer>());
            }
            _skeleton._children.get(parent._id).add(_id);
        }
    }

    public void setParent(int parent) {
        if (parent != -1) {
            _skeleton._parentJ.set(_id, parent);
            if (_skeleton._children.get(parent) == null) {
                _skeleton._children.set(parent, new ArrayList<Integer>());
            }
            _skeleton._children.get(parent).add(_id);
        }
    }

    public int getId() {
        return _id;
    }

    public Joint getParent() {
        return _skeleton.getJoint(_skeleton._parentJ.get(_id));
    }

    public int getParentId() {
        return _skeleton._parentJ.get(_id);
    }

    public void addChild(Joint child) {
        if (_skeleton._children.get(_id) == null) {
            _skeleton._children.set(_id, new ArrayList<Integer>());
        }
        _skeleton._children.get(_id).add(child._id);
    }

    public ArrayList<Integer> getChildren() {
        return _skeleton._children.get(_id);
    }

    public String getName() {
        return _name;
    }

    public void setLocalRotation(Quaternion local) {
        _skeleton._localRotations.get(_id).setValue(local);
    }

    public Quaternion getLocalRotation() {
        return _skeleton._localRotations.get(_id);
    }

    public Quaternion getWorldOrientation() {
        return _skeleton._globalRotations.get(_id);
    }

    public void setLocalPosition(ColumnVector3 translation) {
        _skeleton._localPosition.get(_id).set(0, translation.x());
        _skeleton._localPosition.get(_id).set(1, translation.y());
        _skeleton._localPosition.get(_id).set(2, translation.z());
    }

    public ColumnVector3 getLocalPosition() {
        return _skeleton._localPosition.get(_id);
    }

    public ColumnVector3 getWorldPosition() {
        return _skeleton._globalPosition.get(_id);
    }

    public void rotate(Quaternion rotation) {
        Quaternion tmp = rotation;
        tmp.normalize();
        Quaternion r = _skeleton._localRotations.get(_id);
        r.multiply(tmp);
        r.normalize();
    }

    public ColumnVector3 getPosition() {
        return _skeleton._globalPosition.get(_id);
    }

    public void update() {
        if (getParentId() == -1) {
            _skeleton._globalPosition.get(_id).set(_skeleton._localPosition.get(_id));
            _skeleton._globalRotations.get(_id).setValue(_skeleton._localRotations.get(_id));
            _skeleton._globalRotations.get(_id).normalize();

        } else {
            _skeleton._globalPosition.get(_id).set(getParent().getWorldOrientation().multiply(_skeleton._localPosition.get(_id)));
            _skeleton._globalPosition.get(_id).add(getParent().getWorldPosition());
            _skeleton._globalRotations.get(_id).setValue(getParent().getWorldOrientation().multiply(getLocalRotation()));
            _skeleton._globalRotations.get(_id).normalize();
        }
    }

    public void updateMatrix() {
        _skeleton._localMs.get(_id).set(_skeleton._localRotations.get(_id).getMatrix());
        _skeleton._localMs.get(_id).set(0, 3, getLocalPosition().x());
        _skeleton._localMs.get(_id).set(1, 3, getLocalPosition().y());
        _skeleton._localMs.get(_id).set(2, 3, getLocalPosition().z());
        if (getParentId() == -1) {
            _skeleton._globalMs.get(_id).set(_skeleton._localMs.get(_id));
        } else {
            _skeleton._globalMs.get(_id).set(getParent().getGlobalMatrix().multiply(getLocalMatrix()));
        }
    }

    public Matrix44 getLocalMatrix() {
        return _skeleton._localMs.get(_id);
    }

    public Matrix44 getGlobalMatrix() {
        return _skeleton._globalMs.get(_id);
    }

}
