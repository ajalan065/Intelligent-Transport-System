package com.cse.its.its;

/**
 * Created by ajalan on 16/9/16.
 */
public class LightData {
    private long timestamp;
    private double x, y;

    /**
     * Constructor defined
     * @param timestamp
     * @param x
     * @param y
     */
    public LightData(long timestamp, double x, double y) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieve the timestamp
     * @return timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Retrieve the timstamp
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
     * Set the value of x
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Converts the timestamp to string
     * @return string
     */
    public String toString()
    {
        return "t="+timestamp+"x x="+x;
        //return "t="+timestamp+", x="+x+", y="+y;
    }
}
