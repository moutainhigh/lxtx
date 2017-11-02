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

<footer>
<div class="container">
	<div class="row">
            <div class="span4">
                <p><img alt="" src="includes/templates/jersey/images/yeeze/foot.jpg"></p>
                <p>We are professional online company in the world. We offer quality and fashion Ray Ban Sunglasses for buyers all over the world.</p>
                <p>We guarantee a safe and secure shopping environment. We have successfully taken out the risk of online shopping-from product ordering, secure payment and delivering.</p>
            </div>
            <div class="span8">
                <div class="row">
                    <div class="span8"></div>
                    <div class="span8">

                    </div>
                </div>
            </div>
            <div class="span4">
                <p class="heading">About Us</p>
                <p>We will not collect and personally identifiable information (e.g. name, address, telephone number and e-mail address), also referred to herein as "personal information", about you unless you provide it to us voluntarily. </p>
                <!--<p class="heading">Subscribe</p>
                <p>Keep updated with our news. Your email is safe with us!</p>
                <div class="input-append">
                    <input type="text" class="span2" placeholder="Enter Email Address">
                    <button class="btn btn-inverse" type="button">Subscribe!</button>
                </div>-->
            </div>
            <div class="span4">
                <p class="heading">Corporation Information</p>
                <ul class="footer-navigate">
                    <li><a rel="nofollow" href="/">Home</a></li>
                    <li><a rel="nofollow" href="<?php echo zen_href_link(FILENAME_ABOUT);?>">About Us</a></li>
                    <li><a rel="nofollow" href="<?php echo zen_href_link(FILENAME_CONDITIONS);?>">Conditions of Use</a></li>
                    <li><a rel="nofollow" href="<?php echo zen_href_link(FILENAME_SHIPPING);?>">Shipping &amp; Returns</a></li>
                    <li><a rel="nofollow" href="<?php echo zen_href_link(FILENAME_EZPAGES, 'id=2');?>">Privacy Notice</a></li>
                    <li><a rel="nofollow" href="<?php echo zen_href_link(FILENAME_ACCOUNT, '', 'SSL');?>"><?php echo HEADER_TITLE_MY_ACCOUNT;?></a></li>
                </ul>
            </div>
</div>
<div style="clear:both"></div>
</div>


</footer>
<section id="footer-menu">
    <div class="container">
        <div class="row">
            <div class="span4">

                <p class="copyright"><?php echo FOOTER_TEXT_BODY; ?></p>
            </div>
            <div class="span8 hidden-phone">
                <ul class="pull-right">
                    <li><a href="<?php echo zen_href_link(FILENAME_EZPAGES, 'id=2');?>">Privacy</a></li>
                    <li><a href="<?php echo zen_href_link(FILENAME_ABOUT);?>">About Us</a></li>
                    <li><a href="<?php echo zen_href_link(FILENAME_CONTACT_US);?>">Contact Us</a></li>

                </ul>
            </div>
        </div>
    </div>
	<div style="clear:both"></div>
</section>




<div style="display:none">
<?php echo FOOTER_CNZZ_LA;?>
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


