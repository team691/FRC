package edu;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;

/**
 * Controls the Ramp Articulating arm on the robot. 
 * @author Ryan Shepard
 */
public class RampArticulate
{
    // public static final double SAFTEY_INTERVAL = 1.0;
    public static final Value FWD  = Relay.Value.kForward;
    public static final Value REV  = Relay.Value.kReverse;
    public static final Value OFF  = Relay.Value.kOff;
    protected Relay rightRelay     = Objects.rampSpikeR;
    protected Relay leftRelay      = Objects.rampSpikeL;
    protected boolean reverseLeft  = false;
    protected boolean reverseRight = false;

    public RampArticulate(boolean reverseL, boolean reverseR)
    {
        this.reverseLeft = reverseL;
        this.reverseRight = reverseR;
    }

    // protected double offTime                   = Time.time() + SAFTEY_INTERVAL;
    // protected Value currentDirection           = OFF;

    /**
     * Brings up the ramp articulator.
     */
    public void bringUp()
    {
        set( REV );
    }

    /**
     * Brings the ramp articulator down.
     */ 
    public void bringDown()
    {
        set( FWD );
    }

    /**
     * Turns the ramp articulator off.
     */ 
    public void turnOff()
    {
        set( OFF );
    }
    /**
     * Sets the ramp articulator relay(s), checking to see if the polarity on the
     * relay(s) need to be reversed.
     * for each joystick (R and L) respectively.
     * @param in The intended arm movement direction.
     */
    public void set(Value in)
    {
        //handle the right relay
        if ( reverseRight )
        {
            if ( in == FWD )
                rightRelay.set( REV );
            else if ( in == REV )
                rightRelay.set( FWD );
            else
                rightRelay.set( OFF );

        }
        else
            rightRelay.set( in );

        //handle the left relay
        if ( reverseLeft )
        {
            if ( in == FWD )
                leftRelay.set( REV );
            else if ( in == REV )
                leftRelay.set( FWD );
            else
                leftRelay.set( OFF );
        }
        else
            leftRelay.set( in );
    }
}

//FIRST FRC team 691 2012 competition code
