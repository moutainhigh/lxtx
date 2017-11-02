/*初始化表格工具栏*/
function initNavGrid(grid_selector, pager_selector, hasAdd, hasDel, hasEdit,
		hasSearch, hasRefresh, hasView) {
	var firstName="";
	var userReg = /^[0-9]+$/;
	// navButtons
	jQuery(grid_selector).jqGrid(
			'navGrid',
			pager_selector,
			{ // navbar options
				edit : hasEdit,
				editicon : 'icon-pencil blue',
				add : hasAdd,
				addicon : 'icon-plus-sign purple',
				del : hasDel,
				delicon : 'icon-trash red',
				search : hasSearch,
				searchicon : 'icon-search orange',
				refresh : hasRefresh,
				refreshicon : 'icon-refresh green',
				view : hasView,
				viewicon : 'icon-zoom-in grey',
			},
			{
				// edit record form
				closeAfterEdit : true,
				closeAfterAdd : true,
				recreateForm : true,
				beforeShowForm : function(e) {
					var form = $(e[0]);
					form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar')
							.wrapInner('<div class="widget-header" />')
					style_edit_form(form);
					firstName = $(".CaptionTD:first").text();
					$(".CaptionTD:first").append(
							"<span style='color:red;'>*</span>");
				},
				afterSubmit : function(data, postdata) {
					var url = subUrl();
					if (!common.checkSession(data, url + "login")) {
						return;
					}
					common.checkResponseMsg(data.responseJSON, "修改");
					$(grid_selector).jqGrid().trigger("reloadGrid");
					return true;
				},
				beforeSubmit : function(postdata, formid) {
					var checkVal="";
					var j = 0;
					for ( var i in postdata) {
						if (j == 0) {
							checkVal = eval("postdata." + i);
						}
						j++;
					}

					var checkUserName = false;
					if ($.trim(checkVal) == '') {
						return [ false, firstName + " 不能为空！" ];
					} else if (firstName == '用户名') {
						if (!userReg.test($.trim(checkVal))) {
							return [ false, firstName + "格式不正确！" ];
						}
						var userId = postdata['grid-table_id'];
						$.ajax({
							url : "checkUserNameUnique.json",
							data : {
								"userName" : $.trim(checkVal),
								"id" : userId
							},
							async : false,
							type : "POST",
							success : function(data) {
								var url = subUrl();
								if (!common.checkSession(data, url + "login")) {
									return;
								}
								if (data.data == false) {
									checkUserName = true;
								}
							}
						});
						if (checkUserName) {
							return [ false, firstName + " 已存在！" ];
						}
					}
					return [ true, "" ];
					/*
					 * 方法需要返回[success,message]格式的数组信息 success
					 * ：boolean值，为true指示后续的操作继续.否则停止并显示错误信息（message）提示用户。
					 * message ：错误时显示的提示信息
					 */
				}
			},
			{
				// new record form
				closeAfterAdd : true,
				closeAfterEdit : true,
				recreateForm : true,
				viewPagerButtons : false,
				beforeShowForm : function(e) {
					var form = $(e[0]);
					form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar')
							.wrapInner('<div class="widget-header" />')
					style_edit_form(form);
					/*
					 * if($("#tr_employeeNo").length>0){//用户信息管理：工号不能为空
					 * employeeName=$("#tr_employeeNo").text();
					 * $("#tr_employeeNo .CaptionTD").append("<span
					 * style='color:red;'>*</span>"); }
					 */
					firstName = $(".CaptionTD:first").text();

					$(".CaptionTD:first").append(
							"<span style='color:red;'>*</span>");
				},
				afterSubmit : function(data, postdata) {
					var url = subUrl();
					if (!common.checkSession(data, url + "login")) {
						return;
					}
					common.checkResponseMsg(data.responseJSON, "添加");
					$(grid_selector).jqGrid().trigger("reloadGrid");
					return "SUCCESS";
				},
				beforeSubmit : function(postdata, formid) {
					var checkVal="";
					var j = 0;
					for ( var i in postdata) {
						if (j == 0) {
							checkVal = eval("postdata." + i);
						}
						j++;
					}
					var checkUserName = false;
					if ($.trim(checkVal) == '') {
						return [ false, firstName + " 不能为空！" ];
					} else if (firstName == '用户名') {
						if (!userReg.test($.trim(checkVal))) {
							return [ false, firstName + "格式不正确！" ];
						}
						$.ajax({
							url : "checkUserNameUnique.json",
							data : {
								"userName" : $.trim(checkVal)
							},
							async : false,
							type : "POST",
							success : function(data) {
								var url = subUrl();
								if (!common.checkSession(data, url + "login")) {
									checkUserName = false;
								} else {
									if (data.data == false) {// 存在
										checkUserName = true;
									}
								}
							}
						});
						if (checkUserName) {
							return [ false, firstName + " 已存在！" ];
						}
					} 
						return [ true, "" ];
					

					/*
					 * 方法需要返回[success,message]格式的数组信息 success
					 * ：boolean值，为true指示后续的操作继续，否则停止并显示错误信息（message）提示用户。
					 * message ：错误时显示的提示信息
					 */

				}
			},
			{
				// delete record form
				closeAfterAdd : true,
				recreateForm : true,
				beforeShowForm : function(e) {
					var form = $(e[0]);
					if (form.data('styled'))
						return false;

					form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar')
							.wrapInner('<div class="widget-header" />')
					style_delete_form(form);

					form.data('styled', true);
				},
				afterSubmit : function(data, postdata) {
					var url = subUrl();
					if (!common.checkSession(data, url + "login")) {
						return;
					}
					common.checkResponseMsg(data.responseJSON, "删除");
					$(grid_selector).jqGrid().trigger("reloadGrid");
					return "SUCCESS";
				}
			},
			{
				// search form
				recreateForm : true,
				afterShowSearch : function(e) {
					var form = $(e[0]);
					form.closest('.ui-jqdialog').find('.ui-jqdialog-title')
							.wrap('<div class="widget-header" />')
					style_search_form(form);
				},
				afterRedraw : function() {
					style_search_filters($(this));
				},
				multipleSearch : true,
			/**
			 * multipleGroup:true, showQuery: true
			 */
			},
			{
				// view record form
				recreateForm : true,
				beforeShowForm : function(e) {
					var form = $(e[0]);
					form.closest('.ui-jqdialog').find('.ui-jqdialog-title')
							.wrap('<div class="widget-header" />')
				}
			})

}

