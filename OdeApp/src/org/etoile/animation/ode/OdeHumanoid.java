/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.animation.ode;

import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;

/**
 *
 * @author Jing Huang
 */
public class OdeHumanoid extends OdeMultiBodyObject{

    protected double[] _COM = new double[3];
    protected double[] _COMDiff = new double[3];
    protected double[] _rootOffset = new double[3];
    protected double[] _externalCOM = new double[3];
    protected double _externalMass = 0;
    protected double[] _roottranslation = new double[3];
    
    public OdeHumanoid(OdePhysicsEnvironment env) {
        super(env);
    }
    
    public void glueFeetToFloor(){
        OdeRigidBody fl = _env.getBody(_bodyIds.get("foot_l"));
        OdeRigidBody fr = _env.getBody(_bodyIds.get("foot_r"));
        //_env.getSpace().add(fl.getGeom());
        //_env.getSpace().add(fr.getGeom());
        double[] posl = {fl.getPosition().get0(),fl.getPosition().get1(),fl.getPosition().get2()};
        double[] posr = {fr.getPosition().get0(),fr.getPosition().get1(),fr.getPosition().get2()};
        OdeJoint fixl = createJoint("fix_l_foot", JointType.FIXED, fl, null, posl, null, null);
        OdeJoint fixr = createJoint("fix_r_foot", JointType.FIXED, fr, null, posr, null, null);
    }
    
    public void addExternalCOMterm(double[] externalCOM, double externalMass){
        OdeRigidBody fl = _env.getBody(_bodyIds.get("foot_l"));
        _externalCOM = externalCOM;
        _externalMass = externalMass;
    }
    
    public void addLocalExternalCOMterm(double[] externalCOM, double externalMass){
        computeRootTranslation();
        _externalCOM[0] = _roottranslation[0] + externalCOM[0];
        _externalCOM[1] = _roottranslation[1] + externalCOM[1];
        _externalCOM[2] = _roottranslation[2] + externalCOM[2];
        _externalMass = externalMass;
    }
    
    public void setRootOffset(double x, double y, double z){
        _rootOffset[0] = x;
        _rootOffset[1] = y;
        _rootOffset[2] = z;
    }

    public double[] getRootOffset() {
        return _rootOffset;
    }
    
    public double[] computeRootTranslation(){
        OdeRigidBody root = _env.getBody(_bodyIds.get("lower_torso"));
        QuaternionD r = root.getRotation();
        DVector3 newoffset = QuaternionD.multiplication(r, new DVector3(_rootOffset[0],_rootOffset[1],_rootOffset[2]));
        DVector3C pos =root.getPosition();
        _roottranslation[0] = pos.get0() - newoffset.get0();
        _roottranslation[1] = pos.get1() - newoffset.get1();
        _roottranslation[2] = pos.get2() - newoffset.get2();
        return _roottranslation;
    }
    
    public QuaternionD getRootRotation(){
        OdeRigidBody root = _env.getBody(_bodyIds.get("lower_torso"));
        QuaternionD r = root.getRotation();
        return r;
    }
    
    public void updateCOM(double dt){
        DVector3 center = new DVector3(0,0,0);
        double totalMass = 0;
        for(String name: _bodyIds.keySet()){
            OdeRigidBody body = getBody(name);
            DVector3C pos = body.getPosition();
            double mass = body.getMass();
            center.add(pos.reScale(mass));
            totalMass += mass;
        }
        totalMass += _externalMass;
        DVector3C pos = new DVector3(_externalCOM[0] * _externalMass, _externalCOM[1]*_externalMass, _externalCOM[2]*_externalMass);
        center.add(pos);
        center.scale(1.0/totalMass);
        
        if(dt == 0){
            _COMDiff[0] = 0;
            _COMDiff[1] = 0;
            _COMDiff[2] = 0;
        }else{
            _COMDiff[0] = (center.get0() - _COM[0]) / dt;
            _COMDiff[1] = (center.get1() - _COM[1]) / dt;
            _COMDiff[2] = (center.get2() - _COM[2]) / dt;
        }
        _COM[0] = center.get0();
        _COM[1] = center.get1();
        _COM[2] = center.get2();
    }
    
    public void getCOM(double[] COM){
        COM[0] = _COM[0];
        COM[1] = _COM[1];
        COM[2] = _COM[2];
    }
    
    public void getCOMDiff(double[] COMdiff){
        COMdiff[0] = _COMDiff[0];
        COMdiff[1] = _COMDiff[1];
        COMdiff[2] = _COMDiff[2];
    }
  
    public double[] getTranslation(){
        return _roottranslation;
    }
}
