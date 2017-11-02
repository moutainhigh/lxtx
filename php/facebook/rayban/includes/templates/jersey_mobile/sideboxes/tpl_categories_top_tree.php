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
                                       
    $content .= '<ul class="list-style-none sideBoxContent">';
	for ($i=0;$i<sizeof($box_categories_array);$i++) {
		if ($box_categories_array[$i]['top'] == true) {	
				$content .= '<li class="categories-top-list"><a class="category-top" href="' . zen_href_link(FILENAME_DEFAULT, $box_categories_array[$i]['path']) . '">' . $box_categories_array[$i]['name']; 

                if (SHOW_COUNTS == 'true') {
 			       if ((CATEGORIES_COUNT_ZERO == '1' and $box_categories_array[$i]['count'] == 0) or $box_categories_array[$i]['count'] >= 1) {
		        	  $content .= '<span class="sub-count">' . CATEGORIES_COUNT_PREFIX . $box_categories_array[$i]['count'] . CATEGORIES_COUNT_SUFFIX . '</span>';
        			}
			    }

				$content .= '</a></li>';  
			}
	}
    $content .= '</ul>';
?>