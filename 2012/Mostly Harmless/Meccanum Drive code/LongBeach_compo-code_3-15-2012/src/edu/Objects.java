package edu;

import edu.io.AutoTarget;
import edu.wpi.first.wpilibj.*;
import org.team691.drive.Drive;
import org.team691.drive.MeccanumDrive;
import org.team691.drive.PIDControlledVelocityMotor;
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
    public static final int SIDECAR_1             = 2; // 1 //good
    public static final int SIDECAR_2             = 1; // 2 //good

    public static final int RIGHT_JOYSTICK_PORT   = 2;
    public static final int LEFT_JOYSTICK_PORT    = 1;
    public static final int SHOOTER_JOYSTICK_PORT = 3;

    public static final int L_DRIVE_1             = 4; // 2 //good

    // public static final int L_DRIVE_2           = 2;
    public static final int R_DRIVE_1 = 3; // 1 //good

    // public static final int R_DRIVE_2           = 9;

    
    public static final int FR_PIVOT  = 5; // 1 //good
    public static final int FR_ENC    = 1; // 1
    public static final int FR_ENC_A  = 3; // 1 //good
    public static final int FR_ENC_B  = 4; // 1 //good
    
    public static final int FL_PIVOT  = 5; // 2 //good
    public static final int FL_ENC    = 2; // 1
    public static final int FL_ENC_A  = 3; // 2 //good
    public static final int FL_ENC_B  = 4; // 2 //good
    
    public static final int BL_PIVOT  = 2; // 2 //good
    public static final int BL_ENC    = 3; // 1
    public static final int BL_ENC_A  = 1; // 2 //good
    public static final int BL_ENC_B  = 2; // 2 //good
    
    public static final int BR_PIVOT  = 2; // 1 //good
    public static final int BR_ENC    = 4; // 1
    public static final int BR_ENC_A  = 1; // 1 //good
    public static final int BR_ENC_B  = 2; // 1 //good


    // public static final int INTAKE_LIMIT_1      = -1;
    // public static final int INTAKE_LIMIT_2      = -1;
