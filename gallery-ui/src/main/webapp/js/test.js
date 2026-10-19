jq.alert = function (msg, opts) {
//    var label = $("#errorMessage").val(msq);
    $("#errorMessage").empty();
    $("#errorMessage").append(msg);
    $("#myModal").modal();
//    var label = $("#errorMessage").text(msq);
}