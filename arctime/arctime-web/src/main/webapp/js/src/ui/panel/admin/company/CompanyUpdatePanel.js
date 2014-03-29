
Ext.namespace("ui.panel.admin.company");

ui.panel.admin.company.CompanyUpdatePanel = Ext.extend(Ext.Panel, {
	constructor: function(c) {
		if (!c || !c.company)
			throw "CompanyUpdatePanel requires a company.";

		var panel = this;

		this.form = new Ext.form.FormPanel({
			title:       'Update Company',
			width:       400,
			autoHeight:  true,
			bodyStyle:   'padding: 10px;',
			labelWidth:  110,
			items: [
				{
					xtype: 'hidden',
					name:  'id'
				}, {
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
							id:         'company-active-modify-yes',
							inputValue: 1,
							style:      'border: 0px;'
						}, {
							boxLabel:   'No',
							name:       'active',
							id:         'company-active-modify-no',
							inputValue: 0,
							style:      'border: 0px;'
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
		this.form.getForm().findField('active').
			setValue('company-active-modify-yes', company.data.active == "1");
		this.form.getForm().findField('active').
			setValue('company-active-modify-no', company.data.active == "0");

		this.setInitialFocus();
	}
});

