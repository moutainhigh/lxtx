<?php
/**
 * jscript_main
 *
 * @package FEC Advanced
 * @copyright Copyright 2007 Numinix Technology http://www.numinix.com
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: jscript_main.php 67 2009-07-19 20:00:15Z numinix $
 */
?>
<script type="text/javascript">
<!--//
function copyBillToInfo(form1){
 if(document.form1.copybilling.checked) {
	document.form1.gender_shipping.value = document.form1.gender_billing.value;
  document.form1.lastname_shipping.value = document.form1.lastname_billing.value;
  document.form1.firstname_billing.value = document.firstname_billing.value;
  document.form1.company_shipping.value = document.form1.company_billing.value;
  document.form1.street_address_shipping.value = document.form1.street_address_billing.value;
  document.form1.city_shipping.value = document.form1.city_billing.value;
  document.form1.state_shipping.value = document.form1.state_billing.value;
  document.form1.postcode_shipping.value = document.postcode_billing.value;
  document.form1.country_shipping.value = document.form1.country_billing.value;
  document.form1.suburb_shipping.value = document.form1.suburb_billing.value;
  document.form1.zone_id_shipping.value = document.form1.zone_id_billing.value;
  }
}
//-->
</script>
<script type="text/javascript">
$(document).ready(function(){
	$('#new_customer').click(function (){
		if($(this).is(':checked')) {
			$('#qc_create_account').fadeIn();
			$('#qc_login').hide();
		}
	});
	
	$('#back_login').click(function() {
		$('#qc_create_account').fadeOut();
		$('#qc_login').fadeIn();
		$('#new_customer').removeAttr('checked');
	});
	
	$('#shippingAddress-checkbox').click(function (){
		if($(this).is(':checked')) {
			$('#shippingField').fadeOut();
		} else {
			$('#shippingField').fadeIn();
		}
	});
<?php if (jQueryshippingAddress == true) { ?>
  $('#shippingField').fadeOut();
<?php } ?>
});
</script>
