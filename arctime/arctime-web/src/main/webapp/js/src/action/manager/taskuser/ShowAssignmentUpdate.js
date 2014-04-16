
Ext.namespace("action.manager.taskuser");

action.manager.taskuser.ShowAssignmentUpdate = function(task) {
	return new Ext.Action({
		id:       'action.manager.taskuser.showassignmentupdate',
		text:     'Update',
		iconCls:  'icon-assignment-edit',
		disabled: true,
		handler: function() {
			// Get the grid.
			var taskUserGrid = Ext.getCmp('ui.grid.manager.taskusergrid');

			// Make sure the panel exists.
			var assignmentUpdPanel = new ui.panel.manager.taskuser.AssignmentUpdatePanel({
				task: task,
				renderTo: 'assignment-update-panel'
			});

			// Hide the grid and show the panel.
			taskUserGrid.hide();
			assignmentUpdPanel.show();

			// Get the selected task assignment.
			var assignment = taskUserGrid.selModel.selected.items[0];

			// Set the focus.
			assignmentUpdPanel.setValues(assignment);
		}
	});
}

