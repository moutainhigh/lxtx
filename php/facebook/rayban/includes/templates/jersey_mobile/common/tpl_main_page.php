<?php

/**

* Template designed by 12leaves.com

* 12leaves.com - Free ecommerce templates and design services

*

* Common Template

* 

* @package languageDefines

* @copyright Copyright 2009-2010 12leaves.com

* @copyright Copyright 2003-2007 Zen Cart Development Team

* @copyright Portions Copyright 2003 osCommerce

* @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0

* @version $Id: tpl_main_page.php 7085 2007-09-22 04:56:31Z ajeh $

*/



// the following IF statement can be duplicated/modified as needed to set additional flags

  if (in_array($current_page_base,explode(",",'list_pages_to_skip_all_right_sideboxes_on_here,separated_by_commas,and_no_spaces')) ) {

    $flag_disable_right = true;

  }



  $header_template = 'tpl_header.php';

  $footer_template = 'tpl_footer.php';

  $left_column_file = 'column_left.php';

  $right_column_file = 'column_right.php';

  $body_id = ($this_is_home_page) ? 'indexHome' : str_replace('_', '', $_GET['main_page']);





?>

<body id="<?php echo $body_id . 'Body'; ?>"<?php if($zv_onload !='') echo ' onload="'.$zv_onload.'"'; ?>>

<?php //if (defined('SKINS_PANEL')) { require ('demo_panel.php'); }?>

















<?php  require($template->get_template_dir('tpl_header.php',DIR_WS_TEMPLATE, $current_page_base,'common'). '/tpl_header.php');?>







<div class="center-upper-bg"></div>



<div class="middle">


<?php

 /**

  * prepares and displays header output

  *

  */

  if (CUSTOMERS_APPROVAL_AUTHORIZATION == 1 && CUSTOMERS_AUTHORIZATION_HEADER_OFF == 'true' and ($_SESSION['customers_authorization'] != 0 or $_SESSION['customer_id'] == '')) {

    $flag_disable_header = true;

  }

?>


<!--菜单下面广告开始
<div id="mm_banner">
    <div class="belowHeaderContentContainer">
         <a title="Sitewide Sale" name="#top_index" href="#" class="link1" id="shippingSliverLink1"></a>
         <img src="/includes/templates/jersey_mobile/images/checkout_50.jpg" class="headerBelowNavSliver" id="shippingSliverImage" style="visibility: visible;">
     </div>
</div>
<!--菜单下面广告结束-->

<?php 

	if ((DEFINE_BREADCRUMB_STATUS == '1' || DEFINE_BREADCRUMB_STATUS == '2') && ($this_is_home_page != '1')) { ?>

    <div id="navBreadCrumb"><div class="nav-bc"><?php echo $breadcrumb->trail(BREAD_CRUMBS_SEPARATOR); ?></div>

    <div class="clearBoth"></div></div>

<?php } ?>














<?php if ($messageStack->size('upload') > 0) echo $messageStack->output('upload'); ?>

<?php

 /**

  * prepares and displays center column

  *

  */

 require($body_code); ?>

<?php

  if (SHOW_BANNERS_GROUP_SET4 != '' && $banner = zen_banner_exists('dynamic', SHOW_BANNERS_GROUP_SET4)) {

    if ($banner->RecordCount() > 0) {

?>

<div id="bannerFour" class="banners"><?php echo zen_display_banner('static', $banner); ?></div>

<?php

    }

  }

?>

	

  <div class="clearBoth"></div>

  



</div>



<?php

  if (SHOW_BANNERS_GROUP_SET5 != '' && $banner = zen_banner_exists('dynamic', SHOW_BANNERS_GROUP_SET5)) {

    if ($banner->RecordCount() > 0) {

?>

<div id="bannerFive" class="banners"><?php echo zen_display_banner('static', $banner); ?></div>



<?php

    }

  }

?>

<?php





 /**

  * prepares and displays footer output

  *

  */

  if (CUSTOMERS_APPROVAL_AUTHORIZATION == 1 && CUSTOMERS_AUTHORIZATION_FOOTER_OFF == 'true' and ($_SESSION['customers_authorization'] != 0 or $_SESSION['customer_id'] == '')) {

    $flag_disable_footer = true;

  }

  require($template->get_template_dir('tpl_footer.php',DIR_WS_TEMPLATE, $current_page_base,'common'). '/tpl_footer.php');

?>

<?php

  if (DISPLAY_PAGE_PARSE_TIME == 'true') {

?>

<div class="smallText center">Parse Time: <?php echo $parse_time; ?> - Number of Queries: <?php echo $db->queryCount(); ?> - Query Time: <?php echo $db->queryTime(); ?></div>

<?php

  }

?>

<?php

  if (SHOW_BANNERS_GROUP_SET6 != '' && $banner = zen_banner_exists('dynamic', SHOW_BANNERS_GROUP_SET6)) {

    if ($banner->RecordCount() > 0) {

?>

<div id="bannerSix" class="banners"><?php echo zen_display_banner('static', $banner); ?></div>

<?php

    }

  }

?>

</body>