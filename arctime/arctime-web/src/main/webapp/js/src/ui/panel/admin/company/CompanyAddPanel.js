
Ext.namespace("ui.panel.admin.company");

ui.panel.admin.company.CompanyAddPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.admin.company.companyaddpanel',
			title:      'Add a new Company',
			width:      450,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			items: [
				{
					xtype:      'textfield',
					labelWidth: 60,
					fieldLabel: 'Name',
					name:       'name',
					allowBlank: false,
					width:      400
				}, {
					xtype:      'radiogroup',
					labelWidth: 60,
					fieldLabel: 'Active',
					name:       'active',
					items: [
						{
							boxLabel:   'Yes',
							name:       'active',
							id:         'company-active-yes',
							inputValue: "true",
							checked:    true,
							style:      'border: 0px;'
						}, {
							boxLabel:   'No',
							name:       'active',
							id:         'company-active-no',
							inputValue: "false",
							checked:    false,
							style:      'border: 0px;'
						}
					]
				}
			],
			buttons: [
				new Ext.Button(new action.admin.company.DoCompanyAdd()),
				new Ext.Button(new action.admin.company.ShowCompanyGrid())
			]
		});

		ui.panel.admin.company.CompanyAddPanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.getForm().findField('name').focus();
	}
});

