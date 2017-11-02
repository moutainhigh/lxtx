// jscript_menu
function menu(nav){	
	$('li:has(> ul)',nav).addClass('parent');
	$('li:has(> ul) > a',nav).addClass('hasSubnav');
	$("li.parent",nav).hover(function(){
		$(this).addClass('on'); $('> a', this).addClass('hover'); 
		$(this).children("ul").show();
	}, function(){
		$(this).removeClass('on'); $('> a', this).removeClass('hover'); 
		$(this).children("ul").hide();
	});
}
function accordion(nav,content,on,type){
$(nav).bind(type, function(){
		var $cur = $(this);	
		$(nav).removeClass(on);
		$(content).hide();
		$cur.addClass(on);
		$cur.next(content).fadeIn();
	})}

// jscript_tab
function jq(obj){
	return typeof(obj) == "string" ? document.getElementById(obj) : obj;
}
function hoverli(n, m){
	for(var i=1;i<=m;i++){ 
		jq('tbc_0'+i).className='undis'; 
		jq('tb_'+i).className= 'off'; 
	}
	jq('tbc_0'+n).className='dis';
	jq('tb_'+n).className= 'on';
}

//jscript_myeasyValidator
$(function(){
	$('#attrib-grid-table').find('input:text').change(function() {
		var n = parseInt($(this).val(), 10);
		if(n >= 0 && n < 1000) {} else if(n >= 1000) { n = 999; } else { n = 0; }
		$(this).val(n);
	});
	$('#attrib-grid-table').find('.add_qty').click(function() {
		var qty = parseInt($(this).parent().find('input:text').val(), 10);
		if(qty < 999) {
			qty = qty + 1;
		} else {
			qty = 999;
		}
		$(this).parent().parent().find('input:text').val(qty);
	});
	$('#attrib-grid-table').find('.reduce_qty').click(function() {
		var qty = parseInt($(this).parent().find('input:text').val(), 10);
		if(qty > 0) {
			qty = qty -1;
		} else {
			qty = 0;
		}
		$(this).parent().parent().find('input:text').val(qty);
	});
	$('td.cartQuantity').find('input:text').change(function() {
		var q = $(this).val();
		if(q > 999) q=999;
		$(this).val(q);
	});	
	
	var xOffset = -20; // x distance from mouse
  var yOffset = 20; // y distance from mouse  
	
	//input tips
	$("[reg],[url]:not([reg])").hover(
		function(e) {
			if($(this).attr('tip') != undefined){
				var top = (e.pageY + yOffset);
				var left = (e.pageX + xOffset);
				$('body').append( '<p id="vtip"><img id="vtipArrow" src="images/vtip_arrow.png"/>' + $(this).attr('tip') + '</p>' );
				$('p#vtip').css("top", top+"px").css("left", left+"px");
				$('p#vtip').bgiframe();
			}
		},
		function() {
			if($(this).attr('tip') != undefined){
				$("p#vtip").remove();
			}
		}
	).mousemove(
		function(e) {
			if($(this).attr('tip') != undefined){
				var top = (e.pageY + yOffset);
				var left = (e.pageX + xOffset);
				$("p#vtip").css("top", top+"px").css("left", left+"px");
			}
		}
	).blur(function(){
		if($(this).attr("reg") == undefined){
			ajax_validate($(this));
		}else{
			validate($(this));
		}
	});
	
	$("form").submit(function(){
		var isSubmit = true;
		$(this).find("[reg],[url]:not([reg])").each(function(){
			if($(this).attr("reg") == undefined){
				if(!ajax_validate($(this))){
					isSubmit = false;
				}
			}else{
				if(!validate($(this))){
					isSubmit = false;
				}
			}
		});
		
		$(this).find('#attrib-grid-table').each(function() {
			var tqty = 0;
			$('#attrib-grid-table').find('tr.attrib-grid-hHeader').each(function() {
				tqty += parseInt($(this).find('input:text').val(), 10);
			});
			if(tqty == 0) {
				isSubmit = false;
				alert('Please Select Your Size');
			}
		});
			
		if(typeof(isExtendsValidate) != "undefined"){
   			if(isSubmit && isExtendsValidate){
				return extendsValidate();
			}
   		}
		return isSubmit;
	});
	
});

