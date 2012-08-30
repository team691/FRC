package Team691.DriveSystems.Base;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Encoder;
import Team691.Util.*;
/**
 *
 * @author Gerard
 */
public class PIDControlledPositionMotor
{
    ////////////////////////////////DECLARE VARIABLES///////////////////////////
    //--------------------CONSTANTS---------------------------------------------
        public static final double DEFAULT_ENCODER_CLICKS_PER_TURN = 360;

        public static final double DEFAULT_ENCODER_OUTPUT_SCALE = 1;
        public static final double DEFAULT_PID_SCALE = 0.0001;
        public static final double DEFAULT_KP = 1.0;
        public static final double DEFAULT_KI = 0.0;
        public static final double DEFAULT_KD = 2.0;
        /**
         * The motor will wait until (1.0/thisValue) seconds have passed before trying
         * to update it's values. This is because if the change in time is too
         * small, then the motor will start behaving erraticly because the error
         * in the system increases asymtoticly as the change in time aproaches 0.
         */
        public static final double DEFAULT_UPDATES_PER_SECOND = 50.0;

        public static final double MAX_MTR_POWER =  1.0;
        public static final double MIN_MTR_POWER = -1.0;
        /**
         * When setting the power that the motor is moving at, values closer than
         * this to 0, or the max and min values will be set = to the max/min value.
         */
        public static final double MTR_POWER_ERROR = 0.01;//1%

    //********************Moving Parts******************************************
        protected SpeedController mtr;
        protected Encoder encode;

    //####################Logic Objects#########################################
        protected PID pidController = new PID
                (DEFAULT_PID_SCALE, DEFAULT_KP, DEFAULT_KI, DEFAULT_KD);
        protected double pidOutput = 0;

    //%%%%%%%%%%%%%%%%%%%%Time variables%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        protected double lastTime = 0.0;
        protected double timeInterval = 1.0/DEFAULT_UPDATES_PER_SECOND;

    //$$$$$$$$$$$$$$$$$$$$Encoder variables$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        protected double encoderOutputScale = DEFAULT_ENCODER_OUTPUT_SCALE;
        protected double lastEncoderCount = 0;//last cycle's encoder count
        protected double encoderCount = 0;    //this cycle's encoder count
        protected Angle currentPosition = Angle.zero();   //messured in degrees
        protected Angle targetPosition = Angle.zero();

    //!!!!!!!!!!!!!!!!!!!!Instance Settings and variables!!!!!!!!!!!!!!!!!!!!!!!
        protected boolean enabled = true;
        protected boolean allowMovingToTargetAnglePlus180IfIsCloser = false;

        protected boolean shouldDisablePIDWhileCloseToTarget = false;
        protected boolean isDebugMode = false;


    ////////////////////////////////END DECLARE VARIABLES///////////////////////


    public PIDControlledPositionMotor(SpeedController mtrIn, Encoder encIn)
    {
        this.mtr = mtrIn;
        this.encode = encIn;
    }

    public PIDControlledPositionMotor
            (SpeedController mtrIn, Encoder encIn, double encoderOutputScaleIn)
    {
        this(mtrIn, encIn);
        this.encoderOutputScale = encoderOutputScaleIn;
    }
    public PIDControlledPositionMotor
            (SpeedController mtrIn, Encoder encIn, double encoderOutputScaleIn,
            PID pidObjectIn)
    {
        this(mtrIn, encIn, encoderOutputScaleIn);
        this.pidController = pidObjectIn;
    }

    public PIDControlledPositionMotor
            (SpeedController mtrIn, Encoder encIn, double encoderOutputScaleIn,
            double PIDOutputScalerIn, double KPIn, double KIIn, double KDIn)
    {
        this(mtrIn, encIn, encoderOutputScaleIn,
            new PID(PIDOutputScalerIn, KPIn, KIIn, KDIn));
    }

