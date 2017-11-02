define("a/a.js",["biz_common/dom/event.js","biz_common/utils/url/parse.js","a/a_report.js","biz_wap/utils/ajax.js","biz_wap/utils/position.js","a/card.js","a/mpshop.js","biz_wap/jsapi/core.js","biz_common/tmpl.js","a/a_tpl.html.js","a/sponsor_a_tpl.html.js","biz_common/utils/report.js","biz_common/dom/class.js","biz_wap/utils/storage.js","appmsg/cdn_img_lib.js","biz_wap/utils/mmversion.js","a/profile.js","a/android.js","a/ios.js","a/app_card.js","a/sponsor.js"],function(require,exports,module,alert){
"use strict";
function report(e,a){
Report("http://mp.weixin.qq.com/mp/ad_report?action=follow&type="+e+a);
}
function checkNeedAds(){
var is_need_ad=1,_adInfo=null,screen_num=0,both_ad=0,inwindowwx=-1!=navigator.userAgent.indexOf("WindowsWechat");
if(!document.getElementsByClassName||-1==navigator.userAgent.indexOf("MicroMessenger")||inwindowwx)is_need_ad=0,
js_sponsor_ad_area.style.display="none",js_top_ad_area.style.display="none",js_bottom_ad_area.style.display="none";else{
var adLS=new LS("ad");
if(window.localStorage)try{
var key=[biz,sn,mid,idx].join("_"),_ad=adLS.get(key);
_adInfo=_ad.info;
try{
_adInfo=eval("("+_adInfo+")");
}catch(e){
_adInfo=null;
}
var _adInfoSaveTime=_ad.time,_now=+new Date;
_adInfo&&18e4>_now-1*_adInfoSaveTime&&1*_adInfo.advertisement_num>0?is_need_ad=0:adLS.remove(key);
}catch(e){
is_need_ad=1,_adInfo=null;
}
}
return screen_num=Math.ceil(document.body.scrollHeight/(document.documentElement.clientHeight||window.innerHeight)),
both_ad=screen_num>=2?1:0,{
is_need_ad:is_need_ad,
both_ad:both_ad,
_adInfo:_adInfo
};
}
function afterGetAdData(e,a){
var t={},i=e.is_need_ad,o=e._adInfo;
if(0==i)t=o,t||(t={
advertisement_num:0
});else{
if(a.advertisement_num>0&&a.advertisement_info){
var p=a.advertisement_info;
t.advertisement_info=saveCopy(p);
}
t.advertisement_num=a.advertisement_num;
}
if(1==i&&(window._adRenderData=t),t=t||{
advertisement_num:0
},!t.flag&&t.advertisement_num>0){
var n=t.advertisement_num,r=t.advertisement_info;
window.adDatas.num=n;
for(var _=0;n>_;++_){
var d=null,s=r[_];
if(s.exp_info=s.exp_info||{},s.is_cpm=s.is_cpm||0,s.biz_info=s.biz_info||{},s.app_info=s.app_info||{},
s.pos_type=s.pos_type||0,s.logo=s.logo||"",100==s.pt||115==s.pt){
for(var l=s.exp_info.exp_value||[],c=!1,m=0,u=0;u<l.length;++u){
var f=l[u]||{};
if(1==f.exp_type&&(m=f.comm_attention_num,c=m>0),2==f.exp_type){
c=!1,m=0;
break;
}
}
s.biz_info.show_comm_attention_num=c,s.biz_info.comm_attention_num=m,d={
usename:s.biz_info.user_name,
pt:s.pt,
url:s.url,
traceid:s.traceid,
adid:s.aid,
ticket:s.ticket,
is_appmsg:!0
};
}else if(102==s.pt)d={
appname:s.app_info.app_name,
versioncode:s.app_info.version_code,
pkgname:s.app_info.apk_name,
androiddownurl:s.app_info.apk_url,
md5sum:s.app_info.app_md5,
signature:s.app_info.version_code,
rl:s.rl,
traceid:s.traceid,
pt:s.pt,
ticket:s.ticket,
type:s.type,
adid:s.aid,
is_appmsg:!0
};else if(101==s.pt)d={
appname:s.app_info.app_name,
app_id:s.app_info.app_id,
icon_url:s.app_info.icon_url,
appinfo_url:s.app_info.appinfo_url,
rl:s.rl,
traceid:s.traceid,
pt:s.pt,
ticket:s.ticket,
type:s.type,
adid:s.aid,
is_appmsg:!0
};else if(103==s.pt||104==s.pt||2==s.pt&&s.app_info){
var g=s.app_info.down_count||0,y=s.app_info.app_size||0,v=s.app_info.app_name||"",j=s.app_info.category,h=["万","百万","亿"];
if(g>=1e4){
g/=1e4;
for(var w=0;g>=10&&2>w;)g/=100,w++;
g=g.toFixed(1)+h[w]+"次";
}else g=g.toFixed(1)+"次";
y=formSize(y),j=j?j[0]||"其他":"其他",v=formName(v),s.app_info._down_count=g,s.app_info._app_size=y,
s.app_info._category=j,s.app_info.app_name=v,d={
appname:s.app_info.app_name,
app_rating:s.app_info.app_rating||0,
app_id:s.app_info.app_id,
channel_id:s.app_info.channel_id,
md5sum:s.app_info.app_md5,
rl:s.rl,
pkgname:s.app_info.apk_name,
url_scheme:s.app_info.url_scheme,
androiddownurl:s.app_info.apk_url,
versioncode:s.app_info.version_code,
appinfo_url:s.app_info.appinfo_url,
traceid:s.traceid,
pt:s.pt,
url:s.url,
ticket:s.ticket,
type:s.type,
adid:s.aid,
is_appmsg:!0
};
}else if(105==s.pt){
var b=s.card_info.card_id||"",k=s.card_info.card_ext||"";
k=k.htmlDecode();
try{
k=JSON.parse(k),k.outer_str=s.card_info.card_outer_id||"",k=JSON.stringify(k);
}catch(x){
k="{}";
}
d={
card_id:b,
card_ext:k,
pt:s.pt,
ticket:s.ticket||"",
url:s.url,
rl:s.rl,
tid:s.traceid,
traceid:s.traceid,
type:s.type,
adid:s.aid,
is_appmsg:!0
};
}else if(106==s.pt){
var z=s.mp_shop_info.pid||"",I=s.mp_shop_info.outer_id||"";
d={
pid:z,
outer_id:I,
pt:s.pt,
url:s.url,
rl:s.rl,
tid:s.traceid,
traceid:s.traceid,
type:s.type,
adid:s.aid,
is_appmsg:!0
};
}else if(108==s.pt||109==s.pt||110==s.pt)d={
pt:s.pt,
ticket:s.ticket||"",
url:s.url,
traceid:s.traceid,
adid:s.aid,
is_appmsg:!0
};else if(111==s.pt||113==s.pt||114==s.pt||112==s.pt){
var y=s.app_info.app_size||0,v=s.app_info.app_name||"";
y=formSize(y),v=formName(v),s.app_info.app_size=y,s.app_info.app_name=v,d={
appname:s.app_info.app_name,
app_rating:s.app_info.app_rating||0,
app_id:s.app_info.app_id,
channel_id:s.app_info.channel_id,
md5sum:s.app_info.app_md5,
rl:s.rl,
pkgname:s.app_info.apk_name,
url_scheme:s.app_info.url_scheme,
androiddownurl:s.app_info.apk_url,
versioncode:s.app_info.version_code,
appinfo_url:s.app_info.appinfo_url,
traceid:s.traceid,
pt:s.pt,
url:s.url,
ticket:s.ticket,
type:s.type,
adid:s.aid,
source:source||"",
is_appmsg:!0
};
}
var E=s.image_url;
require("appmsg/cdn_img_lib.js");
var D=require("biz_common/utils/url/parse.js");
E&&E.isCDN()&&(E=E.replace(/\/0$/,"/640"),E=E.replace(/\/0\?/,"/640?"),s.image_url=D.addParam(E,"wxfrom","50",!0)),
adDatas.ads["pos_"+s.pos_type]={
a_info:s,
adData:d
};
}
var T=function(e){
var a=window.pageYOffset||document.documentElement.scrollTop||document.body.scrollTop;
"undefined"!=typeof e&&(a=e);
10>=a&&(B.style.display="block",DomEvent.off(window,"scroll",T));
},q=function(){
var e=window.pageYOffset||document.documentElement.scrollTop||document.body.scrollTop,a=document.documentElement.clientHeight||window.innerHeight;
O.offsetTop<e+a&&(Class.addClass(document.getElementById("js_ad_area"),"show"),DomEvent.off(window,"scroll",q));
},C=document.getElementById("js_bottom_ad_area"),B=document.getElementById("js_top_ad_area"),O=document.getElementById("js_sponsor_ad_area"),A=adDatas.ads;
for(var S in A)if(0==S.indexOf("pos_")){
var d=A[S],s=!!d&&d.a_info;
if(d&&s)if(0==s.pos_type){
if(C.innerHTML=TMPL.tmpl(a_tpl,s),111==s.pt||112==s.pt||113==s.pt||114==s.pt){
var N=document.getElementsByClassName("js_download_app_card")[0],H=N.offsetWidth,M=Math.floor(H/2.875);
M>0&&(N.style.height=M+"px");
}
}else if(1==s.pos_type){
B.style.display="none",B.innerHTML=TMPL.tmpl(a_tpl,s),DomEvent.on(window,"scroll",T);
var R=0;
window.localStorage&&(R=1*localStorage.getItem(S)||0),window.scrollTo(0,R),T(R);
}else if(3==s.pos_type){
var N=document.createElement("div");
N.appendChild(document.createTextNode(s.image_url)),s.image_url=N.innerHTML,O.innerHTML=TMPL.tmpl(sponsor_a_tpl,s),
O.style.display="block";
var P=O.clientWidth;
document.getElementById("js_main_img").style.height=P/1.77+"px",DomEvent.on(window,"scroll",q),
q(0);
}
}
bindAdOperation();
}
}
function saveCopyArr(e){
for(var a=[],t=0;t<e.length;++t){
var i=e[t],o=typeof i;
i="string"==o?i.htmlDecode():i,"object"==o&&(i="[object Array]"==Object.prototype.toString.call(i)?saveCopyArr(i):saveCopy(i)),
a.push(i);
}
return a;
}
function saveCopy(e){
var a={};
for(var t in e)if(e.hasOwnProperty(t)){
var i=e[t],o=typeof i;
i="string"==o?i.htmlDecode():i,"object"==o&&(i="[object Array]"==Object.prototype.toString.call(i)?saveCopyArr(i):saveCopy(i)),
a[t]=i;
}
return a;
}
function formName(e){
for(var a=[" ","-","(",":",'"',"'","：","（","—","－","“","‘"],t=-1,i=0,o=a.length;o>i;++i){
var p=a[i],n=e.indexOf(p);
-1!=n&&(-1==t||t>n)&&(t=n);
}
return-1!=t&&(e=e.substring(0,t)),e;
}
function formSize(e){
return"number"!=typeof e?e:(e>=1024?(e/=1024,e=e>=1024?(e/1024).toFixed(2)+"MB":e.toFixed(2)+"KB"):e=e.toFixed(2)+"B",
e);
}
function seeAds(){
var adDatas=window.adDatas;
if(adDatas&&adDatas.num>0){
var onScroll=function(){
for(var scrollTop=window.pageYOffset||document.documentElement.scrollTop,i=0;total_pos_type>i;++i)!function(i){
var pos_key="pos_"+i,gdt_a=gdt_as[pos_key];
if(gdt_a=!!gdt_a&&gdt_a[0],gdt_a&&gdt_a.dataset&&gdt_a.dataset.apurl){
var gid=gdt_a.dataset.gid,tid=gdt_a.dataset.tid,apurl=gdt_a.dataset.apurl,is_cpm=1*gdt_a.dataset.is_cpm,ads=adDatas.ads,a_info=ads[pos_key].a_info||{},exp_info=a_info.exp_info||{},exp_id=exp_info.exp_id||"",exp_value=exp_info.exp_value||[],pos_type=adDatas.ads[pos_key].a_info.pos_type,gdt_area=el_gdt_areas[pos_key],offsetTop=gdt_area.offsetTop,adHeight=gdt_a.clientHeight,adOffsetTop=offsetTop+gdt_a.offsetTop;
adDatas.ads[pos_key].ad_engine=0;
try{
exp_value=JSON.stringify(exp_value);
}catch(e){
exp_value="[]";
}
if(-1!=apurl.indexOf("ad.wx.com")&&(adDatas.ads[pos_key].ad_engine=1),function(){
try{
var e=window.__report,a=ping_test_apurl[pos_key],t=new Date,i=t.getHours(),o=ping_test_apurl_random&&i>=12&&18>=i&&0==pos_type;
!a[0]&&o&&scrollTop+innerHeight>offsetTop&&(a[0]=!0,e(81)),!a[1]&&o&&scrollTop+innerHeight>offsetTop+40&&(a[1]=!0,
e(82));
}catch(p){}
}(),!ping_apurl[pos_key]&&(0==pos_type&&scrollTop+innerHeight>offsetTop||1==pos_type&&(10>=scrollTop||scrollTop-10>=offsetTop)||3==pos_type&&scrollTop+innerHeight>offsetTop)){
ping_apurl[pos_key]=!0;
try{
var mmversion=require("biz_wap/utils/mmversion.js"),report_arg="trace_id="+tid+"&product_type="+adDatas.ads[pos_key].a_info.pt+"&logtype=2&url="+encodeURIComponent(location.href)+"&apurl="+encodeURIComponent(apurl);
tid&&mmversion.gtVersion("6.3.22",!0)&&JSAPI.invoke("adDataReport",{
ad_info:report_arg
},function(){});
}catch(e){}
ajax({
url:"/mp/advertisement_report?report_type=1&tid="+tid+"&adver_group_id="+gid+"&apurl="+encodeURIComponent(apurl)+"&__biz="+biz+"&pos_type="+pos_type+"&exp_id="+exp_id+"&exp_value="+exp_value+"&r="+Math.random(),
mayAbort:!0,
success:function(res){
try{
res=eval("("+res+")");
}catch(e){
res={};
}
res&&0!=res.ret?ping_apurl[pos_key]=!1:ping_apurl.pos_0&&ping_apurl.pos_1;
},
error:function(){
(new Image).src=location.protocol+"//mp.weixin.qq.com/mp/jsmonitor?idkey=28307_27_1";
},
async:!0
});
}
var ping_cpm_apurl_obj=ping_cpm_apurl[pos_key];
if(is_cpm&&!ping_cpm_apurl_obj.hasPing){
var rh=.5;
scrollTop+innerHeight>=adOffsetTop+adHeight*rh&&adOffsetTop+adHeight*(1-rh)>=scrollTop?3==pos_type?(ping_cpm_apurl_obj.hasPing=!0,
ajax({
url:"/mp/advertisement_report?report_type=1&tid="+tid+"&adver_group_id="+gid+"&apurl="+encodeURIComponent(apurl+"&viewable=true")+"&__biz="+biz+"&pos_type="+pos_type+"&r="+Math.random(),
mayAbort:!0,
success:function(res){
try{
res=eval("("+res+")");
}catch(e){
res={};
}
res&&0!=res.ret&&(ping_cpm_apurl_obj.hasPing=!1);
},
async:!0
})):ping_cpm_apurl_obj.clk||(ping_cpm_apurl_obj.clk=setTimeout(function(){
ping_cpm_apurl_obj.hasPing=!0,ajax({
url:"/mp/advertisement_report?report_type=1&tid="+tid+"&adver_group_id="+gid+"&apurl="+encodeURIComponent(apurl+"&viewable=true")+"&__biz="+biz+"&pos_type="+pos_type+"&exp_id="+exp_id+"&exp_value="+exp_value+"&r="+Math.random(),
mayAbort:!0,
success:function(res){
try{
res=eval("("+res+")");
}catch(e){
res={};
}
res&&0!=res.ret&&(ping_cpm_apurl_obj.hasPing=!1);
},
async:!0
});
},1001)):3!=pos_type&&ping_cpm_apurl_obj.clk&&(clearTimeout(ping_cpm_apurl_obj.clk),
ping_cpm_apurl_obj.clk=null);
}
}
}(i);
};
DomEvent.on(window,"scroll",onScroll),onScroll();
}
}
function ad_click(e,a,t,i,o,p,n,r,_,d,s,l,c,m,u,f,g){
if(!has_click[o]){
has_click[o]=!0;
var y=document.getElementById("loading_"+o);
y&&(y.style.display="inline");
var v=g.exp_info||{},j=v.exp_id||"",h=v.exp_value||[];
try{
h=JSON.stringify(h);
}catch(w){
h="[]";
}
AdClickReport({
click_pos:1,
report_type:2,
type:e,
exp_id:j,
exp_value:h,
url:encodeURIComponent(a),
tid:o,
rl:encodeURIComponent(t),
__biz:biz,
pos_type:d,
pt:_,
pos_x:c,
pos_y:m,
ad_w:u,
ad_h:f
},function(){
if(has_click[o]=!1,y&&(y.style.display="none"),"5"==e)location.href="/mp/profile?source=from_ad&tousername="+a+"&ticket="+p+"&uin="+uin+"&key="+key+"&__biz="+biz+"&mid="+mid+"&idx="+idx+"&tid="+o;else{
if("105"==_&&l)return void Card.openCardDetail(l.card_id,l.card_ext,l);
if("106"==_&&l)return void(location.href=ParseJs.join(a,{
outer_id:l.outer_id
}));
if(0==a.indexOf("https://itunes.apple.com/")||0==a.indexOf("http://itunes.apple.com/"))return JSAPI.invoke("downloadAppInternal",{
appUrl:a
},function(e){
e.err_msg&&-1!=e.err_msg.indexOf("ok")||(location.href="http://"+location.host+"/mp/ad_redirect?url="+encodeURIComponent(a)+"&ticket="+p+"&uin="+uin);
}),!1;
if(-1==a.indexOf("mp.weixin.qq.com"))a="http://mp.weixinbridge.com/mp/wapredirect?url="+encodeURIComponent(a);else if(-1==a.indexOf("mp.weixin.qq.com/s")&&-1==a.indexOf("mp.weixin.qq.com/mp/appmsg/show")){
var t={
source:4,
tid:o,
idx:idx,
mid:mid,
appuin:biz,
pt:_,
aid:r,
ad_engine:s,
pos_type:d
},i=window.__report;
if(("104"==_||"113"==_||"114"==_)&&l||-1!=a.indexOf("mp.weixin.qq.com/mp/ad_app_info")){
var n="",c="";
l&&(n=l.pkgname&&l.pkgname.replace(/\./g,"_"),c=l.channel_id||""),t={
source:4,
tid:o,
traceid:o,
mid:mid,
idx:idx,
appuin:biz,
pt:_,
channel_id:c,
aid:r,
engine:s,
pos_type:d,
pkgname:n
};
}
a=URL.join(a,t),(0==a.indexOf("http://mp.weixin.qq.com/promotion/")||0==a.indexOf("https://mp.weixin.qq.com/promotion/"))&&(a=URL.join(a,{
traceid:o,
aid:r,
engine:s
})),!r&&i&&i(80,a);
}
location.href=a;
}
});
}
}
function bindAdOperation(){
seeAds();
for(var e=0;total_pos_type>e;++e)!function(e){
var a="pos_"+e,t=el_gdt_areas[a];
if(!t)return!1;
if(!t.getElementsByClassName)return t.style.display="none",!1;
var i=t.getElementsByClassName("js_ad_link")||[],o=adDatas.ads[a];
if(o){
for(var p=o.adData,n=o.a_info,r=n.pos_type,_=o.ad_engine,d=0,s=i.length;s>d;++d)!function(e,a){
var t=i[e],o=t.dataset;
if(o&&3!=n.pos_type){
var p=o.type,d=o.url,s=o.rl,l=o.apurl,c=o.tid,m=o.ticket,u=o.group_id,f=o.aid,g=o.pt;
DomEvent.on(t,"click",function(e){
var t=!!e&&e.target;
if(!t||!t.className||-1==t.className.indexOf("js_ad_btn")&&-1==t.className.indexOf("btn_processor_value")){
if(a){
a.adid=window.adid||a.adid;
var i="&tid="+a.traceid+"&uin="+uin+"&key="+key+"&ticket="+(a.ticket||"")+"&__biz="+biz+"&source="+source+"&scene="+scene+"&appuin="+biz+"&aid="+a.adid+"&ad_engine="+_+"&pos_type="+r+"&r="+Math.random();
"103"==a.pt||"111"==a.pt||"112"==a.pt?report(23,i):("104"==a.pt||"113"==a.pt||"114"==a.pt)&&report(25,i);
}
var o,y,v,j;
return o=position.getX(t,"js_ad_link")+e.offsetX,y=position.getY(t,"js_ad_link")+e.offsetY,
v=document.getElementsByClassName("js_ad_link")[0]&&document.getElementsByClassName("js_ad_link")[0].clientWidth,
j=document.getElementsByClassName("js_ad_link")[0]&&document.getElementsByClassName("js_ad_link")[0].clientHeight,
ad_click(p,d,s,l,c,m,u,f,g,r,_,a,o,y,v,j,n),!1;
}
},!0);
}
}(d,p);
if(p){
p.adid=window.adid||p.adid;
var l=n.exp_info||{},c=l.exp_id||"",m=l.exp_value||[];
try{
m=JSON.stringify(m);
}catch(u){
m="[]";
}
var f="&tid="+p.traceid+"&uin="+uin+"&key="+key+"&ticket="+(p.ticket||"")+"&__biz="+biz+"&source="+source+"&scene="+scene+"&appuin="+biz+"&aid="+p.adid+"&ad_engine="+_+"&pos_type="+r+"&exp_id="+c+"&exp_value="+m+"&r="+Math.random();
if(p.report_param=f,"100"==p.pt||"115"==p.pt){
var g=require("a/profile.js");
return void new g({
btnViewProfile:document.getElementById("js_view_profile_"+r),
btnAddContact:document.getElementById("js_add_contact_"+r),
adData:p,
pos_type:r,
report_param:f,
aid:p.adid,
ad_engine:_
});
}
if("102"==p.pt){
var y=require("a/android.js"),v=15,j=p.pkgname&&p.pkgname.replace(/\./g,"_");
return void new y({
btn:document.getElementById("js_app_action_"+r),
adData:p,
report_param:f,
task_ext_info:[p.adid,p.traceid,j,source,v,_].join("."),
via:[p.traceid,p.adid,j,source,v,_].join(".")
});
}
if("101"==p.pt){
var h=require("a/ios.js");
return void new h({
btn:document.getElementById("js_app_action_"+r),
adData:p,
ticket:p.ticket,
report_param:f
});
}
if("105"==p.pt)return void new Card({
btn:document.getElementById("js_card_action_"+r),
adData:p,
report_param:f,
pos_type:r
});
if("106"==p.pt)return void new MpShop({
btn:document.getElementById("js_shop_action_"+r),
adData:p,
report_param:f,
pos_type:r
});
if("103"==p.pt||"104"==p.pt||"111"==p.pt||"112"==p.pt||"113"==p.pt||"114"==p.pt){
var w=require("a/app_card.js"),v=15,j=p.pkgname&&p.pkgname.replace(/\./g,"_");
return void new w({
btn:document.getElementById("js_appdetail_action_"+r),
js_app_rating:document.getElementById("js_app_rating_"+r),
adData:p,
report_param:f,
pos_type:r,
url_scheme:p.url_scheme,
via:[p.traceid,p.adid,j,source,v,_].join("."),
ticket:p.ticket,
appdetail_params:["&aid="+p.adid,"traceid="+p.traceid,"pkgname="+j,"source="+source,"type="+v,"engine="+_,"appuin="+biz,"pos_type="+r,"ticket="+p.ticket,"scene="+scene].join("&"),
engine:_
});
}
if("108"==p.pt||"109"==p.pt||"110"==p.pt){
var b=require("a/sponsor.js");
new b({
adDetailBtn:document.getElementById("js_ad_detail"),
adMoreBtn:document.getElementById("js_ad_more"),
adAbout:document.getElementById("js_btn_about"),
adImg:document.getElementById("js_main_img"),
adMessage:document.getElementById("js_ad_message"),
adData:p,
a_info:n,
pos_type:r,
report_param:f
});
}
}
}
}(e);
}
var js_bottom_ad_area=document.getElementById("js_bottom_ad_area"),js_top_ad_area=document.getElementById("js_top_ad_area"),js_sponsor_ad_area=document.getElementById("js_sponsor_ad_area"),pos_type=window.pos_type||0,__report=window.__report,total_pos_type=4,el_gdt_areas={
pos_3:js_sponsor_ad_area,
pos_1:js_top_ad_area,
pos_0:js_bottom_ad_area
},gdt_as={
pos_3:js_sponsor_ad_area.getElementsByClassName("js_ad_link"),
pos_1:js_top_ad_area.getElementsByClassName("js_ad_link"),
pos_0:js_bottom_ad_area.getElementsByClassName("js_ad_link")
};
window.adDatas={
ads:{},
num:0
};
var adDatas=window.adDatas,has_click={},DomEvent=require("biz_common/dom/event.js"),URL=require("biz_common/utils/url/parse.js"),AReport=require("a/a_report.js"),AdClickReport=AReport.AdClickReport,ajax=require("biz_wap/utils/ajax.js"),position=require("biz_wap/utils/position.js"),Card=require("a/card.js"),MpShop=require("a/mpshop.js"),JSAPI=require("biz_wap/jsapi/core.js"),ParseJs=require("biz_common/utils/url/parse.js"),TMPL=require("biz_common/tmpl.js"),a_tpl=require("a/a_tpl.html.js"),sponsor_a_tpl=require("a/sponsor_a_tpl.html.js"),Report=require("biz_common/utils/report.js"),Class=require("biz_common/dom/class.js"),LS=require("biz_wap/utils/storage.js"),ping_apurl={
pos_0:!1,
pos_1:!1,
pos_3:!1
},ping_cpm_apurl={
pos_0:{},
pos_1:{},
pos_3:{}
},ping_test_apurl={
pos_0:[],
pos_1:[],
pos_3:[]
},ping_test_apurl_random=Math.random()<.3,innerHeight=window.innerHeight||document.documentElement.clientHeight,ad_engine=0,keyOffset="https:"==top.location.protocol?5:0;
return{
checkNeedAds:checkNeedAds,
afterGetAdData:afterGetAdData
};
});