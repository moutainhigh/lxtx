<?php
/**
* Template designed by 12leaves.com
* 12leaves.com - Free ecommerce templates and design services
 * 
 * @copyright Copyright 2008-2009 12leaves.com
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 */

// test if box should display
  $show_languages= true;

  if ($show_languages == true) {
    if (!isset($lng) || (isset($lng) && !is_object($lng))) {
      $lng = new language;
    }

    reset($lng->catalog_languages);
    $languages_array = array();
    $current_language = '';    
      while (list($key, $value) = each($lng->catalog_languages)) {
        $languages_array_popup[$key] = $value['name'];
      }
     
    foreach ($languages_array_popup as $k => $v){
        if ($k == $_SESSION['languages_code']) {
            $current_language = $v;
        }
    }
  }
      $lang_array = array();
	  while (list($key, $value) = each($lng->catalog_languages)) {
        $lang_array[] = array('id' => $key, 'text' => $value['name']);
      }
      $hidden_get_variables = '';
      reset($_GET);

      while (list($key, $value) = each($_GET)) {
        if ( ($key != 'language') && ($key != zen_session_name()) && ($key != 'x') && ($key != 'y') ) {
          $hidden_get_variables .= zen_draw_hidden_field($key, $value);
        }
      }

?>
<a href="#" id="language_switcher">
<?php
  echo $current_language; 
?>
</a>