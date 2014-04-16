
Ext.namespace("data.store.manager");

data.store.manager.UserTaskStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		// Make sure the correct parameters were specified.
		if (!c || !c.user)
			throw "UserTaskStore requires an user.";

		var config = Ext.applyIf(c || {}, {
			model:    'data.model.UserTask',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/manager/assignment/user/' + c.user.data.id,
				reader: {
					type: 'json',
					root: 'tasks'
				}
			},
			params: {
				day: c.day
			}
		});

		data.store.manager.UserTaskStore.superclass.constructor.call(this, config);
	},

	setAsOf: function(newDate) {
		this.reload({
			params: {
				day: newDate.format('Y-m-d')
			}
		});
	}
});

