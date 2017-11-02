<?php

/**

* Template designed by 12leaves.com

* 12leaves.com - Free ecommerce templates and design services

*

* Common Template

* 

* @package languageDefines

* @copyright Copyright 2009-2010 12leaves.com

* @copyright Copyright 2003-2006 Zen Cart Development Team

* @copyright Portions Copyright 2003 osCommerce

* @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0

* @version $Id: tpl_header.php 4813 2006-10-23 02:13:53Z drbyte $

*/

?>

<?php

  // Display all header alerts via messageStack:

  if ($messageStack->size('header') > 0) {

    echo $messageStack->output('header');

  }

  if (isset($_GET['error_message']) && zen_not_null($_GET['error_message'])) {

  echo htmlspecialchars(urldecode($_GET['error_message']));

  }

  if (isset($_GET['info_message']) && zen_not_null($_GET['info_message'])) {

   echo htmlspecialchars($_GET['info_message']);

} else {



}



// test if box should display

  $show_languages= true;



  if ($show_languages == true) {

    if (!isset($lng) || (isset($lng) && !is_object($lng))) {

      $lng = new language;

    }



    reset($lng->catalog_languages);

    $languages_array = array();

    $current_language = '';    

      while (list($key, $value) = each($lng->catalog_languages)) {

        $languages_array_popup[$key] = $value['name'];

      }

     

    foreach ($languages_array_popup as $k => $v){

        if ($k == $_SESSION['languages_code']) {

            $current_language = $v;

        }

    }

  }



?>

<?php

if (!isset($flag_disable_header) || !$flag_disable_header) {

?>
<div style="display:none">
<!-- Facebook Pixel Code -->
<script>
!function(f,b,e,v,n,t,s)
{if(f.fbq)return;n=f.fbq=function(){n.callMethod?
n.callMethod.apply(n,arguments):n.queue.push(arguments)};
if(!f._fbq)f._fbq=n;n.push=n;n.loaded=!0;n.version='2.0';
n.queue=[];t=b.createElement(e);t.async=!0;
t.src=v;s=b.getElementsByTagName(e)[0];
s.parentNode.insertBefore(t,s)}(window,document,'script',
'https://connect.facebook.net/en_US/fbevents.js');
 fbq('init', '386380038368649'); 
fbq('track', 'PageView');
fbq('track', 'ViewContent');
</script>
<noscript>
 <img height="1" width="1" 
src="https://www.facebook.com/tr?id=386380038368649&ev=PageView
&noscript=1"/>
</noscript>
<!-- End Facebook Pixel Code -->
</div>
<!--<div id="google_translate_element"></div><script type="text/javascript">
function googleTranslateElementInit() {
  new google.translate.TranslateElement({pageLanguage: 'en', includedLanguages: 'de,en,fr,it', layout: google.translate.TranslateElement.FloatPosition.TOP_LEFT}, 'google_translate_element');
}
</script>
<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>-->
<style type="text/css">
.goog-te-gadget .goog-te-combo{
	width: 60%;
}

.index-cate{
    width: 100%;
    vertical-align: bottom;
    font-size: 1.5em;
    font-weight: 400;
    color: #222;
    border-bottom: 1px solid #d2d2d2;
    position: relative;
}
.timetitle {
    font-weight: 700;
    font-size: 12px;
    color: #222;
    text-align: left;
    line-height: 25px;
    background: #f2f2f2;
    border-top: 1px solid #d2d2d2;
    padding-left: 10px;
    border-bottom: 1px solid #d2d2d2;
}
.index-cate img{ width: 100%;}

.time {
    background: #272b2e;
    color: #fff;
    text-align: center;
}

#t_d, #t_h, #t_m, #t_s {
    display: inline-block;
    color: red;
    background-image: -webkit-gradient(linear, 0 0,0 bottom, from(rgba(243, 27, 29, 1)), to(rgba(57, 6, 5, 1)));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    padding: 0px;
}

#t_d, #t_h, #t_m, #t_s {
    font-size: 48px;
    font-weight: 700;
    margin-left: 10px;
    margin-right: 10px;
    color: #eca414;
    line-height: 1.1em;
}

