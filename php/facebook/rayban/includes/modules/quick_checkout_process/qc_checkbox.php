<?php
  // BEGIN OPTIONAL CHECKBOX
  if (FEC_CHECKBOX == 'true') {
    if (zen_not_null($_POST['fec_checkbox'])) {
      $_SESSION['fec_checkbox'] = $_POST['fec_checkbox'];
    } else {
      unset($_SESSION['fec_checkbox']);
    }
  }
  // END OPTIONAL CHECKBOX
?>