
Ext.namespace("ui.panel.admin.company");

ui.panel.admin.company.CompanyAddPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.admin.company.companyaddpanel',
			title:      'Add a new Company',
			width:      400,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			labelWidth: 110,
			items: [
				{
					xtype:      'textfield',
					fieldLabel: 'Name',
					name:       'name',
					allowBlank: false,
					width:      220
				}, {
					xtype:      'radiogroup',
					fieldLabel: 'Active',
					name:       'active',
					items: [
						{
							boxLabel:   'Yes',
							name:       'active',
							id:         'company-active-yes',
							inputValue: 1,
							checked:    true,
							style:      'border: 0px;'
						}, {
							boxLabel:   'No',
							name:       'active',
							id:         'company-active-no',
							inputValue: 0,
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

