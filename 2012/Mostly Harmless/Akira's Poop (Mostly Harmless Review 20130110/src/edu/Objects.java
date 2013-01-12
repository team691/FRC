package edu;

import edu.io.AutoTarget;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import org.team691.drive.ArcadeDrive;
import org.team691.drive.Drive;
import org.team691.util.*;



/**
 * This class provides singleton access to all the robot's major components.
 * The class creates, initializes and otherwise prepares all the needed components
 * during <code>RobotMain.robotInit()</code>. 
 * 
 * The idea for this class was taken from one of FIRST's example projects.
 * @author Gerard Boberg and Michael Erbach
 */
public class Objects
{
    public static final int SIDECAR_1                   = 2;
    public static final int SIDECAR_2                   = 4;

    public static final int LEFT_JOYSTICK_PORT          = 1;
    public static final int RIGHT_JOYSTICK_PORT         = 2;
    public static final int SHOOTER_JOYSTICK_PORT       = 3;

    public static final int L_DRIVE_1                   = 1; // 1
    public static final int R_DRIVE_1                   = 2; // 1
    
    public static final int SHOOTER_ENCODER             = 10;// 1
    public static final int SHOOTER_MOTOR_R             = 5; // 1
    public static final int SHOOTER_MOTOR_L             = 6; // 1
    public static final int FEEDER_SPIKE                = 8; // 2
    public static final int TURRET_TURN_MOTOR           = 4; // 1
    
    //Force sensor at 12-14 // 1
    //Turret limit switch 12-14 // 1

    public static final int INTAKE_AND_TRANSFER_VICTOR  = 3; // 1
    public static final int RAMP_SPIKE_R                = 4; // 1
    public static final int RAMP_SPIKE_L                = 3; // 1

    // User input----------------------------------------------------------------
    public static EnhancedJoystick leftJoy    = new EnhancedJoystick( LEFT_JOYSTICK_PORT );

    // End user input------------------------------------------------------------

    // Drive components----------------------------------------------------------
    public static SpeedController rDrive        = new Victor( SIDECAR_1, R_DRIVE_1 );;
    public static SpeedController lDrive        = new Victor( SIDECAR_1, L_DRIVE_1 );
    // End drive components-----------------------------------------------------

    
    //---------------------Shooter and turret components------------------------

    // shooter components
    public static SpeedController shooterVictorR = new Jaguar( SIDECAR_1,
                                                                  SHOOTER_MOTOR_R );
    public static SpeedController shooterVictorL = new Jaguar( SIDECAR_1,
                                                                  SHOOTER_MOTOR_L );
    
    //double speed controller allows us to control both motors with a single
    //  implements SpeedController class
    public static DoubleSpeedController shooterDoubleMotor =
        new DoubleSpeedController( shooterVictorR, shooterVictorL, false, true );

    public static CounterBase shooterRPMEncoder = new Counter();
    public static RPMMotor turretShooterMotor   = new RPMMotor( shooterDoubleMotor,
                                                              shooterRPMEncoder );

    // turret components
    public static SpeedController turretRotateVictor = new Victor( SIDECAR_1,
                                                                   TURRET_TURN_MOTOR );
    // End shooter and turret components----------------------------------------

    // manipulation objects-----------------------------------------------------

    public static Relay feederSpike = new Relay( SIDECAR_1, FEEDER_SPIKE );
    public static Relay rampSpikeR = new Relay( SIDECAR_1, RAMP_SPIKE_R );
    public static Relay rampSpikeL = new Relay( SIDECAR_1, RAMP_SPIKE_L );

    public static SpeedController intakeAndConvayorSpike =
        new Victor( SIDECAR_2, INTAKE_AND_TRANSFER_VICTOR );
    // end manipulation components-----------------------------------------------

    // camera tracking object

    // shooter encoder setup-----------------------------------------------------
    static
    {
        ( (Counter)shooterRPMEncoder ).setUpSource( SIDECAR_1, SHOOTER_ENCODER );
        ( (Counter)shooterRPMEncoder ).clearDownSource();
        ( (Counter)shooterRPMEncoder ).setMaxPeriod( 200 );
        ( (Counter)shooterRPMEncoder ).setUpSourceEdge( true, false );
        ( (Counter)shooterRPMEncoder ).start();
    }

    // end shooter encoder setup-------------------------------------------------

    // drive PID calibration-----------------------------------------------------
    // scale, KP, KI, KD = (DEFAULT_SCALE * some_constant, 70, 5, 35)
    public static final double DEFAULT_SCALE = 0.00005;//0.00003; //0.000035; // 0.000075
    
    //now create all the sub system components.
    public static Drive driveSystem              = new ArcadeDrive();
    public static AutoTarget autoTarget          = new AutoTarget();
    public static IntakeAndConveyor intakeSystem = new IntakeAndConveyor();
    public static RampArticulate armSystem       = new RampArticulate( false, true );
    public static Turret turret                  = new Turret();
}

//FIRST FRC team 691 2012 competition code
