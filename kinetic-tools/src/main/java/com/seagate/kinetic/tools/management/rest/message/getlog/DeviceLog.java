/**
 * Copyright (C) 2014 Seagate Technology.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.seagate.kinetic.tools.management.rest.message.getlog;

import java.util.List;

import kinetic.admin.Capacity;
import kinetic.admin.Configuration;
import kinetic.admin.KineticLogType;
import kinetic.admin.Limits;
import kinetic.admin.Statistics;
import kinetic.admin.Temperature;
import kinetic.admin.Utilization;
import kinetic.client.KineticException;

import com.seagate.kinetic.admin.impl.DefaultConfiguration;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;

/**
 * 
 * Device log json message container
 * 
 * @author chiaming
 */
public class DeviceLog {

    private DeviceStatus deviceStatus = null;

    private List<Utilization> utilizations = null;

    private List<Temperature> temperatures = null;

    private Capacity capacity = null;

    private DefaultConfiguration configuration = null;

    private List<Statistics> statistics = null;

    private Limits limits = null;

    private byte[] messages = null;

    private KineticLogType[] containedLogTypes = null;

    public void setDeviceStatus(DeviceStatus status) {
        this.deviceStatus = status;
    }

    public DeviceStatus getDeviceStatus() {
        return this.deviceStatus;
    }

    /**
     * 
     * Get the utilization information of the drive
     * <p>
     * 
     * @return a List of utilization information from the drive.
     * 
     * @see Utilization
     */
    public List<Utilization> getUtilization() {
        return this.utilizations;
    }

    public void setUtilization(List<Utilization> listOfUtilization) {
        this.utilizations = listOfUtilization;
    }

    /**
     * 
     * Get the temperature information of the drive
     * <p>
     * 
     * @return a List of temperature information from the drive.
     * 
     * @see Temperature
     */
    public List<Temperature> getTemperature() {
        return this.temperatures;
    }

    public void setTemperature(List<Temperature> listOfTemps) {
        this.temperatures = listOfTemps;
    }

    /**
     * 
     * Get the capacity information of the drive
     * <p>
     * 
     * @return the capacity information from the drive.
     * 
     * @see Capacity
     */
    public Capacity getCapacity() {
        return this.capacity;
    }

    public void setCapacity(Capacity c) {
        this.capacity = c;
    }

    /**
     * 
     * Get the configuration information of the drive
     * <p>
     * 
     * @return the configuration information from the drive.
     * 
     * @see DefaultConfiguration
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(Configuration c) {
        this.configuration = (DefaultConfiguration) c;
    }

    /**
     * 
     * Get the statistics information of the drive
     * <p>
     * 
     * @return a List of statistic information from the drive.
     * 
     * @throws KineticException
     *             if any internal error occurred.
     * @see Statistics
     */
    public List<Statistics> getStatistics() {
        return this.statistics;
    }

    public void setStatistics(List<Statistics> listOfStatistics) {
        this.statistics = listOfStatistics;
    }

    /**
     * 
     * Get Kinetic log messages.
     * 
     * @return Kinetic log messages from the drive.
     * 
     * @throws KineticException
     *             if any internal error occurred.
     */
    public byte[] getMessages() {
        return this.messages;
    }

    public void setMessages(byte[] messages) {
        this.messages = messages;
    }

    /**
     * Get Kinetic log type values set in this log instance.
     * 
     * @return an array of KineticLogType information contains in this instance.
     * 
     * @throws KineticException
     *             if any internal errors occur
     */
    public KineticLogType[] getContainedLogTypes() throws KineticException {
        return this.containedLogTypes;
    }

    public void setContainedLogTypes(KineticLogType[] containedLogTypes) {
        this.containedLogTypes = containedLogTypes;
    }

    /**
     * 
     * Get the limits information of the drive
     * <p>
     * 
     * @return the limits information from the drive.
     * 
     * @throws KineticException
     *             if any internal error occurred.
     * @see Limits
     */
    public Limits getLimits() {
        return this.limits;
    }

    public void setLimits(Limits l) {
        this.limits = l;
    }

}
