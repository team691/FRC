package Team691.Util;

import com.sun.squawk.util.MathUtils;
/**
 * Contains a set of useful mathematical functions not found in Math or MathUtils.
 * Also contains some MathUtils functions because MathUtils is bugged on Gerard's
 * computer so this class centralizes all possible compile errors. The f in
 * Mathf stands for functions.
 * @author Gerard
 */
public class Mathf
{
    public static final double PI = 3.141592653589793;
    public static final double E = 2.718281828459045;
    /**
     * A tiny number.
     */
    public static final double EPSILON = 0.000000001; //(1 / 1 billion)
    public static final double DEG2RAD = PI/180.0;
    public static final double RAD2DEG = 180.0/PI;


    public static double cos(final double value)
    {   return Math.cos(value);   }
    public static double sin(final double value)
    {   return Math.sin(value);   }
    public static double tan(final double value)
    {   return Math.tan(value);   }
    public static double acos(final double value)
    {   return MathUtils.acos(value);   }
    public static double asin(final double value)
    {   return MathUtils.asin(value);   }
    public static double atan(final double value)
    {   return MathUtils.atan(value);   }
    public static double atan(final double y,final double x)
    {   return MathUtils.atan(y/x);   }
    public static double atan2(final double y,final double x)
    {   return MathUtils.atan2(y, x);   }
    public static double sqrt(final double value)
    {   return Math.sqrt(value);    }
    public static double dist(final double a,final double b)
    {   return (b-a);   }
    public static double absDist(final double a,final double b)
    {   return abs(b-a);   }

    /**
     * Returns the inputed value without a negative sign.
     */
    public static double abs(final double value)
    {   
        return Math.abs(value); /*Double.longBitsToDouble
                (              //bit shift removes the sign bit then
                    (Double.doubleToLongBits(value)<<1)   >>>1
                );             //bit shift returns to normal sans negitive sign.
   */ }
    /**
     * Returns the magnitude of the vector formed by a and b. Think a^2 + b^2 = c^2.
     */
    public static double mag(final double a,final double b)
    {   return sqrt((a*a)+(b*b));   }
    public static double toDegrees(final double value)
    {   return value * RAD2DEG;   }
    public static double toRadians(final double value)
    {   return value * DEG2RAD;   }
    
    public static int sign(final double value)
    {
        switch(  (int) (Double.doubleToLongBits(value) >> 63) )
        {
            case 0:  return  1; //sign bit is 0. value is positive
            case 1:  return -1; //sign bit is 1. value is negitive
            default: return  1; //fail compy/me?
        }
    }

    /**
     * Linerally intepolates between two values - from a to b. Useful for
     * smoothing values over time if <code>Time.deltaTime</code> is used as
     * the third parameter.
     * @param a The first value.
     * @param b The second value.
     * @param t A value between 0 and 1 that defines t between the values
     * to return.
     * @return A if t is 0, B if t is 1, the midpoint if t is 0.5,
     * and t% from A to B otherwise.
     */
    public static double lerp(final double a,final double b, double t)
    {
        t = clamp01(t);
        if(  approximately(t, 0)  )
            return a;
        if(  approximately(t, 1)  )
            return b;
        if(  approximately(t, 0.5))
            return (a+b)/2.0;

        double delta = b-a;
        return (a + delta * t);
    }
    /**
     * Restricts the inputed value between 1 and -1.
     * @param input The value to be checked.
     * @return -1 if input is less than -1, 1 if the input is greater than 1, and
     * unmodified input otherwise.
     */
    public static double clamp11(final double input)
    {
        if(input >= 1)
            return 1;
        if(input <= -1)
            return -1;
        return input;
    }
    /**
     * Restricts the inputed value between 0 and 1.
     * @param input The value to be checked.
     * @return 0 if input is less than 0, 1 if the input is greater than 1, and
     * unmodified input otherwise.
     */
    public static double clamp01(final double input)
    {
        if(input >= 1)
            return 1;
        if(input <= 0)
            return 0;
        return input;
    }

    /**
     * Restricts a value between a and b. A and B's size realative to each other
     * does not matter.
     * @param input The value to be checked.
     * @param a The first bound.
     * @param b The other bound.
     * @return If input is between a and b, the unmodified input; if the input is
     * larger than both a and b, then the greater of a and b; if the input is less
     * than both a and b, the lesser of a and b.
     */
    public static double clamp(final double input, double a, double b)
    {
        //put the larger value in b
        if(a > b)
        {
            double temp = b;
            b = a;
            a = temp;
        }

        if(input >= b)
            return b;
        if(input <= a)
            return a;
        return input;
    }
    /**
     * Restricts a value between 0 and 1 not by cutting off values outside the
     * range, but by looping the value the same way one would an angle. For
     * example: 3.2579 would return 0.2579
     * @param value The value to repeat.
     * @return The value looped inside of [0, 1).
     */
    public static double repeat01(double value)
    {
        value -= (int)value * sign(value); //tricky cut off the non decimal of the double
        while(value >= 1) value -= 1;
        while(value <  0) value += 1;
        return value;
    }
    /**
     * Restricts an angle in degrees between -180 and 180 not by cutting off values
     * outside the range, but by reducing the value to inside the area.
     * @param angle The angle to reduce.
     * @return The angle looped inside of (-180, 180]
     */
    public static double repeatAngle(double angle)
    {
        while(angle >   180) angle -= 360;
        while(angle <= -180) angle += 360;
        return angle;
    }

    /**
     * Restricts a value between a and b not by cutting off values
     * outside the range, but by reducing the value to inside the area.
     * @param value The angle to reduce.
     * @return The angle looped inside of [a, b)
     */
    public static double repeat(double value, double a, double b)
    {
        //put the larger value into b
        if(a > b)
        {
            double temp = b;
            b = a;
            a = temp;
        }
        double dt = absDist(a,b);
        while(value >= b) value -= dt;
        while(value <  a) value += dt;
        return value;
    }

    /**
     * Returns true if two angles (assumed degrees) are close to each other.
     * Takes into account the fact that angles repeat after 360.
     * @param a The first angle in degrees.
     * @param b The second angle in degrees.
     * @return True if the angles are within 0.5 degrees of each other, false otherwise.
     */
    public static boolean areAnglesClose(double a, double b)
    {
        double dt = absDist(a,b);
        if( dt < 0.5)
            return true;
        a = repeatAngle(a);
        b = repeatAngle(b);
        dt = absDist(a,b);
        if( dt < 0.5 || dt > 359.5)// check for -180, 180 are ==
            return true;
        return false;
    }

    /**
     * Useful because <code>1.0 == 3.0/3.0</code> does not always return true.
     * Due to internal computer error.
     * @param a The first value.
     * @param b The second value.
     * @return True if the values are close to each other.
     */
    public static boolean approximately(final double a, final double b)
    {   return  abs(a - b) <= EPSILON;    }
    /**
     * Useful because <code>1.0 == 3.0/3.0</code> does not always return true
     * due to internal errors.
     * @param a The first value.
     * @param b The second value.
     * @param ERROR The maximum distance the values can be from each other.
     * @return True if the values are within ERROR of each other.
     */
    public static boolean approximately(final double a, final double b, final double ERROR)
    {   return  abs(a - b) <= ERROR;    }

    public static int floor(double input)
    {
        if(input >= 0)
            return (int)input;
        else
            return (int)(input - 1);
    }

    public static int ceiling(double input)
    {
        if(input <= 0)
            return (int) input;
        else
            return (int) (input + 1);
    }
}
