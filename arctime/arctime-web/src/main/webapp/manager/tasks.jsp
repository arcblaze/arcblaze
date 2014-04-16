<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: Tasks</title>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>

    <div id="container-maxwidth">
      <%@ include file="/ssi/manager_navigation.jspf" %>

      <!-- This is where the user grid and panels will be rendered. -->
      <div id="task-management-grid"></div>
      <div id="task-add-panel"></div>
      <div id="task-update-panel"></div>
      <div id="task-user-grid"></div>
      <div id="assignment-add-panel"></div>
      <div id="assignment-update-panel"></div>

      <!-- Add the employee management scripts. -->
      <script src="/js/src/util/io/ServerIO.js"></script>
      <script src="/js/src/data/model/Task.js"></script>
      <script src="/js/src/data/model/TaskUser.js"></script>
      <script src="/js/src/action/manager/task/DoTaskActivate.js"></script>
      <script src="/js/src/action/manager/task/DoTaskAdd.js"></script>
      <script src="/js/src/action/manager/task/DoTaskDeactivate.js"></script>
      <script src="/js/src/action/manager/task/DoTaskDelete.js"></script>
      <script src="/js/src/action/manager/task/DoTaskSearch.js"></script>
      <script src="/js/src/action/manager/task/DoTaskUpdate.js"></script>
      <script src="/js/src/action/manager/task/ShowTaskAdd.js"></script>
      <script src="/js/src/action/manager/task/ShowTaskGrid.js"></script>
      <script src="/js/src/action/manager/task/ShowTaskUsers.js"></script>
      <script src="/js/src/action/manager/task/ShowTaskUpdate.js"></script>
      <script src="/js/src/action/manager/taskuser/DoAssignmentAdd.js"></script>
      <script src="/js/src/action/manager/taskuser/DoAssignmentDelete.js"></script>
      <script src="/js/src/action/manager/taskuser/DoAssignmentSearch.js"></script>
      <script src="/js/src/action/manager/taskuser/DoAssignmentUpdate.js"></script>
      <script src="/js/src/action/manager/taskuser/ShowAssignmentAdd.js"></script>
      <script src="/js/src/action/manager/taskuser/ShowAssignmentUpdate.js"></script>
      <script src="/js/src/action/manager/taskuser/ShowTaskUserGrid.js"></script>
      <script src="/js/src/data/store/manager/TaskStore.js"></script>
      <script src="/js/src/data/store/manager/TaskUserStore.js"></script>
      <script src="/js/src/ui/grid/manager/TaskGrid.js"></script>
      <script src="/js/src/ui/grid/manager/TaskUserGrid.js"></script>
      <script src="/js/src/ui/panel/manager/task/TaskAddPanel.js"></script>
      <script src="/js/src/ui/panel/manager/task/TaskUpdatePanel.js"></script>
      <script src="/js/src/ui/panel/manager/taskuser/AssignmentAddPanel.js"></script>
      <script src="/js/src/ui/panel/manager/taskuser/AssignmentUpdatePanel.js"></script>
      <script src="/js/src/ui/tbar/manager/TaskUserToolbar.js"></script>
      <script src="/js/src/ui/tbar/manager/TaskToolbar.js"></script>
      <script>
        // Invoked when the page is ready.
        Ext.onReady(function() {
            // Create the grid.
	        new ui.grid.manager.TaskGrid({
                // Specify where the grid will be rendered.
                renderTo: 'task-management-grid'
            });
        });
      </script>

    </div>

    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
