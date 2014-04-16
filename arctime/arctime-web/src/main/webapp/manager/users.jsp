<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: User Accounts</title>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>

    <div id="container-maxwidth">
      <%@ include file="/ssi/manager_navigation.jspf" %>

      <!-- This is where the user grid and panels will be rendered. -->
      <div id="user-management-grid"></div>
      <div id="user-add-panel"></div>
      <div id="user-update-panel"></div>
      <div id="user-task-grid"></div>
      <div id="assignment-add-panel"></div>
      <div id="assignment-update-panel"></div>

      <!-- Add the employee management scripts. -->
      <script src="/js/src/util/io/ServerIO.js"></script>
      <script src="/js/src/data/model/Supervisor.js"></script>
      <script src="/js/src/data/model/User.js"></script>
      <script src="/js/src/data/model/UserTask.js"></script>
      <script src="/js/src/action/manager/user/DoUserActivate.js"></script>
      <script src="/js/src/action/manager/user/DoUserAdd.js"></script>
      <script src="/js/src/action/manager/user/DoUserDeactivate.js"></script>
      <script src="/js/src/action/manager/user/DoUserDelete.js"></script>
      <script src="/js/src/action/manager/user/DoUserSearch.js"></script>
      <script src="/js/src/action/manager/user/DoUserUpdate.js"></script>
      <script src="/js/src/action/manager/user/ShowUserAdd.js"></script>
      <script src="/js/src/action/manager/user/ShowUserGrid.js"></script>
      <script src="/js/src/action/manager/user/ShowUserTasks.js"></script>
      <script src="/js/src/action/manager/user/ShowUserUpdate.js"></script>
      <script src="/js/src/action/manager/usertask/DoAssignmentAdd.js"></script>
      <script src="/js/src/action/manager/usertask/DoAssignmentDelete.js"></script>
      <script src="/js/src/action/manager/usertask/DoAssignmentSearch.js"></script>
      <script src="/js/src/action/manager/usertask/DoAssignmentUpdate.js"></script>
      <script src="/js/src/action/manager/usertask/ShowAssignmentAdd.js"></script>
      <script src="/js/src/action/manager/usertask/ShowAssignmentUpdate.js"></script>
      <script src="/js/src/action/manager/usertask/ShowUserTaskGrid.js"></script>
      <script src="/js/src/action/manager/supervisor/DoSupervisorAdd.js"></script>
      <script src="/js/src/action/manager/supervisor/DoSupervisorDelete.js"></script>
      <script src="/js/src/action/manager/supervisor/ShowSupervisorAdd.js"></script>
      <script src="/js/src/data/store/manager/UserStore.js"></script>
      <script src="/js/src/data/store/manager/UserTaskStore.js"></script>
      <script src="/js/src/data/store/manager/SupervisorStore.js"></script>
      <script src="/js/src/ui/grid/manager/UserGrid.js"></script>
      <script src="/js/src/ui/grid/manager/UserTaskGrid.js"></script>
      <script src="/js/src/ui/grid/manager/SupervisorGrid.js"></script>
      <script src="/js/src/ui/panel/manager/user/UserAddPanel.js"></script>
      <script src="/js/src/ui/panel/manager/user/UserUpdatePanel.js"></script>
      <script src="/js/src/ui/panel/manager/usertask/AssignmentAddPanel.js"></script>
      <script src="/js/src/ui/panel/manager/usertask/AssignmentUpdatePanel.js"></script>
      <script src="/js/src/ui/tbar/manager/UserTaskToolbar.js"></script>
      <script src="/js/src/ui/tbar/manager/UserToolbar.js"></script>
      <script src="/js/src/ui/tbar/manager/SupervisorToolbar.js"></script>
      <script>
        // Invoked when the page is ready.
        Ext.onReady(function() {
            // Create the grid.
	        new ui.grid.manager.UserGrid({
                // Specify where the grid will be rendered.
                renderTo: 'user-management-grid'
            });
        });
      </script>

    </div>

    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
