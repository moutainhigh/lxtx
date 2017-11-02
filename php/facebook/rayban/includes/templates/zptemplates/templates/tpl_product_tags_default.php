<?php
/**
* Page Template
*
* Loaded automatically by index.php?main_page=PRODUCTTAGS.<br />
* Displays conditions page.
*
* @package templateSystem
* @copyright Copyright 2003-2006 Zen Cart Development Team
* @copyright Portions Copyright 2003 osCommerce
* @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
* @version $Id: tpl_PRODUCTTAGS_default.php  v1.3 $
*/
$breadcrumb->trail($_GET['letter']);
?>
<div class="centerColumn" id="ProductTags">
<!-- A TO Z -->
<div class="letter_tags">
<?php
// display productTagList
$tags_sql = "select * from ".TABLE_TAGS_DESCRIPTION." where status = 1 order by sort";
$tags = $db->Execute($tags_sql);
$n = 0;
while(!$tags->EOF){
	if($n == 0) echo '| ';
	echo '<a href="'.HTTP_SERVER.DIR_WS_CATALOG.'product_tags/'.strtoupper($tags->fields['tags_name']).'/">'.strtoupper($tags->fields['tags_name']).'</a> | ';
	$tags->MoveNext();
	$n++;
}
?> 
</div>
<!-- END A TO Z -->

<?php
if ($_GET['letter']=='0-9'){
	$producttags_split_sql = "select * from ".TABLE_TAGS_PRODUCTS." where tags_value REGEXP '^[0-9]'";
}else{
	$producttags_split_sql = "select * from ".TABLE_TAGS_PRODUCTS." where tags_value REGEXP  '^".strtolower($_GET['letter'])."'";
}//print_r($producttags_split_sql);
//$producttags_split = new splitPageResults($producttags_split_sql, 50, 'tags_id', 'page',false);
//$zco_notifier->notify('NOTIFY_MODULE_PRODUCT_LISTING_RESULTCOUNT', $producttags_split->number_of_rows);
$producttags = $db->Execute($producttags_split_sql);
//echo $producttags->RecordCount();
?>


<?php
if($producttags->RecordCount() > 0&&!empty($_GET['letter'])){
	echo '<div class="rtbox">';
	echo '<h1 id="tagsListHeading">Tags '.$_GET['letter'].'</h1>';
	echo '<div id="tagnew">';
	echo '<div class="tags_list">';
	echo '<ul>';
	while (!$producttags->EOF){
		$url_name = strtolower(str_replace(" ","-",trim($producttags->fields['tags_value'])));
		$myarp = array('.','+',',','，','。','%','&','*','/','\\','|','=','(',')','^','$','#','@','!','~','`','/',';',':','"','[',']','{','}','>','<');
		for($i=0; $i<=count($myarp); $i++){
			$url_name = str_replace($myarp[$i],"",$url_name);
		}
		echo '<li><a href="'.HTTP_SERVER.DIR_WS_CATALOG.'tags_products/'.$url_name.'_'.(int)$producttags->fields['tags_id'].'.html">'.$producttags->fields['tags_value'].'('.sizeof(explode('-',$producttags->fields['products_id'])).')</a>'; 
		$producttags->MoveNext();
	}	
	echo '</ul></div></div></div>';	
}else{
?>
<div class="rtbox">
<?php if(!empty($_GET['letter'])){ ?>
<h1 id="tagsListHeading">Tags <?php echo '<font color="#990000;">'.$_GET['letter'].'</font>';?> Does not exist</h1>
<?php } ?>
<br class="clearBoth"/>

<?php //Latest Tags; ?>
<h1 id="tagsListHeading">Latest Tags</h1>
<div id="tagnew">
<div class="tags_list">
<ul>
<?php 
$tags_sql="select * from ".TABLE_TAGS_PRODUCTS." where tags_status=1 and tags_display_main_page=1 order by tags_date_modified desc,tags_sort desc limit 28";
$product_tags = $db->Execute($tags_sql);
while(!$product_tags->EOF){
	$url_name=strtolower(str_replace(" ","-",trim($product_tags->fields['tags_value'])));
	$myarp=array('.','+',',','，','。','%','&','*','/','\\','|','=','(',')','^','$','#','@','!','~','`','/',';',':','"','[',']','{','}','>','<');
	for($i=0;$i<=count($myarp);$i++){
		$url_name=str_replace($myarp[$i],"",$url_name);
	}
	echo '<li><a href="'.HTTP_SERVER.DIR_WS_CATALOG.'tags_products/'.$url_name.'_'.(int)$product_tags->fields['tags_id'].'.html">'.$product_tags->fields['tags_value'].'('.sizeof(explode('-',$product_tags->fields['products_id'])).')</a></li>';
	$product_tags->MoveNext();
}
?>
</ul>
</div>
</div>
<?php // END Latest Tags ?>

<?php //All Tags ?>
<h1 id="tagsListHeading">All Tags</h1>
<div id="tagscatalog">
<div class="tags_list">
	<ul>
<?php 
$tags_sql="select * from ".TABLE_TAGS_PRODUCTS." where tags_status=1  order by tags_sort desc,tags_date_modified desc";
$product_tags = $db->Execute($tags_sql);
$t=0;
while(!$product_tags->EOF){
	$t++;
	$url_name=strtolower(str_replace(" ","-",trim($product_tags->fields['tags_value'])));
	$myarp=array('.','+',',','，','。','%','&','*','/','\\','|','=','(',')','^','$','#','@','!','~','`','/',';',':','"','[',']','{','}','>','<');
	for($i=0;$i<=count($myarp);$i++){
		$url_name=str_replace($myarp[$i],"",$url_name);
	}
	echo '<li><a href="'.HTTP_SERVER.DIR_WS_CATALOG.'tags_products/'.$url_name.'_'.(int)$product_tags->fields['tags_id'].'.html">'.$product_tags->fields['tags_value'].'('.sizeof(explode('-',$product_tags->fields['products_id'])).')</a></li>';
	$product_tags->MoveNext();
}
?>
  </ul>
</div>
</div>
<?php // END All Tags ?>

</div>

<?php			
}
?>

<?php //print_r(zen_get_all_get_params(array('page', 'info', 'x', 'y'))); ?>
<?php //if (($producttags_split->number_of_rows > 0) && ((PREV_NEXT_BAR_LOCATION == '2') || (PREV_NEXT_BAR_LOCATION == '3')) ) {
?>
<div style="text-align:center"><?php //echo TEXT_RESULT_PAGE . ' ' . $producttags_split->display_links(5, zen_get_all_get_params(array('page', 'info', 'x', 'y'))); ?></div>
<?php
// }
?>
</div>