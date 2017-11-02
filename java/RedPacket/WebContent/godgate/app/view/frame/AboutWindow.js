Ext.define('app.view.frame.AboutWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.aboutwindow',
    requires: [
        'Ext.form.FieldSet',
        'Ext.form.field.Display',
        'Ext.form.field.Checkbox'
    ],
    closeAction: 'destroy',
    width: 400,
    title: '关于',

    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            defaults: {
                margin: '5 5 5 5'
            },
            items: [
                {
                    xtype: 'fieldset',
                    fieldDefaults: {
                        margin: '0 0 0 0'
                    },
                    title: '产品信息',
                    items: [
                        {
                            xtype: 'displayfield',
                            anchor: '100%',
                            fieldLabel: '产品名称',
                            value: $user.system
                        },
                        {
                            xtype: 'displayfield',
                            anchor: '100%',
                            fieldLabel: '版本信息',
                            value: $user.version
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: '版权',
                    items: [
                        {
                            xtype: 'displayfield',
                            anchor: '100%',
                            labelWidth: 120,
                            value: '<b>版权所有 :</b> <a href='+$user.website+'>XX科技</a>'
                        },
                        {
                            xtype: 'displayfield',
                            anchor: '100%',
                            value: '<b>联系方式 :</b> <a href="#">'+$user.telephone+'</a>'
                        }
                    ]
                }
            ]
        });
        me.callParent(arguments);
    }

});
