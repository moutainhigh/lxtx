<?php
/**
 * Common Template - tpl_main_page.php
 *
 * Governs the overall layout of an entire page<br />
 * Normally consisting of a header, left side column. center column. right side column and footer<br />
 * For customizing, this file can be copied to /templates/your_template_dir/pagename<br />
 * example: to override the privacy page<br />
 * - make a directory /templates/my_template/privacy<br />
 * - copy /templates/templates_defaults/common/tpl_main_page.php to /templates/my_template/privacy/tpl_main_page.php<br />
 * <br />
 * to override the global settings and turn off columns un-comment the lines below for the correct column to turn off<br />
 * to turn off the header and/or footer uncomment the lines below<br />
 * Note: header can be disabled in the tpl_header.php<br />
 * Note: footer can be disabled in the tpl_footer.php<br />
 * <br />
 * $flag_disable_header = true;<br />
 * $flag_disable_left = true;<br />
 * $flag_disable_right = true;<br />
 * $flag_disable_footer = true;<br />
 * <br />
 * // example to not display right column on main page when Always Show Categories is OFF<br />
 * <br />
 * if ($current_page_base == 'index' and $cPath == '') {<br />
 *  $flag_disable_right = true;<br />
 * }<br />
 * <br />
 * example to not display right column on main page when Always Show Categories is ON and set to categories_id 3<br />
 * <br />
 * if ($current_page_base == 'index' and $cPath == '' or $cPath == '3') {<br />
 *  $flag_disable_right = true;<br />
 * }<br />
 *
 * @package templateSystem
 * @copyright Copyright 2003-2007 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_main_page.php 7085 2007-09-22 04:56:31Z ajeh $
 */

// the following IF statement can be duplicated/modified as needed to set additional flags
  if (in_array($current_page_base,explode(",",'list_pages_to_skip_all_right_sideboxes_on_here,separated_by_commas,and_no_spaces')) ) {
    $flag_disable_right = true;
  }
  $i=0;
  $header_template = 'tpl_header.php';
  $footer_template = 'tpl_footer.php';
  $left_column_file = 'column_left.php';
  $right_column_file = 'column_right.php';
  $body_id = ($this_is_home_page) ? 'indexHome' : str_replace('_', '', $_GET['main_page'].' '.$breadcrumb->trailss(BREAD_CRUMBS_SEPARATOR).' ');
  
?>

<body id="<?php echo $body_id . 'Body'; ?>"<?php if($zv_onload !='') echo ' onload="'.$zv_onload.'"'; ?>>

<div class="body_box <?php echo $breadcrumb->trailss(BREAD_CRUMBS_SEPARATOR);?>">

<?php
  require($template->get_template_dir('tpl_header.php',DIR_WS_TEMPLATE, $current_page_base,'common'). '/tpl_header.php');

  
  ?>
<?php
$left_flag = 1;
$right_flag = 1;

// global disable of column_left
if (in_array($_GET['main_page'], array('shopping_cart', 'quick_shopping')) || COLUMN_LEFT_STATUS == 0 || (CUSTOMERS_APPROVAL == '1' and $_SESSION['customer_id'] == '') || (CUSTOMERS_APPROVAL_AUTHORIZATION == 1 && CUSTOMERS_AUTHORIZATION_COLUMN_LEFT_OFF == 'true' and ($_SESSION['customers_authorization'] != 0 or $_SESSION['customer_id'] == ''))) {
	$left_flag = 0;
}
// global disable of column_right
if (COLUMN_RIGHT_STATUS == 0 || (CUSTOMERS_APPROVAL == '1' and $_SESSION['customer_id'] == '') || (CUSTOMERS_APPROVAL_AUTHORIZATION == 1 && CUSTOMERS_AUTHORIZATION_COLUMN_RIGHT_OFF == 'true' and ($_SESSION['customers_authorization'] != 0 or $_SESSION['customer_id'] == ''))) {
	$right_flag = 0;
}

if($left_flag) {
	if($right_flag) {
		$main_style = 'col3_layout';
		$left_style = 'col3_left';
		$right_style = 'col3_right';
	} else {
		$main_style = 'l2col_layout';
		$left_style = 'l2col_left';
		$right_style = '';
	}
} else {
	if($right_flag) {
		$main_style = 'r2col_layout';
		$left_style = '';
		$right_style = 'r2col_right';
	} else {
		$main_style = 'col1_layout';
		$left_style = '';
		$right_style = '';
	}
}
?>
</div>
<div class="middle <?php echo $breadcrumb->trailss(BREAD_CRUMBS_SEPARATOR); ?>">
  
  <div class="main <?php if(!$this_is_home_page ){echo 'main_pro';}?>">
  
  <?php if ($this_is_home_page) { ?>
  

<?php } ?>


<?php
if($left_flag && !$this_is_home_page ) { // display left column
?>
	<?php require(DIR_WS_MODULES . zen_get_module_directory('column_left.php')); ?>
    
<?php
} // eof display left column
?>

<?php // display main column ?>


<?php if ($messageStack->size('upload') > 0) { ?>
<?php echo $messageStack->output('upload'); ?>
<?php } ?>


<?php //prepares and displays center column

 require($body_code); ?>


<?php // eof display main column ?>

<?php
if($right_flag) { // display right column
?>
<div id="rightCol" class="<?php echo $right_style;?>" style="width: <?php echo COLUMN_WIDTH_RIGHT; ?>">
	<div id="rightWrapper" style="width: <?php echo BOX_WIDTH_RIGHT; ?>">
	<?php require(DIR_WS_MODULES . zen_get_module_directory('column_right.php')); ?>
  </div>
</div>
<?php
} // eof display right column
?>
</div>

</div>
<div style="clear:both"></div>
<?php
  //prepares and displays footer output
require($template->get_template_dir('tpl_footer.php',DIR_WS_TEMPLATE, $current_page_base,'common'). '/tpl_footer.php');
?>

<?php
  if (DISPLAY_PAGE_PARSE_TIME == 'true') {
?>
<div class="smallText center">Parse Time: <?php echo $parse_time; ?> - Number of Queries: <?php echo $db->queryCount(); ?> - Query Time: <?php echo $db->queryTime(); ?></div>
<?php
  }
?>


</body>