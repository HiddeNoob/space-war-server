package com.hiddenoob.Math;

public class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3() { this(0,0,0); }
    public Vector3(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }

    public Vector3 add(Vector3 o) { return new Vector3(this.x + o.x, this.y + o.y, this.z + o.z); }
    public Vector3 sub(Vector3 o) { return new Vector3(this.x - o.x, this.y - o.y, this.z - o.z); }
    public Vector3 mul(double s) { return new Vector3(this.x * s, this.y * s, this.z * s); }

    public double length() { return Math.sqrt(x*x + y*y + z*z); }

    @Override
    public String toString() { return "Vector3(" + x + "," + y + "," + z + ")"; }
    
    @Override
    public Vector3 clone() { return new Vector3(x,y,z); }

}
