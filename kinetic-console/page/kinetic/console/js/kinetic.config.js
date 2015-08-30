Kinetic.Portal.prototype.renderRack = function (rack) {
    // Do nothing
}

Kinetic.Portal.prototype.renderPartitionTable = function (json) {
    $("#detail").empty();
    var self = this;

    // ring abstraction
    $("#detail").append("<h3></h3>");
    $("#detail").append("<table id='partition_abstract' class='table table-bordered'></table>");

    var theadContent = "";
    theadContent += "<thead><tr class='info'>";
    theadContent += "<th><strong>Total Partitions</strong></th>";
    theadContent += "<th><strong>Total Drives</strong></th>";
    theadContent += "</tr></thead>";
    $("#partition_abstract").append(theadContent);

    var tableContent = "";
    tableContent += "<tbody>";
    tableContent += "<tr class='warning'>";
    tableContent += "<td>" + json.length + "</td>";
    tableContent += "<td>" + driveStates.length + "</td>";
    tableContent += "</tr>";
    tableContent += "</tbody>";
    $("#partition_abstract").append(tableContent);

    $("#detail").append("<h3></h3>");
    $("#detail").append("<table id='ring_abstract' class='table table-bordered'></table>");

    theadContent = "";
    theadContent += "<thead><tr class='info'>";
    theadContent += "<th><strong>Partition#</strong></th>";
    theadContent += "<th><strong>Drives Id</strong></th>";
    theadContent += "</tr></thead>";
    $("#ring_abstract").append(theadContent);


    var driveList;
    tableContent = "";
    tableContent += "<tbody>";
    var partitionId, drivesArray;
    for (i = 0; i <= json.length - 1; i++) {
        partitionId = json[i].partitionId;
        drivesArray = json[i].driveIds;

        tableContent += "<tr class='warning'>";
        tableContent += "<td>" + partitionId + "</td>";

        tableContent += "<td>";
        for (j = 0; j < drivesArray.length; j++) {
            tableContent += drivesArray[j];
            tableContent += "; "
        }
        tableContent += "</td>";
        tableContent += "</tr>";
    }
    tableContent += "</tbody>";
    $("#ring_abstract").append(tableContent);

};

$(document).ready(function () {
	$.getJSON(Kinetic.Config.URL_DESCRIBE_ALL_DEVICES, function (json) {
		driveStates = json;
        portal = new Kinetic.Portal();
        portal.loadRackList();

        $.getJSON(Kinetic.Config.URL_DESCRIBE_SWIFT_RING, function (ring) {
            portal.renderPartitionTable(ring);
        });
	});
});