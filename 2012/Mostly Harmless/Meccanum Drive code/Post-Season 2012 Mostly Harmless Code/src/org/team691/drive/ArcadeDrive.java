package org.team691.drive;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * Drive system with one joystick where the x-axis is used to rotate the robot and
 * the y-axis is used to move forward and backward. For strafing, use the
 * {@link MeccanumDrive} system.
 * 
 * @author Akira
 */
public class ArcadeDrive implements Drive{
    
    private SpeedController rightDrive;
    private SpeedController leftDrive;
    
    /**
     * Value to hold if the Arcade Drive system is enabled.
     */
    private boolean enabled = true;

    /**
     * Constructor for the Arcade Drive system.
     * @param rightMotor    The {@link SpeedController} used for the right side.
     * @param leftMotor     The {@link SpeedController} used for the left side.
     */
    public ArcadeDrive(SpeedController rightMotor, SpeedController leftMotor){
        rightDrive = rightMotor;
        leftDrive = leftMotor;
        enabled = true;
    }
    
    /**
     * Update the desired direction. All values should come from the same joystick.
     * The z-axis will never be used because there is no strafing enabled. Strafing
     * is available with the {@link MeccanumDrive} system.
     * @param X     The value from the x-axis of the joystick. Forward and backward
     *              movement.
     * @param Y     The value from the y-axis of the joystick. Turning left and
     *              right movement.
     * @param Z     Never used.
     */
    public void update(double X, double Y, double Z) {
        if(enabled){
            rightDrive.set(X - Y);
            leftDrive.set(X + Y);
        }
    }

    /**
     * Tell the robot to stop.
     */
    public void stop() {
        update(0,0,0);
    }

    /**
     * Disable the robot. The robot must be re-enabled to move again.
     */
    public void disable() {
        enabled = false;
    }

    /**
     * Enable the robot.
     */
    public void enable() {
        enabled = true;
    }
    
}
