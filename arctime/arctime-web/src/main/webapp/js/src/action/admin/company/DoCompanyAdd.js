
Ext.namespace("action.admin.company");

action.admin.company.DoCompanyAdd = function() {
	return new Ext.Action({
		id:      'action.admin.company.docompanyadd',
		text:    'Add',
		iconCls: 'icon-company-add',
		handler: function() {
			var formPanel = Ext.getCmp('ui.panel.admin.company.companyaddpanel');

			if (!formPanel.getForm().isValid()) {
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			Ext.Msg.progress('Adding Company',
				'Please wait while the company is added...');

			var io = new util.io.ServerIO();
			io.doFormRequest(formPanel, {
				url: '/admin/company/add',
				mysuccess: function(data) {
					var grid = Ext.getCmp('ui.grid.admin.companygrid');
					grid.getStore().reload();
				}
			});
		}
	});
}

