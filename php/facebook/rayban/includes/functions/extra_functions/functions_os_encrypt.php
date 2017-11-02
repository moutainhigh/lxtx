<?php
function os_encrypt($string,$operation,$key='') {
	$key = md5($key);
	$key_length = strlen($key);
	$string = $operation == 'D' ? base64_decode($string) : substr(md5($string.$key),0,8).$string;
	$string_length = strlen($string);
	$rndkey = $box =array();
	$result = '';
	for($i=0; $i<=255; $i++) {
		$rndkey[$i] = ord($key[$i%$key_length]);
		$box[$i] = $i;
	}
	for($j=$i=0; $i<256; $i++) {
		$j = ($j + $box[$i] + $rndkey[$i])%256;
		$tmp = $box[$i];
		$box[$i] = $box[$j];
		$box[$j] = $tmp;
	}
	for($a=$j=$i=0;$i<$string_length;$i++) {
		$a = ($a+1)%256;
		$j = ($j+$box[$a])%256;
		$tmp = $box[$a];
		$box[$a] = $box[$j];
		$box[$j] = $tmp;
		$result .= chr(ord($string[$i])^($box[($box[$a]+$box[$j])%256]));
	}
	if($operation=='D') {
		if(substr($result,0,8) == substr(md5(substr($result,8).$key),0,8)) {
			return substr($result,8);
		} else {
			return '';
		}
	} else {
		return str_replace('=','',base64_encode($result));
	}
}

function os_get_contents($url) {
	$ch = curl_init();
	$timeout = 5;
	curl_setopt ($ch, CURLOPT_URL, $url);
	curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt ($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
	$str = curl_exec($ch);
	curl_close($ch);
	return $str;
}

function os_log($file, $msg) {
	$file = DIR_FS_CATALOG.'cache/'.$file;
	$fp = @fopen($file,"a+");
	if(!$fp) return false;
	if(filesize($file) > 2097152) {
		$finfo = pathinfo($file);
		$ext = $finfo['extension'];
		$name = $finfo['basename'];
		$dir = $finfo['dirname'].'/';
		if($ext != '') {
			$name = str_ireplace('.'.$ext, '', $name);
			$name = $name.'-'.date('YmdHi');
			$new_file = $dir.$name.'.'.$ext;
		} else {
			$new_file = $dir.$name.'-'.$date('YmdHi');
		}
		fclose($fp);
		rename($file, $new_file);
		$fp = @fopen($file,"a+");
	}
	$str = "[".date("Y-m-d H:i:s",time())."] ".$msg;
	fwrite($fp, $str."\n");
	fclose($fp);
}
?>