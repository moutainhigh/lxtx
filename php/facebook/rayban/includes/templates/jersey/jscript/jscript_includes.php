<?php
/**
 * jscript auto_loaders
 *
 * @author yellow1912 (RubikIntegration.com)
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 */

if(isset($loaders) && count($loaders) > 0){
	$_jscripts = $_css_files = array();
	foreach($loaders as $j){
		if(in_array('*', $j['conditions']['pages']) || in_array($current_page_base, $j['conditions']['pages'])){
			$_jscript_files[] = $j['jscript_files'];
			$_css_files[] = $j['css_files'];
		}
		else{
			$load = false;	
			if(isset($j['conditions']['call_backs']))
			foreach($j['conditions']['call_backs'] as $function){
				$f = explode(',',$function);
				if(count($f) == 2){
					$load = call_user_func(array($f[0], $f[1]));
				}
				else $load = $function();
				
				if($load){
					$_jscript_files[] = $j['jscript_files'];
					$_css_files[] = $j['css_files'];
					break;
				}
			}
		}
	}
	
	if(count($_css_files) > 0)
		loadFiles($_css_files, 'css');	

	if(count($_jscript_files) > 0)
		loadFiles($_jscript_files);
}