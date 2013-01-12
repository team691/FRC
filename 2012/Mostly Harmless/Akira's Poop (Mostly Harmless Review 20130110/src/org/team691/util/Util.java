
package org.team691.util;

/**
 * Contains various robot-specific utility functions that don't fit into Mathf.
 * @author Gerard Boberg
 */
public class Util
{
    public static final double JOYSTICK_DEADZONE = 0.08; // 7.5%

    /**
     * if the joystick value is within the "deadzone", an error region deemed as zero, the return is zero
     * (to allow for human error or small variations in the joystick position to be interpreted as zero)
     * @param value
     * @return the interpreted value of the joystick
     */
    public static double joystickDeadZone(final double value)
    {
        if ( Math.abs( value ) < JOYSTICK_DEADZONE )
            return 0;
        return value;
    }
    public static double squareAxis( final double value )
    {
        if( value >= 0)
            return value * value;
        else
            return -1 * value * value;
    }
    public static double cubeAxis( final double value )
    {
        return value * value * value;
    }
    
    
    /**
     * Runs the inputed joystick value through the equation 1/2 * (x^2) and 
     * preserving the sign. This drasticly reduces the rate that the joysticks
     * ramp up. Implemented because the kit bot tests were too responsive.
     */
    public static double oneHalfXSquared( double x )
    {
        if( x > 0 )
            return (x*x) /  2.0;
        else//if( x < 0 )
            return (x*x) / -2.0;
    }

    public static final double DEADBAND_VALUE = 0.082;
    public static final double VICTOR_FIT_C1  = -1.56847;
    public static final double VICTOR_FIT_C2  = -5.46889;
    public static final double VICTOR_FIT_E1  = 0.437239;
    public static final double VICTOR_FIT_A1  = ( -( 125.0 * VICTOR_FIT_E1
                                                    + 125.0 * VICTOR_FIT_C1
                                                    - 116.00 / 125.0 ) );
    public static final double VICTOR_FIT_E2 = 2.24214;
    public static final double VICTOR_FIT_G2 = -0.042375;
    public static final double VICTOR_FIT_A2 = ( -125.0
                                                 * ( VICTOR_FIT_C2 + VICTOR_FIT_E1
                                                     + VICTOR_FIT_G2 ) - 116.00 ) / 125.0;

    /**
     * linearly interpolates the power value given to a victor motor
     * (makes it get up to speed smoothly)
     * @param desiredPower
     * @return the lerp'd power to be sent to the robot
     */
    public static double victorLinearize(double desiredPower)
    {
        // deadzone
        if ( desiredPower > DEADBAND_VALUE )
            desiredPower -= DEADBAND_VALUE;
        else if ( desiredPower < -DEADBAND_VALUE )
            desiredPower += DEADBAND_VALUE;
        else
            return 0.0;

        // move -1.0-1.0 into the range of (-1+deadband)-(1-deadband)s
        desiredPower = desiredPower / ( 1.0 - DEADBAND_VALUE );

        // x^2   -->   x^7
        double desiredPower3 = desiredPower * desiredPower * desiredPower;
        double desiredPower5 = desiredPower3 * desiredPower * desiredPower;
        double desiredPower7 = desiredPower5 * desiredPower * desiredPower;

        // Calculate 5th order
        double answerOrder5 = ( VICTOR_FIT_A1 * desiredPower5
                                + VICTOR_FIT_C1 * desiredPower3
                                + VICTOR_FIT_E1 * desiredPower );

        // calculate 7th order
        double answerOrder7 = ( VICTOR_FIT_A2 * desiredPower7
                                + VICTOR_FIT_C2 * desiredPower5
                                + VICTOR_FIT_E2 * desiredPower3
                                + VICTOR_FIT_G2 * desiredPower );

        // average 5th and 6th together
        double answer = 0.85 * 0.5 * ( answerOrder7 + answerOrder5 )
                        + 0.15 * desiredPower * ( 1 - DEADBAND_VALUE );

        if ( answer > 0.001 )
            answer += DEADBAND_VALUE;
        else if ( answer < -0.001 )
            answer -= DEADBAND_VALUE;

        return answer;
    }
}

//FIRST FRC team 691 2012 competition code
