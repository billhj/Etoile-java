/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.etoile.animation.demo;

import org.etoile.animation.ode.SimpleOdeCollision;
import static org.ode4j.drawstuff.DrawStuff.dsDrawBox;
import static org.ode4j.drawstuff.DrawStuff.dsSetColorAlpha;
import org.ode4j.math.DMatrix3;
import org.ode4j.math.DVector3;
import org.ode4j.ode.DContact;
import org.ode4j.ode.DContactBuffer;
import org.ode4j.ode.DContactGeom;
import org.ode4j.ode.DContactJoint;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DSpace;
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
public class DemoSimpleOdeCollision extends SimpleOdeCollision {

    boolean _showContactPoint = true;

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
            DMatrix3 RI = new DMatrix3();
            RI.setIdentity();
            for (int i = 0; i < n; i++) {
                DContact contact = contacts.get(i);

                // Paranoia  <-- not working for some people, temporarily removed for 0.6
                //dIASSERT(dVALIDVEC3(contact[i].geom.pos));
                //dIASSERT(dVALIDVEC3(contact[i].geom.normal));
                //dIASSERT(!dIsNan(contact[i].geom.depth));
                contact.surface.slip1 = 0.1;
                contact.surface.slip2 = 0.1;
                contact.surface.mode = dContactSoftERP | dContactSoftCFM | dContactApprox1 | dContactSlip1 | dContactSlip2;
                contact.surface.mu = 0.2; // was: dInfinity
                contact.surface.soft_erp = 0.06;
                contact.surface.soft_cfm = 0.04;
                DContactJoint c = OdeHelper.createContactJoint(_world, _contactgroup, contact);
                c.attach(contact.geom.g1.getBody(),
                        contact.geom.g2.getBody());
                if (_showContactPoint) {
                    DContactGeom contactgeom = contacts.get(i).getContactGeom();
                    double dp = contactgeom.depth;
                    DVector3 normal = contactgeom.normal;
                    DVector3 ss = new DVector3(0.02, 0.02, 0.02).add(normal.reScale(dp));
                    dsSetColorAlpha(1, 0, 0, 1);
                    dsDrawBox(contactgeom.pos, RI, ss);
                }
            }
        }
    }
}
