package edu.io;

import edu.wpi.first.wpilibj.DriverStationLCD;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import org.team691.util.Time;

/**
 * Second thread that handles network communication with the targeting computer.
 * This is effectively a second main loop that does nothing but check the i/o 
 * connection. placing potentially
 * blocking i/o methods in a second thread can, will, and has saved the robot.
 * @author Gerard Boberg
 */
public class TargetingComputerConnection extends Thread
{
    public static final int MAX_PIXEL_OFFSET      = 8;
    public static final double BLOCK_TIMEOUT      = 5;
    public static final String DEFAULT_TARGET_URL = "socket://10.6.91.42:20000";
    public static final int NEW_DATA              = 0;
    public static final int NOT_CONNECTED         = -1;
    public static final int NO_NEW_DATA           = 1;
    protected static long intervalMills           = 100; // 10 times per second
    protected byte[] buffer                       = new byte[256];
    protected DataInputStream in                  = null;
    protected DataOutputStream out                = null;
    protected SocketConnection socket             = null;
    protected volatile int targetHoop             = 1;
    protected String targetUrl                    = DEFAULT_TARGET_URL;
    protected volatile double readBlockTime       = Time.time() + BLOCK_TIMEOUT;
    protected KPacket packet                      = new KPacket();
    protected volatile boolean isReading          = false;
    protected boolean continues                   = true;
    protected AutoTarget wraper;
    protected int tryConnect = 0;

    public TargetingComputerConnection(double ioChecksPerSecond, AutoTarget wraper)
    {
        this.wraper                               = wraper;
        TargetingComputerConnection.intervalMills = (long)( ( 1000.0
                                                              / ioChecksPerSecond ) );
    }

    public TargetingComputerConnection(String url, double ioChecksPerSecond,
                                       AutoTarget wraper)
    {
        this.targetUrl                            = url;
        this.wraper                               = wraper;
        TargetingComputerConnection.intervalMills = (long)( ( 1000.0
                                                              / ioChecksPerSecond ) );
    }

    /**
     * The main method of the new thread. This is effectively a second main loop
     * that does nothing but check the i/o connection. placing potentially
     * blocking i/o methods in a second thread can and will save the robot.
     */
    public void run()
    {
        while( continues )
        {
            try
            {
                readLock();

                switch(getConnectionStatus())
                {
                    case NOT_CONNECTED :
                        if ( ( ++tryConnect % 4 ) == 0 )
                            attemptConnection();
                        break;

                    case NO_NEW_DATA :
                        requestNewData();
                        break;

                    case NEW_DATA :

                        // log( "avalible: " + in.available() );
                        updateData();
                        break;
                }

                readUnlock();

                try
                {
                    synchronized (this)
                    {
                        this.wait( intervalMills );
                    }
                }
                catch (InterruptedException e)
                {
                    if( continues )
                        log( "interrupted. ignoring and running next cycle." );
                    else
                    {
                        log( "interrupted. ending the thread." );
                        return; //kills this thread
                    }
                }
            }
            catch (Exception e)
            {
                log( "Exception in TCC thread. Ignoring.\n\t" + e.toString() );
            }
        }
    }

    protected void readLock()
    {
        isReading     = true;
        readBlockTime = Time.time() + BLOCK_TIMEOUT;
    }

    protected void readUnlock()
    {
        isReading     = false;
        readBlockTime = Time.time() + 999999999999999;
    }

    public boolean hasBlocked()
    {
        return ( isReading && ( Time.time() > readBlockTime ) );
    }

