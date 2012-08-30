package org.team691.util;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * Takes in two speed controllers and allows them to be used together as one
 * object.
 * @author Gerard Boberg
 */
public class DoubleSpeedController implements SpeedController
{
    protected boolean reverseA = false;
    protected boolean reverseB = false;
    protected double value     = 0.0;
    protected SpeedController a;
    protected SpeedController b;

    // constructors
    public DoubleSpeedController(SpeedController a, SpeedController b)
    {
        this.a = a;
        this.b = b;
    }

    public DoubleSpeedController(SpeedController a, SpeedController b,
                                 boolean reverseAPower, boolean reverseBPower)
    {
        this( a, b );

        this.reverseA = reverseAPower;
        this.reverseB = reverseBPower;
    }

    // standard set/get methods
    public void disable()
    {
        set( 0 );
    }

    public void set(double in)
    {
        value = in;

        a.set( ( reverseA ) ? -value : value );
        b.set( ( reverseB ) ? -value : value );
    }

    public void pidWrite(double in)
    {
        set( in );
    }

    public void set(double in, byte fake)
    {
        set( in );
    }

    public double get()
    {
        return value;
    }

    public boolean getReverseAPower()
    {
        return reverseA;
    }

    public boolean getReverseBPower()
    {
        return reverseB;
    }

    public void setReverseAPower(boolean value)
    {
        reverseA = value;
    }

    public void setReverseBPower(boolean value)
    {
        reverseB = value;
    }
}

//FIRST FRC team 691 2012 competition code
