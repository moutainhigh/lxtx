<?php
/**
 * Page Template
 *
 * Main index page<br />
 * Displays greetings, welcome text (define-page content), and various centerboxes depending on switch settings in Admin<br />
 * Centerboxes are called as necessary
 *
 * @package templateSystem
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_index_default.php 3464 2006-04-19 00:07:26Z ajeh $
 */
?>

<?php //getThisClassProductsHtml(14,12);?>

<?php //getThisClassProductsHtml(17,12);?>


<?php //getThisClassProductsHtml(18,12);?>

<?php //getThisClassProductsHtml(38,12);?>

<?php
$show_display_category = $db->Execute(SQL_SHOW_PRODUCT_INFO_MAIN);
while (!$show_display_category->EOF) {
?>
<?php if ($show_display_category->fields['configuration_key'] == 'SHOW_PRODUCT_INFO_MAIN_FEATURED_PRODUCTS') {
	//display the Featured Products Center Box
	require($template->get_template_dir('tpl_modules_featured_products.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_featured_products.php'); 
} ?>
<?php if ($show_display_category->fields['configuration_key'] == 'SHOW_PRODUCT_INFO_MAIN_SPECIALS_PRODUCTS') {
	//display the Special Products Center Box
	require($template->get_template_dir('tpl_modules_specials_default.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_specials_default.php');
} ?>
<?php if ($show_display_category->fields['configuration_key'] == 'SHOW_PRODUCT_INFO_MAIN_NEW_PRODUCTS') {
	//display the New Products Center Box
	require($template->get_template_dir('tpl_modules_whats_new.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_whats_new.php');
} ?>
<?php if ($show_display_category->fields['configuration_key'] == 'SHOW_PRODUCT_INFO_MAIN_UPCOMING') {
	//display the Upcoming Products Center Box
	include(DIR_WS_MODULES . zen_get_module_directory(FILENAME_UPCOMING_PRODUCTS));
} ?>
<?php
	$show_display_category->MoveNext();
} // !EOF
?>
<!--<div id="bestSellers" class="centerBoxWrapper">
	<h2 class="centerBoxHeading">Best Sellers</h2>
	<div class="bestSellersWrapper clearBoth">
// <?php
// $sql = "SELECT p.products_id as pid, pd.products_name as pname FROM ".TABLE_PRODUCTS." p, ".TABLE_PRODUCTS_DESCRIPTION." pd
		// WHERE p.products_status = 1 AND p.products_id = pd.products_id
		// AND pd.language_id = '".(int)$_SESSION['languages_id']."'
		// ORDER BY p.products_ordered DESC, p.products_id DESC LIMIT 0, 6";
// $best = $db->Execute($sql);
// while(!$best->EOF) {
	// $best_price = zen_get_products_display_price($best->fields['pid']);
	// $best_img = zen_get_products_image($best->fields['pid'], 90, 90);
	
	// echo '		<div class="bestSellersBox">'."\n";
	// echo '		<div class="bestSellersImg"><a href="'.zen_href_link(FILENAME_PRODUCT_INFO, 'products_id='.$best->fields['pid']).'">'.$best_img."</a></div>\n";
	// echo '		<div class="bestSellersDetail"><div class="bestSellersName"><a href="'.zen_href_link(FILENAME_PRODUCT_INFO, 'products_id='.$best->fields['pid']).'">'.$best->fields['pname'].'</a></div><div class="bestSellersRating">'.zen_image(DIR_WS_TEMPLATE.'images/best_rating.png').'</div><div class="bestSellersPrice">'.$best_price.'</div></div>'."\n";
	// echo "		</div>\n";
	
	// $best->MoveNext();
// }
// ?>
	</div>
</div>-->
