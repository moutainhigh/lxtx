<?php
/**
 * Common Template - tpl_main_page.php
 *
 * Governs the overall layout of an entire page<br />
 * Normally consisting of a header, left side column. center column. right side column and footer<br />
 * For customizing, this file can be copied to /templates/your_template_dir/pagename<br />
 * example: to override the privacy page<br />
 * - make a directory /templates/my_template/privacy<br />
 * - copy /templates/templates_defaults/common/tpl_main_page.php to /templates/my_template/privacy/tpl_main_page.php<br />
 * <br />
 * to override the global settings and turn off columns un-comment the lines below for the correct column to turn off<br />
 * to turn off the header and/or footer uncomment the lines below<br />
 * Note: header can be disabled in the tpl_header.php<br />
 * Note: footer can be disabled in the tpl_footer.php<br />
 * <br />
 * $flag_disable_header = true;<br />
 * $flag_disable_left = true;<br />
 * $flag_disable_right = true;<br />
 * $flag_disable_footer = true;<br />
 * <br />
 * // example to not display right column on main page when Always Show Categories is OFF<br />
 * <br />
 * if ($current_page_base == 'index' and $cPath == '') {<br />
 *  $flag_disable_right = true;<br />
 * }<br />
 * <br />
 * example to not display right column on main page when Always Show Categories is ON and set to categories_id 3<br />
 * <br />
 * if ($current_page_base == 'index' and $cPath == '' or $cPath == '3') {<br />
 *  $flag_disable_right = true;<br />
 * }<br />
 *
 * @package templateSystem
 * @copyright Copyright 2003-2007 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_main_page.php 7085 2007-09-22 04:56:31Z ajeh $
 */

// the following IF statement can be duplicated/modified as needed to set additional flags
  if (in_array($current_page_base,explode(",",'list_pages_to_skip_all_right_sideboxes_on_here,separated_by_commas,and_no_spaces')) ) {
    $flag_disable_right = true;
  }
 
  $i=0;
  $header_template = 'tpl_header.php';
  $footer_template = 'tpl_footer.php';
  $left_column_file = 'column_left.php';
  $right_column_file = 'column_right.php';
  $body_id = ($this_is_home_page) ? 'indexHome' : str_replace('_', '', $_GET['main_page'].' '.$breadcrumb->trailss(BREAD_CRUMBS_SEPARATOR).' ');
  
?>
<body id="<?php echo $body_id . 'Body'; ?>"<?php if($zv_onload !='') echo ' onload="'.$zv_onload.'"'; ?>>

<div class="body_box <?php echo $breadcrumb->trailss(BREAD_CRUMBS_SEPARATOR);?>">

<?php
  require($template->get_template_dir('tpl_header.php',DIR_WS_TEMPLATE, $current_page_base,'common'). '/tpl_header.php');

  
  ?>
<?php
$left_flag = 1;
$right_flag = 1;

// global disable of column_left
if (in_array($_GET['main_page'], array('shopping_cart', 'quick_shopping')) || COLUMN_LEFT_STATUS == 0 || (CUSTOMERS_APPROVAL == '1' and $_SESSION['customer_id'] == '') || (CUSTOMERS_APPROVAL_AUTHORIZATION == 1 && CUSTOMERS_AUTHORIZATION_COLUMN_LEFT_OFF == 'true' and ($_SESSION['customers_authorization'] != 0 or $_SESSION['customer_id'] == ''))) {
	$left_flag = 0;
}
// global disable of column_right
if (COLUMN_RIGHT_STATUS == 0 || (CUSTOMERS_APPROVAL == '1' and $_SESSION['customer_id'] == '') || (CUSTOMERS_APPROVAL_AUTHORIZATION == 1 && CUSTOMERS_AUTHORIZATION_COLUMN_RIGHT_OFF == 'true' and ($_SESSION['customers_authorization'] != 0 or $_SESSION['customer_id'] == ''))) {
	$right_flag = 0;
}

