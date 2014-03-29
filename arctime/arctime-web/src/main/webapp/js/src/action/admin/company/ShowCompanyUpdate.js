
Ext.namespace("action.admin.company");

action.admin.company.ShowCompanyUpdate = function() {
	return new Ext.Action({
		id:       'action.admin.company.showcompanyupdate',
		text:     'Update',
		iconCls:  'icon-company-edit',
		disabled: true,
		handler: function() {
			var companyGrid = Ext.getCmp('ui.grid.admin.companygrid');
			var company = companyGrid.getSelectionModel().getSelections()[0];

			companyGrid.hide();

			var existing = Ext.getCmp('ui.panel.admin.company.companyupdatepanel');
			if (existing)
				existing.destroy();

			var companyUpdPanel = new ui.panel.admin.company.CompanyUpdatePanel({
				renderTo: 'company-update-panel',
				company: company
			});

			companyUpdPanel.setValues(company);
		}
	});
}

