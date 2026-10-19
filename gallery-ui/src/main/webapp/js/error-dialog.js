// Shows ZK's error alerts in the page's Bootstrap modal instead of a browser alert.
// The message is inserted as text: it can contain user input.
jq.alert = function (msg) {
    $("#errorMessage").text(msg);
    $("#myModal").modal();
};
