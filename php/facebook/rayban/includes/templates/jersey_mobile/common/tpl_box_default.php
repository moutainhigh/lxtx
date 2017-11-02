<?php
/**
* Template designed by 12leaves.com
* 12leaves.com - Free ecommerce templates and design services
*
* Common Template
* 
* @package languageDefines
* @copyright Copyright 2009-2010 12leaves.com
* @copyright Copyright 2003-2005 Zen Cart Development Team
* @copyright Portions Copyright 2003 osCommerce
* @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
* @version $Id: tpl_box_default_left.php 2975 2006-02-05 19:33:51Z birdbrain $
*/

// choose box images based on box position
  if ($title_link) {
    $title = $title . ' - <a href="' . zen_href_link($title_link) . '">' . BOX_HEADING_LINKS . '</a>';
  }
//
// #wel - category header style
  if ($box_id == "categories") {
    $header_left = 'main-sidebox-header-left';
    $header_right = 'main-sidebox-header-right';
  } else {
    $header_left = '';
    $header_right = '';
  }
//
?>
<!--// bof: <?php echo $box_id; ?> //-->
<div class="BoxContainer">
<div class="b-header-left <?php echo $header_left; ?>"><h2 class="centerBoxHeading b-header-right <?php echo $header_right; ?>" id="<?php echo str_replace('_', '-', $box_id) . 'Heading'; ?>"><?php echo $title; ?></h2></div>
<?php 
echo $content; 
?>
</div>
<!--// eof: <?php echo $box_id; ?> //-->
