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
<div id="hero">
	<a href="cat-eye-sunglasses-c-3.html"><img width="100%" src="<?php echo DIR_WS_TEMPLATE; ?>images/sunglass/banner1.jpg" /></a>
</div>





<?php /*?><div id="indexright">
	<div class="" style="width: 822px; margin: 0px auto; float:left;">
		<div class="banner">
			<div id="banner">
				<div class="banners">
              <div class="slides" id="faded">
                <div>
                  <div> 
                    <!-- BOF- BANNER #1 display -->
                    <div id="bannerOne"><a href="italie-c-39_51.html"><img src="<?php echo DIR_WS_TEMPLATE; ?>images/nike/bbox1.jpg" border="0" /></a></div>
                    <!-- EOF- BANNER #1 display --> 
                  </div>
                <div> 
                    <!-- BOF- BANNER #2 display -->
                    <div id="bannerTwo"><a href="france-c-39_40.html"><img src="<?php echo DIR_WS_TEMPLATE; ?>images/nike/bbox2.jpg" border="0" /></a></div>
                    <!-- EOF- BANNER #2 display --> 
                  </div>
				<div> 
                    <!-- BOF- BANNER #3 display -->
                    <div id="bannerThree"><a href="euro-2016-c-39.html"><img src="<?php echo DIR_WS_TEMPLATE; ?>images/nike/bbox3.jpg" border="0" /></a></div>
                    <!-- EOF- BANNER #3 display --> 
                  </div>
				<div> 
                    <!-- BOF- BANNER #4 display -->
                    <div id="bannerfour"><a href="air-max-95-c-63_70.html"><img src="<?php echo DIR_WS_TEMPLATE; ?>images/nike/banner_3.jpg" border="0" /></a></div>
                    <!-- EOF- BANNER #4 display --> 
                  </div>
				<div> 
                    <!-- BOF- BANNER #5 display -->
                    <div id="bannerfour"><a href="yeezy-c-4_13.html"><img src="<?php echo DIR_WS_TEMPLATE; ?>images/nike/banner_4.jpg" border="0" /></a></div>
                    <!-- EOF- BANNER #5 display --> 
                  </div>
                  
                </div>
			  <span class="corner1"></span> <span class="corner2"></span>
                
              </div>
            </div>
       
            
            <script>
				$(function(){
				$("#faded").faded({
				speed: 1000,
				bigtarget: false,
				autoplay: 5000,
				autorestart: false,
				autopagination:false,
				crossfade:false
				});
			});
			</script> 
			  
			
			</div>
            
             <ul id="pagination">
                <li class="one"><span></span><a href="#" rel="0"></a></li>
                <li class="two"><span></span><a href="#" rel="1"></a></li>
				<li class="three"><span></span><a href="#" rel="2"></a></li>
             </ul>
            
		</div>
        
        <div id="wss-home-banners-mid">
            <div id="ad1" class="wss-home-mid"> 
                  <a href="dortmund-c-67_69.html" target="_top" style="opacity: 1;">
                  <img border="0" alt="maillots de Paris Saint-Germain 2016" src="<?php echo DIR_WS_TEMPLATE; ?>images/nike/L_bbox1.jpg">
                  </a>
            </div>
            <div id="ad2" class="wss-home-mid">
                  <a href="maillot-psg-c-85_156.html" target="_top" style="opacity: 1;">
                  <img border="0" alt="maillots de FC Barcelona 2016" src="<?php echo DIR_WS_TEMPLATE; ?>images/nike/L_bbox2.jpg">
                  </a>  
            </div>
        </div>
        
	</div>
	<br class="clearBoth">


<div style="clear:both"></div>
<?php getThisClassProductsHtml(500,4);?>

<?php getThisClassProductsHtml(501,4);?>


<?php getThisClassProductsHtml(465,4);?>

<?php getThisClassProductsHtml(38,12);?>
<?php */?>


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

<?php /*?><div id="whatsNew" class="centerBoxWrapper" style="width:1020px; margin:20px auto; padding:0;">

<h2 class="centerBoxHeading">New Products</h2>
<?php 
$pd_id ='267,829,202,205,97,207,208,209,124,213,212,214,215,216,217,218';
 $p_explode = explode(',',$pd_id );

 for($iid=0;$iid<16; $iid++)
   {
?><div class="centerBoxContentsNew centeredContent back">
<a href="<?php echo zen_href_link('product_info' ,'cPath='.zen_get_product_path($p_explode[$iid]).'&products_id='.$p_explode[$iid]);?>" target="_top"><?php echo zen_get_products_image($p_explode[$iid],'200','200');?></a><div class="item_name"><a href="<?php echo zen_href_link('product_info' ,'cPath='.zen_get_product_path($p_explode[$iid]).'&products_id='.$p_explode[$iid]);?>" target="_top"><?php echo zen_get_products_name($p_explode[$iid]);?></a></div><div class="price-box"><?php echo zen_get_products_display_price($p_explode[$iid]);?></div>
</div>
<?php if($iid%4==3) { ?><br class="clearBoth"> <?php }?>
 <?php }?>
</div>
	<?php //require($template->get_template_dir('/tpl_random_default.php',DIR_WS_TEMPLATE, $current_page_base,'templates').'/tpl_random_default.php'); ?>
</div><?php */?>