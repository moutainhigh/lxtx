<?php
/**
 * jscript_addr_pulldowns
 *
 * handles pulldown menu dependencies for state/country selection
 *
 * @package page
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: J_Schilz for Integrated COWOA - 14 April 2007
 */
?>
<script language="javascript" type="text/javascript">
  function update_zone(theForm) {

    // if there is no zone_id field to update, or if it is hidden from display, then exit performing no updates
    if (!theForm || !theForm.elements["zone_id"]) return;
    if (theForm.zone_id.type == "hidden") return;

	  
	
    // set initial values
    var SelectedCountry = theForm.zone_country_id.options[theForm.zone_country_id.selectedIndex].value;
    var SelectedZone = theForm.elements["zone_id"].value;

    //fire an ajax request to save the selected country in session
    $.get("index.php?main_page=quick_aj", { action:"CustShowPrice", selected_country_id: SelectedCountry },
    function(data){
      $("#CustShowPrice").html(data);
      $("#confirmOrderBtn").removeAttr("disabled");
      $("#confirmOrderBtn").removeClass("btnDisable");
     });        

    // reset the array of pulldown options so it can be repopulated
    var NumState = theForm.zone_id.options.length;
    while(NumState > 0) {
      NumState = NumState - 1;
      theForm.zone_id.options[NumState] = null;
    }
    // build dynamic list of countries/zones for pulldown
  <?php echo zen_js_zone_list('SelectedCountry', 'theForm', 'zone_id'); ?>

    // if we had a value before reset, set it again
    if (SelectedZone != "") theForm.elements["zone_id"].value = SelectedZone;
  }

  function update_zone_shipping(theForm) {
    // if there is no zone_id field to update, or if it is hidden from display, then exit performing no updates
    if (!theForm || !theForm.elements["zone_id_shipping"]) return;
    if (theForm.zone_id_shipping.type == "hidden") return;

    // set initial values
    var SelectedCountry = theForm.zone_country_id_shipping.options[theForm.zone_country_id_shipping.selectedIndex].value;
    var SelectedZone = theForm.elements["zone_id_shipping"].value;

    // reset the array of pulldown options so it can be repopulated
    var NumState = theForm.zone_id_shipping.options.length;
    while(NumState > 0) {
      NumState = NumState - 1;
      theForm.zone_id_shipping.options[NumState] = null;
    }
    // build dynamic list of countries/zones for pulldown
  <?php echo zen_fec_js_zone_list_shipping('SelectedCountry', 'theForm', 'zone_id_shipping'); ?>

    // if we had a value before reset, set it again
    if (SelectedZone != "") theForm.elements["zone_id_shipping"].value = SelectedZone;
  }

  function hideStateField(theForm) {
    theForm.state.disabled = true;
    theForm.state.className = 'hiddenField';
    theForm.state.setAttribute('className', 'hiddenField');
		document.getElementById("zoneDiv").className = 'clearBoth visibleDiv';
		document.getElementById("stDiv").className = 'clearBoth hiddenDiv';
	/*document.getElementById("zoneLabel").className = 'inputLabel visibleField';
	document.getElementById("stateZone").className = 'visibleField';
	document.getElementById("zoneText").className = 'alert visibleField';
    document.getElementById("stateLabel").className = 'hiddenField';
    document.getElementById("stateLabel").setAttribute('className', 'hiddenField');
    document.getElementById("stText").className = 'hiddenField';
    document.getElementById("stText").setAttribute('className', 'hiddenField');*/
  }
  
  function hideStateFieldShipping(theForm) {
    theForm.state_shipping.disabled = true;
    theForm.state_shipping.className = 'hiddenField';
    theForm.state_shipping.setAttribute('className', 'hiddenField');
		document.getElementById("zoneSDiv").className = 'clearBoth visibleDiv';
		document.getElementById("stSDiv").className = 'clearBoth hiddenDiv';
    /*document.getElementById("stateLabelShipping").className = 'hiddenField';
    document.getElementById("stateLabelShipping").setAttribute('className', 'hiddenField');
    document.getElementById("stTextShipping").className = 'hiddenField';
    document.getElementById("stTextShipping").setAttribute('className', 'hiddenField');
    document.getElementById("stBreakShipping").className = 'hiddenField';
    document.getElementById("stBreakShipping").setAttribute('className', 'hiddenField');*/
  }

  function showStateField(theForm) {
    theForm.state.disabled = false;
    theForm.state.className = 'inputLabel visibleField';
    theForm.state.setAttribute('className', 'visibleField');
		document.getElementById("zoneDiv").className = 'clearBoth hiddenDiv';
		document.getElementById("stDiv").className = 'clearBoth visibleDiv';
	/*document.getElementById("zoneLabel").className = 'hiddenField';
	document.getElementById("stateZone").className = 'hiddenField';
	document.getElementById("zoneText").className = 'hiddenField';
    document.getElementById("stateLabel").className = 'inputLabel visibleField';
    document.getElementById("stateLabel").setAttribute('className', 'inputLabel visibleField');
    document.getElementById("stText").className = 'alert visibleField';
    document.getElementById("stText").setAttribute('className', 'alert visibleField');*/
  }
  
  function showStateFieldShipping(theForm) {
    theForm.state_shipping.disabled = false;
    theForm.state_shipping.className = 'inputLabel visibleField';
    theForm.state_shipping.setAttribute('className', 'visibleField');
		document.getElementById("zoneSDiv").className = 'clearBoth hiddenDiv';
		document.getElementById("stSDiv").className = 'clearBoth visibleDiv';
    /*document.getElementById("stateLabelShipping").className = 'inputLabel visibleField';
    document.getElementById("stateLabelShipping").setAttribute('className', 'inputLabel visibleField');
    document.getElementById("stTextShipping").className = 'alert visibleField';
    document.getElementById("stTextShipping").setAttribute('className', 'alert visibleField');
    document.getElementById("stBreakShipping").className = 'clearBoth visibleField';
    document.getElementById("stBreakShipping").setAttribute('className', 'clearBoth visibleField');*/
  }
//--></script>