</style>
<?php /*?><div class="moblie-yeeze">
	<div class="time">
<div class="time_title">90% OFF AVAILABLE LIMITED</div>
<span id="t_d">1</span>D
<span id="t_h">8</span>H
<span id="t_m">52</span>M
<span id="t_s">29</span>S
</div>
<style type="text/css">
.time{ color: #999; font-size: 14px; width: 100%; background: #222; }
.time_title{ background: #f2f2f2; border-bottom: 1px solid #d2d2d2; line-height: 2.0em; padding-left: 15px; color: #222;}
#t_d,#t_h,#t_m,#t_s{ color: #f00; font-size: 22px; font-weight: 700;}
.time span{ padding-left: 20px;}
</style>
<script>
   function GetRTime(){
       var EndTime= new Date('2016/10/21 00:00:00');
       var NowTime = new Date();
       var t =EndTime.getTime() - NowTime.getTime();
       var d=Math.floor(t/1000/60/60/24);
       var h=Math.floor(t/1000/60/60%24);
       var m=Math.floor(t/1000/60%60);
       var s=Math.floor(t/1000%60);

       document.getElementById("t_d").innerHTML = d ;
       document.getElementById("t_h").innerHTML = h ;
       document.getElementById("t_m").innerHTML = m ;
       document.getElementById("t_s").innerHTML = s ;
   }
   setInterval(GetRTime,0);
</script>
</div><?php */?>


<div class="mobile-index">
<ul>
  <li><a href="./" class="logo-img"><img height="40px" alt="Sunglasses Official Discount Online Shop" src="<?php echo DIR_WS_TEMPLATE; ?>/images/yeeze/logo.png"></a></li>
  <li id="top-menu"><a class="logo-img"><img height="40px" src="<?php echo DIR_WS_TEMPLATE; ?>/images/yeeze/menu.png"></a></li>
  <li><a href="<?php echo zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL');?>" class="logo-img"><img height="40px" src="<?php echo DIR_WS_TEMPLATE; ?>/images/yeeze/shoppingbag.png"></a></li>
  <?php if($_SESSION['customer_id']) { ?>
		<li><a class="logo-img" href="<?php echo zen_href_link(FILENAME_LOGOFF, '', 'SSL');?>" rel="nofollow"><img height="40px" src="<?php echo DIR_WS_TEMPLATE; ?>/images/yeeze/me.png"></a></li>
  <?php } else { ?>
	     <li><a class="logo-img" href="<?php echo zen_href_link(FILENAME_LOGIN, '', 'SSL');?>" rel="nofollow"><img height="40px" src="<?php echo DIR_WS_TEMPLATE; ?>/images/yeeze/me.png"></a></li>
    <?php } ?>
