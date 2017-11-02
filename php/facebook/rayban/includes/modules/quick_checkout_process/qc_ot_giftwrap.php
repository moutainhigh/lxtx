<?php    
  // BOF GIFT WRAPPING
  if (FEC_GIFT_WRAPPING_SWITCH == 'true') {
    $wrapsettings = array(); 
     $prod_count = 1; 
    for ($i=0, $n=sizeof($order->products); $i<$n; $i++) {
       $prid = $order->products[$i]['id'];
       $wrapsettings[$prid] = array(); 
       for ($q = 1; $q <= $order->products[$i]['qty']; $q++) {
        $name = "wrap_prod_" . $prod_count; 
        if (isset($_POST[$name]) && ($_POST[$name] == 'on')) {
          $_SESSION[$name] = $_POST[$name];
          $wrapsettings[$prid][$q] = 1;  
        } else {
          $wrapsettings[$prid][$q] = 0; 
          $_SESSION[$name] = ''; 
        }
        $prod_count++;  
       }
    }
    // tsg_logger($wrapsettings);
    $_SESSION['wrapsettings'] = $wrapsettings;
  }
  // EOF GIFT WRAPPING
?>