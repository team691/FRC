/*
 * TEAM     : 691 Hart Burn
 * AUTHOR(S): Akira "Ninja" H., Casey G., Gerard "B-Bo" B.,
 *            Bryan "Brandon" S. (Don't call him Brandon)
 * SEASON   : 2011
 * CONTACT  : mheid2011@gmail.com
 */

package org.team691.drive;

import edu.wpi.first.wpilibj.Encoder;
import org.team691.util.Mathf;
import org.team691.util.Time;

/**
 * Controls the wheel movement for a standard, 4-wheeled, Meccanum drive setup.
 * @author Akira, Gerard
 */
public class MeccanumDrive implements Drive
{
    /**
     * Array of the wheels for the Meccanum Drive.
     * <li>[0] = Front Right Motor
     * <li>[1] = Front Left Motor
     * <li>[2] = Back Left Motor
     * <li>[3] = Back Right Motor
     */
    protected MeccanumWheel[] wheels               = new MeccanumWheel[4];
    protected double[] magnitudes                  = new double[wheels.length];
    protected double[] lastMagnitudes              = new double[wheels.length];

    protected boolean shouldSmoothVelocityOverTime = false;
    protected double smoothingFactor               = 8.0;
    protected double turnOffset                    = 0;

    
    public MeccanumDrive(){}
    
    public MeccanumDrive( double turnFromStrafeOffset )
    {
        this();
        this.turnOffset = turnFromStrafeOffset;
    }
    
    /**
     * Constructor for the MeccanumDrive object.
     * @param velMtrsIn A length 4 array of Velocity motors where [0] is the FR
     * motor, [1] is the FL motor, [2] is the BL motor, and [3] is the BR motor.
     */
    public MeccanumDrive(PIDControlledVelocityMotor[] velMtrsIn)
    {
        for(int count = 0; count < wheels.length; count++)
        {
            wheels[count] = new MeccanumWheel( velMtrsIn[count], count + 1 );
        }
    }

    /**
     * Constructor for the MeccanumDrive object. This method is only used for
     * backwards compatability.
     * 
     * @param cRIOSlot      The cRIO slot the wheels are connected to.
     * @param channelNum The array of channel numbers for the speed controllers
     * ([0] is Front Right Wheel, [1] is Front Left Wheel, [2] is Back Left
     * Wheel, [3] is Back Right Wheel).
     * @param FREnc The Front Right Wheel encoder object.
     * @param FLEnc The Front Left Wheel encoder object.
     * @param BREnc The Back Right Wheel encoder object.
     * @param BLEnc The Back Left Wheel encoder object.
     */
    public MeccanumDrive(int cRIOSlot, int channelNum[], Encoder FREnc, Encoder FLEnc, Encoder BREnc,
                         Encoder BLEnc){
        wheels[0] = new MeccanumWheel(cRIOSlot, channelNum[0], FREnc, MeccanumWheel.FR_WHEEL);
        wheels[1] = new MeccanumWheel(cRIOSlot, channelNum[1], FLEnc, MeccanumWheel.FL_WHEEL);
        wheels[2] = new MeccanumWheel(cRIOSlot, channelNum[2], BLEnc, MeccanumWheel.BL_WHEEL);
        wheels[3] = new MeccanumWheel(cRIOSlot, channelNum[3], BREnc, MeccanumWheel.BR_WHEEL);
    }

    /**
     * Updates all four wheel speeds. The left and right joysticks are used like
     * the Halo driving style (left joystick controls forward and strafing, right
     * joystick controls turning).
     * @param forward   Value from the left joystick's y-axis.
     * @param strafe    Value from the left joystick's x-axis.
     * @param turn      Value from the right joystick's x-axis.
     */
    public void update(double forward, double strafe, double turn)
    {
        // uses fewer processes if  all values are 0
        if ( (forward == 0) && (strafe == 0) && (turn == 0) )
        {
            for(int count = 0; count < wheels.length; count++)
            {
                wheels[count].setMotor( 0 );

                lastMagnitudes[count] = 0;
            }
        }
        else // normal operation
        {
            double max = -1;

            for(int count = 0; count < wheels.length; count++)
            {
                // update() calculates the magnitude that this wheel should move at,
                // but does not actually set the motor - gives us a chance to
                // syncronize the wheel's speeds
                magnitudes[count] = wheels[count].update( forward, strafe, turn );

                double temp = Math.abs( magnitudes[count] );

                if ( temp > max ){
                    max = temp;
                }
            }

            // now we actually tell the motors how to move
            for(int count = 0; count < wheels.length; count++)
            {
                // We can't set the motor's power to more than 100%, scale everything
                // down equaly
                if ( max > 1 ){
                    magnitudes[count] /= max;
                }

                // Allows for less violent changes in motor power
                if ( shouldSmoothVelocityOverTime )
                {
                    magnitudes[count] = Mathf.lerp( lastMagnitudes[count],
                                                    magnitudes[count],
                                                    Time.deltaTime() * smoothingFactor );
                }

                // now we move the motors
                wheels[count].setMotor( magnitudes[count] );

                lastMagnitudes[count] = magnitudes[count];
            }
        }
    }

    /**
     * Stops the drive system by turning off all the motors.
     */
    public void stop()
    {
        update( 0, 0, 0 );
    }

    public void lockDown()
    {
        stop();
    }

    public void enable()
    {
        for( int count = 0; count < wheels.length; count++){
            wheels[count].enable();
        }
    }

    public void disable() 
    {
        for( int count = 0; count < wheels.length; count++){
            wheels[count].disable();
        }
    }

    /**
     * Provides a nicely formatted way to read off all the important numbers in
     * the drive system.
     * @return A human readable string containing the current motor setting and
     * encoder velocity readings.
     */
    public String toString()
    {
        return ( "\n\nFRMag: " + wheels[0].getMagnitude() + "\t\t\tFREncVel: "
                 + wheels[0].toString() + "\nFLMag: " + wheels[1].getMagnitude()
                 + "\t\t\tFLEncVel: " + wheels[1].toString() + "\nBLMag: "
                 + wheels[2].getMagnitude() + "\t\t\tBLEncVel: " + wheels[2].toString()
                 + "\nBRMag: " + wheels[3].getMagnitude() + "\t\t\tBREncVel: "
                 + wheels[3].toString() );
    }
}
//FIRST FRC team 691 2012 competition code
