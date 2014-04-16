
Ext.namespace("data.store.manager");

data.store.manager.TaskUserStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		// Make sure the correct parameters were specified.
		if (!c || !c.task)
			throw "TaskUserStore requires a task.";

		var config = Ext.applyIf(c || {}, {
			model:    'data.model.TaskUser',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/manager/assignment/task/' + c.task.data.id,
				reader: {
					type: 'json',
					root: 'users'
				}
			},
			params: {
				day: c.day
			}
		});

		data.store.manager.TaskUserStore.superclass.constructor.call(this, config);
	},

	setAsOf: function(newDate) {
		this.reload({
			params: {
				day: newDate.format('Y-m-d')
			}
		});
	}
});

