
Ext.namespace("ui.panel.manager.holiday");

ui.panel.manager.holiday.HolidayAddPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		var form = this;
		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.holiday.holidayaddpanel',
			title:      'Add a new Holiday',
			width:      460,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			labelWidth: 120,
			items: [
				{
					xtype:        'combobox',
					fieldLabel:   'Predefined',
					name:         'predefined',
					allowBlank:   true,
					width:        400,
					displayField: 'description',
					typeAhead:    true,
					queryMode:    'local',
					store: new data.store.manager.HolidayStore({
						common: true
					}),
					listeners:  {
						select: function(field, values) {
							if (values && values.length > 0) {
								form.getForm().findField('description').setValue(
									values[0].data.description);
								form.getForm().findField('config').setValue(
									values[0].data.config);
							}
						}
					}
				}, {
					xtype:      'label',
					html:       '<div style="padding:0px 5px 6px 104px;">' +
								'Provide a brief phrase that describes the ' +
								'holiday. For example, "Christmas Day". ' +
								'This value will be displayed on the timesheet ' +
								'in which the holiday falls.' +
								'</div>'
				}, {
					xtype:      'textfield',
					fieldLabel: 'Description',
					name:       'description',
					allowBlank: false,
					width:      400
				}, {
					xtype:      'label',
					html:       '<div style="padding-left:120px;">' +
								'Provide a configuration that defines how the ' +
								'holiday should be calculated. The configuration ' +
								'should follow one of these formats: ' +
								'<li><i>Jan 1st</i> - A specific month and day.</li>' +
								'<li><i>July 4th Observance</i> - The word ' +
								'"observance" means that if the date falls ' +
								'on a weekend, then the holiday will be ' +
								'observed on either Friday or Monday.</li>' +
								'<li><i>1st Monday in September</i> - A specific ' +
								'day of the week within a month.</li>' +
								'<li><i>4 Thu in Nov + 1</i> - A + or - modifier ' +
								'can be added to the end to slide the day ' +
								'forwards or backwards from a reference point ' +
								'(e.g., the Friday after Thanksgiving).</li>' +
								'</div>'
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
		this.getForm().findField('predefined').focus();
	}
});

