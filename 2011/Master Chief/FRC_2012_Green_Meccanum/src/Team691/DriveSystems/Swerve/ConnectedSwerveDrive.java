package Team691.DriveSystems.Swerve;
import Team691.DriveSystems.Base.*;
import Team691.Util.Mathf;
import Team691.Util.Angle;
import Team691.Util.Time;
/**
 * Controls a 4 wheel swerve drive system where each wheel has two motors and an
 * encoder: one motor controls the direction the wheel is facing, one motor
 * controls the speed that the wheel spins, and the encoder is used to accurately
 * determine the current direction that the wheel is facing.
 * @author Gerard
 */
public class ConnectedSwerveDrive implements Drive
{
    /*  II|I
    // ---+---
    // III|IV*/
    protected SwerveWheel[] wheels = new SwerveWheel[4];
    protected PIDControlledVelocityMotor leftVelocityMtr;
    protected PIDControlledVelocityMotor rightVelocityMtr;
    public ConnectedSwerveDrive
    (PIDControlledPositionMotor[] posMtrs, PIDControlledVelocityMotor[] velMtrs)
    {
        for(int count = 0; count <= 3; count++)
            wheels[count] = new SwerveWheel(posMtrs[count], count);
        leftVelocityMtr = velMtrs[0];
        leftVelocityMtr = velMtrs[1];
    }
    /**
     * Input values less than this are ignored.
     */
    public static final double INPUT_ERROR = 0.05;

    protected double leftDriveMagnitude = 0;
    protected double lastLeftDriveMagnitude = 0;
    protected double lastLastLeftDriveMagnitude = 0;

    protected double rightDriveMagnitude = 0;
    protected double lastRightDriveMagnitude = 0;
    protected double lastLastRightDriveMagnitude = 0;

    protected Angle targetPos = Angle.half();
    protected double targetMagnitude = 0;
    
    protected boolean shouldSmoothInputOverTime = false;
    protected double smoothingFactor = 8.0;
    protected boolean shouldMoveToDefaultPositionWhenAllInputIsZero = false;
    public static final Angle DEFAULT_POSITION = Angle.half();
    protected boolean shouldKillPIDWhenAllInputIsZero = true;
    

    public void update(double strafingAxis, double forwardAxis, double turningAxis)
    {                       //X                  //Y                   //twist
        
        //verify input is valid
        strafingAxis = Mathf.clamp11(strafingAxis);
        forwardAxis  = Mathf.clamp11(forwardAxis);
        turningAxis  = Mathf.clamp11(turningAxis);
        //all input has been moved to safe range.

        //now check for input values less than 5%. These values are set = to 0.
        if(Mathf.approximately(strafingAxis, 0, INPUT_ERROR)) strafingAxis = 0;
        if(Mathf.approximately(forwardAxis, 0, INPUT_ERROR)) forwardAxis = 0;
        if(Mathf.approximately(turningAxis, 0, INPUT_ERROR)) turningAxis = 0;
        //done checking input.
        
        if( strafingAxis == 0 && forwardAxis == 0 && turningAxis == 0)
        {
            if(shouldMoveToDefaultPositionWhenAllInputIsZero )
                targetPos = DEFAULT_POSITION;
            else if(shouldKillPIDWhenAllInputIsZero)
                targetPos = targetPos;
        }
        else
        {
            //take our X,Y values and turn them into an Angle
            double tempVal = Mathf.atan2(forwardAxis, strafingAxis);
            tempVal *= Mathf.RAD2DEG;
            Angle tempAngle = new Angle(tempVal);
        
            if(shouldSmoothInputOverTime)
                targetPos = targetPos.lerp(tempAngle, Time.deltaTime() * smoothingFactor);
        }
        //find the magnitude of the input vectors
        if(turningAxis == 0)
            targetMagnitude = Mathf.mag(strafingAxis, forwardAxis);
        else
            targetMagnitude = ( (Mathf.mag(strafingAxis, forwardAxis) + turningAxis) / 2);
        targetMagnitude = Mathf.clamp11(targetMagnitude);

        //all input is now valid. hand the data off to the wheels so they can
        //      perform their individual calculations.
        //right-----------------------------------------------------------------
        rightDriveMagnitude =  wheels[0].update(targetPos, turningAxis);
        rightDriveMagnitude += wheels[3].update(targetPos, turningAxis);
        rightDriveMagnitude /= 2;//find the average
        //and multiply by how much we want to move
        rightDriveMagnitude *= targetMagnitude;
        
        //left------------------------------------------------------------------
        leftDriveMagnitude =  wheels[1].update(targetPos, turningAxis);
        leftDriveMagnitude += wheels[2].update(targetPos, turningAxis);
        leftDriveMagnitude /= 2;//find the average
        //and multiply by how much we want to move
        leftDriveMagnitude *= targetMagnitude;
        
        
        //now pass the angle values from the wheels into the motors.
        rightVelocityMtr.setTargetVelocity(rightDriveMagnitude);
        rightVelocityMtr.update();
        leftVelocityMtr.setTargetVelocity(leftDriveMagnitude);
        leftVelocityMtr.update();
        

        //store the values of the last two cycles.
        lastLastRightDriveMagnitude = lastRightDriveMagnitude;
        lastRightDriveMagnitude = rightDriveMagnitude;
        
        lastLastLeftDriveMagnitude = lastLeftDriveMagnitude;
        lastLeftDriveMagnitude = leftDriveMagnitude;
    }

    public void stop()
    {
        update(0,0,0);
    }
}