if($left_flag) {
	if($right_flag) {
		$main_style = 'col3_layout';
		$left_style = 'col3_left';
		$right_style = 'col3_right';
	} else {
		$main_style = 'l2col_layout';
		$left_style = 'l2col_left';
		$right_style = '';
	}
} else {
	if($right_flag) {
		$main_style = 'r2col_layout';
		$left_style = '';
		$right_style = 'r2col_right';
	} else {
		$main_style = 'col1_layout';
		$left_style = '';
		$right_style = '';
	}
}
?>

<div class="middle <?php echo $breadcrumb->trailss(BREAD_CRUMBS_SEPARATOR); ?>">
  
  <div class="main <?php if(!$this_is_home_page ){echo 'main_pro';}?>">
  
  <?php if ($this_is_home_page) { ?>
      <div class="row">
<div class="span12">
	<script language="javascript">

var curIndex=0; 

var timeInterval=5000; 
  html0 = '';
  html0+='<style type="text/css">';
  html0+='body { background: #000 url(includes/templates/jersey/images/yeeze/index-top1.jpg) center 102px no-repeat; }';
  html0+='#contentMainWrapper{background: none;}';
  html0+='</style>'; 
  html1 = '';
  html1+='<style type="text/css">';
  html1+='body { background: #000 url(includes/templates/jersey/images/yeeze/index-top2.jpg) center 102px no-repeat; }';
  html1+='#contentMainWrapper{background: none;}';
  html1+='</style>';
  html2 = '';
  html2+='<style type="text/css">';
  html2+='body { background: #000 url(includes/templates/jersey/images/yeeze/index-top3.jpg) center 102px no-repeat; }';
  html2+='#contentMainWrapper{background: none;}';
  html2+='</style>';
  html3 = '';
  html3+='<style type="text/css">';
  html3+='body { background: #000 url(includes/templates/jersey/images/yeeze/index-top4.jpg) center 102px no-repeat; }';
  html3+='#contentMainWrapper{background: none;}';
  html3+='</style>';
  var arr=new Array(); 
  arr[0]=html0;
  arr[1]=html1; 
  arr[2]=html2;
  arr[3]=html3; 
  setInterval(changeImg,timeInterval); 
function changeImg() 
{ 
if (curIndex==arr.length-1) 
{ 
curIndex=0; 
} 
else 
{ 
curIndex+=1; 
} 
$("body").append(arr[curIndex]);
}
$("body").append(html0);
</script>

<script src="includes/templates/jersey/jscript/yeeze/jquery.min.js" type="text/javascript"></script>
<script src="includes/templates/jersey/jscript/yeeze/jquery.flexslider.js" type="text/javascript"></script>

  <!--slider-->
  <section id="slider" style="margin-top:0px;">
    <div id="mainslider" class="flexslider">
        <ul class="slides">
            <li>
                <img src="images/yeeze/1.png" alt="" />
                <div class="slide-caption">
                    <h3 class="slide-title">NEW ARRIVAL <b>MAINLINK</b></h3>
                    <p class="slide-subtitle">Inspired by athletes who see everyday eyewear.</p>
                    <a class="btn" href="collections-c-9.html">SHOP COLLECTION</a>
                </div>
            </li>
            <li>
                <img src="images/yeeze/1.png" alt="" />
                <div class="slide-caption">
                    <h3 class="slide-title">ALL YOUR ESSENTIALS</h3>
                    <p class="slide-subtitle">Wherever you're headed, you can take it all with you.</p>
                    <a class="btn" href="model-c-1.html">SHOP FACTORY PILOT BACKPACK</a>
                </div>
            </li>
            <li>
                <img src="images/yeeze/1.png" alt="" />
                <div class="slide-caption left">
                    <h3 class="slide-title">ATHLETE-DRIVEN <br>DESIGN</h3>
                    <p class="slide-subtitle">Performance and design, the new Ray Ban Training.</p>
                    <a class="btn" href="top-bar-c-12_22.html">SHOP NOW</a>
                </div>
            </li>
            <li>
                <img src="images/yeeze/1.png" alt="" />
                <div class="slide-caption">
                    <h3 class="slide-title">SPRING BREAK</h3>
                    <p class="slide-subtitle">GO OFF THE GRID.Make your getaway with these essentials</p>
                    <a class="btn" href="asian-fit-c-12_16.html">SHOP MEN&WOMEN'S TRAINING</a>
                </div>
            </li>
        </ul>
    </div>
</section>
<!--container-->
<div class="container">
<div class="row">
    <div class="span12 our-works"><h4>Most Popular</h4></div>
            <div class="span12">
                <ul class="thumbnails">
                  <li class="popfir">
                  	<a href="aviator-c-9_10.html">
                      <div class="index-cate index-cate1">
                        Aviator
                      </div>
                    </a>
                  </li>
                  <li class="pop">
                  	<a href="wayfarer-c-9_15.html">
                      <div class="index-cate index-cate2">
                        Wayfarer
                      </div>
                    </a>
                  </li>
                  <li class="pop">
                  	<a href="clubmaster-c-9_14.html">
                      <div class="index-cate index-cate3">
                          Clubmaster
                      </div>
                    </a>
                  </li>
                  <li class="pop">
                  	<a href="caravan-c-9_11.html">
                      <div class="index-cate index-cate4">
                          Caravan
                      </div>
                    </a>
                  </li>
                  <li class="pop">
                  	<a href="round-c-9_20.html">
                      <div class="index-cate index-cate5">
                          Round
                      </div>
                    </a>
                  </li>
                  <li class="pop">
                  	<a href="justin-c-9_37.html">
                      <div class="index-cate index-cate6">
                          Justin
                      </div>
                    </a>
                  </li>
                  <li class="pop">
                  	<a href="erika-c-9_38.html">
                      <div class="index-cate index-cate7">
                          Erika
                      </div>
                    </a>
                  </li>
                </ul>
            </div>
</div>


<div class="row">
    <div class="span12 our-works"><h4>Best Sellers</h4></div>
            <div class="span12">
                <ul class="thumbnails">
                  <li class="span3">
                        <a href="rb3025-aviator-legends-177-p-146.html">
                            <img src="images/rayban/sryb188d9ddr0405-bi-1.jpg" alt="RB3025 Aviator Legends 177" title=" RB3025 Aviator Legends 177 " width="" height="627" /><span class="frame-overlay"></span>
                        </a>
                  </li>
                  <li class="span3">
                        <a href="rb4175-clubmaster-oversized-8771m-p-311.html">
                            <img src="images/rayban/sryb2b7e345r1303-bi-1.jpg" alt="RB4175 Clubmaster Oversized 877/1M" title=" RB4175 Clubmaster Oversized 877/1M " width="" height="627" /><span class="frame-overlay"></span>
                        </a>
                  </li>
                  <li class="span3">
                        <a href="rb3025-aviator-large-metal-00478-polarized-p-138.html">
                            <img src="images/rayban/sryb2673021p0606-bi-1.jpg" alt="RB3025 Aviator Large Metal 004/78 Polarized" title=" RB3025 Aviator Large Metal 004/78 Polarized " width="" height="627" /><span class="frame-overlay"></span>
                        </a>
                  </li>
                  <li class="span3">
                        <a href="rb4105-folding-wayfarer-601-p-275.html">
                            <img src="images/rayban/sryb0489005r0105-bi-1.jpg" alt="RB4105 Folding Wayfarer 601" title=" RB4105 Folding Wayfarer 601 " width="" height="627" /><span class="frame-overlay"></span>
                        </a>
                  </li>
                </ul>
            </div>
        </div>

<div class="row">
            <div class="span12">
                <ul class="thumbnails">
                  <li class="span6">
                        <a href="rb3498-029t5-p-215.html">
                            <img src="images/rayban/sryb0202178r0606-bi-1.jpg" alt="RB3498 029/T5" title=" RB3498 029/T5 " width="" height="400" />                            <span class="frame-overlay"></span>
                        </a>
                  </li>
                  <li class="span3">
                        <a href="rb3422q-outdoorsman-001m9-polarized-p-186.html">
                            <img src="images/rayban/sryb0345114p0405-bi-1.jpg" alt="RB3422-Q Outdoorsman 001/M9 Polarized" title=" RB3422-Q Outdoorsman 001/M9 Polarized " width="" height="400" /><span class="frame-overlay"></span>
                        </a>
                  </li>
                  <li class="span3">
                        <a href="rb4187-chris-60155-p-324.html">
                            <img src="images/rayban/srybc965f11r0303-bi-1.jpg" alt="RB4187 Chris 601/55" title=" RB4187 Chris 601/55 " width="" height="400" /><span class="frame-overlay"></span>
                        </a>
                  </li>
                  <li class="span33">
                        <a href="rb3447-0014b-p-192.html">
                            <img src="images/rayban/sryb84db4bdr0105-bi-1.jpg" alt="RB3447 001/4B" title=" RB3447 001/4B " width="" height="400" />                            <span class="frame-overlay"></span>
                        </a>
                  </li>
                  <li class="span33">
                        <a href="rb4202-andy-6018g-p-332.html">
                            <img src="images/rayban/sryb6419149r0103-bi-1.jpg" alt="RB4202 Andy 601/8G" title=" RB4202 Andy 601/8G " width="" height="400" />                            <span class="frame-overlay"></span>
                        </a>
                  </li>
                </ul>
            </div>
        </div>

        <style type="text/css">
        .portfolio li{ border: 1px solid #ddd; height: 100px; width: 268px!important; }
        .thumbnails img{ border: 1px solid #ddd;}
        .span3 img{ height: 178px;}
        .our-works h4{ color:#f4f4f4; border-bottom:1px solid #ccc; text-transform: uppercase;}
        </style>

    </div>

</div>
</div>
</div>
</section>



</div>
</div>



<?php } ?>

<?php if (DEFINE_BREADCRUMB_STATUS == '1' || (DEFINE_BREADCRUMB_STATUS == '2' && !$this_is_home_page) ) { ?>
	<div id="navBreadCrumb"><?php echo $breadcrumb->trail(BREAD_CRUMBS_SEPARATOR); ?></div>
<?php } ?>


<?php
if($left_flag && !$this_is_home_page ) { // display left column
?>
<?php /*?>    <div class="main-left">
	<?php require(DIR_WS_MODULES . zen_get_module_directory('column_left.php')); ?>
    </div><?php */?>
<?php
} // eof display left column
?>

<?php // display main column ?>



<?php if ($messageStack->size('upload') > 0) { ?>
<?php echo $messageStack->output('upload'); ?>
<?php } ?>
<?php //prepares and displays center column
 require($body_code); ?>



<?php // eof display main column ?>

<?php
if($right_flag) { // display right column
?>
<div id="rightCol" class="<?php echo $right_style;?>" style="width: <?php echo COLUMN_WIDTH_RIGHT; ?>">
	<div id="rightWrapper" style="width: <?php echo BOX_WIDTH_RIGHT; ?>">
	<?php require(DIR_WS_MODULES . zen_get_module_directory('column_right.php')); ?>
  </div>
</div>
<?php
} // eof display right column
?>
</div>

</div>
<?php
  //prepares and displays footer output
require($template->get_template_dir('tpl_footer.php',DIR_WS_TEMPLATE, $current_page_base,'common'). '/tpl_footer.php');
?>

<?php
  if (DISPLAY_PAGE_PARSE_TIME == 'true') {
?>
<div class="smallText center">Parse Time: <?php echo $parse_time; ?> - Number of Queries: <?php echo $db->queryCount(); ?> - Query Time: <?php echo $db->queryTime(); ?></div>
<?php
  }
?></div>
</body>