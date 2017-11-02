<?php
 $orders_number = $_POST['order_number'];
 ?>
<div class="centerColumn" id="checkoutSuccess">

<h1 id="checkoutSuccessHeading"><?php echo HEADING_TITLE; ?></h1>
<h3 id="checkoutSuccessThanks">Thanks for shopping with us online!</h3>
<div id="checkoutSuccessContactLink" ><?php echo 'Please direct any questions you have to <a href="' . zen_href_link(FILENAME_CONTACT_US) . '">customer service</a>';?></div>
<div id="checkoutSuccessOrderInfo" class="box"> 
  <div class="centerBoxHeading">Order Details:</div>
   <div class="content">
   <div id="checkoutSuccessOrderNumber"><?php echo 'Order No. ' . $orders_number; ?></div>
   
	<?php
	if (isset($orders_number) && trim($orders_number) != ''){
		$order_query = 'select orders_id  from ' . TABLE_ORDERS . ' where orders_number= "'. $orders_number . '"';
		$orders_id_db = $db->Execute($order_query);
		$orders_id = $orders_id_db->fields['orders_id'];
		
		$sql = 'SELECT * FROM ' . TABLE_ORDERS_TOTAL . ' ' .
			   'WHERE orders_id = :orders_id ' .
			   'ORDER BY sort_order';
		$sql = $db->bindVars($sql, ':orders_id', $orders_id, 'integer');
		$res = $db->Execute($sql);	
		$order_info .= '<ul>';
		while (!$res->EOF) {
		  $order_info .= '<li>' . $res->fields['title'] . ' ' . $res->fields['text'] . '</li>';
		  $res->MoveNext();
		}
		
		$order_info .= '</ul>';
		
		echo $order_info;
		unset($sql, $res, $order_info) ;
	}
	?>

 </div> 
</div>

</div>
