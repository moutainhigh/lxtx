!function(a){!function(a){var b,c=a.top.HangqingManage.CurrentGood,
	d=c.Margin.split(","),e=c.Point.split("|"),
	f=e.length,g=[],h=[],i=[],j=[];for(b=0;f>b;b++)h.push(e[b].split(",")[0]),i.push(e[b].split(",")[1]);
	for(b in d)j.push(['<li class="table-cell"><label data-index="',b,'">',parseInt(d[b]),
	                   "</label></li>"].join("")),2==b%3&&(g.push(['<ul class="table">',j.join(""),"</ul>"].join("")),
	                		   j=[]);
	j.length>0&&g.push(['<ul class="table">',j.join(""),"</ul>"].join("")),
	$("#definecashnum").html(g.join("")).find("li label").eq(0).addClass("active"),
	g=[];for(b in h)g.push(['<li class="table-cell"><label data-fee="',
	                        a.top.$.formatNumberForMinDigital(a.top.$.formatNumber(.01*i[b],4)),'">',h[b],
	                        "</label></li>"].join(""));
	$("#setting-point ul").html(g.slice(0,3).join("")).find("li:first-child label").addClass("active")}(a),
	
	
	
	function(a){var b=a.top.tap,c=a.top._;
	$("#definecashnum li label").off(b).on(b,function(b){var c=$(b.target);event.preventDefault(),
	$("#definecashnum li label.active").removeClass("active"),c.addClass("active"),
	a.SaveData.Amount=c.attr("data-index"),a.changeFee()}),
	$("#setting-point li label").off(b).on(b,function(b){var c=$(b.target);b||event.preventDefault(),
	$("#setting-point li label.active").removeClass("active"),c.addClass("active"),
	a.SaveData.PointSetting=c.parents("li").index(),a.changeFee()}),
	$("#createorderbox .num-wrap").find("input").off("input").on("input",
			function(b){var c=b.target.value;a.top.$.defineValidate.number(c)||(c=c.replace(/[^0-9]+/g,"").replace(/^[0]+/,"")),
			a.JudgeNum(c)}),$("#createorderbox .num-wrap").find(".btn-coin").off(b).on(b,function(b){var c=$(b.target),
				d=c.siblings("input").val();c.is(".btn-add")?a.JudgeNum(++d):a.JudgeNum(--d)}),
				$("#createorderbox .sure-btn-wrap .cancel label").off(b).on(b,c.throttle(function(){(event||a.top.event).preventDefault(),
					setTimeout(function(){a.top.layer.closeAll()},350)},1e3,{trailing:!1}));
					var d=!1;
					
					var submitted = false;
					$("#createorderbox .sure-btn-wrap .determine label").off(b).on(b,c.throttle(function(){
						if (submitted == true) {
							return;
						}
						submitted = true;
						if((event||a.top.event).preventDefault(),!d){
							var b=a.top.HangqingManage.CurrentGood,c=parseInt($("#definecashnum li label.active").text()),
							e=parseInt($("#setting-point li label.active").text()),f=$("#createorderbox .num-wrap input").val();
							//d=!0,
							var postdata = {direction:$("#createorderbox").is(".createup")?"1":"2",
									subject:b.Code,money:c,amount:f,limit:e,orderIndex:b.Now};
							var path = global_util.getAppPath();
							
						$.judgeLoginStatus(function(){
							if(a.validTime()){
								a.ClearSave(),
								a.top.layer.msg("当前非服务时间",{time:2e3},function(){
//									if (a.top && a.top.layer) {
//										a.top.layer.closeAll();	
//									}
									submitted = false;
								})
							}else{
								//second step, show the records
								$.get(path + "/order/createUserOrderPoint?wxid="+$("#wxid").val(), postdata, function(data) {
									//hecm 去掉 因为引入了main和prepare等js包后，此处已无需再次解析
									//data = $.parseJSON(data);
									//need input mbl
									if(data.status=="9"){
										a.ClearSave(),
										a.top.layer.msg(data.Message,{time:2e3},function(){
											if (a.top && a.top.layer) {
												a.open(path+"/user/toSetTel");
												a.top.layer.closeAll();
												submitted = false;
											}
										})
									}else if(data.status=="1"){
										a.ClearSave(),
										a.top.layer.msg(data.Message,{time:2e3},function(){
											if (a.top && a.top.layer) {
												a.top.layer.closeAll();
												submitted = false;
											}
										})
									}else{
										a.ClearSave(),
										a.top.layer.msg(data.Message,{time:2e3},function()
												{
											a.top.layerCallback(data);
											//setup the seconds
											//orderListTool.setupTimerAndList(data.orderList, data.seconds);
											if (a.top && a.top.layer) {
												a.top.layer.closeAll();
												submitted = false;
											}
												})
									}
									
								});
							}
						})
			}},2e3,{trailing:!1}))}(a),
								a.changeFee=function(){$("#setting-point li label.active").attr("data-fee"),
									$("#definecashnum li label.active").text(),$("#createorderbox .num-wrap input").val()
								},
								a.validTime=function(){
									var now = new Date();
							        var seperator1 = "-";
							        var year = now.getFullYear();
							        var month = now.getMonth() + 1;
							        var strDate = now.getDate();
							        if (month >= 1 && month <= 9) {
							            month = "0" + month;
							        }
							        if (strDate >= 0 && strDate <= 9) {
							            strDate = "0" + strDate;
							        }
							        var currentdate = year + seperator1 + month + seperator1 + strDate;
									var begin = new Date(currentdate+" 04:00:00");
									var end = new Date(currentdate+" 09:00:00");
									var week; 
									if(now.getDay()==0) week="0";
									if(now.getDay()==1) week="1";
									if(now.getDay()==6) week="6";
									/*if(week == "0"){
										return true;
									}else if(week=="1"){
										if(now.getTime() <= end.getTime()){
											return true;
										}
									}else if(week=="6"){
										if(now.getTime() >= begin.getTime()){
											return true;
										}
									}*/
									if (begin.getTime() <= now.getTime() && now.getTime() <= end.getTime()) {
										return true;
									} else {
										return false;
									}
									
								},
								a.JudgeNum=function(b){var c=a.top.HangqingManage.CurrentGood,
									d=c.MaxBuyNum||10;1>=b?(b=1,$("#createorderbox .btn-minute").addClass("disable")):$("#createorderbox .btn-minute").removeClass("disable"),
											b>=d?(b=d,$("#createorderbox .btn-add").addClass("disable")):$("#createorderbox .btn-add").removeClass("disable"),
													$("#createorderbox .num-wrap input").val(b),a.SaveData.OrderNum=b,a.changeFee()},
													a.ClearSave=function(){"2"==a.top.DealDirection?a.top.CreateSetting.Down={}:a.top.CreateSetting.Up={}},
													function(a){a.top.CreateSetting=a.top.CreateSetting||{},a.top.CreateSetting.Up=a.top.CreateSetting.Up||{},
														a.top.CreateSetting.Down=a.top.CreateSetting.Down||{};var b={},
														c=a.top.tap;a.SaveData=b="2"==a.top.DealDirection?a.top.CreateSetting.Down:a.top.CreateSetting.Up;
														var d=b.Amount||0,e=b.OrderNum||1,f=b.PointSetting||0;$("#definecashnum li").eq(d).find("label").trigger(c),
														$("#createorderbox .num-wrap input").val(e),JudgeNum(e),$("#setting-point li").eq(f).find("label").trigger(c)}(a)}(this);
