var table=document.getElementsByTagName("table");
var url = table[0].children[1].children[0].children[2].children[0].children[0].getAttribute('href');

if(getCookie('everyday') == '1'){
}
else{
	setCookie('everyday',"1","d1");
	layer.open({
	  type: 2,
	  title: '最新公告',
	  shadeClose: true,
	  shade: false,
	  maxmin: true, //开启最大化最小化按钮
	  area: ['900px', '720px'],
	  content: url
	});
}
/**
layer.open({
  type: 2,
  title: false,
  closeBtn: 0, //不显示关闭按钮
  shade: [0],
  area: ['340px', '215px'],
  offset: 'rb', //右下角弹出
  time: 2000, //2秒后自动关闭
  anim: 2,
  content: [url, 'no'], //iframe的url，no代表不显示滚动条
  end: function(){ //此处用于演示
    layer.open({
      type: 2,
      title: '最新公告',
      shadeClose: true,
      shade: false,
      maxmin: true, //开启最大化最小化按钮
      area: ['893px', '600px'],
      content: url
    });
  }
});
**/

function setCookie(name,value,time)
{
var strsec = getsec(time);
var exp = new Date();
exp.setTime(exp.getTime() + strsec*1);
document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}
function getsec(str)
{
//alert(str);
var str1=str.substring(1,str.length)*1;
var str2=str.substring(0,1);
if (str2=="s")
{
return str1*1000;
}
else if (str2=="h")
{
return str1*60*60*1000;
}
else if (str2=="d")
{
return str1*24*60*60*1000;
}
}

function getCookie(name)
{
var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
if(arr=document.cookie.match(reg))
return unescape(arr[2]);
else
return null;
}