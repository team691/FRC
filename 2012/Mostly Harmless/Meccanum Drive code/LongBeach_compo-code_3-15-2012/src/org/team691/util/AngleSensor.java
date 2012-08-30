package org.team691.util;

import edu.wpi.first.wpilibj.AnalogModule;

/**
 * Reads an analog absolute encoder and turns it into an angle object
 * that the rest of the program can use.
 * @author Gerard Boberg
 */
public class AngleSensor
{
    public static final double VOLT_TO_ANGLE_CONVERSION = 360.0 / 5.05;
    protected static boolean hasBeenSettup              = false;
    protected Angle offset                              = Angle.zero();
    protected Angle last                                = Angle.zero();
    protected volatile double volts                     = 0.0;
    protected int channel;
    public AnalogModule mod;
    protected int slot;

    public AngleSensor(int slot, int channel)
    {
        this.slot    = slot;
        this.channel = channel;
        mod          = AnalogModule.getInstance( slot );

        /*     //This section was used with digital encoders, averaging together
               //the voltage readings over millisecond intervals. unused after
               //changing to analog encoders.
        mod.setAverageBits( channel, 6 );    // 6
        System.out.println( "Average bits: " + mod.getAverageBits( channel ) );
        mod.setOversampleBits( channel, 0 ); // 1
        System.out.println( "Over sampling: " + mod.getOversampleBits( channel ) );
        mod.setSampleRate( 974.658 * 256 );
        // 5 5 5 5 5 0 0 0 0 0 0 0 0 0 0   <--- (x2^AVG x2^OVER) -> average -> #
        // | | | | | | | | | | | | | | |
        // |--------_____________________|  x 974.658 / seconds
        /**/
    }

    public AngleSensor(int slot, int channel, Angle offset)
    {
        this( slot, channel );

        this.offset = offset;
    }

    public Angle getOffset()
    {
        return offset;
    }

    /**
     * Applies an offset value to the encoder readings. The real output of 
     * this.get() is 
     * raw angle - offset
     * @param offset the new offset to use. This number should be the same 
     * across all robot boots.
     */
    public void setOffset(Angle offset)
    {
        this.offset = offset;
    }

    /**
     * Sets the offset to the current direction the angle sensor is facing. This
     * has the effect of setting this position as the new 0 position.
     */
    public void reset()
    {
        volts = 0.0;
        int times = 5;

        synchronized (this)
        {
            for(int count = times; count > 0; count--)
            {
                volts += getRaw();

                try
                {
                    this.wait( 50 );
                }
                catch (InterruptedException ex) {}
            }
        }

        volts /= times;

        System.out.println( "average voltage over " + times + " samples: " + volts );
        System.out.println( "Angle = " + offset.toString() );

        // volts *= VOLT_TO_ANGLE_CONVERSION;

        setOffset( new Angle( volts * VOLT_TO_ANGLE_CONVERSION ) );
    }

    /**
     * Read the absolute encoder's current angle reading
     * @return The Angle object represent the current angle the sensor is feeding
     * back.
     */
    public Angle get()
    {
        Angle out = new Angle( getRaw() * VOLT_TO_ANGLE_CONVERSION );

        out  = out.subtract( offset );

        last = out;
        return out;
    }

    /**
     * Reads the raw voltage the sensor is sending to the analog module.
     * @return A double representing the voltage, values from 0.0-5.0
     */
    public double getRaw()
    {
        return mod.getVoltage( channel );

        /* mod.getAverageVoltage(channel); */
    }
}
//FIRST FRC team 691 2012 competition code