    /**
     * Gives command flow to the motor so that it determine how much it needs to
     * control the speed controller to achieve the target velocity.
     * @return The cosine of the target position - the current position. This is
     * the amount to scale the velocity motor's output amount in a swerve drive
     * setup.
     */
    public double update()
    {
        if(!enabled)
            return getCosineOfErrorValue();
        //too little time has passed, get out.
        if(Time.time() - lastTime < timeInterval)
            return getCosineOfErrorValue();

        //updates all of the encoder varibles internaly
        _updateEncoderPosition();

        double dist = currentPosition.distanceTo(targetPosition);

        if(allowMovingToTargetAnglePlus180IfIsCloser && Mathf.abs(dist) > 90)
            pidOutput = pidController.calc(  180 - dist);
        else
            pidOutput = pidController.calc(  dist  );

        if(   shouldDisablePIDWhileCloseToTarget)
        {
            if(currentPosition.equals(targetPosition))
                setSpeedController(0);
            else if (allowMovingToTargetAnglePlus180IfIsCloser)
                 if (currentPosition.equals(targetPosition.plus180()))
                     setSpeedController(0);
        }
        else
            setSpeedController(pidOutput);


        lastTime = Time.time();
        if(isDebugMode)
            System.out.println(  this.toString()  );
        return getCosineOfErrorValue();
    }

    public double getCosineOfErrorValue()
    {
        return Mathf.cos
               (Mathf.DEG2RAD * (targetPosition.get() - currentPosition.get()));
    }

    protected void _updateEncoderPosition()
    {
        lastEncoderCount = encoderCount;
        encoderCount = encode.get() * encoderOutputScale;
        currentPosition = new Angle(encoderCount);
    }

    protected double adjustSpeedController(final double input)
    {   return setSpeedController(  input + mtr.get() );    }

    protected double setSpeedController(double input)
    {
        input = bindInput(input);
        mtr.set(input);
        return mtr.get();
    }

    public double getSpeedControllerSetting()
    {   return mtr.get();   }

    public void resetEncoder()
    {
        encode.start();
        encode.reset();
        encode.setDistancePerPulse(1);
        //encode.start();

        //4 minutes until encoder self stops - longer than competition
        encode.setMaxPeriod(240);
        //a single click reset stop countdown
        encode.setMinRate(0);
    }

    public void enable()
    {   this.enabled = true;    }

    public void disable()
    {   this.enabled = false;   }

    public void setTargetPosition(double input)
    {
        setTargetPosition(  new Angle(input)  );
    }
    
    public void setTargetPosition(Angle input)
    {
        targetPosition = input;
        enable();
    }

    public Angle getTargetPosition()
    {   return targetPosition.clone();  }

    public Angle getCurrentPosition()
    {   return currentPosition.clone(); }

    public void setNumberOfUpdatesPerSecond(double input)
    {
        if(Mathf.approximately(input, 0))
            return;
        if(input > 1000.0)
            return;
        timeInterval = 1.0/input;
    }

    public void overwritePIDControl(double input)
    {
        input = bindInput(input);
        mtr.set(input);
        disable();
    }

    public void setDebugMode(final boolean input)
    {   isDebugMode = input;     }

    public PID getPIDObject()
    {   return pidController;   }

    public void setPIDObject(PID input)
    {   pidController = input;  }

    public void setAllowMovingToTargetAnglePlus180IfIsCloser(boolean input)
    {   allowMovingToTargetAnglePlus180IfIsCloser = input;  }

    public void setShouldDisablePIDWhileCloseToTarget(boolean input)
    {
        shouldDisablePIDWhileCloseToTarget = input;
    }

    public String toString()
    {
        return ("------------------------------------------------\n"+
                "Encoder Location: "+ encoderCount                      +"\n"+
                "Current Position: " + currentPosition.toString()       +"\n"+
                "Target Position: " + targetPosition.toString()         +"\n"+
                "Speed Controller Setting: "+getSpeedControllerSetting()+"\n"+
                "Cosine of Error Valuez; "+getCosineOfErrorValue()      +"\n"+
                "KP: "+pidController.getKP() +"\n"+
                "KI: "+pidController.getKI() +"\n"+
                "KD: "+pidController.getKD() +"\n"+
                "Last PID Output: "+pidOutput+"\n"+
                "------------------------------------------------");
    }

    public static double bindInput(final double input)
    {
            if(Mathf.approximately(input, 0, MTR_POWER_ERROR))
            return 0;
        else if(Mathf.approximately(input, MAX_MTR_POWER, MTR_POWER_ERROR))
            return MAX_MTR_POWER;
        else if(Mathf.approximately(input, MIN_MTR_POWER, MTR_POWER_ERROR))
            return MIN_MTR_POWER;
        else
            return Mathf.clamp(input, MIN_MTR_POWER, MAX_MTR_POWER);
    }
}