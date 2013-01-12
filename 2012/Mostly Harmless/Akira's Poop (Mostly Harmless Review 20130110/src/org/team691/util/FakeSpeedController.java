package org.team691.util;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * To satisfy various constructors so we can test the sensors without damaging
 * anything, this class tricks others into thinking that they're working with
 * an actual motor.
 * @author Gerard Boberg
 */
public class FakeSpeedController implements SpeedController
{
    protected double value = 0.0;

    public void disable() {}

    public void set(double in)
    {
        value = in;
    }

    public void pidWrite(double in)
    {
        value = in;
    }

    public void set(double in, byte fake)
    {
        value = in;
    }

    public double get()
    {
        return value;
    }
}

//FIRST FRC team 691 2012 competition code
