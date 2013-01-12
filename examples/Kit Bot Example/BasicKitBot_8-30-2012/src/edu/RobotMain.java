/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu;


import edu.wpi.first.wpilibj.SimpleRobot;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Joystick;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotMain extends SimpleRobot
{
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous()
    {
        
    }
    
    
    public Victor rightWheel = new Victor( 1, 1 );
    public Victor leftWheel = new Victor ( 1, 2 );
    
    public Joystick leftJoystick = new Joystick( 1 );
    //public Joystick rightJoystick = new Joystick( 2 );
    

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() 
    {
        double X = leftJoystick.getX();
        double Y = -leftJoystick.getY();
        
        rightWheel.set( Y - X );
        leftWheel.set( Y + X );
        
        System.out.println( "Hello World" );
        
    }
}
