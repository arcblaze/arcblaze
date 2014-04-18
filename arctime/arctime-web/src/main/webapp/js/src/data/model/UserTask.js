
Ext.namespace("data.model");

Ext.define('data.model.UserTask', {
	extend: 'Ext.data.Model',
	fields: [
		{
			name:      'id',
			dataIndex: 'id',
			header:    'ID',
			width:     40,
			hidden:    true,
			sortable:  true,
			visible:   false
		}, {
			name:      'userId',
			dataIndex: 'userId',
			header:    'User ID',
			width:     40,
			hidden:    true,
			sortable:  true,
			visible:   false
		}, {
			name:      'taskId',
			dataIndex: 'taskId',
			header:    'Task ID',
			width:     40,
			hidden:    true,
			sortable:  true,
			visible:   false
		}, {
			name:      'description',
			dataIndex: 'description',
			header:    'Description',
			width:     200,
			sortable:  true
		}, {
			name:      'jobCode',
			dataIndex: 'jobCode',
			header:    'Job Code',
			width:     180,
			sortable:  true
		}, {
			name:      'laborCat',
			dataIndex: 'laborCat',
			header:    'Labor Category',
			width:     180,
			sortable:  true
		}, {
			name:      'itemName',
			dataIndex: 'itemName',
			header:    'Item Name',
			width:     215,
			sortable:  true
		}, {
			name:      'begin',
			dataIndex: 'begin',
			header:    'Begin',
			width:     80,
			sortable:  true,
			renderer:  function(value) {
				return Ext.Date.format(new Date(value), 'm/d/Y');
			}
		}, {
			name:      'end',
			dataIndex: 'end',
			header:    'End',
			width:     80,
			sortable:  true,
			renderer:  function(value) {
				return Ext.Date.format(new Date(value), 'm/d/Y');
			}
		}
	],

	getColumnModel: function() {
		var flds = [ ];
		this.fields.each(function(field) {
			if (typeof(field.visible) == "undefined" || field.visible) {
				flds.push({
					dataIndex: field.dataIndex,
					hidden:    field.hidden,
					renderer:  field.renderer,
					sortable:  field.sortable,
					text:      field.header,
					type:      field.type,
					width:     field.width
				});
			}
		});
		return flds;
	}
});

