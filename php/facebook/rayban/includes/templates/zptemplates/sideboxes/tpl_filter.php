<?php
$content = "";
if(!empty($get)) {
	$content .= '<div class="filterBox">'."\n";
	$get_url = zen_get_all_get_params($getKeys);
	$content .= '<div class="remove"><a rel="nofollow" href="'.zen_href_link(FILENAME_DEFAULT, $get_url).'">Clear All</a></div>'."\n";
	
	foreach($get as $opn => $opvn) {
		$get_url = zen_get_all_get_params(array($opn));
		$content .= '<div class="remove"><a rel="nofollow" href="'.zen_href_link(FILENAME_DEFAULT, $get_url).'">'.$opn.':&nbsp;'.$opvn.'</a></div>'."\n";
	}
	$content .= '</div>'."\n";
}

if(zen_has_category_subcategories($current_category_id)) {
	$content .= '<h3 class="leftBoxHeading"><span></span>Categories</h3>'."\n";
	$content .= '<div class="filterBox">'."\n";
	$subs = array();
	$subs = zen_get_categories_diy($subs, $current_category_id, '', 1);
	foreach($subs as $sub) {
		$total = zen_count_products_in_category($sub['id'], true);
		$content .= '<div><a href="'.zen_href_link(FILENAME_DEFAULT, 'cPath='.$cPath.'_'.$sub['id']).'">'.$sub['text'].'</a> ('.$total.')</div>'."\n";
	}
	$content .= '</div>'."\n";
}

if(!empty($optArr)) {
	foreach($optArr as $id => $name) {
		$content .= '<h3 class="leftBoxHeading"><span></span>'.$name.'</h3>'."\n";
		$content .= '<div class="filterBox">'."\n";
		natsort($attArr[$id]);
		foreach($attArr[$id] as $opvid => $opvname) {
			$total = 0;
			foreach($fltArr as $pid => $pAttr) {
				$_exist = true;
				if(!empty($get)) {
					foreach($get as $k => $v) {
						if(!empty($pAttr[$k]) && in_array($v, $pAttr[$k])) {
							continue;
						} else {
							$_exist = false;
						}
					}
				}
				
				if($_exist && !empty($pAttr[$name]) && !empty($pAttr[$name][$opvid]) && $pAttr[$name][$opvid] == $opvname) $total++;
			}

			$exclude = array();
			if(in_array($name, $getKeys)) $exclude[] = $name;
			$get_url = zen_get_all_get_params($exclude);
			if(!empty($get[$name]) && $get[$name] == $opvname) {
				$inGet = true;
			} else {
				$inGet = false;
				if($get_url != '') { $get_url .= "&$name=".rawurlencode($opvname); } else { $get_url .= "$name=".rawurlencode($opvname); }
			}
			
			if($inGet) {
				$content .= '<div class="selected">';
			} else {
				$content .= '<div class="normal">';
			}
			if($total) {
				$content .= '<a rel="nofollow" href="'.zen_href_link(FILENAME_DEFAULT, $get_url).'">'.$opvname.' </a> ('.$total.')</div>'."\n";;
			} else {
				$content .= '<span>'.$opvname.'</span> ('.$total.')</div>'."\n";;
			}
		}
		$content .= '</div>'."\n";
	}
}
?>