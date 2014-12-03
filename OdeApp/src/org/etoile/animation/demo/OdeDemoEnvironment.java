/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.etoile.animation.demo;

import org.etoile.animation.ode.OdePhysicsEnvironment;
import org.etoile.animation.ode.SimpleOdeCollision;

/**
 *
 * @author huang
 */
public class OdeDemoEnvironment extends OdePhysicsEnvironment{

    public OdeDemoEnvironment(){
        super();
        _id = "OdeDemoEnvironment";
    }
    
    @Override
    public void physicsSimulationStep() {
        super.physicsSimulationStep();
    }
    
    void setSimpleOdeCollision(SimpleOdeCollision collision){
        collision.setSpace(_space);
        collision.setWorld(_world);
        _collision = collision;
    }
}
