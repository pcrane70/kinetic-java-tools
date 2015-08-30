var BYTES_PER_GB = 1073741824;
var Kinetic = Kinetic || {};
var portal;
var driveStates;
var anyslider;

var debug = false;   // disable debug to get real drive ops line infomation dd
var debugPutOpsHistory = [];
var debugGetOpsHistory = [];
var debugDeleteOpsHistory = [];


Kinetic.Const = {
    BYTES_PER_KB: 1024,
    DATA_READY: 0,
}

Kinetic.Config = {
    // servlet requests url
    URL_DESCRIBE_ALL_DEVICES: "/servlet/ConsoleServlet?action=dscdevice",
    URL_DESCRIBE_HW_VIEW: "/kinetic/hwview",
    URL_DESCRIBE_SWIFT_RING: "data/ring.json",

    // images url
    IMAGE_DRIVE_NORMAL: "img/drive_green_1.png",
    IMAGE_DRIVE_UNREACHABLE: "img/drive_yellow_1.png",
    IMAGE_DRIVE_DOWN: "img/drive_red_1.png",

    // charts refresh period
    CHARTS_REFRESH_PERIOD_IN_SEC: 8,

    // drive line charts maximum data size
    CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE: 16,

    MAX_HASH_VALUE : "2^32"
}

Kinetic.State = {
    NORMAL: 0,
    UNREACHABLE: 1,
    DOWN: 2
};

Kinetic.Drive = function (slot, wwn, ip1, ip2) {
    this.slot = slot;
    this.wwn = wwn;
    this.ip1 = ip1;
    this.ip2 = ip2;
    this.port = 8123;
    this.tlsPort = 8443;
    this.rackId_ = 0;
    this.rackLoc_ = 0;
    this.chassisId_ = 0;
    this.state = 0;
    this.ready = 1;
    this.usedCap = 0;
    this.totalCap = 0;
    this.history = {
        putOps: [],
        getOps: [],
        deleteOps: [],
        putTrpt: [],
        getTrpt: [],
        deleteTrpt: []
    };
    this.currentStat = {
        totalPuts: 0,
        totalGets: 0,
        totalDeletes: 0,
        totalPutBytes: 0,
        totalGetBytes: 0,
        totalDeleteBytes: 0
    };
};

Kinetic.Drive.prototype.setRackId = function (rackId) {
    this.rackId_ = rackId;
};

Kinetic.Drive.prototype.getRackId = function () {
    return this.rackId_;
};

Kinetic.Drive.prototype.setRackLoc = function (rackLoc) {
    this.rackLoc_ = rackLoc;
};

Kinetic.Drive.prototype.getRackLoc = function () {
    return this.rackLoc_;
};

Kinetic.Drive.prototype.setChassisId = function (chassisId) {
    this.chassisId_ = chassisId;
};

Kinetic.Drive.prototype.getChassisId = function () {
    return this.chassisId_;
};

/*
 * Kinetic Chassis Abstraction
 */
Kinetic.Chassis = function (id, unit, mgtIp1, mgtIp2) {
    this.id = id;
    this.unit = unit;
    this.mgtIp1 = mgtIp1;
    this.mgtIp2 = mgtIp2;
    this.usedCap = 0;
    this.totalCap = 0;
    this.rackId_ = 0;
    this.drives_ = [];
};

Kinetic.Chassis.prototype.setRackId = function (rackId) {
    this.rackId_ = rackId;
};

Kinetic.Chassis.prototype.getRackId = function () {
    return this.rackId_;
};

Kinetic.Chassis.prototype.clearDrives = function () {
    this.drives_ = [];
};

Kinetic.Chassis.prototype.listDrives = function () {
    return this.drives_;
};

Kinetic.Chassis.prototype.addOrUpdateDrive = function (drive) {
    var index;
    var added = 0;
    for (index = 0; index < this.drives_.length; index++) {
        if (this.drives_[index].wwn == drive.wwn) {
            added = 1;
            this.drives_[index] = drive;
            break;
        }
    }

    if (added == 0) {
        this.drives_.push(drive);
    }
};


