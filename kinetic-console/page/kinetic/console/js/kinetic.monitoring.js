Kinetic.Portal.prototype.renderRack = function (rack) {
    $("#overview").empty();
    $("#detail").empty();
    var self = this;
    var totalDrives = 0;
    var normalDrives = 0;

    // render the rack overview chart
    var chassisArray = rack.listChassises();
    var driveArray;
    $("#overview").append("<div id='rackContainer' class='rack slider'></div>");
    var chassisIndex;
    var driveIndex;
    var stateIndex;
    var currentState = Kinetic.State.NORMAL;
    for (chassisIndex = 0; chassisIndex < chassisArray.length; chassisIndex++) {
        var cid = "chassis" + chassisArray[chassisIndex].id;
        var chassisDiv = "<div><div id=" + cid + " class=chassis></div></div>";
        $("#rackContainer").append(chassisDiv);

        driveArray = chassisArray[chassisIndex].listDrives();
        for (driveIndex = 0; driveIndex < driveArray.length; driveIndex++) {
            var eId = driveArray[driveIndex].wwn.replace(/\s+/g,'_');

            ++totalDrives;
            var drivehtml = "<span class='drive_box'><img class='drive_img' ";
            drivehtml += " id='" + eId + "'";

            currentState = this.getDrive(driveArray[driveIndex].wwn).state;
            if (currentState == Kinetic.State.NORMAL) {
                ++normalDrives;
                drivehtml += " src=img/drive_green_1.png />";
            } else if (currentState == Kinetic.State.UNREACHABLE) {
                drivehtml += " src=img/drive_yellow_1.png />";
            } else {
                drivehtml += " src=img/drive_red_1.png />";
            }

            drivehtml += "<div><div>" + driveArray[driveIndex].wwn
                + "</div><div>" + driveArray[driveIndex].ip1 + "(" + driveArray[driveIndex].ip2 + "):" + driveArray[driveIndex].port
                + "</div></div>";
            drivehtml += "<div class='jump'></div><div class='line_chart'><span class='inlinesparkline' id='sparkline_"
                + eId + "'></span></div></span>";
            $("#" + cid).append(drivehtml);

            (function () {  $('#' + driveArray[driveIndex].wwn.replace(/\s+/g,'_')).tooltipster({
                content: $('<samp>WWN: ' + driveArray[driveIndex].wwn + '</samp></br>'
                    + '<samp>IP1: ' + driveArray[driveIndex].ip1 + '</samp></br>'
                    + '<samp>IP2: ' + driveArray[driveIndex].ip2 + '</samp></br>'
                    + '<samp>UNIT: ' + chassisArray[chassisIndex].unit + '</samp></br>')
            })})();

            (function () {  $('#' + driveArray[driveIndex].wwn.replace(/\s+/g,'_')).click(function () {
                var wwn = $(this).attr("id");
                selectedDrive = wwn.replace(/\_/g,' ');
                showDriveInfo(selectedDrive);
                $("#nodeInfo").center();
                $("#nodeInfo").show();
                return false;
            })})();
        }

        $("#" + cid).click(function () {
            return false;
        });
    }

    refreshChartsAndTables();

    var slider = $('#rackContainer').anyslider({
            interval: 0,
            keyboard: false,
            speed: 1500,
            afterChange: reRenderChassisUnitInfo
        }
    );

    anyslider = slider.data('anyslider');
    anyslider.goTo(1);
    reRenderChassisUnitInfo();

    // anyslide will clone the chassis elements and cause that they have same id, manually remove the replicated id to
    // fix the bug brought by anyslider clone
    $(".clone > .chassis").removeAttr("id");
    $(".clone > .chassis > .drive_box > .line_chart >.inlinesparkline").removeAttr("id");


    setInterval(function () {
    	$(".jqstooltip").css("height","40px");
        $(".jqstooltip").css("width","80px");
        
        if (debug)
        {
            if(debugPutOpsHistory.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE)
            {
                debugPutOpsHistory.shift();
            }else if(debugGetOpsHistory.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE){
                debugGetOpsHistory.shift();
            }else if(debugDeleteOpsHistory.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE){
                debugDeleteOpsHistory.shift();
            }

            debugPutOpsHistory.push(Math.floor((Math.random() * 100) + 1));
            debugGetOpsHistory.push(Math.floor((Math.random() * 100) + 1));
            debugDeleteOpsHistory.push(Math.floor((Math.random() * 100) + 1));
        }

        var i, j, k, l;
        var rack, chassis, drive;
        var selectedRack = $("#racks_dropbox option:selected").text();
        for (i = 0; i < portal.racks.length; i++) {
            rack = portal.racks[i];
            if (rack.location == selectedRack) {
                for (j = 0; j < rack.listChassises().length; j++) {
                    chassis = rack.listChassises()[j];
                    
                    var chassisLatestPutOps = 0;
                    var chassisLatestGetOps = 0;
                    var chassisLatestDeleteOps = 0;
                    for (k = 0; k < chassis.listDrives().length; k++) {
                        drive = chassis.listDrives()[k];
                        var id = "sparkline_" + drive.wwn;
                        var putOps = drive.history.putOps;
                        var getOps = drive.history.getOps;
                        var deleteOps = drive.history.deleteOps;
                        if (debug)
                        {
                            putOps = debugPutOpsHistory;
                            getOps = debugGetOpsHistory;
                            deleteOps = debugDeleteOpsHistory;
                        }
                        
                        
                        chassisLatestPutOps += putOps.length > 0 ? putOps[putOps.length -1] : 0;
                        chassisLatestGetOps += getOps.length > 0 ? getOps[getOps.length -1] : 0;
                        chassisLatestDeleteOps += deleteOps.length > 0 ? deleteOps[deleteOps.length -1] : 0;

                        if (drive.state != Kinetic.State.NORMAL)
                        {
                            putOps = [];
                            getOps = [];
                            deleteOps = [];
                        }

                        $('#' + id).sparkline(putOps, {
                            lineColor: 'blue',
                            fillColor: false,
                            width: 170,
                            height: 40,
                            tooltipPrefix: "PutOps: ",
                            tooltipClassname: "sparkline_tooltip_cls"
                        });

                        $('#' + id).sparkline(getOps, {
                            lineColor: 'red',
                            fillColor: false,
                            composite: true,
                            tooltipPrefix: "GetOps: ",
                            tooltipClassname: "sparkline_tooltip_cls"
                        });

                        $('#' + id).sparkline(deleteOps, {
                            lineColor: 'yellow',
                            fillColor: false,
                            composite: true,
                            tooltipPrefix: "DeleteOps: ",
                            tooltipClassname: "sparkline_tooltip_cls"
                        });
                    }
                    
                    if (chassis.history.putOps.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE) {
                    	chassis.history.putOps.shift();
                    } else if (chassis.history.getOps.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE) {
                    	chassis.history.getOps.shift();
                    } else if (chassis.history.deleteOps.length >= Kinetic.Config.CHARTS_DRIVE_HISTORY_LINE_MAX_DATA_SIZE) {
                    	chassis.history.deleteOps.shift();
                    }
                    
                    chassis.history.putOps.push(chassisLatestPutOps);
                    chassis.history.getOps.push(chassisLatestGetOps);
                    chassis.history.deleteOps.push(chassisLatestDeleteOps);
                }
            }
        }
    }, 3000);
};

