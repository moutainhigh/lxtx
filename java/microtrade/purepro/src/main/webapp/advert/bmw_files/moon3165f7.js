function __moonf__(){
window.__wxgspeeds&&(window.__wxgspeeds.moonloadedtime=+new Date),"object"!=typeof JSON&&(window.JSON={
stringify:function(){
return"";
},
parse:function(){
return{};
}
});
var e=function(){
!function(){
var e={},o={},t={};
e.COMBO_UNLOAD=0,e.COMBO_LOADING=1,e.COMBO_LOADED=2;
var n=function(e,t,n){
if(!o[e]){
o[e]=n;
for(var r=3;r--;)try{
moon.setItem(moon.prefix+e,n.toString()),moon.setItem(moon.prefix+e+"_ver",moon_map[e]);
break;
}catch(i){
moon.clear();
}
}
},r=window.alert;
window.__alertList=[],window.alert=function(e){
r(e),window.__alertList.push(e);
};
var i=function(e){
if(!e||!o[e])return null;
var n=o[e];
if("function"==typeof n&&!t[e]){
var a={},s={
exports:a
},c=n(i,a,s,r);
n=o[e]=c||s.exports,t[e]=!0;
}
return n;
};
e.combo_status=e.COMBO_UNLOAD,e.run=function(){
var o=e.run.info,t=o&&o[0],n=o&&o[1];
if(t&&e.combo_status==e.COMBO_LOADED){
var r=i(t);
n&&n(r);
}
},e.use=function(o,t){
e.run.info=[o,t],e.run();
},window.define=n,window.seajs=e;
}(),function(){
if(window.__nonce_str){
var e=document.createElement;
document.createElement=function(o){
var t=e.apply(this,arguments);
return"object"==typeof o&&(o=o.toString()),"string"==typeof o&&"script"==o.toLowerCase()&&t.setAttribute("nonce",window.__nonce_str),
t;
};
}
window.addEventListener&&window.__DEBUGINFO&&Math.random()<.01&&window.addEventListener("load",function(){
var e=document.createElement("script");
e.src=__DEBUGINFO.safe_js,e.type="text/javascript",e.async=!0;
var o=document.head||document.getElementsByTagName("head")[0];
o.appendChild(e);
});
}(),function(){
function e(e){
return"[object Array]"===Object.prototype.toString.call(e);
}
function t(e){
return"[object Object]"===Object.prototype.toString.call(e);
}
function n(e){
var t=e.stack||e.toString()||"";
try{
t=t.replace(/http(s)?:\/\/res\.wx\.qq\.com/g,"");
for(var n=/\/([^.]+)\/js\/(\S+?)\.js(\,|:)?/g;n.test(t);)t=t.replace(n,"$2$3");
}catch(e){
t=e.stack?e.stack:"";
}
var r=[];
for(o in _)_.hasOwnProperty(o)&&r.push(o+":"+_[o]);
return r.push("STK:"+t.replace(/\n/g,"")),r.join("|");
}
function r(e){
if(!e){
var o=window.onerror;
window.onerror=function(){},f=setTimeout(function(){
window.onerror=o,f=null;
},50);
}
}
function i(e,o,t){
var n;
if(window.ActiveXObject)try{
n=new ActiveXObject("Msxml2.XMLHTTP");
}catch(r){
try{
n=new ActiveXObject("Microsoft.XMLHTTP");
}catch(i){
n=!1;
}
}else window.XMLHttpRequest&&(n=new XMLHttpRequest);
n&&(n.open(e,o,!0),n.setRequestHeader("cache-control","no-cache"),n.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"),
n.setRequestHeader("X-Requested-With","XMLHttpRequest"),n.send(t));
}
function a(e){
return function(o,t){
if("string"==typeof o)try{
o=new Function(o);
}catch(n){
throw n;
}
var i=[].slice.call(arguments,2),a=o;
return o=function(){
try{
return a.apply(this,i.length&&i||arguments);
}catch(e){
throw e.stack&&console&&console.error&&console.error("[TryCatch]"+e.stack),d&&window.__moon_report&&(window.__moon_report([{
offset:O,
log:"timeout_error;host:"+top.location.host,
e:e
}]),r(f)),e;
}
},e(o,t);
};
}
function s(e){
return function(o,t,n){
if("undefined"==typeof n)var n=!1;
var i=this,a=t;
return t=function(){
try{
return a.apply(i,arguments);
}catch(e){
throw e.stack&&console&&console.error&&console.error("[TryCatch]"+e.stack),d&&window.__moon_report&&(window.__moon_report([{
offset:v,
log:"listener_error;type:"+o+";host:"+top.location.host,
e:e
}]),r(f)),e;
}
},a.moon_lid=x,j[x]=t,x++,e.call(i,o,t,n);
};
}
function c(e){
return function(o,t,n){
if("undefined"==typeof n)var n=!1;
var r=this;
return t=j[t.moon_lid],e.call(r,o,t,n);
};
}
var d,l,u,w,_,m,f,p=/MicroMessenger/i.test(navigator.userAgent),h=window.define,g=0,v=2,y=4,O=9,b=10;
if(window.__initCatch=function(e){
d=e.idkey,l=e.startKey||0,u=e.limit,w=e.badjsId,_=e.reportOpt||"",m=e.extInfo||{},
m.rate=m.rate||.5;
},window.__moon_report=function(o){
var r=.5;
if(m&&m.rate&&(r=m.rate),!(!/mp\.weixin\.qq\.com/.test(location.href)&&!/payapp\.weixin\.qq\.com/.test(location.href)||Math.random()>r||!p||top!=window&&!/mp\.weixin\.qq\.com/.test(top.location.href))&&(t(o)&&(o=[o]),
e(o)&&""!=d)){
var a="",s=[],c=[],_=[],f=[];
"number"!=typeof u&&(u=1/0);
for(var h=0;h<o.length;h++){
var g=o[h]||{};
if(!(g.offset>u||"number"!=typeof g.offset||g.offset==y&&m&&m.network_rate&&Math.random()>=m.network_rate)){
var v=1/0==u?l:l+g.offset;
s[h]="[moon]"+d+"_"+v+";"+g.log+";"+n(g.e||{})||"",c[h]=v,_[h]=1;
}
}
for(var O=0;O<c.length;O++)f[O]=d+"_"+c[O]+"_"+_[O],a=a+"&log"+O+"="+s[O];
if(f.length>0){
i("POST",location.protocol+"//mp.weixin.qq.com/mp/jsmonitor?","idkey="+f.join(";")+"&lc="+s.length+a);
var r=1;
if(m&&m.badjs_rate&&(r=m.badjs_rate),w&&Math.random()<r){
a=a.replace(/uin\:(.)*\|biz\:(.)*\|mid\:(.)*\|idx\:(.)*\|sn\:(.)*\|/,"");
var b=new Image,j="https://badjs.weixinbridge.com/badjs?id="+w+"&level=4&from="+encodeURIComponent(location.host)+"&msg="+encodeURIComponent(a);
b.src=j.slice(0,1024);
}
}
}
},window.setTimeout=a(window.setTimeout),window.setInterval=a(window.setInterval),
Math.random()<.01&&window.Document&&window.HTMLElement){
var j={},x=0;
Document.prototype.addEventListener=s(Document.prototype.addEventListener),Document.prototype.removeEventListener=c(Document.prototype.removeEventListener),
HTMLElement.prototype.addEventListener=s(HTMLElement.prototype.addEventListener),
HTMLElement.prototype.removeEventListener=c(HTMLElement.prototype.removeEventListener);
}
var E=window.navigator.userAgent;
if((/ip(hone|ad|od)/i.test(E)||/android/i.test(E))&&!/windows phone/i.test(E)&&window.localStorage&&window.localStorage.setItem){
var D=window.localStorage.setItem,I=0;
window.localStorage.setItem=function(e,o){
if(!(I>=10))try{
D.call(window.localStorage,e,o);
}catch(t){
t.stack&&console&&console.error&&console.error("[TryCatch]"+t.stack),window.__moon_report([{
offset:b,
log:"localstorage_error;"+t.toString(),
e:t
}]),I++,I>=3&&window.moon&&window.moon.clear&&moon.clear();
}
};
}
window.seajs&&h&&(window.define=function(){
for(var e,o=[],t=arguments&&arguments[0],n=0,i=arguments.length;i>n;n++){
var a=e=arguments[n];
"function"==typeof e&&(e=function(){
try{
return a.apply(this,arguments);
}catch(e){
throw"string"==typeof t&&console.error("[TryCatch][DefineeErr]id:"+t),e.stack&&console&&console.error&&console.error("[TryCatch]"+e.stack),
d&&window.__moon_report&&(window.__moon_report([{
offset:g,
log:"define_error;id:"+t+";",
e:e
}]),r(f)),e;
}
},e.toString=function(e){
return function(){
return e.toString();
};
}(arguments[n])),o.push(e);
}
return h.apply(this,o);
});
}(),function(e){
function o(e,o,t){
return window.__DEBUGINFO?(window.__DEBUGINFO.res_list||(window.__DEBUGINFO.res_list=[]),
window.__DEBUGINFO.res_list[e]?(window.__DEBUGINFO.res_list[e][o]=t,!0):!1):!1;
}
function t(e){
var o=new TextEncoder("utf-8").encode(e),t=crypto.subtle||crypto.webkitSubtle;
return t.digest("SHA-256",o).then(function(e){
return n(e);
});
}
function n(e){
for(var o=[],t=new DataView(e),n=0;n<t.byteLength;n+=4){
var r=t.getUint32(n),i=r.toString(16),a="00000000",s=(a+i).slice(-a.length);
o.push(s);
}
return o.join("");
}
function r(e,o,t){
if("object"==typeof e){
var n=Object.prototype.toString.call(e).replace(/^\[object (.+)\]$/,"$1");
if(t=t||e,"Array"==n){
for(var r=0,a=e.length;a>r;++r)if(o.call(t,e[r],r,e)===!1)return;
}else{
if("Object"!==n&&i!=e)throw"unsupport type";
if(i==e){
for(var r=e.length-1;r>=0;r--){
var s=i.key(r),c=i.getItem(s);
if(o.call(t,c,s,e)===!1)return;
}
return;
}
for(var r in e)if(e.hasOwnProperty(r)&&o.call(t,e[r],r,e)===!1)return;
}
}
}
var i=e.localStorage,a=document.head||document.getElementsByTagName("head")[0],s=1,c=2,d=window.__moon_host||"res.wx.qq.com",l=new RegExp("^(http(s)?:)?//"+d),u=0,w={
prefix:"__MOON__",
loaded:[],
unload:[],
clearSample:Math.random()<u,
hit_num:0,
mod_num:0,
version:1003,
init:function(){
w.loaded=[],w.unload=[];
var o,n,a;
if(i){
var s="_moon_ver_key_",c=i.getItem(s);
c!=w.version&&(w.clear(),i.setItem(s,w.version));
}
if(-1!=location.search.indexOf("no_moon1=1")&&w.clear(),i){
var d=1*i.getItem(w.prefix+"clean_time"),u=+new Date;
if(u-d>=1296e6){
w.clear();
try{
!!i&&i.setItem(w.prefix+"clean_time",+new Date);
}catch(_){}
}
}
r(moon_map,function(r,s){
if(n=w.prefix+s,a=!!r&&r.replace(l,""),o=!!i&&i.getItem(n),version=!!i&&(i.getItem(n+"_ver")||"").replace(l,""),
w.mod_num++,w.clearSample||!o||a!=version)w.unload.push(a.replace(l,""));else{
if("https:"==location.protocol&&window.moon_hash_map&&window.moon_hash_map[s]&&window.crypto)try{
t(o).then(function(e){
window.moon_hash_map[s]!=e&&console.log(s);
});
}catch(c){}
try{
var d="//# sourceURL="+s+"\n//@ sourceURL="+s;
e.eval.call(e,'define("'+s+'",[],'+o+")"+d),w.hit_num++;
}catch(c){
w.unload.push(a.replace(l,""));
}
}
}),w.load(w.genUrl());
},
genUrl:function(){
var e=w.unload;
if(!e||e.length<=0)return[];
var o,t,n="",r=[],i={},a=-1!=location.search.indexOf("no_moon2=1"),s="//"+d;
-1!=location.href.indexOf("moon_debug2=1")&&(s="//mp.weixin.qq.com");
for(var c=0,l=e.length;l>c;++c)/^\/(.*?)\//.test(e[c]),RegExp.$1&&(t=RegExp.$1,n=i[t],
n?(o=n+","+e[c],o.length>1e3||a?(r.push(n+"?v="+w.version),n=location.protocol+s+e[c],
i[t]=n):(n=o,i[t]=n)):(n=location.protocol+s+e[c],i[t]=n));
for(var u in i)i.hasOwnProperty(u)&&r.push(i[u]);
return r;
},
load:function(e){
if(window.__wxgspeeds&&(window.__wxgspeeds.mod_num=w.mod_num,window.__wxgspeeds.hit_num=w.hit_num),
!e||e.length<=0)return seajs.combo_status=seajs.COMBO_LOADED,seajs.run(),void(console.debug&&console.debug("[moon] load js complete, all in cache, cost time : 0ms, total count : "+w.mod_num+", hit num: "+w.hit_num));
seajs.combo_status=seajs.COMBO_LOADING;
var o=0,t=+new Date;
window.__wxgspeeds&&(window.__wxgspeeds.combo_times=[],window.__wxgspeeds.combo_times.push(t)),
r(e,function(n){
w.request(n,c,function(){
if(window.__wxgspeeds&&window.__wxgspeeds.combo_times.push(+new Date),o++,o==e.length){
var n=+new Date-t;
window.__wxgspeeds&&(window.__wxgspeeds.mod_downloadtime=n),seajs.combo_status=seajs.COMBO_LOADED,
seajs.run(),console.debug&&console.debug("[moon] load js complete, url num : "+e.length+", total mod count : "+w.mod_num+", hit num: "+w.hit_num+", use time : "+n+"ms");
}
});
});
},
request:function(e,t,n){
if(e){
t=t||0;
var r=-1;
window.__DEBUGINFO&&(__DEBUGINFO.res_list||(__DEBUGINFO.res_list=[]),__DEBUGINFO.res_list.push({
type:"js",
status:"pendding",
start:+new Date,
end:0,
url:e
}),r=__DEBUGINFO.res_list.length-1);
var i=document.createElement("script");
i.src=e,i.type="text/javascript",i.async=!0,i.onerror=function(i){
if(o(r,"status","error"),o(r,"end",+new Date),t>=0)w.request(e,t,n);else if(window.__moon_report){
var a=new Error(i);
window.__moon_report([{
offset:s,
log:"load_script_error: "+e,
e:a
}]);
}
},"undefined"!=typeof moon_crossorigin&&moon_crossorigin&&i.setAttribute("crossorigin",!0),
i.onload=i.onreadystatechange=function(){
o(r,"status","loaded"),o(r,"end",+new Date),!i||i.readyState&&!/loaded|complete/.test(i.readyState)||(o(r,"status","200"),
i.onload=i.onreadystatechange=null,"function"==typeof n&&n());
},t--,a.appendChild(i);
}
},
setItem:function(e,o){
!!i&&i.setItem(e,o);
},
clear:function(){
i&&(r(i,function(e,o){
~o.indexOf(w.prefix)&&i.removeItem(o);
}),console.debug&&console.debug("[moon] clear"));
}
};
seajs&&seajs.use&&"string"==typeof window.__moon_mainjs&&seajs.use(window.__moon_mainjs),
window.moon=w;
}(window),window.moon.init();
};
e(),!!window.__moon_initcallback&&window.__moon_initcallback();
}
__moonf__();