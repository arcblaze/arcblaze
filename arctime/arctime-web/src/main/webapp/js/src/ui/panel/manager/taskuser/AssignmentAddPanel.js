
Ext.namespace("ui.panel.manager.taskuser");

ui.panel.manager.taskuser.AssignmentAddPanel = Ext.extend(Ext.form.FormPanel, {
    constructor: function(c) {
        if (!c || !c.task)
            throw "AssignmentAddPanel requires a task to be provided.";

        var config = Ext.applyIf(c || {}, {
            id:         'ui.panel.manager.taskuser.assignmentaddpanel',
            title:      'Add a new User Assignment for ' +
                        c.task.data.description + ' (' + c.task.data.jobCode + ')',
            width:      450,
            autoHeight: true,
            bodyStyle:  'padding: 10px;',
            labelWidth: 120,
            items: [
                {
                    xtype: 'hidden',
                    name:  'taskId',
                    value: c.task.data.id
                }, {
                    xtype: 'label',
                    html:  '<div style="padding:0px 5px 6px 104px;">' +
                           'Select the user to be assigned to this task.' +
                           '</div>'
                }, new Ext.form.ComboBox({
                    fieldLabel:     'User',
                    name:           'userId',
                    displayField:   'fullName',
                    valueField:     'id',
                    hiddenName:     'userId',
                    mode:           'local',
                    forceSelection: true,
                    triggerAction:  'all',
                    selectOnFocus:  true,
                    width:          210,
                    allowBlank:     false,
                    store: new data.store.manager.UserStore({
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
                    width:      340,
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
                    width:      340,
                    allowBlank: false
                }
            ],
            buttons: [
                new Ext.Button(new action.manager.taskuser.DoAssignmentAdd()),
                new Ext.Button(new action.manager.taskuser.ShowTaskUserGrid())
            ]
        });

        ui.panel.manager.taskuser.AssignmentAddPanel.superclass.constructor.call(this, config);
    },

    setInitialFocus: function() {
        this.getForm().findField('userId').focus();
    }
});

