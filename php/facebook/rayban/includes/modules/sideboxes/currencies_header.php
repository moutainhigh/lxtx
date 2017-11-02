<?php
/**
 * search_header ("sidebox") - this is a search field that appears in the navigation header
 * (it's not really a "sidebox" per se.
 *
 * @package templateSystem
 * @copyright Copyright 2003-2005 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: search_header.php 2834 2006-01-11 22:16:37Z birdbrain $
 */
  $show_currencies= true;

  if ($show_currencies == true) {
    if (isset($currencies) && is_object($currencies)) {

      reset($currencies->currencies);
      $currencies_array = array();
      while (list($key, $value) = each($currencies->currencies)) {
        $currencies_array[] = array('id' => $key, 'text' => $value['title']);
      }

      $hidden_get_variables = '';
      reset($_GET);
      while (list($key, $value) = each($_GET)) {
        if ( ($key != 'currency') && ($key != zen_session_name()) && ($key != 'x') && ($key != 'y') ) {
          $hidden_get_variables .= zen_draw_hidden_field($key, $value);
        }
      }


    require($template->get_template_dir('tpl_currencies_header.php',DIR_WS_TEMPLATE, $current_page_base,'sideboxes'). '/tpl_currencies_header.php');

    $title = '<label>' . BOX_HEADING_CURRENCIES . '</label>';
    $title_link = false;
    require($template->get_template_dir('tpl_box_header.php',DIR_WS_TEMPLATE, $current_page_base,'common'). '/tpl_box_header.php');
    }
  }
?>