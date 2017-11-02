//SectionA start 
//a lot of encoding/decoding related functions
!function(a)
{
	function b(a,b,c)
	{return a>=b&&c>=a}
	function c(a,b)
	{return Math.floor(a/b)}
	function d(a)
	{var b=0,c=this;
		c.get=function(){return b>=a.length?N:Number(a[b])},
		c.offset=function(c){if(b+=c,0>b)throw new Error("Seeking past start of the buffer");if(b>a.length)throw new Error("Seeking past EOF")},
		c.match=function(c){if(c.length>b+a.length)return!1;var d;for(d=0;d<c.length;d+=1)if(Number(a[b+d])!==c[d])return!1;return!0}
	}
	function e(a)
	{
		var b=0;
		this.emit=function(){var c,d=N;for(c=0;c<arguments.length;++c)d=Number(arguments[c]),a[b++]=d;return d}}
	function f(a)
	{
		var c=0,
		d=function(){
			for(var c=[],d=0,e=a.length;d<a.length;)
			{
				var f=a.charCodeAt(d);
				if(b(f,55296,57343))
					if(b(f,56320,57343))
						c.push(65533);
					else if(d===e-1)
						c.push(65533);
					else{
						var g=a.charCodeAt(d+1);
						if(b(g,56320,57343)){
							var h=1023&f,i=1023&g;
							d+=1,c.push(65536+(h<<10)+i)
						} else
						 c.push(65533)
					}	
				else 
					c.push(f);
					d+=1
			}
			return c}();
		this.offset=function(a){
			if(c+=a,0>c)
				throw new Error("Seeking past start of the buffer");
			if(c>d.length)
				throw new Error("Seeking past EOF")},
				this.get=function(){
					return c>=d.length?O:d[c]
				}
	}
	function g()
	{
		var a="";
		this.string=function(){return a},
		this.emit=function(b){65535>=b?a+=String.fromCharCode(b):(b-=65536,a+=String.fromCharCode(55296+(1023&b>>10)),a+=String.fromCharCode(56320+(1023&b)))}
	}
	
	function h(a,b)
	{
		if(a)
			throw new Error("EncodingError");
		return b||65533
	}

	function i()
	{
		throw new Error("EncodingError")
	}
	function j(a)
	{
		if(a=String(a).trim().toLowerCase(),Object.prototype.hasOwnProperty.call(R,a))return R[a];
		throw new Error("EncodingError: Unknown encoding: "+a)
	}
	function k(a,b)
	{
		return(b||[])[a]||null
	}
	function l(a,b)
	{
		var c=b.indexOf(a);
		return-1===c?null:c
	}
	function m(a)
	{
		if(a>39419&&189e3>a||a>1237575)
			return null;
		var b,c=0,d=0,e=S.gb18030;
		for(b=0;b<e.length;++b)
		{
			var f=e[b];
			if(!(f[0]<=a))
				break;
			c=f[0],d=f[1]
		}
		return d+a-c
	}
	function n(a)
	{
		var b,c=0,d=0,e=S.gb18030;
		for(b=0;b<e.length;++b){
			var f=e[b];
			if(!(f[1]<=a))
				break;
			c=f[1],d=f[0]
		}
		return d+a-c
	}
	function o(a)
	{
		var c=a.fatal,d=0,e=0,f=0,g=0;
		this.decode=function(a)
		{
			var i=a.get();
			if(i===N)
				return 0!==e?(e=0,h(c)):O;
			if(a.offset(1),0===e)
			{
				if(b(i,0,127))
					return i;
				if(b(i,194,223))
					e=1,g=128,d=i-192;
				else if(b(i,224,239))
					e=2,g=2048,d=i-224;
				else{
					if(!b(i,240,244))
						return h(c);
					e=3,g=65536,d=i-240
				}
			return d*=Math.pow(64,e),null}

			if(!b(i,128,191))
				return d=0,e=0,f=0,g=0,a.offset(-1),h(c);
			if(f+=1,d+=(i-128)*Math.pow(64,e-f),f!==e)
				return null;
			var j=d,k=g;
			return d=0,e=0,f=0,g=0,b(j,k,1114111)&&!b(j,55296,57343)?j:h(c)}
	}
	function p(a)
	{
		a.fatal,
		this.encode=function(a,d)
		{
			var e=d.get();
			if(e===O)
				return N;
			if(d.offset(1),b(e,55296,57343))
				return i(e);
			if(b(e,0,127))
				return a.emit(e);

			var f,g;
			b(e,128,2047)?(f=1,g=192):b(e,2048,65535)?(f=2,g=224):b(e,65536,1114111)&&(f=3,g=240);
			for(var h=a.emit(c(e,Math.pow(64,f))+g);f>0;)
			{
				var j=c(e,Math.pow(64,f-1));
				h=a.emit(128+j%64),f-=1}
				return h
			}
	}
	function q(a,c)
	{
		var d=c.fatal;
		this.decode=function(c)
		{
			var e=c.get();
			if(e===N)
				return O;
			if(c.offset(1),b(e,0,127))
				return e;
			var f=a[e-128];
			return null===f?h(d):f}
	}
	function r(a,c)
	{
		c.fatal,
		this.encode=function(c,d){
			var e=d.get();
			if(e===O)return N;
			if(d.offset(1),b(e,0,127))
				return c.emit(e);
			var f=l(e,a);
			return null===f&&i(e),c.emit(f+128)}
	}
	function s(a,c)
	{
		var d=c.fatal,e=0,f=0,g=0;
		this.decode=function(c){
			var i=c.get();
			if(i===N&&0===e&&0===f&&0===g)
				return O;
			i!==N||0===e&&0===f&&0===g||(e=0,f=0,g=0,h(d)),c.offset(1);

			var j;
			if(0!==g)
				return j=null,b(i,48,57)&&(j=m(10*(126*(10*(e-129)+(f-48))+(g-129))+i-48)),e=0,f=0,g=0,null===j?(c.offset(-3),h(d)):j;if(0!==f)return b(i,129,254)?(g=i,null):(c.offset(-2),e=0,f=0,h(d));if(0!==e){if(b(i,48,57)&&a)return f=i,null;var l=e,n=null;e=0;var o=127>i?64:65;return(b(i,64,126)||b(i,128,254))&&(n=190*(l-129)+(i-o)),j=null===n?null:k(n,S.gbk),null===n&&c.offset(-1),null===j?h(d):j}return b(i,0,127)?i:128===i?8364:b(i,129,254)?(e=i,null):h(d)}}

	function t(a,d){d.fatal,this.encode=function(d,e){var f=e.get();if(f===O)return N;if(e.offset(1),b(f,0,127))return d.emit(f);var g=l(f,S.gbk);if(null!==g){var h=c(g,190)+129,j=g%190,k=63>j?64:65;return d.emit(h,j+k)}if(null===g&&!a)return i(f);g=n(f);var m=c(c(c(g,10),126),10);g-=10*126*10*m;var o=c(c(g,10),126);g-=126*10*o;var p=c(g,10),q=g-10*p;return d.emit(m+129,o+48,p+129,q+48)}}
	function u(a){var c=a.fatal,d=!1,e=0;this.decode=function(a){var f=a.get();if(f===N&&0===e)return O;if(f===N&&0!==e)return e=0,h(c);if(a.offset(1),126===e)return e=0,123===f?(d=!0,null):125===f?(d=!1,null):126===f?126:10===f?null:(a.offset(-1),h(c));if(0!==e){var g=e;e=0;var i=null;return b(f,33,126)&&(i=k(190*(g-1)+(f+63),S.gbk)),10===f&&(d=!1),null===i?h(c):i}return 126===f?(e=126,null):d?b(f,32,127)?(e=f,null):(10===f&&(d=!1),h(c)):b(f,0,127)?f:h(c)}}
	function v(a){a.fatal;var d=!1;this.encode=function(a,e){var f=e.get();if(f===O)return N;if(e.offset(1),b(f,0,127)&&d)return e.offset(-1),d=!1,a.emit(126,125);if(126===f)return a.emit(126,126);if(b(f,0,127))return a.emit(f);if(!d)return e.offset(-1),d=!0,a.emit(126,123);var g=l(f,S.gbk);if(null===g)return i(f);var h=c(g,190)+1,j=g%190-63;return b(h,33,126)&&b(j,33,126)?a.emit(h,j):i(f)}}
	function w(a){var c=a.fatal,d=0,e=null;this.decode=function(a){if(null!==e){var f=e;return e=null,f}var g=a.get();if(g===N&&0===d)return O;if(g===N&&0!==d)return d=0,h(c);if(a.offset(1),0!==d){var i=d,j=null;d=0;var l=127>g?64:98;if((b(g,64,126)||b(g,161,254))&&(j=157*(i-129)+(g-l)),1133===j)return e=772,202;if(1135===j)return e=780,202;if(1164===j)return e=772,234;if(1166===j)return e=780,234;var m=null===j?null:k(j,S.big5);return null===j&&a.offset(-1),null===m?h(c):m}return b(g,0,127)?g:b(g,129,254)?(d=g,null):h(c)}}
	function x(a){a.fatal,this.encode=function(a,d){var e=d.get();if(e===O)return N;if(d.offset(1),b(e,0,127))return a.emit(e);var f=l(e,S.big5);if(null===f)return i(e);var g=c(f,157)+129,h=f%157,j=63>h?64:98;return a.emit(g,h+j)}}
	function y(a){var c=a.fatal,d=0,e=0;this.decode=function(a){var f=a.get();if(f===N)return 0===d&&0===e?O:(d=0,e=0,h(c));a.offset(1);var g,i;return 0!==e?(g=e,e=0,i=null,b(g,161,254)&&b(f,161,254)&&(i=k(94*(g-161)+f-161,S.jis0212)),b(f,161,254)||a.offset(-1),null===i?h(c):i):142===d&&b(f,161,223)?(d=0,65377+f-161):143===d&&b(f,161,254)?(d=0,e=f,null):0!==d?(g=d,d=0,i=null,b(g,161,254)&&b(f,161,254)&&(i=k(94*(g-161)+f-161,S.jis0208)),b(f,161,254)||a.offset(-1),null===i?h(c):i):b(f,0,127)?f:142===f||143===f||b(f,161,254)?(d=f,null):h(c)}}
	function z(a){a.fatal,this.encode=function(a,d){var e=d.get();if(e===O)return N;if(d.offset(1),b(e,0,127))return a.emit(e);if(165===e)return a.emit(92);if(8254===e)return a.emit(126);if(b(e,65377,65439))return a.emit(142,e-65377+161);var f=l(e,S.jis0208);if(null===f)return i(e);var g=c(f,94)+161,h=f%94+161;return a.emit(g,h)}}
	function A(a){var c=a.fatal,d={ASCII:0,escape_start:1,escape_middle:2,escape_final:3,lead:4,trail:5,Katakana:6},e=d.ASCII,f=!1,g=0;this.decode=function(a){var i=a.get();switch(i!==N&&a.offset(1),e){default:case d.ASCII:return 27===i?(e=d.escape_start,null):b(i,0,127)?i:i===N?O:h(c);case d.escape_start:return 36===i||40===i?(g=i,e=d.escape_middle,null):(i!==N&&a.offset(-1),e=d.ASCII,h(c));case d.escape_middle:var j=g;return g=0,36!==j||64!==i&&66!==i?36===j&&40===i?(e=d.escape_final,null):40!==j||66!==i&&74!==i?40===j&&73===i?(e=d.Katakana,null):(i===N?a.offset(-1):a.offset(-2),e=d.ASCII,h(c)):(e=d.ASCII,null):(f=!1,e=d.lead,null);case d.escape_final:return 68===i?(f=!0,e=d.lead,null):(i===N?a.offset(-2):a.offset(-3),e=d.ASCII,h(c));case d.lead:return 10===i?(e=d.ASCII,h(c,10)):27===i?(e=d.escape_start,null):i===N?O:(g=i,e=d.trail,null);case d.trail:if(e=d.lead,i===N)return h(c);var l=null,m=94*(g-33)+i-33;return b(g,33,126)&&b(i,33,126)&&(l=f===!1?k(m,S.jis0208):k(m,S.jis0212)),null===l?h(c):l;case d.Katakana:return 27===i?(e=d.escape_start,null):b(i,33,95)?65377+i-33:i===N?O:h(c)}}}
	function B(a){a.fatal;var d={ASCII:0,lead:1,Katakana:2},e=d.ASCII;this.encode=function(a,f){var g=f.get();if(g===O)return N;if(f.offset(1),(b(g,0,127)||165===g||8254===g)&&e!==d.ASCII)return f.offset(-1),e=d.ASCII,a.emit(27,40,66);if(b(g,0,127))return a.emit(g);if(165===g)return a.emit(92);if(8254===g)return a.emit(126);if(b(g,65377,65439)&&e!==d.Katakana)return f.offset(-1),e=d.Katakana,a.emit(27,40,73);if(b(g,65377,65439))return a.emit(g-65377-33);if(e!==d.lead)return f.offset(-1),e=d.lead,a.emit(27,36,66);var h=l(g,S.jis0208);if(null===h)return i(g);var j=c(h,94)+33,k=h%94+33;return a.emit(j,k)}}function C(a){var c=a.fatal,d=0;this.decode=function(a){var e=a.get();if(e===N&&0===d)return O;if(e===N&&0!==d)return d=0,h(c);if(a.offset(1),0!==d){var f=d;if(d=0,b(e,64,126)||b(e,128,252)){var g=127>e?64:65,i=160>f?129:193,j=k(188*(f-i)+e-g,S.jis0208);return null===j?h(c):j}return a.offset(-1),h(c)}return b(e,0,128)?e:b(e,161,223)?65377+e-161:b(e,129,159)||b(e,224,252)?(d=e,null):h(c)}}function D(a){a.fatal,this.encode=function(a,d){var e=d.get();if(e===O)return N;if(d.offset(1),b(e,0,128))return a.emit(e);if(165===e)return a.emit(92);if(8254===e)return a.emit(126);if(b(e,65377,65439))return a.emit(e-65377+161);var f=l(e,S.jis0208);if(null===f)return i(e);var g=c(f,188),h=31>g?129:193,j=f%188,k=63>j?64:65;return a.emit(g+h,j+k)}}function E(a){var c=a.fatal,d=0;this.decode=function(a){var e=a.get();if(e===N&&0===d)return O;if(e===N&&0!==d)return d=0,h(c);if(a.offset(1),0!==d){var f=d,g=null;if(d=0,b(f,129,198)){var i=178*(f-129);b(e,65,90)?g=i+e-65:b(e,97,122)?g=i+26+e-97:b(e,129,254)&&(g=i+26+26+e-129)}b(f,199,253)&&b(e,161,254)&&(g=12460+94*(f-199)+(e-161));var j=null===g?null:k(g,S["euc-kr"]);return null===g&&a.offset(-1),null===j?h(c):j}return b(e,0,127)?e:b(e,129,253)?(d=e,null):h(c)}}function F(a){a.fatal,this.encode=function(a,d){var e=d.get();if(e===O)return N;if(d.offset(1),b(e,0,127))return a.emit(e);var f=l(e,S["euc-kr"]);if(null===f)return i(e);var g,h;if(12460>f){g=c(f,178)+129,h=f%178;var j=26>h?65:52>h?71:77;return a.emit(g,h+j)}return f-=12460,g=c(f,94)+199,h=f%94+161,a.emit(g,h)}}function G(a){var c=a.fatal,d={ASCII:0,escape_start:1,escape_middle:2,escape_end:3,lead:4,trail:5},e=d.ASCII,f=0;this.decode=function(a){var g=a.get();switch(g!==N&&a.offset(1),e){default:case d.ASCII:return 14===g?(e=d.lead,null):15===g?null:27===g?(e=d.escape_start,null):b(g,0,127)?g:g===N?O:h(c);case d.escape_start:return 36===g?(e=d.escape_middle,null):(g!==N&&a.offset(-1),e=d.ASCII,h(c));case d.escape_middle:return 41===g?(e=d.escape_end,null):(g===N?a.offset(-1):a.offset(-2),e=d.ASCII,h(c));case d.escape_end:return 67===g?(e=d.ASCII,null):(g===N?a.offset(-2):a.offset(-3),e=d.ASCII,h(c));case d.lead:return 10===g?(e=d.ASCII,h(c,10)):14===g?null:15===g?(e=d.ASCII,null):g===N?O:(f=g,e=d.trail,null);case d.trail:if(e=d.lead,g===N)return h(c);var i=null;return b(f,33,70)&&b(g,33,126)?i=k(178*(f-1)+26+26+g-1,S["euc-kr"]):b(f,71,126)&&b(g,33,126)&&(i=k(12460+94*(f-71)+(g-33),S["euc-kr"])),null!==i?i:h(c)}}}function H(a){a.fatal;var d={ASCII:0,lead:1},e=!1,f=d.ASCII;this.encode=function(a,g){var h=g.get();if(h===O)return N;if(e||(e=!0,a.emit(27,36,41,67)),g.offset(1),b(h,0,127)&&f!==d.ASCII)return g.offset(-1),f=d.ASCII,a.emit(15);if(b(h,0,127))return a.emit(h);if(f!==d.lead)return g.offset(-1),f=d.lead,a.emit(14);var j=l(h,S["euc-kr"]);if(null===j)return i(h);var k,m;return 12460>j?(k=c(j,178)+1,m=j%178-26-26+1,b(k,33,70)&&b(m,33,126)?a.emit(k,m):i(h)):(j-=12460,k=c(j,94)+71,m=j%94+33,b(k,71,126)&&b(m,33,126)?a.emit(k,m):i(h))}}function I(a,c){var d=c.fatal,e=null,f=null;this.decode=function(c){var g=c.get();if(g===N&&null===e&&null===f)return O;if(g===N&&(null!==e||null!==f))return h(d);if(c.offset(1),null===e)return e=g,null;var i;if(i=a?(e<<8)+g:(g<<8)+e,e=null,null!==f){var j=f;return f=null,b(i,56320,57343)?65536+1024*(j-55296)+(i-56320):(c.offset(-2),h(d))}return b(i,55296,56319)?(f=i,null):b(i,56320,57343)?h(d):i}}
	function J(a,d){d.fatal,
		this.encode=function(d,e)
		{
			function f(b){var c=b>>8,e=255&b;return a?d.emit(c,e):d.emit(e,c)}
			var g=e.get();
			if(g===O)return N;
			if(e.offset(1),b(g,55296,57343)&&i(g),65535>=g)
				return f(g);
			var h=c(g-65536,1024)+55296,j=(g-65536)%1024+56320;
				return f(h),f(j)
		}
	}
	function K(a,b){
			return b.match([255,254])?(b.offset(2),"utf-16"):b.match([254,255])?(b.offset(2),"utf-16be"):b.match([239,187,191])?(b.offset(3),"utf-8"):a
	}
	function L(b,c){return this&&this!==a?(b=b?String(b):T,c=Object(c),this._encoding=j(b),this._streaming=!1,this._encoder=null,this._options={fatal:Boolean(c.fatal)},Object.defineProperty?Object.defineProperty(this,"encoding",{get:function(){return this._encoding.name}}):this.encoding=this._encoding.name,this):new L(b,c)}
	function M(b,c){
		return this&&this!==a?(b=b?String(b):T,c=Object(c),this._encoding=j(b),this._streaming=!1,this._decoder=null,this._options={fatal:Boolean(c.fatal)},Object.defineProperty?Object.defineProperty(this,"encoding",{get:function(){return this._encoding.name}}):this.encoding=this._encoding.name,this):new M(b,c)}if("function"!=typeof a.TextEncoder){var N=-1,O=-1,P=[{encodings:[{labels:["unicode-1-1-utf-8","utf-8","utf8"],name:"utf-8"}],heading:"The Encoding"},{encodings:[{labels:["cp864","ibm864"],name:"ibm864"},{labels:["cp866","ibm866"],name:"ibm866"},{labels:["csisolatin2","iso-8859-2","iso-ir-101","iso8859-2","iso_8859-2","l2","latin2"],name:"iso-8859-2"},{labels:["csisolatin3","iso-8859-3","iso_8859-3","iso-ir-109","l3","latin3"],name:"iso-8859-3"},{labels:["csisolatin4","iso-8859-4","iso_8859-4","iso-ir-110","l4","latin4"],name:"iso-8859-4"},{labels:["csisolatincyrillic","cyrillic","iso-8859-5","iso_8859-5","iso-ir-144"],name:"iso-8859-5"},{labels:["arabic","csisolatinarabic","ecma-114","iso-8859-6","iso_8859-6","iso-ir-127"],name:"iso-8859-6"},{labels:["csisolatingreek","ecma-118","elot_928","greek","greek8","iso-8859-7","iso_8859-7","iso-ir-126"],name:"iso-8859-7"},{labels:["csisolatinhebrew","hebrew","iso-8859-8","iso-8859-8-i","iso-ir-138","iso_8859-8","visual"],name:"iso-8859-8"},{labels:["csisolatin6","iso-8859-10","iso-ir-157","iso8859-10","l6","latin6"],name:"iso-8859-10"},{labels:["iso-8859-13"],name:"iso-8859-13"},{labels:["iso-8859-14","iso8859-14"],name:"iso-8859-14"},{labels:["iso-8859-15","iso_8859-15"],name:"iso-8859-15"},{labels:["iso-8859-16"],name:"iso-8859-16"},{labels:["koi8-r","koi8_r"],name:"koi8-r"},{labels:["koi8-u"],name:"koi8-u"},{labels:["csmacintosh","mac","macintosh","x-mac-roman"],name:"macintosh"},{labels:["iso-8859-11","tis-620","windows-874"],name:"windows-874"},{labels:["windows-1250","x-cp1250"],name:"windows-1250"},{labels:["windows-1251","x-cp1251"],name:"windows-1251"},{labels:["ascii","ansi_x3.4-1968","csisolatin1","iso-8859-1","iso8859-1","iso_8859-1","l1","latin1","us-ascii","windows-1252"],name:"windows-1252"},{labels:["cp1253","windows-1253"],name:"windows-1253"},{labels:["csisolatin5","iso-8859-9","iso-ir-148","l5","latin5","windows-1254"],name:"windows-1254"},{labels:["cp1255","windows-1255"],name:"windows-1255"},{labels:["cp1256","windows-1256"],name:"windows-1256"},{labels:["windows-1257"],name:"windows-1257"},{labels:["cp1258","windows-1258"],name:"windows-1258"},{labels:["x-mac-cyrillic","x-mac-ukrainian"],name:"x-mac-cyrillic"}],heading:"Legacy single-byte encodings"},{encodings:[{labels:["chinese","csgb2312","csiso58gb231280","gb2312","gbk","gb_2312","gb_2312-80","iso-ir-58","x-gbk"],name:"gbk"},{labels:["gb18030"],name:"gb18030"},{labels:["hz-gb-2312"],name:"hz-gb-2312"}],heading:"Legacy multi-byte Chinese (simplified) encodings"},{encodings:[{labels:["big5","big5-hkscs","cn-big5","csbig5","x-x-big5"],name:"big5"}],heading:"Legacy multi-byte Chinese (traditional) encodings"},{encodings:[{labels:["cseucpkdfmtjapanese","euc-jp","x-euc-jp"],name:"euc-jp"},{labels:["csiso2022jp","iso-2022-jp"],name:"iso-2022-jp"},{labels:["csshiftjis","ms_kanji","shift-jis","shift_jis","sjis","windows-31j","x-sjis"],name:"shift_jis"}],heading:"Legacy multi-byte Japanese encodings"},{encodings:[{labels:["cseuckr","csksc56011987","euc-kr","iso-ir-149","korean","ks_c_5601-1987","ks_c_5601-1989","ksc5601","ksc_5601","windows-949"],name:"euc-kr"},{labels:["csiso2022kr","iso-2022-kr"],name:"iso-2022-kr"}],heading:"Legacy multi-byte Korean encodings"},{encodings:[{labels:["utf-16","utf-16le"],name:"utf-16"},{labels:["utf-16be"],name:"utf-16be"}],heading:"Legacy utf-16 encodings"}],Q={},R={};P.forEach(function(a){a.encodings.forEach(function(a){Q[a.name]=a,a.labels.forEach(function(b){R[b]=a})})});var S=a["encoding-indexes"]||{};Q["utf-8"].getEncoder=function(a){return new p(a)},Q["utf-8"].getDecoder=function(a){return new o(a)},function(){["ibm864","ibm866","iso-8859-2","iso-8859-3","iso-8859-4","iso-8859-5","iso-8859-6","iso-8859-7","iso-8859-8","iso-8859-10","iso-8859-13","iso-8859-14","iso-8859-15","iso-8859-16","koi8-r","koi8-u","macintosh","windows-874","windows-1250","windows-1251","windows-1252","windows-1253","windows-1254","windows-1255","windows-1256","windows-1257","windows-1258","x-mac-cyrillic"].forEach(function(a){var b=Q[a],c=S[a];b.getDecoder=function(a){return new q(c,a)},b.getEncoder=function(a){return new r(c,a)}})}(),Q.gbk.getEncoder=function(a){return new t(!1,a)},Q.gbk.getDecoder=function(a){return new s(!1,a)},Q.gb18030.getEncoder=function(a){return new t(!0,a)},Q.gb18030.getDecoder=function(a){return new s(!0,a)},Q["hz-gb-2312"].getEncoder=function(a){return new v(a)},Q["hz-gb-2312"].getDecoder=function(a){return new u(a)},Q.big5.getEncoder=function(a){return new x(a)},Q.big5.getDecoder=function(a){return new w(a)},Q["euc-jp"].getEncoder=function(a){return new z(a)},Q["euc-jp"].getDecoder=function(a){return new y(a)},Q["iso-2022-jp"].getEncoder=function(a){return new B(a)},Q["iso-2022-jp"].getDecoder=function(a){return new A(a)},Q.shift_jis.getEncoder=function(a){return new D(a)},Q.shift_jis.getDecoder=function(a){return new C(a)},Q["euc-kr"].getEncoder=function(a){return new F(a)},Q["euc-kr"].getDecoder=function(a){return new E(a)},Q["iso-2022-kr"].getEncoder=function(a){return new H(a)},Q["iso-2022-kr"].getDecoder=function(a){return new G(a)},Q["utf-16"].getEncoder=function(a){return new J(!1,a)},Q["utf-16"].getDecoder=function(a){return new I(!1,a)},Q["utf-16be"].getEncoder=function(a){return new J(!0,a)},Q["utf-16be"].getDecoder=function(a){return new I(!0,a)};var T="utf-8";L.prototype={encode:function(a,b){a=a?String(a):"",b=Object(b),this._streaming||(this._encoder=this._encoding.getEncoder(this._options)),this._streaming=Boolean(b.stream);for(var c=[],d=new e(c),g=new f(a);g.get()!==O;)this._encoder.encode(d,g);if(!this._streaming){var h;do h=this._encoder.encode(d,g);while(h!==N);this._encoder=null}return new Uint8Array(c)}},M.prototype={decode:function(a,b){if(a&&!("buffer"in a&&"byteOffset"in a&&"byteLength"in a))throw new TypeError("Expected ArrayBufferView");a||(a=new Uint8Array(0)),b=Object(b),this._streaming||(this._decoder=this._encoding.getDecoder(this._options)),this._streaming=Boolean(b.stream);var c=new Uint8Array(a.buffer,a.byteOffset,a.byteLength),e=new d(c),f=K(this._encoding.name,e);if(j(f)!==this._encoding)throw new Error("BOM mismatch");for(var h,i=new g;e.get()!==N;)h=this._decoder.decode(e),null!==h&&h!==O&&i.emit(h);if(!this._streaming){do h=this._decoder.decode(e),null!==h&&h!==O&&i.emit(h);while(h!==O);this._decoder=null}return i.string()}},a.TextEncoder=L,a.TextDecoder=M}
	}(this),

