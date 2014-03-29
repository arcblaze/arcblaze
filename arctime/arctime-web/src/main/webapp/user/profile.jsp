<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: Profile</title>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>


    <div id="container">
      <!-- This is where the profile information will be rendered. -->
      <table id="profile-table">
        <tr>
          <td valign="top">
            <div id="profile-update-panel"></div>
          </td>
          <td valign="top" style="padding-left:20px;">
            <div id="supervisor-grid"></div>
          </td>
        </tr>
      </table>

      <!-- Add the profile management scripts. -->
      <script src="/js/src/util/io/ServerIO.js"></script>
      <script src="/js/src/data/model/User.js"></script>
      <script src="/js/src/data/model/Supervisor.js"></script>
      <script src="/js/src/data/store/SupervisorStore.js"></script>
      <script src="/js/src/action/user/DoProfileUpdate.js"></script>
      <script src="/js/src/ui/panel/user/ProfileUpdatePanel.js"></script>
      <script src="/js/src/ui/grid/SupervisorGrid.js"></script>
      <script>
        // Invoked when the page is ready.
        Ext.onReady(function() {

            // Create the panel.
            new ui.panel.user.ProfileUpdatePanel({
                // Specify where the panel will be rendered.
                renderTo: 'profile-update-panel',

                // Provide the user data.
                user: { data: user }
            });

            // Create the supervisor grid.
            new ui.grid.SupervisorGrid({
                // Specify where the grid will be rendered.
                renderTo: 'supervisor-grid',

                // Don't include the supervisor toolbar.
                includeToolbar: false,

                // Set the height.
                height: 140,

                // Provide the user data.
                user: { data: user }
            });
        });
      </script>
    </div>


    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
