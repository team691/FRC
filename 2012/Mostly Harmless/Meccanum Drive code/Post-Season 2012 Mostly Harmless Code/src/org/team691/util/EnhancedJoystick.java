package org.team691.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.parsing.IInputOutput;

/**
 * This is a simplified version of the Joystick class provided by FRC because
 * the FRC version is bloated, over-complicated, and inefficient. Created by
 * team 691 during the summer of 2011.
 * @author Gerard
 */
public class EnhancedJoystick implements IInputOutput // the implements, it does nothing!
{
    // --------------------------DECLARE VARIABLES-------------------------------
    // --------------------------DECLARE CONSTANTS-------------------------------
    public static final int X             = 1;
    public static final int LEFT_X        = 1;
    public static final int Y             = 2;
    public static final int LEFT_Y        = 2;
    public static final int Z             = 3;
    public static final int TWIST         = 3;
    public static final int RIGHT_X       = 3;
    public static final int THROTTLE      = 4;
    public static final int RIGHT_Y       = 4;
    public static final int HAT_X         = 5;
    public static final int DPAD_X        = 5;
    public static final int HAT_Y         = 6;
    public static final int DPAD_Y        = 6;

    public static final int BUTTON_X      = 1;
    public static final int BUTTON_A      = 2;
    public static final int BUTTON_B      = 3;
    public static final int BUTTON_Y      = 4;
    public static final int BUTTON_L1     = 5;
    public static final int BUTTON_R1     = 6;
    public static final int BUTTON_L2     = 7;
    public static final int BUTTON_R2     = 8;
    public static final int BUTTON_SELECT = 9;
    public static final int BUTTON_START  = 10;
    public static final int BUTTON_L3     = 11;
    public static final int BUTTON_R3     = 12;
    public static final int BUTTON_DU     = 15;
    public static final int BUTTON_DD     = 16;
    public static final int BUTTON_DL     = 13;
    public static final int BUTTON_DR     = 14;

    // --------------------------END DECLARE CONSTANTS---------------------------

    /**
     * Used internally to receive input from the device.
     */
    protected DriverStation ds;

    /**
     * The USB port that the device is attached to.
     */
    protected int portNum;

    /**
     * Used internally to prevent excessive i/o use, and to track change
     * in button pressed state.
     */
    protected int lastButtons = 0;
    protected double lastTime = Time.time();

    // --------------------------END DECLARE VARIABLES---------------------------

    /**
     * Constructs an object that allows access to an HID input device on a USB port.
     * @param portNumIn What USB port the device is plugged into.
     */
    public EnhancedJoystick(final int portNumIn)
    {
        ds      = DriverStation.getInstance();
        portNum = portNumIn;
    }

    /**
     * The raw value fed from the USB port is returned. When this number is read
     * in binary, the first bit represents the state of button 1, the second bit
     * represents the state of button 2, and so on. Do not use if unfamiliar with
     * bit shift operators.
     * @return The bit stream of 1/0 values for buttons 1-12 on the HID input.
     */
    public int getButtons()
    {
        if ( Time.time() == lastTime )
            return lastButtons;

        lastTime    = Time.time();
        lastButtons = ds.getStickButtons( portNum );

        // transcribe the dpad onto buttons
        if ( getRawAxis( DPAD_Y ) < -0.5 )
            lastButtons += ( 1 << BUTTON_DD - 1 );
        if ( getRawAxis( DPAD_Y ) > 0.5 )
            lastButtons += ( 1 << BUTTON_DU - 1 );
        if ( getRawAxis( DPAD_X ) < -0.5 )
            lastButtons += ( 1 << BUTTON_DL - 1 );
        if ( getRawAxis( DPAD_X ) > 0.5 )
            lastButtons += ( 1 << BUTTON_DR - 1 );

        return lastButtons;
    }

    /**
     * Used to check the value of a given button on the SimpleHID device. HID
     * devices can have up to 12 normal buttons. Values 13-16 will return the
     * Joystick hat/Gamepad dpad axis values formatted to a boolean.
     * @param button What button to check.
     * @return True if the button is pressed and false otherwise.
     */
    public boolean getRawButton(int button)
    {
        // value is out of bounds for HID. Return false.
        // if ( (button < 0) || (button > 16) )
        // return false;

        return ( ( ( getButtons() ) & ( 1 << ( button - 1 ) ) ) != 0 );

        /*
         * // value is a face button
         * if ( button <= 12 )
         *   return ( ( getButtons() & ( 0x1 << ( button - 1 ) ) ) != 0 );
         *
         * // value corrosponds to a hat/dpad location. Return that axis as a boolean value.
         * switch(button)
         * {
         *   case 13 :
         *       return getRawAxis( 5 ) < -0.5;
         *   case 14 :
         *       return getRawAxis( 5 ) > 0.5;
         *   case 15 :
         *       return getRawAxis( 6 ) < -0.5;
         *   case 16 :
         *       return getRawAxis( 6 ) > 0.5;
         * }
         *
         * return false;
         */
    }

