<?php
//edit by thomas

function getThisSonClassIDArray($rootid,$check = true){
	global $db;	
	static $arr = array() ;
	if($check){		
		$arr = array();
	}
	
	$rsc = $db->Execute("select categories_id from categories where parent_id = '$rootid'");
	if($rsc->RecordCount() > 0){
		while(!$rsc->EOF){
				getThisSonClassIDArray($rsc->fields['categories_id'],false);
			$rsc->MoveNext();
		}
	}else{
		$arr[] = $rootid;		
	}
	return $arr;
}

function getThisSonClassIDString($rootid){
	$carr = getThisSonClassIDArray($rootid);
	return implode(',',$carr);
}

function getThisClassProductsHtml($rootid,$limit,$i="11"){
	global $db;
	$rsc = $db->Execute("select categories_name from categories_description where categories_id = '$rootid' and language_id = '1'");
?>
<div class="mid-cent fl">
<div class="content-top fl"><?php echo $rsc->fields['categories_name']?></div>
<div class="content fl">
<?php
	$cstr = getThisSonClassIDString($rootid);
	$rsp = $db->Execute("select products_id from products_to_categories where categories_id in (".$cstr.") order by products_id desc limit 0,$limit");
	while(!$rsp->EOF){
		$rs = $db->Execute("select p.products_id, p.products_image, pd.products_name from products p, products_description pd where p.products_id = pd.products_id and p.products_id = '".$rsp->fields['products_id']."' ");
		$products_price = zen_get_products_display_price($rs->fields['products_id']);
?>

	<div class="ware fl"><div class="goods_aImg"><a href="<?php echo zen_href_link(zen_get_info_page($rs->fields['products_id']), 'cPath=' . $productsInCategory[$rs->fields['products_id']] . '&products_id=' . $rs->fields['products_id'])?>"><?php echo zen_image(DIR_WS_IMAGES . $rs->fields['products_image'], $rs->fields['products_name'], '150', '150')?></a></div>
	<div><?php echo $products_price?></div>
	<p><a style="font-size:12px;" href="<?php echo zen_href_link(zen_get_info_page($rs->fields['products_id']), 'cPath=' . $productsInCategory[$rs->fields['products_id']] . '&products_id=' . $rs->fields['products_id'])?>"><?php echo $rs->fields['products_name']?></a></p>
	</li>
</div>
<?php
		$rsp->MoveNext();
	}
?>
</div></div>
<?php
}


?>