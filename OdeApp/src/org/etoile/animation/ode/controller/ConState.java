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
import org.etoile.animation.ode.controller.JSimbiconController;


/**
 *
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class ConState {

///////////////////////////////////////////////////
// FILE:      controller.h
// CONTAINS:  defs for FSM controller
///////////////////////////////////////////////////
	public int num;               // absolute state number
	public int localNum;          // local state number
	public float th[] = new float[JSimbiconController.MaxJoints];   // target angles
	public float thd[] = new float[JSimbiconController.MaxJoints];  // coeff for d
	public float thdd[] = new float[JSimbiconController.MaxJoints]; // coeff for d_dot
	public int next;              // next state
	public boolean timeFlag;         // TRUE for time-based transitions
	public boolean leftStance;       // TRUE if this is a state standing on left foot
	public boolean poseStance;       // TRUE is this is an absolute pose state
	public float transTime;       // transition time
	public int sensorNum;         // transition sensor number

   
    
    /** Creates a new instance of ConState */
    public ConState() {
        num = -1;
    }
    
    public void setThThDThDD(int index, float t, float tD, float tDD){
        th[index] = t;
        thd[index] = tD;
        thdd[index] = tDD;
    }
    
}
