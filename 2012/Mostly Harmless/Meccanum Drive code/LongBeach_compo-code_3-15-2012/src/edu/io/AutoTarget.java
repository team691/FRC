
package edu.io;

import org.team691.util.PID;
import org.team691.util.Time;

/**
 * Wrapper class for the low-level networking. Allows for easy and simple interface
 * with the confusing TargetingComputerConnection class.
 * @author Michael Erbach and Gerard Boberg
 */
public class AutoTarget
{
    public static final int NOT_CONNECTED = TargetingComputerConnection.NOT_CONNECTED;
    
    public static final double PIXEL_COUNT_TO_MOTOR_POWER = 0.01;
    protected PID rotatePID = new PID(PIXEL_COUNT_TO_MOTOR_POWER, 3.35, 0, -160);

    public static final int NETWORK_UPDATES_PER_SECOND    = 8; // use 20 or 30 for final
    public static final String URL                        = "socket://10.6.91.42:20000";
    protected volatile int targetHoop                     = TOP;
    
    protected volatile double turretAngle  = 0;
    protected volatile int turretPower     = 0;
    protected volatile boolean readyToFire = false;
    protected volatile boolean newData     = false;
    protected volatile boolean lockedOn    = false;
    protected KPacket packet;
    protected TargetingComputerConnection tcc;

    public AutoTarget()
    {
        // init kinect networking class. Runs in a seperate thread
        if ( tcc != null )
            tcc.kill();

        tcc = new TargetingComputerConnection( URL, NETWORK_UPDATES_PER_SECOND, this );

        tcc.start();

        packet = tcc.getPacket();
    }
    
    public int getConnectionStatus()
    {
        return tcc.getConnectionStatus();
    }

    /**
     * I/O connections are known to block randomly. This checks up on it 
     * and replaces the dead connection as needed.
     */
    public void checkForBlockedConnection()
    {
        if ( tcc.hasBlocked() )
        {
            log( "Second thread has blocked! killing it" );

            // end the thread, or at least downsize it.
            tcc.kill();
            tcc.setPriority( Thread.MIN_PRIORITY );
            tcc.interrupt();

            log( "replacing with new thread" );

            tcc = new TargetingComputerConnection( URL, NETWORK_UPDATES_PER_SECOND,
                                                   this );

            tcc.start();

            packet = tcc.getPacket();

            log( "done" );
        }
    }

    /**
     * Returns the Object holding the data feedback from the targeting computer.
     */
    public KPacket getPacket()
    {
        return packet;
    }

    /**
     * returns true if the Kinect camera system has found a target. Does not mean
     * "ready to fire", only "I can see something".
     */
    public boolean isLockedOn()
    {
        return true;//lockedOn;
    }

    /**
     * Returns if Deep Thought has finalized a firing solution, and thinks we
     * will hit if we fire now.
     */
    public boolean isReadyToFire()
    {
        return true;//readyToFire;
    }

    /**
     * return an int representing the angle that the Kinect auto targeting
     * deems as the best angle to shoot at
     * (to be used for turret swiveling)
     */
    public double getTurretAngle()
    {
        return turretAngle;
    }

    /**
     * return an int representing the turret roller speed that the Kinect
     * auto targeting deems as the best power to shoot at
     * (to be used for specific distances)
     */
    public int getTurretPower()
    {
        return turretPower;
    }

    public static final int TOP    = 1;
    public static final int LEFT   = 2;
    public static final int BOTTOM = 3;
    public static final int RIGHT  = 4;

    /**
     * Tells the targeting computer what hoop to aim at.
     * 1 = top
     * 2 = left
     * 3 = bottom
     * 4 = right
     * -1 = DISABLE (example: you're looking at the enemy's hoops)
     */
    public void cycleTarget(int hoopNum)
    {
        tcc.setTargetHoopNumber( hoopNum );

        targetHoop = hoopNum;
    }

    /**
     * @return True if there is new data Avalible.
     */
    public boolean newData()
    {
        return newData;
    }

    /**
     * Resets the newData flag.
     */
    public void clearNewData()
    {
        newData = false;
    }

    // //////EVERYTHING BELOW THIS LINE IS FOR THE I/O THREAD ONLY//////////////
    // //////////////////////DON'T TOUCH////////////////////////////////////////

    /**
     * Updates the value of the target byte. The method is public because I need
     * to call it in TargetingComputerConnection, but it needs to be protected
     * to protect it from user error.
     * @param check If this is not the exact same connection as the one stored
     * locally, then the method does nothing and returns.
     */
    public synchronized boolean updateLockedOn(boolean value,
                                               TargetingComputerConnection check)
    {
        // protects the public method
        if ( check != tcc )
            return false;

        // else do everything below
        lockedOn = value;
        newData  = true;
        return true;
    }

    public static final double offsetAngle = -4; //-2
    public synchronized boolean updateTurretAngle(int value,
                                                  TargetingComputerConnection check)
    {
        // protects the public method
        if ( check != tcc )
            return false;

        // else do everything below
        //turretAngle = value * PIXEL_COUNT_TO_MOTOR_POWER;
        turretAngle = rotatePID.calc(-value + offsetAngle );
        newData     = true;
        return true;
    }

    public synchronized boolean updateTurretPower(int value,
                                                  TargetingComputerConnection check)
    {
        // protects the public method
        if ( check != tcc )
            return false;

        // else do everything below
        turretPower = value;
        newData     = true;
        return true;
    }

    public synchronized boolean updateNewData(boolean value,
                                              TargetingComputerConnection check)
    {
        // protects the public method
        if ( check != tcc )
            return false;

        newData = value;
        return true;
    }

    public synchronized boolean updateReadyToFire(boolean value,
                                                  TargetingComputerConnection check)
    {
        // protects the public method
        if ( check != tcc )
            return false;

        newData     = true;
        readyToFire = value;
        return true;
    }
    
    /**
     * terse System.out.println
     */
    public static void log(String s)
    {
        System.out.println( Time.string() + ":.........." + s );
    }
    /**
     * Resets the I value of the rotational PID system. Used during debug, but
     * unneeded for the competition because KI is 0.
     */
    public  void resetI()
    {
        rotatePID.resetI();
    }
}

//FIRST FRC team 691 2012 competition code

