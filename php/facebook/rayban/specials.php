<?php
require('includes/application_top.php');

$sql = "SELECT p.products_id as pid, p.products_price as price, s.specials_new_products_price as special FROM ".TABLE_PRODUCTS." p LEFT JOIN ".TABLE_SPECIALS." s ON p.products_status = 1 AND p.products_id = s.products_id";
$rs = $db->Execute($sql);
if($rs->RecordCount() > 0) {
	while(!$rs->EOF) {
		if(!$rs->fields['special']) {
			mt_srand(microtime(true)*1000);
			$new_price = round(100*$rs->fields['price']/mt_rand(20, 30), 2);
			
			$price_sql = "UPDATE ".TABLE_PRODUCTS." SET `products_price` = '$new_price' WHERE `products_id` = '".$rs->fields['pid']."'";
			$db->Execute($price_sql);
			$sp_sql = "INSERT INTO ".TABLE_SPECIALS." (`products_id`, `specials_new_products_price`, `specials_date_added`) VALUES ('".$rs->fields['pid']."', '".$rs->fields['price']."', NOW())";
			$db->Execute($sp_sql);
			
			echo $rs->fields['pid'].':'.$rs->fields['price'].' - '.$new_price."<br />\n";
			usleep(1000);
		}
		
		$rs->MoveNext();
	}
}

echo 'Done.';