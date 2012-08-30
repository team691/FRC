package Team691.DriveSystems.Tank;


import Team691.Util.Time;
import Team691.Util.Mathf;
import Team691.DriveSystems.Base.*;
import edu.wpi.first.wpilibj.SpeedController;
/**
 * Controls a simple 2 wheel tank drive system.
 * @author Gerard
 */
public class TankDrive implements Drive
{
    //////////////////////////DECLARE VARIABLES/////////////////////////////////
        protected double leftMagnitude = 0;
        protected double rightMagnitude = 0;

        //used for smoothing the drive------------------------------------------
        /**
         * If false, the class will change the speed of the wheels instantly,
         * possibly resulting in sudden jerks with large changes in input and may
         * damage the motors if too much jerking occurs.
         * If true, this class will try to change the speed of the wheels smoothly,
         * but the robot will be less responsive.
         */
        protected boolean shouldDriveBeSmoothedOverTime = false;
        /**
         * Affects how long to takes for the drive to reach it's new destination
         * speed. It should take <code>1 / smoothingFactor</code> seconds to reach
         * the new value.
         */
        protected double smoothingFactor = 8;
        protected double lastLeftMagnitude = 0;
        protected double lastRightMagnitude = 0;
    /////////////////////////MOVING PARTS///////////////////////////////////////
        //Speed Controllers-----------------------------------------------------
            protected SpeedController  leftWheel;
            protected SpeedController rightWheel;   
    /////////////////////////END MOVING PARTS///////////////////////////////////
            
    /////////////////////////END DECLARE VARIABLES//////////////////////////////

    /**
     * Constructor. Creates a TankDrive object to interface between user input
     * and the hardware.
     * @param leftWheel The SpeedController connected to the left wheel.
     * @param rightWheel The SpeedController connected to the right wheel.
     */
    public TankDrive(SpeedController leftWheel, SpeedController rightWheel)
    {
        this.leftWheel = leftWheel;
        this.rightWheel = rightWheel;
    }

    public void update(double X, double Y, double turn)
    {
        update(X, Y);
    }
    /**
     * Takes the values read from the user input
     * @param forwardAxis
     * @param turningAxis
     */
    public void update(double forwardAxis, double turningAxis)
    {
        //calculate how much each motor should move
        leftMagnitude = forwardAxis + turningAxis;
        rightMagnitude = forwardAxis - turningAxis;

        //the speed controllers only accept values inside [-1, 1], so we need
        //      to reduce our input to inside that range, but clamping would
        //      create uneven driving, so we need to scale down all values
        //      realative to the maximum.
        double max = Math.abs(leftMagnitude);
        if(Math.abs(rightMagnitude) > max)
            max = Math.abs(rightMagnitude);
        if(max > 1)
        {
            leftMagnitude /= max;
            rightMagnitude /= max;
        }

        //Handle smooth change over time
        if(shouldDriveBeSmoothedOverTime)
        {
            leftMagnitude  = Mathf.lerp( lastLeftMagnitude,  leftMagnitude, Time.deltaTime()* smoothingFactor);
            rightMagnitude = Mathf.lerp(lastRightMagnitude, rightMagnitude, Time.deltaTime()* smoothingFactor);
        }

        //we are done calculating, tell the motors how much to move.
        leftWheel.set ( leftMagnitude  );
        rightWheel.set( rightMagnitude );

        //store values for smooth over time use
        lastLeftMagnitude  = leftMagnitude;
        lastRightMagnitude = rightMagnitude;
    }

    public void stop()
    {
        leftWheel.set(0);
        rightWheel.set(0);
    }
}