//    public static final int L_DRIVE_ENCODER_A = 7;
//    public static final int L_DRIVE_ENCODER_B = 8;
//    public static final int R_DRIVE_ENCODER_A = 5;
//    public static final int R_DRIVE_ENCODER_B = 6;

    // public static final int SHOOTER_ENCODER_A     = -1;
    // public static final int SHOOTER_ENCODER_B     = -1;
    // public static final int TRANSFER_SPIKE_2      = 7;
    // public static final int TURRET_LEFT_LIMIT     = -1;
    // public static final int TURRET_RIGHT_LIMIT    = -1;
    
    public static final int SHOOTER_ENCODER            = 14;// 1 //good
    public static final int SHOOTER_RPM_R              = 6; // 1 //good
    public static final int SHOOTER_RPM_L              = 10;// 1 //good
    public static final int FEEDER_SPIKE               = 8; // 1 //good
    public static final int TURRET_TURN                = 6; // 2 //good

    public static final int INTAKE_AND_TRANSFER_VICTOR = 7; // 2 //good
    public static final int RAMP_SPIKE_R               = 4; // 1 //good
    public static final int RAMP_SPIKE_L               = 3; // 1 //good

    //swerve drive angle offset values, unused in Meccanum
    public final static Angle FR_OFFSET                = new Angle( -133 );
    public final static Angle FL_OFFSET                = new Angle( 171.5 );
    public final static Angle BR_OFFSET                = new Angle( -39 );
    public final static Angle BL_OFFSET                = new Angle( 42 );

    // User input----------------------------------------------------------------
    public static Joystick rightJoy           = new Joystick( RIGHT_JOYSTICK_PORT );
    public static Joystick leftJoy            = new Joystick( LEFT_JOYSTICK_PORT );
    public static EnhancedJoystick shooterJoy =
        new EnhancedJoystick( SHOOTER_JOYSTICK_PORT );

    // End user input------------------------------------------------------------

    // Drive components----------------------------------------------------------

    // right velocity control
    public static SpeedController rDriveVictor1 = new Victor( SIDECAR_1, R_DRIVE_1 );
    // public static SpeedController rDriveVictor2 = new Victor( SIDECAR_1, R_DRIVE_2 );

    public static SpeedController rDrive        = rDriveVictor1;
    

    // left velocity control
    public static SpeedController lDriveVictor1 = new Victor( SIDECAR_2, L_DRIVE_1 );
    // public static SpeedController lDriveVictor2 = new Victor( SIDECAR_1, L_DRIVE_2 );

    public static SpeedController lDrive        = lDriveVictor1;

    

    // front-right drive wheel control
    public static SpeedController frSteeringVictor = new Victor( SIDECAR_1, FR_PIVOT );
    public static AngleSensor frSteeringEncoder = new AngleSensor( 1, FR_ENC, FR_OFFSET );
    public static AngleMotor frSteeringController  = new AngleMotor( frSteeringVictor,
                                                                    frSteeringEncoder );
    public static Encoder frVelocityEncoder        = new Encoder( SIDECAR_1, FR_ENC_A,
                                                           SIDECAR_1, FR_ENC_B, true,
                                                           CounterBase.EncodingType.k1X );
    public static PIDControlledVelocityMotor frVelocityController = 
            new PIDControlledVelocityMotor(frSteeringVictor, frVelocityEncoder);

    
    // front-left drive wheel control
    public static SpeedController flSteeringVictor = new Victor( SIDECAR_2, FL_PIVOT );
    public static AngleSensor flSteeringEncoder = new AngleSensor( 1, FL_ENC, FL_OFFSET );
    public static AngleMotor flSteeringController  = new AngleMotor( flSteeringVictor,
                                                                    flSteeringEncoder );
    public static Encoder flVelocityEncoder        = new Encoder( SIDECAR_2, FL_ENC_A,
                                                           SIDECAR_2, FL_ENC_B, false,
                                                           CounterBase.EncodingType.k1X );
    public static PIDControlledVelocityMotor flVelocityController = 
            new PIDControlledVelocityMotor(flSteeringVictor, flVelocityEncoder);

    
    // back-left drive wheel control
    public static SpeedController blSteeringVictor = new Victor( SIDECAR_2, BL_PIVOT );
    public static AngleSensor blSteeringEncoder = new AngleSensor( 1, BL_ENC, BL_OFFSET );
    public static AngleMotor blSteeringController  = new AngleMotor( blSteeringVictor,
                                                                    blSteeringEncoder );
    public static Encoder blVelocityEncoder        = new Encoder( SIDECAR_2, BL_ENC_A,
                                                           SIDECAR_2, BL_ENC_B, true,
                                                           CounterBase.EncodingType.k1X );
    public static PIDControlledVelocityMotor blVelocityController = 
            new PIDControlledVelocityMotor(blSteeringVictor, blVelocityEncoder);

    
    // back-right position control
    public static SpeedController brSteeringVictor = new Victor( SIDECAR_1, BR_PIVOT );
    public static AngleSensor brSteeringEncoder = new AngleSensor( 1, BR_ENC, BR_OFFSET );
    public static AngleMotor brSteeringController  = new AngleMotor( brSteeringVictor,
                                                                    brSteeringEncoder );
    public static Encoder brVelocityEncoder        = new Encoder( SIDECAR_1, BR_ENC_A,
                                                           SIDECAR_1, BR_ENC_B, false,
                                                           CounterBase.EncodingType.k1X );
    public static PIDControlledVelocityMotor brVelocityController = 
            new PIDControlledVelocityMotor(brSteeringVictor, brVelocityEncoder);


    public static  PIDControlledVelocityMotor[] meccanumMotors = 
    {
        frVelocityController,
        flVelocityController,
        blVelocityController,
        brVelocityController
    };
    // End drive components-----------------------------------------------------

    // Shooter and turret components--------------------------------------------

    // shooter components
    public static SpeedController shooterRPMVictorR = new Jaguar( SIDECAR_1,
                                                                  SHOOTER_RPM_R );
    public static SpeedController shooterRPMVictorL = new Jaguar( SIDECAR_1,
                                                                  SHOOTER_RPM_L );
    
    //double speed controller allows us to control both motors with a single
    //  implements SpeedController class
    public static DoubleSpeedController shooterDoubleMotor =
        new DoubleSpeedController( shooterRPMVictorR, shooterRPMVictorL, false, true );

    public static CounterBase shooterRPMEncoder = new Counter();
    public static RPMMotor turretShooterMotor   = new RPMMotor( shooterDoubleMotor,
                                                              shooterRPMEncoder );

    // turret components
    public static SpeedController turretRotateVictor = new Victor( SIDECAR_2,
                                                                   TURRET_TURN );

    /*
     * removed due to wiring difficulties
     * public static DigitalInput turretLimitRight = new DigitalInput( SIDECAR_2,
     *                                                               TURRET_RIGHT_LIMIT );
     * public static DigitalInput turretLimitLeft = new DigitalInput( SIDECAR_2,
     *                                                              TURRET_LEFT_LIMIT );
     */
    // End shooter and turret components----------------------------------------

    // manipulation objects-----------------------------------------------------

    public static Relay feederSpike = new Relay( SIDECAR_1, FEEDER_SPIKE );


    public static Relay rampSpikeR = new Relay( SIDECAR_1, RAMP_SPIKE_R );
    public static Relay rampSpikeL = new Relay( SIDECAR_1, RAMP_SPIKE_L );

    
    // public static Relay convayorSpike = new Relay( SIDECAR_2, TRANSFER_SPIKE_2 );
    public static SpeedController intakeAndConvayorSpike =
        new Victor( SIDECAR_2, INTAKE_AND_TRANSFER_VICTOR );

    // public static DigitalInput intakeLimit2    = new DigitalInput( SIDECAR_2,
    // INTAKE_LIMIT_2 );
    // public static DigitalInput intakeLimit1 = new DigitalInput( SIDECAR_2,
    // INTAKE_LIMIT_1 );

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

    static
    {
        // DRIVE_LOCATION               watch the +/- !
        // swerve drive tuning
        // meccanum drive tuning
        
        // FRONT RIGHT               //-DEFAULT_SCALE * 0.6, 126, 3.2, 1700 ) );
        frSteeringController.setPID( new PID(0,0,0,0));
        frVelocityController.setPID( new PID( DEFAULT_SCALE * 2, 1.5, 0, 300.0 ) );
        
        // FRONT LEFT                //-DEFAULT_SCALE * 0.6, 120, 3, 1600 ) );
        flSteeringController.setPID( new PID(0,0,0,0));
        flVelocityController.setPID( new PID( DEFAULT_SCALE * -2, 1.5, 0, 300.0 ) );
        
        // BACK LEFT                 //-DEFAULT_SCALE * 0.6, 132, 3.4, 1800 ) );    
        blSteeringController.setPID( new PID(0,0,0,0));
        blVelocityController.setPID( new PID( DEFAULT_SCALE * -2, 4, 0.00, 30.0 ) );
        
        // BACK RIGHT                //DEFAULT_SCALE* 0.6, 125, 3.2, 1900 ) );
        brSteeringController.setPID( new PID(0,0,0,0)); 
        brVelocityController.setPID( new PID( DEFAULT_SCALE *  2, 4, 0.00, 30.0 ) );
    }
    // end drive PID calibration-------------------------------------------------
    
    //now create all the sub system components.
    public static Drive driveSystem              = new MeccanumDrive();
                                                   //new SwerveDrive(); // new TankDrive();
    public static AutoTarget autoTarget          = new AutoTarget();
    public static IntakeAndConveyor intakeSystem = new IntakeAndConveyor();
    public static RampArticulate armSystem       = new RampArticulate( false, true );
    public static Turret turret                  = new Turret();
}

//FIRST FRC team 691 2012 competition code