//SectionA end 


// SectionB start
//Zlib related functions
	function(a){
		if(a.ArrayBuffer)
			{
				Zlib_CompressionMethod={DEFLATE:8,RESERVED:15};
				var b=!0;
				Zlib_Util_stringToByteArray=function(a){var b,c=a.split(""),d=c.length;for(b=0;d>b;b++)c[b]=(255&c[b].charCodeAt(0))>>>0;return c},
				Zlib_Huffman_buildHuffmanTable=function(a){var c,d,e,f,g,h,i,j,k,l=a.length,m=0,n=Number.POSITIVE_INFINITY,o=l;for(j=0;o>j;++j)a[j]>m&&(m=a[j]),a[j]<n&&(n=a[j]);for(c=1<<m,d=new(b?Uint32Array:Array)(c),e=1,f=0,g=2;m>=e;){for(j=0;l>j;++j)if(a[j]===e){for(h=0,i=f,k=0;e>k;++k)h=h<<1|1&i,i>>=1;for(k=h;c>k;k+=g)d[k]=e<<16|j;++f}++e,f<<=1,g<<=1}return[d,m,n]},
				Zlib_Adler32=function(a){return"string"==typeof a&&(a=Zlib_Util_stringToByteArray(a)),Zlib_Adler32_update(1,a)},
				Zlib_Adler32_update=function(a,b){for(var c,d=65535&a,e=65535&a>>>16,f=b.length,g=0;f>0;){c=f>Zlib_Adler32_OptimizationParameter?Zlib_Adler32_OptimizationParameter:f,f-=c;do d+=b[g++],e+=d;while(--c);d%=65521,e%=65521}return(e<<16|d)>>>0},Zlib_Adler32_OptimizationParameter=1024;var c=Zlib_Huffman_buildHuffmanTable;
				Zlib_RawInflate=function(a,c){var d=this,e=32768;switch(d.buffer,d.blocks=[],d.bufferSize=e,d.totalpos=0,d.ip=0,d.bitsbuf=0,d.bitsbuflen=0,d.input=b?new Uint8Array(a):a,d.output,d.op,d.bfinal=!1,d.bufferType=Zlib_RawInflate_BufferType.ADAPTIVE,d.resize=!1,(c||!(c={}))&&(c.index&&(d.ip=c.index),c.bufferSize&&(d.bufferSize=c.bufferSize),c.bufferType&&(d.bufferType=c.bufferType),c.resize&&(d.resize=c.resize)),d.bufferType){case Zlib_RawInflate_BufferType.BLOCK:d.op=Zlib_RawInflate_MaxCopyLength,d.output=new(b?Uint8Array:Array)(Zlib_RawInflate_MaxBackwardLength+d.bufferSize+Zlib_RawInflate_MaxCopyLength);break;case Zlib_RawInflate_BufferType.ADAPTIVE:d.op=0,d.output=new(b?Uint8Array:Array)(d.bufferSize),d.expandBuffer=d.expandBufferAdaptive,d.concatBuffer=d.concatBufferDynamic,d.decodeHuffman=d.decodeHuffmanAdaptive;break;default:throw new Error("invalid inflate mode")}},Zlib_RawInflate_BufferType={BLOCK:0,ADAPTIVE:1},
				Zlib_RawInflate.prototype.decompress=function(){for(;!this.bfinal;)this.parseBlock();return this.concatBuffer()},Zlib_RawInflate_MaxBackwardLength=32768,Zlib_RawInflate_MaxCopyLength=258,
				Zlib_RawInflate_Order=function(a){return b?new Uint16Array(a):a}([16,17,18,0,8,7,9,6,10,5,11,4,12,3,13,2,14,1,15]),
				Zlib_RawInflate_LengthCodeTable=function(a){return b?new Uint16Array(a):a}([3,4,5,6,7,8,9,10,11,13,15,17,19,23,27,31,35,43,51,59,67,83,99,115,131,163,195,227,258,258,258]),
				Zlib_RawInflate_LengthExtraTable=function(a){return b?new Uint8Array(a):a}([0,0,0,0,0,0,0,0,1,1,1,1,2,2,2,2,3,3,3,3,4,4,4,4,5,5,5,5,0,0,0]),
				Zlib_RawInflate_DistCodeTable=function(a){return b?new Uint16Array(a):a}([1,2,3,4,5,7,9,13,17,25,33,49,65,97,129,193,257,385,513,769,1025,1537,2049,3073,4097,6145,8193,12289,16385,24577]),
				Zlib_RawInflate_DistExtraTable=function(a){return b?new Uint8Array(a):a}([0,0,0,0,1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9,10,10,11,11,12,12,13,13]),
				Zlib_RawInflate_FixedLiteralLengthTable=function(a){return a}(function(){var a,d,e=new(b?Uint8Array:Array)(288);for(a=0,d=e.length;d>a;++a)e[a]=143>=a?8:255>=a?9:279>=a?7:8;return c(e)}()),
				Zlib_RawInflate_FixedDistanceTable=function(a){return a}(function(){var a,d,e=new(b?Uint8Array:Array)(30);for(a=0,d=e.length;d>a;++a)e[a]=5;return c(e)}()),
				Zlib_RawInflate.prototype.parseBlock=function(){var a=this.readBits(3);switch(1&a&&(this.bfinal=!0),a>>>=1){case 0:this.parseUncompressedBlock();break;case 1:this.parseFixedHuffmanBlock();break;case 2:this.parseDynamicHuffmanBlock();break;default:throw new Error("unknown BTYPE: "+a)}},
				Zlib_RawInflate.prototype.readBits=function(a){for(var b,c=this.bitsbuf,d=this.bitsbuflen,e=this.input,f=this.ip;a>d;){if(b=e[f++],void 0===b)throw new Error("input buffer is broken");c|=b<<d,d+=8}return b=c&(1<<a)-1,c>>>=a,d-=a,this.bitsbuf=c,this.bitsbuflen=d,this.ip=f,b},
				Zlib_RawInflate.prototype.readCodeByTable=function(a){for(var b,c,d,e=this.bitsbuf,f=this.bitsbuflen,g=this.input,h=this.ip,i=a[0],j=a[1];j>f&&(b=g[h++],void 0!==b);)e|=b<<f,f+=8;return c=i[e&(1<<j)-1],d=c>>>16,this.bitsbuf=e>>d,this.bitsbuflen=f-d,this.ip=h,65535&c},
				Zlib_RawInflate.prototype.parseUncompressedBlock=function(){var a,c,d,e,f=this.input,g=this.ip,h=this.output,i=this.op,j=h.length;if(this.bitsbuf=0,this.bitsbuflen=0,a=f[g++],void 0===a)throw new Error("invalid uncompressed block header: LEN (first byte)");if(c=a,a=f[g++],void 0===a)throw new Error("invalid uncompressed block header: LEN (second byte)");if(c|=a<<8,a=f[g++],void 0===a)throw new Error("invalid uncompressed block header: NLEN (first byte)");if(d=a,a=f[g++],void 0===a)throw new Error("invalid uncompressed block header: NLEN (second byte)");if(d|=a<<8,c===~d)throw new Error("invalid uncompressed block header: length verify");if(g+c>f.length)throw new Error("input buffer is broken");switch(this.bufferType){case Zlib_RawInflate.BufferType.BLOCK:for(;i+c>h.length;){if(e=j-i,c-=e,b)h.set(f.subarray(g,g+e),i),i+=e,g+=e;else for(;e--;)h[i++]=f[g++];this.op=i,h=this.expandBuffer(),i=this.op}break;case Zlib_RawInflate.BufferType.ADAPTIVE:for(;i+c>h.length;)h=this.expandBuffer({fixRatio:2});break;default:throw new Error("invalid inflate mode")}if(b)h.set(f.subarray(g,g+c),i),i+=c,g+=c;else for(;c--;)h[i++]=f[g++];this.ip=g,this.op=i,this.output=h},
				Zlib_RawInflate.prototype.parseFixedHuffmanBlock=function(){this.decodeHuffman(Zlib_RawInflate_FixedLiteralLengthTable,Zlib_RawInflate_FixedDistanceTable)},
				Zlib_RawInflate.prototype.parseDynamicHuffmanBlock=function(){function a(a,b,c){var d,e,f,g;for(g=0;a>g;)switch(d=this.readCodeByTable(b)){case 16:for(f=3+this.readBits(2);f--;)c[g++]=e;break;case 17:for(f=3+this.readBits(3);f--;)c[g++]=0;e=0;break;case 18:for(f=11+this.readBits(7);f--;)c[g++]=0;e=0;break;default:c[g++]=d,e=d}return c}var d,e,f,g,h=this.readBits(5)+257,i=this.readBits(5)+1,j=this.readBits(4)+4,k=new(b?Uint8Array:Array)(Zlib_RawInflate_Order.length);for(g=0;j>g;++g)k[Zlib_RawInflate_Order[g]]=this.readBits(3);d=c(k),e=new(b?Uint8Array:Array)(h),f=new(b?Uint8Array:Array)(i),this.decodeHuffman(c(a.call(this,h,d,e)),c(a.call(this,i,d,f)))},
				Zlib_RawInflate.prototype.decodeHuffman=function(a,b){var c=this.output,d=this.op;this.currentLitlenTable=a;for(var e,f,g,h,i=c.length-Zlib_RawInflate_MaxCopyLength;256!==(e=this.readCodeByTable(a));)if(256>e)d>=i&&(this.op=d,c=this.expandBuffer(),d=this.op),c[d++]=e;else for(f=e-257,h=Zlib_RawInflate_LengthCodeTable[f],Zlib_RawInflate_LengthExtraTable[f]>0&&(h+=this.readBits(Zlib_RawInflate_LengthExtraTable[f])),e=this.readCodeByTable(b),g=Zlib_RawInflate_DistCodeTable[e],Zlib_RawInflate_DistExtraTable[e]>0&&(g+=this.readBits(Zlib_RawInflate_DistExtraTable[e])),d>=i&&(this.op=d,c=this.expandBuffer(),d=this.op);h--;)c[d]=c[d++-g];for(;this.bitsbuflen>=8;)this.bitsbuflen-=8,this.ip--;this.op=d},
				Zlib_RawInflate.prototype.decodeHuffmanAdaptive=function(a,b){var c=this.output,d=this.op;this.currentLitlenTable=a;for(var e,f,g,h,i=c.length;256!==(e=this.readCodeByTable(a));)if(256>e)d>=i&&(c=this.expandBuffer(),i=c.length),c[d++]=e;else for(f=e-257,h=Zlib_RawInflate_LengthCodeTable[f],Zlib_RawInflate_LengthExtraTable[f]>0&&(h+=this.readBits(Zlib_RawInflate_LengthExtraTable[f])),e=this.readCodeByTable(b),g=Zlib_RawInflate_DistCodeTable[e],Zlib_RawInflate_DistExtraTable[e]>0&&(g+=this.readBits(Zlib_RawInflate_DistExtraTable[e])),d+h>i&&(c=this.expandBuffer(),i=c.length);h--;)c[d]=c[d++-g];for(;this.bitsbuflen>=8;)this.bitsbuflen-=8,this.ip--;this.op=d},
				Zlib_RawInflate.prototype.expandBuffer=function(){var a,c,d=new(b?Uint8Array:Array)(this.op-Zlib_RawInflate_MaxBackwardLength),e=this.op-Zlib_RawInflate_MaxBackwardLength,f=this.output;if(b)d.set(f.subarray(Zlib_RawInflate_MaxBackwardLength,d.length));else for(a=0,c=d.length;c>a;++a)d[a]=f[a+Zlib_RawInflate_MaxBackwardLength];if(this.blocks.push(d),this.totalpos+=d.length,b)f.set(f.subarray(e,e+Zlib_RawInflate_MaxBackwardLength));else for(a=0;Zlib_RawInflate_MaxBackwardLength>a;++a)f[a]=f[e+a];return this.op=Zlib_RawInflate_MaxBackwardLength,f},
				Zlib_RawInflate.prototype.expandBufferAdaptive=function(a){var c,d,e,f,g=0|this.input.length/this.ip+1,h=this.input,i=this.output;return a&&("number"==typeof a.fixRatio&&(g=a.fixRatio),"number"==typeof a.addRatio&&(g+=a.addRatio)),2>g?(d=(h.length-this.ip)/this.currentLitlenTable[2],f=0|258*(d/2),e=f<i.length?i.length+f:i.length<<1):e=i.length*g,b?(c=new Uint8Array(e),c.set(i)):c=i,this.output=c,this.output},
				Zlib_RawInflate.prototype.concatBuffer=function(){var a,c,d,e,f,g=0,h=this.totalpos+(this.op-Zlib_RawInflate_MaxBackwardLength),i=this.output,j=this.blocks,k=new(b?Uint8Array:Array)(h);if(0===j.length)return b?this.output.subarray(Zlib_RawInflate_MaxBackwardLength,this.op):this.output.slice(Zlib_RawInflate_MaxBackwardLength,this.op);for(c=0,d=j.length;d>c;++c)for(a=j[c],e=0,f=a.length;f>e;++e)k[g++]=a[e];for(c=Zlib_RawInflate_MaxBackwardLength,d=this.op;d>c;++c)k[g++]=i[c];return this.blocks=[],this.buffer=k,this.buffer},
				Zlib_RawInflate.prototype.concatBufferDynamic=function(){var a,c=this,d=this.op;return b?c.resize?(a=new Uint8Array(d),a.set(c.output.subarray(0,d))):a=c.output.subarray(0,d):(c.output.length>d&&(c.output.length=d),a=c.output),c.buffer=a,c.buffer},
				Zlib_Inflate=function(a,b){var c,d,e=this;switch(e.input=a,e.ip=0,e.rawinflate,e.verify,(b||!(b={}))&&(b.index&&(e.ip=b.index),b.verify&&(e.verify=b.verify)),c=a[e.ip++],d=a[e.ip++],15&c){case Zlib_CompressionMethod.DEFLATE:this.method=Zlib_CompressionMethod.DEFLATE;break;default:throw new Error("unsupported compression method")}if(0!==((c<<8)+d)%31)throw new Error("invalid fcheck flag:"+((c<<8)+d)%31);if(32&d)throw new Error("fdict flag is not supported");e.rawinflate=new Zlib_RawInflate(a,{index:e.ip,bufferSize:b.bufferSize,bufferType:b.bufferType,resize:b.resize})},Zlib_Inflate_BufferType=Zlib_RawInflate_BufferType,
				Zlib_Inflate.prototype.decompress=function(){var a,b,c=this,d=c.input;if(a=c.rawinflate.decompress(),c.ip=c.rawinflate.ip,c.verify&&(b=(d[c.ip++]<<24|d[c.ip++]<<16|d[c.ip++]<<8|d[c.ip++])>>>0,b!==Zlib_Adler32(a)))throw new Error("invalid adler-32 checksum");
		return a}
		}
	}(this),
