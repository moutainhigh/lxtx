<?php
/**
 * Header Languages Links Template
 *
 * @package templateSystem
 * @copyright Copyright 2003-2005 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_header_languages.php 2006-05-31 bunyip $
 */
  $content = "";

  $content .= '<ul class="list-popup list-style-none">';

  while (list($key, $value) = each($lng->catalog_languages)) {
    $content .= '<li>'.zen_image(DIR_WS_LANGUAGES .  $value['directory'] . '/images/' . $value['image'], $value['name'], '', '').'<a class="language-popup-links" href="' . zen_href_link($_GET['main_page'], zen_get_all_get_params(array('language', 'currency')) . 'language=' . $key, $request_type) . '">' . $value['name'] . '</a></li>';
  }
  $content .= '</ul>';
  echo $content;
?>