$(document).ready(function () {
    $("#nodeInfo").hide();
    $("#chassisStatInfo").hide();
    $.getJSON(Kinetic.Config.URL_DESCRIBE_ALL_DEVICES, function (json) {
        driveStates = json;

        $.getJSON(Kinetic.Config.URL_DESCRIBE_HW_VIEW, function (hwdata) {
            portal = new Kinetic.Portal();
            portal.loadRackList(hwdata);

            $("#racks_dropbox").change(function () {
                var selectedRack = $("#racks_dropbox option:selected").text();

                var racks = portal.racks;
                var index;
                for (index = 0; index < racks.length; index++) {
                    if (selectedRack == racks[index].location) {
                        portal.renderRack(racks[index]);
                    }
                }
            });

            $("#nodeInfo").click(function () {
                selectedDrive = "";
                $("#nodeInfo").slideUp();
            });

            $("#chassisStatInfo").click(function () {
                selectedChassis = "";
                $("#chassisStatInfo").slideUp();
            });

            setInterval(function () {
                $.getJSON(Kinetic.Config.URL_DESCRIBE_ALL_DEVICES, function (json1) {
                    driveStates = json1;
                    refreshChartsAndTables();
                });
            }, Kinetic.Config.CHARTS_REFRESH_PERIOD_IN_SEC * 1000);

            setInterval(function () {
                if (selectedDrive != "" )
                {
                    var driveIndex;
                    var driveLog;
                    for (driveIndex = 0; driveIndex < driveStates.length; driveIndex++) {
                        if (driveStates[driveIndex].wwn == selectedDrive) {
                            driveLog = driveStates[driveIndex].log;
                            break;
                        }
                    }

                    if (capacityChart != undefined)
                    {
                        reRenderCapacity(capacityChart, driveLog);
                    }

                    if (temperatureHdaChart != undefined && temperatureCpuChart != undefined)
                    {
                        reRenderTemperature(temperatureHdaChart, temperatureCpuChart, driveLog);
                    }

                    if (utilizationChart != undefined)
                    {
                        reRenderUtilizations(utilizationChart, driveLog);
                    }

                    if (counterChart != undefined)
                    {
                        reRenderCounters(counterChart, driveLog);
                    }
                }
            }, 5000);

            setInterval(function () {
                if (selectedChassis != "")
                {
                    updateChassisStatInfo();
                }
            }, 6000);

            $("#chassis_unit_info").click(function () {
                selectedChassis = anyslider.currentSlide();
                $("#chassisStatInfo").center();
                $("#chassisStatInfo").show();
                showChassisStatInfo();
                return false;
            });
        });
    });
});