Kinetic.Rack = function (id, description, location) {
    this.id = id;
    this.description = description;
    this.location = location;
    this.chassises_ = [];
    this.totalDevices = 0;
    this.normalDevices = 0;
    this.unreachableDevices = 0;
    this.failedDevices = 0;
    this.usedCap = 0;
    this.totalCap = 0;
};

Kinetic.Rack.prototype.clearChassises = function () {
    this.chassises_ = [];
};

Kinetic.Rack.prototype.listChassises = function () {
    return this.chassises_;
};

Kinetic.Rack.prototype.addOrUpdateChassis = function (chassis) {
    var index;
    var added = 0;
    for (index = 0; index < this.chassises_.length; index++) {
        if (this.chassises_[index].id == chassis.id) {
            added = 1;
            this.chassises_[index] = chassis;
            break;
        }
    }

    if (added == 0) {
        this.chassises_.push(chassis);
    }
};

/*
 * Kinetic Portal
 */
Kinetic.Portal = function () {
    this.racks = [];
    this.rackMap = {};
    this.chassisMap = {};
    this.deviceMap = {};


    this._renderRackDropbox = function () {
        var index;
        var option;
        for (index = 0; index < this.racks.length; index++) {
            if (index == 0) {
                option = "<option selected=selected>" + this.racks[index].location + "</option>";
            } else {
                option = "<option>" + this.racks[index].location + "</option>";
            }

            $("#racks_dropbox").append(option);
        }
    };
};


Kinetic.Portal.prototype.getRackByDriveWwn = function (wwn) {
    var rackLoc = this.deviceMap[wwn].getRackLoc();
    return this.rackMap[rackLoc];
};

Kinetic.Portal.prototype.getRackLocationByDriveWwn = function (wwn) {
    return this.deviceMap[wwn].getRackLoc();
};

Kinetic.Portal.prototype.getDrive = function (wwn) {
    return this.deviceMap[wwn];
};

Kinetic.Portal.prototype.getChassis = function (rackLoc, chassisUnit) {
    var key = rackLoc + "/" + chassisUnit;
    return this.chassisMap[key];
};

Kinetic.Portal.prototype.updateChassisCap = function (rackLoc, chassisUnit) {
    var key = rackLoc + "/" + chassisUnit;
    var chassis = this.chassisMap[key];

    var deviceIndex;
    var device;
    var usedCap = 0, totalCap = 0;
    for (deviceIndex = 0; deviceIndex < chassis.listDrives().length; deviceIndex++)
    {
        device = chassis.listDrives()[deviceIndex];
        usedCap += device.usedCap;
        totalCap += device.totalCap;
    }

    chassis.usedCap = usedCap;
    chassis.totalCap = totalCap;

    return chassis;
};

Kinetic.Portal.prototype.updateRackCap = function (rackLoc) {
    var rack = this.rackMap[rackLoc];
    var i, j;

    var chassis, device;
    var usedCap = 0, totalCap = 0;
    for (i=0; i<rack.listChassises().length; i++)
    {
        chassis = rack.listChassises()[i];
        for(j=0; j< chassis.listDrives().length; j++)
        {
            device = chassis.listDrives()[j];
            usedCap += device.usedCap;
            totalCap += device.totalCap;
        }
    }

    rack.usedCap = usedCap;
    rack.totalCap = totalCap;

    return rack;
};

