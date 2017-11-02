<?php

/**

 * Common Template - tpl_footer.php

 *

 * this file can be copied to /templates/your_template_dir/pagename<br />

 * example: to override the privacy page<br />

 * make a directory /templates/my_template/privacy<br />

 * copy /templates/templates_defaults/common/tpl_footer.php to /templates/my_template/privacy/tpl_footer.php<br />

 * to override the global settings and turn off the footer un-comment the following line:<br />

 * <br />

 * $flag_disable_footer = true;<br />

 *

 * @package templateSystem

 * @copyright Copyright 2003-2010 Zen Cart Development Team

 * @copyright Portions Copyright 2003 osCommerce

 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0

 * @version $Id: tpl_footer.php 15511 2010-02-18 07:19:44Z drbyte $

 */

require(DIR_WS_MODULES . zen_get_module_directory('footer.php'));

?>
<?php

if (!isset($flag_disable_footer) || !$flag_disable_footer) {

?>



<div id="navSuppWrapper">
<div id="navSupp">
	<ul>
		<a class="footerlink" href="<?php echo zen_href_link(FILENAME_ABOUT);?>"><li>About Us</li></a>
		<a class="footerlink" href="<?php echo zen_href_link(FILENAME_CONTACT_US);?>"><li>Contact Us</li></a>
		<a class="footerlink" href="<?php echo zen_href_link(FILENAME_SHIPPING);?>"><li>Shipping Information</li></a>
		<a class="footerlink" href="<?php echo zen_href_link(FILENAME_CONDITIONS);?>"><li>Conditions of Use</li></a>
		<a class="footerlink" href="<?php echo zen_href_link(FILENAME_EZPAGES, 'id=2');?>"><li>Privacy Notice</li></a>
		<a class="footerlink" href="<?php echo zen_href_link(FILENAME_ACCOUNT, '', 'SSL');?>"><li>My Account</li></a>
	</ul>
</div>
</div>




<?php  if($_GET['main_page'] != 'shopping_cart') { ?>
<!--bof-navigation display -->
<!--底部语言选择类开始-->

<div class="footer_menu">

  <!--<div class="languages-wrapper">
<div class="top-item">
				<div class="top-item-english">Condiciones de Uso</div>
				 <a id="menu_more" class="item-icon" href="#">&nbsp;</a>
                 <div id="menu_more_popup" class="popup popup-win hidden pull-right">
                 <img class="close-pic float-right" src="<?php echo ($template->get_template_dir('', DIR_WS_TEMPLATE, $current_page_base,'images'). '/close_pic.gif'); ?>" alt="close" />
					<?php if (EZPAGES_STATUS_HEADER == '1' or (EZPAGES_STATUS_HEADER == '2' and (strstr(EXCLUDE_ADMIN_IP_FOR_MAINTENANCE, $_SERVER['REMOTE_ADDR'])))) { ?>
					<?php require($template->get_template_dir('tpl_ezpages_bar_header.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_ezpages_bar_header.php'); ?>
   					<hr />
					<?php } ?>
					<?php if ($_SESSION['customer_id']) { ?>
				    <a href="<?php echo zen_href_link(FILENAME_ACCOUNT, '', 'SSL'); ?>"><?php echo HEADER_TITLE_MY_ACCOUNT; ?></a><br />
				    <a href="<?php echo zen_href_link(FILENAME_LOGOFF, '', 'SSL'); ?>"><?php echo HEADER_TITLE_LOGOFF; ?></a>
					<?php
				    } else {
			        if (STORE_STATUS == '0') {
					?>
				    <a href="<?php echo zen_href_link(FILENAME_LOGIN, '', 'SSL'); ?>"><?php echo HEADER_TITLE_LOGIN; ?></a>
					<?php } } ?>
                 </div>
                </div>


<?php
    $languges_object = new language;
    if (count ($languges_object->catalog_languages) > 1){
?>
            	<div class="languages">
                <?php require($template->get_template_dir('tpl_languages_header.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_languages_header.php'); ?>
                <div id="langPopup" class="popup popup-win hidden">
                <img class="close-pic float-right" src="<?php echo ($template->get_template_dir('', DIR_WS_TEMPLATE, $current_page_base,'images'). '/close_pic.gif'); ?>" alt="close" />
					<?php require(DIR_WS_MODULES . zen_get_module_directory('header_languages.php')); ?>
                </div>
                </div>
<?php
   }
?>
<?php
    if (isset($currencies) && is_object($currencies) && count ($currencies->currencies) > 1){
?>
				<div class="currency">
                <?php require($template->get_template_dir('tpl_currency_header.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_currency_header.php'); ?>

                <div id="currPopup" class="popup popup-win-currPopup hidden">
                <img class="close-pic float-right" src="<?php echo ($template->get_template_dir('', DIR_WS_TEMPLATE, $current_page_base,'images'). '/close_pic.gif'); ?>" alt="close" />
                  <ul class="list-popup">
                    <?php foreach ($currencies_array_popup as $k=>$v) { ?>
                    <li><a href="<?php if ((stripos($_SERVER['REQUEST_URI'], '.php')) !== false ) echo htmlspecialchars($_SERVER['REQUEST_URI']); else echo '?'; ?><?php echo '&amp;currency='.$k; ?>"><?php echo $v; ?></a></li>
                    <?php }?>
                 </ul>
                </div>
                </div>
<?php
    }
?>
<div class="clear"></div>
</div>-->


</div>
<!--底部语言选择类结束-->



<?php } ?>
<div class="clearer"></div>


<?php

} // flag_disable_footer

?>

<div id="bcover">
  <table style="margin:10px auto;" width="95%">
  <tbody><tr>
    <td><input class="index-search-input" placeholder="Search Your Favourite Sunglasses" id="sword"></td>
    <td width="50px"><input class="cssButton index-search-btn" value="Go" id="skey" type="button"></td>
  </tr>
</tbody></table>
<div id="header-menu" style=" border-top:1px solid #2e2e2e;">
  <ul>
  
  		<?php 
			$categories=new category_tree;

			$row = $categories->zen_category_sub(0);
			$y = 0;
			foreach($row as $key=>$currow)
	 		//for($i=0;$i<sizeof($row);$i++)
			{
				if($key!="") {
				echo '<a href="'.zen_href_link(FILENAME_DEFAULT, 'cPath='.$currow['path']).'">' .'<li class="submenu">' .$currow['name']. '</li></a>'; 				
			    }
			}
		?>

    <a rel="nofollow" href="<?php echo zen_href_link(FILENAME_CONDITIONS);?>"><li>CONDITIONS</li></a>
    <a rel="nofollow" href="<?php echo zen_href_link(FILENAME_ABOUT);?>"><li>ABOUT US</li></a>
    <a rel="nofollow" href="<?php echo zen_href_link(FILENAME_ACCOUNT, '', 'SSL');?>"><li>MY ACCOUNT</li></a>
</ul>
</div>
<br style="clear:both;">


<div class="mobile-index">
<ul>
  <li><a href="./" class="logo-img"><img height="40px" alt="YEEZY 350 Official Discount Online Shop" src="<?php echo DIR_WS_TEMPLATE; ?>/images/yeeze/logo.png"></a></li>
  <li id="top-menu"><a class="logo-img"><img height="40px" src="<?php echo DIR_WS_TEMPLATE; ?>/images/yeeze/menu.png"></a></li>
  <li><a href="<?php echo zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL');?>" class="logo-img"><img height="40px" src="<?php echo DIR_WS_TEMPLATE; ?>/images/yeeze/shoppingbag.png"></a></li>
  <?php if($_SESSION['customer_id']) { ?>
		<li><a class="logo-img" href="<?php echo zen_href_link(FILENAME_LOGOFF, '', 'SSL');?>" rel="nofollow"><img height="40px" src="<?php echo DIR_WS_TEMPLATE; ?>/images/yeeze/me.png"></a></li>
  <?php } else { ?>
	     <li><a class="logo-img" href="<?php echo zen_href_link(FILENAME_LOGIN, '', 'SSL');?>" rel="nofollow"><img height="40px" src="<?php echo DIR_WS_TEMPLATE; ?>/images/yeeze/me.png"></a></li>
    <?php } ?>
</ul>
</div>



<div style="display:none">
<?php echo FOOTER_CNZZ_LA;?>
</div>
</div>


