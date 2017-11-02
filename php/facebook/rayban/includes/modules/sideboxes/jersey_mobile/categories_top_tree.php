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

    $main_category_tree = new category_tree;
    $row = 0;
    $box_categories_array = array();
	$column_box_default = 'tpl_box_default.php';

// don't build a tree when no categories
    $check_categories = $db->Execute("select categories_id from " . TABLE_CATEGORIES . " where categories_status=1 limit 1");
    if ($check_categories->RecordCount() > 0) {
      $box_categories_array = $main_category_tree->zen_category_tree();
    }
    require($template->get_template_dir('tpl_categories_top_tree.php', DIR_WS_TEMPLATE, $current_page_base,'sideboxes') . '/tpl_categories_top_tree.php');


    $title = BOX_HEADING_CATEGORIES;
    $title_link = false;


	$column_left_display= $db->Execute("select layout_box_name from " . TABLE_LAYOUT_BOXES . " where layout_box_location = 0 and layout_box_status= '1' and layout_template ='" . $template_dir . "'" . ' order by layout_box_sort_order');

	if (zen_get_box_id($column_left_display->fields['layout_box_name']) == "categoriestoptree")	{
	    require($template->get_template_dir($column_box_default, DIR_WS_TEMPLATE, $current_page_base,'common') . '/' . $column_box_default);
	}
?>