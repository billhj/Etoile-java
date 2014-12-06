package org.etoile.animation.ode.controller;

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



/**
 *
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class JSimbiconController {
    final static int MaxStates = 100;
    final static int MaxJoints = 10;
    final static int MaxGroups = 30;
    final static int MaxJumps = 20;
    final static int NONE = -1;

    public ConState state[] = new ConState[MaxStates];                         // states, index-access by groups
    public Group groups[] = new Group[MaxGroups];         // groups
    public int nrStates = 0;                     // number of states
    public int nrGroups = 0;                     // number of groups
    public float kp[] = new float[MaxJoints];
    public float kd[] = new float[MaxJoints];
    public float targetLimit[][] = new float[2][MaxJoints];      // target joint angles min,max limits
    public float torqLimit[][] = new float[2][MaxJoints];        // torque min, max limits
    public float jointLimit[][] = new float[2][MaxJoints];       // joint angle limits

    public int fsmState = 0;                    // current state
    public float stateTime = 0;                 // time spent within state
    
    
    
    public int currentGroupNumber = 0;
    public int desiredGroupNumber = 0;
    
    
    // lookup group by name
    public Group findGroup(String name){
        
        return null;
    }
    
//    // advance FSM state
//    boolean advance(Bip7 b){
//	boolean transition = false;
//	ConState s = state[fsmState];
//
//	boolean oldStance = s.leftStance;
//	if ( s.timeFlag && (stateTime > s.transTime) )   // time-based transition ?
//		transition = true;
//	else if (!s.timeFlag && (b.FootState[s.sensorNum]!=0)) {   // sensor-based transition ?
//		transition = true;
//	}
//
//	if (transition) {
//		stateTime = 0;                    // reset state time
//		fsmState = s.next;           // normal state transition to next state
//                if (currentGroupNumber != desiredGroupNumber){
//                    currentGroupNumber = desiredGroupNumber;
//                    //find out the local group number
//                    int localfsmState = state[fsmState].localNum;
//                    fsmState = groups[currentGroupNumber].stateOffset + localfsmState;
//                }
//	}
//	boolean newStance = state[fsmState].leftStance;
//	boolean newStep = (newStance != oldStance)? true : false;
//	return newStep;
//    }
    
     // advance FSM state
    boolean advance(){
	boolean transition = false;
	ConState s = state[fsmState];

	boolean oldStance = s.leftStance;
	if ( s.timeFlag && (stateTime > s.transTime) )   // time-based transition ?
		transition = true;
//	else if (!s.timeFlag && (b.FootState[s.sensorNum]!=0)) {   // sensor-based transition ?
//		transition = true;
//	}

	if (transition) {
		stateTime = 0;                    // reset state time
		fsmState = s.next;           // normal state transition to next state
                if (currentGroupNumber != desiredGroupNumber){
                    currentGroupNumber = desiredGroupNumber;
                    //find out the local group number
                    int localfsmState = state[fsmState].localNum;
                    fsmState = groups[currentGroupNumber].stateOffset + localfsmState;
                }
	}
	boolean newStance = state[fsmState].leftStance;
	boolean newStep = (newStance != oldStance)? true : false;
	return newStep;
    }
    
    /** Creates a new instance of Controller */
    public JSimbiconController() {
        //initialize some of the default parameters
        for (int i=0;i<MaxJoints;i++){
            kp[i] = 300;
            kd[i] = 30;
            torqLimit[0][i] = -1000;
            torqLimit[1][i] = 1000;
            targetLimit[0][i] = -(float)Math.PI;
            targetLimit[1][i] = (float)Math.PI;
            jointLimit[0][i] = -(float)Math.PI;
            jointLimit[1][i] = (float)Math.PI;        
        }

        jointLimit[0][1] = -1;
        jointLimit[1][1] = 3;
        jointLimit[0][3] = -1;
        jointLimit[1][3] = 3;        

        jointLimit[0][2] = -3;
        jointLimit[1][2] = -0.02f;
        jointLimit[0][4] = -3;
        jointLimit[1][4] = -0.02f;        
        
        targetLimit[0][1] = -0.4f;
        targetLimit[1][1] = 1.6f;
        targetLimit[0][3] = -0.4f;
        targetLimit[1][3] = 1.6f; 
        
        nrStates = 0;
        nrGroups = 0;
    }
    
    
    
    /** manually add a walking controller to the states...*/
    public void addWalkingController(){
        /* - State 0 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 0;
        state[nrStates].next = nrStates+1;
        state[nrStates].timeFlag = true;
        state[nrStates].leftStance = true;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0.3f;
        state[nrStates].sensorNum = 0;

        state[nrStates].setThThDThDD(0, 0, 0, 0);
        state[nrStates].setThThDThDD(1, 0.4f, 0, 0.2f);
        state[nrStates].setThThDThDD(2, -1.1f, 0, 0);
        state[nrStates].setThThDThDD(3, 0, 0, 0);
        state[nrStates].setThThDThDD(4, -0.05f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.2f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.2f, 0, 0);
       
        nrStates ++;

        /* - State 1 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 1;
        state[nrStates].next = nrStates+1;
        state[nrStates].timeFlag = false;
        state[nrStates].leftStance = true;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0;
        state[nrStates].sensorNum = 0;

        state[nrStates].setThThDThDD(0, 0, 0, 0);
        state[nrStates].setThThDThDD(1, -0.7f, 2.2f, 0);
        state[nrStates].setThThDThDD(2, -0.05f, 0, 0);
        state[nrStates].setThThDThDD(3, 0, 0, 0);
        state[nrStates].setThThDThDD(4, -0.1f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.2f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.2f, 0, 0);
        nrStates ++;

        
        /* - State 2 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 2;
        state[nrStates].next = nrStates+1;
        state[nrStates].timeFlag = true;
        state[nrStates].leftStance = false;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0.3f;
        state[nrStates].sensorNum = 0;

        state[nrStates].setThThDThDD(0, 0, 0, 0);
        state[nrStates].setThThDThDD(1, 0, 0, 0);
        state[nrStates].setThThDThDD(2, -0.05f, 0, 0);
        state[nrStates].setThThDThDD(3, 0.4f, 0, 0.2f);
        state[nrStates].setThThDThDD(4, -1.1f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.2f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.2f, 0, 0);
        nrStates ++;
        

        /* - State 3 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 1;
        state[nrStates].next = nrStates-3;
        state[nrStates].timeFlag = false;
        state[nrStates].leftStance = false;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0;
        state[nrStates].sensorNum = 6;

        state[nrStates].setThThDThDD(0, 0, 0, 0);
        state[nrStates].setThThDThDD(1, 0, 0, 0);
        state[nrStates].setThThDThDD(2, -0.1f, 0, 0);
        state[nrStates].setThThDThDD(3, -0.7f, 2.2f, 0);
        state[nrStates].setThThDThDD(4, -0.05f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.2f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.2f, 0, 0);
        nrStates ++;        
        
        groups[nrGroups] = new Group();
        groups[nrGroups].num = 0;
        groups[nrGroups].stateOffset = nrStates-4;
        groups[nrGroups].nStates = 4;
        
        nrGroups++;

    }

    
    /** manually add a running controller to the states...*/
    public void addRunningController(){
        /* - State 0 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 0;
        state[nrStates].next = nrStates+1;
        state[nrStates].timeFlag = true;
        state[nrStates].leftStance = true;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0.21f;
        state[nrStates].sensorNum = 0;

        state[nrStates].setThThDThDD(0, 0, 0, 0);
        state[nrStates].setThThDThDD(1, 0.8f, 0, 0.2f);
        state[nrStates].setThThDThDD(2, -1.84f, 0, 0);
        state[nrStates].setThThDThDD(3, 0, 0, 0);
        state[nrStates].setThThDThDD(4, -0.05f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.2f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.27f, 0, 0);
        
        nrStates ++;

        /* - State 1 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 1;
        state[nrStates].next = nrStates+1;
        state[nrStates].timeFlag = true;
        state[nrStates].leftStance = true;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0;
        state[nrStates].sensorNum = 0;

        state[nrStates].setThThDThDD(0, -0.22f, 0, 0);
        state[nrStates].setThThDThDD(1, 1.08f, 0.0f, 0.2f);
        state[nrStates].setThThDThDD(2, -2.18f, 0, 0);
        state[nrStates].setThThDThDD(3, 0, 0, 0);
        state[nrStates].setThThDThDD(4, -0.05f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.2f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.27f, 0, 0);
        nrStates ++;

        /* - State 2 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 2;
        state[nrStates].next = nrStates+1;
        state[nrStates].timeFlag = true;
        state[nrStates].leftStance = false;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0.21f;
        state[nrStates].sensorNum = 0;

        state[nrStates].setThThDThDD(0, 0, 0, 0);
        state[nrStates].setThThDThDD(1, 0, 0, 0);
        state[nrStates].setThThDThDD(2, -0.05f, 0, 0);
        state[nrStates].setThThDThDD(3, 0.8f, 0, 0.2f);
        state[nrStates].setThThDThDD(4, -1.84f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.27f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.2f, 0, 0);
        nrStates ++;

        /* - State 3 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 1;
        state[nrStates].next = nrStates-3;
        state[nrStates].timeFlag = true;
        state[nrStates].leftStance = false;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0;
        state[nrStates].sensorNum = 6;

        

        state[nrStates].setThThDThDD(0, -0.22f, 0, 0);
        state[nrStates].setThThDThDD(1, 0, 0, 0);
        state[nrStates].setThThDThDD(2, -0.05f, 0, 0);
        state[nrStates].setThThDThDD(3, 1.08f, 0.0f, 0.2f);
        state[nrStates].setThThDThDD(4, -2.18f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.27f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.2f, 0, 0);
        nrStates ++;        
        
        groups[nrGroups] = new Group();
        groups[nrGroups].num = 1;
        groups[nrGroups].stateOffset = nrStates-4;
        groups[nrGroups].nStates = 4;

        
        nrGroups++;

    }
    
    
    /** manually add a crouch walking controller to the states...*/
    public void addCrouchWalkController(){
        /* - State 0 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 0;
        state[nrStates].next = nrStates+1;
        state[nrStates].timeFlag = true;
        state[nrStates].leftStance = true;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0.3f;
        state[nrStates].sensorNum = 0;

        state[nrStates].setThThDThDD(0, -0.18f, 0, 0);
        state[nrStates].setThThDThDD(1, 1.1f, 0, 0.2f);
        state[nrStates].setThThDThDD(2, -2.17f, 0, 0);
        state[nrStates].setThThDThDD(3, 0, 0, 0);
        state[nrStates].setThThDThDD(4, -0.97f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.62f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.44f, 0, 0);
        
        nrStates ++;

        /* - State 1 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 1;
        state[nrStates].next = nrStates+1;
        state[nrStates].timeFlag = false;
        state[nrStates].leftStance = true;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0;
        state[nrStates].sensorNum = 0;

        state[nrStates].setThThDThDD(0, -0.25f, 0, 0);
        state[nrStates].setThThDThDD(1, -0.7f, 2.2f, 0.0f);
        state[nrStates].setThThDThDD(2, -0.05f, 0, 0);
        state[nrStates].setThThDThDD(3, 0, 0, 0);
        state[nrStates].setThThDThDD(4, -0.92f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.2f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.44f, 0, 0);
        nrStates ++;

        /* - State 2 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 2;
        state[nrStates].next = nrStates+1;
        state[nrStates].timeFlag = true;
        state[nrStates].leftStance = false;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0.3f;
        state[nrStates].sensorNum = 0;

        state[nrStates].setThThDThDD(0, -0.18f, 0, 0);
        state[nrStates].setThThDThDD(1, 0, 0, 0);
        state[nrStates].setThThDThDD(2, -0.97f, 0, 0);
        state[nrStates].setThThDThDD(3, 1.1f, 0, 0.2f);
        state[nrStates].setThThDThDD(4, -2.17f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.44f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.62f, 0, 0);
        nrStates ++;
        
        /* - State 3 - */
        state[nrStates] = new ConState();
        state[nrStates].num = nrStates;
        state[nrStates].localNum = 1;
        state[nrStates].next = nrStates-3;
        state[nrStates].timeFlag = false;
        state[nrStates].leftStance = false;
        state[nrStates].poseStance = false;
        state[nrStates].transTime = 0;
        state[nrStates].sensorNum = 6;

        

        state[nrStates].setThThDThDD(0, -0.25f, 0, 0);
        state[nrStates].setThThDThDD(1, 0, 0, 0);
        state[nrStates].setThThDThDD(2, -0.92f, 0, 0);
        state[nrStates].setThThDThDD(3, -0.7f, 2.2f, 0.0f);
        state[nrStates].setThThDThDD(4, -0.05f, 0, 0);
        state[nrStates].setThThDThDD(5, 0.44f, 0, 0);
        state[nrStates].setThThDThDD(6, 0.2f, 0, 0);
        nrStates ++;        

        groups[nrGroups] = new Group();
        groups[nrGroups].num = 2;
        groups[nrGroups].stateOffset = nrStates-4;
        groups[nrGroups].nStates = 4;

        
        nrGroups++;

    }
        
    
}





