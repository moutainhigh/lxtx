<?php
/**
 * categories sidebox - prepares content for the main categories sidebox
 *
 * @package templateSystem
 * @copyright Copyright 2003-2005 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: categories.php 2718 2005-12-28 06:42:39Z drbyte $
 */
$pcid = 0;
$cates = array();
$cates = zen_get_categories_diy($cates, $pcid, '', 1);

if(!empty($cates)) {
	// don't build a tree when no categories
	require($template->get_template_dir('tpl_categories_diy.php',DIR_WS_TEMPLATE, $current_page_base,'sideboxes'). '/tpl_categories_diy.php');

	$title = BOX_HEADING_CATEGORIES;
	$title_link = false;

	require($template->get_template_dir($column_box_default, DIR_WS_TEMPLATE, $current_page_base,'common') . '/' . $column_box_default);
}
?>