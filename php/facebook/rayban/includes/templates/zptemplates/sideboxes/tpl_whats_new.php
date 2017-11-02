<?php
/**
 * Side Box Template
 *
 * @package templateSystem
 * @copyright Copyright 2003-2007 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_whats_new.php 6128 2007-04-08 04:53:32Z birdbrain $
 */
  $content = "";
  $whats_new_box_counter = 0;
  while (!$random_whats_new_sidebox_product->EOF) {
    $whats_new_box_counter++;
    $whats_new_price = zen_get_products_display_price($random_whats_new_sidebox_product->fields['products_id']);
    $content .= '<div class="sideBoxContent clearBoth">';
    $content .= '<div class="whatsNewImgBox"><a href="' . zen_href_link(zen_get_info_page($random_whats_new_sidebox_product->fields['products_id']), 'cPath=' . zen_get_generated_category_path_rev($random_whats_new_sidebox_product->fields['master_categories_id']) . '&products_id=' . $random_whats_new_sidebox_product->fields['products_id']) . '">' . zen_image(DIR_WS_IMAGES . $random_whats_new_sidebox_product->fields['products_image'], $random_whats_new_sidebox_product->fields['products_name'], 50, 50) . '</a></div>';
    $content .= '<div class="whatsNewDetail"><div class="whatsNewName"><a href="' . zen_href_link(zen_get_info_page($random_whats_new_sidebox_product->fields['products_id']), 'cPath=' . zen_get_generated_category_path_rev($random_whats_new_sidebox_product->fields['master_categories_id']) . '&products_id=' . $random_whats_new_sidebox_product->fields['products_id']) . '">' . zen_trunc_string($random_whats_new_sidebox_product->fields['products_name'], 80, '...') . '</a></div>';
    $content .= '<div class="cprice">' . $whats_new_price . '</div></div>';
    $content .= '</div>';
	if($this_is_home_page) {
		$random_whats_new_sidebox_product->MoveNext();
	} else {
		$random_whats_new_sidebox_product->MoveNextRandom();
	}
  }
?>
