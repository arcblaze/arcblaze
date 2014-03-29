<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: Timesheets</title>
    <%@ include file="/ssi/meta.jspf" %>
    <link rel="stylesheet" href="/css/timesheet.css"/>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>

    <div id="container">
      <!-- This is where the timesheet is injected into the page. -->
      <div id="timesheet-div"></div>

      <!-- Add the timesheet scripts. -->
      <script src="/js/src/util/io/ServerIO.js"></script>
      <script src="/js/src/timesheet/timesheet.js"></script>
      <script src="/js/src/timesheet/inactivityTimer.js"></script>
      <script src="/js/src/timesheet/choosePayPeriod.js"></script>
      <script src="/js/src/timesheet/cellManagement.js"></script>
      <script src="/js/src/action/timesheet/DoComplete.js"></script>
      <script src="/js/src/action/timesheet/DoFix.js"></script>
      <script src="/js/src/action/timesheet/DoSave.js"></script>

      <script>
          // Do the post-startup activities.
          Ext.onReady(function() {
              // Retrieve and display the timesheet.
              retrieveCurrentTimesheet('timesheet-div', user);
          });
      </script>
    </div>

    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
