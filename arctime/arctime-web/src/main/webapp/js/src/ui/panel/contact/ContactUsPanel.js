
Ext.namespace("ui.panel.contact");

ui.panel.contact.ContactUsPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "ProfileUpdatePanel requires an user.";

		var form = this;

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.contact.contactuspanel',
			border:     true,
			frame:      false,
			title:       'Contact Us',
			width:       690,
			autoHeight:  true,
			bodyStyle:   'padding: 10px;',
			labelWidth:  70,
			items: [
				{
					xtype:      'textfield',
					fieldLabel: 'Name',
					name:       'name',
					allowBlank: false,
					width:      360
				}, {
					xtype:      'textfield',
					fieldLabel: 'Email',
					name:       'email',
					allowBlank: false,
					width:      360
				}, {
					xtype:      'textarea',
					fieldLabel: 'Message',
					name:       'message',
					allowBlank: false,
					width:      650,
					height:     250
				}
			],
			buttons: [
				new Ext.Button(new action.contact.DoSendMessage())
			]
		});

		ui.panel.contact.ContactUsPanel.superclass.constructor.call(this, config);

		// Set the values in the form.
		this.setValues(c.user);
	},

	setInitialFocus: function() {
		this.getForm().findField('name').focus();
	},

	setValues: function(user) {
		if (user) {
			// Set the form values.
			this.getForm().findField('name').
				setValue(user.data.fullName);
			this.getForm().findField('email').setValue(user.data.email);
		}

		// Set the form focus.
		this.setInitialFocus();
	}
});

