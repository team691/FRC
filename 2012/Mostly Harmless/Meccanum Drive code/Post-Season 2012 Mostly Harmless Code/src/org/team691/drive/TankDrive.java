package org.team691.drive;

import edu.Objects;
import edu.wpi.first.wpilibj.SpeedController;
import org.team691.drive.Drive;

/**
 * This class represents and moves a simple tank drive system. The class takes
 * two speed controllers, that mechanically control all the wheels on their
 * respective sides of the robot (use the <code>DoubleSpeedController</code> if
 * there are two motors on a given side).
 * 
 * Implements the drive interface so that drive systems can be swapped out inside
 * the program with minimal effort.
 * 
 * This simple, quick implementation uses the <code>Objects</code> to retrieve
 * the needed speed controllers. This allows swapping between our primary drive
 * code and our backup drive code with a single comment, but sacrifices versatility.
 * @author Gerard Boberg
 * @see Objects
 * @see Drive
 */
public class TankDrive implements Drive
{
    protected SpeedController rightDriveMotor = Objects.rDrive;
    protected SpeedController leftDriveMotor = Objects.lDrive;
    protected boolean enabled = true;

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
        if( !enabled )
            return;
        
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

    /**
     * Tells the drive system to move itself into a position optimized to prevent
     * the robot from being moved by other robots. Simply calls <code>stop()</code>
     * @see stop()
     */
    public void lockDown()
    {
        stop();
    }
}
//FIRST FRC team 691 2012 competition code