    protected void updateData()
    {
        if ( readPacket() )
        {
            echoPacket();
            wraper.updateNewData( true, this );
        }
        else
            requestNewData();

        switch(targetHoop)
        {
            case AutoTarget.TOP :  // top
                wraper.updateTurretPower( packet.speed_top, this );
                wraper.updateTurretAngle( packet.offset_from_top, this );
                wraper.updateReadyToFire( Math.abs( packet.offset_from_top ) 
                                         < MAX_PIXEL_OFFSET, this );
                wraper.updateLockedOn( ( packet.target_number & 0x0F ) != 0, this );
                break;
            case AutoTarget.LEFT : // left
                wraper.updateTurretPower( packet.speed_left, this );
                wraper.updateTurretAngle( packet.offset_from_left, this );
                wraper.updateReadyToFire( Math.abs( packet.offset_from_left ) 
                                          < MAX_PIXEL_OFFSET, this );
                wraper.updateLockedOn( ( packet.target_number & 0x0F ) != 0, this );
                break;
            case AutoTarget.BOTTOM : // bottom
                wraper.updateTurretPower( packet.speed_bottom, this );
                wraper.updateTurretAngle( packet.offset_from_bottom, this );
                wraper.updateReadyToFire( Math.abs( packet.offset_from_bottom ) 
                                          < MAX_PIXEL_OFFSET, this );
                wraper.updateLockedOn( ( packet.target_number & 0x0F ) != 0, this );
                break;
            case AutoTarget.RIGHT : // right
                wraper.updateTurretPower( packet.speed_right, this );
                wraper.updateTurretAngle( packet.offset_from_right, this );
                wraper.updateReadyToFire( Math.abs( packet.offset_from_right ) 
                                         < MAX_PIXEL_OFFSET, this );
                wraper.updateLockedOn( ( packet.target_number & 0x0F ) != 0, this );
                break;
        }
    }

    /**
     * Sends a copy of the most recently received packet to the targeting 
     * computer. For debug use.
     */
    protected void echoPacket()
    {
        if( getConnectionStatus() != NOT_CONNECTED )
            write( packet );
    }

    /**
     * Sends the ASCII character 'W' to the targeting computer as a signal that
     * no new packet has been received. For debug use.
     */
    protected void requestNewData()
    {
        if ( getConnectionStatus() != NOT_CONNECTED )
            write( (byte)'W' );
    }

    /**
     * Commands the auto targeting system to aim for a given hoop. This doesn't
     * tell the targeting computer anything, it simply tells the java program
     * to ignore certain numbers.
     */
    public synchronized void setTargetHoopNumber(int target)
    {
        targetHoop = target;

        updateData();
    }

    /**
     * Retrieves the packet object holding the connection data.
     * @return The KPacket that is currently being used to hold incoming data.
     * The data inside the KPacket is updated asynchronously.
     */
    public KPacket getPacket()
    {
        return packet;
    }

    /**
     * Returns an error code saying what state the connection is in
     * @return NOT_CONNECTED if the connection has failed or doesn't exist,
     * NEW_DATA if a connection exists and there is data to be read, or 
     * NO_NEW_DATA if a connection exists, but it has no new data.
     */
    public int getConnectionStatus()
    {
        // no input/output, no data
        if ( ( in == null ) || ( out == null ) || ( socket == null ) )
            return NOT_CONNECTED;

        // we have in/out. see if there's anything new
        try
        {
            if( in.available() > 0 )
                return NEW_DATA;
            else
                return NO_NEW_DATA;
        }
        catch (IOException e)
        {
            log( "IO exception when checking for new data. closing all streams." );
            tryCloseAllStreams();
            return NOT_CONNECTED;
        }
    }

    /**
     * writes a given KPacket to the output connection.
     * @param data The KPacet to send in binary form.
     */
    protected void write(KPacket data)
    {
        write( data.data );
    }

    /**
     * Writes data to the output data stream.
     * @param data The buffer to write to the connection.
     */
    protected void write(byte[] data)
    {
        try
        {
            out.write( data );
            out.flush();
        }
        catch (IOException ex)
        {
            log( "IOException while writing a data packet. Closing streams." );
            tryCloseAllStreams();
        }
    }

    /**
     * Sends a single byte of data on the output connection to DeepThought
     * @param data the byte to send.
     */
    protected void write(byte data)
    {
        try
        {
            out.write( data );
            out.flush();
        }
        catch (IOException ex)
        {
            log( "IOException while writing a data packet. Closing streams." );
            tryCloseAllStreams();
        }
    }

