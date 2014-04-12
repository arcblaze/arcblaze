<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: Holidays</title>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>

    <div id="container-maxwidth">
      <%@ include file="/ssi/manager_navigation.jspf" %>
      
      <!-- This is where the holiday grid and panels will be rendered. -->
      <div id="holiday-management-grid"></div>
      <div id="holiday-add-panel"></div>
      <div id="holiday-update-panel"></div>

      <!-- Add the holiday management scripts. -->
      <script src="/js/src/util/io/ServerIO.js"></script>
      <script src="/js/src/data/model/Holiday.js"></script>
      <script src="/js/src/data/store/manager/HolidayStore.js"></script>
      <script src="/js/src/action/manager/holiday/DoHolidayAdd.js"></script>
      <script src="/js/src/action/manager/holiday/DoHolidayDelete.js"></script>
      <script src="/js/src/action/manager/holiday/DoHolidaySearch.js"></script>
      <script src="/js/src/action/manager/holiday/ShowHolidayAdd.js"></script>
      <script src="/js/src/action/manager/holiday/ShowHolidayGrid.js"></script>
      <script src="/js/src/ui/panel/manager/HolidayAddPanel.js"></script>
      <script src="/js/src/ui/grid/manager/HolidayGrid.js"></script>
      <script src="/js/src/ui/tbar/manager/HolidayToolbar.js"></script>
      <script>
        // Invoked when the page is ready.
        Ext.onReady(function() {
            // Create the grid.
            var grid = new ui.grid.manager.HolidayGrid({
                // Specify where the grid will be rendered.
                renderTo: 'holiday-management-grid'
            });
        });
      </script>

    </div>

    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
