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
package com.seagate.kinetic.tools.management.rest.bridge.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import kinetic.admin.ACL;
import kinetic.admin.Device;
import kinetic.admin.KineticLog;
import kinetic.admin.KineticLogType;
import kinetic.client.KineticException;

import com.seagate.kinetic.tools.management.cli.impl.AdminClientRegister;
import com.seagate.kinetic.tools.management.cli.impl.CheckFirmwareVersion;
import com.seagate.kinetic.tools.management.cli.impl.Command;
import com.seagate.kinetic.tools.management.cli.impl.DefaultCommandInvoker;
import com.seagate.kinetic.tools.management.cli.impl.DeviceDiscovery;
import com.seagate.kinetic.tools.management.cli.impl.GetLog;
import com.seagate.kinetic.tools.management.cli.impl.GetVendorSpecificDeviceLog;
import com.seagate.kinetic.tools.management.cli.impl.InstantErase;
import com.seagate.kinetic.tools.management.cli.impl.Invoker;
import com.seagate.kinetic.tools.management.cli.impl.KineticDevice;
import com.seagate.kinetic.tools.management.cli.impl.LockDevice;
import com.seagate.kinetic.tools.management.cli.impl.PingReachableDrive;
import com.seagate.kinetic.tools.management.cli.impl.Report;
import com.seagate.kinetic.tools.management.cli.impl.SecureErase;
import com.seagate.kinetic.tools.management.cli.impl.SetClusterVersion;
import com.seagate.kinetic.tools.management.cli.impl.SetErasePin;
import com.seagate.kinetic.tools.management.cli.impl.SetLockPin;
import com.seagate.kinetic.tools.management.cli.impl.SetSecurity;
import com.seagate.kinetic.tools.management.cli.impl.UnLockDevice;
import com.seagate.kinetic.tools.management.rest.bridge.RestBridgeService;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceInfo;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;
import com.seagate.kinetic.tools.management.rest.message.ErrorResponse;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.RestResponseWithStatus;
import com.seagate.kinetic.tools.management.rest.message.checkversion.CheckVersionRequest;
import com.seagate.kinetic.tools.management.rest.message.checkversion.CheckVersionResponse;
import com.seagate.kinetic.tools.management.rest.message.discover.DiscoverRequest;
import com.seagate.kinetic.tools.management.rest.message.discover.DiscoverResponse;
import com.seagate.kinetic.tools.management.rest.message.erasedevice.InstantEraseRequest;
import com.seagate.kinetic.tools.management.rest.message.erasedevice.InstantEraseResponse;
import com.seagate.kinetic.tools.management.rest.message.erasedevice.SecureEraseRequest;
import com.seagate.kinetic.tools.management.rest.message.erasedevice.SecureEraseResponse;
import com.seagate.kinetic.tools.management.rest.message.getlog.DeviceLog;
import com.seagate.kinetic.tools.management.rest.message.getlog.GetLogRequest;
import com.seagate.kinetic.tools.management.rest.message.getlog.GetLogResponse;
import com.seagate.kinetic.tools.management.rest.message.lockdevice.LockDeviceRequest;
import com.seagate.kinetic.tools.management.rest.message.lockdevice.LockDeviceResponse;
import com.seagate.kinetic.tools.management.rest.message.lockdevice.UnLockDeviceRequest;
import com.seagate.kinetic.tools.management.rest.message.lockdevice.UnLockDeviceResponse;
import com.seagate.kinetic.tools.management.rest.message.ping.PingRequest;
import com.seagate.kinetic.tools.management.rest.message.ping.PingResponse;
import com.seagate.kinetic.tools.management.rest.message.setclversion.SetClusterVersionRequest;
import com.seagate.kinetic.tools.management.rest.message.setclversion.SetClusterVersionResponse;
import com.seagate.kinetic.tools.management.rest.message.setpin.SetErasePinRequest;
import com.seagate.kinetic.tools.management.rest.message.setpin.SetErasePinResponse;
import com.seagate.kinetic.tools.management.rest.message.setpin.SetLockPinRequest;
import com.seagate.kinetic.tools.management.rest.message.setpin.SetLockPinResponse;
import com.seagate.kinetic.tools.management.rest.message.setsecurity.SetSecurityRequest;
import com.seagate.kinetic.tools.management.rest.message.setsecurity.SetSecurityResponse;

/**
 * 
 * This is an example of a dummy RestBridgeService that responds with hard-coded
 * response associated with the requested data .
 * 
 * @author emma
 *
 */