</ul>
</div>
<?php if ($this_is_home_page) {?>

<a href="wayfarer-c-9_15.html">
<img src="includes/templates/jersey_mobile/images/main/summers.jpg" width="100%" style="margin-top:0px;">
</a>
<div class="time">
<div class="timetitle">
2017 SUMMER PROMOTIONS TIME LIMITED:
</div>
<span id="t_d">00</span>D
<span id="t_h">00</span>H
<span id="t_m">00</span>M
<span id="t_s">00</span>S
</div>
<div class="timetitle">
BIG SALE SAVE 93% OFF FREE SHIPPING 4 PAIRS.
</div>
<script>
   function GetRTime(){
       var EndTime= new Date(getNextDay(new Date));
       var NowTime = new Date();
       var t =EndTime.getTime() - NowTime.getTime();

       var d=Math.floor(t/1000/60/60/24);
       var h=Math.floor(t/1000/60/60%24);
       var m=Math.floor(t/1000/60%60);
       var s=Math.floor(t/1000%60);

       document.getElementById("t_d").innerHTML = d ;
       document.getElementById("t_h").innerHTML = h ;
       document.getElementById("t_m").innerHTML = m ;
       document.getElementById("t_s").innerHTML = s ;
   }
   setInterval(GetRTime,0);

   function getNextDay(d){
        d = new Date(d);
        d = +d + 1000*60*60*24;
        d = new Date(d);
        return d.getFullYear()+"/"+(d.getMonth()+1)+"/"+d.getDate();
         
    }
</script> 
<a href="collections-c-9.html">
  <img src="includes/templates/jersey_mobile/images/main/glasses.jpg" width="100%">
</a>

<div style="width:100%; margin:0 auto; border-top:1px solid #d2d2d2;">
<a href="aviator-c-9_10.html">
<div class="index-cate index-cate1">
    <img src="includes/templates/jersey_mobile/images/main/index-cate1.jpg">
<div class="indexcatetitle">Aviator</div>
</div>

</a>

<a href="wayfarer-c-9_15.html">

<div class="index-cate index-cate2">

    <img src="includes/templates/jersey_mobile/images/main/index-cate2.jpg">

<div class="indexcatetitle">Wayfarer</div>

</div>

</a>

<a href="clubmaster-c-9_14.html">

<div class="index-cate index-cate3">

    <img src="includes/templates/jersey_mobile/images/main/index-cate3.jpg">

<div class="indexcatetitle">Clubmaster</div>

</div>

</a>

<a href="round-c-9_20.html">

<div class="index-cate index-cate4">

    <img src="includes/templates/jersey_mobile/images/main/index-cate4.jpg">

<div class="indexcatetitle">Round</div>

</div>

</a>

<a href="justin-c-9_37.html">

<div class="index-cate index-cate5">

    <img src="includes/templates/jersey_mobile/images/main/index-cate5.jpg">

<div class="indexcatetitle">Justin</div>
</div>
</a>
<a href="erika-c-9_38.html">
<div class="index-cate index-cate6">
    <img src="includes/templates/jersey_mobile/images/main/index-cate6.jpg">
<div class="indexcatetitle">Erika</div>
</div>

</a>

<script src="includes/templates/jersey_mobile/jscript/unslider.js"></script>
<div class="banner-slide">
    <ul>
    <li><a href="rb3498-c-1_28.html"><img src="includes/templates/jersey_mobile/images/main/5.jpg" width="100%"> </a></li>
    <li><a href="round-c-9_20.html"><img src="includes/templates/jersey_mobile/images/main/2.jpg" width="100%"> </a></li>
    <li><a href="collections-c-9.html"><img src="includes/templates/jersey_mobile/images/main/3.jpg" width="100%"></a></li> 
    <li><a href="top-bar-c-12_22.html"><img src="includes/templates/jersey_mobile/images/main/4.jpg" width="100%"></a></li>   
    </ul>
</div>
<div class="banner-slide" style="margin-top:-5px; margin-bottom:-5px;">
    <ul>
    <li><a href="clubmaster-c-9_14.html"><img src="includes/templates/jersey_mobile/images/main/new1.jpg" width="100%" /></a></li>
    <li><a href="shooter-c-12_53.html"><img src="includes/templates/jersey_mobile/images/main/new2.jpg" width="100%" /></a></li>
    <li><a href="round-c-9_20.html"><img src="includes/templates/jersey_mobile/images/main/new3.jpg" width="100%" /></a></li>
    <li><a href="caravan-c-9_11.html"><img src="includes/templates/jersey_mobile/images/main/new2.jpg" width="100%" /></a></li>  
    </ul>

</div>

<script type="text/javascript">

$(function() {

    $('.banner-slide').unslider({

        speed: 3000,               

        delay: 500,             

        complete: function() {}, 

        keys: true,              

        dots: true,              

        fluid: false             

    });
});

</script>

<br class="clearBoth">

</div>


<?php }?>




<?php

if ($this_is_home_page) {

  if (SHOW_BANNERS_GROUP_SET1 != '' && $banner = zen_banner_exists('dynamic', SHOW_BANNERS_GROUP_SET1)) {

    if ($banner->RecordCount() > 0) {

?>

<div id="bannerOne" class="banners"><?php echo zen_display_banner('static', $banner); ?></div>

<?php

    }

  }

}   

?>


<?php } ?>