    /**
     * Tries to read a 56-byte KinectDataPacket from the input stream.
     * @return true if the read was successful, false if it failed.
     */
    protected boolean readPacket()
    {
        try
        {
            if ( getConnectionStatus() != NEW_DATA )
            {
                return false;
            }

            int readable = in.available();

            if ( readable < packet.data.length )
            {
                return false;
            }

            for(int count = 0; count < readable; count++)
            {
                buffer[count] = in.readByte();
            }

            // find the most recent header packet in the buffer
            String tester = new String( buffer, 0, readable );
            int index     = -1;
            int index_max = -1;

            while( tester.length() - index > packet.data.length )
            {
                index = tester.indexOf( "Head", index + 1 );

                if ( index > index_max )
                {
                    index_max = index;
                }

                if ( index_max == -1 )
                {
                    return false;
                }

                if ( index == -1 )
                {
                    break;
                }
            }

            packet.copyOver( buffer, index_max, packet.data.length );
            packet.updateValues();

            return true;
        }
        catch (IOException ex)
        {
            log( "IOException while reading from input stream. Closing streams." );
            tryCloseAllStreams();

            return false;
        }
    }

    int fails = 0;

    /**
     * Tries to connect to the remote host.
     * @return true if successful, false otherwise.
     */
    protected boolean attemptConnection()
    {
        try
        {
            // try to connect, abandon if we fail
            try
            {
                socket = (SocketConnection)Connector.open( targetUrl );
            }
            catch (Exception ex)
            {
                if ( ( ++fails % 2 ) == 0 )
                {
                    log( "connection failed. Error:\n" + ex.toString() );
                    DriverStationLCD dslcd = DriverStationLCD.getInstance();
                    String err = "unkown Kinect error.";
                    String soln = "unkown solution.";
                    if( ex.toString().indexOf("-1") != -1 )
                    {
                        err  = "CRIO out of ports";
                        soln = "Reboot CRIO";
                    }
                    else if( ex.toString().indexOf("67") != -1)
                    {
                        err  = "Computer stuck at boot screen!";
                        soln = "hit enter on the keyboard";
                    }
                    else if ( ex.toString().indexOf("refused") != -1 )
                    {
                        err  = "Computer is midboot and program is not loaded!";
                        soln = "wait 60 seconds, OR double click program -> run in console";
                    }
                    dslcd.println(DriverStationLCD.Line.kMain6, 1, "No auto connect!");
                    dslcd.println(DriverStationLCD.Line.kUser2, 1, "E: " + err );
                    dslcd.println(DriverStationLCD.Line.kUser3, 1, "S: " + soln );
                    dslcd.updateLCD();
                }

                return false;
            }
            DriverStationLCD dslcd = DriverStationLCD.getInstance();
            dslcd.println(DriverStationLCD.Line.kMain6, 1, "Connected to auto!");
            dslcd.println(DriverStationLCD.Line.kUser2, 1, "<(\"<)(^\"^)(>\")>");
            dslcd.println(DriverStationLCD.Line.kUser3, 1, ":D");
            dslcd.updateLCD();
            // we are connected!
            log( "connection established to   " + targetUrl );

            // socket settings don't work. Removed.
            
            // Get the input and output streams of the connection.
            log( "opening data streams" );
            in  = socket.openDataInputStream();
            out = socket.openDataOutputStream();

            // data streams open
            return true;
        }
        catch (Exception e)
        {
            Time.newCycle();
            log( "error while connecting\n" );
            log( e );
            tryCloseAllStreams();

            return false;
        }
    }

    /**
     * Terse System.out.println
     * @param s the string to write
     */
    public static void log(String s)
    {
        System.out.println( Time.string() + ":TCC......." + s );
    }

    /**
     * Terse System.out.println for exceptions
     * @param e the exception to print out.
     */
    public static void log(Exception e)
    {
        log( "Exception: \n" + e.toString() );
    }

    /**
     * Closes all open connection streams.
     */
    protected void tryCloseAllStreams()
    {
        log( "closing all streams" );

        try
        {
            in.close();
        }
        catch (Exception e) {}

        try
        {
            out.close();
        }
        catch (Exception e) {}

        try
        {
            socket.close();
        }
        catch (Exception e) {}

        in     = null;
        out    = null;
        socket = null;
    }

    /**
     * Ends this thread.
     */
    public void kill()
    {
        continues = false;

        tryCloseAllStreams();
        this.interrupt();
    }
}

//FIRST FRC team 691 2012 competition code
