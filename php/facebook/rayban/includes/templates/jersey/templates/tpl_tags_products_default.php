<div id="indexProductList" class="centerColumn">
<?php 
if(!empty($tags_head) && strtolower($tags_head)!='null'){
	echo '<h1>'.$tags_head.'</h1>'."\n";
}
?>
<?php 
if(!empty($products_content)){ 
	$i=0;
	foreach($products_content as $value){
		$i++;
?>
	<div class="centerBoxContentsProducts centeredContent back" style="width:25%;overflow:hidden;">
<?php
echo '    <div class="pImgBox"><div class="pImg"><a href="' . zen_href_link(zen_get_info_page($value['products_id']), 'cPath=' . $productsInCategory[$value['products_id']] . '&products_id=' . $value['products_id']) . '">';
echo zen_image(DIR_WS_IMAGES . $value['products_image'], $value['products_name'], 150, 150);
echo '</a></div></div>'."\n";
echo '    <div class="pName"><a href="' . zen_href_link(zen_get_info_page($value['products_id']), 'cPath=' . $productsInCategory[$value['products_id']] . '&products_id=' . $value['products_id']) . '">'.$value['products_name'].'</a></div>';
echo '   <div class="cprice">'.$value['products_price'].'</div>'."\n";
?>
		<a href="<?php echo zen_href_link(zen_get_info_page($value['products_id']), 'cPath=' . $productsInCategory[$value['products_id']] . '&products_id=' . $value['products_id']);?>"><img width="42" height="24" title=" view product " alt="view product" src="<?php echo $template->get_template_dir('view.png',DIR_WS_TEMPLATE, $current_page_base,'images').'/view.png';?>"></a>
    <a href="<?php echo zen_href_link($_GET['main_page'], zen_get_all_get_params(array('action')) . 'action=buy_now&products_id=' . $value['products_id']);?>"><img width="76" height="25" title=" buy product " alt="buy product" src="<?php echo $template->get_template_dir('buy.png',DIR_WS_TEMPLATE, $current_page_base,'images').'/buy.png';?>"></a>
  </div>
<?php 
		if($i%4==0){echo "<div class='clearBoth'></div>";}
	}
} else{
	echo "No Product exist !";
}
?>
</div>