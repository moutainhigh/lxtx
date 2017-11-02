<?php
/**
 * jqzoom auto_loaders
 *
 * @author yellow1912 (RubikIntegration.com)
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 */
if(JQLIGHTBOX_STATUS == 'true'){
$pages = explode(',',JQLIGHTBOX_PAGES);

$loaders[] = array('conditions' => array('pages' => $pages),
										'jscript_files'			 => array(
																						'jquery-1.4.2.min.js' 				=> 	array('path' => 'jquery/',
																										 											'order'=> 1),
                      											'jquery.lightbox-0.5.min.js' 	=> 	array('path' => 'jquery/',
                                            															'order'=> 11),

											                      'jqlightbox.php' 							=> 	array('path' => '',
                                            															'order'=> 12)

                                                                  ),
                     'css_files'					=> array('jqlightbox.css'	=> array('path' => '',
                     																													'order' => 1))
                                                                  );
}