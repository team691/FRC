package org.team691.util;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * This class takes in angleSensor SpeedController (motor) and an angle sensor (absolute
 * encoder) and uses them to precisely control angleSensor wheel to face angleSensor specific angle.
 * @author Gerard Boberg
 */
public class AngleMotor
{
    public static final double DEFAULT_SCALE       = 0.000035; // 0.000075
    public static final double DEFAULT_KP          = 70;       // 125
    public static final double DEFAULT_KI          = 5;        // 5
    public static final double DEFAULT_KD          = 35;       // 100
    public static final double I_CHECKS_PER_SECOND = 3;        // 3
    public static final double MAX_POWER           = 1;        // 0.23 //0.75
    public static final double MIN_POWER           = -1;       // -0.23 //0.75

    public static final double SIGNIFICANT        = 17;                        // degrees
    public static final double UPDATES_PER_SECOND = 20;                        // 17
    public static final double I_UPDATE_INTERVAL  = 1.0 / I_CHECKS_PER_SECOND; // 0.33333
    public static final double UPDATE_INTERVAL    = 1.0 / UPDATES_PER_SECOND;
    protected double lastDist                     = 0.0;
    protected double lastPID                      = 0.0;
    protected PID pid = new PID( DEFAULT_SCALE, DEFAULT_KP, DEFAULT_KI, DEFAULT_KD );
    protected Angle target                        = Angle.zero();
    protected double nextUpdate                   = Time.time() + UPDATE_INTERVAL;
    protected double nextICheck                   = Time.time() + I_CHECKS_PER_SECOND;
    protected double lastTime                     = Time.time();
    protected Angle lastITarget                   = Angle.zero();
    protected Angle finalTarget                   = Angle.zero();
    protected boolean enabled                     = true;
    protected double dTime                        = UPDATE_INTERVAL;
    protected Angle current                       = Angle.zero();
    protected AngleSensor angleSensor;
    protected SpeedController motor;

    public AngleMotor(SpeedController mtr, AngleSensor a)
    {
        enable();

        this.motor = mtr;
        this.angleSensor   = a;
    }

    /**
     * Gives command flow to the motors so that they can move the motors to their
     * desired positions.
     */
    public void update()
    {
        if ( !enabled )
            return;
        if ( Time.time() < nextUpdate )
            return;

        // Smooth out the change from the current target to the new target.
        // Prevents the motors from spazzing out, and resists small changes in
        // the input.
        dTime  = Time.time() - lastTime;
        target = finalTarget; // target.lerp( finalTarget, dTime * SCALE );

        // Find where we are, figure out how far away we are, then do the PID
        // calculation.
        current  = angleSensor.get();
        lastDist = current.distanceTo( target );
        lastPID  = pid.calc( lastDist );

        // Swerve wheels spin really fast. Clamping motor power helps prevent
        // spaz outs.
        lastPID = clampMotorIn( lastPID );

        motor.set( Util.victorLinearize( lastPID ) );

        // allows us to use I to overcome mechanical resistance, but not explode
        if ( Time.time() > nextICheck )
        {
            if ( significantChange( lastITarget, finalTarget ) )
                pid.resetErrorI();

            lastITarget = finalTarget;
            nextICheck  = Time.time() + I_UPDATE_INTERVAL;
        }

        // update time-related variables.
        nextUpdate = Time.time() + UPDATE_INTERVAL;
        lastTime   = Time.time();
    }
    
    /**
     * Tells the motor to go to the specified target.
     * @param target The direction the user wants the wheel to face.
     */
    public void setTargetAngle(Angle target)
    {
        this.finalTarget = target;
    }

    

    /**
     * Disables the automatic movement systems and directly sets the SpeedController.
     * Used for debug.
     * @param input the power, in the range [-1, 1], to set the motor to.
     */
    public void overwritePID(double input)
    {
        disable();
        motor.set( Util.victorLinearize( input ) );
    }

    /**
     * Enables PID control.
     */
    public void enable()
    {
        enabled = true;
    }

    /**
     * Disables PID control.
     */
    public void disable()
    {
        enabled = false;

        motor.set( 0.0 );
    }

    /**
     * Retrieves the current enabled setting.
     * @return true if enabled, false otherwise.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Retrieves the current disabled setting. Equivelant to !isEnabled()
     * @return true if disabled, false otherwise.
     */
    public boolean isDisabled()
    {
        return !enabled;
    }

    /**
     * Resets the PID controller, and the offset angle to the current direction.
     * Used for debug only.
     */
    public void reset()
    {
        motor.set( 0 );

        finalTarget = Angle.zero();
        target      = Angle.zero();

        pid.resetErrorI();
        angleSensor.reset();
    }

    /**
     * Returns the dot product of the desired force direction and the wheel's 
     * current force direction. Used to coordinate drive motors with the angle
     * motors.
     * @return The cosine of the distance from the current angle to the 
     * target angle.
     */
    public double getCosOfError()
    {
        return Mathf.cos( current.distanceTo( target ) * Mathf.DEG2RAD );
    }

    /**
     * Retrieves the current target angle.
     * @return the target angle
     */
    public Angle getTargetAngle()
    {
        return finalTarget;
    }

    /**
     * Replaces the current PID object with a new one.
     * Allows changing PID values during runtime.
     * @param pid The new PID object to use.
     */
    public void setPID(PID pid)
    {
        this.pid = pid;
    }

    /**
     * Retrieves the PID object being used to make calculations. Useful for debug.
     * @return The PID object currently being used. Changes to this object will
     * directly affect the calculations made to move the wheel.
     */
    public PID getPID()
    {
        return pid;
    }

    /**
     * Returns the most recent PID output.
     * @return the most recent PID output.
     */
    public double getLastPID()
    {
        return lastPID;
    }

    /**
     * returns the most recent distance to the target angle from the current.
     * @return the most recent distance setting.
     */
    public double getLastDistance()
    {
        return lastDist;
    }
    
    /**
     * Clamps the maximum and minimum power that can be sent to the motors so
     * that they don't overshoot too much.
     * @param value The value to clamp. This should be the most recent value
     * returned by PID.
     * @return The value clamped between MAX_POWER and MIN_POWER.
     */
    public static double clampMotorIn(double value)
    {
        if ( value > MAX_POWER )
            return MAX_POWER;
        if ( value < MIN_POWER )
            return MIN_POWER;
        return value;
    }

    /**
     * returns true if there is a significant change between the two input angles.
     * @param a the first angle to compare
     * @param b the second angle to compare
     * @return true if the distance between a and b is greater than SIGNIFICANT.
     */
    protected static boolean significantChange(Angle a, Angle b)
    {
        if ( Mathf.abs( a.distanceTo( b ) ) > SIGNIFICANT )
            return true;
        return false;
    }
}

//FIRST FRC team 691 2012 competition code