    /**
     * Reads the value of a given axis.
     * @param axis What axis to check.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getRawAxis(final int axis)
    {
        if ( ( axis < 0 ) || ( axis > 6 ) )
            return 0;
        if ( axis == Y )
            return -ds.getStickAxis( portNum, axis );

        return ds.getStickAxis( portNum, axis );
    }

    /**
     * Reads a given joystick axis and turns it into two yes/no buttons
     * @param axis what axis to check.
     * @param readPositive values greater than 0.5 will cause a true
     * @param readNegitive values less than -0.5 will cause a true
     * @return true if the axis is at least halfway in the desired direction,
     * false otherwise.
     */
    public boolean axisToButton(final int axis, final boolean readPositive,
                                final boolean readNegitive)
    {
        if ( readPositive && getRawAxis(axis) >  0.5 )
            return true;
        if ( readNegitive && getRawAxis(axis) < -0.5 )
            return true;

        return false;
    }

    /**
     * Reads the value of the X axis.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getX()
    {
        return getRawAxis( X );
    }

    /**
     * Reads the value of the left X axis on a gamepad.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getLeftX()
    {
        return getRawAxis( LEFT_X );
    }

    /**
     * Reads the value of the Y axis.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getY()
    {
        return -getRawAxis( Y );
    }

    /**
     * Reads the value of the left Y axis on a gamepad.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getLeftY()
    {
        return -getRawAxis( LEFT_Y );
    }

    /**
     * Reads the value of the Z axis.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getZ()
    {
        return getRawAxis( Z );
    }

    /**
     * Reads the value of the right X axis on a gamepad.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getRightX()
    {
        return getRawAxis( RIGHT_X );
    }

    /**
     * Reads the value of a Joystick's throttle.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getThrottle()
    {
        return getRawAxis( THROTTLE );
    }

    /**
     * Reads the value of the right Y axis on a gamepad.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getRightY()
    {
        return getRawAxis( RIGHT_Y );
    }

    /**
     * Reads the value of a Joystick's hat switch's X axis.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getHatX()
    {
        return getRawAxis( HAT_X );
    }

    /**
     * Reads the value of a gamepad's Dpad's X axis.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getDpadX()
    {
        return getRawAxis( DPAD_X );
    }

    /**
     * Reads the value of the Joystick's hat switch's Y axis.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getHatY()
    {
        return getRawAxis( HAT_Y );
    }

    /**
     * Reads the value of a gamepad's Dpad's Y axis.
     * @return Where the axis is in relation to it's resting point bounded between
     * 1 and -1 where 0 is the resting point.
     */
    public double getDpadY()
    {
        return getRawAxis( DPAD_Y );
    }

    /**
     * Returns the magnitude of the vector formed by the X and Y values.
     * @return The magnitude of axis 1 and axis 2.
     */
    public double getMagnitude()
    {
        double tempX = getX();
        double tempY = getY();

        return Math.sqrt( ( tempX * tempX ) + ( tempY * tempY ) );
    }

    /**
     * Returns the magnitude of the vector formed by two specified axis values.
     * @return The magnitude of axis a and axis b.
     */
    public double getMagnitude(final int axisA, final int axisB)
    {
        double tempX = getRawAxis( axisA );
        double tempY = getRawAxis( axisB );

        return Math.sqrt( ( tempX * tempX ) + ( tempY * tempY ) );
    }

    /**
     * Returns the angle formed by the X and Y axis and the origin.
     * @return The direction of the vector in radians.
     */
    public double getDirectionRadians()
    {
        return Mathf.atan2( getX(), getY() );
    }

    /**
     * Returns the angle formed by the A and B axis and the origin.
     * @return The direction of the vector in radians.
     */
    public double getDirectionRadians(final int axisA, final int axisB)
    {
        return Mathf.atan2( getRawAxis( axisA ), getRawAxis( axisB ) );
    }

    /**
     * Returns the angle formed by the X and Y axis and the origin.
     * @return The direction of the vector in degrees.
     */
    public double getDirectionDegrees()
    {
        return Math.toDegrees( getDirectionRadians() );
    }

    /**
     * Returns the angle formed by the A and B axis and the origin.
     * @return The direction of the vector in degrees.
     */
    public double getDirectionDegrees(final int axisA, final int axisB)
    {
        return Math.toDegrees( getDirectionRadians( axisA, axisB ) );
    }

}


//FIRST FRC team 691 2012 competition code