Kinetic.Portal.prototype.loadRackList = function () {
    var self = this;
    $.getJSON(Kinetic.Config.URL_DESCRIBE_HW_VIEW, function (data) {
        var json = data.hwconfig;
        var index;
        var rackObj;
        var rackJsonObj;
        var rackLocation;
        for (index = 0; index < json.racks.length; index++) {
            rackJsonObj = json.racks[index]
            rackLocation = rackJsonObj.coordinate.x + "_" + rackJsonObj.coordinate.y + "_" +
                rackJsonObj.coordinate.z;
            rackObj = new Kinetic.Rack(rackJsonObj.id, rackLocation, rackLocation);
            self.racks.push(rackObj);
            self.rackMap[rackLocation] = rackObj;

            rackObj.clearChassises();
            var i, j;
            var chassisObj;
            var chassisJsonObj;
            var driveObj;
            var driveJsonObj;
            var ip1;
            var ip2;

            for (i = 0; i < rackJsonObj.chassis.length; i++) {
                chassisJsonObj = rackJsonObj.chassis[i];
                if (chassisJsonObj.ips.length <= 0) {
                    ip1 = "";
                    ip2 = "";
                } else if ((chassisJsonObj.ips.length == 1)) {
                    ip1 = chassisJsonObj.ips[0];
                    ip2 = "";
                } else {
                    ip1 = chassisJsonObj.ips[0];
                    ip2 = chassisJsonObj.ips[1];
                }
                chassisObj = new Kinetic.Chassis(chassisJsonObj.id, chassisJsonObj.coordinate.x, ip1, ip2);
                chassisObj.setRackId(rackObj.id);
                for (j = 0; j < chassisJsonObj.devices.length; j++) {
                    driveJsonObj = chassisJsonObj.devices[j];
                    if (driveJsonObj.deviceId.ips.length <= 0) {
                        ip1 = "";
                        ip2 = "";
                    } else if ((driveJsonObj.deviceId.ips.length == 1)) {
                        ip1 = driveJsonObj.deviceId.ips[0];
                        ip2 = "";
                    } else {
                        ip1 = driveJsonObj.deviceId.ips[0];
                        ip2 = driveJsonObj.deviceId.ips[1];
                    }
                    driveObj = new Kinetic.Drive(driveJsonObj.coordinate.x, driveJsonObj.deviceId.wwn, ip1, ip2);
                    driveObj.port = driveJsonObj.deviceId.port;
                    driveObj.tlsPort = driveJsonObj.deviceId.tlsPort;
                    driveObj.setChassisId(chassisObj.id);
                    driveObj.setRackId(rackObj.id);
                    driveObj.setRackLoc(rackObj.location);
                    chassisObj.addOrUpdateDrive(driveObj);
                    self.deviceMap[driveJsonObj.deviceId.wwn] = driveObj;
                }
                rackObj.addOrUpdateChassis(chassisObj);
                self.chassisMap[rackLocation + "/" + chassisJsonObj.coordinate.x] = chassisObj;
            }
        }

        self._renderRackDropbox();

        if (json.racks.length > 0) {
            self.renderRack(self.racks[0]);
        }
    });
};

jQuery.fn.center = function () {
    this.css("position", "absolute");
    this.css("top", Math.max(0,
            (($(window).height() - $(this).outerHeight()) / 2)
            + $(window).scrollTop())
        + "px");
    this.css("left", Math.max(0,
            (($(window).width() - $(this).outerWidth()) / 2)
            + $(window).scrollLeft())
        + "px");
    return this;
};

function reRenderChassisUnitInfo() {
    $("#chassis_unit_info").empty();
    var selectedRackLocation = $("#racks_dropbox option:selected").text();

    var visibleSlide = anyslider.currentSlide();
    var rack, chassis;
    var i;
    for (i = 0; i < portal.racks.length; i++) {
        rack = portal.racks[i];
        if (rack.location == selectedRackLocation) {
            if (visibleSlide == 0 && rack.listChassises().length > 0) {
                visibleSlide = 1;
            }
            chassis = rack.listChassises()[visibleSlide - 1];
        }
    }

    var chassis = portal.updateChassisCap(selectedRackLocation, visibleSlide);
    var freeCapByGB = ((chassis.totalCap - chassis.usedCap)/BYTES_PER_GB).toFixed(2);
    var totalCapByGB = (chassis.totalCap/BYTES_PER_GB).toFixed(2);

    $("#chassis_unit_info").append("Unit: " + chassis.unit + "<br/>("
        + freeCapByGB + "GB Free/" +  totalCapByGB + "GB Total)");
}

