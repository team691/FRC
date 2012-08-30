package edu.io;

/**
 * Data structure that holds targeting data coming in from DeepThought.
 * To use, just refer to the public variable fields.
 * @author Gerard Boberg and Mentor Mr. Petras
 */
public class KPacket
{
    public static final int DATA_START = 8;
    public static final int HEADER_END = 7;
    public byte[] data                 = new byte[56];
    public int angle;
    public int distance;
    public int frame_count;
    public String header;
    public int offset_from_bottom;
    public int offset_from_left;
    public int offset_from_right;
    public int offset_from_top;
    public int speed_bottom;
    public int speed_left;
    public int speed_right;
    public int speed_top;
    public int target_number;

    public void copyOver(byte[] input)
    {
        copyOver( input, 0, data.length );
    }

    public void copyOver(byte[] input, int offset, int length)
    {
        System.arraycopy( input, offset, data, 0, length );
        updateValues();
    }

    protected void updateValues()
    {
        header             = new String( data, 0, 8 );
        frame_count        = getInt( 1 );
        offset_from_top    = getInt( 2 );
        offset_from_left   = getInt( 3 );
        offset_from_bottom = getInt( 4 );
        offset_from_right  = getInt( 5 );
        speed_top          = getInt( 6 );
        speed_left         = getInt( 7 );
        speed_right        = getInt( 8 );
        speed_bottom       = getInt( 9 );
        distance           = getInt( 10 );
        angle              = getInt( 11 );
        target_number      = getInt( 12 );
    }

    public int getInt(int number)
    {
        if ( ( number < 1 ) || ( number > 12 ) )
            return 0;

        int output;

        // read little endian input
        output = unsign( data[( DATA_START - 1 ) + ( number * 4 )] ) << 24;
        output += unsign( data[( DATA_START - 2 ) + ( number * 4 )] ) << 16;
        output += unsign( data[( DATA_START - 3 ) + ( number * 4 )] ) << 8;
        output += unsign( data[( DATA_START - 4 ) + ( number * 4 )] );

        return output;
    }

    public int getInt(int number, boolean isBigEndian)
    {
        if ( ( number < 1 ) || ( number > 12 ) )
            return 0;

        int output;

        if ( !isBigEndian )
        {    // isLittleEndian
            output = unsign( data[( DATA_START - 1 ) + ( number * 4 )] ) << 24;
            output += unsign( data[( DATA_START - 2 ) + ( number * 4 )] ) << 16;
            output += unsign( data[( DATA_START - 3 ) + ( number * 4 )] ) << 8;
            output += unsign( data[( DATA_START - 4 ) + ( number * 4 )] );

            return output;
        }
        else // ( isBigEndian )
        {
            output = unsign( data[( DATA_START - 4 ) + ( number * 4 )] ) << 24;
            output += unsign( data[( DATA_START - 3 ) + ( number * 4 )] ) << 16;
            output += unsign( data[( DATA_START - 2 ) + ( number * 4 )] ) << 8;
            output += unsign( data[( DATA_START - 1 ) + ( number * 4 )] );

            return output;
        }
    }

    /**
     * Converts from signed bytes (-128 --- 127) into unsigned bytes (255 --- 0)
     * @param value the input signed byte
     * @return the output number, as an int.
     */
    public static int unsign(byte value)
    {
        return ( value & 127 ) + ( ( value < 0 ) ? 128 : 0 );
    }
}

//FIRST FRC team 691 2012 competition code
