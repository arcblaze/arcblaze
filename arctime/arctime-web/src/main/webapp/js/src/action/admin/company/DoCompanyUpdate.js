
Ext.namespace("action.admin.company");

action.admin.company.DoCompanyUpdate = function() {
	return new Ext.Action({
		id:      'action.admin.company.docompanyupdate',
		text:    'Update',
		iconCls: 'icon-company-edit',
		handler: function() {
			var form = Ext.getCmp('ui.panel.admin.company.companyupdatepanel').form;

			if (!form.getForm().isValid()) {
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			Ext.Msg.progress('Updating Company',
				'Please wait while the company is saved...');

			var io = new util.io.ServerIO();
			io.doFormRequest(form, {
				url: '/admin/company/update',
				mysuccess: function(data) {
					var grid = Ext.getCmp('ui.grid.admin.companygrid');
					grid.getStore().reload();
				}
			});
		}
	});
}

