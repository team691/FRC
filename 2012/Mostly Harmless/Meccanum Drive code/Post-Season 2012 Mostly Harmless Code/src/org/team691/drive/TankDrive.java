package org.team691.drive;

import edu.wpi.first.wpilibj.SpeedController;
import org.team691.util.DoubleSpeedController;

/**
 * This class represents and moves a simple tank drive system. The class takes
 * two speed controllers, that mechanically control all the wheels on their
 * respective sides of the robot (use the {@link DoubleSpeedController} if
 * there are two motors on a given side).
 * 
 * Implements the drive interface so that drive systems can be swapped out inside
 * the program with minimal effort.
 * 
 * @author Gerard Boberg, Akira
 * 
 * @see Drive
 */
public class TankDrive implements Drive
{
    protected SpeedController rightDriveMotor;
    protected SpeedController leftDriveMotor;
    protected boolean enabled = true;
    
    /**
     * Constructor for the Tank Drive system.
     * 
     * @param rightMotor The motor to control the right side.
     * @param leftMotor The motor to control the left side.
     */
    public TankDrive(SpeedController rightMotor, SpeedController leftMotor){
        rightDriveMotor = rightMotor;
        leftDriveMotor = leftMotor;
    }

    /**
     * Passes command flow to the drive system so that it can move the robot.
     * Does nothing if the drive system has been disabled.
     * 
     * @param Y A number, retrieved from a joystick axis and inside [-1,1],
     * representing how much the driver wants to move forwards.
     * @param X A number, retrieved from a joystick axis and inside [-1,1],
     * representing how much the driver wants to strafe left. This is unused in
     * tank drive because it has no way of applying a force perpendicular to it's
     * forward axis.
     * @param T A number, retrieved from a joystick axis and inside [-1,1],
     * representing how much the driver wants to turn clockwise.
     */
    public void update(double Y, double X, double T)
    {
        // not enabled --> get out and do nothing
        if( !enabled ){return;}
        
        rightDriveMotor.set( Y - T );
        leftDriveMotor.set ( Y + T );
    }

    /**
     * Disables this drive system. This calls <code>stop()</code> and sets a flag
     * so that <code>update()</code> will do nothing until <code>enable()</code> is called.
     * @see enable()
     */
    public void disable()
    {
        stop();
        enabled = false;
    }

    /**
     * Enables the drive system. This sets a flag so that <code>update()</code>
     * will run normally.
     * @see disable()
     */
    public void enable()
    {
        enabled = true;
    }

    /**
     * Tells the drive system to come to a complete stop. Simply calls 
     * <code>update(0,0,0)</code>
     * @see update(double, double, double)
     */
    public void stop()
    {
        update(0,0,0);
    }
}
//FIRST FRC team 691 2012 competition code
