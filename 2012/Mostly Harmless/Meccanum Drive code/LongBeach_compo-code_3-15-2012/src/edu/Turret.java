
package edu;

import edu.io.AutoTarget;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SpeedController;

import org.team691.util.Mathf;
import org.team691.util.RPMMotor;
import org.team691.util.Time;
import org.team691.util.Util;

/**
 * Unifies and controls the turret aiming and firing systems.
 * @author Michael Erbach and Gerard Boberg
 */
public class Turret
{
    public static final double MAX_ROTATE           = 0.4;
    public static final double MIN_ROTATE           = -0.4;
    public static final double SMOOTH_TO_ZERO_SCALE = 50;

    // imports kinect smart targeting from Objects
    protected AutoTarget autoTarget = Objects.autoTarget;

    // the uptake system, NOT the intake or feeder
    protected IntakeAndConveyor conveyor = Objects.intakeSystem;

    // what turns, in combination with the conveyor, to fire - the feeder roller
    protected Relay feeder = Objects.feederSpike;

    // protected DigitalInput limitLeft;  
    // limit switch so that the turret can only turn so far
    // protected DigitalInput limitRight; 
    // limit switch so that the turret can only turn so far
    protected double objectiveAngle = 0.0; 
    // this double represents the POWER being sent to the motor, NOT its speed
    protected double objectiveSpeed = 0.0; 
    // this double represents the POWER being sent to the launch roller, NOT an angle measurement
    private double offsetSpeed = 0.0;
    protected CounterBase shooterEncoder = Objects.shooterRPMEncoder; 
    // measures the launch roller RPM

    // (the angle measurement and consequent PID calculations happen in the wrapper
    //& calculations classes)
    protected RPMMotor turretLaunchRoller = Objects.turretShooterMotor; 
    // the turret launch roller is powered by two jaguar-based motors (DOUBLEspeedcontroller)
    protected SpeedController turretRotateMotor = Objects.turretRotateVictor; 
    // the angle-controlling motor

    // called during constructor
    {
        turretLaunchRoller.disable();
    }
    
    /**
     * updates the turret's angle (now based on PID values
     * updates the turret's launch roller for PID values
     * sets the turret's goal angle value so that it will turn at a smooth rate
     */
    public void update()
    {
        // updates the turret angle, launch roller PID-based speed, sets new
        // power levels according to PID
        // the reason the update comes first is in RobotMain the kinect-based
        // angle setting can be manually overridden
        angleTurret();
        turretLaunchRoller.update();

        // smoothly sends the turret left-right power to 0, so that it doesn't
        // overshoot.
        objectiveAngle = Mathf.lerp( objectiveAngle, 0,
                                     Time.deltaTime() * SMOOTH_TO_ZERO_SCALE );

        // setSpeedObjective();
        // setAngleObjective();
    }

    /**
     * sets the motor for angling the turret based on the PID-thrown objective value
     */
    public void angleTurret()
    {
        // if the limit switch is thrown and the POWER (see variable defs)
        // sent to the motor is < 0 (is moving left), turn off the motor

        /*
         * if ( limitLeft.get() && ( objectiveAngle < 0 ) )
         *   turretRotateMotor.set( 0 );
         * else if ( limitRight.get() && ( objectiveAngle > 0 ) )
         *   turretRotateMotor.set( 0 );
         * else
         */

        turretRotateMotor.set(
                Util.victorLinearize( clampTurretRotate( -objectiveAngle ) ) );
    }

    /**
     * sets the launch roller's speed to a predetermined speed regardless of the PID-thrown value
     * @param value 
     */
    public void setLaunchRoller(double value)
    {
        turretLaunchRoller.overWritePID( value );
    }

    /**
     * (supposedly) sets the launch roller to the highest speed (1.0 to the speed controller 
     * is max power)
     */
    public void launchRollerMaxPower()
    {
        setLaunchRoller( 1.0 );
    }

    /**
     * turns on the launch roller motor
     */
    public void spinUpShooter()
    {
        turretLaunchRoller.enable();
    }

    /**
     * turns off the launch roller's motor
     */
    public void spinDownShooter()
    {
        turretLaunchRoller.disable();
    }

    /**
     * turns off the conveyor relay
     * turns off the feeder relay
     */
    public void stopShooting()
    {
        conveyorOff();
        feederOff();
    }

    /**
     * turns on both conveyor and feeder
     */
    public void shoot()
    {
        conveyorUp();
        feederOn();
    }

