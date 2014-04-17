
Ext.namespace("ui.panel.manager.usertask");

ui.panel.manager.usertask.AssignmentAddPanel = Ext.extend(Ext.form.FormPanel, {
    constructor: function(c) {
        if (!c || !c.user)
            throw "AssignmentAddPanel requires an user to be provided.";

        var config = Ext.applyIf(c || {}, {
            id:         'ui.panel.manager.usertask.assignmentaddpanel',
            title:      'Add a new Task Assignment for ' + c.user.data.fullName,
            width:      470,
            autoHeight: true,
            bodyStyle:  'padding: 10px;',
            labelWidth: 120,
            items: [
                {
                    xtype: 'hidden',
                    name:  'userId',
                    value: c.user.data.id
                }, {
                    xtype: 'label',
                    html:  '<div style="padding:0px 5px 6px 104px;">Select ' +
                           'the task to which the user should be assigned.' +
                           '</div>'
                }, new Ext.form.ComboBox({
                    fieldLabel:     'Task',
                    name:           'taskId',
                    displayField:   'description',
                    valueField:     'id',
                    hiddenName:     'taskId',
                    mode:           'local',
                    forceSelection: true,
                    triggerAction:  'all',
                    selectOnFocus:  true,
                    width:          320,
                    allowBlank:     false,
                    store: new data.store.manager.TaskStore({
                        includeAdministrative: false,
                        includeInactive: false
                    })
                }), {
                    xtype: 'label',
                    html:  '<div style="padding:0px 5px 6px 104px;">Choose ' +
                           'a date range during which the user can bill hours ' +
                           'to the above task.' +
                           '</div>'
                }, {
                    xtype:      'datefield',
                    fieldLabel: 'Assignment Start',
                    name:       'begin',
                    width:      220
                }, {
                    xtype:      'datefield',
                    fieldLabel: 'Assignment End',
                    name:       'end',
                    width:      220
                }, {
                    xtype: 'label',
                    html:  '<div style="padding:0px 5px 6px 104px;">Provide ' +
                           'the labor category used to describe the type of ' +
                           'work being performed on the task.' +
                           '</div>'
                }, {
                    xtype:      'textfield',
                    fieldLabel: 'Labor Category',
                    name:       'laborCat',
                    width:      300,
                    allowBlank: false
                }, {
                    xtype: 'label',
                    html:  '<div style="padding:0px 5px 6px 104px;">Provide ' +
                           'the name of the item associated with this ' +
                           'assignment in the accounting system. (See ' +
                           'information on <a href="/integration/">Integration' +
                           '</a> for more information.) Example: ' +
                           '<li>John Doe:Example Task</li>' +
                           '</div>'
                }, {
                    xtype:      'textfield',
                    fieldLabel: 'Item Name',
                    name:       'itemName',
                    width:      300,
                    allowBlank: false
                }
            ],
            buttons: [
                new Ext.Button(new action.manager.usertask.DoAssignmentAdd()),
                new Ext.Button(new action.manager.usertask.ShowUserTaskGrid())
            ]
        });

        ui.panel.manager.usertask.AssignmentAddPanel.superclass.constructor.call(this, config);
    },

    setInitialFocus: function() {
        this.getForm().findField('taskId').focus();
    }
});

