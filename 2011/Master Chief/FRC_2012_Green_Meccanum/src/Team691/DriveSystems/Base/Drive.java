package Team691.DriveSystems.Base;

/**
 *
 * @author Gerard
 */
public interface Drive
{
    /**
     * Gives command flow to the drive system, and passes in this cycle's user
     * input. Perform all the drive calculations and motor movement in here.
     * Call from the main method every cycle.
     * @param forwardAxis The axis, aquired from a joystick input, corrosponding
     * to the user's desire to move directly forward.
     * @param strafingAxis The axis, aquired from a joystick input, corrosonding
     * to the user's desire to move directly sideways. Some drive systems, such
     * as tank drive, cannont use this value. In this case, pass in 0, and ignore
     * the value inside the method.
     * @param turningAxis The axis, aquired from a joystick input, corrosoponding
     * to the user's desire to turn in place.
     */
    public void update(double forwardAxis, double strafingAxis, double turningAxis);
    /**
     * Tells the drive system to come to a complete stop.
     */
    public void stop();
}
