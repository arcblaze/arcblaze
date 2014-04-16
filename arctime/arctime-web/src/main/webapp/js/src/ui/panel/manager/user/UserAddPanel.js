
Ext.namespace("ui.panel.manager.user");

ui.panel.manager.user.UserAddPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		var privilegeItems = [ ];
		for (var r = 0; r < roles.length; r++) {
			privilegeItems.push({
				boxLabel:   roles[r],
				name:       'roles',
				id:         'user-privileges-' + roles[r].toLowerCase(),
				inputValue: roles[r],
				checked:    false
			});
		}

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.user.useraddpanel',
			title:      'Add a new User Account',
			width:      400,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			items: [
				{
					xtype:      'textfield',
					fieldLabel: 'First Name',
					name:       'firstName',
					allowBlank: false,
					width:      260,
					labelWidth: 120
				}, {
					xtype:      'textfield',
					fieldLabel: 'Last Name',
					name:       'lastName',
					allowBlank: false,
					width:      260,
					labelWidth: 120
				}, {
					xtype:      'textfield',
					fieldLabel: 'Login',
					name:       'login',
					allowBlank: false,
					width:      220,
					labelWidth: 120
				}, {
					xtype:      'textfield',
					inputType:  'password',
					fieldLabel: 'Password',
					name:       'password',
					allowBlank: false,
					width:      220,
					labelWidth: 120
				}, {
					xtype:      'textfield',
					inputType:  'password',
					fieldLabel: 'Confirm Password',
					name:       'confirm',
					allowBlank: false,
					width:      220,
					labelWidth: 120
				}, {
					xtype:      'textfield',
					fieldLabel: 'Email',
					name:       'email',
					allowBlank: false,
					width:      375,
					labelWidth: 120
				}, new Ext.form.ComboBox({
					fieldLabel:     'Primary Supervisor',
					name:           'supervisor',
					displayField:   'fullName',
					valueField:     'id',
					hiddenName:     'supervisor',
					mode:           'local',
					forceSelection: true,
					triggerAction:  'all',
					selectOnFocus:  true,
					width:          375,
					labelWidth:     120,
					allowBlank:     false,
					store: new data.store.manager.UserStore({
						includeInactive: false
					})
				}), {
					xtype:      'checkboxgroup',
					fieldLabel: 'Privileges',
					name:       'privileges',
					columns:    2,
					items:      privilegeItems,
					width:      320,
					labelWidth: 120
				}, {
					xtype:      'radiogroup',
					fieldLabel: 'Active',
					name:       'active',
					width:      240,
					labelWidth: 120,
					items: [
						{
							boxLabel:   'Yes',
							name:       'active',
							id:         'user-active-yes',
							inputValue: 'true',
							checked:    true,
							style:      'border: 0px;'
						}, {
							boxLabel:   'No',
							name:       'active',
							id:         'user-active-no',
							inputValue: 'false',
							checked:    false,
							style:      'border: 0px;'
						}
					]
				}
			],
			buttons: [
				new Ext.Button(new action.manager.user.DoUserAdd()),
				new Ext.Button(new action.manager.user.ShowUserGrid())
			]
		});

		ui.panel.manager.user.UserAddPanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.getForm().findField('firstName').focus();
	}
});

