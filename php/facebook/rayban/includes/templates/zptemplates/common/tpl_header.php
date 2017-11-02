<?php
/**
 * Common Template - tpl_header.php
 *
 * this file can be copied to /templates/your_template_dir/pagename<br />
 * example: to override the privacy page<br />
 * make a directory /templates/my_template/privacy<br />
 * copy /templates/templates_defaults/common/tpl_footer.php to /templates/my_template/privacy/tpl_header.php<br />
 * to override the global settings and turn off the footer un-comment the following line:<br />
 * <br />
 * $flag_disable_header = true;<br />
 *
 * @package templateSystem
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_header.php 4813 2006-10-23 02:13:53Z drbyte $
 */
?>

<?php
  // Display all header alerts via messageStack:
  if ($messageStack->size('header') > 0) {
    echo $messageStack->output('header');
  }
  if (isset($_GET['error_message']) && zen_not_null($_GET['error_message'])) {
  echo htmlspecialchars(urldecode($_GET['error_message']));
  }
  if (isset($_GET['info_message']) && zen_not_null($_GET['info_message'])) {
   echo htmlspecialchars($_GET['info_message']);
} else {
}
?>

<?php
if (!isset($flag_disable_header) || !$flag_disable_header) {
?>
<div id="rl-topnav">
	<a id="rl-logo" target="_parent" href="/"><img width="200px" height="100px" src="<?php echo DIR_WS_TEMPLATE; ?>images/sunglass/logo.png" alt="" title=""></a>
    <div class="disc"><img src="<?php echo DIR_WS_TEMPLATE; ?>images/sunglass/discount.png" /></div>
    <div id="rl-utility-container" class="" style="visibility: visible; height: auto;">
    	<ul id="rl-utilitynav">
			<li id="rl-accountsel">
				<?php if($_SESSION['customer_id']) { ?>
                	<a href="<?php echo zen_href_link(FILENAME_LOGOFF, '', 'SSL');?>" rel="nofollow"><?php echo HEADER_TITLE_LOGOFF;?></a>
                <?php } else { ?>
                <a id="myAccount" title="S`identifier | Enregistrer" href="<?php echo zen_href_link(FILENAME_LOGIN, '', 'SSL');?>">Login | Register</a><span id="rl-welcome-back" style="display:none;">Welcome</span>
                <?php } ?>
            </li>
			<li id="rl-bagmenu"><a id="shoppingBag" title="Mon panier" href="<?php echo zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL');?>">Shopping Bag <span>(</span><span id="cartItemCount"><?php echo $_SESSION['cart']->count_contents() ?></span><span>)</span></a></li>
		</ul>
    </div>
    <div id="rl-globalnav-container">
    	<div id="rl-searcharea">
    		<form method="get" action="index.php?main_page=advanced_search_result" name="quick_find_header">
			<input type="hidden" value="advanced_search_result" name="main_page">
			<input type="hidden" value="1" name="search_in_description">
			<input id="sli_search_1" type="text" onblur="if (this.value == '') this.value = 'Search';" onfocus="if (this.value == 'Search') this.value = '';" value="" style="" maxlength="" size="" name="keyword">
            <input id="searchBtn" type="submit" value="">
            </form>
			<?php //require(DIR_WS_MODULES.'sideboxes/search_header.php');?>
        </div>
        <div id="nav-menu">
    <div class="nav-container">
        <ul id="nav">
        
        <li class="main-li active" id=""><a href="" class="">Home</a></li>
        <li class="level0 nav-1 parent" id=""><a href="cat-eye-sunglasses-c-3.html" class="">Categories</a><ul class="level0 ul-2">
 <?php 
			$categories=new category_tree;

			$row = $categories->zen_category_sub(0);
			$y = 0;
			foreach($row as $key=>$currow)
	//		for($i=0;$i<sizeof($row);$i++)
			{
				if($key!="") {
				echo '<li id="'.$currow['name'].'" class="level0 nav-1 parent"><a href="' . zen_href_link(FILENAME_DEFAULT, 'cPath='.$currow['path']) . '" rel="dropmenu'.$y.'">'.$currow['name'].'<span id="pg" class="pgonow"></span></a>';
							
			/*$subrows = $categories->zen_category_sub($key);
			if($subrows){echo '<ul class="level0 ul-2">';}
			$i=1;
			foreach($subrows as $skey=>$scurrow)	//		for($i=0;$i<sizeof($row);$i++)
			{   
				$count=sizeof($subrows);
				
				
				if($skey!="") {				
				
				
				echo '<li id="" class="level1 nav-1-1 first"><a href="' . zen_href_link(FILENAME_DEFAULT, 'cPath='.$scurrow['path']) . '" rel="dropmenu'.$y.'">'.$scurrow['name'].'</a><ul class="level1 ul-3">';
				$y++;
				$s_subrows = $categories->zen_category_sub($skey);	
				foreach($s_subrows as $s_skey=>$s_scurrow){
					if($s_skey!=''){
					
					echo '<li id="" class="level2 nav-1-2 first"><a href="' . zen_href_link(FILENAME_DEFAULT, 'cPath='.$s_scurrow['path']) . '" rel="dropmenu'.$y.'">'.$s_scurrow['name'].'</a></li>';
					}
				}				
	          
			   echo '</ul></li>';
			   
			   
			  }
			  $i++;
			  
			  }
	if($subrows){echo '</ul>';}*/
	echo '</li>';
					
			}
				
			}
?>



</ul></li>
<li><a href="<?php echo zen_href_link(FILENAME_CONTACT_US);?>">Contact Us</a></li>
<li><a href="<?php echo zen_href_link(FILENAME_EZPAGES, 'id=7');?>">Privacy Policy</a></li>
<li><a href="<?php echo zen_href_link(FILENAME_EZPAGES, 'id=1');?>">About Us</a></li>

</ul></div></div>
    </div>
</div>


<?php /*?><div id="headerWrapper">
	<div class="main-width">
    	<div id="header_fb"></div>
        <div id="h_tl_2">
			<?php echo '<a rel="nofollow" href="' . HTTP_SERVER . DIR_WS_CATALOG . '">'; ?><?php echo HEADER_TITLE_CATALOG; ?></a> |
            <?php if($_SESSION['customer_id']) { ?>
            <a href="<?php echo zen_href_link(FILENAME_LOGOFF, '', 'SSL');?>" rel="nofollow"><?php echo HEADER_TITLE_LOGOFF;?></a> |
            <?php } else { ?>
            <a href="<?php echo zen_href_link(FILENAME_LOGIN, '', 'SSL');?>" rel="nofollow"><?php echo HEADER_TITLE_LOGIN;?></a>
            <?php } ?>     	
        </div>
        
			<form method="get" action="index.php?main_page=advanced_search_result" name="quick_find_header">
			<input type="hidden" value="advanced_search_result" name="main_page">
			<input type="hidden" value="1" name="search_in_description">
			<input id="sli_search_1" type="text" onblur="if (this.value == '') this.value = 'Entrez vos mots clefs ici';" onfocus="if (this.value == 'Entrez vos mots clefs ici') this.value = '';" value="" style="" maxlength="" size="" name="keyword">
            <input id="searchBtn" type="image" value="">
            </form>
			<?php //require(DIR_WS_MODULES.'sideboxes/search_header.php');?>


			<a href="" id="h_l_1"></a>
			<div id="h_l_2">
            	<a href="<?php echo zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL');?>" rel="nofollow">
                <span><?php echo '('.$_SESSION['cart']->count_contents().' items)' ?></span><?php echo $currencies->format($_SESSION['cart']->show_total());?>
                <a href="<?php echo zen_href_link(FILENAME_SHOPPING_CART, '', 'SSL');?>" rel="nofollow">Panier</a>
                </a>
            </div>
            
		<div class="header_right fr">
				<ul class="topnav">
					
					<li>
							<!-- ========== CURRENCIES ========= -->
<?php #CURRENCIES START ?>
	<?php echo zen_draw_form('currencies', zen_href_link(basename(ereg_replace('.php','', $PHP_SELF)), '', $request_type, false), 'get');?>
	
	<?php if (isset($currencies) && is_object($currencies)) {
	reset($currencies->currencies);
	$currencies_array = array();
	while (list($key, $value) = each($currencies->currencies)) {
	//$currencies_array[] = array('id' => $key, 'text' => $value['title']);
	$currencies_array[] = array('id' => $key, 'text' => $key);
	 }
	$hidden_get_variables = '';
	reset($_GET);
	while (list($key, $value) = each($_GET)) {
	if ( ($key != 'currency') && ($key != zen_session_name()) && ($key != 'x') && ($key != 'y') ) {
	$hidden_get_variables .= zen_draw_hidden_field($key, $value);
	}
	}
	}?>
	
	<?php  echo "".zen_draw_pull_down_menu('currency', $currencies_array, $_SESSION['currency'], ' onchange="this.form.submit();"') . $hidden_get_variables . zen_hide_session_id();?></form>
	
	
<?php #CURRENCIES END ?>
<!-- ====================================== -->
					</li>
				</ul>
				<p class="blank"></p>

		</div>
		<div class="clearBoth"></div>
        
<div id="nav-menu">
<div class="nav-container">
<ul id="nav">

<li class="main-li active" id=""><a href="" class="">Accueil</a></li>

 <?php 
			$categories=new category_tree;

			$row = $categories->zen_category_sub(0);
			$y = 0;
			foreach($row as $key=>$currow)
	//		for($i=0;$i<sizeof($row);$i++)
			{
				if($key!="") {
				echo '<li id="'.$currow['name'].'" class="level0 nav-1 parent" onmouseout="toggleMenu(this,0)" onmouseover="toggleMenu(this,1)"><a href="' . zen_href_link(FILENAME_DEFAULT, 'cPath='.$currow['path']) . '" rel="dropmenu'.$y.'">'.$currow['name'].'<span id="pg" class="pgonow"></span></a><ul class="level0 ul-2">';
				
			
			$subrows = $categories->zen_category_sub($key);
			
			foreach($subrows as $skey=>$scurrow)	//		for($i=0;$i<sizeof($row);$i++)
			{   
				
				if($skey!="") {
				
				
				echo '<li id="" class="level1 nav-1-1 first"><a href="' . zen_href_link(FILENAME_DEFAULT, 'cPath='.$scurrow['path']) . '" rel="dropmenu'.$y.'">'.$scurrow['name'].'</a></li>';
		
				
	}}
	
	echo '</ul></li>';
	
	
				
			}

				

				
			}
?>

</ul></div>
	  </div>
	  </div>
	</div><?php */?>




<?php if (EZPAGES_STATUS_HEADER == '1' or (EZPAGES_STATUS_HEADER == '2' and (strstr(EXCLUDE_ADMIN_IP_FOR_MAINTENANCE, $_SERVER['REMOTE_ADDR'])))) { ?>
<!--bof-header ezpage links-->
<?php //require($template->get_template_dir('tpl_ezpages_bar_header.php',DIR_WS_TEMPLATE, $current_page_base,'templates'). '/tpl_ezpages_bar_header.php'); ?>
<!--eof-header ezpage links-->
<?php } ?>

<?php } ?>
</div>