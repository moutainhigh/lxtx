<?php
/**
 * Side Box Template
 *
 * @package templateSystem
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_categories.php 4162 2006-08-17 03:55:02Z ajeh $
 */
  $content = "";
  
  $content .= '<div id="' . str_replace('_', '-', $box_id . 'Content') . '" class="sideBoxContent">' . "\n";
  $content .= '<ul>'."\n";
  foreach($cates as $cate){
	  $cid = $cate['id'];
	  $cname = $cate['text'];
	  $hasSub = zen_has_category_subcategories($cid);

	  if(isset($cPath_array) && in_array($cid, $cPath_array)){
		  $content .= '	<li class="clva"><a class="selected" href="'.zen_href_link(FILENAME_DEFAULT, 'cPath='.$cid).'">'.$cname.'</a></li>'."\n";
	  } else {
		  $content .= '	<li class="clva"><a href="'.zen_href_link(FILENAME_DEFAULT, 'cPath='.$cid).'">'.$cname.'</a></li>'."\n";
	  }
	  
	  if($hasSub && isset($cPath_array) && in_array($cid, $cPath_array)){
		  //$content .= '	<ul>'."\n";
		  $sub_cates = array();
		  $sub_cates = zen_get_categories_diy($sub_cates, $cid, '', 1);
		  foreach($sub_cates as $sub){
				$subHasSub = zen_has_category_subcategories($sub['id']);
				
			  if(isset($cPath_array) && in_array($sub['id'], $cPath_array)){
				  $content .= '	<li class="clvb"><a class="selected" href="'.zen_href_link(FILENAME_DEFAULT, 'cPath='.$cid.'_'.$sub['id']).'">'.$sub['text'].'</a></li>'."\n";
			  } else {
				  $content .= '	<li class="clvb"><a href="'.zen_href_link(FILENAME_DEFAULT, 'cPath='.$cid.'_'.$sub['id']).'">'.$sub['text'].'</a></li>'."\n";
			  }
				
				if($subHasSub && isset($cPath_array) && in_array($sub['id'], $cPath_array)) {
					$subsub_cates = array();
					$subsub_cates = zen_get_categories_diy($subsub_cates, $sub['id'], '', 1);
					foreach($subsub_cates as $subsub){
						if(isset($cPath_array) && in_array($subsub['id'], $cPath_array)){
							$content .= '	<li class="clvc"><a class="selected" href="'.zen_href_link(FILENAME_DEFAULT, 'cPath='.$cid.'_'.$sub['id'].'_'.$subsub['id']).'">'.$subsub['text'].'</a></li>'."\n";
						} else {
							$content .= '	<li class="clvc"><a href="'.zen_href_link(FILENAME_DEFAULT, 'cPath='.$cid.'_'.$sub['id'].'_'.$subsub['id']).'">'.$subsub['text'].'</a></li>'."\n";
						}
					}
				}
		  }
		  //$content .= '	</ul>'."\n";
	  }
	  
	  //$content .= '	</li>'."\n";
  }
  $content .= '</ul>'."\n";
  $content .= '</div>'."\n";
?>