public class DefaultRestBridgeService implements RestBridgeService {
    private static final int DEFAULT_PING_DISCOVER_TIMEOUT = 10;
    private static final String DRIVES_FILE_PREFIX = "drives_";
    private static final String PING_FILE_PREFIX = "ping_";
    private static final String GETLOG_LOG_FILE_PREFIX = "getlog_";
    private static final String GETLOG_VENDOR_DEVICE_PREFIX = "getvendorspecificdevicelog_";
    private static final String ALL = "all";
    private static final String DEVICE = "device";
    private static final String TOOL_HOME = System.getProperty(
            "kinetic.tools.out", ".");
    private static final String ROOT_DIR = TOOL_HOME + File.separator + "out"
            + File.separator;
    private static final AdminClientRegister adminClientRegister = new AdminClientRegister();

    public DefaultRestBridgeService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public RestResponse service(RestRequest request) {

        // get message type
        MessageType mtype = request.getMessageType();

        // response message
        RestResponse response = null;
        try {
            switch (mtype) {
            case PING:
                response = ping((PingRequest) request);
                break;
            case DISCOVER:
                response = discover((DiscoverRequest) request);
                break;
            case GETLOG:
                response = getlog((GetLogRequest) request);
                break;
            case CHECKVERSION:
                response = this.checkVersion((CheckVersionRequest) request);
                break;
            case SET_ERASEPIN:
                response = this.setErasePin((SetErasePinRequest) request);
                break;
            case SET_LOCKPIN:
                response = this.setLockPin((SetLockPinRequest) request);
                break;
            case INSTANT_ERASE:
                response = this.instantErase((InstantEraseRequest) request);
                break;
            case SECURE_ERASE:
                response = this.secureErase((SecureEraseRequest) request);
                break;
            case LOCK_DEVICE:
                response = this.lockDevice((LockDeviceRequest) request);
                break;
            case UNLOCK_DEVICE:
                response = this.unLockDevice((UnLockDeviceRequest) request);
                break;
            case SET_CLVERSION:
                response = this
                        .setClusterVersion((SetClusterVersionRequest) request);
                break;

            case SET_SECURITY:
                response = this.setSecurity((SetSecurityRequest) request);
                break;
            default:
                response = new ErrorResponse();
                ((ErrorResponse) response)
                        .setErrorCode(HttpServletResponse.SC_NOT_FOUND);
                response.setOverallStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response = new ErrorResponse();
            response.setMessageType(mtype);
            ((ErrorResponse) response)
                    .setErrorCode(HttpServletResponse.SC_BAD_REQUEST);
            ((ErrorResponse) response).setErrorMessage(e.getMessage());
        } catch (IOException e) {
            response = new ErrorResponse();
            response.setMessageType(mtype);
            ((ErrorResponse) response)
                    .setErrorCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ((ErrorResponse) response).setErrorMessage(e.getMessage());
        } catch (KineticException e) {
            response = new ErrorResponse();
            response.setMessageType(mtype);
            ((ErrorResponse) response)
                    .setErrorCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ((ErrorResponse) response).setErrorMessage(e.getMessage());
        }

        return response;
    }

    private RestResponse getlog(GetLogRequest request)
            throws NumberFormatException, IOException, KineticException {
        GetLogResponse response = new GetLogResponse();
        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDeviceLogs(null);
            return response;
        }

        String type = null;
        if (null == request.getLogType()) {
            type = ALL;
        } else if (KineticLogType.DEVICE == request.getLogType()) {
            type = DEVICE;
            response = (GetLogResponse) getVendorSpecificLog(request);
            return response;
        } else {
            type = request.getLogType().toString();
        }

        Invoker invoker = new DefaultCommandInvoker();
        Command getLogCommand = null;
        if (null == discoId) {
            getLogCommand = new GetLog(kineticIds, GETLOG_LOG_FILE_PREFIX
                    + System.currentTimeMillis(), type, request.getUseSsl(),
                    request.getClversion(), Long.parseLong(request
                            .getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            getLogCommand = new GetLog(ROOT_DIR + discoId,
                    GETLOG_LOG_FILE_PREFIX + System.currentTimeMillis(), type,
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }

        getLogCommand.setAdminClientRegister(adminClientRegister);
        Report report = invoker.execute(getLogCommand);

        List<DeviceLog> deviceLogs = new ArrayList<DeviceLog>();
        for (KineticDevice kineticDevice : report.getSucceedDevices()) {
            addToDeviceLog(report, deviceLogs, kineticDevice,
                    request.getLogType(), HttpServletResponse.SC_OK);
        }

        for (KineticDevice kineticDevice : report.getFailedDevices()) {
            addToDeviceLog(report, deviceLogs, kineticDevice,
                    request.getLogType(),
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }

        response.setDeviceLogs(deviceLogs);

        return response;
    }

    private RestResponse getVendorSpecificLog(GetLogRequest request)
            throws NumberFormatException, IOException {
        GetLogResponse response = new GetLogResponse();

        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDeviceLogs(null);
            return response;
        }

        String name = request.getName();
        if (name == null || name.isEmpty()) {
            response.setDeviceLogs(null);
            return response;
        }

        Invoker invoker = new DefaultCommandInvoker();
        Command getVendorSpecificDeviceLog = null;
        if (null == discoId) {
            getVendorSpecificDeviceLog = new GetVendorSpecificDeviceLog(name,
                    kineticIds, GETLOG_VENDOR_DEVICE_PREFIX
                            + System.currentTimeMillis(), request.getUseSsl(),
                    request.getClversion(), Long.parseLong(request
                            .getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            getVendorSpecificDeviceLog = new GetVendorSpecificDeviceLog(name,
                    discoId, GETLOG_VENDOR_DEVICE_PREFIX
                            + System.currentTimeMillis(), request.getUseSsl(),
                    request.getClversion(), Long.parseLong(request
                            .getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        Report report = invoker.execute(getVendorSpecificDeviceLog);

        List<DeviceLog> deviceLogs = new ArrayList<DeviceLog>();
        for (KineticDevice kineticDevice : report.getSucceedDevices()) {
            addToSpecificDeviceLog(report, deviceLogs, kineticDevice,
                    HttpServletResponse.SC_OK, response);
        }

        for (KineticDevice kineticDevice : report.getFailedDevices()) {
            addToSpecificDeviceLog(report, deviceLogs, kineticDevice,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE, response);
        }

        response.setDeviceLogs(deviceLogs);

        return response;

    }

    private void addToSpecificDeviceLog(Report report,
            List<DeviceLog> deviceLogs, KineticDevice kineticDevice,
            int responseCode, GetLogResponse response)
            throws UnsupportedEncodingException {
        DeviceId device;
        DeviceStatus dstatus;
        DeviceLog deviceLog;
        Device deviceSpecificLog = (Device) report
                .getAdditionMessage(kineticDevice);

        device = initDevice(kineticDevice);
        deviceLog = new DeviceLog();
        dstatus = new DeviceStatus();

        dstatus.setDevice(device);
        if (null != deviceSpecificLog && null != deviceSpecificLog.getName()) {
            dstatus.setMessage(new String(deviceSpecificLog.getName(), "UTF-8"));
        }

        deviceLog.setDeviceStatus(dstatus);
        deviceLogs.add(deviceLog);

        if (null != deviceSpecificLog && null != deviceSpecificLog.getValue()) {
            response.setValue(deviceSpecificLog.getValue());
        }
    }

    private void addToDeviceLog(Report report, List<DeviceLog> deviceLogs,
            KineticDevice kineticDevice, KineticLogType type, int responseCode)
            throws KineticException {
        DeviceId device;
        DeviceStatus dstatus;
        DeviceLog deviceLog;
        KineticLog myKineticLog = (KineticLog) report
                .getAdditionMessage(kineticDevice);
        device = initDevice(kineticDevice);
        deviceLog = new DeviceLog();
        dstatus = new DeviceStatus();
        dstatus.setDevice(device);

        if (null != myKineticLog) {
            if (null == type) {
                setDefaultLogTypes(deviceLog, myKineticLog);
            } else {
                switch (type) {
                case UTILIZATIONS:
                    deviceLog.setUtilization(myKineticLog.getUtilization());
                    break;
                case TEMPERATURES:
                    deviceLog.setTemperature(myKineticLog.getTemperature());
                    break;
                case CAPACITIES:
                    deviceLog.setCapacity(myKineticLog.getCapacity());
                    break;
                case CONFIGURATION:
                    deviceLog.setConfiguration(myKineticLog.getConfiguration());
                    break;
                case STATISTICS:
                    deviceLog.setStatistics(myKineticLog.getStatistics());
                    break;
                case MESSAGES:
                    deviceLog.setMessages(myKineticLog.getMessages());
                    break;
                case LIMITS:
                    deviceLog.setLimits(myKineticLog.getLimits());
                    break;
                case DEVICE:
                    break;
                default:
                    setDefaultLogTypes(deviceLog, myKineticLog);
                }
            }
        }

        deviceLog.setDeviceStatus(dstatus);
        deviceLogs.add(deviceLog);
    }

    private void setDefaultLogTypes(DeviceLog deviceLog, KineticLog myKineticLog)
            throws KineticException {
        deviceLog.setCapacity(myKineticLog.getCapacity());
        deviceLog.setConfiguration(myKineticLog.getConfiguration());
        deviceLog.setLimits(myKineticLog.getLimits());
        deviceLog.setStatistics(myKineticLog.getStatistics());
        KineticLogType[] logTypes = myKineticLog.getContainedLogTypes();
        List<KineticLogType> listOfNewLogType = new ArrayList<KineticLogType>();
        for (int i = 0; i < logTypes.length; i++) {
            if (!logTypes[i].equals(KineticLogType.MESSAGES)) {
                listOfNewLogType.add(logTypes[i]);
            }
        }
        KineticLogType[] arrayOfNewLogType = new KineticLogType[listOfNewLogType
                .size()];
        deviceLog.setContainedLogTypes(listOfNewLogType
                .toArray(arrayOfNewLogType));
        deviceLog.setTemperature(myKineticLog.getTemperature());
        deviceLog.setUtilization(myKineticLog.getUtilization());
    }

    private String discoverDevices(List<KineticDevice> devices, int timeout,
            String discoId) {
        DeviceDiscovery deviceDiscovery;
        String formatFlag = "";
        try {
            deviceDiscovery = new DeviceDiscovery();
            TimeUnit.SECONDS.sleep(timeout);
            DeviceDiscovery.persistToFile(deviceDiscovery.listDevices(),
                    discoId, formatFlag);
            if (devices != null) {
                for (KineticDevice device : deviceDiscovery.listDevices()) {
                    devices.add(device);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return discoId;
    }

    private String discoverDevicesViaScope(List<KineticDevice> devices,
            int timeout, String discoId, String startIp, String endIp) {
        DeviceDiscovery deviceDiscovery;
        String formatFlag = "";
        try {
            deviceDiscovery = new DeviceDiscovery(startIp, endIp);
            TimeUnit.SECONDS.sleep(timeout);
            DeviceDiscovery.persistToFile(deviceDiscovery.listDevices(),
                    discoId, formatFlag);
            if (devices != null) {
                for (KineticDevice device : deviceDiscovery.listDevices()) {
                    devices.add(device);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return discoId;
    }

    private String discoverDevices(int timeout) {
        String discoId = DRIVES_FILE_PREFIX + System.currentTimeMillis();
        String rootDir = TOOL_HOME + File.separator + "out" + File.separator
                + discoId;

        return discoverDevices(null, timeout, rootDir);
    }

    private RestResponse discover(DiscoverRequest request)
            throws NumberFormatException, IOException {
        DiscoverResponse response = new DiscoverResponse();
        List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
        List<KineticDevice> kineticDevices = null;
        List<DeviceId> kineticIds = request.getDevices();
        String discoId = request.getDiscoId();

        String defaultDiscoId = DRIVES_FILE_PREFIX + System.currentTimeMillis();

        discoId = discoId == null ? defaultDiscoId : discoId;

        String inputDiscoIdPath = ROOT_DIR + discoId;

        if (request.getScoped()) {
            if (request.getSubnet() != null || kineticIds != null) {
                return new ErrorResponse();
            }

            String startIp = request.getStartIp();
            String endIp = request.getEndIp();

            kineticDevices = new ArrayList<KineticDevice>();
            try {
                discoverDevicesViaScope(kineticDevices, request.getTimeout(),
                        inputDiscoIdPath, startIp, endIp);
            } catch (Exception e) {
                return new ErrorResponse();
            }
        } else if (request.getSubnet() == null && kineticIds == null) {
            kineticDevices = new ArrayList<KineticDevice>();
            try {
                discoverDevices(kineticDevices, request.getTimeout(),
                        inputDiscoIdPath);
            } catch (Exception e) {
                return new ErrorResponse();
            }
        } else {
            Command command = null;
            if (kineticIds != null) {
                command = new PingReachableDrive(kineticIds, inputDiscoIdPath,
                        request.getUseSsl(), request.getClversion(),
                        Long.parseLong(request.getIdentity()),
                        request.getKey(), request.getRequestTimeout());
            } else {
                command = new PingReachableDrive(request.getSubnet(),
                        inputDiscoIdPath, request.getUseSsl(),
                        request.getClversion(), Long.parseLong(request
                                .getIdentity()), request.getKey(),
                        request.getRequestTimeout());
            }
            Invoker invoker = new DefaultCommandInvoker();
            Report report = invoker.execute(command);
            kineticDevices = report.getSucceedDevices();
        }

        for (KineticDevice device : kineticDevices) {
            DeviceInfo dstatus = new DeviceInfo();
            dstatus.setDevice(device);
            device.setSerialNumber(device.getSerialNumber());
            devices.add(dstatus);
        }

        response.setDiscoId(discoId);
        response.setDevices(devices);
        return response;
    }

    private RestResponse ping(PingRequest request)
            throws NumberFormatException, IOException {
        PingResponse response = new PingResponse();
        List<DeviceId> kineticIds = request.getDevices();
        String discoId = request.getDiscoId();
        if (kineticIds == null && discoId == null) {
            discoId = discoverDevices(DEFAULT_PING_DISCOVER_TIMEOUT);
        }

        Command command = null;
        if (discoId == null) {
            command = new PingReachableDrive(kineticIds, PING_FILE_PREFIX
                    + System.currentTimeMillis(), request.getUseSsl(),
                    request.getClversion(), Long.parseLong(request
                            .getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            command = new PingReachableDrive(ROOT_DIR + discoId,
                    PING_FILE_PREFIX + System.currentTimeMillis(),
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        execCommandAndSetResp(response, command,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        return response;
    }

    private void execCommandAndSetResp(RestResponseWithStatus response,
            Command command, int failureRespCode) {
        Invoker invoker = new DefaultCommandInvoker();
        Report report = invoker.execute(command);

        List<DeviceStatus> respDevices = new ArrayList<DeviceStatus>();
        DeviceId device = null;
        DeviceStatus dstatus = null;

        for (KineticDevice kineticDevice : report.getSucceedDevices()) {
            device = initDevice(kineticDevice);
            dstatus = new DeviceStatus();
            dstatus.setDevice(device);
            respDevices.add(dstatus);
        }

        for (KineticDevice kineticDevice : report.getFailedDevices()) {
            device = initDevice(kineticDevice);
            dstatus = new DeviceStatus();
            dstatus.setDevice(device);
            dstatus.setStatus(failureRespCode);
            respDevices.add(dstatus);
        }

        response.setDevices(respDevices);
    }

    private DeviceId initDevice(KineticDevice kineticDevice) {
        DeviceId device;
        String[] ips;
        device = new DeviceId();
        device.setPort(kineticDevice.getPort());
        device.setTlsPort(kineticDevice.getTlsPort());
        device.setWwn(kineticDevice.getWwn());
        ips = new String[kineticDevice.getInet4().size()];
        ips = kineticDevice.getInet4().toArray(ips);
        device.setIps(ips);
        return device;
    }

    private RestResponse checkVersion(CheckVersionRequest request)
            throws NumberFormatException, IOException {
        CheckVersionResponse response = new CheckVersionResponse();

        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDevices(null);
            return response;
        }

        Invoker invoker = new DefaultCommandInvoker();
        Command command = null;
        if (discoId == null) {
            command = new CheckFirmwareVersion(
                    request.getExpectFirmwareVersion(), kineticIds,
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            command = new CheckFirmwareVersion(
                    request.getExpectFirmwareVersion(), ROOT_DIR + discoId,
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        Report report = invoker.execute(command);
        DeviceId device = null;
        DeviceStatus dstatus = null;
        List<DeviceStatus> respDevices = new ArrayList<DeviceStatus>();

        for (KineticDevice kineticDevice : report.getSucceedDevices()) {
            device = initDevice(kineticDevice);
            dstatus = new DeviceStatus();
            dstatus.setDevice(device);
            respDevices.add(dstatus);
        }

        for (KineticDevice kineticDevice : report.getFailedDevices()) {
            device = initDevice(kineticDevice);
            dstatus = new DeviceStatus();
            dstatus.setDevice(device);
            dstatus.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            dstatus.setMessage("Expect " + request.getExpectFirmwareVersion()
                    + " but " + kineticDevice.getFirmwareVersion());
            respDevices.add(dstatus);
        }

        response.setDevices(respDevices);

        return response;
    }

    private RestResponse setErasePin(SetErasePinRequest request)
            throws NumberFormatException, IOException {
        SetErasePinResponse response = new SetErasePinResponse();
        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDevices(null);
            return response;
        }

        Command command = null;
        if (discoId == null) {
            command = new SetErasePin(request.getOldPin(), request.getNewPin(),
                    kineticIds, request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            command = new SetErasePin(request.getOldPin(), request.getNewPin(),
                    ROOT_DIR + discoId, request.getUseSsl(),
                    request.getClversion(), Long.parseLong(request
                            .getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        execCommandAndSetResp(response, command,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        return response;
    }

    private RestResponse setLockPin(SetLockPinRequest request)
            throws NumberFormatException, IOException {
        SetLockPinResponse response = new SetLockPinResponse();
        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDevices(null);
            return response;
        }

        Command command = null;
        if (discoId == null) {
            command = new SetLockPin(request.getOldPin(), request.getNewPin(),
                    kineticIds, request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            command = new SetLockPin(request.getOldPin(), request.getNewPin(),
                    ROOT_DIR + discoId, request.getUseSsl(),
                    request.getClversion(), Long.parseLong(request
                            .getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        execCommandAndSetResp(response, command,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        return response;
    }

    private RestResponse instantErase(InstantEraseRequest request)
            throws NumberFormatException, IOException {
        InstantEraseResponse response = new InstantEraseResponse();
        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDevices(null);
            return response;
        }

        Command command = null;
        if (discoId == null) {
            command = new InstantErase(request.getPin(), kineticIds,
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            command = new InstantErase(request.getPin(), ROOT_DIR + discoId,
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        execCommandAndSetResp(response, command,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        return response;
    }

    private RestResponse secureErase(SecureEraseRequest request)
            throws NumberFormatException, IOException {
        SecureEraseResponse response = new SecureEraseResponse();
        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDevices(null);
            return response;
        }

        Command command = null;
        if (discoId == null) {
            command = new SecureErase(request.getPin(), kineticIds,
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            command = new SecureErase(request.getPin(), ROOT_DIR + discoId,
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        execCommandAndSetResp(response, command,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        return response;
    }

    private RestResponse lockDevice(LockDeviceRequest request)
            throws NumberFormatException, IOException {
        LockDeviceResponse response = new LockDeviceResponse();
        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDevices(null);
            return response;
        }

        Command command = null;
        if (discoId == null) {
            command = new LockDevice(kineticIds, request.getPin(),
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            command = new LockDevice(ROOT_DIR + discoId, request.getPin(),
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        execCommandAndSetResp(response, command,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        return response;
    }

    private RestResponse unLockDevice(UnLockDeviceRequest request)
            throws NumberFormatException, IOException {
        UnLockDeviceResponse response = new UnLockDeviceResponse();
        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDevices(null);
            return response;
        }

        Command command = null;
        if (discoId == null) {
            command = new UnLockDevice(kineticIds, request.getPin(),
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            command = new UnLockDevice(ROOT_DIR + discoId, request.getPin(),
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        execCommandAndSetResp(response, command,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        return response;
    }

    private RestResponse setClusterVersion(SetClusterVersionRequest request)
            throws NumberFormatException, IOException {
        SetClusterVersionResponse response = new SetClusterVersionResponse();
        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDevices(null);
            return response;
        }

        Command command = null;
        if (discoId == null) {
            command = new SetClusterVersion(String.valueOf(request
                    .getNewClversion()), kineticIds, request.getUseSsl(),
                    request.getClversion(), Long.parseLong(request
                            .getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            command = new SetClusterVersion(String.valueOf(request
                    .getNewClversion()), ROOT_DIR + discoId,
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        execCommandAndSetResp(response, command,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        return response;
    }

    private RestResponse setSecurity(SetSecurityRequest request)
            throws NumberFormatException, IOException {
        SetSecurityResponse response = new SetSecurityResponse();
        String discoId = request.getDiscoId();
        List<DeviceId> kineticIds = request.getDevices();
        if (kineticIds == null && (discoId == null || discoId.isEmpty())) {
            response.setDevices(null);
            return response;
        }

        List<ACL> acls = request.getAcl();
        Command command = null;
        if (discoId == null) {
            command = new SetSecurity(acls, kineticIds, request.getUseSsl(),
                    request.getClversion(), Long.parseLong(request
                            .getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        } else {
            command = new SetSecurity(acls, ROOT_DIR + discoId,
                    request.getUseSsl(), request.getClversion(),
                    Long.parseLong(request.getIdentity()), request.getKey(),
                    request.getRequestTimeout());
        }
        execCommandAndSetResp(response, command,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        return response;
    }
}
