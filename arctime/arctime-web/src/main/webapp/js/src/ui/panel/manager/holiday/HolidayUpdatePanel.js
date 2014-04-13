
Ext.namespace("ui.panel.manager.holiday");

ui.panel.manager.holiday.HolidayUpdatePanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		var form = this;
		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.holiday.holidayupdatepanel',
			title:      'Update Holiday',
			width:      460,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			labelWidth: 120,
			items: [
				{
					xtype: 'hidden',
					name:  'id'
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
				new Ext.Button(new action.manager.holiday.DoHolidayUpdate()),
				new Ext.Button(new action.manager.holiday.ShowHolidayGrid())
			]
		});

		ui.panel.manager.holiday.HolidayUpdatePanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.getForm().findField('description').focus();
	},

	setValues: function(holiday) {
		// Set the form values.
		this.getForm().findField('id').setValue(holiday.data.id);
		this.getForm().findField('description').setValue(holiday.data.description);
		this.getForm().findField('config').setValue(holiday.data.config);

		// Set the form focus.
		this.setInitialFocus();
	}
});

