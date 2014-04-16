
Ext.namespace("action.manager.usertask");

action.manager.usertask.ShowAssignmentUpdate = function(user) {
	return new Ext.Action({
		id:       'action.manager.usertask.showassignmentupdate',
		text:     'Update',
		iconCls:  'icon-assignment-edit',
		disabled: true,
		handler: function() {
			// Get the grid.
			var userTaskGrid = Ext.getCmp('ui.grid.manager.usertaskgrid');

			// Make sure the panel exists.
			var assignmentUpdPanel = new ui.panel.manager.usertask.AssignmentUpdatePanel({
				user: user,
				renderTo: 'assignment-update-panel'
			});

			// Hide the grid and show the panel.
			userTaskGrid.hide();
			assignmentUpdPanel.show();

			// Get the selected user assignment.
			var assignment = userTaskGrid.selModel.selected.items[0];

			// Set the focus.
			assignmentUpdPanel.setValues(assignment);
		}
	});
}