function subUrl() {
	var url = window.location.href;
	url = url.substring(0, url.lastIndexOf('\/'));
	url = url.substring(0, url.lastIndexOf('\/') + 1);
	return url;
}

function style_edit_form(form) {
	// enable datepicker on "sdate" field and switches for "stock" field
	form.find('input[name=sdate]').datepicker({
		format : 'yyyy-mm-dd',
		autoclose : true
	}).end().find('input[name=stock]').addClass('ace ace-switch ace-switch-5')
			.wrap('<label class="inline" />')
			.after('<span class="lbl"></span>');

	// update buttons classes
	var buttons = form.next().find('.EditButton .fm-button');
	buttons.addClass('btn btn-sm').find('[class*="-icon"]').remove();// ui-icon,
	// s-icon
	buttons.eq(0).addClass('btn-primary').prepend('<i class="icon-ok"></i>');
	buttons.eq(1).prepend('<i class="icon-remove"></i>')

	buttons = form.next().find('.navButton a');
	buttons.find('.ui-icon').remove();
	// buttons.eq(0).append('<i class="icon-chevron-left"></i>');
	// buttons.eq(1).append('<i class="icon-chevron-right"></i>');
}

function style_delete_form(form) {
	var buttons = form.next().find('.EditButton .fm-button');
	buttons.addClass('btn btn-sm').find('[class*="-icon"]').remove();// ui-icon,
	// s-icon
	buttons.eq(0).addClass('btn-danger').prepend('<i class="icon-trash"></i>');
	buttons.eq(1).prepend('<i class="icon-remove"></i>')
}

