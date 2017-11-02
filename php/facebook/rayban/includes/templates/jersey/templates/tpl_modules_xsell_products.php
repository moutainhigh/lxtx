<?php
//
// +----------------------------------------------------------------------+
// |zen-cart Open Source E-commerce                                       |
// +----------------------------------------------------------------------+
// | Copyright (c) 2003 The zen-cart developers                           |
// |                                                                      |
// | http://www.zen-cart.com/index.php                                    |
// |                                                                      |
// | Portions Copyright (c) 2003 osCommerce                               |
// +----------------------------------------------------------------------+
// | This source file is subject to version 2.0 of the GPL license,       |
// | that is bundled with this package in the file LICENSE, and is        |
// | available through the world-wide-web at the following url:           |
// | http://www.zen-cart.com/license/2_0.txt.                             |
// | If you did not receive a copy of the zen-cart license and are unable |
// | to obtain it through the world-wide-web, please send a note to       |
// | license@zen-cart.com so we can mail you a copy immediately.          |
// +----------------------------------------------------------------------+
// $Id: tpl_modules_xsell_products.php 277 2005-09-08 23:03
//
?>
<!-- bof: tpl_modules_xsell_products -->
<table  width="100%" border="0" cellspacing="0" cellpadding="0" class="centerbox">
  <tr class="centerboxheading">
    <td width="100%" class="centerboxheading" style="font-size:14px; font-weight:bold; padding-left:15px;"><?php echo $title; ?><br/><br/><br/></td>
  </tr>
  <tr>
    <td id="xsell" class="centerboxcontent" style="text-align:center; height:180px; padding-left:15px;">
<?php
  require($template->get_template_dir('tpl_list_box_content.php',DIR_WS_TEMPLATE, $current_page_base,'common'). '/tpl_list_box_content.php');
?>
    </td>
  </tr>
  <tr>
    <td class="centerboxfooter" height="5px">
    </td>
  </tr>
</table>
<br />
<!-- eof: tpl_modules_xsell_products -->