function validate(obj){
	var reg = new RegExp(obj.attr("reg"));
	var objValue = obj.attr("value");
	
	obj.change(function() {
		change_error_style(obj,"change");
		return false;
	});
	
	if(!reg.test(objValue)){
		change_error_style(obj,"add");
		change_tip(obj,null,"remove");
		return false;
	}else{
		if(obj.attr("url") == undefined){
			change_error_style(obj,"remove");
			change_tip(obj,null,"remove");
			return true;
		}else{
			return ajax_validate(obj);
		}
	}
}

function ajax_validate(obj){
	
	var url_str = obj.attr("url");
	if(url_str.indexOf("?") != -1){
		url_str = url_str+"&"+obj.attr("name")+"="+obj.attr("value");
	}else{
		url_str = url_str+"?"+obj.attr("name")+"="+obj.attr("value");
	}
	var feed_back = $.ajax({url: url_str,cache: false,async: false}).responseText;
	feed_back = feed_back.replace(/(^\s*)|(\s*$)/g, "");
	if(feed_back == 'success'){
		change_error_style(obj,"remove");
		change_tip(obj,null,"remove");
		return true;
	}else{
		change_error_style(obj,"add");
		change_tip(obj,feed_back,"add");
		return false;
	}
}

function change_tip(obj,msg,action_type){
	
	if(obj.attr("tip") == undefined){//初始化判断TIP是否为空
		obj.attr("is_tip_null","yes");
	}
	if(action_type == "add"){
		if(obj.attr("is_tip_null") == "yes"){
			obj.attr("tip",msg);
		}else{
			if(msg != null){
				if(obj.attr("tip_bak") == undefined){
					obj.attr("tip_bak",obj.attr("tip"));
					obj.attr("tip",msg);
				}
			}
		}
	}else{
		if(obj.attr("is_tip_null") == "yes"){
			obj.removeAttr("tip");
			obj.removeAttr("tip_bak");
		}else{
			obj.attr("tip",obj.attr("tip_bak"));
			obj.removeAttr("tip_bak");
		}
	}
}

function change_error_style(obj,action_type){
	if(action_type == "add"){
		obj.addClass("input_validation-failed");
		if(obj.next().hasClass("validation_advice")){}
		else{
			if((obj.attr("tip") == undefined)){
				obj.after("<div class=\"validation_advice\" >This is a required field.</div>");
			}
			else{
				if(obj.val() ==''){
					obj.after("<div class=\"validation_advice\" >This is a required field.</div>");
					}
				else {
				obj.after("<div class=\"validation_advice\" >"+obj.attr("tip")+"</div>");
				}
			}
		}
	////////// change value
	} else if(action_type == 'change') {
		obj.next().remove();
		if((obj.attr("tip") == undefined) || obj.val() =='') {
			obj.after("<div class=\"validation_advice\" >This is a required field.</div>");
		} else {
			obj.after("<div class=\"validation_advice\" >"+obj.attr("tip")+"</div>");
		}
	//////////
	} else {
		obj.removeClass("input_validation-failed");
		if(obj.next().hasClass("validation_advice")){
			obj.next().remove();
		}
	}
}

$.fn.validate_callback = function(msg,action_type,options){
	this.each(function(){
		if(action_type == "failed"){
			change_error_style($(this),"add");
			change_tip($(this),msg,"add");
		}else{
			change_error_style($(this),"remove");
			change_tip($(this),null,"remove");
		}
	});
};

// jscript_ntabs
function nTabs(thisObj,Num){
	if(thisObj.className == "active")return;
		var tabObj = thisObj.parentNode.id;
		var tabList = document.getElementById(tabObj).getElementsByTagName("li");
		for(i=0; i <tabList.length; i++){
		if (i == Num) {
			thisObj.className = "active";
			document.getElementById(tabObj+"_Content"+i).style.display = "block";
		} else {
			tabList[i].className = "normal";
			document.getElementById(tabObj+"_Content"+i).style.display = "none";
		}
	}
}

