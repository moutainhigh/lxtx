<?php
if(in_array($_GET['main_page'], array('login', 'create_account', 'account', 'shopping_cart', 'checkout', 'fec_confirmation', 'checkout_shipping', 'checkout_payment', 'no_account', 'address_book_process'))) {
?>
<script language="javascript">
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
<?php } ?>