
Ext.namespace("ui.grid.admin");

ui.grid.admin.CompanyGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		var company = new data.model.Company();

		var grid = this;

		this.store = new data.store.admin.CompanyStore();
		this.toolbar = new ui.tbar.admin.CompanyToolbar({
			store: grid.store
		});

		var config = Ext.applyIf(c || {}, {
			title:       'Companies',
			id:          'ui.grid.admin.companygrid',
			store:       grid.store,
			multiSelect: true,
			stripeRows:  true,
			autoWidth:   true,
			autoHeight:  true,
			maxHeight:   ((document.height !== undefined) ?
					document.height : document.body.offsetHeight) - 145,
			tbar:        grid.toolbar,
			columns:     company.getColumnModel(),
			loadMask:    true,
	        bbar: Ext.create('Ext.PagingToolbar', {
	            store:       grid.store,
	            displayInfo: true,
	            displayMsg:  'Displaying companies {0} - {1} of {2}',
	            emptyMsg:    'No companies to display'
	        })
		});

		ui.grid.admin.CompanyGrid.superclass.constructor.call(this, config);

		this.getSelectionModel().addListener('selectionchange', function(model) {
			var count = model.selected.items.length;

			var companyDel = Ext.getCmp('action.admin.company.docompanydelete');
			var companyAct = Ext.getCmp('action.admin.company.docompanyactivate');
			var companyDea = Ext.getCmp('action.admin.company.docompanydeactivate');
			var companyUpd = Ext.getCmp('action.admin.company.showcompanyupdate');

			var allActive = true;
			for (var s = 0; s < count && allActive; s++)
				allActive = model.selected.items[s].data.active;

			var allInactive = true;
			for (var s = 0; s < count && allInactive; s++)
				allInactive = !model.selected.items[s].data.active;

			if (companyDel)
				(count > 0) ? companyDel.enable() : companyDel.disable();
			if (companyUpd)
				(count == 1) ? companyUpd.enable() : companyUpd.disable();
			if (companyAct)
				(count > 0 && allInactive) ?
					companyAct.enable() : companyAct.disable();
			if (companyDea)
				(count > 0 && allActive) ?
					companyDea.enable() : companyDea.disable();
		});
	},

	getSelectedIds: function() {
		var ids = [ ];
		var records = this.selModel.selected.items;
		for (var i = 0; i < records.length; i++)
			ids.push(records[i].data.id);
		return ids;
	}
});

