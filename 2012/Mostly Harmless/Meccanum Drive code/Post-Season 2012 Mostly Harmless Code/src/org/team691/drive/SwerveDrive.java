package org.team691.drive;

import edu.Objects;
import edu.wpi.first.wpilibj.SpeedController;
import org.team691.util.Angle;
import org.team691.util.AngleMotor;
import org.team691.util.Mathf;
import org.team691.util.Util;

/**
 * Controls a connected-wheel swerve drive system.
 * @author Gerard Boberg and Casey Graff
 */
public class SwerveDrive implements Drive
{
    // 7.5%
    public static final double ERROR           = 0.075;
    public static double LEFT_TURN_VELOCITY    = 1.0;
    public static double RIGHT_TURN_VELOCITY   = -1.0;
    public static final Angle DEFAULT_TURN_FR  = new Angle( 45 );
    public static final Angle DEFAULT_TURN_FL  = new Angle( -45 );
    public static final Angle DEFAULT_TURN_BR  = new Angle( -45 );
    public static final Angle DEFAULT_TURN_BL  = new Angle( 45 );
    public static final Angle DEFAULT_POSITION = new Angle( 0 );
    protected AngleMotor fr;
    protected AngleMotor fl;
    protected AngleMotor br;
    protected AngleMotor bl;
    protected SpeedController rV               = Objects.rDrive;
    protected SpeedController lV               = Objects.lDrive;
    public Angle target                        = DEFAULT_POSITION;

    protected double rightDrivePower;
    protected double leftDrivePower;

    /**
     *     Gives command flow to the swerve drive so that it is able to accuratly
     *     move all wheels. Call this method OR the wheel lockdown method every
     *     single main loop, but not both. The two methods fight each other.
     *     Recomended use:
     *     <p><pre><code>
     *     if(  driverWantsToLockDown  )
     *         drive.lockDown();
     *     else
     *         drive.update(Y, X, T);
     *     </code></pre></p>
     *
     *     @param Y The forward/backward joystick value.
     *     @param X The strafe left/right joystick value.
     *     @param T The turn in place left/right joystick value.
     */
    public void update(double Y, double X, double T)
    {
        // the joysticks feedback some tiny number at 0. Ignore inconsiquential
        // input.
        // Y = deadZone(Y);
        // X = deadZone(X); //already dead-zoned in robot main
        // T = deadZone(T);

        /*
         * if ( T != 0 )
         * {
         *   int temp = ( T >= 0 ) ? 1 : -1;
         *
         *   // weights the Turn value higher than normal.
         *   T = Mathf.sqrt( Mathf.abs( temp ) );
         *   T *= temp;
         * }
         */

        if ( ( Y == 0.0 ) && ( X == 0.0 ) && ( T == 0.0 ) )
            target = DEFAULT_POSITION;
        else
            target = Mathf.atan2ToAngle( Y, X ).subtract( 90 );

        int reverse = 1;

        // if we want to turn
        if ( T != 0.0 )
        {
            fr.setTargetAngle( target.lerp( DEFAULT_TURN_FR, T ) );
            fl.setTargetAngle( target.lerp( DEFAULT_TURN_FL, T ) );
            bl.setTargetAngle( target.lerp( DEFAULT_TURN_BL, T ) );
            br.setTargetAngle( target.lerp( DEFAULT_TURN_BR, T ) );
            
            fr.update();
            fl.update();
            bl.update();
            br.update();

            rightDrivePower = // T * RIGHT_TURN_VELOCITY
                ( ( Mathf.mag( Y, X ) ) + ( T * RIGHT_TURN_VELOCITY ) );

            rightDrivePower *= ( fr.getCosOfError() + br.getCosOfError() ) / 2;
            rightDrivePower = Mathf.clamp11( rightDrivePower );

            leftDrivePower  = // T * LEFT_TURN_VELOCITY
                ( ( Mathf.mag( Y, X ) ) + ( T * LEFT_TURN_VELOCITY ) );

            leftDrivePower  *= ( fl.getCosOfError() + bl.getCosOfError() ) / 2;
            leftDrivePower  = Mathf.clamp11( leftDrivePower );

            // double v = (rightDrivePower + leftDrivePower) / 2;
            rV.set( Util.victorLinearize( -rightDrivePower * reverse ) );
            lV.set( Util.victorLinearize( -leftDrivePower * reverse ) );
        }
        else // if we don't want to turn
        {
            fr.setTargetAngle( target );
            fl.setTargetAngle( target );
            bl.setTargetAngle( target );
            br.setTargetAngle( target );

            fr.update();
            fl.update();
            bl.update();
            br.update();

            rightDrivePower = Mathf.mag( Y, X );
            rightDrivePower *= ( fr.getCosOfError() + br.getCosOfError() ) / 2;

            leftDrivePower  = Mathf.mag( Y, X );
            leftDrivePower  *= ( fl.getCosOfError() + bl.getCosOfError() ) / 2;

            // double v = (rightDrivePower + leftDrivePower) / 2;
            rV.set( Util.victorLinearize( -rightDrivePower * reverse ) );
            lV.set( Util.victorLinearize( -leftDrivePower * reverse ) );
        }
    }

    /**
     * Places all the wheels in an X formation -- this is the most resistant to
     * movement that we could come up with. This prevents the robot from being
     * pushed around. This method fights with update, so call only one of either
     * update() or lockDown() per loop cycle.
     * Recomended use:
     * <p><pre><code>
     * if(  driverWantsToLockDown  )
     *     drive.lockDown();
     * else
     *     drive.update(Y, X, T);
     * </code></pre></p>
     */
    public void lockDown()
    {
        fr.setTargetAngle( DEFAULT_TURN_FR.negitive() );
        fl.setTargetAngle( DEFAULT_TURN_FL.negitive() );
        bl.setTargetAngle( DEFAULT_TURN_BL.negitive() );
        br.setTargetAngle( DEFAULT_TURN_BR.negitive() );
        fr.update();
        fl.update();
        bl.update();
        br.update();
        rV.set( 0 );
        lV.set( 0 );
    }

    public void stop()
    {
        disable();
    }

    public void enable()
    {
        fr.enable();
        fl.enable();
        bl.enable();
        br.enable();
    }

    public void disable()
    {
        fr.disable();
        fl.disable();
        bl.disable();
        br.disable();
        lV.set( 0 );
        rV.set( 0 );
    }

    public void resetAllEncoders()
    {
        fr.reset();
        fl.reset();
        bl.reset();
        br.reset();
    }
}

//FIRST FRC team 691 2012 competition code