// SectionB end
		
// SectionC end
//Message header
	function(a){
		var b=a.Header=function()
			{
				var a=this;
				a.Ver=3,a.Len=0,a.OrgLen=0,a.Type=1,a.ID=0,a.ComCode=0,a.EnCryCode=0,a.Binary=0,a.Encode=0,
			a.StrSplit="\r\n\r\n",
			a.toHeadText=function(){
				var b=[];
				for(var c in a)
					"function"!=typeof a[c]&&"StrSplit"!=c&&b.push(a[c]);
				return b.join(",")
			},
			a.toHeadString=function(){var b=a.toHeadText()+a.StrSplit;return b},
			a.FromHeadString=function(b){if(b.indexOf(",")>-1){var c=b.split(","),d=0;for(var e in a)"function"!=typeof a[e]&&"StrSplit"!=e&&(a[e]=c[d],d+=1)}
			return a}
		};
		a.HqHeader=function(){var a=new b;return a.Session=0,a.Stime=0,a}
	}(this),
// SectionC end

// SectionD start
	function(a){
		var b=a.ReqType={CRTNone:0,CRTCkLive:1,CRTAuth:2,PRTLogout:3,CRTReqMtInfo:50,CRTReqSbInfo:51,CRTreqSbList:52,PRTReqSbReport:53,CRTReqCodeList:54,PRTReqSomeSymReport:55,PRTReqSubMtList:56,PRTReqDealInfo:57,PRTReqConditionCode:58,PRTReqTradeTime:59,CRTReqSbKLine:150,CRTRequestSymbolTick:151,CRTReqTrend:152,CRTReqRealTimePrice:153,PRTReLevel2:154,PRTeqFullPriceUseTradeCode:155,PRTReqMinutesKLine:156,PRTReqOddLot:157,PRTReqFinance:158,PRTReqFreeText:159,PRTReqLinkSym:160,CRTRequestAddPush:200,CRTRequestDeletePush:201,CRTRequestUpdatePush:202,SERPushPrice:250,SERPushTick:251,PSERPushLevel2:252,PSERPPushOddLot:253,SERErrorMsg:300,PSerPushUpdateSym:350,PCRTTickUser:351,PRTUploadUserData:400,PRTUploadOrgExData:401,PRTReqUserData:402,PRTReqOrgExData:403,PRTReqBlockCfg:62,PRTReqBlockList:63,
				News:1e4};
		a.RSType={PRSTBegin:0,PRSTRisePercent:1,PRSTRiseValue:2,PRSTPriceNew:3,PRSTPriceAverage:4,PRSTSettle:5,PRSTCurVolume:6,PRSTPriceBuy:7,PRSTPriceSell:8,PRSTVolumeTotal:9,PRSTPriceOpen:10,PRSTPricePrevClose:11,PRSTPriceHigh:12,PRSTPriceLow:13,PRSTAmountTotal:14,PRSTAmplitude:15,PRSTVolRatio:16,PRSTBidRatio:17,PRSTBidDifference:18,PRSTIn:19,PRSTOut:20,PRSTInOutRatio:21,PRSTPricePrevAvg:22,PRSTPriceHold:23,PRSTCurHold:24,PRSTPE:25,PRSTTradeRate:26,PRSTMoneyIn:27,PRSTRiseRate:28,PRSTEnd:29};
		var c=a.NewsInType={ERTCurNewsTitle:10,ERTCurNewsContent:11,ERTExNewsTitle:12,ERTExNewsContent:13,ERTInfoTitle:14,ERTInfoContent:15,ERTErrMsg:300};
		a.ArrayBuffer&&(ArrayBuffer.prototype.zUnion=function(a){var b=null;if(0==this.byteLength)b=a;else{b=new ArrayBuffer(this.byteLength+a.byteLength);var c=new Uint8Array(b);c.set(new Uint8Array(this),0),c.set(new Uint8Array(a),this.byteLength)}return b},
		ArrayBuffer.prototype.zCopy=function(a,b){var c=new Uint8Array(this);return c.set(new Uint8Array(b),a),c.buffer},
		ArrayBuffer.prototype.zToNewSize=function(a){var b=new ArrayBuffer(a);return b=b.zCopy(0,this)},
		ArrayBuffer.prototype.Oslice=function(a,b){var c=null;if("undefined"!=typeof this.slice)c=this.slice(a,b);else if(c=new ArrayBuffer(b-a),b!=a){var d=new Uint8Array(c);d.set(new Uint8Array(this,a,b-a),0)}return c});

var d=function(){};
d.prototype.BytesLength=function(){return{Uint8ArrayLength:1,Int8ArrayLength:1,Uint16ArrayLength:2,Int16ArrayLength:2,Uint32ArrayLength:4,Int32ArrayLength:4,Float32ArrayLength:4,Float64ArrayLength:8}}(),
d.prototype.base64ArrayBuffer=function(a)
{
	for(var b,c,d,e,f,g="",h="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",i=new Uint8Array(a),j=i.byteLength,k=j%3,l=j-k,m=0;l>m;m+=3)
		f=i[m]<<16|i[m+1]<<8|i[m+2],b=(16515072&f)>>18,c=(258048&f)>>12,d=(4032&f)>>6,e=63&f,g+=h[b]+h[c]+h[d]+h[e];
	return 1==k?(f=i[l],b=(252&f)>>2,c=(3&f)<<4,g+=h[b]+h[c]+"=="):2==k&&(f=i[l]<<8|i[l+1],b=(64512&f)>>10,c=(1008&f)>>4,d=(15&f)<<2,g+=h[b]+h[c]+h[d]+"="),g
},
d.prototype.SdecodeArrayBuffer=function(a){var b,c,d,e,f,g="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",h=.75*a.length,i=a.length,j=0;"="===a[a.length-1]&&(h--,"="===a[a.length-2]&&h--);var k=new ArrayBuffer(h),l=new Uint8Array(k);for(b=0;i>b;b+=4)c=g.indexOf(a[b]),d=g.indexOf(a[b+1]),e=g.indexOf(a[b+2]),f=g.indexOf(a[b+3]),l[j++]=c<<2|d>>4,l[j++]=(15&d)<<4|e>>2,l[j++]=(3&e)<<6|63&f;return k},
d.prototype.CusDataView=function(a){
	this.olittleEndian=new Int8Array(new Int16Array([1]).buffer)[0]>0,
	this.buffer=a,
	this.setUint8=function(a,b){var c=new Uint8Array(this.buffer);c[a]=b},
	this.setUint16=function(a,b,c){var d=new ArrayBuffer(2),e=new Uint16Array(d);e[0]=b,d=this.ExlittleEndian(d,c),this.SetBuffValue(a,d)},
	this.setUint32=function(a,b,c){var d=new ArrayBuffer(4),e=new Uint32Array(d);e[0]=b,d=this.ExlittleEndian(d,c),this.SetBuffValue(a,d)},
	this.setInt8=function(a,b){var c=Int8Array(this.buffer);c[a]=b},
	this.setInt16=function(a,b,c){var d=new ArrayBuffer(2),e=new Int16Array(d);e[0]=b,d=this.ExlittleEndian(d,c),this.SetBuffValue(a,d)},
	this.setInt32=function(a,b,c){var d=new ArrayBuffer(4),e=new Int32Array(d);e[0]=b,this.ExlittleEndian(a,4,c),this.SetBuffValue(a,d)},
	this.setFloat32=function(a,b,c){var d=new ArrayBuffer(4),e=new Float32Array(this.buffer);e[0]=b,this.ExlittleEndian(d,c),this.SetBuffValue(a,d)},
	this.setFloat64=function(a,b,c){var d=new ArrayBuffer(8),e=new Float64Array(this.buffer);e[0]=b,d=this.ExlittleEndian(d,c),this.SetBuffValue(a,d)},
	this.ExlittleEndian=function(a,b){if(this.olittleEndian!=b)for(var c=new Uint8Array(a),d=c.length/2,e=0;d>e;e++){var f=c[e];c[e]=c[c.length-e-1],c[c.length-e-1]=f}return a},
	this.SetBuffValue=function(a,b){var c=new Uint8Array(b),d=new Uint8Array(this.buffer,a,b.byteLength);d.set(c,0)},
	this.GetBuffValue=function(a,b){var c=new ArrayBuffer(b),d=new Uint8Array(c),e=new Uint8Array(this.buffer,a,b);return d.set(e,0),c},
	this.getUint8=function(a){var b=new Uint8Array(this.buffer,a,1);return b[0]},
	this.getUint16=function(a,b){var c=this.GetBuffValue(a,2);c=this.ExlittleEndian(c,b);var d=new Uint16Array(c,0,1);return d[0]},
	this.getUint32=function(a,b){var c=this.GetBuffValue(a,4);c=this.ExlittleEndian(c,b);var d=new Uint32Array(c,0,1);return d[0]},
	this.getInt8=function(a){var b=Int8Array(this.buffer,a,1);return b[0]},
	this.getInt16=function(a,b){var c=this.GetBuffValue(a,2);c=this.ExlittleEndian(c,b);var d=new Int16Array(c,0,1);return d[0]},
	this.getInt32=function(a,b){var c=this.GetBuffValue(a,4);c=this.ExlittleEndian(c,b);var d=new Int32Array(c,0,1);return d[0]},
	this.getFloat32=function(a,b){var c=this.GetBuffValue(a,4);c=this.ExlittleEndian(c,b);var d=new Float32Array(c,0,1);return d[0]},
	this.getFloat64=function(a,b){var c=this.GetBuffValue(a,8);c=this.ExlittleEndian(c,b);var d=new Float64Array(c,0,1);return d[0]}},

d.prototype.GetRealDataView=function(a){var b=null;return b="undefined"==typeof DataView?new this.CusDataView(a):new DataView(a)},
d.prototype.DecodeCA=function(a){var b=[21,233,242,122,137,144,115,37],c=0,d=a.length;for(c=0;d>c;++c){var e=a[c];a[c]=240&e<<4|15&e>>4}var f=b.length;for(c=0;d>c;c+=f)for(var g=0;f>g;++g){var h=c+g;d>h&&(a[h]^=b[g])}return a},
window.NetCommon=new d,

window.XmlHelper=function(a,b){for(var c=[],d=$(a).find(b),e=0;e<d.length;e++){var f={},g=d[e].attributes;for(var h in g)g[h].value&&g[h].name&&(f[g[h].name]=g[h].value);c.push(f)}return c};
var e=function(a){
	switch(a.Head.Type<<0){
		case b.CRTCkLive:
		case b.CRTAuth:
		case b.PRTLogout:
		case b.CRTReqMtInfo:
		case b.CRTReqSbInfo:
		case b.CRTreqSbList:
		case b.PRTReqSbReport:
		case b.PRTReqSubMtList:
		case b.PRTReqMinutesKLine:
		case b.CRTReqTrend:
		case b.CRTReqSbKLine:
		case b.CRTReqRealTimePrice:
		case b.CRTReqCodeList:
		case b.PRTReqSpreadCodeTable:
		case b.PRTReqFinance:
		case b.PRTReqAHSym:
		case b.PRTReqTradeTime:
		case b.PRTReqConditionCode:
			this.SucTaskList["SucCallBack"+a.Head.ID]&&this.SucTaskList["SucCallBack"+a.Head.ID](a),
			this.DeleteCallBack(a);
			break;

		case b.SERPushPrice:
			"function"==typeof window.PushDataSuccessCallback&&window.PushDataSuccessCallback(a);
			break;
		case b.News:
			var d=JSON.parse(a.Data);
			switch(d.Type){
				case c.ERTCurNewsTitle:
				case c.ERTInfoTitle:
				case c.ERTExNewsTitle:
				case c.ERTCurNewsContent:
				case c.ERTExNewsContent:
				case c.ERTExNewsTitle:
				case c.ERTInfoContent:
					this.SucTaskList["SucCallBack"+a.Head.ID]&&this.SucTaskList["SucCallBack"+a.Head.ID](a),this.DeleteCallBack(a);
					break;
				case c.ERTErrMsg:
					this.ErrTaskList["ErrCallBack"+a.Head.ID]&&this.ErrTaskList["ErrCallBack"+a.Head.ID](a),
					this.DeleteCallBack(a)}
					break;
				case b.SERErrorMsg:
					this.ErrTaskList["ErrCallBack"+a.Head.ID]&&this.ErrTaskList["ErrCallBack"+a.Head.ID](a),this.DeleteCallBack(a)
		}
		this.ClearTimeout(a.Head.ID)},

//tool for send http requests
f=window.Net=function(a,b){this.ResText="",this.Server=a,this.Port=b,this.SucTaskList={},this.ErrTaskList={},this.ReqID=0};
f.prototype.HttpTimeOut=function(){window.Prompt.alert("请求超时！")},f.prototype.HttpError=function(){window.Prompt.alert("报错了onerror")},
f.prototype.HttpIsConnected=function(){return"boolean"==typeof this.IsConnected?this.IsConnected:!1},
f.prototype.SendReq=function(a){this.ReqID++,a.Head.ID=this.ReqID,a.SucCallBack&&(this.SucTaskList["SucCallBack"+this.ReqID]=a.SucCallBack),a.ErrCallBack&&(this.ErrTaskList["ErrCallBack"+this.ReqID]=a.ErrCallBack);var b=a.toHttpText();this.GloabalXhr=new XMLHttpRequest;var c="http://"+this.Server+":"+this.Port+"?"+new Date;this.GloabalXhr.open("POST",c,!0),this.GloabalXhr.timeout=1e4,this.GloabalXhr.withCredentials=!1,this.GloabalXhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded;"),this.GloabalXhr.responseType="text";var d=this;this.GloabalXhr.onload=function(){200==this.status&&(d.GloabalXhr=null,d.ResText=this.responseText,d.ProcessRecData())},this.GloabalXhr.ontimeout=this.HttpTimeOut,this.GloabalXhr.onerror=this.HttpError,this.GloabalXhr.send(b)},
f.prototype.ProcessRecData=function(){},
f.prototype.DeleteCallBack=function(a){this.SucTaskList["SucCallBack"+a.Head.ID]=null,delete this.SucTaskList["SucCallBack"+a.Head.ID],this.ErrTaskList["SucCallBack"+a.Head.ID]=null,delete this.ErrTaskList["SucCallBack"+a.Head.ID]},
f.prototype.Callback=e;

//definition of g 
var g=window.Socket=function(a){var b=this;b.ResText="",b.ServerAddr=a,b.SucTaskList={},b.ErrTaskList={},b.ReqID=0,b.CommandList=[],b.GloableComFlag=!1,b.IsConnected=!1,b.StockWs=null,b.FirstConnect=!0,b.ConnectionSocket()};
g.prototype.ProcessAllCommand=function()
{
	var a=this;
	alert("command list size:"+a.CommandList.length);
	if(a.IsConnected&&a.CommandList.length>0)
		for(;a.CommandList.length>0;)
			req=a.CommandList.shift(),a.SendReq(req)
},
g.prototype.SendReq=function(a){
	var b=this;
	if(b.IsConnected)
	{
		b.ReqID++,
		a.Head.Comcode=0,
		a.Head.ID=b.ReqID;
		var c=a.toHttpText(),
		d=new TextEncoder("utf-8").encode(c).buffer;
		
		console.log("send message to server:"+c);
		b.GloableComFlag&&(d=window.NetCommon.base64ArrayBuffer(d))&&(console.log("some encoding work.")),
		a.SucCallBack&&(b.SucTaskList["SucCallBack"+b.ReqID]=a.SucCallBack),
		a.ErrCallBack&&(b.ErrTaskList["ErrCallBack"+b.ReqID]=a.ErrCallBack),
		b.SetTimeout(b.ReqID,a);
		b.StockWs.send(d);
	} else {
		for(var e in b.CommandList)
		if(JSON.stringify(a)===JSON.stringify(b.CommandList[e]))return;b.CommandList.push(a)
	}},
g.prototype.SocketClose=function(a){HqCollections.ReConnection(a)},
g.prototype.SocketError=function(){},
g.prototype.SocketOpen=function()
{
	console.log("in SocketOpen.");
	this.ProcessAllCommand()
},
g.prototype.IsConnected=function(){return"boolean"==typeof this.IsConnection?this.IsConnection:!1},
g.prototype.ConnectionSocket=function(){
	console.log("server addr is:" + this.ServerAddr);
	var a=this, b=this.StockWs=new WebSocket("Ws://"+this.ServerAddr); 
	a.ResText="",a.ResBuffer=new ArrayBuffer(0),
	b.onopen=function(){
		b.binaryType="arraybuffer",a.IsConnected=!0,a.FirstConnect=!1,console.log("开始连接"),
		a.SocketOpen.call(a)
	},
	b.onclose=function(){a.StockWs=null,a.ResText="",a.ResBuffer=new ArrayBuffer(0),a.IsConnected=!1,a.FirstConnect?(a.FirstConnect=!1,a.SocketClose(!0)):a.SocketClose(),console.log("关闭连接")},
	b.onerror=function(){a.ResText="",a.ResBuffer=new ArrayBuffer(0),a.SocketError(),console.log("发生错误")},
	b.onmessage=function(b){
		if(a.GloableComFlag="string"==typeof b.data,!a.IsFirst)return a.IsFirst=!0,void 0;
		var c=null;c=a.GloableComFlag?window.NetCommon.SdecodeArrayBuffer(b.data):b.data,a.ResBuffer=a.ResBuffer.zUnion(c),c=null,a.ProcessRecData()
	}
},

g.prototype.ProcessRecData=function()
{
	var a=this,b=new HqRes,c=window.NetCommon.GetRealDataView(a.ResBuffer);
	a.ResText=new TextDecoder("utf-8").decode(c);
	console.log(a.ServerAddr);
	console.log("response from server:" + a.ResText);
	/*if (a.ResText.indexOf("Symbol") > 0 && a.ResText.indexOf("LastSettle") < 0) {
		var source = a.ResText;
		console.log(source);
		var start = source.indexOf("Symbol") - 2;
		var substring = source.substr(start);
		var end = substring.indexOf("]")+2;
		
		var indexInfo = substring.substr(0, end);
		if (indexInfo.endsWith("}")) {
			var obj = $.parseJSON(indexInfo);
			if (obj) {
				console.log(obj);
			}
		}
	}*/
	
	var d=a.ResText.indexOf(b.Head.StrSplit);
	if(d>7)
	{
		var e=a.ResText.substring(0,d);
		b.Head.FromHeadString(e);
		var msgType = b.Head.Type;
		if (msgType == '10000') { //news
			var body = a.ResText.substring(d+4);
			var data = JSON.parse(body);
			if (data.type == 'coupon') {
				noticeMsg(data);	
			}
		}
		
		var f=new TextEncoder("utf-8").encode(b.Head.toHeadString()).buffer.byteLength<<0;
		if(isNaN(b.Head.Len)||(b.Head.Len=b.Head.Len<<0),a.ResBuffer.byteLength>=f+b.Head.Len)
		{
			var g=a.ResBuffer.Oslice(f,f+b.Head.Len);
			a.ResBuffer=a.ResBuffer.Oslice(f+b.Head.Len,a.ResBuffer.byteLength);
			var h=new Uint8Array(g);
			1==b.Head.EnCryCode&&(h=window.NetCommon.DecodeCA(h)),1==b.Head.Comcode&&(h=new Zlib_Inflate(h).decompress()),b.Data=new TextDecoder("utf-8").decode(h),a.Callback(b),a.ProcessRecData()
		}
	}
},
g.prototype.Callback=e,
g.prototype.DeleteCallBack=function(a){var b=this,c=a.Head.ID;b.SucTaskList["SucCallBack"+c]=null,delete b.SucTaskList["SucCallBack"+c],b.ErrTaskList["SucCallBack"+c]=null,delete b.ErrTaskList["SucCallBack"+c]},function(){var a=[],b=a.slice;Events=g.prototype.Events={on:function(a,b,c){if(!d(this,"on",a,[b,c])||!b)return this;this._events||(this._events={});var e=this._events[a]||(this._events[a]=[]);return e.push({callback:b,context:c,ctx:c||this}),this},once:function(a,b,c){if(!d(this,"once",a,[b,c])||!b)return this;var e=this,f=_.once(function(){e.off(a,f),b.apply(this,arguments)});return f._callback=b,this.on(a,f,c)},off:function(a,b,c){if(!this._events||!d(this,"off",a,[b,c]))return this;if(!a&&!b&&!c)return this._events=void 0,this;for(var e=a?[a]:_.keys(this._events),f=0,g=e.length;g>f;f++){a=e[f];var h=this._events[a];if(h)if(b||c){for(var i=[],j=0,k=h.length;k>j;j++){var l=h[j];(b&&b!==l.callback&&b!==l.callback._callback||c&&c!==l.context)&&i.push(l)}i.length?this._events[a]=i:delete this._events[a]}else delete this._events[a]}return this},trigger:function(a){if(!this._events)return this;var c=b.call(arguments,1);if(!d(this,"trigger",a,c))return this;var f=this._events[a],g=this._events.all;return f&&e(f,c),g&&e(g,arguments),this},listenTo:function(a,b,c){var d=this._listeningTo||(this._listeningTo={}),e=a._listenId||(a._listenId=_.uniqueId("l"));return d[e]=a,c||"object"!=typeof b||(c=this),a.on(b,c,this),this},listenToOnce:function(a,b,d){if("object"==typeof b){for(var e in b)this.listenToOnce(a,e,b[e]);return this}if(c.test(b)){for(var f=b.split(c),g=0,h=f.length;h>g;g++)this.listenToOnce(a,f[g],d);return this}if(!d)return this;var i=_.once(function(){this.stopListening(a,b,i),d.apply(this,arguments)});return i._callback=d,this.listenTo(a,b,i)},stopListening:function(a,b,c){var d=this._listeningTo;if(!d)return this;var e=!b&&!c;c||"object"!=typeof b||(c=this),a&&((d={})[a._listenId]=a);for(var f in d)a=d[f],a.off(b,c,this),(e||_.isEmpty(a._events))&&delete this._listeningTo[f];return this}};var c=/\s+/,d=function(a,b,d,e){if(!d)return!0;if("object"==typeof d){for(var f in d)a[b].apply(a,[f,d[f]].concat(e));return!1}if(c.test(d)){for(var g=d.split(c),h=0,i=g.length;i>h;h++)a[b].apply(a,[g[h]].concat(e));return!1}return!0},e=function(a,b){var c,d=-1,e=a.length,f=b[0],g=b[1],h=b[2];switch(b.length){case 0:for(;++d<e;)(c=a[d]).callback.call(c.ctx);return;case 1:for(;++d<e;)(c=a[d]).callback.call(c.ctx,f);return;case 2:for(;++d<e;)(c=a[d]).callback.call(c.ctx,f,g);return;case 3:for(;++d<e;)(c=a[d]).callback.call(c.ctx,f,g,h);return;default:for(;++d<e;)(c=a[d]).callback.apply(c.ctx,b);return}};Events.bind=Events.on,Events.unbind=Events.off}(),
g.prototype.Timeout=1e4,
$.extend(g.prototype,g.prototype.Events),
g.prototype.SetTimeout=function(a,b){var c=this,d=c.Timeout;c.off("trigger::timeout"+a).on("trigger::timeout"+a,function(b){c.DeleteCallBackById(a),c.SendReq(b)}),c["outtimer"+a]=setTimeout(function(){c.trigger("trigger::timeout"+a,b)},d)},g.prototype.DeleteCallBackById=function(a){var b=this;b.SucTaskList["SucCallBack"+a]=null,delete b.SucTaskList["SucCallBack"+a],b.ErrTaskList["SucCallBack"+a]=null,delete b.ErrTaskList["SucCallBack"+a]},
g.prototype.ClearTimeout=function(a){var b=this;b.off("trigger::timeout"+a),b["outtimer"+a]&&clearTimeout(b["outtimer"+a])}}(this),
	
	//data structure for request header/body/response 
	function(a){
		a.HtNet=function(b,c){
			var d=new a.Net(b,c);
			return d.ProcessRecData=function(){
				var a=new HqRes,b=this.ResText.indexOf(a.Head.StrSplit);
				if(b>-7){
					var c=this.ResText.substring(0,b);
					a.Head.FromHeadString(c);
					var d=this.ResText.substring(b+4,this.ResText.length);
					d.length>0&&(a.Data=d),this.Callback(a)}},d},
		a.SocketNet=function(b){
			return new a.Socket(b)},
		a.HqRes=function(){
			this.Head=new a.HqHeader,
			this.Data=null},
		a.hqReq=function(){
			this.Head=new a.HqHeader},
		hqReq.prototype.GetReqCmd=function(){
			var a=this,b="";
			switch(a.Head.Type){
				case ReqType.CRTCkLive:
					b='{"ConnectionID":11}';
					break;
				case ReqType.CRTAuth:
					b='{"User":"'+a.UName+'", "Pass":"'+a.Pwd+'", "AuthSerID":'+a.AuthSerID+', "UserID":'+a.UserID+',"Session":'+a.Session+',"Product":'+a.Product+',"SubProduct":'+a.SubProduct,b+=',"Block":[';var c,d,e=[],f=null,g=null;for(f=0;f<a.Block.length;f++){for(c='{"Name":'+a.Block[f].Name+',"Mask":[',d=[],g=0;g<a.Block[f].Mask.lenth;g++){var h='{Market":'+a.Block[f].Mask[g].Market+'"Mask":'+a.Block[f].Mask[g].Mask+'"}';d.push(h)}c+=d.join(",")+"]}",e.push(c)}
					b+=e.join(",")+"],",
					b+='"Market":['+a.Market.join(",")+"]}";
					break;
				case ReqType.PRTLogout:
					b='{"UserID":'+a.UserID+', "Session":'+a.Session+',"Product":'+a.Product3+"}";
					break;
				case ReqType.CRTReqMtInfo:
					var f,i=[],j=a.Market.length;
					for(f=0;j>f;f++)
						i.push('{"Market":'+a.Market[f]+"}");
					b="["+i.join(",")+"]";
					break;
				case ReqType.CRTReqSbInfo:
					var f,k,l=[],j=a.Symbol.length;
					for(f=0;j>f;f++)
						k=a.Symbol[f],l.push('{"Market":'+k.Market+',"Code":"'+k.Code+'"}');
					b="["+l.join(",")+"]";
					break;
				case ReqType.PRTReqAHSym:
					b='{"Crc32":0}';
					break;
				case ReqType.CRTRequestUpdatePush:
					for(var f in a.Symbol)
						this.Symbol[f].Type=a.Type||1,this.Symbol[f].BrokerCount=a.BrokerCount||20,this.Symbol[f].Language=a.Language||0,this.Symbol[f].PushFlag=a.PushFlag||0;b=JSON.stringify(a.Symbol);
						break;
				case ReqType.CRTreqSbList:
					b='{"MarketID":'+a.Market+',"IDType":'+a.IDType+',"BeginPos":'+a.Pos+',"Count":'+a.Count+',"GetQuote":'+a.GetQuote+',"PushFlag":'+a.PushFlag+"}";
					break;
				case ReqType.PRTReqSbReport:
					b='{"Type":'+a.iType+',"Desc":'+a.Desc+',"BeginPos":'+a.Pos+',"Count":'+a.Count+',"GetQuote":'+a.GetQuote+',"PushFlag":'+a.PushFlag+',"Market":[';var f,k,i=[],j=a.Market.length;for(f=0;j>f;f++)k=a.Market[f],i.push('{"MarketID":'+k.MarketID+',"IDType":'+k.IDType+"}");
					b+=i.join(",")+"]}";
					break;
				case ReqType.CRTReqRealTimePrice:
					b='{"PushFlag":0,"GetSymInfo":'+a.GetSymInfo+ ',"wxid":' + $("#wxid").val() + ',"Symbol":[';var f,k,i=[],j=a.Symbol.length;for(f=0;j>f;f++)k=a.Symbol[f],i.push('{"Market":'+k.Market+',"Code":"'+k.Code+'"}');
					b+=i.join(",")+"]}";
					break;
				case ReqType.PRTReqSubMtList:
					b='{"Req":"SubMarket"}';
					break;
				case ReqType.PRTReqMinutesKLine:
					b='{"Market":'+a.Market+', "Code":"'+a.Code+'", "PushFlag":'+a.PushFlag+', "Minute":'+a.Minute+',"TypeCount":'+a.TypeCount+',"Weight":'+a.Weight+",",b+='"TimeType":'+a.TimeType+',"Count":'+a.Count+',"Time0":"'+a.Time0+'","Time1":"'+a.Time1+'"}';
					break;
				case ReqType.CRTReqSbKLine:
					b='{"Market":'+a.Market+', "Code":"'+a.Code+'", "PushFlag":'+a.PushFlag+', "KLineType":'+a.KLineType+',"Weight":'+a.Weight+",",b+='"TimeType":'+a.TimeType+',"Count":'+a.Count+',"Time0":"'+a.Time0+'","Time1":"'+a.Time1+'"}';
					break;
				case ReqType.CRTReqTrend:
					b='{"Market":'+a.Market+', "Code":"'+a.Code+'", "PushFlag":'+a.PushFlag+', "TimeType":'+a.TimeType+',"TimeValue0":'+a.TimeValue0+',"TimeValue1":'+a.TimeValue1+',"Day":"'+a.Day+'"}';
					break;
				case ReqType.PRTReqFinance:
					b='{"Market":'+a.Market+', "Code":"'+a.Code+'"}';
					break;
				case ReqType.PRTReqConditionCode:
					var m={Market:a.Market,Code:a.Code,TaradeCode:a.TaradeCode,Name:a.Name};
					b=JSON.stringify(m);
					break;
				case ReqType.CRTReqCodeList:
					b='{"Code":"'+a.Code+'"}';
					break;
				case ReqType.PRTReqSpreadCodeTable:
					b='{"Market":"'+a.Market+'"}';
					break;
				case ReqType.PRTReqTradeTime:
					b=this.TradeTimeIDList instanceof Array?this.TradeTimeIDList:[{TradeTimeID:0}],
					b=JSON.stringify(b)
			}return b},
		hqReq.prototype.toHttpText=function(){
			var a=this.GetReqCmd();
			this.Head.Len=a.length;
			var b=this.Head.toHeadString();
			return b+a}}(this),
		
		function(a){
			var b=a.HqCollections=function(a){this.Init(a)},
			c=b.prototype;c.MaxConnectNum=3,c.TipConnectNum=3,c.EndTipConnectNum=2,c.EndTipConnectTime=10,c.CurrentChooseServer=[],
			c.ChooseServer=function(){
				var a,b=this,c=b.Servers,d=c.length,e=b.MaxConnectNum,f=[],g=[];
				if(b.CurrentChooseServer=[],d>e)
				{
					for(;f.length<e;)
						f.indexOf(a=Math.floor(Math.random()*d))<0&&f.push(a);

					for(var h in f)
						g.push(c[f[h]])
				} else 
					g=c;

				for(var h in g)
					b.CurrentChooseServer.push({server:g[h]});
				return b
			},
			c.Init=function(a){
				var b=this;
				return b.isDoHqAuth=b.isReady=b.isReconn=!0,b.Servers=a,b.ChooseServer().InitServer(b.ConnnetTime=1,!0),b},
			c.InitServer=function(b,c){
				var d=this,e=d.CurrentChooseServer||[],f=e.length,g=d.TipConnectNum;if(1>f)return layer.msg("连接服务器失败，请稍后重试！"),d;
				if(a.NotConnentNet)return d;
				if(b>g){d.TipConnentionObj||(d.TipConnentionObj=[]),d.ConnnetTime=1,d.TipConnentionObj.push(new Date);var h=d.TipConnentionObj.length;if(h>d.EndTipConnectNum){if(d.TipConnentionObj[h-1].getTime()-d.TipConnentionObj[0].getTime()<=6e4*d.EndTipConnectTime)return d.TipConnentionObj=d.TipConnentionObj.slice(h-2),$.netNetTip(null,2),void 0;d.TipConnentionObj=d.TipConnentionObj.slice(h-2)}return $.netNetTip(function(){d.ChooseServer().InitServer(d.ConnnetTime=1)},1),d}return d.ConnectServer(c),d},
			c.ConnectServer=function(a){var b=this,c=b.CurrentChooseServer,d=null;for(d in c)b.InitWebSocket(c[d],a)},
			c.InitWebSocket=function(b,c){
				var d,e=this,f=b.server.Addr+":"+b.server.Port+"/WebSocket/message";
				return b.Socket=d=new a.SocketNet(f),
				d.SocketClose=function(a){var b=!0,c=null,f=e.CurrentChooseServer;for(c in f)f[c].Socket!=d?f[c].CloseSocket||(b=!1):f[c].CloseSocket=!0;b&&e.ReConnection(a)},
				d.SocketOpen=function()
				{
						var a=null,b=[],f=e.CurrentChooseServer;
						try{
							for(a in f)
								f[a].Socket!=d&&f[a].Socket.StockWs.close()
							}catch(g){
								console.log(g)
						}
						for(a in f)
							if(f[a].Socket==d)
							{
								b.push(f[a]);
								break
						}
						e.CurrentChooseServer=b,
						e.hqNetSocket||(e.hqNetSocket=this),
						e.doConnectAfter(c)
				},e},
c.doConnectAfter=function(){
	var a=this,b=HangqingManage;
	a.Live(),
	b.CurrentGood&&a.MarketInfo?(a.PushCall(a.PushHQFunc,null,a.PushSymbol),b.NeedKLine&&a.PushCallChartLine(a.PushHQFunc,null,a.PushSymbol)):b.doReqMtInfo(a.Merchs).GoodInfo(a.Merchs,$.proxy(b.doAfterGoodInfo,b,a.Merchs))
},
c.ReConnection=function(){var a=this;a.Close(),a.InitServer(++a.ConnnetTime)},
c.Close=function(){var a=this;a.Ser=null,a.hqNetSocket&&(a.hqNetSocket=null),a.LiveHandler&&clearTimeout(a.LiveHandler),a.PushHQtimer&&clearTimeout(a.PushHQtimer),a.deReqTrendTime&&clearTimeout(a.deReqTrendTime),a.deReqKLineTime&&clearTimeout(a.deReqKLineTime)},
c.Send=function(a,b,c){
	"function"!=typeof b&&(b=function(){}),
	"function"!=typeof c&&(c=function(a){
		console.log(a.ErrMsg||"请求发送失败！")}),
	a.Head.Session=this.ConnectionSession,
	a.SucCallBack=b,
	a.ErrCallBack=function(a){a&&(a=JSON.parse(a.Data)),c(a||{})},
	this.SendSocketReq(a)
},
c.SendSocketReq=function(a){
	var b=this;
	b.hqNetSocket?b.hqNetSocket.SendReq(a):b.PushReq(a)
},
c.PushReq=function(a){
	var b=this;b.SocketReqArray||(b.SocketReqArray=[]),
	b.SocketReqArray.push(a)
},
c.Live=function(){
	var a=this;a.LiveHandler&&clearTimeout(a.LiveHandler),
	a.LiveHandler=setTimeout(function(){
		a.doCRTCkLive(function(b){
			a.STime=b.Head.Stime,
			a.Live()},
			function(){a.Live()})},6e4)},
c.doCRTCkLive=function(a,b){
	var c=new window.hqReq;
	c.Head.Type=window.ReqType.CRTCkLive,
	this.Send(c,a,b)},
c.doReqRealTimePrice=function(a,b,c){
		var d=this;
		if(d.isDoHqAuth)
		{
			var e=new window.hqReq;
			e.Head.Type=window.ReqType.CRTReqRealTimePrice,
			e.PushFlag=1,e.GetSymInfo=1;
			for(var f in c)
				c[f].Market=Number(c[f].Market);
			e.Symbol=c,d.PushHQFunc=a,d.PushSymbol=c,d.Send(e,function(e){d.STime=e.Head.Stime,d.PushCall(a,b,c);var f=d.ParseHqData(e.Data).Symbol;a(f,e.Head.Stime)},b)
		}
			else b()
	},
c.doReqTradeTime=function(a,b,c){if(this.isDoHqAuth){var d=this,e=new window.hqReq;e.Head.Type=ReqType.PRTReqTradeTime,e.TradeTimeIDList=c,d.Send(e,function(a){var b=a.Data;"string"==typeof b&&(b=JSON.parse(b)),d.TradeTimeList=b.TradeTime||[],d.HasFinishTradeTime=!0},function(){d.HasFinishTradeTime=!0,console.log("请求夜盘失败！")})}},
c.ParseHqData=function(a){
	var type = typeof String.prototype.endsWith;
	if (!type || type != 'function') {
		var s = a.length - 6;
		if (s>=0 && a.lastIndexOf("LotSiz") == s) {
			a = a + "e\":10}]}";
		}
	} else {
		if ("string"==typeof a && a.endsWith("LotSiz")) {
			a = a + "e\":10}]}";
		}		
	}

	return "string"==typeof a&&(a=JSON.parse(a)),a||{}
},
c.doReqMtInfo=function(a,b,c)
{
	var d=this;
	if(this.isDoHqAuth)
	{
		var e=new window.hqReq;
		e.Head.Type=ReqType.CRTReqMtInfo,
		e.Market=c,
		this.Send(e,
			function(a){
				d.MarketInfo=d.ParseHqData(a.Data).Market
			},b)
	}
},
c.doReqSbInfo=function(a,b,c){if(this.isDoHqAuth){var d=new window.hqReq;d.Head.Type=window.ReqType.CRTReqSbInfo,d.Symbol=c,this.Send(d,a,b)}},
c.doTreqSbList=function(a,b,c,d,e,f,g,h){if(this.isDoHqAuth){var i=new window.hqReq;i.Head.Type=window.ReqType.CRTreqSbList,i.Market=c,i.IDType=d,i.Pos=e,i.Count=f,i.GetQuote=g,i.PushFlag=h,this.Send(i,a,b)}},
c.doReqSbReport=function(a,b,c,d,e,f,g,h,i){var j=this;if(j.isDoHqAuth){var k=new window.hqReq;k.Head.Type=window.ReqType.PRTReqSbReport,k.Market=c,k.Desc=d,k.iType=e,k.Pos=f,k.Count=g,k.GetQuote=h,k.PushFlag=i,j.Send(k,function(b){j.STime=b.Head.Stime,a(b)},b)}},
c.doReqSubMtList=function(a,b){if(this.isDoHqAuth){var c=new window.hqReq;c.Head.Type=window.ReqType.PRTReqSubMtList,this.Send(c,a,b)}},
c.doReqMinutesKLine=function(a,b,c,d,e,f,g,h,i,j,k,l){if(this.isDoHqAuth){var m=new window.hqReq;m.Head.Type=window.ReqType.PRTReqMinutesKLine,m.Market=c,m.Code=d,m.PushFlag=e,m.Minute=f,m.TypeCount=g,m.Weight=h,m.TimeType=i,m.Count=j,m.Time0=k,m.Time1=l,this.Send(m,a,b)}},
c.doReqSbKLine=function(a,b,c,d,e,f,g,h,i,j,k,l){var m=this,n=m.STime&&new Date(m.STime.replace(/-/g,"/"))||new Date;if(m.isDoHqAuth){var o=new window.hqReq;o.Head.Type=window.ReqType.CRTReqSbKLine,o.Market=c,o.Code=d,o.PushFlag=0,o.KLineType=Number(f),o.Weight=g||0,o.TimeType=h||2,o.Count=i,o.Time0=j||n.format("YYYY-M-D hh:mm:ss"),o.Time1=k||"",m.Send(o,function(b){var c=m.ParseKLData(b.Data,l),d=c.datas,e=c.notupdate;m.STime=b.Head.Stime,a(d,e)},b)}},
c.ParseKLData=function(a,b){"string"==typeof a&&(a=JSON.parse(a));var c=this,e=rdata=$.ParseToJson(a.KLine),f=rdata.length,g=!1,h=HangqingManage.CurrentGood.Dec||0,i=rdata[f-1]||{},j=c.dateTimeParse,k=(new Date).getTimezoneOffset();if(j||(j=c.dateTimeParse=new d),b){g=!0,e=c.CurrentKLineData||[];var l,m,n=0,o=e.length,p=e[o-1]||{},q=p.Time;for(n=0;f>n;n++)(l=rdata[n]).Time&&(l.Time=m=j.AddMinutes(j.parseDateTime(i.Time),0-k))>=q&&(l.Close=$.formatNumber(l.Close,h),l.High=$.formatNumber(l.High,h),l.Low=$.formatNumber(l.Low,h),l.Open=$.formatNumber(l.Open,h),m==q?(l.Close!=p.Close||l.High!=p.High||l.Low!=p.Low||l.Open!=p.Open)&&(e[o-1]=l,g=!1):(e.push(l),g=!1))}else for(var n in e)e[n].Time=j.AddMinutes(j.parseDateTime(e[n].Time),0-k),e[n].Close=$.formatNumber(e[n].Close,h),e[n].High=$.formatNumber(e[n].High,h),e[n].Low=$.formatNumber(e[n].Low,h),e[n].Open=$.formatNumber(e[n].Open,h);return{datas:c.CurrentKLineData=e,notupdate:g}},
c.doReqTrend=function(a,b,c,d,e,f,g){var h=this;if(h.isDoHqAuth){e=e||0,f=f||0;var i=new window.hqReq,j=h.FindMarketInfo(c,e);if(!j)return b(),void 0;i.Head.Type=window.ReqType.CRTReqTrend,i.Market=c,i.Code=d,i.PushFlag=0,i.TimeType=0,i.TimeValue1=Number(h.FindOpenClose_Max(j,"Close")),g?(i.TimeType=1,i.TimeValue0=h.CurrentTrendData[h.CurrentTrendData.length-1].TimeIndex,i.TimeValue1=5):i.TimeValue0=Number(h.FindOpenClose_Min(j,"Open")),i.Day=h.FindTradeTime(j,Number(h.FindOpenClose_Min(j,"Open")),i.TimeValue1,f),h.Send(i,function(b){h.STime=b.Head.Stime;var c=h.ParseTrendData(b,b.Data,i.TimeValue0,i.TimeValue1,j,f,g);a(c)},b)}},
c.FindTradeTime=function(a,b){var c,e=this,f=e.dateTimeParse;f||(f=e.dateTimeParse=new d),c=f.parseDateTime(e.STime),!c&&(c=new Date((new Date).getTime()-1e3*3600*a.TimeZone));var g,h,i,j=null,k=null,l=a.TradeTime;for(k in l)i=l[k],j?(g=f.parseDateTime(i),h=f.AddMinutes(g,Number(b)),f.isHigh(c,h)&&(j=i)):j=i;return j},
c.FindTradeNextTime=function(a,b,c,d){for(var e=this,f=e.FindTradeTime(a,b,c,d),g=f,h=0;h<a.TradeTime.length;h++)if(a.TradeTime[h]==f){g=a.TradeTime[h+1],("undefined"==typeof g||null==g)&&(g=f);break}return g},
c.FindOpenClose_Min=function(a,b){var c,d=Number.MAX_VALUE;for(var e in a.OpenCloseTime)c=a.OpenCloseTime[e][b],d=isNaN(d)||null==d?c:Math.min(d,c);return d},
c.FindOpenClose_Max=function(a,b){var c,d=Number.MIN_VALUE,e=null;for(e in a.OpenCloseTime)c=a.OpenCloseTime[e][b],d=isNaN(d)||null==d?c:Math.max(d,c);return d},
c.FindMarketInfo=function(a,b){function c(a,b){return a.OpenCloseTime=JSON.parse(JSON.stringify(b.Time).replace(/Time/g,"")),a.OpenTime=b.Open,a}var e,f=this,g=f.MarketInfo||[],h=g.length,i=null;for(e=0;h>e;e++)if(g[e].Market==a){i=_.clone(g[e]);break}if(b){var j,k,l=f.TradeTimeList,m=l.length,n=f.dateTimeParse;for(n||(n=f.dateTimeParse=new d),j=f.STime?n.parseDateTime(f.STime):new Date,k=j.getDay(),e=0;m>e;e++)if(l[e].ID==b){for(var o=l[e].Time,h=o.length,p=0;h>p;p++){if(o[e].Week==k){i=c(i,o[p]);break}(o[e].Week>6||o[e].Week<0)&&(i=c(i,o[p]))}break}}return i},
c.ParseTrendData=function(a,b,c,e,f,g,h){"string"==typeof b&&(b=JSON.parse(b));var i=this,j=i.dateTimeParse,k=0,l=(new Date).getTimezoneOffset(),m=HangqingManage.CurrentGood.Dec||0;j||(j=i.dateTimeParse=new d);try{k=f.OpenCloseTime[f.OpenCloseTime.length-1].Close||Number.MAX_VALUE}catch(n){k=Number.MAX_VALUE}var o,p=[],q=j.parseDateTime(a.Head.Stime),r=j.parseDateTime(b.Day),s=parseInt((q.getTime()-r.getTime())/1e3/60)+1,t=$.ParseToJson(b.Trend||[]),o=tmp=t[0]||{},u=c,v=0;for(h&&(e=Number.MAX_VALUE,p=i.CurrentTrendData||[],tmp=p[p.length-1]||{},u=tmp.TimeIndex||0,p.length=Math.max(p.length-1,0));u<Math.min(s,e,k);u++)(tmp=t[v])?o=tmp:tmp=o,tmp.Time<u?v++:(tmp.Time==u&&v++,tmp.Price=$.formatNumber(tmp.Price,m),p.push($.extend({},tmp,{Time:new Date(r.getTime()+6e4*u-6e4*l),Volume:0,TimeIndex:u})));return i.CurrentTrendData=p},
c.judgeTimeInTradeTime=function(a,b)
{
	for(var c,d=a.OpenCloseTime,e=d.length,f=a.TradeTime,g=6e4*d[0].Open,h=6e4*d[e-1].Close,i=0;i<f.length;i++)
		if(c=new Date(f[i].replace(/\-/g,"/")),b.getTime()>=c.getTime()+g&&b.getTime()<=c.getTime()+h)
			return!0;
	return!1
},
c.judgeTimeInOpenTime=function(a,b){var c,d=a.OpenCloseTime,e=d.length,f=a.TradeTime,g=6e4*d[0].Open;6e4*d[e-1].Close;for(var h=0;h<f.length;h++)if(c=new Date(f[h].replace(/\-/g,"/")),b.format("YYYY-MM-DD hh:mm")==new Date(c.getTime()+g).format("YYYY-MM-DD hh:mm"))return!0;return!1},
c.doReqCodeList=function(a,b,c){if(this.isDoHqAuth){var d=new window.hqReq;d.Head.Type=window.ReqType.CRTReqCodeList,d.Code=c,this.Send(d,a,b)}},
c.doReqConditionCode=function(a,b,c,d,e,f){if(this.isDoHqAuth){var g=new window.hqReq;g.Head.Type=window.ReqType.PRTReqConditionCode,g.Market=c,g.Code=d,g.TaradeCode=e,g.Name=f,this.Send(g,a,b)}},
c.UpdataTrendLineData=function(a){
	var b=this,c=b.FindMarketInfo(a.Market,a.TradeID||0),d=(new Date).getTimezoneOffset();
	if(b.CurrentTrendData)
	{
		if(b.judgeTimeInOpenTime(c,a.Time))
			return b.CurrentTrendData=[],b.CurrentTrendData.push({Price:a.Now,Time:new Date(a.Time.getTime()-6e4*d),TimeIndex:c.OpenCloseTime[0].Open,Volume:0}),b.CurrentTrendData;
		if(b.judgeTimeInTradeTime(c,a.Time))
		{
			var e=b.CurrentTrendData||[],f=Math.max(0,e.length-1),g=_.clone(e[f]||{});
			try{
				var h=new Date(a.Time.getTime()-60*1e3*d);
				g.Time&&g.Time.format("hhmm")!=h.format("hhmm")?(g.Price=a.Now,g.Time=h,g.TimeIndex=g.TimeIndex+1,e.push(g)):(g.Price=a.Now,e[f]=g)}catch(i){b.CurrentTrendData=null,HangqingManage.doReqTrend(this.CurrentUser.SSucFunc,this.CurrentUser.SErrFunc)}
			return e
		} 
		console.log("in UpdataTrendLineData, no change");
	}
},
c.UpdataKLineData=function(a,b){var c=this,d=c.FindMarketInfo(a.Market,a.TradeID||0),e=(new Date).getTimezoneOffset();if(c.CurrentKLineData&&c.judgeTimeInTradeTime(d,a.Time)){var f=c.CurrentKLineData||[],g=Math.max(0,f.length-1),h=_.clone(f[g]||{}),i=new Date(a.Time.getTime()-6e4*e),j={1:1,2:5,3:60,4:3,5:15,6:30,10:1440,11:10080,20:43200};try{!h.Time||h.Time.getTime()+6e4*j[b]>i.getTime()?(h.Close=a.Now,h.High=Math.max(a.Now,h.High||Number.MIN_VALUE),h.Low=Math.min(a.Now,h.Low||Number.MAX_VALUE),f[g]=h):(h.Close=a.Now,h.High=a.Now,h.Time=new Date(h.Time.getTime()+6e4*j[b]),h.Open=a.Now,h.Low=a.Now,f.push(h))}catch(k){c.CurrentKLineData=null,HangqingManage.doReqSbKLine(this.CurrentUser.KSucFunc,this.CurrentUser.KErrFunc)}return f}},
c.PushCallChartLine=function(a){var b=this;1==a?b.PushCallKLine():b.PushCallSLine()},
c.PushCallKLine=function(){var a=this;a.LiveHandler&&clearTimeout(a.LiveHandler),a.deReqKLineTime&&clearTimeout(a.deReqKLineTime),a.deReqKLineTime=setTimeout(function(){var b=HangqingManage.CurrentGood;a.CurrentKLineData=null,a.doReqSbKLine(a.KSucFunc,a.KErrFunc,b.Market,b.Code,1,a.KType,0,2,100)},3e4)},
c.PushCallSLine=function(){var a=this;a.LiveHandler&&clearTimeout(a.LiveHandler),a.deReqTrendTime&&clearTimeout(a.deReqTrendTime),a.deReqTrendTime=setTimeout(function(){var b=HangqingManage.CurrentGood;a.CurrentTrendData=null,a.doReqTrend(a.SSucFunc,a.SErrFunc,b.Market,b.Code,b.TradeID,b.TradeTimeType)},3e4)},
c.PushCall=function(a,b,c){var d=this;d.ClosePush(),d.PushAll(a,b,c),d.PushHQtimer&&clearTimeout(d.PushHQtimer),d.PushHQtimer=setTimeout(function(){d.PushCall(a,b,c)},18e4)},
c.PushAll=function(a,b,c){var d=this;return d.doReqUpdatePush(function(b){var c=d.ParseHqData(b.Data).Symbol;c.length>0&&a(c,d.STime=b.Head.Stime)},b,c,1),void 0},
c.ClosePush=function(){var a=this;a.HttpPushHandler&&clearTimeout(a.HttpPushHandler),a.doSucReqTrend&&delete a.doSucReqTrend,a.doReqClosePush()},
c.doReqClosePush=function(){return this.doReqUpdatePush(function(){},function(){},[],1),this},
c.doReqUpdatePush=function(a,b,c,d){if(this.isDoHqAuth){var e=new window.hqReq;e.Head.Type=ReqType.CRTRequestUpdatePush,e.PushFlag=1,e.Language=0,e.Type=d,e.BrokerCount=20,e.Symbol=c,window.PushDataSuccessCallback=a,this.Send(e,null,b)}
};
var d=function(){},
e=d.prototype;
e.parseDateTime=function(a){var b=a.replace(/[-、]/g,"/"),c=null;try{c=new Date(b)}finally{return c}},
e.getDateObject=function(a){return"string"==typeof a?this.parseDateTime(a):a instanceof Date?a:isNaN(a)?null:new Date(a)},
e.AddDay=function(a,b){return a=this.getDateObject(a),a instanceof Date?new Date(a.getTime()+24*60*1e3*60*Number(b)):null},
e.AddHours=function(a,b){return a=this.getDateObject(a),a instanceof Date?new Date(a.getTime()+1e3*60*60*Number(b)):null},
e.AddMinutes=function(a,b){return a=this.getDateObject(a),a instanceof Date?new Date(a.getTime()+1e3*60*Number(b)):null},
e.getWeek=function(a){return a=this.getDateObject(a),a instanceof Date?a.getDay():null},
e.isHigh=function(a,b){return a instanceof Date&&b instanceof Date?a.getTime()>b.getTime():!1}
}(this);

!function(root){
	root.noticeMsg=function (data){
		root.layer.open({
			type : 0,
			title : data.title,
			area : [ "90%", "auto" ],
			closeBtn: 0,//['我知道了'], //按钮
			skin : 'layui-layer-createorder layui-layer-notice', //没有背景色
			shadeClose : true,
			content : data.message,
			cancel : function(index) {
				event.preventDefault();
				layer.close(index);
			},
			btn1 : function(index) {
				event.preventDefault();
				layer.close(index);
				
			}
		});
	}
}(this);