function showDriveInfo(wwn) {
    var drive = portal.getDrive(wwn);
    $("#nodeInfoContainer").remove();

    var driveState = drive.state;
    var driveReady = drive.ready;
    if (driveReady != Kinetic.Const.DATA_READY || driveState != Kinetic.State.NORMAL) {
        $("#nodeInfo").append("<div id='nodeInfoContainer'></div>");
        $("#nodeInfoContainer").append("<a class='button'>" + drive.wwn
            + "(" + drive.ip1 + ", "
            + drive.ip2 + ")" + "</a>");
        $("#capacity").remove();
        $("#temperature").remove();
        $("#utilizations").remove();
        $("#counters").remove();
        $("#nodeInfoContainer").append(
            "<div style='text-align: center;'><img src='img/Not_available_icon.png' style='margin-top: 50px'/></div>");
        return false;
    }

    var driveIndex;
    var driveLog;
    for (driveIndex = 0; driveIndex < driveStates.length; driveIndex++) {
        if (driveStates[driveIndex].wwn == drive.wwn) {
            driveLog = driveStates[driveIndex].log;
            break;
        }
    }
    $("#nodeInfo").append(
        "<div id='nodeInfoContainer'" + ">"
        + "<a class='button'>" + drive.wwn
        + "(" + drive.ip1 + ", "
        + drive.ip2 + ")" + "</a>"
        + "<div id='capacity' class='capacity'></div>"
        + "<div id='temperature' class='temperature'>"
        + "<text id='temperature_title'>Temperature(℃)</text>"
        + "<div id='temperature_hda' class='temperature_hda'></div>"
        + "<div id='temperature_cpu' class='temperature_cpu'></div>"
        + "</div>"
        + "<div id='utilizations' class='utilizations'></div>"
        + "<div id='counters' class='counters'></div>"
        + "</div>");

    $("#temperature_title").hide();

    renderCapacity(driveLog);
    renderTemperature(driveLog);
    renderUtilizations(driveLog);
    renderCounters(driveLog);
}

function renderCapacity(nodeInfo) {
    var used = nodeInfo.capacity.portionFull * nodeInfo.capacity.nominalCapacityInBytes;
    var remaining = nodeInfo.capacity.nominalCapacityInBytes - used;
    var freePercentage = 1 - nodeInfo.capacity.portionFull;

    if ($('#capacity').length > 0) {
        google.load('visualization', '1.0', {
            'packages': ['corechart'], 'callback': function () {
                var data = google.visualization.arrayToDataTable([
                    ['Capacity', 'Current'],
                    ['Remaining', remaining],
                    ['Used', used]
                ]);

                var options = {
                    title: 'Capacity (B)',
                    is3D: true,
                    legend: {alignment: 'center', position: 'bottom'}
                };

                var chart = new google.visualization.PieChart(document.getElementById('capacity'));
                chart.draw(data, options);
            }
        });
    }
}

