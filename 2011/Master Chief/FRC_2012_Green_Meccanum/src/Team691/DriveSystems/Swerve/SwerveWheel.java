package Team691.DriveSystems.Swerve;
import Team691.DriveSystems.Base.*;
import Team691.Util.Angle;
import Team691.Util.Mathf;

/**
 *
 * @author Gerard
 */
public class SwerveWheel
{
    //     /   90     \
    //    #1   |    #0 wheelNum
    //     180---0 degrees
    //    #2   |    #3
    //    \   270     /
    protected int wheelNum = 0;
    protected Angle turnAngle = Angle.threeFourths();
    public static Angle forward = Angle.half();
    public static Angle right = Angle.zero();
    protected PIDControlledPositionMotor mtr;
    protected Angle targetAngle = new Angle(forward);

    public SwerveWheel(PIDControlledPositionMotor motorIn, int wheelNumIn)
    {
        mtr = motorIn;
        wheelNum = wheelNumIn;
        switch(wheelNum)
        {
            case 0: turnAngle = Angle.threeFourths(); break;
            case 1: turnAngle = Angle.threeFourths(-1); break;
            case 2: turnAngle = Angle.quarter(-1); break;
            case 3: turnAngle = Angle.quarter(); break;
        }
    }

    public double update()
    {
        mtr.setTargetPosition(targetAngle);
        return mtr.update();
    }

    public double update(Angle newTargetAngle, double turningAmount)
    {   setTargetAngle(newTargetAngle); return update(turningAmount);   }

    public double update(double turningAmount)
    {
        mtr.setTargetPosition(targetAngle.lerp(turnAngle, turningAmount));
        return mtr.update();
    }

    public void setTargetAngle(Angle input)
    {   targetAngle = input;    }

    public Angle getTargetAngle()
    {   return targetAngle.clone();  }

    public PIDControlledPositionMotor getMotor()
    {   return mtr; }

    public void setMotor(PIDControlledPositionMotor input)
    {   mtr = input;    }
    
    
    public double getCosineOfErrorValue()
    {   return mtr.getCosineOfErrorValue(); }
}
