/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

// About 5,800 lines of java in this project, give or take and not counting the C++ or py

package edu;

import edu.io.AutoTarget;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import org.team691.drive.Drive;
import org.team691.util.EnhancedJoystick;
import org.team691.util.Time;
import org.team691.util.Util;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotMain extends SimpleRobot
{
    public static boolean DEBUG_MODE                 = true;
    public static boolean AXIS_CAMERA_IS_ATTACHED    = false;
    public static final double DEFAULT_RPM_OFFSET    = 125.0;
    public static final double DEBUG_UPDATE_INTERVAL = 2.0;
    public static final double GC_INTERVAL           = 30.0;

    // static final int THROTTLE = EnhancedJoystick.THROTTLE;

    // manual preset RPM values
    public static final int DUMP_RPM  = 1200;
    public static final int KEY_RPM  = 1950;
    public static final int MID_RPM  = 1900;
    public static final int BUMP_RPM = 2100;

    public static final int MIN_RPM  = 750;
    public static final int MAX_RPM  = 2600;
    // end manual preset RPM values

    public double printTime                          = Time.time() + DEBUG_UPDATE_INTERVAL;
    public boolean lastManualButtonHeld              = false;
    public boolean lastAutoAimButtonHeld             = false;
    private boolean lastOffsetHeld                   = false;
    
    public boolean lockSwerve                        = false;
    public boolean acceptTargetingData               = false;
    
    public EnhancedJoystick leftJoy                  = Objects.leftJoy;
    
    public Drive drive                               = Objects.driveSystem;
    public RampArticulate rampArticulate             = Objects.armSystem;
    public IntakeAndConveyor intakeAndConveyor       = Objects.intakeSystem;
    
    public Turret turret                             = Objects.turret;
    public AutoTarget autoTarget                     = Objects.autoTarget;
    
    
    protected double gcTime                          = Time.time();
    public double Y                                  = 0,
                  X                                  = 0,//joystick axises
                  T                                  = 0,
                  turtRot                            = 0,
                  turtPow                            = 0,
                  rpmOffset                          = 0;
    
    /**
     * This function is called once when the robot is first turned on.
     * As of 3-15-2012, robotInit() does nothing other than setup the axis cam.
     */
    public void robotInit()
    {
        zeroAll();
        Watchdog.getInstance().setEnabled(false);
        log("robotInit()");
        if(  AXIS_CAMERA_IS_ATTACHED  )
        {
            try
            {
                log("setting up axis camera");
                AxisCamera.getInstance().writeCompression(0);
                AxisCamera.getInstance().writeBrightness(10);
                AxisCamera.getInstance().writeResolution(AxisCamera.ResolutionT.k160x120);
                AxisCamera.getInstance().writeColorLevel(50);
            } catch(Exception e)
            {
                log("error setting up axis camera\n" + e.toString() );
            }
        }
    }

    // allows cordination with alliance during automode -- wait until basket is clear
    public static final double AUTO_MODE_SHOOT_DELAY = 0.0;

    /**
     * This function is called once each time the robot enters autonomous mode.
     *
     * As of 3-15-2012, autonomous brings down the ramp arm, and drives backwards
     * into the coop bridge while attempting to shoot at the top basket via 
     * auto-targeting. The intake is on the same motor as the conveyor, so when
     * the shooter starts feeding balls, it will turn on the intake. This will
     * give us a chance for a 3, or 4 ball autonomous mode.
     * 
     * It will wait until <code>AUTO_MODE_SHOOT_DELAY</code> seconds have passed
     * before it starts checking the intake and feeder, so that we don't interfere
     * with our alliance partners.
     */
    public void autonomous()
    {
        zeroAll();

        drive.enable();
        double autoShootTime = Time.time() + AUTO_MODE_SHOOT_DELAY;

        while( isEnabled() && isAutonomous() )
        {
            rpmOffset = DEFAULT_RPM_OFFSET;
            acceptTargetingData = true;

            //place time-sensitive code below this line
            if ( !Time.newCycle() )
                continue;

            if( autoTarget.getConnectionStatus() == AutoTarget.NOT_CONNECTED )
            {
                turret.setAngleObjective(0);
                turret.setSpeedObjective( KEY_RPM );
                turret.setSpeedOffset( rpmOffset );
            }
            else 
            {
                handleTurretAutoTargeting();
            }
            
            turret.update();
            turret.spinUpShooter();
            
            //if( shooterJoy.getRawButton(2) )
            //    turret.feederReverse();

            if ( Time.time() > autoShootTime )
                turret.shootWithChecks();

            if ( DEBUG_MODE )
                debugPrint();
        } // end autonomous loop

        zeroAll();
    } // end autonomous

    /**
     * This function is called once each time the robot is disabled.
     *
     * The disabled method, it does nothing! Also prints out debug data.
     */
    public void disabled()
    {
        zeroAll();
        drive.disable();

        while( isDisabled() )
        {
            //place time-sensitive code below this line
            if ( !Time.newCycle() )
                continue;

            acceptTargetingData = false;
            handleTurretAutoTargeting();

            if ( DEBUG_MODE )
                debugPrint();
        } // end disabled loop
        zeroAll();
    } // end disabled

    /**
     * This function is called once each time the robot enters operator control.
     *
     * Every cycle, operatorControl will read the user input, perform actions
     * based on detected buttons, updates the drive system, and updates the 
     * automatic target system.
     * 
     * Most of the minor sub system controls (such as the ramp arm) are inside
     * the various <code>handle_____Input()</code> methods.
     */
    public void operatorControl()
    {
        zeroAll();
        drive.enable();

        while( isEnabled() && isOperatorControl() )
        {
            drive.enable();

            // place time-sensitive code below this line
            if ( !Time.newCycle() )
                continue;

            // get joystick axis input
            Y       = Util.joystickDeadZone( -leftJoy.getY() );
            X       = Util.joystickDeadZone( leftJoy.getX() );
            turtRot = Util.joystickDeadZone( -leftJoy.getZ() ); // twist
            
            //Now mathematicly modify the numbers to change their responsiveness.
            Y = Util.cubeAxis( Y );
            X = Util.cubeAxis( X );
            turtRot = Util.oneHalfXSquared( turtRot );
            
//            handleTurretAutoTargeting();

            // override kinect input if the driver wants to turn the turret manually
            if ( turtRot != 0 )
                turret.setAngleObjective( turtRot );
            turret.update();

            //check the various joystick buttons
            handleUniversalInput();
            
            // pass command flow to the drive system, so that we can move.
            if ( lockSwerve )
                drive.stop();
            else
                drive.update( Y, X, T );

            

            // print various debugging output
            if ( DEBUG_MODE )
                debugPrint();
        } // end operator control loop
    } // end operator control=

    /**
     * Checks all buttons related to controlling the robot. These do not change
     * no matter if the robot is in manual mode or automatic mode.
     *
     * as of 3-15-2012 this binds  :
     *      shooter hat switch    : set auto target to aim at the HAT_DIRECTION hoop
     *      shooter 10            : RPM offset += 25 (toggle)
     *      shooter 9             : RPM offset -= 25 (toggle)
     *      left 3 or shooter 7   : bring down ramp manipulator (hold)
     *      left 2 or shooter 8   : bring up ramp manipulator   (hold)
     *      right 3 or shooter 3  : turn on both the intake and conveyor (hold)
     *      right 2 or shooter 4  : reverse both the intake and conveyor (hold)
     *      right 1 or shooter 11 : lock down swerve drive into the X position (hold)
     *      left  1               : reduce left joystick axis by 1/3 (hold)
     *      shooter Throttle      : choose manual or automatic input modes.
     *                              This resets the following vars:
     *                                  - rpm offset         --> 0
     *                                  - target hoop        --> top
     *
     */
    protected void handleUniversalInput()
    {
        // handle launching balls -- hold on SHOOTER 1
        if ( leftJoy.getRawButton( 1 ) )
        {
            turret.spinUpShooter();
            turret.shootWithChecks();
        }
        else
        {
            turret.resetShooterI();
            turret.spinDownShooter();
            turret.stopShooting();
        }

        // adjust rpm offset -- toggle on SHOOTER 10, -9
//        if ( leftJoy.getButtons() != 0 )
//        {
//            if ( !lastOffsetHeld )
//            {
//                if ( leftJoy.getRawButton( 10 ) )
//                {
//                    rpmOffset      += 25;
//                    lastOffsetHeld = true;
//                }
//                else if ( leftJoy.getRawButton( 9 ) )
//                {
//                    rpmOffset      -= 25;
//                    lastOffsetHeld = true;
//                }
//            }
//        }
//        else
//            lastOffsetHeld = false;

        if ( leftJoy.getRawButton( 5 ) || leftJoy.getRawButton( 6 ) )
            rampArticulate.bringDown();
        else if ( leftJoy.getRawButton( 3 ) || leftJoy.getRawButton( 4 ) )
            rampArticulate.bringUp();
        else
            rampArticulate.turnOff();

        // handle intakeAndConveyor and conveyor -- hold on SHOOTER 3, -5
        if ( leftJoy.getRawButton( EnhancedJoystick.BUTTON_DU ) )
            intakeAndConveyor.turnOn( true );
        else if ( leftJoy.getRawButton( EnhancedJoystick.BUTTON_DD ) )
            intakeAndConveyor.turnReverse( true );
        else
            intakeAndConveyor.turnOff( false );

        // Slows down robot for micro adjustment -- hold on LEFT 1
        if ( leftJoy.getRawButton( 1 ) )
        {
            Y *= 0.333;
            X *= 0.333;
            T *= 0.333;
        }
        
        //check the throttle to see if we're supposed to accept targeting data
        //  or not.
        acceptTargetingData = leftJoy.axisToButton(4, false, true);
        if( !acceptTargetingData )
        {
            rpmOffset = DEFAULT_RPM_OFFSET;
            autoTarget.cycleTarget( AutoTarget.TOP );
        }
        
        //reverse the conveyor and feeder if a ball gets stuck.
        if ( leftJoy.getRawButton( 12 ) )
        {
            turret.conveyorDown();
            turret.feederReverse();
        }
        
        //temporary dump setting
//        if( shooterJoy.getRawButton( 4 ) )
//            turret.setSpeedObjective( DUMP_RPM );
//        else if( shooterJoy.getRawButton( 6 ) )
//            turret.setSpeedObjective( KEY_RPM );
//        
//        if( shooterJoy.getRawButton(2) )
//            turret.feederReverse();
        
    } // end handle universal controls

    protected int newData = 0;
    /**
     * handles the actions needed to maintain the auto targeting computer.
     * Does (almost) nothing if <code>acceptTargetingData</code> is false.
     */
    protected void handleTurretAutoTargeting()
    {
        autoTarget.checkForBlockedConnection();

        // handle targeting computer connection
        if ( autoTarget.newData() )
        {
            newData++;
            if ( acceptTargetingData )
            {
                turret.setAngleObjective( autoTarget.getTurretAngle() );
                turret.setSpeedObjective( autoTarget.getTurretPower() );
            }
            autoTarget.clearNewData();
        }
        
        turret.setSpeedOffset( rpmOffset );
    }


    /**
     * Prints out debug data continuously.
     */
    protected void debugPrint()
    {
        if ( Time.time() > printTime )
        {
            log( "network updates per second    : " + newData / DEBUG_UPDATE_INTERVAL
                 + "\nAccept network data       : " + acceptTargetingData
                 + "\nnetwork Distance to target: " + autoTarget.getPacket().distance
                 + "\ncurrent RPM               : " + turret.getShooterRPM()
                 + "\ntarget  RPM               : " + turret.getShooterTargetRPM()
                 + "\ncurrent rotate setting    : " + turret.getRotateSetting()
                 + "\n current RPM offset       : " + rpmOffset );
            log("************************************************************");
            log("************************************************************");
            log( "Y, X, T: " + Y + ", " + X + ", " + T );

            newData = 0;
            printTime = Time.time() + DEBUG_UPDATE_INTERVAL;
        }
        
        if( Time.time() > gcTime )
        {
            System.gc();
            gcTime = Time.time() + GC_INTERVAL;
        }
    }

    /**
     * Resets various variables to their initial state.
     */
    public void zeroAll()
    {
        edu.wpi.first.wpilibj.Watchdog.getInstance().setEnabled( false );
        Time.newCycle();
        Time.newCycle();

        lastManualButtonHeld  = false;
        lockSwerve            = false;
        acceptTargetingData   = true;
        lastAutoAimButtonHeld = false;
        lastOffsetHeld        = false;
        Y = X = T             = 0;
        rpmOffset             = DEFAULT_RPM_OFFSET;

        turret.spinDownShooter();
        turret.stopShooting();
        turret.setAngleObjective( 0 );
        turret.setSpeedObjective( 0 );
        turret.setSpeedOffset( 0 );
        turret.resetShooterI();
        autoTarget.checkForBlockedConnection();
        autoTarget.cycleTarget( AutoTarget.TOP );
        drive.stop();
        System.gc();
    }

    /**
     * Terse System.out.println that adds the current time to the print out.
     * @param s  the string to print.
     */
    public static void log(String s)
    {
        if( DEBUG_MODE )
            System.out.println( Time.string() + ":.........." + s );
    }
}

//FIRST FRC team 691 2012 competition code