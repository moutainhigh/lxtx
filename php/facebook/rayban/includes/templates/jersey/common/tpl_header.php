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
<section id="ye_top-menu">
    <div class="container">
        <div class="row">
          <div class="span8 hidden-phone">
          FREE shipping and FREE returns order over $80. <span style="color:#f00;margin-left:10px;">SALE SAVE UP TO 90% OFF </span>
          </div>
		  <!--<div style="float:left;margin-left:-231px">
		        <div id="google_translate_element"></div><script type="text/javascript">
function googleTranslateElementInit() {
  new google.translate.TranslateElement({pageLanguage: 'en', includedLanguages: 'de,en,fr,it', layout: google.translate.TranslateElement.FloatPosition.TOP_LEFT}, 'google_translate_element');
}
</script><script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>
		  </div>-->
          <div class="span3 pull-right">
            <ol class="top-breadcrumb"> 
                <?php /*?><li><a href="index.php?main_page=track">TRACKING</a></li><?php */?>
				<?php if($_SESSION['customer_id']) { ?>
		<li><a href="<?php echo zen_href_link(FILENAME_LOGOFF, '', 'SSL');?>" rel="nofollow"><?php echo HEADER_TITLE_LOGOFF;?></a></li>
        <li><a href="<?php echo zen_href_link(FILENAME_ACCOUNT, '', 'SSL');?>" rel="nofollow"><?php echo HEADER_TITLE_MY_ACCOUNT;?></a></li>
        
    <?php } else { ?>
	     <li><a href="<?php echo zen_href_link(FILENAME_LOGIN, '', 'SSL');?>" rel="nofollow"><?php echo HEADER_TITLE_LOGIN;?></a></li>
	     <li><a href="<?php echo zen_href_link(FILENAME_CREATE_ACCOUNT, '', 'SSL');?>" rel="nofollow"><?php echo HEADER_TITLE_CREATE_ACCOUNT;?></a></li>
         
    <?php } ?>
                
           </ol>
          </div>        
        </div>
    </div>
</section>
<header id="yee_header">
	<div class="container">
		<div class="row header-top">
			<div class="span2 logo">
                <a title="" href="./" class="logo-img">
				  <img alt="Ray Ban Offical Discount Online Shop" src="<?php echo DIR_WS_TEMPLATE; ?>images/yeeze/logo.jpg">
				</a>
            </div>
		</div>
		<div class="span8">
		<nav id="menu1" class="clearfix">
			<ul class="sf-js-enabled sf-shadow">
				<li><a href="./"><span class="name">Home</span></a></li>
                <?php 
					$categories=new category_tree;
		
					$row = $categories->zen_category_sub(0);
					$y = 0;
					foreach($row as $key=>$currow)
					//for($i=0;$i<sizeof($row);$i++)
					{
						if($key!="") {
						echo '<li id="'.$currow['name'].'" class="level0 nav-1 parent" onmouseout="toggleMenu(this,0)" onmouseover="toggleMenu(this,1)"><a href="' . zen_href_link(FILENAME_DEFAULT, 'cPath='.$currow['path']) . '" rel="dropmenu'.$y.'">'.$currow['name'].'<span id="pg" class="pgonow"></span></a><ul class="level0 ul-2">';
						
					
					$subrows = $categories->zen_category_sub($key);
					
					foreach($subrows as $skey=>$scurrow)	//		for($i=0;$i<sizeof($row);$i++)
					{   
						
						if($skey!="") {
						
						
						echo '<li id="" class="level1 nav-1-1 first"><a href="' . zen_href_link(FILENAME_DEFAULT, 'cPath='.$scurrow['path']) . '" rel="dropmenu'.$y.'">'.$scurrow['name'].'</a></li>';
				
						
			}}
			
			echo '</ul></li>';
							
					}
						
					}
		    ?>
                                       
				<li><a rel="nofollow" href="<?php echo zen_href_link(FILENAME_CONTACT_US);?>"><span class="name">Contacts</span></a></li> 
			</ul>
		</nav>
		</div>
		<div class="span2 social-container" style="margin-top:13px;">
			<ul class="topul">
				<li style="margin-left:-30px;" class="shoppingbag">
					<a class="shopping-bag" rel="nofollow" href="<?php echo zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL');?>">
						<span></span>
					
					</a>
				</li>
				<li class="shoppingbag">
				    <a rel="nofollow" href="<?php echo zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL');?>" style="color: rgb(204, 204, 204);">
					<span>BAG</span></a></li>
				<li style="margin-left:5px;" class="shoppingbag">
					<a class="shopping-bag" href="<?php echo zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL');?>" style="color: rgb(204, 204, 204);">
					(<?php echo $_SESSION['cart']->count_contents();?>)
					</a></li>
				<li style="margin-left:25px;"><span class="topSearch">SEARCH</span></li>
			</ul>
		</div>
	</div>
</header>
<div class="bodysearch">
  <div class="bodysearchcontent">
      <div class="pull-right searchclose">x</div>
      
	  <form method="get" action="index.php?main_page=advanced_search_result" name="quick_find_header" id="search_mini_form">
            <input type="hidden" value="advanced_search_result" name="main_page">
            <input type="hidden" value="1" name="search_in_description">
            <input type="text" onblur="if (this.value == '') this.value = 'Search Your Favorite Ray Ban Here ...';" onfocus="if (this.value == 'Search Your Favorite Ray Ban Here ...') this.value = '';" value="Search Your Favorite Ray Ban Here ..." style="width: -30px" class="s_txt"  maxlength="120" size="6" name="keyword">
           
            <!--<input type="submit" class="s_but" value="">-->
			<button type="submit" class="btn " style="display:none;"><i class="icon-search-form"></i></button>
            
      </form>
	  </div>
</div>
<div style="width:100%;margin:0 auto;padding:0">
<img width="100%" src="<?php echo DIR_WS_TEMPLATE; ?>images/yeeze/rb.jpg" />
</div>

<div style="clear:both"></div>





<?php if (EZPAGES_STATUS_HEADER == '1' or (EZPAGES_STATUS_HEADER == '2' and (strstr(EXCLUDE_ADMIN_IP_FOR_MAINTENANCE, $_SERVER['REMOTE_ADDR'])))) { ?>
<!--bof-header ezpage links-->
<?php //require($template->get_template_dir('tpl_ezpages_bar_header.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_ezpages_bar_header.php'); ?>
<!--eof-header ezpage links-->
<?php } ?>

<?php } ?>