
Ext.namespace("action.manager.holiday");

action.manager.holiday.ShowHolidayAdd = function() {
	return new Ext.Action({
		id:      'action.manager.holiday.showholidayadd',
		text:    'Add',
		iconCls: 'icon-holiday-add',
		handler: function() {
			var holidayGrid = Ext.getCmp('ui.grid.manager.holidaygrid');

			var holidayAddPanel = new ui.panel.manager.holiday.HolidayAddPanel({
				renderTo: 'holiday-add-panel'
			});

			// Hide the grid and show the panel.
			holidayGrid.hide();
			holidayAddPanel.show();

			// Set the focus.
			holidayAddPanel.setInitialFocus();
		}
	});
}

