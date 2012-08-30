
// ------------------------------------------------------------------------------
// PID controller code for team 691; frc 2011 season
// Main PID programer: Gerard
//
// Uses the porportional (current), Intergal (total), and differential(diffrence)
// Errors to output where to move the motors
// ------------------------------------------------------------------------------
// updated fri feb 18 2011

/**
 * @TEAM: 691 Hart Burn
 * @AUTHOR: Akira "Ninja" H., Casey G., Gerard "B-Bo" B.,
 *          Bryan "Brandon" S. (Don't call him Brandon), Robert "Sir Sayer" G.,
 *          Shoma H.
 * @SEASON: 2011
 * @CONTACT: mheid2011@gmail.com (Akira "Ninja" H.)
 */

package org.team691.util;

public class PID
{
    public static final double KD_DEFAULT     = 2;       // tuning will not be easy
    public static final double KI_DEFAULT     = 0.00001; // created these rough numbers
    public static final double KP_DEFAULT     = 7;       // manual brute force tests
    public static final double SCALER_DEFAULT = 0.0001;
    protected double cmd                      = 0;
    protected double measured                 = 0;

    // -------------------End declare vars---------------------------------------

    protected double KP, KI, KD; // constants
    protected long dTime;        // delta time in milliseconds
    protected boolean debugMode;
    protected double errorD;
    protected double errorI;
    protected double errorP;
    protected double lastCmdIn;

    // -----------------Declare vars---------------------------------------------
    protected double lastError;
    protected long lastTime;
    protected long nowTime;
    protected double output; // output
    public double scaler;    // how much to scale output, based on gear ratio,

    // motor rpm, and encoder clicks/round

    // constructor
    // assumes default values if none are given
    public PID()
    {
        this( SCALER_DEFAULT, KP_DEFAULT, KI_DEFAULT, KD_DEFAULT );
    }

    // constructs a PID object with scale value, K constants
    public PID(double scaler, double KP, double KI, double KD)
    {
        this.scaler = scaler;
        this.KP     = KP;
        this.KI     = KI;
        this.KD     = KD;

        errorI      = 0;
        lastError   = 0;
        nowTime     = System.currentTimeMillis();

        debugMode   = false;
    }

    // if on, will system.out.println the data each time calc is run
    public void setDebugMode(boolean input)
    {
        debugMode = input;
    }

    // calculates the PID output
    public double calc(double cmdIn, double measuredIn)
    {
        cmd      = cmdIn;
        measured = measuredIn;

        // updates all of the needed time variables
        _updateTime();

        // current error
        errorP = cmdIn - measuredIn;

        // increment total error by current error scaled by delta time
        errorI += ( errorP * ( dTime / 1000.0 ) );

        // current change in error over time
        errorD = ( errorP - lastError ) / dTime;

        // calculate output
        output = ( KP * errorP ) + ( KI * errorI ) + ( KD * errorD );

        // scale the output down into something useable
        output    *= scaler;

        lastCmdIn = cmdIn;
        lastError = errorP;

        if ( debugMode == true )
            System.out.println( this.toString() );
        return output;
    }

    public double calc(double diffrenceIn)
    {
        return calc( 0, diffrenceIn );
    }

    // After reaching the destination, we can reset I to prevent huge values
    public void resetI()
    {
        errorI = 0;
    }

    // alias for resetI()
    public void resetErrorI()
    {
        resetI();
    }

    // calculates change in time
    // also prevents for divide by 0 errors
    private void _updateTime()
    {
        lastTime = nowTime;
        nowTime  = System.currentTimeMillis();
        dTime    = nowTime - lastTime; // milliseconds

        // catch possible divide by 0 errors
        if ( dTime == 0 )
            dTime = 1;
    }

    // alias for calc()
    public double calculate(double cmdIn, double measuredIn)
    {
        return calc( cmdIn, measuredIn );
    }

    // returns current KP value
    public double getKP()
    {
        return KP;
    }

    // allows us to change KP during run time, can be used for live calibration
    public double setKP(double KPIn)
    {
        return KP = KPIn;
    }

    // changes the KP value relative to itself.
    public double adjustKP(double input)
    {
        return setKP( getKP() + input );
    }

    // returns current KI value
    public double getKI()
    {
        return KI;
    }

    // allows us to change KI during run time, can be used for live calibration
    public double setKI(double KIIn)
    {
        return KI = KIIn;
    }

    // changes the KI value relative to itself.
    public double adjustKI(double input)
    {
        return setKI( getKI() + input );
    }

    // returns current KD value
    public double getKD()
    {
        return KD;
    }

    // allows us to change KD during run time, can be used for live calibration
    public double setKD(double KDIn)
    {
        return KD = KDIn;
    }

    // changes the KD value relative to itself.
    public double adjustKD(double input)
    {
        return setKD( getKD() + input );
    }

    // returns current scaler value
    public double getScaler()
    {
        return scaler;
    }

    // allows us to change scale value during run time, can be used for live calibration
    public double setScaler(double scaleIn)
    {
        return scaler = scaleIn;
    }

    public double adjustSclaer(double input)
    {
        return setScaler( getScaler() + input );
    }

    public String toString()
    {
        return "------------------------------" + "\n" + "KP: " + KP + "\n" + "KI: " + KI
               + "\n" + "KD: " + KD + "\n" + "PID input: " + cmd + "; \t\t " + measured
               + ";\n" + "errorP: " + errorP + "\n" + "errorI: " + errorI + "\n"
               + "errorD: " + errorD + "\n" + "delta Time: " + dTime + "\n" + "output: "
               + output + "\n" + "------------------------------";
    }
} // end of class; end of file

//FIRST FRC team 691 2012 competition code
