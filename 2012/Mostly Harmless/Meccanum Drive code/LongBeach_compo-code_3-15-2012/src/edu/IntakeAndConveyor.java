package edu;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * Represents the internal ball manipulation systems on the robot that feed the 
 * balls into the shooter. 
 * @author Ryan Shepard
 */
public class IntakeAndConveyor
{
    public static final Value kForward = Relay.Value.kForward;
    public static final Value kReverse = Relay.Value.kReverse;
    public static final Value kOff     = Relay.Value.kOff;
    public int numBalls                = 0;
    public boolean wasCollected        = false;
    public boolean wasShot             = false;
    protected boolean override         = false;
    protected SpeedController intakeRelay        = Objects.intakeAndConvayorSpike;

    // public DigitalInput intakeLimitTwo = Objects.intakeLimit2;
    // public DigitalInput intakeLimitOne = Objects.intakeLimit1;

    /**
     * turns on the intake and conveyor spike.
     */
    public void turnOn()
    {
        if( !override )
         set( 1 );
    }
    
    /**
     * turns off the intake and conveyor spike.
     */
    public void turnOff()
    {
        if( !override )
            set( 0 );
    }

    /**
     * sets the intake and conveyor spike to run backwards.
     */
    public void turnReverse()
    {
        if( !override )
            set( -1 );
    }
    
    /**
     * sets the speed controller.
     */
    public void set( double powerIn )
    {
        intakeRelay.set( -powerIn );
    }
    
    public void turnOff( boolean over )
    {
        if( over )
        {
            override = true;
            set( 0 );
        }
        else
            override = false;
    }
    
    public void turnOn( boolean over )
    {
        if( over )
        {
            override = true;
            set(1);
        }
        else
            override = false;
    }
    
    public void turnReverse( boolean over )
    {
        if( over )
        {
            override = true;
            set( -1 );
        }
        else
            override = false;
    }

    // automatically intakes balls if and only if we do not have 3 balls already

    /*
     * public void autoCollect()
     * {
     *   // if the intake limit switch is pressed and has not yet been toggled,
     *   // increase the count of how many balls have been collected
     *   // and toggle the boolean representing that the switch was just thrown
     *   // (the toggling is to prevent one ball over the limit switch from
     *   // being counted as multiple if it stays on the switch)
     *   if ( ( intakeLimitOne.get() == true ) )
     *   {
     *       if ( !wasCollected )
     *       {
     *           numBalls++;
     *
     *           wasCollected = true;
     *       }
     *   }
     *   else
     *       wasCollected = false;
     *
     *   // use the sae toggle system as above to count the number of balls being shot
     *   if ( ( intakeLimitTwo.get() == true ) && ( wasShot == false ) )
     *   {
     *       if ( !wasShot )
     *       {
     *           numBalls--;
     *
     *           wasShot = true;
     *       }
     *   }
     *   else
     *       wasShot = false;
     *
     *   // if we have less than three balls, turn intake on, else turn
     *   // it backwards to repel oncoming balls
     *   if ( numBalls < 3 )
     *       intakeRelay.set( kForward );
     *   else
     *       intakeRelay.set( kReverse );
     * }
     *
     */
}

//FIRST FRC team 691 2012 competition code