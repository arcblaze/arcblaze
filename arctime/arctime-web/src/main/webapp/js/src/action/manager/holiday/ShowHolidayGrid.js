
Ext.namespace("action.manager.holiday");

action.manager.holiday.ShowHolidayGrid = function() {
	return new Ext.Action({
		id:      'action.manager.holiday.showholidaygrid',
		text:    'Back to Holidays',
		iconCls: 'icon-holiday-go',
		handler: function() {
			// Get the panels.
			var holidayAddPanel =
				Ext.getCmp('ui.panel.manager.holiday.holidayaddpanel');
			var holidayUpdPanel =
				Ext.getCmp('ui.panel.manager.holiday.holidayupdatepanel');

			// Hide the panels.
			if (holidayAddPanel) holidayAddPanel.hide();
			if (holidayUpdPanel) holidayUpdPanel.hide();

			// Show the grid.
			Ext.getCmp('ui.grid.manager.holidaygrid').show();
		}
	});
}

