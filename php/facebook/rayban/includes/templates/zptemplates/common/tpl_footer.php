<?php
/**
 * Common Template - tpl_footer.php
 *
 * this file can be copied to /templates/your_template_dir/pagename<br />
 * example: to override the privacy page<br />
 * make a directory /templates/my_template/privacy<br />
 * copy /templates/templates_defaults/common/tpl_footer.php to /templates/my_template/privacy/tpl_footer.php<br />
 * to override the global settings and turn off the footer un-comment the following line:<br />
 * <br />
 * $flag_disable_footer = true;<br />
 *
 * @package templateSystem
 * @copyright Copyright 2003-2005 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_footer.php 4821 2006-10-23 10:54:15Z drbyte $
 */
require(DIR_WS_MODULES . zen_get_module_directory('footer.php'));
?>

<?php
if (!isset($flag_disable_footer) || !$flag_disable_footer) {
?>
<div id="rrfooter" class="chain">
	<ul class="rrcolumn alpha">
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
	<ul class="rrcolumn second">
		<li class="first">Customer service</li>
		<li><a href="<?php echo zen_href_link(FILENAME_CONTACT_US);?>">Send us an email</a></li>
	</ul>
	<ul class="rrcolumn third">
		<li class="first">Shipping & Returns</li>
		<li><a href="index.php?main_page=account">Order tracking</a></li>
		<li><a href="<?php echo zen_href_link(FILENAME_EZPAGES, 'id=6');?>">Shipping & Returns</a></li>
	</ul>
	<ul class="rrcolumn omega">
		<li class="first">The company</li>
		<li><a href="<?php echo zen_href_link(FILENAME_EZPAGES, 'id=1');?>">About Us</a></li>
	</ul>
	<div id="first-to-know" class="column flr alpha omega">
		<div class="footNavTitle">Receive Updates From TIFFANY</div>
        <div class="subscribe">
            <p class="privacy">
                <a href="<?php echo zen_href_link(FILENAME_EZPAGES, 'id=7');?>" target="_blank">Click here</a>  to the privacy policy of TIFFANY
            </p>
        </div>
	</div>
	<ul id="bottomlinks">
		<li><a href="index.php?main_page=site_map">Site Map</a></li>
		<li><a href="<?php echo zen_href_link(FILENAME_EZPAGES, 'id=7');?>">Privacy Notice</a></li>
		<li><a href=""><?php echo FOOTER_TEXT_BODY; ?></a></li>
	</ul>
</div>

<?php /*?><div class="back_to_top" style="display: block;" id=goTopBtn><a rel="nofollow" href="javascript:void(0)"></a></div>
<SCRIPT type=text/javascript>goTopEx();</SCRIPT><?php */?>

<div style="display:none">
<!-- GoStats JavaScript Based Code -->
<script type="text/javascript" src="https://ssl.gostats.com/js/counter.js"></script>
<script type="text/javascript">_gos='monster.gostats.cn';_goa=486081;
_got=4;_goi=1;_goz=0;_god='hits';_gol='流量统计站';_GoStatsRun();</script>
<noscript><a target="_blank" title="流量统计站" 
href="http://gostats.cn"><img alt="流量统计站" 
src="https://ssl.gostats.com/bin/count/a_486081/t_4/i_1/z_0/show_hits/ssl_monster.gostats.cn/counter.png" 
style="border-width:0" /></a></noscript>
<!-- End GoStats JavaScript Based Code -->

</div>




<?php
} // flag_disable_footer
?>

<?php
//load all site-wide jscript_*.php files from includes/templates/YOURTEMPLATE/jscript, alphabetically
  $directory_array = $template->get_template_part($template->get_template_dir('.php',DIR_WS_TEMPLATE, $current_page_base,'jscript'), '/^jscript_/', '.php');
  while(list ($key, $value) = each($directory_array)) {
//include content from all site-wide jscript_*.php files from includes/templates/YOURTEMPLATE/jscript, alphabetically.
//These .PHP files can be manipulated by PHP when they're called, and are copied in-full to the browser page
    require($template->get_template_dir('.php',DIR_WS_TEMPLATE, $current_page_base,'jscript') . '/' . $value); echo "\n";
  }
//include content from all page-specific jscript_*.php files from includes/modules/pages/PAGENAME, alphabetically.
  $directory_array = $template->get_template_part($page_directory, '/^jscript_/');
  while(list ($key, $value) = each($directory_array)) {
//include content from all page-specific jscript_*.php files from includes/modules/pages/PAGENAME, alphabetically.
//These .PHP files can be manipulated by PHP when they're called, and are copied in-full to the browser page
    require($page_directory . '/' . $value); echo "\n";
  }
?>