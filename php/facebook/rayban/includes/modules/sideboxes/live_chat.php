<?php
require($template->get_template_dir('tpl_live_chat.php',DIR_WS_TEMPLATE, $current_page_base,'sideboxes'). '/tpl_live_chat.php');
$title =  'Live Chat';
$title_link = false;
require($template->get_template_dir($column_box_default, DIR_WS_TEMPLATE, $current_page_base,'common') . '/' . $column_box_default);
?>