function style_search_filters(form) {
	form.find('.delete-rule').val('X');
	form.find('.add-rule').addClass('btn btn-xs btn-primary');
	form.find('.add-group').addClass('btn btn-xs btn-success');
	form.find('.delete-group').addClass('btn btn-xs btn-danger');
}
function style_search_form(form) {
	var dialog = form.closest('.ui-jqdialog');
	var buttons = dialog.find('.EditTable')
	buttons.find('.EditButton a[id*="_reset"]').addClass('btn btn-sm btn-info')
			.find('.ui-icon').attr('class', 'icon-retweet');
	buttons.find('.EditButton a[id*="_query"]').addClass(
			'btn btn-sm btn-inverse').find('.ui-icon').attr('class',
			'icon-comment-alt');
	buttons.find('.EditButton a[id*="_search"]').addClass(
			'btn btn-sm btn-purple').find('.ui-icon').attr('class',
			'icon-search');
}

function beforeDeleteCallback(e) {
	var form = $(e[0]);
	if (form.data('styled'))
		return false;

	form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner(
			'<div class="widget-header" />')
	style_delete_form(form);

	form.data('styled', true);
}

function beforeEditCallback(e) {
	var form = $(e[0]);
	form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner(
			'<div class="widget-header" />')
	style_edit_form(form);
}

// it causes some flicker when reloading or navigating grid
// it may be possible to have some custom formatter to do this as the grid is
// being created to prevent this
// or go back to default browser checkbox styles for the grid
function styleCheckbox(table) {
	/**
	 * $(table).find('input:checkbox').addClass('ace') .wrap('<label />')
	 * .after('<span class="lbl align-top" />')
	 * 
	 * 
	 * $('.ui-jqgrid-labels th[id*="_cb"]:first-child')
	 * .find('input.cbox[type=checkbox]').addClass('ace') .wrap('<label
	 * />').after('<span class="lbl align-top" />');
	 */
}

// unlike navButtons icons, action icons in rows seem to be hard-coded
// you can change them like this in here if you want
function updateActionIcons(table) {
	/**
	 * var replacement = { 'ui-icon-pencil' : 'icon-pencil blue',
	 * 'ui-icon-trash' : 'icon-trash red', 'ui-icon-disk' : 'icon-ok green',
	 * 'ui-icon-cancel' : 'icon-remove red' }; $(table).find('.ui-pg-div
	 * span.ui-icon').each(function(){ var icon = $(this); var $class =
	 * $.trim(icon.attr('class').replace('ui-icon', '')); if($class in
	 * replacement) icon.attr('class', 'ui-icon '+replacement[$class]); })
	 */
}

// replace icons with FontAwesome icons like above
function updatePagerIcons(table) {
	var replacement = {
		'ui-icon-seek-first' : 'icon-double-angle-left bigger-140',
		'ui-icon-seek-prev' : 'icon-angle-left bigger-140',
		'ui-icon-seek-next' : 'icon-angle-right bigger-140',
		'ui-icon-seek-end' : 'icon-double-angle-right bigger-140'
	};
	$('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon')
			.each(function() {
				var icon = $(this);
				var $class = $.trim(icon.attr('class').replace('ui-icon', ''));

				if ($class in replacement)
					icon.attr('class', 'ui-icon ' + replacement[$class]);
			})
}

function enableTooltips(table) {
	$('.navtable .ui-pg-button').tooltip({
		container : 'body'
	});
	$(table).find('.ui-pg-div').tooltip({
		container : 'body'
	});
}

// var selr = jQuery(grid_selector).jqGrid('getGridParam','selrow');

// enable search/filter toolbar
// jQuery(grid_selector).jqGrid('filterToolbar',{defaultSearch:true,stringResult:true})

// switch element when editing inline
function aceSwitch(cellvalue, options, cell) {
	setTimeout(function() {
		$(cell).find('input[type=checkbox]').wrap('<label class="inline" />')
				.addClass('ace ace-switch ace-switch-5').after(
						'<span class="lbl"></span>');
	}, 0);
}
// enable datepicker
function pickDate(cellvalue, options, cell) {
	setTimeout(function() {
		$(cell).find('input[type=text]').datepicker({
			format : 'yyyy-mm-dd',
			autoclose : true
		});
	}, 0);
}