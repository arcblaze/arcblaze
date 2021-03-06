
Ext.namespace("action.admin.company");

action.admin.company.DoCompanyDeactivate = function() {
	return new Ext.Action({
		id:       'action.admin.company.docompanydeactivate',
		text:     'Deactivate',
		iconCls:  'icon-company-edit',
		disabled: true,
		handler: function() {
			var grid = Ext.getCmp('ui.grid.admin.companygrid');
			var ids = grid.getSelectedIds();

			var c = ids.length > 1 ? 'companies' : 'company';
			var C = ids.length > 1 ? 'Companies' : 'Company';

			Ext.Msg.progress('Deactivating ' + C,
				'Please wait while deactivating the ' + c + '...');

			var io = new util.io.ServerIO();
			io.doAjaxRequest({
				url: '/rest/admin/company/deactivate',
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

