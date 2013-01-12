
/** FRC Team 691 competition code **/

package org.team691.drive;

import edu.wpi.first.wpilibj.SpeedController;

import org.team691.util.Mathf;

/**
 *
 * @author Cheese
 */
public class MeccanumDriveNoEncoders implements Drive
{
    // constants
    public static final int FR = 0;
    public static final int FL = 1;
    public static final int BL = 2;
    public static final int BR = 3;

    // end constants

    protected SpeedController[] mtrs;
    protected double[] mtrMags = new double[4];

    /**
     *
     * @param in An array of the motors to use. Should be of length 4, where
     * the array positions are:
     * <p>0: Front Right</p>
     * <p>1: Front Left</p>
     * <p>2: Back  Left</p>
     * <p>3: Back  Right</p>
     * Think quadrants of coordinate grid:
     * <pre>
     *  FL  |  FR
     *   2  |   1
     *   --------
     *  BL  |  BR
     *   3  |   4
     * </pre>
     */
    public MeccanumDriveNoEncoders(SpeedController[] in)
    {
        this.mtrs = in;
    }

    /**
     * Calculates and moves the wheels of a meccanum drive.
     */
    public void update(double F, double R, double C)
    {
        double max = -1;

        // for each wheel, calc target value and track the greatest value
        for(int count = 0; count < mtrs.length; count++)
        {
            switch(count)
            {
                // -1 is because electronics wired backwards
                case FR :
                    mtrMags[count] = -1 * ( F - R - C );
                    break;
                case FL :
                    mtrMags[count] = ( F + R + C );
                    break;
                case BL :
                    mtrMags[count] = ( F - R + C );
                    break;
                case BR :
                    mtrMags[count] = -1 * ( F + R - C );
                    break;
            }

            if ( Mathf.abs( mtrMags[count] ) > max )
                max = Mathf.abs( mtrMags[count] );
        }

        // reduce all values inside the legal range and set the motors
        for(int count = 0; count < mtrMags.length; count++)
        {
            if ( max > 1 )
                mtrMags[count] /= max;

            if ( mtrs[count] != null )
                mtrs[count].set( mtrMags[count] );

            // lastMtrMags[count] = mtrMags[count];
        }
    } // end update

    /**
     * Tells the drive to stop moving.
     */
    public void stop()
    {
        update( 0, 0, 0 );
    }

    public void lockDown()
    {
        stop();
    }

    public void disable() {}

    public void enable() {}

}


//FIRST FRC team 691 2012 competition code
