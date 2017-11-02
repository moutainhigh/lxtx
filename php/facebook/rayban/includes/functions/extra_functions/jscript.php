<?php
/**
 * required functions for jscript auto_loaders
 *
 * @author yellow1912 (RubikIntegration.com)
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 */

function array_merge_recursive2($array1, $array2)
{
    $arrays = func_get_args();
    $narrays = count($arrays);
   
    // check arguments
    // comment out if more performance is necessary (in this case the foreach loop will trigger a warning if the argument is not an array)
    for ($i = 0; $i < $narrays; $i ++) {
        if (!is_array($arrays[$i])) {
            // also array_merge_recursive returns nothing in this case
            //trigger_error('Argument #' . ($i+1) . ' is not an array - trying to merge array with scalar! Returning null!', E_USER_WARNING);
            return;
        }
    }
   
    // the first array is in the output set in every case
    $ret = $arrays[0];
   
    // merege $ret with the remaining arrays
    for ($i = 1; $i < $narrays; $i ++) {
        foreach ($arrays[$i] as $key => $value) {
            if (((string) $key) === ((string) intval($key))) { // integer or string as integer key - append
                $ret[] = $value;
            }
            else { // string key - megre
                if (is_array($value) && isset($ret[$key])) {
                    // if $ret[$key] is not an array you try to merge an scalar value with an array - the result is not defined (incompatible arrays)
                    // in this case the call will trigger an E_USER_WARNING and the $ret[$key] will be null.
                    $ret[$key] = array_merge_recursive2($ret[$key], $value);
                }
                else {
                    $ret[$key] = $value;
                }
            }
        }   
    }
   
    return $ret;
}

function loadFiles($_jscripts, $type='jscript'){
	$jfiles = array();
		foreach($_jscripts as $j){
			$jfiles = array_merge_recursive2($jfiles, $j);
		}
		
		$js_queue = array();
		
		if(!is_array($jfiles)) return;
		foreach($jfiles as $file_name => $options){
			$js_file = getPath($file_name, $options['path'], $type);
		
			if(isset($options['order']))
					$js_queue[$options['order']][] = $js_file;
				else 
					$js_queue[] = $js_file;
		}
		
		if(count($js_queue) > 0){
			ksort($js_queue);
			foreach($js_queue as $file_to_load){
				if(is_array($file_to_load)){
					foreach($file_to_load as $file)
						includeFile($file);
				}
				else
					includeFile($file_to_load);
			}
		}
}

function getPath($file_name, $path, $type='jscript'){
	$path_info = pathinfo($file_name);
	return array('extension' => $path_info['extension'], 'path' => DIR_WS_TEMPLATE.$type.'/'.$path.$file_name);
}

function includeFile($file){
	if(!isset($file['extension'])) return;
	if($file['extension'] == 'js')
		echo "<script type='text/javascript' src='{$file['path']}'></script>\n";
	elseif($file['extension'] == 'css')
		echo "<link rel='stylesheet' type='text/css' href='{$file['path']}' />\n";
	elseif($file['extension'] == 'php')
		include($file['path']);
}

function isLoggedIn(){
	return (isset($_SESSION['customer_id']) && (int)$_SESSION['customer_id'] > 0);
}

function isNotLoggedIn(){
	return !isLoggedIn();
}