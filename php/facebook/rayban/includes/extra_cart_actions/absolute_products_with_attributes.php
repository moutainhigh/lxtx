<?php

switch($_GET['action']) {
  case 'add_product':
	if (is_array($_POST['products_id'])) {
	  foreach($_POST['products_id'] as $prid => $qty) {
	    $products_id = zen_get_prid($prid);
		if (is_array($_POST['id'])) {
		  foreach($_POST['id'] as $option => $option_value) {
		    $_POST['attribs'][$prid][$option] = $option_value;
		  }
		}
		foreach($_POST['attribs'][$prid] as $option_id => $value_id) {
		  if (substr($option_id, 0, strlen(TEXT_PREFIX)) == TEXT_PREFIX) {
		    $option_id = substr($option_id, strlen(TEXT_PREFIX));
		  }
		  $check_attrib = $db->Execute(	"select pov.products_options_values_name from " . TABLE_PRODUCTS_ATTRIBUTES . " pa, " . TABLE_PRODUCTS_OPTIONS_VALUES . " pov " . 
		  								"where pa.options_values_id = pov.products_options_values_id " .
										"and pa.options_id = '".(int)$option_id . "' " . 
										"and pa.products_id = '".(int)$products_id ."' " . 
										"and pov.language_id = '".(int)$_SESSION['languages_id']."'");
		  if ($check_attrib->RecordCount() <= 1 && $check_attrib->fields['products_options_values_name'] == '') {
		    unset($_POST['attribs'][$prid][$option_id]);
		  }
		}
		if ((int)$qty > 0) {
		  reset($_POST['attribs'][$prid]);
		  $_SESSION['cart']->add_cart($products_id, $qty, $_POST['attribs'][$prid]);
		}
	  }
	}
	$messageStack->reset();
	unset($_SESSION['cart_errors']);
	$_SESSION['cart']->get_products(false);  //Update all prices now we have added everything
  break;
}

?>