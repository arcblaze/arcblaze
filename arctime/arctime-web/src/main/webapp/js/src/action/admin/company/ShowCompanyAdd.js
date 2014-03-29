
Ext.namespace("action.admin.company");

action.admin.company.ShowCompanyAdd = function() {
	return new Ext.Action({
		id:      'action.admin.company.showcompanyadd',
		text:    'Add',
		iconCls: 'icon-company-add',
		handler: function() {
			var companyAddPanel =
				Ext.getCmp('ui.panel.admin.company.companyaddpanel');
			var companyGrid = Ext.getCmp('ui.grid.admin.companygrid');

			if (!companyAddPanel)
				companyAddPanel = new ui.panel.admin.company.CompanyAddPanel({
					renderTo: 'company-add-panel'
				});

			companyGrid.hide();
			companyAddPanel.show();

			companyAddPanel.setInitialFocus();
		}
	});
}

