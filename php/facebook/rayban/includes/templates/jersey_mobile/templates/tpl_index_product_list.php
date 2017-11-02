<?php

/**

 * Page Template

 *

 * Loaded by main_page=index<br />

 * Displays product-listing when a particular category/subcategory is selected for browsing

 *

 * @package templateSystem

 * @copyright Copyright 2003-2010 Zen Cart Development Team

 * @copyright Portions Copyright 2003 osCommerce

 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0

 * @version $Id: tpl_index_product_list.php 15589 2010-02-27 15:03:49Z ajeh $

 */

?>

<div class="rightWarapper" id="indexProductList">

<h1 id="product-list-heading" class="subtitle"><?php echo $breadcrumb->last(); ?></h1>

<?php

// categories_description

    if ($current_categories_description != '') {

?>

<div id="indexProductListCatDescription" class="content"><?php echo $current_categories_description;  ?></div>

<?php } // categories_description ?>


<br class="clearBoth" />
<?php 
$query_category='';
$query_category .='select c.categories_id,cd.categories_name from '.TABLE_CATEGORIES.' c, '.TABLE_CATEGORIES_DESCRIPTION.' cd where c.categories_id=cd.categories_id and cd.language_id='.(int)$_SESSION['languages_id'].' and c.parent_id=0';
$conteent_category=$db->Execute($query_category);
if($conteent_category->RecordCount() > 0){
	$i=0;

	 $i++;
?>
<div>
<div id="Categories" style="padding-left:10px">
<?php
$query_sub_c='';
$query_sub_c .='select c.categories_id,cd.categories_name from '.TABLE_CATEGORIES.' c, '.TABLE_CATEGORIES_DESCRIPTION.' cd where c.categories_id=cd.categories_id  and cd.language_id='.(int)$_SESSION['languages_id'].' and c.parent_id='.$current_category_id;
$content_sub_c=$db->Execute($query_sub_c);
if($content_sub_c->RecordCount() > 0){
  while (!$content_sub_c->EOF) {
  ?>
   <div class="categoryListBoxContents"><a href="<?php echo zen_href_link('index','cPath='.$content_sub_c->fields['categories_id']);?>"><?php echo $content_sub_c->fields['categories_name'];?></a></div>
  <?php
   $content_sub_c->MoveNext();
  }
  }
?>
</div></div>
<?php
}
?>
<style>

#productListing {
    float: left;
	}
</style>


<?php

/**

 * require the code for listing products

 */

 require($template->get_template_dir('tpl_modules_product_listing.php', DIR_WS_TEMPLATE, $current_page_base,'templates'). '/' . 'tpl_modules_product_listing.php');

?>

<?php

//// bof: categories error

if ($error_categories==true) {

  // verify lost category and reset category

  $check_category = $db->Execute("select categories_id from " . TABLE_CATEGORIES . " where categories_id='" . $cPath . "'");

  if ($check_category->RecordCount() == 0) {

    $new_products_category_id = '0';

    $cPath= '';

  }

?>

<?php require($template->get_template_dir('tpl_modules_whats_new.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_whats_new.php'); ?>

<?php } //// eof: categories error ?>

</div>

