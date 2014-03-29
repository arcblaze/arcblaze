
Ext.namespace("action.admin.company");

action.admin.company.ShowCompanyGrid = function() {
	return new Ext.Action({
		id:      'action.admin.company.showcompanygrid',
		text:    'Back to Companies',
		iconCls: 'icon-company-go',
		handler: function() {
			var companyGrid = Ext.getCmp('ui.grid.admin.companygrid');
			var companyAddPanel = Ext.getCmp('ui.panel.admin.company.companyaddpanel');
			var companyUpdPanel = Ext.getCmp('ui.panel.admin.company.companyupdatepanel');

			if (companyAddPanel) companyAddPanel.destroy();
			if (companyUpdPanel) companyUpdPanel.destroy();

			if (companyGrid) companyGrid.show();
		}
	});
}

