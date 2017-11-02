<?php
/**
* Template designed by 12leaves.com
* 12leaves.com - Free ecommerce templates and design services
* 
* Side Box Template
*
* @package templateSystem
* @copyright Copyright 2003-2006 Zen Cart Development Team
* @copyright Portions Copyright 2003 osCommerce
* @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
* @version $Id: tpl_categories.php 4162 2006-08-17 03:55:02Z ajeh $
*/
  $content = "";

   $content .= '<form method="get" action="http://www.buysrb.com/index.php?main_page=advanced_search_result" name="quick_find_header">
  <input type="hidden" value="advanced_search_result" name="main_page">
  <input type="hidden" value="1" name="search_in_description">
  <input type="text" maxlength="30" size="6" name="keyword">
  <input type="submit" value="Search">
</form>
';
  $content .= '</div>';
?>