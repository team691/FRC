
package org.team691.util;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.SpeedController;

/**
 *
 * @author Gerard Boberg
 */
public class RPMMotor
{
    public static final double DEFAULT_ENCODER_CLICKS_TO_REVOLUTION = 0.166667; // 0.002778; // ~1/360
    public static final double DEFAULT_ENCODER_SPEED_TO_RPM = 0.1;
    public static final double DEFAULT_KP                   = 90.0;      // 100
    public static final double DEFAULT_KI                   = 0;         // 1
    public static final double DEFAULT_KD                   = 2500;         // 30
    public static final double DEFAULT_SCALE                = 0.0000001; // 0.000001
    public static final double ERROR_PID                    = 0.025; // less than 2.5% change
    public static final double ERROR_RPM                    = 50; // within 100 rpm of target ~5%
    public static final double UPDATES_PER_SECOND           = 20;
    public static final double UPDATE_INTERVAL              = 1.0 / UPDATES_PER_SECOND;
    protected double encoderSpeed                           = 0;
    protected int lastCount                                 = 0;
    protected int deltaCount                                = 0;
    protected double lastPID                                = 0.0;
    protected double objectiveRPM                           = 0.0;
    public PID pid = new PID( DEFAULT_SCALE, DEFAULT_KP, DEFAULT_KI, DEFAULT_KD );
    protected double lastTime                               = Time.time();
    public double encoderToRevolution = DEFAULT_ENCODER_CLICKS_TO_REVOLUTION;
    public double encoderToRPM                              = DEFAULT_ENCODER_SPEED_TO_RPM;
    protected double deltaTime                              = UPDATE_INTERVAL;
    protected double rpm                                    = 0;
    protected boolean disabled                              = false;
    protected CounterBase enc;
    protected SpeedController mtr;

    public RPMMotor(SpeedController motor, CounterBase enconder)
    {
        mtr = motor;
        enc = enconder;
    }

    /**
     * Updates the motors based on PID if there has been no change.
     * if the robot is disabled or updateSpeedVars() has been called, get out
     * else calculate the next PID values to be sent to the motors and send them
     */
    public void update()
    {
        if ( !updateSpeedVars() || isDisabled() )
            return;

        if ( objectiveRPM > rpm )
            lastPID = pid.calc( objectiveRPM, rpm * 1.0 );
        else
            lastPID = pid.calc( objectiveRPM, rpm * 1.025 );

        adjustMotor( lastPID );
    }

    /**
     * Update everything with new values if enough time has passed.
     * if enough time has passed, update the change in time, the encoder count, 
     * the encoder speed, the rpm, and the last time and count values
     * @return that the values have been updated or not
     */
    public boolean updateSpeedVars()
    {
        if ( Time.time() - UPDATE_INTERVAL < lastTime )
            return false;

        deltaTime    = Time.time() - lastTime;
        deltaCount   = enc.get() - lastCount;
        encoderSpeed = ( encoderToRevolution * ( deltaCount ) ) / deltaTime;
        rpm          = encoderSpeed; // * 60;
        lastTime     = Time.time();
        lastCount    = enc.get();
        return true;
    }

    /**
     * disables the drive system, then sets the motor value to a defined value.
     * @param value 
     */
    public void overWritePID(double value)
    {
        disable();
        mtr.set( value );
    }

    /**
     * increases the motor's power value by the parameter value.
     * @param value 
     */
    public void adjustMotor(double value)
    {
        mtr.set( mtr.get() + value );
    }

    /**
     * @return our objective RPM value, presumably after PID calculations.
     */
    public double getTarget()
    {
        return objectiveRPM;
    }

    /**
     * sets the objectiveRPM value to the parameter value (presumably calculated by PID).
     * @param value 
     */
    public void setTarget(double value)
    {
        objectiveRPM = value;
    }

    /**
     * @return the rpm
     */
    public double getCurrent()
    {
        return rpm;
    }

    /**
     * @return the current PID object (for modifying the PID constructor values during runtime).
     */
    public PID getPID()
    {
        return pid;
    }

    /**
     * changes the current PID object's reference to another PID object given in the parameters.
     * @param newPID 
     */
    public void setPID(PID newPID)
    {
        pid = newPID;
    }

    /**
     * @return the last PID object's reference.
     */
    public double getLastPID()
    {
        return lastPID;
    }

    /**
     * disables the drive system (sets all motors to zero power and sets the disabled boolean to true).
     */
    public void disable()
    {
        disabled = true;

        mtr.set( 0 );
    }

    /**
     * enables the drive system by setting the disabled boolean to false.
     */
    public void enable()
    {
        disabled = false;
    }

    /**
     * @return if the drive system is disabled.
     */
    public boolean isDisabled()
    {
        return disabled;
    }

    /**
     * @return if the current RPM is close to the objectiveRPM.
     */
    public boolean isCloseToTarget()
    {
        return Mathf.approximately( lastPID, 0, ERROR_PID )
               && ( Mathf.approximately( rpm, objectiveRPM, ERROR_RPM )
                  || rpm > objectiveRPM ) ;
    }

    /**
     * @return deltaCount.
     */
    public double getLastDCount()
    {
        return deltaCount;
    }

    /**
     * @return deltaTime.
     */
    public double getLastDTime()
    {
        return deltaTime;
    }
    
    public void resetI()
    {
        pid.resetI();
    }
}

//FIRST FRC team 691 2012 competition code
