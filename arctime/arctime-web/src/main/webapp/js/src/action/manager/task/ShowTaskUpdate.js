
Ext.namespace("action.manager.task");

action.manager.task.ShowTaskUpdate = function() {
	return new Ext.Action({
		id:       'action.manager.task.showtaskupdate',
		text:     'Update',
		iconCls:  'icon-task-edit',
		disabled: true,
		handler: function() {
			var taskGrid = Ext.getCmp('ui.grid.manager.taskgrid');

			var taskUpdPanel = new ui.panel.manager.task.TaskUpdatePanel({
				renderTo: 'task-update-panel'
			});

			// Hide the grid and show the panel.
			taskGrid.hide();
			taskUpdPanel.show();

			// Get the selected task.
			var task = taskGrid.getSelectionModel().selected.items[0];

			// Set the focus.
			taskUpdPanel.setValues(task);
		}
	});
}

