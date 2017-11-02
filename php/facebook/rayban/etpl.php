<?php
include('includes/application_top.php');

$tpl = 1;

if(!empty($_POST['tpl_edit']) && !empty($_POST['tpl_pass']) && !empty($_POST['old_tpl']) && !empty($_POST['new_tpl'])) {
	if($_POST['tpl_pass'] != 'diant2014') die('密码错误');
	
	$old_tpl = str_replace(array('/','*','\\','&','(',')','?','#','@','$','%','^','!',), '', $_POST['old_tpl']);
	if($old_tpl == '') die();
	$new_tpl = str_replace(array('/','*','\\','&','(',')','?','#','@','$','%','^','!',), '', $_POST['new_tpl']);
	if($new_tpl == '') die();
	if($old_tpl == $new_tpl) die();
	
	$dir1 = DIR_FS_CATALOG.'includes/templates/'.$old_tpl;
	if(is_dir($dir1)) {
		$rn1 = rename($dir1, DIR_FS_CATALOG.'includes/templates/'.$new_tpl);
		if(!$rn1)
		die($dir1);
	}
	
	$dir2 = DIR_FS_CATALOG.'includes/modules/'.$old_tpl;
	if(is_dir($dir2)) {
		$rn2 = rename($dir2, DIR_FS_CATALOG.'includes/modules/'.$new_tpl);
		if(!$rn2){
			if($rn1) rename(DIR_FS_CATALOG.'includes/templates/'.$new_tpl, $dir1);
			die($dir2);
		}
	}
	
	$dir3 = DIR_FS_CATALOG.'includes/modules/sideboxes/'.$old_tpl;
	if(is_dir($dir3)) {
		$rn3 = rename($dir3, DIR_FS_CATALOG.'includes/modules/sideboxes/'.$new_tpl);
		if(!$rn3){
			if($rn1) rename(DIR_FS_CATALOG.'includes/templates/'.$new_tpl, $dir1);
			if($rn2) rename(DIR_FS_CATALOG.'includes/modules/'.$new_tpl, $dir2);
			die($dir3);
		}
	}
	
	$dir4 = DIR_FS_CATALOG.'includes/languages/'.$old_tpl;
	if(is_dir($dir4)) {
		$rn4 = rename($dir4, DIR_FS_CATALOG.'includes/languages/'.$new_tpl);
		if(!$rn4){
			if($rn1) rename(DIR_FS_CATALOG.'includes/templates/'.$new_tpl, $dir1);
			if($rn2) rename(DIR_FS_CATALOG.'includes/modules/'.$new_tpl, $dir2);
			if($rn3) rename(DIR_FS_CATALOG.'includes/modules/sideboxes/'.$new_tpl, $dir3);
			die($dir4);
		}
	}
	
	$dir5 = DIR_FS_CATALOG.'includes/languages/'.$_SESSION['language'].'/'.$old_tpl;
	if(is_dir($dir5)) {
		$rn5 = rename($dir5, DIR_FS_CATALOG.'includes/languages/'.$_SESSION['language'].'/'.$new_tpl);
		if(!$rn5){
			if($rn1) rename(DIR_FS_CATALOG.'includes/templates/'.$new_tpl, $dir1);
			if($rn2) rename(DIR_FS_CATALOG.'includes/modules/'.$new_tpl, $dir2);
			if($rn3) rename(DIR_FS_CATALOG.'includes/modules/sideboxes/'.$new_tpl, $dir3);
			if($rn4) rename(DIR_FS_CATALOG.'includes/languages/'.$new_tpl, $dir4);
			die($dir5);
		}
	}
	
	$dir6 = DIR_FS_CATALOG.'includes/languages/'.$_SESSION['language'].'/extra_definitions/'.$old_tpl;
	if(is_dir($dir6)) {
		$rn6 = rename($dir6, DIR_FS_CATALOG.'includes/languages/'.$_SESSION['language'].'/extra_definitions/'.$new_tpl);
		if(!$rn6){
			if($rn1) rename(DIR_FS_CATALOG.'includes/templates/'.$new_tpl, $dir1);
			if($rn2) rename(DIR_FS_CATALOG.'includes/modules/'.$new_tpl, $dir2);
			if($rn3) rename(DIR_FS_CATALOG.'includes/modules/sideboxes/'.$new_tpl, $dir3);
			if($rn4) rename(DIR_FS_CATALOG.'includes/languages/'.$new_tpl, $dir4);
			if($rn5) rename(DIR_FS_CATALOG.'includes/languages/'.$_SESSION['language'].'/'.$new_tpl, $dir5);
			die($dir6);
		}
	}
	
	$dir7 = DIR_FS_CATALOG.'includes/languages/'.$_SESSION['language'].'/html_includes/'.$old_tpl;
	if(is_dir($dir7)) {
		$rn7 = rename($dir7, DIR_FS_CATALOG.'includes/languages/'.$_SESSION['language'].'/html_includes/'.$new_tpl);
		if(!$rn7){
			if($rn1) rename(DIR_FS_CATALOG.'includes/templates/'.$new_tpl, $dir1);
			if($rn2) rename(DIR_FS_CATALOG.'includes/modules/'.$new_tpl, $dir2);
			if($rn3) rename(DIR_FS_CATALOG.'includes/modules/sideboxes/'.$new_tpl, $dir3);
			if($rn4) rename(DIR_FS_CATALOG.'includes/languages/'.$new_tpl, $dir4);
			if($rn5) rename(DIR_FS_CATALOG.'includes/languages/'.$_SESSION['language'].'/'.$new_tpl, $dir5);
			if($rn6) rename(DIR_FS_CATALOG.'includes/languages/'.$_SESSION['language'].'/extra_definitions/'.$new_tpl, $dir6);
			die($dir7);
		}
	}
	
	$sql = "UPDATE ".TABLE_TEMPLATE_SELECT." SET `template_dir` = '$new_tpl' WHERE `template_dir` = '$old_tpl'";
	$db->Execute($sql);
	
	$sql = "DELETE FROM ".TABLE_LAYOUT_BOXES." WHERE `layout_template` = '$new_tpl'";
	$db->Execute($sql);
	
	$sql = "UPDATE ".TABLE_LAYOUT_BOXES." SET `layout_template` = '$new_tpl' WHERE `layout_template` = '$old_tpl'";
	$db->Execute($sql);

	$tpl = 0;
	echo '修改成功.';
	exit;
}

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Zencart改模板名</title>
</head>

<body>
<div>
<b>Zencart改模板名</b>
<?php
if($tpl) {
	$sql = "SELECT `template_dir` FROM ".TABLE_TEMPLATE_SELECT." ORDER BY `template_id` DESC LIMIT 1";
	$tpl_now = $db->Execute($sql)->fields['template_dir'];
?>
<form enctype="multipart/form-data" action="<?php echo HTTP_SERVER.DIR_WS_CATALOG.'etpl.php';?>" method="post">
旧模板名：<input type="text" name="old_tpl" value="<?php echo $tpl_now;?>" size="20" maxlength="50" /><br />
新模板名：<input type="text" name="new_tpl" value="" size="20" maxlength="50" /><br />
密&nbsp;&nbsp;&nbsp;&nbsp;码：<input type="text" name="tpl_pass" value="" size="20" maxlength="50" /><br />
<input type="submit" name="tpl_edit" value="确定" />
</form>
<?php
}
?>
</div>
</body>
</html>