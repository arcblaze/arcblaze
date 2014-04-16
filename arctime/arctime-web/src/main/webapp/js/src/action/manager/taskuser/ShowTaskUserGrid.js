
Ext.namespace("action.manager.taskuser");

action.manager.taskuser.ShowTaskUserGrid = function() {
	return new Ext.Action({
		id:      'action.manager.taskuser.showassignmentgrid',
		text:    'Back to Assignments',
		iconCls: 'icon-assignment-go',
		handler: function() {
			// Get the panels.
			var assignmentAddPanel =
				Ext.getCmp('ui.panel.manager.taskuser.assignmentaddpanel');
			var assignmentUpdPanel =
				Ext.getCmp('ui.panel.manager.taskuser.assignmentupdatepanel');

			// Hide the panels.
			if (assignmentAddPanel) assignmentAddPanel.hide();
			if (assignmentUpdPanel) assignmentUpdPanel.hide();

			// Show the grid.
			Ext.getCmp('ui.grid.manager.taskusergrid').show();
		}
	});
}

