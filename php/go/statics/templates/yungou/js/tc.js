  lastScrollY=0;
    function heartBeat(){
		  diffY=document.body.scrollTop;
		   percent=.1*(diffY-lastScrollY);
		     if(percent>0)percent=Math.ceil(percent);
			  else percent=Math.floor(percent);
			    document.all.lovexin12.style.pixelTop+=percent;
				 document.all.lovexin14.style.pixelTop+=percent;
				  lastScrollY=lastScrollY+percent;
				   }
  suspendcode12="<DIV id=lovexin12 style='left:10px;POSITION:absolute;TOP:420px;'>

  <img src='{G_TEMPLATES_STYLE}/images/zuo.jpg' border=0>
  </div>" 
  document.write(suspendcode12);  suspendcode14="<DIV id=lovexin14 style='right:10px;POSITION:absolute;TOP:420px;'>

  <img src='{G_TEMPLATES_STYLE}/images/you.jpg' border=0>
  </div>"
   document.write(suspendcode14); window.setInterval("heartBeat()",1);
