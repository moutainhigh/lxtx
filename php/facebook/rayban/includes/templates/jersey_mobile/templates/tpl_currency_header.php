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



  /**

   * require code to show EZ-Pages list

   */

/*

if ($current_page_base == 'index') {

	$active	= 'tab_active';

}

*/

  $show_currencies= true;



  if ($show_currencies == true) {

    if (isset($currencies) && is_object($currencies)) {



      reset($currencies->currencies);

      $currencies_array = array();

      while (list($key, $value) = each($currencies->currencies)) {

        $currencies_array_popup[$key] = $value['title'] . '&nbsp;(' . $value['symbol_left'] . ')';

		$currencies_array_symbol[$key] = $value['symbol_left'];

      }



      $hidden_get_variables = '';

      reset($_GET);

      while (list($key, $value) = each($_GET)) {

        if ( ($key != 'currency') && ($key != zen_session_name()) && ($key != 'x') && ($key != 'y') ) {

          $hidden_get_variables .= zen_draw_hidden_field($key, $value);

        }

      }

                      

    }

  }



//  fn_print_r($currencies);



?>

<div class="item-currency">
<?php

  echo $currencies_array_popup[$_SESSION['currency']]; 

?>
</div>

<a href="#" id="currency_switcher">

</a>