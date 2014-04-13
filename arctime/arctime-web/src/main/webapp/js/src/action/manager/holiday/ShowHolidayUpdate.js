
Ext.namespace("action.manager.holiday");

action.manager.holiday.ShowHolidayUpdate = function() {
	return new Ext.Action({
		id:       'action.manager.holiday.showholidayupdate',
		text:     'Update',
		iconCls:  'icon-holiday-edit',
		disabled: true,
		handler: function() {
			var holidayGrid = Ext.getCmp('ui.grid.manager.holidaygrid');

			var holidayUpdPanel = new ui.panel.manager.holiday.HolidayUpdatePanel({
				renderTo: 'holiday-update-panel'
			});

			// Hide the grid and show the panel.
			holidayGrid.hide();
			holidayUpdPanel.show();

			// Get the selected holiday.
			var holiday = holidayGrid.getSelectionModel().selected.items[0];

			// Set the focus.
			holidayUpdPanel.setValues(holiday);
		}
	});
}

