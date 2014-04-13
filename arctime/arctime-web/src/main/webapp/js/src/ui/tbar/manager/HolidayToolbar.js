
Ext.namespace("ui.tbar.manager");

ui.tbar.manager.HolidayToolbar = Ext.extend(Ext.Toolbar, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			items: [
				new action.manager.holiday.ShowHolidayAdd(),
				new action.manager.holiday.ShowHolidayUpdate(),
				new action.manager.holiday.DoHolidayDelete(),

				'->',

				new Ext.form.TextField({
					id: 'ui.field.manager.holiday.search',
					width: 100,
					listeners: {
						specialkey: function(tf, evt) {
							// Listen for the Enter key.
							if (evt.ENTER == evt.getKey()) {
								// Get the search action.
								var search = Ext.getCmp(
									'action.manager.holiday.doholidaysearch');

								// Invoke the handler.
								search.handler();
							}
						}
					}
				}),
				new action.manager.holiday.DoHolidaySearch()
			]
		});

		ui.tbar.manager.HolidayToolbar.superclass.constructor.call(this, config);
	}
});

