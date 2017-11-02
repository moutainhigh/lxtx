<?php
/**
 * new_products.php module
 *
 * @package modules
 * @copyright Copyright 2003-2008 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: new_products.php 8730 2008-06-28 01:31:22Z drbyte $
 */
if (!defined('IS_ADMIN_FLAG')) {
  die('Illegal Access');
}

// initialize vars
// initialize vars
$list_of_products = '';
$also_like_query = '';
$nested_cid = $db->Execute('select parent_id from '.TABLE_CATEGORIES.' where categories_id = (select master_categories_id from '.TABLE_PRODUCTS.' where products_id = '.(int)$_GET['products_id'].')')->fields['parent_id'];
$display_limit = zen_get_new_date_range();

if($nested_cid != 0) {
	$productsInCategory = zen_get_categories_products_list($nested_cid, false, true, 0, $display_limit);
  if (is_array($productsInCategory) && sizeof($productsInCategory) > 0) {
    // build products-list string to insert into SQL query
    foreach($productsInCategory as $key => $value) {
      $list_of_products .= $key . ', ';
    }
    $list_of_products = substr($list_of_products, 0, -2); // remove trailing comma
		$also_like_query = "select distinct p.products_id, p.products_image, pd.products_name,
														 p.products_price, p.master_categories_id
														 from " . TABLE_PRODUCTS . " p, " . TABLE_PRODUCTS_DESCRIPTION . " pd
														 where p.products_id = pd.products_id
														 and pd.language_id = '" . (int)$_SESSION['languages_id'] . "'
														 and   p.products_status = 1 and p.products_id in (" . $list_of_products . ")";
	}
} else {

  $also_like_query = "select distinct p.products_id, p.products_image, p.products_tax_class_id, pd.products_name,
                                p.products_date_added, p.products_price, p.products_type, p.master_categories_id
                           from " . TABLE_PRODUCTS . " p, " . TABLE_PRODUCTS_DESCRIPTION . " pd
                           where p.products_id = pd.products_id
                           and pd.language_id = '" . (int)$_SESSION['languages_id'] . "'
                           and   p.products_status = 1 " . $display_limit;
}

if ($also_like_query != '') $also_like = $db->ExecuteRandomMulti($also_like_query, 10);

$row = 0;
$col = 0;
$list_box_contents = array();
$title = '';

$num_products_count = ($also_like_query == '') ? 0 : $also_like->RecordCount();

// show only when 1 or more
if ($num_products_count > 0) {

  while (!$also_like->EOF) {
    $products_price = zen_get_products_display_price($also_like->fields['products_id']);
    if (!isset($productsInCategory[$also_like->fields['products_id']])) $productsInCategory[$also_like->fields['products_id']] = zen_get_generated_category_path_rev($also_like->fields['master_categories_id']);

		if($col == 0) { $col_style = 'first_col '; } elseif($col == 4) { $col_style = 'last_col '; } else { $col_style = ''; }
    $list_box_contents[$row][$col] = array('params' => 'class="'.$col_style.'prolist"',
    'text' => (($also_like->fields['products_image'] == '' and PRODUCTS_IMAGE_NO_IMAGE_STATUS == 0) ? '' : '<div class="pImgBox"><div class="pImg"><a href="' . zen_href_link(zen_get_info_page($also_like->fields['products_id']), 'cPath=' . $productsInCategory[$also_like->fields['products_id']] . '&products_id=' . $also_like->fields['products_id']) . '">' . zen_image(DIR_WS_IMAGES . $also_like->fields['products_image'], $also_like->fields['products_name'], SMALL_IMAGE_WIDTH, SMALL_IMAGE_WIDTH) . '</a></div></div>') . '<div class="pname"><a href="' . zen_href_link(zen_get_info_page($also_like->fields['products_id']), 'cPath=' . $productsInCategory[$also_like->fields['products_id']] . '&products_id=' . $also_like->fields['products_id']) . '">' . $also_like->fields['products_name'] . '</a></div>' . '<div class="cprice">' . $products_price . '</div>');

    $col ++;
		if($col > 4) {
			$col = 0;
			$row++;
		}
    $also_like->MoveNextRandom();
  }

  if ($also_like->RecordCount() > 0) {
      $title = '<h3 class="alsoLikeHeading">You May Also Like</h3>';
  }
}
?>