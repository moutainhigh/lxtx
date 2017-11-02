<?php
/**
 * Page Template
 *
 * @package templateSystem
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_product_reviews_write_default.php 4365 2006-09-03 19:16:58Z wilt $
 */
?>
<div class="centerColumn" id="reviewsWrite">
<!--bof Main Product Image -->
      <?php
        if (zen_not_null($products_image)) {
    ?>
  <div id="reviewWriteMainImage" class="centeredContent back"><?php
        	/**
 * display the main product image
        	 */
   require($template->get_template_dir('/tpl_modules_main_product_image.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_modules_main_product_image.php'); ?>
</div>
<?php
  //} else {
  ?>

<?php
        }
      ?>
<!--eof Main Product Image-->
<div class="forward">
<div id="reviewsWriteProductPageLink" class="buttonRow"><?php echo '<a href="' . zen_href_link(zen_get_info_page($_GET['products_id']), zen_get_all_get_params()) . '">' . zen_image_button(BUTTON_IMAGE_GOTO_PROD_DETAILS , BUTTON_GOTO_PROD_DETAILS_ALT) . '</a>'; ?></div>
<div class="buttonRow"><?php echo '<a href="' . zen_href_link(FILENAME_REVIEWS) . '">' . zen_image_button(BUTTON_IMAGE_REVIEWS, BUTTON_REVIEWS_ALT) . '</a>'; ?></div>
</div>

<h1 id="reviewsWriteHeading"><?php echo $products_name . $products_model; ?></h1>

<h2 id="reviewsWritePrice"><?php echo $products_price; ?></h2>

<h3 id="reviewsWriteReviewer" class=""><?php echo SUB_TITLE_FROM, zen_output_string_protected($customer->fields['customers_firstname'] . ' ' . $customer->fields['customers_lastname']); ?></h3>
<br class="clearBoth" />

<?php if ($messageStack->size('review_text') > 0) echo $messageStack->output('review_text'); ?>

<?php echo zen_draw_form('product_reviews_write', zen_href_link(FILENAME_PRODUCT_REVIEWS_WRITE, 'action=process&products_id=' . $_GET['products_id'], 'SSL'), 'post', 'onsubmit="return checkForm(product_reviews_write);"'); ?>

<div id="reviewsWriteReviewsRate"><?php echo SUB_TITLE_RATING; ?></div>
<input type="hidden" id="rating" value="4" name="rating" />
<div class="ratingRow">Rating:
<span class="rating_active ratings"></span>
<span class="rating_active ratings"></span>
<span class="rating_active ratings"></span>
<span class="rating_active ratings"></span>
<span class="ratings"></span>
<script type="text/javascript">
$(this).ready(function() {
	$(".ratings").click(function() {
		$(".ratings").removeClass("rating_active");
		$(this).addClass("rating_active");
		$(this).prevAll().addClass("rating_active");
		$("#rating").attr("value", $(this).index()+1);
	});
});
</script>		
</div>
<label id="textAreaReviews" for="review-text"><?php echo SUB_TITLE_REVIEW; ?></label>
<?php echo zen_draw_textarea_field('review_text', 60, 5, '', 'id="review-text"'); ?>
    <div class="buttonRow forward"><?php echo zen_image_submit(BUTTON_IMAGE_SUBMIT, BUTTON_SUBMIT_ALT); ?></div>
<div id="reviewsWriteReviewsNotice" class="notice"><?php echo TEXT_NO_HTML . (REVIEWS_APPROVAL == '1' ? '<br />' . TEXT_APPROVAL_REQUIRED: ''); ?></div>
<br class="clearBoth" />
</form>
</div>
