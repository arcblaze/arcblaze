
Ext.namespace("action.manager.task");

action.manager.task.ShowTaskAdd = function() {
	return new Ext.Action({
		id:      'action.manager.task.showtaskadd',
		text:    'Add',
		iconCls: 'icon-task-add',
		handler: function() {
			var taskGrid = Ext.getCmp('ui.grid.manager.taskgrid');

			var taskAddPanel = new ui.panel.manager.task.TaskAddPanel({
				renderTo: 'task-add-panel'
			});

			// Hide the grid and show the panel.
			taskGrid.hide();
			taskAddPanel.show();

			// Set the focus.
			taskAddPanel.setInitialFocus();
		}
	});
}

