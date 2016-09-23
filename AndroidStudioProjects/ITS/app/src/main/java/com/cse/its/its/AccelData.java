package com.cse.its.its;

/**
 * Created by ajalan on 24/9/16.
 */
public class AccelData {
    private long timestamp;
    private double x;
    private double y;
    private double z;

    /**
     * Constructor defined
     * @param timestamp
     * @param x
     * @param y
     */
    public AccelData(long timestamp, double x, double y, double z) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Retrieve the timestamp
     * @return timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timstamp
     * @param timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get the value of x
     * @return double
     */
    public double getX() {
        return x;
    }

    /**
     * Set the value of x
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Get the value of y
     * @return double
     */
    public double getY() {
        return y;
    }

    /**
     * Set the value of y
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the value of z
     * @return double
     */
    public double getZ() {
        return z;
    }

    /**
     * Set the value of z
     * @param z
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Converts the timestamp to string
     * @return string
     */
    public String toString() {
        return "t="+timestamp+", x="+x+", y="+y+", z="+z;
    }
}
