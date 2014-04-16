
Ext.namespace("action.manager.task");

action.manager.task.ShowTaskUsers = function() {
	return new Ext.Action({
		id:       'action.manager.task.showtaskusers',
		text:     'Assigned Users',
		iconCls:  'icon-users',
		disabled: true,
		handler: function() {
			// Get the task grid.
			var taskGrid = Ext.getCmp('ui.grid.manager.taskgrid');

			// Get the selected task.
			var task = taskGrid.selModel.selected.items[0];

			// Create the task user grid.
			var assignmentGrid = new ui.grid.manager.TaskUserGrid({
				task:     task,
				day:      (new Date()).format('Y-m-d'),
				renderTo: 'task-user-grid'
			});

			// Hide the task grid and show the assignment grid.
			taskGrid.hide();
			assignmentGrid.show();
		}
	});
}
