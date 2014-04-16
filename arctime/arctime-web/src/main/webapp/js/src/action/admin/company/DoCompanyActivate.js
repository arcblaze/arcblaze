
Ext.namespace("action.admin.company");

action.admin.company.DoCompanyActivate = function() {
	return new Ext.Action({
		id:       'action.admin.company.docompanyactivate',
		text:     'Activate',
		iconCls:  'icon-company-edit',
		disabled: true,
		handler: function() {
			var grid = Ext.getCmp('ui.grid.admin.companygrid');
			var ids = grid.getSelectedIds();

			var c = ids.length > 1 ? 'companies' : 'company';
			var C = ids.length > 1 ? 'Companies' : 'Company';

			Ext.Msg.progress('Activating ' + C,
				'Please wait while activating the ' + c + '...');

			var io = new util.io.ServerIO();
			io.doAjaxRequest({
				url: '/rest/admin/company/activate',
				method: 'PUT',
				headers: {
					ids: ids
				},
				mysuccess: function(data) {
					var grid = Ext.getCmp('ui.grid.admin.companygrid');
					grid.selModel.clearSelections();
					grid.selModel.fireEvent('selectionchange', grid.selModel);
					grid.getStore().reload();
				}
			});
		}
	});
}

