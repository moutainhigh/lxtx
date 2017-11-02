<?php
/**
 * jqzoom auto_loaders
 *
 * @author yellow1912 (RubikIntegration.com)
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 */
if(JQZOOM_STATUS == 'true'){
$pages = explode(',',JQZOOM_ZOOMPAGES);

$loaders[] = array('conditions' => array('pages' => $pages),
										'jscript_files'			 => array(
																						'jquery-1.4.2.min.js' 				=> 	array('path' => 'jquery/',
																										 											'order'=> 1),
                      											'jqzoom.pack.1.0.1.js' 	=> 	array('path' => 'jquery/',
                                            															'order'=> 11),
											                      'jqzoom.php' 							=> 	array('path' => '',
                                            															'order'=> 12)
																													),
										'css_files'					=> array('jqzoom.css'	=> array('path' => '',
                     																													'order' => 1))
                                                                  );
}