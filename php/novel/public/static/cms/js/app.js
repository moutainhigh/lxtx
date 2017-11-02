
var LocalCache={NULL_VALUE:'<null>',set:function(key,value,options){if(!key)throw new Error('key is required');if(value===null||value===undefined){value=LocalCache.NULL_VALUE;}
if(value&&typeof value==='object'){value=JSON.stringify(value);}
window.localStorage.setItem(key,value);if(options&&options.ttl){window.localStorage.setItem(key+'_ets',Math.floor(new Date().getTime()/1000)+options.ttl);}else if(options&&options.etag){window.localStorage.setItem(key+'_etag',options.etag);}},get:function(key,options){var entry=LocalCache.getEntry(key,options);return entry?entry.value:null;},getObject:function(key,options){var value=LocalCache.get(key,options);return value?JSON.parse(value):null;},getEntry:function(key,options){if(options&&options.etag){var oldEtag=window.localStorage.getItem(key+'_etag');if(oldEtag!==options.etag){LocalCache.remove(key);return null;}}
var expireTime=window.localStorage.getItem(key+'_ets');if(expireTime&&expireTime<new Date().getTime()/1000){LocalCache.remove(key);return null;}
var value=window.localStorage.getItem(key);var entry=null;if(value){entry={value:value===LocalCache.NULL_VALUE?null:value,readAsObject:function(){return this.value?JSON.parse(this.value):null;}}}
return entry;},remove:function(key){window.localStorage.removeItem(key);window.localStorage.removeItem(key+'_ets');window.localStorage.removeItem(key+'_etag');}};Handlebars.registerHelper('truncate',function(text,len){if(!text||text.length<=len){return text;}
return text.substr(0,len)+'...';});Handlebars.registerHelper('timestamp',function(ts,format){return moment(ts*1000).format(_.isString(format)?format:'YYYY-MM-DD HH:mm:ss');});function getMemberToken(){return Cookies.get('member_token');}

function qs2hash(querystring){return _.chain(querystring).replace('?','').split('&').map(_.ary(_.partial(_.split,_,'='),1)).fromPairs().value();}
var HandlebarTemplates={_cache:{},get:function(name){if(!this._cache[name]){this._cache[name]=Handlebars.compile($('#'+name).html());}
return this._cache[name];}};$.ajaxSetup({beforeSend:function(xhr)
{var token=getMemberToken();if(token){xhr.setRequestHeader('Authorization','Bearer '+token);}}});