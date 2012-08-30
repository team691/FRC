package Team691.DriveSystems.Meccanum;
import Team691.DriveSystems.Base.*;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Encoder;

/**
     * Contains the information unique to each of the four wheels on a standard
     * meccanum drive.
     * @author Akria
     */
    public class MeccanumWheel
    {
        PIDControlledVelocityMotor wheelMotor = null;
        int wheelNum;
        double mag = 0.0;
        double scaler = 1;

        /**
         * Creates a new MeccanumWheel Object.
         * @param motorIn The motor-encoder pair that this object controls
         * @param wheel
         */
        public MeccanumWheel(PIDControlledVelocityMotor motorIn, int wheel)
        {
            wheelMotor = motorIn;
            wheelNum = wheel;
        }

        /**
         * Constructor for the MeccanumWheel object. This method is used for
         * backwards compatability only.
         * @param channelNum Channel number of the SpeedController for the wheel
         * . (cRIO slot is 4).
         * @param enc The encoder for the wheel.
         * @param wheel The wheel number (1=Front Right, 2=Front Left, 3=Back
         * Left, 4=Back Right).
         */
        public MeccanumWheel(int channelNum, Encoder enc,int wheel)
        {
            this.wheelNum = wheel;
            Jaguar WheelJaguar = new Jaguar(4, channelNum);
            wheelMotor = new PIDControlledVelocityMotor(WheelJaguar, enc, scaler);
        }

        /**
         * Update the magnitude needed to move this wheel.
         * @param LY The value from the left joystick's Y-Axis.
         * @param LX The value from the left joystick's X-Axis.
         * @param RX The value from the right joysticks' X-Axis.
         */
        public double update(double LY,double LX,double RX)
        {
            //setMotor is called in the MeccanumDrive class after the values are
            //  modified.
            return  (  mag = calc(LY,RX,LX)  );
        }

        /**
         * Calculate the magnitude for each wheel.
         * @param F (Forward) The value from the left joystick's Y-Axis from
         * -1.0 to 1.0.
         * @param R (Right) The value from the left joystick's X-Axis from
         * -1.0 to 1.0.
         * @param C (Clockwise) The value from the right joysticks' X-Axis from
         * -1.0 to 1.0.
         * @return The magnitude the wheel's motor should have from -1.0 to 1.0.
         */
        public double calc(double F, double R, double C)
        {
            double Mag = 0;
            switch (wheelNum)
            {
                case 1: Mag = -1 *(F - R - C);   break;//front right  wheel
                case 2: Mag =     (F + R + C);   break;//front  left  wheel
                case 3: Mag =     (F - R + C);   break;//back   left  wheel
                case 4: Mag = -1* (F + R - C);   break;//back  right  wheel
            }
            if (Mag >  1) Mag =  1; //jaguars can't accept values outside of (-1,1)
            if (Mag < -1) Mag = -1;

            return Mag;
        }

        /**
         * Sets the motor speed.
         * @param FMag Final magnitude of the motor from -1.0 to 1.0.
         */
        public void setMotor(double FMag)
        {
            wheelMotor.setTargetVelocity(FMag);
            wheelMotor.update();
        }

        /**
         * Gets the magnitude.
         * @return The magnitude from -1.0 to 1.0.
         */
        public double getMagnitude()
        {
            return mag;
        }

        /**
         * Provides a way to read the encoder speed of the wheel.
         * @return The encoder speed of this wheel in a human
         * readable string.
         */
        public String toString()
        {
            return "" + wheelMotor.getEncoderSpeed();
        }
    }//end of wheel class
