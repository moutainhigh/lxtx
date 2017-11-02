<?php

/**

* Template designed by 12leaves.com

* 12leaves.com - Free ecommerce templates and design services

*

* @copyright Copyright 2003-2006 Zen Cart Development Team

* @copyright Portions Copyright 2003 osCommerce

* @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0

* @version $Id: index.php 6550 2007-07-05 03:54:54Z drbyte $

*/    

?>


<div id="tab_nav">

	<ul class="list-style-none">

<?php

//if ($current_page_base == 'xxx') {	$active	= 'tab-active'; 

//	} else { $active = '';}

?>

		<li class="tm-catalog <?php echo $active;?>">
			<a id="catalog"></a>
        </li>



<?php

if ($current_page_base == 'account' || $current_page_base == 'login' || $current_page_base == 'account_edit' || $current_page_base == 'address_book' || $current_page_base == 'account_password' || $current_page_base == 'account_newsletters' || $current_page_base == 'account_notifications') { $active = 'tab-active'; 

	} else { $active = '';

}?>

		
        <li class="logo">
		<a name="top_index" href="<?php echo HTTP_SERVER.DIR_WS_CATALOG;?>"><img alt="logo" src="<?php echo HTTP_SERVER . DIR_WS_CATALOG . DIR_WS_TEMPLATE; ?>images/logo.png"></a>
        
        </li>

<li class="shopcart">
    <a href="<?php echo zen_href_link(FILENAME_SHOPPING_CART, '', 'NONSSL'); ?>"><!--我的购物车链接-->
	<span><?php echo $_SESSION['cart']->count_contents(); ?></span><!--购物车数量-->
    </a></li>
    
</ul>


   <?php require(DIR_WS_MODULES . $template_dir . '/categories_popup.php');  

	echo $content; ?>



</div>
<div class="elear"></div>



<!--搜索开始-->
<div class="layout-bottom_top">
<div class="genduo_top">Teams</div>
<div class="search_top">

<form method="get" action="<?php echo HTTP_SERVER.DIR_WS_CATALOG;?>index.php?main_page=advanced_search_result" name="quick_find_header">



  <input type="hidden" value="advanced_search_result" name="main_page">



  <input type="hidden" value="1" name="search_in_description">



  <input type="text" maxlength="30" size="6" name="keyword" value="Search">



  <input type="submit" value="Search">



</form>

</div>

</div>
<!--搜索结束-->