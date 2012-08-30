package Team691.Util;

/**
 * Centralizes the handling of time, reducing the number of times
 * <code>System.currentTimeMillis()</code> is called, provides an easy way to keep
 * track of the change in time, and handles timers in a central, easy to use location.
 * Also has the advantage of making programing the robot feel more like a
 * programing a video game.
 * @author Gerard
 */
public class Time
{
    protected static final long startTime = System.currentTimeMillis();
    protected static long lastTime = startTime;
    protected static double time = (startTime - System.currentTimeMillis()   ) / 1000.0;
    protected static double realTime = (startTime - System.currentTimeMillis()   ) / 1000.0;
    protected static double realDeltaTime = 0.001;
    protected static double deltaTime = 0.001;
    protected static double timeScale = 1.0;

    /**
     * Updates the time variables. Do not call more than once per cycle.
     * Does not continue if the time elapsed  is less than a millisecond,
     * because we cannot accuratly work with time values that small in a java
     * enviornment.
     * @return True if the time variables were sucessfuly updated, false if they
     * were not. It is recomended that you skip a frame if this returns false.
     */
    public static boolean newCycle()
    {
        long newTime = System.currentTimeMillis();

        //no time has elapsed, get out.
        if(lastTime - newTime == 0)
            return false;

        //update delta time
        realDeltaTime = (newTime - lastTime) / 1000.0;
        if(timeScale <= 0)
            timeScale = 1;
        deltaTime = realDeltaTime * timeScale;

        //increment time
        realTime += realDeltaTime;
        time += deltaTime;
        
        lastTime = newTime;
        return true;
    }

    /**
     * The value of <code>System.currentTimeMils()</code> when the application
     * was started.
     * @return The time in milliseconds at the start of the application.
     */
    public static long startTime()
    {   return startTime;  }

    /**
     * The time elapsed in seconds since the application started. Affected by
     * the timeScale.
     * @return The time elapsed since the application started in local seconds. A value
     * of 1.0 means one second has elapsed local time since the application started.
     */
    public static double time()
    {   return time;  }

    /**
     * The time elapsed in real world seconds since the application started. Not
     * affected by the timeScale.
     * @return The time elapsed since the application started in real seconds.
     * A value of 1.0 means that one second has elapsed in the real world since the
     * application started.
     */
    public static double realTime()
    {   return realTime;  }

    /**
     * The time elapsed in seconds since the previous frame of the application
     * in local time. Affected by the timeScale.
     * @return The time since the previous frame in seconds. A value of 1.0 means
     * that one second has elapsed in local time since the last cycle of the
     * application's main loop.
     */
    public static double deltaTime()
    {   return deltaTime;  }

    /**
     * The time elapsed in seconds since the previous frame of the application
     * in real time. Not affected by the timeScale.
     * @return The time since the previous frame in seconds. A value of 1.0 means
     * that one second has elapsed in real time since the last cycle of the
     * application's main loop.
     */
    public static double realDeltaTime()
    {   return realDeltaTime;   }
    /**
     * Sets the current time scale. The time and deltaTime values are modified by
     * this amount to artifically slow or speed up the system's precieved time
     * relative to the real world's time. The realTime and realDeltaTime values
     * are unaffected by this modifier.
     * @param value The new value of the time scale. Values less than or equal to
     * zero will reset the value to one. Values greater than one will make time
     * move faster, and values less than one will make time move slower.
     */
    public static void setTimeScale(double value)
    {
        if(value > 0)  timeScale = value;
        else           timeScale = 1;
    }
    
    /**
     * Returns the current time scale, which is used to artifically slow or speed
     * up the system's time.
     * @return The current time scale. Defines how local time is scaled compared
     * to real time.
     */
    public static double getTimeScale()
    {   return timeScale;   }
}