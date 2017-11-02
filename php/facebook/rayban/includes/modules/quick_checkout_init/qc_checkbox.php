<?php
  // BEGIN OPTIONAL CHECKBOX 
  if (FEC_CHECKBOX == 'true') {
    if (isset($_POST['fec_checkbox'])) {
      $_SESSION['fec_checkbox'] = $_POST['fec_checkbox'];
    }
  }
  // END OPTIONAL CHECKBOX
?>