/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package RobotDependantCode;


import Team691.DriveSystems.Base.*;
import Team691.Util.*;
import Team691.DriveSystems.Meccanum.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;

import com.sun.squawk.util.MathUtils;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotMain extends SimpleRobot
{

    //VARIABLES-----------------------------------------------------------------
        double LX,LY,RX,CX,CY;

        int cycles = 0;
        long ctime = System.currentTimeMillis();
		
		
		//                  0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16  17 18 19 20 21 22
        int[] channelNum = {8,4,1,5,3,1,2,5,6,3,4 ,7 ,8 ,2 ,7, 6, 10, 9, 6, 4, 5, 8, 13};
        /*
         * For channelNum array:
         * ARRAY POSITION |     OBJECT      | OBJECT TYPE      | cRIO slot
         * -----------------------------------------------------------------
         *       0        |     FRWheel     | SpeedController  |     4
         *       1        |     FLWheel     | SpeedController  |     4
         *       2        |     BLWheel     | SpeedController  |     4
         *       3        |     BRWheel     | SpeedController  |     4
         *       4        |  ForkliftMotor  | SpeedController  |     4
         *       5        |   FREncoderA  1 | Encoder          |     4
         *       6        |   FREncoderB  2 | Encoder          |     4
         *       7        |   FLEncoderA  5 | Encoder          |     4
         *       8        |   FLEncoderB  6 | Encoder          |     4
         *       9        |   BREncoderA  3 | Encoder          |     4
         *      10        |   BREncoderB  4 | Encoder          |     4
         *      11        |   BLEncoderA  7 | Encoder          |     4
         *      12        |   BLEncoderB  8 | Encoder          |     4
         *      13        | MiniBot Deploy  | SpeedController  |     4
         *      14        | GripperLimSwitch| DigitalInput     |     6
         *      15        |   Auto switch   | DigitalInput     |     6
         *      16        |   CameraVert    | Servo            |     6
         *      17        |   CameraHoriz   | Servo            |     6
         *      18        |  GripperMotor   | Victor           |     4
         *      19        | Forklift EncA   | Encoder          |     6
         *      20        | Forklift EncB   | Encoder          |     6
         *      21        |  TopLimSwitch   | DigitalInput     |     6
         *      22        | BottomLimSwitch | DigitalInput     |     6
         */

    //JOYSTICKS-----------------------------------------------------------------
        SimpleHID rightStick = new SimpleHID(1);
        SimpleHID gamepad = new SimpleHID(2);
        SimpleHID leftStick = new SimpleHID(3);

    //MOTOR CONTROLLERS---------------------------------------------------------
        Victor ForkliftUpDown = new Victor(4,channelNum[4]);
        Victor Gripper        = new Victor(4,channelNum[18]);
        Victor Deployer       = new Victor(4,channelNum[13]);

        Servo CameraHoriz = new Servo     (6,channelNum[17]);
        Servo CameraVert = new Servo      (6,channelNum[16]);


        SpeedController FRJag = new Jaguar(4,channelNum[0]);
        SpeedController FLJag = new Victor(4,channelNum[1]);
        SpeedController BLJag = new Jaguar(4,channelNum[2]);
        SpeedController BRJag = new Jaguar(4,channelNum[3]);

    //ENCODERS------------------------------------------------------------------
    //These encoders have 1440 clicks/revolution
        Encoder FREncoder = new Encoder(4,channelNum[5],4,channelNum[6],false,
                CounterBase.EncodingType.k1X);
        Encoder FLEncoder = new Encoder(4,channelNum[7],4,channelNum[8],false,
                CounterBase.EncodingType.k1X);
        Encoder BREncoder = new Encoder(4,channelNum[9],4,channelNum[10],false,
                CounterBase.EncodingType.k1X);
        Encoder BLEncoder = new Encoder(4,channelNum[11],4,channelNum[12],false,
                CounterBase.EncodingType.k1X);

        {
            FREncoder.start();
            FLEncoder.start();
            BLEncoder.start();
            BREncoder.start();
        }

        //This encoder has 128 clicks/revolution
        Encoder ForkliftEncoder = new Encoder(6,channelNum[19],6,channelNum[20],
                false,CounterBase.EncodingType.k1X);

    //DIGITAL INPUTS------------------------------------------------------------
        DigitalInput Bottom = new DigitalInput(6,channelNum[22]);
        DigitalInput Top = new DigitalInput(6,channelNum[21]);
        DigitalInput limGripper = new DigitalInput(6,channelNum[14]);
        DigitalInput AutoSwitch = new DigitalInput(6,channelNum[15]);

   //LOGIC OBJECTS--------------------------------------------------------------

    
        PIDControlledVelocityMotor[] velMtrs = new PIDControlledVelocityMotor[4];
        {
            velMtrs[0] = new PIDControlledVelocityMotor(FRJag, FREncoder);
            velMtrs[1] = new PIDControlledVelocityMotor(FLJag, FLEncoder);
            velMtrs[2] = new PIDControlledVelocityMotor(BLJag, BLEncoder);
            velMtrs[3] = new PIDControlledVelocityMotor(BRJag, BREncoder);
        }
        Drive drive = new MeccanumDrive(velMtrs);


        Camera camera = new Camera(CameraVert, CameraHoriz);

   //METHODS--------------------------------------------------------------------

    /**
     * This function is called once when the robot is turned on.
     */
    public void robotInit()
    {
        Time.newCycle();
        zeroAll();
        setupCamera();
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous()
    {
        zeroAll();

        //"ForkLift down for start pos - pulls command flow");
        setGripper(-0.15);
        autoLift(-1.0, .75);

        //"ForkLift up for a second - pulls command flow");
        autoLift(1.0, 1.0);
        ForkliftUpDown.set(0.1);

        //if(AutoSwitch.get())
        {
            /* //our attempt an autonomous mode.
            //drives forward
            autoDrive(-0.5, 0, 0, 2.0);

            //stops
            autoDrive(0.5,0,0, 0.1);
            drive(0,0,0);
            /**/
        }

        
        while(isEnabled() && !isOperatorControl())
        {
            if(!Time.newCycle())
                continue;
        }
    }

    /**
     * This function is called once each time the robot is disabled.
     */
    public void disabled()
    {
        System.out.println("Default disabled method running. Consider providing your own.");
        zeroAll();
        while(isDisabled())
        {
            if(!Time.newCycle())
                continue;
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl()
    {
        zeroAll();

        while (isOperatorControl() && isEnabled())
        {
            //control the drive system
            LY =  leftStick.getY ();
            LX = -rightStick.getX();
            RX = -leftStick.getX ();
            if(leftStick.getButton(2))
            {
                LY *= 0.3;
                LX *= 0.3;  
                RX *= 0.3;
            }
            
            if(Time.newCycle())
                drive(LY, LX, RX);
                
            //controls the camera
            CX = -gamepad.getRightX();
            CY = -gamepad.getRightY();
            camera.update(CX, CY); 

            adjustForklift();
            adjustGripper();
            adjustMinibot();
        }
    }


    boolean liftStay = false;
    public final static double FLIFT_IDLE_POWER = 0.1;
    /**
     * Checks all the buttons elated to the forklift and commands it to move
     * as needed.
     */
    public void adjustForklift()
    {
        //if the user overrides with both X and Y at the same time, turn off FLift
        //  being bound to the right gamepad stick
        if(gamepad.getButton(SimpleHID.BUTTON_X) && gamepad.getButton(SimpleHID.BUTTON_Y))
            liftStay = true;

        if(!liftStay)
        {
            if(Mathf.abs(gamepad.getY()) > 0.1)
            {   ForkliftUpDown.set(-gamepad.getY());  }
            else if(rightStick.getButton(2))
            {   ForkliftUpDown.set(-1); }
            else if(rightStick.getButton(3))
            {   ForkliftUpDown.set(1);  }
            else ForkliftUpDown.set( FLIFT_IDLE_POWER );
        }
        else if(rightStick.getButton(2))
        {   ForkliftUpDown.set(-1); }
        else if(rightStick.getButton(3))
        {   ForkliftUpDown.set(1);  }
        else ForkliftUpDown.set( FLIFT_IDLE_POWER );
    }

    /**
     * Checks all the buttons related to the gripper and commands it to move as
     * needed.
     */
    public void adjustGripper()
    {
        if(gamepad.getButton(SimpleHID.BUTTON_R1) || leftStick.getButton(1))
            setGripper(0.5); //out
        else if(gamepad.getButton(SimpleHID.BUTTON_R2) || rightStick.getButton(1))
            setGripper(-1); //in
        else
            setGripper(0);
    }


    /**
     * Moves the gripper as needed. Positive values spit out and negative values
     * pull in.
     * @param powerIn How much power to give the Gripper.
     */
    public void setGripper(double powerIn)
    {   Gripper.set(-powerIn);   }//electronics wired it backwards /faceplam

    /**
     * Checks all buttons related to minibot and moves it as needed.
     */
    public void adjustMinibot()
    {
        if (gamepad.getButton(SimpleHID.BUTTON_DU)||rightStick.getButton(6))
            Deployer.set(1);//out
        else if (gamepad.getButton(SimpleHID.BUTTON_DD)||rightStick.getButton(7))
            Deployer.set(-1);//in
        else Deployer.set(0);//kill. with or without fire.
    }

    /**
     * Disables watchdog, the forklift, and all motors.
     */
    public void zeroAll()
    {
        Watchdog.getInstance().setEnabled(false);
        ForkliftUpDown.set(0);
        setGripper(0);
        Deployer.set(0);
        drive(0, 0, 0);
    }

    /**
     * Shorthand for drive.update; Contains corections for mechanical issues.
     * @param forward The Forward-back axis of the joystick
     * @param right The Strafe Left-Right axis of the joystick
     * @param turnClockwise The Turn clock-counterclock axis of the joystick.
     */
    public void drive(double forward, double right, double turnClockwise)
    {   drive.update(forward, right, turnClockwise + right/10.0);    }
     //the + #/10.0 counter acts mechanical resistance in the strafing system.

    /**
     * Automatically drives the robot as if it had received user input for X seconds.
     * LOCKS DOWN CMD FLOW
     * @param Y The forward axis
     * @param X the strafing left-right axis
     * @param Z the turning axis
     * @param howLong the number of seconds to move like this
     */
    public void autoDrive(double Y, double X, double Z, double howLong)
    {

        while(howLong > 0)
        {
            drive(Y, X, Z);
            if(Time.newCycle())
            {
                howLong -= Time.deltaTime();
            }
        }
        drive(0,0,0);
    }

    /**
     * Moves the forklift over a period of time. LOCKS DOWN COMAND FLOW
     * @param power the value to set the motor to
     * @param howLong The number of seconds to move over
     */
    public void autoLift(double power, double howLong)
    {
        while(howLong > 0)
        {
            if(!Time.newCycle())
                continue;
            howLong -= Time.deltaTime();

            ForkliftUpDown.set(power);
        }
        ForkliftUpDown.set(0);

    }

    /**
     * Runs the commands of autonomous mode. WARNING: LOCKS COMAND FLOW. CALL ONLY
     * ONCE DURING AUTOMODE's START.
     */
    public void autoModeDriveCmds()
    {
        System.out.println("Have the gripper pull in slightly");
        setGripper(-0.1);
        System.out.println("ForkLift down for start pos");
        autoLift(-1.0, .5);

        System.out.println("ForkLift up for a second");
        autoLift(1.0, 1.0);
        ForkliftUpDown.set(0);

        System.out.println("Forklift up for two seconds");
        ForkliftUpDown.set(1.0);
        System.out.println("Forward for two seconds");
        autoDrive(-0.5,0,0,2.5);

        System.out.println("FullStop");
        autoDrive(0.5,0,0,0.05);
        drive(0,0,0);

        System.out.println("Forklift up for 2.125 seconds");
        autoLift(1.0, 2.125);
        ForkliftUpDown.set(0);

        System.out.println("Wait a moment");
        autoDrive(0,0,0,0.35);

        System.out.println("Forward for 0.5 seconds");
        autoDrive(-0.5,0,0,0.5);

        System.out.println("Full stop");
        autoDrive(0.5,0,0,0.05);
        drive(0,0,0);

        System.out.println("Gripper out");
        setGripper(1.0);
        autoDrive(0,0,0,0.45);

        System.out.println("Backward for 1.5 seconds");
        autoDrive(0.5,0,0,1.5);

        System.out.println("Full Stop");
        autoDrive(-0.5,0,0,0.05);
        autoDrive(0,0,0,1.5);
        setGripper(0);
    }

    /**
     * Applies all of the variables needed to setup the camera. Call only once
     * during robotInit
     */
    public void setupCamera()
    {
        AxisCamera temp = AxisCamera.getInstance();
        temp.writeCompression(0);
        temp.writeBrightness(10);
        temp.writeResolution(AxisCamera.ResolutionT.k160x120);
        temp.writeColorLevel(50);
    }
}
