<?php
/**
* @package Pages
* @copyright Copyright 2008-2009 RubikIntegration.com
* @copyright Copyright 2003-2006 Zen Cart Development Team
* @copyright Portions Copyright 2003 osCommerce
* @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
* @version $Id: link.php 149 2009-03-04 05:23:35Z yellow1912 $
*/                                             
                                                            
$loaders[] = array('conditions' => array('pages' => array('create_account', 'login', 'no_account')),
										'jscript_files' => array(
										  'jquery/jquery-1.6.1.min.js' => 1,
                      'jquery/jquery_addr_pulldowns.php' => 2,
                      'jquery/jquery_form_check.php' => 3,
                      'jquery/jquery_create_account.php' => 4										
                    )
								);  