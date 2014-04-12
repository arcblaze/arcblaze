<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: Employees</title>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>

    <div id="container-maxwidth">
      <%@ include file="/ssi/manager_navigation.jspf" %>

      <!-- This is where the employee grid and panels will be rendered. -->
      <div id="employee-management-grid"></div>
      <div id="employee-add-panel"></div>
      <div id="employee-update-panel"></div>
      <div id="employee-contract-grid"></div>
      <div id="assignment-add-panel"></div>
      <div id="assignment-update-panel"></div>

      <!-- Add the employee management scripts. -->
      <script src="/js/src/util/io/ServerIO.js"></script>
      <script>
        // Invoked when the page is ready.
        Ext.onReady(function() {
            // Create the grid.
	        new ui.grid.manager.EmployeeGrid({
                // Specify where the grid will be rendered.
                renderTo: 'employee-management-grid'
            });
        });
      </script>

    </div>

    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
