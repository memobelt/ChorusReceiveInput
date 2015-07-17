
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("sendPushToUser", function(request, response) {
  var email = request.params.email;
  var message = request.params.message;
  var iRole = request.params.role;
  var iTask = request.params.task;

  if (message.length > 140) {
    message = message.substring(0, 137) + "...";
  }

  // Send the push.
  var pushQuery = new Parse.Query(Parse.Installation);
  pushQuery.equalTo("channels", "Chorus");
  // Comment this out to receive only for the task that user has entered in
  // pushQuery.equalTo("email", email);
 
  // Send the push notification to results of the query
  Parse.Push.send({
    where: pushQuery,
    data: {
      alert: iRole + ": " + message,
      role: iRole,
      task: iTask
    }
  }).then(function() {
      response.success("Push was sent successfully.")
  }, function(error) {
      response.error("Push failed to send with error: " + error.message);
  });
});