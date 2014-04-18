
Ext.namespace("data.model");

Ext.define('data.model.Holiday', {
	extend: 'Ext.data.Model',
	fields: [
		{
			name:      'id',
			dataIndex: 'id',
			header:    'ID',
			width:     40,
			sortable:  true,
			visible:   false
		}, {
			name:      'description',
			dataIndex: 'description',
			header:    'Description',
			width:     240,
			sortable:  true
		}, {
			name:      'config',
			dataIndex: 'config',
			header:    'Configuration',
			width:     260,
			sortable:  true
		}, {
			name:      'day',
			dataIndex: 'day',
			header:    'Day',
			width:     140,
			sortable:  true,
			renderer:  function(value) {
				return Ext.Date.format(new Date(value), 'D, M j, Y');
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

