
Ext.namespace("ui.panel.manager.holiday");

ui.panel.manager.holiday.HolidayAddPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.holiday.holidayaddpanel',
			title:      'Add a new Holiday',
			width:      460,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			labelWidth: 120,
			items: [
				{
					xtype:      'textfield',
					fieldLabel: 'Description',
					name:       'description',
					allowBlank: false,
					width:      400
				}, {
					xtype:      'textfield',
					fieldLabel: 'Configuration',
					name:       'config',
					allowBlank: false,
					width:      400,
					listeners:  {
						change: function(field, value) {
							var io = new util.io.ServerIO();
							io.doAjaxRequest({
								url: '/rest/manager/holiday/validate',
								method: 'GET',
								message: false,
								params: {
									config: value
								},
								mysuccess: function(data) {
									if (data.valid) {
										field.clearInvalid();
									} else {
										field.markInvalid(data.msg);
									}
								}
							});
						}
					}
				}
			],
			buttons: [
				new Ext.Button(new action.manager.holiday.DoHolidayAdd()),
				new Ext.Button(new action.manager.holiday.ShowHolidayGrid())
			]
		});

		ui.panel.manager.holiday.HolidayAddPanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.getForm().findField('description').focus();
	}
});