function renderTemperature(nodeInfo) {
    $("#temperature_title").show();

    if ($('#temperature_hda').length > 0) {
        google.load('visualization', '1.0', {
            'packages': ['gauge'], 'callback': function () {
                var tMax = nodeInfo.temperature[0].max;
                var tMin = nodeInfo.temperature[0].min;
                var tTarget = nodeInfo.temperature[0].target;

                var data = google.visualization.arrayToDataTable([
                    ['Label', 'Value'],
                    ['HDA', nodeInfo.temperature[0].current]
                ]);

                var options = {
                    width: 125, height: 100,
                    redFrom: tTarget, redTo: tMax,
                    yellowFrom: tMin, yellowTo: tTarget,
                    greenFrom: 0, greenTo: tMin,
                    minorTicks: 5
                };

                var chart = new google.visualization.Gauge(document.getElementById('temperature_hda'));
                chart.draw(data, options);

                $('#temperature_hda_comments').remove();
                $('#temperature_hda').append("<text id='temperature_hda_comments'>Min:&nbsp;"
                    + tMin + "℃<br>Tgt:&nbsp;"
                    + tTarget + "℃<br>Max:&nbsp;"
                    + tMax + "℃</text>");
            }
        });
    }

    if ($('#temperature_cpu').length > 0) {
        google.load('visualization', '1.0', {
            'packages': ['gauge'], 'callback': function () {
                var tMax = nodeInfo.temperature[1].max;
                var tMin = nodeInfo.temperature[1].min;
                var tTarget = nodeInfo.temperature[1].target;

                var data = google.visualization.arrayToDataTable([
                    ['Label', 'Value'],
                    ['CPU', nodeInfo.temperature[1].current]
                ]);

                var options = {
                    width: 125, height: 100,
                    redFrom: tTarget, redTo: tMax,
                    yellowFrom: tMin, yellowTo: tTarget,
                    greenFrom: 0, greenTo: tMin,
                    minorTicks: 5
                };

                var chart = new google.visualization.Gauge(document.getElementById('temperature_cpu'));
                chart.draw(data, options);

                $('#temperature_cpu_comments').remove();
                $('#temperature_cpu').append("<text id='temperature_cpu_comments'>Min:&nbsp;"
                    + tMin + "℃<br>Tgt:&nbsp;"
                    + tTarget + "℃<br>Max:&nbsp;"
                    + tMax + "℃</text>");
            }
        });
    }
}

function renderUtilizations(nodeInfo) {
    if ($('#utilizations').length > 0) {
        google.load("visualization", "1.0", {
            packages: ["corechart"], "callback": function () {
                var data = google.visualization.arrayToDataTable([
                    ["Type", "Utilizations (%)"],
                    ["HDA", Math.floor(nodeInfo.utilization[0].utility * 100)],
                    ["EN0", Math.floor(nodeInfo.utilization[1].utility * 100)],
                    ["EN1", Math.floor(nodeInfo.utilization[2].utility * 100)],
                    ["CPU", Math.floor(nodeInfo.utilization[3].utility * 100)],
                ]);

                var view = new google.visualization.DataView(data);
                view.setColumns([0, 1,
                    {
                        calc: "stringify",
                        sourceColumn: 1,
                        type: "string",
                        role: "annotation"
                    }]);

                var options = {
                    title: "Utilizations (%)",
                    width: 250,
                    height: 200,
                    bar: {groupWidth: "65%"},
                    legend: {position: "none"},
                    hAxis: {maxValue: 100, minValue: 0}
                };
                var chart = new google.visualization.ColumnChart(document.getElementById("utilizations"));
                chart.draw(view, options);
            }
        });
    }
}

