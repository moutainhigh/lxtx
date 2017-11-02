<?php
$full_version = "full";
if(isset($_COOKIE['MobCookie'])){
	setcookie("MobCookie", $full_version, time()-1800);  
} else {
	//setcookie("TestCookie", $value);
	setcookie("MobCookie", $full_version, time()+1800); /* expire in 1/2 hour */ 
}; 

//if ((stripos($_SERVER['REQUEST_URI'], '.php')) !== false ) $r_link = $_SERVER['REQUEST_URI']; 
//else $r_link = '?';

//$r_link = $_SERVER['REQUEST_URI']; 
$link = $_GET['r_link'];
//echo $link;

header('Location:'. $link . '');
//header('Location: index.php');
exit;
?>
