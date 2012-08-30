package Team691.DriveSystems.Base;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Encoder;
import Team691.Util.Mathf;
import Team691.Util.PID;
import Team691.Util.Time;
/**
 *
 * @author Gerard
 */
public class PIDControlledVelocityMotor
{
    ////////////////////////////////DECLARE VARIABLES///////////////////////////
    //--------------------CONSTANTS---------------------------------------------
        public static final double DEFAULT_ENCODER_CLICKS_PER_TURN = 360;
        public static final double DEFAULT_MAX_TURNS_PER_SECOND = 14;
        public static final double DEFAULT_MAX_CLICKS_PER_SECOND =
                DEFAULT_ENCODER_CLICKS_PER_TURN * DEFAULT_MAX_TURNS_PER_SECOND;

        public static final double DEFAULT_ENCODER_OUTPUT_SCALE = 1;
        public static final double DEFAULT_PID_SCALE = 0.0001;
        public static final double DEFAULT_KP = 0.1;
        public static final double DEFAULT_KI = 0.0;
        public static final double DEFAULT_KD = 26.0;
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
        protected double deltaTime = Time.time() - lastTime;
        protected double timeInterval = 1.0/DEFAULT_UPDATES_PER_SECOND;

    //$$$$$$$$$$$$$$$$$$$$Encoder variables$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        protected double encoderOutputScale = DEFAULT_ENCODER_OUTPUT_SCALE;
        protected double lastEncoderCount = 0;//last cycle's encoder count
        protected double encoderCount = 0;    //this cycle's encoder count
        protected double deltaEncoderCount = 0; //change in count since last check
        protected double currentVelocity = 0;   //messured in clicks/second

    //!!!!!!!!!!!!!!!!!!!!Instance Settings and variables!!!!!!!!!!!!!!!!!!!!!!!
        protected boolean enabled = true;
        protected double targetVelocity = 0;
        protected double maximumVelocity = DEFAULT_MAX_CLICKS_PER_SECOND;

        protected boolean shouldDisablePIDWhileTargetVelocityIsZero = true;
        protected boolean isDebugMode = false;


    ////////////////////////////////END DECLARE VARIABLES///////////////////////

        
    public PIDControlledVelocityMotor(SpeedController mtrIn, Encoder encIn)
    {   
        this.mtr = mtrIn;
        this.encode = encIn;
        resetEncoder();
    }

    public PIDControlledVelocityMotor
            (SpeedController mtrIn, Encoder encIn, double encoderOutputScaleIn)
    {
        this(mtrIn, encIn);
        this.encoderOutputScale = encoderOutputScaleIn;
    }
    public PIDControlledVelocityMotor
            (SpeedController mtrIn, Encoder encIn, double encoderOutputScaleIn,
            double PIDOutputScalerIn, double KPIn, double KIIn, double KDIn)
    {
        this(mtrIn, encIn, encoderOutputScaleIn);
        this.pidController = new PID(PIDOutputScalerIn, KPIn, KIIn, KDIn);

    }

    /**
     * Gives command flow to the motor so that it determine how much it needs to
     * control the speed controller to achieve the target velocity.
     * @return The current speed the speed controller is set to.
     */
    public double update()
    {
        if(!enabled)
            return mtr.get();
        //too little time has passed, get out.
        if(Mathf.abs(Time.time() - lastTime) < timeInterval)
            return mtr.get();

        //We're ok time wise, continue
        //Because we skip cycles, and because Time.deltaTime() assumes we are
        //     operating every single cycle, we have to create a local delta time.
        deltaTime = Time.time() - lastTime;

        //updates all of the encoder varibles internaly
        _updateEncoderSpeed(  deltaTime  );

        pidOutput = pidController.calc(  targetVelocity, currentVelocity  );

        if(   shouldDisablePIDWhileTargetVelocityIsZero
              && Mathf.approximately(targetVelocity, 0, MTR_POWER_ERROR*10)   )
            setSpeedController(0);
        else
            adjustSpeedController(pidOutput);


        lastTime = Time.time();
        if(isDebugMode)
            System.out.println(  this.toString()  );
        return mtr.get();
    }

    /**
     * Checks the attached encoder to see how much it has changed
     * @param changeInTime How much time has passed, in seconds
     * @return
     */
    protected void _updateEncoderSpeed(final double changeInTime  )
    {
        if(  Mathf.approximately(changeInTime,0)  )
            throw new ArithmeticException("Division by 0!");
        lastEncoderCount = encoderCount;
        encoderCount = encode.get() * encoderOutputScale;
        deltaEncoderCount = encoderCount - lastEncoderCount;

        //scale up to seconds
          currentVelocity = (deltaEncoderCount / changeInTime);
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

    public double getEncoderSpeed()
    {   return currentVelocity;  }

    public void enable()
    {   this.enabled = true;    }

    public void disable()
    {   this.enabled = false;   }

    public void setTargetVelocity(double input)
    {
        input = bindInput(input);
        targetVelocity = input * maximumVelocity;
        enable();
    }

    public void setNumberOfUpdatesPerSecond(double input)
    {
        if(Mathf.approximately(input, 0))
            return;
        if(input > 1000.0)
            return;
        timeInterval = 1.0/input;
    }
    
    public double getMaxVelocity()
    {   return maximumVelocity;     }

    public double setMaxVelocity(final double input)
    {   return maximumVelocity = input;    }

    public double adjustMaxVelocity(final double input)
    {   return setMaxVelocity(  getMaxVelocity() + input  );    }

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

    public String toString()
    {
        return ("------------------------------------------------\n"+
                "Encoder Location: "+ encoderCount   +"/n"+
                "Current Velociy: " + currentVelocity+"/n"+
                "Target Velocity: " + targetVelocity +"\n"+
                "Speed Controller Setting: "+getSpeedControllerSetting()+"\n"+
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
