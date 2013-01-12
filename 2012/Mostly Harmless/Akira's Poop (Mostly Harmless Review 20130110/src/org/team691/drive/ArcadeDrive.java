package org.team691.drive;

import edu.Objects;
import edu.wpi.first.wpilibj.SpeedController;

/**
 *
 * @author Akira Heid
 */
public class ArcadeDrive implements Drive{
    
    SpeedController rDrive = Objects.rDrive;
    SpeedController lDrive = Objects.lDrive;
    boolean enabled = true;

    public void update(double forwardAxis, double strafingAxis, double turningAxis) {
        if(enabled){
            rDrive.set(forwardAxis - turningAxis);
            lDrive.set(forwardAxis + turningAxis);
        }
    }

    public void stop() {
        update(0,0,0);
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }
}
