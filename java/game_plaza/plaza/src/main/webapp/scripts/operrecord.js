!function(a){
	function b(){
		var a=ServerAndGoodListData;
		return"string"==typeof a&&(a=JSON.parse(a)),a.goodlists||[]}
	var c=!1;
	var hasnextpage = false;
	a.getOperRecord=function(b){
	/*
	 * $.toRequestData({ url:a.BaseAbsolutePath+"user/queryOperRecord",
	 * data:{lastid:b||"0"}, sucBack:function(b){ alert('123');
	 * //a.setlist($.ParseToJson(b.Data||[])) } })},
	 */
		$.get(a.BaseAbsolutePath+"user/queryOperRecord", {id:b||"0"}, function(data) {
			if(data.Data.length==0){
				$("#deta_more_div").html("没有更多了")
			}else{
				var b ="";
				$.each(data.Data,function(index,item){
					b+= "<li>";
					if(index==data.Data.length-1){
            			a.orderid = item.id;
            		}
					b+=" <div class=\"title\">【"+item.dealkind+"】</div><div class=\"boxflex\">" +
							"<div class=\"keyvalue boxflex box_flex_1\"><label>选择数字</label><span class=\"box_flex_1\">"+
							item.code+"</span></div><div class=\"keyvalue boxflex box_flex_1\"><label>期数:</label><span class=\"box_flex_1\">"+
							item.directSn+"</span></div></div><div class=\"boxflex\"><div class=\"keyvalue boxflex box_flex_1\"><label>金额：</label>" +
				       		"<span class=\"box_flex_1\">"+item.money+" 万金币</span></div><div class=\"keyvalue boxflex box_flex_1\"><label>建仓时间：</label>" +
		       				"<span class=\"box_flex_1\">"+item.orderTime1+"</span></div></div>";

					if (item.state == 1) {
						b+="<div class=\"boxflex\"><div class=\"keyvalue boxflex box_flex_1\"><label>中奖码：</label>" +
			       		"<span class=\"box_flex_1\">"+item.announcement+"</span></div>";
						b+="<div class=\"keyvalue boxflex box_flex_1\">" +
						"<label>平仓时间：</label><span class=\"box_flex_1\">"+item.settlementTime1+"</span>" +
						"</div></div>" +
						"<div class=\"boxflex\"><div class=\"earn-wrap "+item.perStyle+"\"><span class=\"dearn\">"
								+item.perStatus+"</span><span class=\"profit\">"+$.formatNumber(item.position,0)+" 万金币</span></div></div>";						
					}
					
					b+="</li>";
				});
				$("#bean_list ul").append(b);
				if(data.Data.length==10){
            		hasnextpage = true;
            	}
				$("#deta_more_div").html("")
			}
		});
	}
			
			a.setlist=function(d){var e,f,g,h,i=function(a){
				var b=['<div class="title">【',a.dealkind,"】</div>",'<div class="boxflex">',
				       '<div class="keyvalue boxflex box_flex_1">',"<label>商品：</label>",
				       '<span class="box_flex_1">',a.name,"</span>",
				       "</div>",'<div class="keyvalue boxflex box_flex_1">',
				       "<label>数量：</label>",'<span class="box_flex_1">',
				       a.num,"</span>","</div>","</div>",'<div class="boxflex">','<div class="keyvalue boxflex box_flex_1">',
				       "<label>建仓价：</label>",'<span class="box_flex_1">',
				       a.holdprice,"</span>","</div>",'<div class="keyvalue boxflex box_flex_1">',
				       "<label>建仓时间：</label>",'<span class="box_flex_1">',a.holddate,"</span>","</div>",
				       "</div>",'<div class="boxflex">','<div class="keyvalue boxflex box_flex_1">',
				       "<label>定金：</label>",'<span class="box_flex_1">',a.amount,"</span>",
				       "</div>",'<div class="keyvalue boxflex box_flex_1">',"<label>止盈止损点数：</label>",'<span class="box_flex_1">',a.stoppoint,"（",a.dealdirect,"）</span>","</div>","</div>"];
				return 5!=a.OperaDetail&&b.push(['<div class="boxflex">','<div class="keyvalue boxflex box_flex_1">',"<label>平仓价：</label>",'<span class="box_flex_1">',
             a.closeprice,"</span>","</div>",'<div class="keyvalue boxflex box_flex_1">',
             "<label>平仓时间：</label>",'<span class="box_flex_1">',a.closedate,"</span>","</div>",
             "</div>",'<div class="boxflex">','<div class="keyvalue boxflex box_flex_1">',"<label>平仓类型：</label>",
             '<span class="box_flex_1">',a.closekind,"</span>","</div>",'<div class="keyvalue boxflex box_flex_1">',
             "<label>手续费：</label>",'<span class="box_flex_1">',a.dealfee,"</span>","</div>","</div>"].join("")),
             b.push(['<div class="earn-wrap ',a.profitclass,'">','<span class="dearn">',a.earnname,
                     "</span>",'<span class="profit">',a.profit,"</span>","</div>"].join("")),
                     ["<li>",b.join(""),"</li>"].join("")},
             j=d||[],k=0,l=0,m=j.length,n={},o=b(),p=[],q=null,r=null,s=null;
             for(m>0&&(a.orderid=j[m-1].HistoryID),k=0;m>k;k++)
            	 if(e=j[k],g=e.CloseType.toString(),!(["2","3","1","4"].indexOf(g)<0))
            	 {
            		 switch(h=$.formatNumber(e.Earn,2),["2","3","1","4"].indexOf(g))
            		 {
            		 	case 0:
            		 		q="止盈",r="earn-up",s="赚";
            		 		break;
            		 	case 1:
            		 		q="止损",r="earn-down",s="亏";
            		 		break;
            		 	case 2:
            		 		q="处理中",r="earn-zorn",s="处",
            		 		h="";
            		 		break;
            		 	case 3:
            		 		q=null,r="earn-zorn",s="退",
            		 		h=$.formatNumber(e.Volume*e.Used,2);
            		 		break;
            		 	default:
            		 		h="";
            		 		break;
            		 }
            		 
            		 n={OperaDetail:g,
        				 dealkind:5==g?"等待成交":"已平仓",
						name:null,
						num:e.Volume,
						holdprice:null,
						holddate:(e.OpenTime||"").substring(0,19),
						amount:e.Used,
						stoppoint:e.Point,
						closeprice:null,
						closedate:(e.CloseTime||"").substring(0,19),
						closekind:q,
						profitclass:r,
						earnname:s,
						dealdirect:"S"==e.Direction?"看跌":"看涨",
						dealfee:e.Fee,profit:h
					};
            		 for(l in o)
            			 if((f=o[l]).MarketId==e.MarketCode&&f.MerchCode==e.ProductCode)
            			 {
            				 n.name=f.Name,n.holdprice=$.formatNumber(e.OpenPrice,f.Dec||0),
            				 n.closeprice=$.formatNumber(e.ClosePrice,f.Dec||0);break
            			 }
            		 p.push(i(n))
            	}
             	$("#bean_list ul").append(p.join("")),c=!0,
             	p.length<10&&(c=!1,$("#deta_more_div").html("没有更多了"))},
             	
             	window.onload=function()
             	{window.document.body.scrollTop=0,a.getOperRecord(),
//         		window.onscroll=_.debounce(function()
//         				{var b=document.documentElement,d=document.body,e=b.clientHeight,f=b.scrollTop,g=0==f?d.scrollTop:f,h=0==f?d.scrollHeight:b.scrollHeight;
//         				g+e>=h-5&&c&&a.getOperRecord(a.orderid)},300)}
         		window.onscroll=_.debounce(function(){
                    var docuElem = document.documentElement, bodyElem = document.body,
                        clientheight = docuElem.clientHeight, docuTop = docuElem.scrollTop,
                        scrolltop = docuTop==0? bodyElem.scrollTop : docuTop,
                        scrollheight = docuTop==0? bodyElem.scrollHeight : docuElem.scrollHeight;
                    if(scrolltop+clientheight>=scrollheight-5){
                        if(hasnextpage){
                        	a.getOperRecord(a.orderid);
                        }
                    }
                }, 300)}
            }(this);                                         
