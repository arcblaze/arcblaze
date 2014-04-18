
Ext.namespace("data.model");

Ext.define('data.model.Task', {
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
			name:      'description',
			dataIndex: 'description',
			header:    'Description',
			width:     320,
			sortable:  true
		}, {
			name:      'jobCode',
			dataIndex: 'jobCode',
			header:    'Job Code',
			width:     220,
			sortable:  true
		}, {
			name:      'administrative',
			dataIndex: 'administrative',
			header:    'Administrative',
			width:     100,
			sortable:  true,
			renderer:  function(val) {
				return val ? "Yes" : "No";
			}
		}, {
			name:      'active',
			dataIndex: 'active',
			header:    'Active',
			width:     60,
			sortable:  true,
			renderer:  function(val) {
				return val ? "Yes" : "No";
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