function renderCounters(nodeInfo) {
    var operationCounters = {};
    var bytesCounters = {};
    var counter;
    for (var i = 0; i < nodeInfo.statistics.length; i++) {
        counter = nodeInfo.statistics[i];
        if (counter.messageType == 'GET') {
            operationCounters.GET = counter.count;
            bytesCounters.GET = counter.bytes;
        } else if (counter.messageType == 'PUT') {
            operationCounters.PUT = counter.count;
            bytesCounters.PUT = counter.bytes;
        } else if (counter.messageType == 'DELETE') {
            operationCounters.DELETE = counter.count;
            bytesCounters.DELETE = counter.bytes;
        } else if (counter.messageType == 'GETNEXT') {
            operationCounters.GETNEXT = counter.count;
            bytesCounters.GETNEXT = counter.bytes;
        } else if (counter.messageType == 'GETPREVIOUS') {
            operationCounters.GETPREVIOUS = counter.count;
            bytesCounters.GETPREVIOUS = counter.bytes;
        } else if (counter.messageType == 'GETKEYRANGE') {
            operationCounters.GETKEYRANGE = counter.count;
            bytesCounters.GETKEYRANGE = counter.bytes;
        } else if (counter.messageType == 'GETVERSION') {
            operationCounters.GETVERSION = counter.count;
            bytesCounters.GETVERSION = counter.bytes;
        } else if (counter.messageType == 'SETUP') {
            operationCounters.SETUP = counter.count;
            bytesCounters.SETUP = counter.bytes;
        } else if (counter.messageType == 'GETLOG') {
            operationCounters.GETLOG = counter.count;
            bytesCounters.GETLOG = counter.bytes;
        } else if (counter.messageType == 'SECURITY') {
            operationCounters.SECURITY = counter.count;
            bytesCounters.SECURITY = counter.bytes;
        } else if (counter.messageType == 'PEER2PEERPUSH') {
            operationCounters.PEER2PEERPUSH = counter.count;
            bytesCounters.PEER2PEERPUSH = counter.bytes;
        }
    }
    if ($('#counters').length > 0) {
        google.load('visualization', '1.0', {
            'packages': ['corechart'], 'callback': function () {
                var data = google.visualization.arrayToDataTable([
                    ['Type', 'Operations (times)', 'Bytes (KB)'],
                    ['GET', operationCounters.GET, bytesCounters.GET / Kinetic.Const.BYTES_PER_KB],
                    ['PUT', operationCounters.PUT, bytesCounters.PUT / Kinetic.Const.BYTES_PER_KB],
                    ['DELETE', operationCounters.DELETE, bytesCounters.DELETE / Kinetic.Const.BYTES_PER_KB],
                    ['GETNEXT', operationCounters.GETNEXT, bytesCounters.GETNEXT / Kinetic.Const.BYTES_PER_KB],
                    ['GETPREVIOUS', operationCounters.GETPREVIOUS, bytesCounters.GETPREVIOUS / Kinetic.Const.BYTES_PER_KB],
                    ['GETKEYRANGE', operationCounters.GETKEYRANGE, bytesCounters.GETKEYRANGE / Kinetic.Const.BYTES_PER_KB],
                    ['GETVERSION', operationCounters.GETVERSION, bytesCounters.GETVERSION / Kinetic.Const.BYTES_PER_KB],
                    ['SETUP', operationCounters.SETUP, bytesCounters.SETUP / Kinetic.Const.BYTES_PER_KB],
                    ['GETLOG', operationCounters.GETLOG, bytesCounters.GETLOG / Kinetic.Const.BYTES_PER_KB],
                    ['SECURITY', operationCounters.SECURITY, bytesCounters.SECURITY / Kinetic.Const.BYTES_PER_KB],
                    ['PEER2PEERPUSH', operationCounters.PEER2PEERPUSH, bytesCounters.PEER2PEERPUSH / Kinetic.Const.BYTES_PER_KB]
                ]);

                var options = {
                    title: 'Operation and Bytes Counters',
                    legend: {alignment: 'center', position: 'bottom'},
                    hAxis: {minTextSpacing: 6, textStyle: {fontSize: 8}},
                    chartArea: {height: '50%'}
                };

                var chart = new google.visualization.ColumnChart(document.getElementById("counters"));
                chart.draw(data, options);
            }
        });
    }
}

