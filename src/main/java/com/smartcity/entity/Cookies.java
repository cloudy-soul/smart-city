package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Cookies - generated from the UML diagram by diagram2app.
 */
@Embeddable
public class Cookies {

    @Column(nullable = false)
    protected Long id;
    @Column(nullable = false)
    protected String timezone;
    @Column(nullable = false)
    protected String browser;
    @Column(nullable = false)
    protected String OS;
    @Column(nullable = false)
    protected String machineName;
    protected double longitude;
    protected double altitude;
    @Column(nullable = false)
    protected String IPAddress;
    @Column(nullable = false)
    protected String MACAddress;

    protected Cookies() {
    }

    public Cookies(Long id, String timezone, String browser, String OS, String machineName, double longitude, double altitude, String IPAddress, String MACAddress) {
        this.id = id;
        this.timezone = timezone;
        this.browser = browser;
        this.OS = OS;
        this.machineName = machineName;
        this.longitude = longitude;
        this.altitude = altitude;
        this.IPAddress = IPAddress;
        this.MACAddress = MACAddress;
    }

    public Long getId() {
        return id;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public String getMACAddress() {
        return MACAddress;
    }

    public void setMACAddress(String MACAddress) {
        this.MACAddress = MACAddress;
    }

    @Override
    public String toString() {
        return "Cookies{" + "id=" + getId() + ", " + "timezone=" + getTimezone() + ", " + "browser=" + getBrowser() + ", " + "OS=" + getOS() + ", " + "machineName=" + getMachineName() + ", " + "longitude=" + getLongitude() + ", " + "altitude=" + getAltitude() + ", " + "IPAddress=" + getIPAddress() + ", " + "MACAddress=" + getMACAddress() + "}";
    }
}
