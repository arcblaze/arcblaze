
Ext.namespace("action.manager.user");

action.manager.user.ShowUserTasks = function() {
	return new Ext.Action({
		id:       'action.manager.user.showusertasks',
		text:     'Assigned Tasks',
		iconCls:  'icon-tasks',
		disabled: true,
		handler: function() {
			// Get the user grid.
			var userGrid = Ext.getCmp('ui.grid.manager.usergrid');

			// Get the selected user.
			var user = userGrid.selModel.selected.items[0];

			// Create the user task grid.
			var assignmentGrid = new ui.grid.manager.UserTaskGrid({
				user:     user,
				day:      (new Date()).format('Y-m-d'),
				renderTo: 'user-task-grid'
			});

			// Hide the user grid and show the assignment grid.
			userGrid.hide();
			assignmentGrid.show();
		}
	});
}

