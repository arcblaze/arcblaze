
Ext.namespace("data.model");

Ext.define('data.model.User', {
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
			name:      'email',
			dataIndex: 'email',
			header:    'Email',
			width:     230,
			sortable:  true
		}, {
			name:      'privileges',
			dataIndex: 'privileges',
			header:    'Privileges',
			width:     80,
			sortable:  true
		}, {
			name:      'roles',
			dataIndex: 'roles',
			header:    'Roles',
			hidden:    true,
			width:     160,
			hidden:    true,
			sortable:  true,
			renderer:  function(val) {
				var names = [ ];
				if (val) {
					if ("array" === typeof(val)) {
						for (var i = 0; i < val.length; i++)
							names.push(val[i].name);
					} else if ("object" === typeof(val)) {
						for (var i in val)
							names.push(val[i].name);
					} else
						return val;
				}
				return names.join(", ");
			}
		}, {
			name:      'active',
			dataIndex: 'active',
			header:    'Active',
			width:     60,
			hidden:    false,
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

