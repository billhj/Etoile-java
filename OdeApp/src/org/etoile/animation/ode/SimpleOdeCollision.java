/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.etoile.animation.ode;

import org.ode4j.ode.DContact;
import org.ode4j.ode.DContactBuffer;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DJoint;
import org.ode4j.ode.DJointGroup;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DWorld;
import static org.ode4j.ode.OdeConstants.dContactApprox1;
import static org.ode4j.ode.OdeConstants.dContactSlip1;
import static org.ode4j.ode.OdeConstants.dContactSlip2;
import static org.ode4j.ode.OdeConstants.dContactSoftCFM;
import static org.ode4j.ode.OdeConstants.dContactSoftERP;
import org.ode4j.ode.OdeHelper;

/**
 *
 * @author Jing Huang
 */
public class SimpleOdeCollision implements BaseCollision{

    protected static final int MAX_CONTACTS = 32;          // maximum number of contact points per body
    protected static DJointGroup _contactgroup = OdeHelper.createJointGroup();
    protected DWorld _world;
    protected DSpace _space;
    
    public SimpleOdeCollision(){
    
    }
    
    public SimpleOdeCollision(DWorld world, DSpace space){
        _world = world;
        _space = space;
    }

    public DWorld getWorld() {
        return _world;
    }

    public void setWorld(DWorld world) {
        this._world = world;
    }

    public DSpace getSpace() {
        return _space;
    }

    public void setSpace(DSpace space) {
        this._space = space;
    }
    
    
    
    protected void nearCallback(Object data, DGeom o1, DGeom o2) {
        assert (o1 != null);
        assert (o2 != null);

        if (o1 instanceof DSpace || o2 instanceof DSpace) {
            //fprintf(stderr,"testing space %p %p\n", o1,o2);
            System.err.println("testing space " + o1 + " " + o2);
            // colliding a space with something
            OdeHelper.spaceCollide2(o1, o2, data, nearCallback);
			// Note we do not want to test intersections within a space,
            // only between spaces.
            return;
        }

		//  fprintf(stderr,"testing geoms %p %p\n", o1, o2);
        final int N = MAX_CONTACTS;
        DContactBuffer contacts = new DContactBuffer(N);
        int n = OdeHelper.collide(o1, o2, N, contacts.getGeomBuffer());
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                DContact contact = contacts.get(i);
				// Paranoia  <-- not working for some people, temporarily removed for 0.6
                //dIASSERT(dVALIDVEC3(contact[i].geom.pos));
                //dIASSERT(dVALIDVEC3(contact[i].geom.normal));
                //dIASSERT(!dIsNan(contact[i].geom.depth));
                contact.surface.slip1 = 0.1;
                contact.surface.slip2 = 0.1;
                contact.surface.mode = dContactSoftERP | dContactSoftCFM | dContactApprox1 | dContactSlip1 | dContactSlip2;
                contact.surface.mu = 0.3; // was: dInfinity
                contact.surface.soft_erp = 0.06;
                contact.surface.soft_cfm = 0.04;
                DJoint c = OdeHelper.createContactJoint(_world, _contactgroup, contact);
                c.attach(contact.geom.g1.getBody(),
                        contact.geom.g2.getBody());
            }
        }
    }

    protected DGeom.DNearCallback nearCallback = new DGeom.DNearCallback() {
        @Override
        public void call(Object data, DGeom o1, DGeom o2) {
            nearCallback(data, o1, o2);
        }
    };
    @Override
    public void startCollision() {
        _space.collide(null, nearCallback);
    }

    @Override
    public void endCollision() {
        _contactgroup.empty();
    }

    public static int getMAX_CONTACTS() {
        return MAX_CONTACTS;
    }

    public static DJointGroup getContactgroup() {
        return _contactgroup;
    }
    
    
    
    
}
