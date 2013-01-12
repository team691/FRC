/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.RobotMain;
import edu.wpi.first.wpilibj.SimpleRobot;

/**
 *
 * @author Aaron Dolgin
 */
public class RobotTemplate extends SimpleRobot{
    RobotMain rm = new RobotMain();
    
    public void robotInit(){
        rm.robotInit();
    }
    
    public void autonomous(){
        rm.autonomous();
    }
    
    public void operatorControl(){
        rm.operatorControl();
    }
    
    public void disabled(){
        rm.disabled();
    }
}
