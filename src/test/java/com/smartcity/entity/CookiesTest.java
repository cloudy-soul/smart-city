package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CookiesTest {

    private static Cookies sample() {
        return new Cookies(1L, "sample", "sample", "sample", "sample", 1.0, 1.0, "sample", "sample");
    }

    @Test
    void constructsValidInstance() {
        Cookies o = sample();
        assertNotNull(o);
    }

    @Test
    void idIsStoredByConstructor() {
        Cookies o = sample();
        assertEquals(1L, o.getId());
    }

    @Test
    void timezoneIsStoredByConstructor() {
        Cookies o = sample();
        assertEquals("sample", o.getTimezone());
    }

    @Test
    void browserIsStoredByConstructor() {
        Cookies o = sample();
        assertEquals("sample", o.getBrowser());
    }

    @Test
    void OSIsStoredByConstructor() {
        Cookies o = sample();
        assertEquals("sample", o.getOS());
    }

    @Test
    void machineNameIsStoredByConstructor() {
        Cookies o = sample();
        assertEquals("sample", o.getMachineName());
    }

    @Test
    void longitudeIsStoredByConstructor() {
        Cookies o = sample();
        assertEquals(1.0, o.getLongitude(), 1e-9);
    }

    @Test
    void altitudeIsStoredByConstructor() {
        Cookies o = sample();
        assertEquals(1.0, o.getAltitude(), 1e-9);
    }

    @Test
    void IPAddressIsStoredByConstructor() {
        Cookies o = sample();
        assertEquals("sample", o.getIPAddress());
    }

    @Test
    void MACAddressIsStoredByConstructor() {
        Cookies o = sample();
        assertEquals("sample", o.getMACAddress());
    }

   

    @Test
    void setTimezoneUpdatesValue() {
        Cookies o = sample();
        o.setTimezone("other");
        assertEquals("other", o.getTimezone());
    }

    @Test
    void setBrowserUpdatesValue() {
        Cookies o = sample();
        o.setBrowser("other");
        assertEquals("other", o.getBrowser());
    }

    @Test
    void setOSUpdatesValue() {
        Cookies o = sample();
        o.setOS("other");
        assertEquals("other", o.getOS());
    }

    @Test
    void setMachineNameUpdatesValue() {
        Cookies o = sample();
        o.setMachineName("other");
        assertEquals("other", o.getMachineName());
    }

    @Test
    void setLongitudeUpdatesValue() {
        Cookies o = sample();
        o.setLongitude(2.0);
        assertEquals(2.0, o.getLongitude(), 1e-9);
    }

    @Test
    void setAltitudeUpdatesValue() {
        Cookies o = sample();
        o.setAltitude(2.0);
        assertEquals(2.0, o.getAltitude(), 1e-9);
    }

    @Test
    void setIPAddressUpdatesValue() {
        Cookies o = sample();
        o.setIPAddress("other");
        assertEquals("other", o.getIPAddress());
    }

    @Test
    void setMACAddressUpdatesValue() {
        Cookies o = sample();
        o.setMACAddress("other");
        assertEquals("other", o.getMACAddress());
    }
}
