
Ext.namespace("action.manager.usertask");

action.manager.usertask.ShowUserTaskGrid = function() {
	return new Ext.Action({
		id:      'action.manager.usertask.showassignmentgrid',
		text:    'Back to Assignments',
		iconCls: 'icon-assignment-go',
		handler: function() {
			// Get the panels.
			var assignmentAddPanel =
				Ext.getCmp('ui.panel.manager.usertask.assignmentaddpanel');
			var assignmentUpdPanel =
				Ext.getCmp('ui.panel.manager.usertask.assignmentupdatepanel');

			// Hide the panels.
			if (assignmentAddPanel) assignmentAddPanel.destroy();
			if (assignmentUpdPanel) assignmentUpdPanel.destroy();

			// Show the grid.
			Ext.getCmp('ui.grid.manager.usertaskgrid').show();
		}
	});
}

