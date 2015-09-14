$(document).ready(function () {
	$.getJSON(Kinetic.Config.URL_LIST_HARDWARE_VIEW_FILES, function (json) {
		var index;
        var option = "<option selected=selected>" + "" + "</option>";
        $("#hwv_dropbox").append(option);
        for (index = 0; index < json.length; index++) {
            option = "<option>" + json[index] + "</option>";
           $("#hwv_dropbox").append(option);
        }
        
        
        $("#hwv_dropbox").change(function () {
	        var selectedhwv = $("#hwv_dropbox option:selected").text();
	        $.getJSON(Kinetic.Config.URL_SELECT_HARDWARE_VIEW_FILE + "&filename=" + selectedhwv, function (json) {	
	        });
	        setTimeout(function(){
	        	location.href="overview.html";
	        }, 1000);
	    });
	});
});
