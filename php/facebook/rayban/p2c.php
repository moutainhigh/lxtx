<?php
require('includes/application_top.php');

$fp = fopen('p2c.csv', 'r');
while($row = fgetcsv($fp, 1000)) {
	$pid = $row[0];
	$cid = $row[1];
	
	$db->Execute("DELETE FROM ".TABLE_PRODUCTS_TO_CATEGORIES." WHERE products_id = '$pid'");
	$db->Execute("UPDATE ".TABLE_PRODUCTS." SET master_categories_id = '$cid' WHERE products_id = '$pid'");
	$db->Execute("INSERT INTO ".TABLE_PRODUCTS_TO_CATEGORIES." VALUES ('$pid', '$cid')");
	
	echo $pid.' - '.$cid."<br />\n";
}
fclose($fp);
echo "DONE.";