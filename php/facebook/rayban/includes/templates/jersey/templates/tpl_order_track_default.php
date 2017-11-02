<?php
/**
 * tpl_page_2_default.php
 *
 * @package templateSystem
 * @copyright Copyright 2003-2005 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_page_2_default.php 3464 2006-04-19 00:07:26Z ajeh $
 */
?>
<div class="centerColumn clear" id="orderTrack">
<div id="orderTrackMainContent" class="contInner">
<?php if ($messageStack->size('order_track') > 0) echo $messageStack->output('order_track'); ?>
<?php
/**
 * load the html_define for the page_2 default
 */
  require($define_page);
?>
</div>

<div class="buttonRow forward"><?php echo zen_back_link() . zen_image_button(BUTTON_IMAGE_BACK, BUTTON_BACK_ALT) . '</a>'; ?></div>
</div>