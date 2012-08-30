package Team691.Util;

/**
 * Stores and preforms calculations on angles. All values are stored in degrees.
 * All values are inside of (-180, 180] degrees.
 * @author Gerard
 */
public class Angle
{
    protected double valueDegs = 0;
    public static final double MAX_VALUE =  180.0;
    public static final double MIN_VALUE = -180.0;
    public static final double ERROR = 0.01;

    public Angle()
    {}

    public Angle(double degrees)
    {   valueDegs = degrees; reduceAngle();    }

    public Angle(Angle other)
    {   this(other.get());    }

    public double get()
    {   return valueDegs;   }

    public double getRadianValue()
    {   return valueDegs * Mathf.DEG2RAD; }

    protected void set(double input)
    {   valueDegs = input;  reduceAngle();   }

    protected void set(Angle other)
    {   set(other.get());    }

    public void setRadianValue(double input)
    {   set(Mathf.RAD2DEG*input);    }

    public Angle add(Angle other)
    {   return add(other.get());  }

    public Angle add(double amount)
    {   return new Angle(this.get() + amount);  }

    public Angle subtract(Angle other)
    {   return subtract(other.get());   }

    public Angle subtract(double amount)
    {   return new Angle(this.get() - amount);  }

    public Angle multiply(Angle other)
    {   return multiply(other.get());   }

    public Angle multiply(double amount)
    {   return new Angle(this.get() * amount);  }

    public Angle divide(Angle other)
    {   return divide(other.get());   }

    public Angle divide(double amount)
    {   return new Angle(this.get() / amount);  }

    public Angle modulus(Angle other)
    {   return modulus(other.get());   }

    public Angle modulus(double amount)
    {   return new Angle(this.get() % amount);  }

    public Angle negitive()
    {   return new Angle(-this.get());   }

    public Angle plus180()
    {   return new Angle(this.get() + 180); }

    public Angle minus180()
    {   return new Angle(this.get() - 180); }

    protected double distanceTo(double other)
    {
        if(Mathf.approximately(this.get(), other, ERROR))
            return 0;

        double dist = Mathf.dist(   this.get(), other   );
        if(Mathf.abs(dist) <= 180)
            return dist;

        dist = 360 - Mathf.abs(dist);
        return dist * -1 * Mathf.sign(   Mathf.dist(this.get(), other)  );
    }
    public double distanceTo(Angle other)
    {   return distanceTo(other.get());    }
    public double distanceTo(Object other)
    {   return distanceTo(((Angle)other).get());    }

    public boolean equals(Angle other)
    {   return compareTo(other) == 0;    }
    public boolean equals(double other)
    {   return compareTo(other) == 0;    }
    
    public int compareTo(double other)
    {
        double comp = this.get() - other;

        if(Mathf.approximately(comp, 0, ERROR))
            return 0;
        else if(comp > 0)
            return Mathf.ceiling(comp);
        else
            return Mathf.floor(comp);
    }
    public int compareTo(Angle other)
    {   return compareTo(other.get());    }
    public int compareTo(Object other)
    {    return compareTo(  ((Angle)other).get()  );    }

    public boolean lessThan(double other)
    {   return compareTo(other) < 0;    }
    public boolean lessThan(Angle other)
    {   return compareTo(other) < 0;    }
    public boolean lessThan(Object other)
    {   return compareTo(other) < 0;    }

    public boolean greaterThan(double other)
    {   return compareTo(other) > 0;    }
    public boolean greaterThan(Angle other)
    {   return compareTo(other) > 0;    }
    public boolean greaterThan(Object other)
    {   return compareTo(other) > 0;    }

    public double sin()
    {   return Mathf.sin(  Mathf.DEG2RAD * (this.get())  );    }
    public double cos()
    {   return Mathf.cos(  Mathf.DEG2RAD * (this.get())  );    }
    public double tan()
    {   return Mathf.tan(  Mathf.DEG2RAD * (this.get())  );    }

    public Angle clone()
    {   return new Angle(this.get());    }

    protected void reduceAngle()
    {
        valueDegs = Mathf.repeat(valueDegs, MIN_VALUE, MAX_VALUE);
        //Mathf.repeat returns a value [-180, 180)
        //but we want a value in (-180, 180]
        if(Mathf.approximately(valueDegs, -180.0, ERROR))
            valueDegs = 180.0;
        if(Mathf.approximately(valueDegs, 180.0, ERROR))
            valueDegs = 180.0;
        else if (Mathf.approximately(valueDegs, 0, ERROR))
            valueDegs = 0;
    }

    public String toString()
    {    return Double.toString(this.get());    }

    public Angle lerp(Angle b, double where)
    {   return lerp(this, b, where);    }

    public static Angle lerp(Angle a, Angle b, double where)
    {
        where = Mathf.clamp01(where);
        if(Mathf.approximately(where, 0))
            return new Angle(a);
        if(Mathf.approximately(where, 1))
            return new Angle(b);

        double delta = a.distanceTo(b);
        delta *= where;
        return new Angle(a.get() + delta);
    }

    //----------static angle values---------
    public static Angle zero()
    {   return new Angle(0); }
    public static Angle zero(double scale)
    {   return new Angle(scale*0); }
    public static Angle sixth()
    {   return new Angle(30);   }
    public static Angle sixth(double scale)
    {   return new Angle(scale*30); }
    public static Angle quarter()
    {   return new Angle(45);   }
    public static Angle quarter(double scale)
    {   return new Angle(scale*45); }
    public static Angle third()
    {   return new Angle(60);   }
    public static Angle third(double scale)
    {   return new Angle(scale*60); }
    public static Angle half()
    {   return new Angle(90);   }
    public static Angle half(double scale)
    {   return new Angle(scale*90); }
    public static Angle right()
    {   return new Angle(90);   }
    public static Angle right(double scale)
    {   return new Angle(scale*90); }
    public static Angle twoThirds()
    {   return new Angle(120);  }
    public static Angle twoThirds(double scale)
    {   return new Angle(scale*120); }
    public static Angle threeFourths()
    {   return new Angle(135);  }
    public static Angle threeFourths(double scale)
    {   return new Angle(scale*135); }
    public static Angle fiveSixths()
    {   return new Angle(150);  }
    public static Angle fiveSixths(double scale)
    {   return new Angle(scale*150); }
    public static Angle full()
    {   return new Angle(180);  }
    public static Angle full(double scale)
    {   return new Angle(scale*180); }
}