//Add favarate to browser
//u=url, t=title
function AddFav(u, t) {
if (document.all) {
window.external.AddFavorite(u, t);
} else{
window.sidebar.addPanel(t, u, "");
}
return false;
}; 


/*$(function() {
	$(this).find('.list_box_row').each(function() {
		$(this).find('li').hover(function() {
			$(this).addClass('box_on');
		}, function() {
			$(this).removeClass('box_on');
		});
	});
	$('#teamlist1').listnav({ 
    noMatchText: 'Nothing Teams your filter', 
    showCounts: false 
  });
	$('#teamlist2').listnav({ 
    noMatchText: 'Nothing Teams your filter', 
    showCounts: false 
  });
	$('#teamlist3').listnav({ 
    noMatchText: 'Nothing Teams your filter', 
    showCounts: false 
  });
	$('#teamlist4').listnav({ 
    noMatchText: 'Nothing Teams your filter', 
    showCounts: false 
  });
	$('#teamlist5').listnav({ 
    noMatchText: 'Nothing Teams your filter', 
    showCounts: false 
  });
	$('#teamlist6').listnav({ 
    noMatchText: 'Nothing Teams your filter', 
    showCounts: false 
  });
});
*/
/*
* jquery.listnav.pack-2.1.js
* Visit http://www.ihwy.com/labs/jquery-listnav-plugin.aspx for more information.
*/
eval(function(p, a, c, k, e, r) { e = function(c) { return (c < a ? '' : e(parseInt(c / a))) + ((c = c % a) > 35 ? String.fromCharCode(c + 29) : c.toString(36)) }; if (!''.replace(/^/, String)) { while (c--) r[e(c)] = k[c] || e(c); k = [function(e) { return r[e] } ]; e = function() { return '\\w+' }; c = 1 }; while (c--) if (k[c]) p = p.replace(new RegExp('\\b' + e(c) + '\\b', 'g'), k[c]); return p } ('(3($){$.15.16=3(f){4 g=$.1O({},$.15.16.1m,f);4 h=[\'T\',\'a\',\'b\',\'c\',\'d\',\'e\',\'f\',\'g\',\'h\',\'i\',\'j\',\'k\',\'l\',\'m\',\'n\',\'o\',\'p\',\'q\',\'r\',\'s\',\'t\',\'u\',\'v\',\'w\',\'x\',\'y\',\'z\',\'-\'];4 j=E;g.K=$.1P(g.K,3(n){F n.17()});F 7.1n(3(){4 d,8,$8,$5,$G,U;U=7.U;d=$(\'#\'+U+\'-1Q\');$8=$(7);4 e={},18=0,V=A,1R=0,X=\'\';3 1o(){d.1p(1q());$5=$(\'.6-5\',d).L(0,1);2(g.M)$G=$(\'.6-1r-1s\',d).L(0,1);1t();1u();2(g.1v)1w();1x();2(!g.Y)$8.N();2(!g.Y)$(\'.O\',$5).19();2(!g.1a)$(\'.T\',$5).19();2(!g.1S)$(\'.-\',$5).19();$(\':1y\',$5).P(\'6-1y\');2($.Z&&(g.Q!=H)){4 a=$.Z(g.Q);2(a!=H)g.10=a}2(g.10!=\'\'){j=A;$(\'.\'+g.10.17(),$5).L(0,1).1b()}D{2(g.Y)$(\'.O\',$5).P(\'6-11\');D{1c(4 i=((g.1a)?0:1);i<h.I;i++){2(e[h[i]]>0){j=A;$(\'.\'+h[i],$5).L(0,1).1b();1T}}}}}3 1z(){$G.1A({1d:$(\'.a\',$5).L(0,1).1U({1e:E,1V:A}).1d-$G.1W({1e:A})})}3 1t(){4 a,J,1X,R,$7,1B=(g.K.I>0);$($8).B().1n(3(){$7=$(7),J=\'\',a=$.1Y($7.1C()).17();2(a!=\'\'){2(1B){R=a.1f(\' \');2((R.I>1)&&($.1Z(R[0],g.K)>-1)){J=R[1].1D(0);1g(J,$7,A)}}J=a.1D(0);1g(J,$7)}})}3 1g(a,b,c){2(/\\W/.20(a))a=\'-\';2(!21(a))a=\'T\';b.P(\'6-\'+a);2(e[a]==1h)e[a]=0;e[a]++;2(!c)18++}3 1w(){1c(4 i=0;i<h.I;i++){2(e[h[i]]==1h)$(\'.\'+h[i],$5).P(\'6-22\')}}3 1u(){$8.1p(\'<1E C="6-12-13" 1F="1G:1H">\'+g.1I+\'</1E>\')}3 1i(a){2($(a).23(\'O\'))F 18;D{4 b=e[$(a).1J(\'C\').1f(\' \')[0]];F(b!=1h)?b:0}}3 1x(){2(g.M){d.1K(3(){1z()})}2(g.M){$(\'a\',$5).1K(3(){4 a=$(7).1L().1j;4 b=($(7).24({1e:A})-1)+\'25\';4 c=1i(7);$G.1A({1j:a,1M:b}).1C(c).N()});$(\'a\',$5).26(3(){$G.S()})}$(\'a\',$5).1b(3(){$(\'a.6-11\',$5).27(\'6-11\');4 a=$(7).1J(\'C\').1f(\' \')[0];2(a==\'O\'){$8.B().N();$8.B(\'.6-12-13\').S();V=A}D{2(V){$8.B().S();V=E}D 2(X!=\'\')$8.B(\'.6-\'+X).S();4 b=1i(7);2(b>0){$8.B(\'.6-12-13\').S();$8.B(\'.6-\'+a).N()}D $8.B(\'.6-12-13\').N();X=a}2($.Z&&(g.Q!=H))$.Z(g.Q,a);$(7).P(\'6-11\');$(7).28();2(!j&&(g.1k!=H))g.1k(a);D j=E;F E})}3 1q(){4 a=[];1c(4 i=1;i<h.I;i++){2(a.I==0)a.1N(\'<a C="O" 1l="#">29</a><a C="T" 1l="#">0-9</a>\');a.1N(\'<a C="\'+h[i]+\'" 1l="#">\'+((h[i]==\'-\')?\'...\':h[i].2a())+\'</a>\')}F\'<14 C="6-5">\'+a.2b(\'\')+\'</14>\'+((g.M)?\'<14 C="6-1r-1s" 1F="1G:1H; 1L:2c; 1d:0; 1j:0; 1M:2d;">0</14>\':\'\')}1o()})};$.15.16.1m={10:\'\',Y:A,2e:E,1a:A,1v:A,1I:\'2f 2g 2h\',M:A,Q:H,1k:H,K:[]}})(2i);', 62, 143, '||if|function|var|letters|ln|this|list||||||||||||||||||||||||||||true|children|class|else|false|return|letterCount|null|length|firstChar|prefixes|slice|showCounts|show|all|addClass|cookieName|spl|hide|_|id|isAll||prevLetter|includeAll|cookie|initLetter|selected|no|match|div|fn|listnav|toLowerCase|allCount|remove|includeNums|click|for|top|margin|split|addLetterClass|undefined|getLetterCount|left|onClick|href|defaults|each|init|append|createLettersHtml|letter|count|addClasses|addNoMatchLI|flagDisabled|addDisabledClass|bindHandlers|last|setLetterCountTop|css|hasPrefixes|text|charAt|li|style|display|none|noMatchText|attr|mouseover|position|width|push|extend|map|nav|numCount|includeOther|break|offset|border|outerHeight|firstWord|trim|inArray|test|isNaN|disabled|hasClass|outerWidth|px|mouseout|removeClass|blur|ALL|toUpperCase|join|absolute|20px|incudeOther|No|matching|entries|jQuery'.split('|'), 0, {}))