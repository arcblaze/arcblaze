
Ext.namespace("data.model");

Ext.define('data.model.TaskUser', {
	extend: 'Ext.data.Model',
	fields: [
		{
			name:      'id',
			dataIndex: 'id',
			header:    'ID',
			width:     40,
			hidden:    true,
			sortable:  true
		}, {
			name:      'userId',
			dataIndex: 'userId',
			header:    'User ID',
			width:     40,
			hidden:    true,
			sortable:  true
		}, {
			name:      'taskId',
			dataIndex: 'taskId',
			header:    'Task ID',
			width:     40,
			hidden:    true,
			sortable:  true
		}, {
			name:      'login',
			dataIndex: 'login',
			header:    'Login',
			width:     80,
			sortable:  true
		}, {
			name:      'firstName',
			dataIndex: 'firstName',
			header:    'First Name',
			width:     70,
			sortable:  true,
			hidden:    true
		}, {
			name:      'lastName',
			dataIndex: 'lastName',
			header:    'Last Name',
			width:     90,
			sortable:  true,
			hidden:    true
		}, {
			name:      'fullName',
			dataIndex: 'fullName',
			header:    'Full Name',
			width:     130,
			sortable:  true
		}, {
			name:      'laborCat',
			dataIndex: 'laborCat',
			header:    'Labor Category',
			width:     130,
			sortable:  true
		}, {
			name:      'itemName',
			dataIndex: 'itemName',
			header:    'Item Name',
			width:     220,
			sortable:  true
		}, {
			name:      'begin',
			dataIndex: 'begin',
			header:    'Begin',
			width:     80,
			sortable:  true,
			renderer:  function(value) {
				return Ext.Date.format(new Date(value), 'D, M j, Y');
			}
		}, {
			name:      'end',
			dataIndex: 'end',
			header:    'End',
			width:     80,
			sortable:  true,
			renderer:  function(value) {
				return Ext.Date.format(new Date(value), 'D, M j, Y');
			}
		}, {
			name:      'email',
			dataIndex: 'email',
			header:    'Email',
			width:     160,
			sortable:  true,
			hidden:    true
		}, {
			name:      'privileges',
			dataIndex: 'privileges',
			header:    'Privileges',
			width:     60,
			sortable:  true,
			hidden:    true
		}, {
			name:      'active',
			dataIndex: 'active',
			header:    'Active',
			width:     60,
			hidden:    true,
			sortable:  true,
			renderer:  function(val) {
				return val ? "Yes" : "No";
			}
		}
	],

	getColumnModel: function() {
		var flds = [ ];
		this.fields.each(function(field) {
			flds.push({
				dataIndex: field.dataIndex,
				hidden:    field.hidden,
				renderer:  field.renderer,
				sortable:  field.sortable,
				text:      field.header,
				type:      field.type,
				width:     field.width
			});
		});
		return flds;
	}
});
