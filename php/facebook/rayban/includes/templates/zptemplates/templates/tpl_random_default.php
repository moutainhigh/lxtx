<?php
/**
 * @Copyright http://www.ezencart.com
 */
function GetFullcPath($cID){
    global $db;
    static $parent_cache = array();
    $cats = array();
    $cats[] = $cID;
    $parent = $db->Execute("SELECT parent_id, categories_id
                            FROM " . TABLE_CATEGORIES . "
                            WHERE categories_id=" . (int)$cID);
    while(!$parent->EOF && $parent->fields['parent_id'] != 0) {
      $parent_cache[(int)$parent->fields['categories_id']] = (int)$parent->fields['parent_id'];
      $cats[] = $parent->fields['parent_id'];
      if(isset($parent_cache[(int)$parent->fields['parent_id']])) {
        $parent->fields['parent_id'] = $parent_cache[(int)$parent->fields['parent_id']];
      } else {
        $parent = $db->Execute("SELECT parent_id, categories_id
                                FROM " . TABLE_CATEGORIES . "
                                WHERE categories_id=" . (int)$parent->fields['parent_id']);
      }
    }
    $cats = array_reverse($cats);
    $cPath = implode('_', $cats);
    return $cPath;
}
 
 $num=10;//随机产品数量

$sql="select * from ".TABLE_PRODUCTS ." a 
 LEFT JOIN ".TABLE_PRODUCTS_DESCRIPTION ." b ON (a.products_id=b.products_id)
 LEFT JOIN ".TABLE_PRODUCTS_TO_CATEGORIES ." c ON (a.products_id=c.products_id)
 WHERE a. products_status = 1 
 AND a.products_quantity > 0
 AND a.products_type = 1
 ORDER BY RAND() limit ".$num;
echo '<div class="rollBox">';

//左边控制按钮
echo '<div class="LeftBotton" onmousedown="ISL_GoUp()" onmouseup="ISL_StopUp()" onmouseout="ISL_StopUp()"></div>';

echo '
     <div class="Cont" id="ISL_Cont">
      <div class="ScrCont">
       <div id="List1">
';
$content='';
$random=$db->Execute($sql);
	while (!$random->EOF) {
		$content.='<div class="box back">';

		//产品图片显示
		$content.='<div class="listimg"><a href="' . zen_href_link(zen_get_info_page($random->fields['products_id']), 'cPath=' .GetFullcPath($random->fields['categories_id']). '&products_id=' . $random->fields['products_id']) . '">'.zen_get_products_image($random->fields['products_id']).'</a></div>';

		//产品名称显示
		$content.='<div class="pname"><a href="' . zen_href_link(zen_get_info_page($random->fields['products_id']), 'cPath=' .GetFullcPath($random->fields['categories_id']). '&products_id=' . $random->fields['products_id']) . '">'.zen_get_products_name($random->fields['products_id']).'</a></div>';

		//产品价格显示
		$content.='<div class="pprice">'.zen_get_products_display_price($random->fields['products_id']).'</div>';

		$content.='</div>';
		$random->MoveNext();
 }
echo  $content;
	echo '
		</div>
		<div id="List2"></div>
	</div>
</div>
	';
//右边控制按钮
echo '<div class="RightBotton" onmousedown="ISL_GoDown()" onmouseup="ISL_StopDown()" onmouseout="ISL_StopDown()"></div>';

echo '</div>';
?>
<script type="text/javascript" src="includes/templates/jersey/jscript/random.js"></script>