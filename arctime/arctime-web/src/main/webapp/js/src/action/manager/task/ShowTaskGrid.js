
Ext.namespace("action.manager.task");

action.manager.task.ShowTaskGrid = function() {
	return new Ext.Action({
		id:      'action.manager.task.showtaskgrid',
		text:    'Back to Tasks',
		iconCls: 'icon-task-go',
		handler: function() {
			// Get the panels.
			var taskAddPanel =
				Ext.getCmp('ui.panel.manager.task.taskaddpanel');
			var taskUpdPanel =
				Ext.getCmp('ui.panel.manager.task.taskupdatepanel');
			var taskUsrPanel =
				Ext.getCmp('ui.panel.manager.task.taskuserpanel');
			var assignmentGrid =
				Ext.getCmp('ui.grid.manager.taskusergrid');

			// Hide the panels.
			if (taskAddPanel) taskAddPanel.destroy();
			if (taskUpdPanel) taskUpdPanel.destroy();
			if (taskUsrPanel) taskUsrPanel.destroy();
			if (assignmentGrid) assignmentGrid.destroy();

			// Show the grid.
			Ext.getCmp('ui.grid.manager.taskgrid').show();
		}
	});
}