    /**
     * shoots after making sure the turret is pointing at the target and the launch roller is up to 
     * speed
     */
    public void shootWithChecks()
    {
        if ( turretCheckToFire() )
        {
            conveyorUp();
            feederOn();
        }
        else
        {
            conveyorOff();
            feederOff();
        }
    }

    /**
     * turns on the feeder
     */
    public void feederOn()
    {
        feeder.set( Relay.Value.kForward );
    }
    
    /**
     * turns on the feeder backwards
     */
    public void feederReverse()
    {
        feeder.set( Relay.Value.kReverse );
    }

    /**
     * turns off the feeder
     */
    public void feederOff()
    {
        feeder.set( Relay.Value.kOff );
    }

    /**
     * turns on the conveyor
     */
    public void conveyorUp()
    {
        conveyor.turnOn();
    }

    /**
     * turns on the conveyor to operate in the reverse direction
     */
    public void conveyorDown()
    {
        conveyor.turnOff();
    }

    /**
     * Turns off the conveyor
     */
    public void conveyorOff()
    {
        conveyor.turnOff();
    }

    /**
     * checks to ensure that we are locked onto at least one target and
     * the launch roller is up to PID speed
     */
    public boolean turretCheckToFire()
    {
        if ( autoTarget.isLockedOn() )
            if ( true /* autoTarget.isReadyToFire() */ )
                if ( Math.abs( objectiveSpeed ) > RobotMain.MIN_RPM )
                    if ( turretLaunchRoller.isCloseToTarget() )
                        return true;

        return false;
    }

    /**
     * Retrieves the shooter's RPM target.
     * @return The RPMMotor's target RPM
     */
    public double getShooterTargetRPM()
    {
        return turretLaunchRoller.getTarget();
    }

    /**
     * Retrieves the RPM that the shooter is currently operating at.
     * @return The RPMMotor's most recent RPM calculation.
     */
    public double getShooterRPM()
    {
        return turretLaunchRoller.getCurrent();
    }

    /**
     * manually input a power to be thrown to the launch roller (using the joystick input)
     */
    public void setSpeedObjective(double objective)
    {
        objectiveSpeed = objective;

        turretLaunchRoller.setTarget( objectiveSpeed + offsetSpeed );
    }

    /**
     * returns variable offsetSpeed
     * @return offsetSpeed
     */
    public double getSpeedOffset()
    {
        return offsetSpeed;
    }

    /**
     * sets the speed offset to a parameter value
     * sets the adds the speed offset to the speed objective value for the launch roller
     * @param offset
     */
    public void setSpeedOffset(double offset)
    {
        offsetSpeed = offset;

        turretLaunchRoller.setTarget( objectiveSpeed + offsetSpeed );
    }

    /**
     * adds a parameter value to the existing speed offset value
     * @param deltaOffset 
     */
    public void adjustSpeedOffset(double deltaOffset)
    {
        setSpeedOffset( getSpeedOffset() + deltaOffset );
    }

    /**
     * Updates the turret's rotation motor setting. This method should be called
     * once every time we receive a packet from DeepThought. This value quickly
     * drops to 0 automaticly when there are no updates.
     * @param objective the power to send to the rotation motor
     */
    public void setAngleObjective(double objective)
    {
        objectiveAngle = objective;
    }
    
    /**
     * returns the angle objective value
     * @return objectiveAngle
     */
    public double getRotateSetting()
    {
        return objectiveAngle;
    }

    /**
     * sets turret's angle to the parameter value unless it is not within the bounds of turning
     * (limit switches)
     * @param value
     * @return rotation value
     */
    public static double clampTurretRotate(double value)
    {
        if ( value > MAX_ROTATE )
            return MAX_ROTATE;
        if ( value < MIN_ROTATE )
            return MIN_ROTATE;
        return value;
    }

    /**
     * returns the last change in the encoder's count value
     */
    public double getLastDCount()
    {
        return turretLaunchRoller.getLastDCount();
    }

    /**
     * returns the last value for the change in time
     */
    public double getLastDTime()
    {
        return turretLaunchRoller.getLastDTime();
    }
    
    /**
     * Resets the shooter RPMMotor's PID's integral value. This should be called
     * continuously when the shooter is not actively spinning.
     */
    public void resetShooterI()
    {
        turretLaunchRoller.resetI();
    }
}

//FIRST FRC team 691 2012 competition code