function refreshChartsAndTables() {
    var device;
    var index;
    var rackTotalDevices = {};
    var rackNormalDevices = {};
    var rackFailedDevices = {};
    var rackUnreachableDevices = {};

    var rack;
    var rackIndex;
    for (rackIndex = 0; rackIndex < portal.racks.length; rackIndex++) {
        rack = portal.racks[rackIndex];
        rackTotalDevices[rack.location] = 0;
        rackNormalDevices[rack.location] = 0;
        rackFailedDevices[rack.location] = 0;
        rackUnreachableDevices[rack.location] = 0;
    }

    var rackLocation;
    for (index = 0; index < driveStates.length; index++) {
        device = portal.getDrive(driveStates[index].wwn);
        rackLocation = portal.getRackLocationByDriveWwn(device.wwn);
        device.ready = driveStates[index].ready;
        device.state = driveStates[index].state;

        if (device.ready == 0) {
            device.totalCap = driveStates[index].log.capacity.nominalCapacityInBytes;
            device.usedCap = driveStates[index].log.capacity.nominalCapacityInBytes
                * driveStates[index].log.capacity.portionFull;

            if (device.currentStat.totalPuts == 0 && device.currentStat.totalGets == 0
                && device.currentStat.totalDeletes == 0 && device.currentStat.totalPutBytes == 0
                && device.currentStat.totalGetBytes == 0 && device.currentStat.totalDeleteBytes == 0) {
                device.history.putOps = [];
                device.history.getOps = [];
                device.history.deleteOps = [];
                device.history.putTrpt = [];
                device.history.getTrpt = [];
                device.history.deleteTrpt = [];
                device.currentStat.totalPuts = driveStates[index].log.statistics[0].count;
                device.currentStat.totalGets = driveStates[index].log.statistics[1].count;
                device.currentStat.totalDeletes = driveStates[index].log.statistics[2].count;
                device.currentStat.totalPutBytes = driveStates[index].log.statistics[0].bytes;
                device.currentStat.totalGetBytes = driveStates[index].log.statistics[1].bytes;
                device.currentStat.totalDeleteBytes = driveStates[index].log.statistics[2].bytes;
            } else {
                if (device.history.putOps.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE) {
                    device.history.putOps.shift();
                } else if (device.history.getOps.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE) {
                    device.history.getOps.shift();
                } else if (device.history.deleteOps.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE) {
                    device.history.deleteOps.shift();
                } else if (device.history.putTrpt.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE) {
                    device.history.putTrpt.shift();
                } else if (device.history.getTrpt.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE) {
                    device.history.getTrpt.shift();
                } else if (device.history.deleteTrpt.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE) {
                    device.history.deleteTrpt.shift();
                }

                device.history.putOps.push(driveStates[index].log.statistics[0].count - device.currentStat.totalPuts);
                device.history.getOps.push(driveStates[index].log.statistics[1].count - device.currentStat.totalGets);
                device.history.deleteOps.push(driveStates[index].log.statistics[2].count - device.currentStat.totalDeletes);
                device.history.putTrpt.push(driveStates[index].log.statistics[0].bytes - device.currentStat.totalPutBytes);
                device.history.getTrpt.push(driveStates[index].log.statistics[1].bytes - device.currentStat.totalGetBytes);
                device.history.deleteTrpt.push(driveStates[index].log.statistics[2].bytes - device.currentStat.totalDeleteBytes);
            }
        }
        ++rackTotalDevices[rackLocation];

        if (device.state == Kinetic.State.NORMAL) {
            ++rackNormalDevices[rackLocation];
            $("#" + device.wwn).attr("src", Kinetic.Config.IMAGE_DRIVE_NORMAL);
        } else if (device.state == Kinetic.State.UNREACHABLE) {
            ++rackUnreachableDevices[rackLocation];
            $("#" + device.wwn).attr("src", Kinetic.Config.IMAGE_DRIVE_UNREACHABLE);
        } else {
            ++rackFailedDevices[rackLocation];
            $("#" + device.wwn).attr("src", Kinetic.Config.IMAGE_DRIVE_DOWN);
        }
    }

    for (rackIndex = 0; rackIndex < portal.racks.length; rackIndex++) {
        rack = portal.racks[rackIndex];
        rack.totalDevices = rackTotalDevices[rack.location];
        rack.normalDevices = rackNormalDevices[rack.location];
        rack.failedDevices = rackFailedDevices[rack.location];
        rack.unreachableDevices = rackUnreachableDevices[rack.location];

        var selectedRackLocation = $("#racks_dropbox option:selected").text();
        if (rack.location == selectedRackLocation) {
            if ($("#drive_state_stats").length) {
                $("#drive_state_stats").empty();
                $("#drive_state_stats").append(rack.normalDevices + "/" + rack.totalDevices);
            }

            var freeCapByGB = ((rack.totalCap - rack.usedCap)/BYTES_PER_GB).toFixed(2);
            var totalCapByGB = (rack.totalCap/BYTES_PER_GB).toFixed(2);
            if ($("#drive_cap_stats").length) {
                portal.updateRackCap(rack.location);
                $("#drive_cap_stats").empty();
                $("#drive_cap_stats").append(freeCapByGB + "/" + totalCapByGB + "(GB)");
            }
        }
    }
}