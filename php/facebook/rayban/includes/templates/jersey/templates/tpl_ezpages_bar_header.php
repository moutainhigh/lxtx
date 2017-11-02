<?php
/**
 * Page Template
 *
 * Displays EZ-Pages Header-Bar content.<br />
 *
 * @package templateSystem
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_ezpages_bar_header.php 3377 2006-04-05 04:43:11Z ajeh $
 */

  /**
   * require code to show EZ-Pages list
   */
  include(DIR_WS_MODULES . zen_get_module_directory('ezpages_bar_header.php'));
?>
<div id="navTop">
<ul>
<!--li><a href="<?php echo HTTP_SERVER.DIR_WS_CATALOG;?>" class="cparent"><?php echo HEADER_TITLE_CATALOG;?></a></li-->
<?php
/*$sql = "SELECT c.categories_id AS cid, cd.categories_name AS cname FROM ".TABLE_CATEGORIES." c, ".TABLE_CATEGORIES_DESCRIPTION." cd 
		WHERE c.parent_id = 0 AND c.categories_id in (1, 58, 107, 119, 169, 224, 267, 313, 360, 383, 397, 447, 460, 1919, 1994)
		AND c.categories_status = 1 AND c.categories_id = cd.categories_id AND cd.language_id = '".$_SESSION['languages_id']."'
		ORDER BY c.sort_order, cd.categories_name";*/
$sql = "SELECT c.categories_id AS cid, cd.categories_name AS cname FROM ".TABLE_CATEGORIES." c, ".TABLE_CATEGORIES_DESCRIPTION." cd 
		WHERE c.parent_id = 0 AND c.categories_status = 1 
		AND c.categories_id = cd.categories_id AND cd.language_id = '".$_SESSION['languages_id']."'
		ORDER BY c.sort_order, cd.categories_name";
$rs = mysqli_query($db->link, $sql);
$subNum = 1;
while($cate = @mysqli_fetch_assoc($rs)) {
	$cname = $cate['cname'];
	if(stripos($cname, 'soccer') === false) $cname = str_ireplace(array(' jerseys', ' jersey', 'sports '), '', $cname);
	echo '<li><a href="'.zen_href_link(FILENAME_DEFAULT, 'cPath='.$cate['cid']).'" class="cparent">'.$cname.'</a>'."\n";
	if(zen_has_category_subcategories($cate['cid'])) {
		$subs = array();
		$subs = zen_get_categories_diy($subs, $cate['cid'], '', 1);
		echo '<ul class="subCate" id="subList'.$subNum.'">'."\n";
		foreach($subs as $sub) {
			echo '	<li><a href="'.zen_href_link(FILENAME_DEFAULT, 'cPath='.$cate['cid'].'_'.$sub['id']).'">'.$sub['text'].'</a></li>'."\n";
		}
		echo '</ul>'."\n";
	}
	echo '</li>'."\n";
	$subNum++;
}
?>
<?php /*if (sizeof($var_linksList) >= 1) { ?>
<?php for ($i=1, $n=sizeof($var_linksList); $i<=$n; $i++) {  ?>
  <li><a href="<?php echo $var_linksList[$i]['link']; ?>"><?php echo $var_linksList[$i]['name']; ?></a><?php echo ($i < $n ? EZPAGES_SEPARATOR_HEADER : '') . "\n"; ?></li>
<?php } // end FOR loop ?>
<?php }*/ ?>
</ul>
</div>
<script type="text/javascript">
menu("#navTop");
</script>
<div class="clearBoth"></div>