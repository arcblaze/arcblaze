
Ext.namespace("action.admin.company");

action.admin.company.DoCompanyDelete = function() {
	return new Ext.Action({
		id:       'action.admin.company.docompanydelete',
		text:     'Delete',
		iconCls:  'icon-company-delete',
		disabled: true,
		handler: function() {
			var grid = Ext.getCmp('ui.grid.admin.companygrid');
			var ids = grid.getSelectedIds();

			var c = ids.length > 1 ? 'companies' : 'company';
			var C = ids.length > 1 ? 'Companies' : 'Company';

			Ext.Msg.confirm('Are you sure?',
				'Are you sure you want to delete the specified ' + c + '?',

				function(btn) {
					if (btn != 'yes')
						return;

					Ext.Msg.progress('Deleting ' + C,
						'Please wait while removing the ' + c + '...');

					var io = new util.io.ServerIO();
					io.doAjaxRequest({
						url: '/admin/company/delete',
						params: {
							ids: ids.join(',')
						},
						mysuccess: function(data) {
							var grid = Ext.getCmp('ui.grid.admin.companygrid');
							grid.getStore().reload();
						}
					});
				}
			);
		}
	});
}

