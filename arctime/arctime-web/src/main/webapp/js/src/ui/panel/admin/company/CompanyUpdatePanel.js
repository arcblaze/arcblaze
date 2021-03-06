
Ext.namespace("ui.panel.admin.company");

ui.panel.admin.company.CompanyUpdatePanel = Ext.extend(Ext.Panel, {
	constructor: function(c) {
		if (!c || !c.company)
			throw "CompanyUpdatePanel requires a company.";

		var panel = this;

		this.form = new Ext.form.FormPanel({
			title:       'Update Company',
			width:       450,
			autoHeight:  true,
			bodyStyle:   'padding: 10px;',
			items: [
				{
					xtype: 'hidden',
					name:  'id'
				}, {
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
					width:      240,
					items: [
						{
							boxLabel:   'Yes',
							name:       'active',
							id:         'company-active-modify-yes',
							inputValue: "true"
						}, {
							boxLabel:   'No',
							name:       'active',
							id:         'company-active-modify-no',
							inputValue: "false"
						}
					]
				}
			],
			buttons: [
				new Ext.Button(new action.admin.company.DoCompanyUpdate()),
				new Ext.Button(new action.admin.company.ShowCompanyGrid())
			]
		});

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.admin.company.companyupdatepanel',
			border:     false,
			frame:      false,
			autoHeight: true,
			width:      780,
			layout:     'column',
			items:      panel.form
		});

		ui.panel.admin.company.CompanyUpdatePanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.form.getForm().findField('name').focus();
	},

	setValues: function(company) {
		this.form.getForm().findField('id').setValue(company.data.id);
		this.form.getForm().findField('name').setValue(company.data.name);
		Ext.getCmp('company-active-modify-yes').setValue(company.data.active);
		Ext.getCmp('company-active-modify-no').setValue(!company.data.active);

		this.setInitialFocus();
	}
});

