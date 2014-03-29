
Ext.namespace("ui.tbar.admin");

ui.tbar.admin.CompanyToolbar = Ext.extend(Ext.Toolbar, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			items: [
				new action.admin.company.ShowCompanyAdd(),
				new action.admin.company.ShowCompanyUpdate(),
				new action.admin.company.DoCompanyActivate(),
				new action.admin.company.DoCompanyDeactivate(),
				new action.admin.company.DoCompanyDelete(),

				'->',

				new Ext.form.Label({
					text: 'Include inactive companies',
					style: 'padding-right:10px;'
				}),
				new Ext.form.Checkbox({
					id: 'ui.field.company.inactive',
					checked: false,
					listeners: {
						check: function(cb, checked) {
							var grid = Ext.getCmp('ui.grid.admin.companygrid');
							if (grid) {
								var store = grid.getStore();
								checked ? store.clearInactiveFilter() :
										  store.setInactiveFilter();
							}
						}
					}
				}),

				'-',

				new Ext.form.TextField({
					id: 'ui.field.company.search',
					width: 100,
					listeners: {
						specialkey: function(tf, evt) {
							if (evt.ENTER == evt.getKey()) {
								var search = Ext.getCmp('action.admin.company.docompanysearch');
								search.handler();
							}
						}
					}
				}),
				new action.admin.company.DoCompanySearch()
			]
		});

		ui.tbar.admin.CompanyToolbar.superclass.constructor.call(this, config);
	}
});

