<?php
if($_GET['main_page'] == 'index' && isset($_GET['cPath']) && sizeof($cPath_array) > 1) {
	$productsInCategory = zen_get_categories_products_list($cPath, false, true, 0, '');
	if(!empty($productsInCategory)) {
		$pidAll = array_keys($productsInCategory);
		
		$pidStr = implode(',', $pidAll);
		/*$sql = "SELECT * FROM ".TABLE_PRODUCTS_OPTIONS." po, ".TABLE_PRODUCTS_OPTIONS_VALUES." pv, ".TABLE_PRODUCTS_ATTRIBUTES." pa 
				WHERE pa.products_id in ($pidStr) AND pa.options_values_id = pv.products_options_values_id
				AND pa.options_id = po.products_options_id AND pa.options_id > 2 
				ORDER BY pa.products_id";*/
		$sql = "SELECT * FROM ".TABLE_PRODUCTS_OPTIONS." po, ".TABLE_PRODUCTS_OPTIONS_VALUES." pv, ".TABLE_PRODUCTS_ATTRIBUTES." pa 
				WHERE pa.products_id in ($pidStr) AND pa.options_values_id = pv.products_options_values_id
				AND pa.options_id = po.products_options_id 
				ORDER BY pa.products_id";
		$attr_query = $db->Execute($sql);
		
		$fltArr = array();
		$optArr = array();
		$attArr = array();
		while(!$attr_query->EOF) {
			$fltArr[$attr_query->fields['products_id']][$attr_query->fields['products_options_name']][$attr_query->fields['options_values_id']] = $attr_query->fields['products_options_values_name'];
			//$fltArr[$attr_query->fields['options_id']][$attr_query->fields['options_values_id']][] = $attr_query->fields['products_id'];
			$attArr[$attr_query->fields['options_id']][$attr_query->fields['options_values_id']] = $attr_query->fields['products_options_values_name'];
			if(!in_array($attr_query->fields['products_options_name'], $optArr))
				$optArr[$attr_query->fields['options_id']] = $attr_query->fields['products_options_name'];
			$attr_query->MoveNext();
		}
		
		$get = $_GET;
		$getKeys = array_keys($get);
		foreach($getKeys as $k => $gk){
			$sql = "SELECT `products_options_id` FROM ".TABLE_PRODUCTS_OPTIONS." WHERE `products_options_name` = '".addslashes($gk)."'";
			$is_opt = $db->Execute($sql);
			if($is_opt->EOF) {
				unset($getKeys[$k]);
				unset($get[$gk]);
			}
		}
	}
	
	require($template->get_template_dir('tpl_filter.php',DIR_WS_TEMPLATE, $current_page_base,'sideboxes'). '/tpl_filter.php');
?>
<div class="leftBoxContainer" id="<?php echo str_replace('_', '-', $box_id ); ?>Content">
<?php echo $content; ?>
</div>
<?php
}
?>