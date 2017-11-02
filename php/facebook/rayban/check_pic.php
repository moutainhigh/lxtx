<?php
require('includes/application_top.php');

$sql = "SELECT `products_id`, `products_image` FROM ".TABLE_PRODUCTS." WHERE `products_status` = 1 ORDER BY `products_id`";
$rs = $db->Execute($sql);
if($rs->RecordCount() > 0) {
	while(!$rs->EOF) {
		$pid = $rs->fields['products_id'];
		$pimg = $rs->fields['products_image'];
		
		if(!file_exists(DIR_WS_IMAGES.$pimg)) {
			$sql = "UPDATE ".TABLE_PRODUCTS." SET `products_status` = 0 WHERE `products_id` = '$pid'";
			$db->Execute($sql);
			echo "$pid Disalbe.<br />\n";
		}
		
		$rs->MoveNext();
	}
}
echo 'Done.';