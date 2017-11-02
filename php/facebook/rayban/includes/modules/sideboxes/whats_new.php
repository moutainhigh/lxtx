<?php
/**
 * whats_new sidebox - displays a random "new" product
 *
 * @package templateSystem
 * @copyright Copyright 2003-2010 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: whats_new.php 15880 2010-04-11 16:24:30Z wilt $
 */

// display limits
// $display_limit = zen_get_products_new_timelimit();
if($this_is_home_page) {
	$cate_ary[1] = array();
	zen_get_subcategories($cate_ary[1], 1);
	$cate_ary[2] = array();
	zen_get_subcategories($cate_ary[2], 77);
	$cate_ary[3] = array();
	zen_get_subcategories($cate_ary[3], 149);

	mt_srand(microtime(true)*1000);
	$cates_ary = $cate_ary[mt_rand(1,3)];
	$cates = '('.implode(', ', $cates_ary).')';
	
	$random_whats_new_sidebox_product_query = "select distinct p.products_id, p.products_image, p.products_tax_class_id, pd.products_name,
                                p.products_date_added, p.products_price, p.products_type, p.master_categories_id
                           from " . TABLE_PRODUCTS . " p, " . TABLE_PRODUCTS_DESCRIPTION . " pd
                           where p.products_id = pd.products_id
                           and pd.language_id = '" . (int)$_SESSION['languages_id'] . "'
                           and p.products_status = 1 AND p.master_categories_id in $cates
						   ORDER BY p.products_date_added DESC, p.products_id DESC
						   LIMIT 0, ".MAX_RANDOM_SELECT_NEW;
	
	$random_whats_new_sidebox_product = $db->Execute($random_whats_new_sidebox_product_query);
} else {
  $display_limit = zen_get_new_date_range();
  $random_whats_new_sidebox_product_query = "select p.products_id, p.products_image, p.products_tax_class_id, p.products_price, pd.products_name,
                                              p.master_categories_id
                           from (" . TABLE_PRODUCTS . " p
                           left join " . TABLE_PRODUCTS_DESCRIPTION . " pd on p.products_id = pd.products_id )
                           where p.products_id = pd.products_id
                           and pd.language_id = '" . (int)$_SESSION['languages_id'] . "'
                           and p.products_status = 1 " . $display_limit;

//  $random_whats_new_sidebox_product = zen_random_select($random_whats_new_sidebox_product_query);
  $random_whats_new_sidebox_product = $db->ExecuteRandomMulti($random_whats_new_sidebox_product_query, MAX_RANDOM_SELECT_NEW);
}

  if ($this_is_home_page && $random_whats_new_sidebox_product->RecordCount() > 0 ) {
    require($template->get_template_dir('tpl_whats_new.php',DIR_WS_TEMPLATE, $current_page_base,'sideboxes'). '/tpl_whats_new.php');
    $title =  BOX_HEADING_WHATS_NEW;
    $title_link = FILENAME_PRODUCTS_NEW;
    require($template->get_template_dir($column_box_default, DIR_WS_TEMPLATE, $current_page_base,'common') . '/' . $column_box_default);
  }