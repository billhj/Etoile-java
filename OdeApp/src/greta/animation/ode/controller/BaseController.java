/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package greta.animation.ode.controller;

/**
 *
 * @author Jing Huang
 * 
 */
public interface BaseController{

    public abstract void update(double dt);
    public abstract String getName() ;
    public abstract void setName(String name) ;
    public abstract int getIndex() ;
    public abstract void setIndex(int index);
    public abstract void setActive(boolean active);
    public abstract boolean isActive();
}
