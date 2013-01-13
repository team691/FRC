package org.usfirst.frc691;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Watchdog;

public class Robot extends SimpleRobot {
	
	public static final int sidecar = 1;
	
	Joystick joy = new Joystick(2);
	
	Victor rDrive = new Victor(sidecar, 2);
	Victor lDrive = new Victor(sidecar, 1);
	
	Victor shooterRVic = new Victor(sidecar, 5);
	Victor shooterLVic = new Victor(sidecar, 6);
        
        Relay feederSpike = new Relay(sidecar, 8);
        
        Victor uptake = new Victor(sidecar, 3);
        
        Victor turntable = new Victor(sidecar, 4);
        
        Relay armLeft = new Relay(sidecar, 3);
        Relay armRight = new Relay(sidecar, 4);
	
	RobotDrive drive = new RobotDrive(rDrive, lDrive);
    
    public void robotInit() {
    	//Disable watchdog
    	System.out.println("robotInit() running!");
    	Watchdog.getInstance().setEnabled(false);
    }
	
    public void operatorControl() {
        while(isEnabled() && isOperatorControl()){
            drive.arcadeDrive(-joy.getRawAxis(2), joy.getRawAxis(1));

            //Shooter
            if(joy.getRawButton(1)) {
                    shooterRVic.set(1.0);
                    shooterLVic.set(-1.0);
            } else {
                    shooterRVic.set(0.0);
                    shooterLVic.set(0.0);
            }

            //Shooter Gatekeeper forward/reverse
            if(joy.getRawButton(3)) {
                    feederSpike.set(Relay.Value.kForward);
            } else if(joy.getRawButton(2)) {
                    feederSpike.set(Relay.Value.kReverse);
            } else {
                    feederSpike.set(Relay.Value.kOff);
            }

            //Uptake forward/reverse
            if(joy.getRawButton(6)) {
                    uptake.set(1.0);
            } else if(joy.getRawButton(7)) {
                    uptake.set(-1.0);
            } else {
                    uptake.set(0.0);
            }

            //Turntable left/right
            if(joy.getRawButton(4)) {
                    turntable.set(0.5);
            } else if(joy.getRawButton(5)) {
                    turntable.set(-0.5);
            } else {
                    turntable.set(0.0);
            }

            //Arm up/down
            if(joy.getRawButton(11)) {
                    armLeft.set(Relay.Value.kForward);
                    armRight.set(Relay.Value.kReverse);
            } else if(joy.getRawButton(10)) {
                    armLeft.set(Relay.Value.kReverse);
                    armRight.set(Relay.Value.kForward);
            } else {
                    armLeft.set(Relay.Value.kOff);
                    armRight.set(Relay.Value.kOff);
            }
        }
    }
}

/*
* X: drive
* Y: drive
* Z: shooter power
* twist: turret
* Dpad: uptake
* shooter:2
* feeder: 1
*/