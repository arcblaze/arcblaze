
Ext.namespace("ui.grid.manager");

ui.grid.manager.UserGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		var user = new data.model.User();

		var config = Ext.applyIf(c || {}, {
			title:            'User Accounts',
			id:               'ui.grid.manager.usergrid',
			store:            new data.store.manager.UserStore(),
			multiSelect:      true,
			stripeRows:       true,
			autoExpandColumn: 'fullName',
			autoWidth:        true,
			height:           300,
			tbar:             new ui.tbar.manager.UserToolbar(),
			columns:          user.getColumnModel(),
			loadMask:         true
		});

		ui.grid.manager.UserGrid.superclass.constructor.call(this, config);

		this.getSelectionModel().addListener('selectionchange', function(model) {
			// Get the number of selected rows.
			var count = model.selected.items.length;

			// Get the buttons.
			var userDel = Ext.getCmp('action.manager.user.douserdelete');
			var userAct = Ext.getCmp('action.manager.user.douseractivate');
			var userDea = Ext.getCmp('action.manager.user.douserdeactivate');
			var userUpd = Ext.getCmp('action.manager.user.showuserupdate');
			var userCon = Ext.getCmp('action.manager.user.showusertasks');

			var allActive = true;
			for (var s = 0; s < count && allActive; s++)
				allActive = model.selected.items[s].data.active;

			var allInactive = true;
			for (var s = 0; s < count && allInactive; s++)
				allInactive = !model.selected.items[s].data.active;

			// Update the buttons based on the selected rows.
			if (userDel)
				(count > 0) ? userDel.enable() : userDel.disable();
			if (userUpd)
				(count == 1) ? userUpd.enable() : userUpd.disable();
			if (userCon)
				(count == 1) ? userCon.enable() : userCon.disable();
			if (userAct)
				(count > 0 && allInactive) ?
					userAct.enable() : userAct.disable();
			if (userDea)
				(count > 0 && allActive) ?
					userDea.enable() : userDea.disable();
		});
	},

	getSelectedIds: function() {
		// This will hold all the ids.
		var ids = [ ];

		// Get the selected records.
		var records = this.getSelectionModel().selected.items;

		// Iterate over the selected records.
		for (var i = 0; i < records.length; i++)
			// Add the id to the list.
			ids.push(records[i].data.id);

		// Return the ids.
		return ids;
	}
});

