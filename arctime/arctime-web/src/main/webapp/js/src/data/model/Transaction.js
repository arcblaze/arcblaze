
Ext.namespace("data.model");

Ext.define('data.model.Transaction', {
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
			name:      'companyId',
			dataIndex: 'companyId',
			header:    'Company ID',
			width:     120,
			sortable:  true,
			visible:   false
		}, {
			name:      'userId',
			dataIndex: 'userId',
			header:    'User ID',
			width:     120,
			sortable:  true,
			visible:   false
		}, {
			name:      'timestamp',
			dataIndex: 'timestamp',
			header:    'Timestamp',
			width:     140,
			sortable:  true,
			renderer:  function(val) {
				return val ? Ext.Date.format(new Date(val), 'm/d/Y h:i:s') : "";
			}
		}, {
			name:      'transactionType',
			dataIndex: 'transactionType',
			header:    'Type',
			width:     120,
			sortable:  true
		}, {
			name:      'description',
			dataIndex: 'description',
			header:    'Description',
			width:     280,
			sortable:  true
		}, {
			name:      'amount',
			dataIndex: 'amount',
			header:    'Amount',
			width:     120,
			sortable:  true
		}, {
			name:      'notes',
			dataIndex: 'notes',
			header:    'Notes',
			width:     120,
			sortable:  true,
			hidden:    true
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

