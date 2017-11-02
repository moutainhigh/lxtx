<?php
/**
 * Page Template
 *
 * come from tpl_modules_no_account.php
 * Loaded automatically by index.php?main_page=create_account.<br />
 * Displays Create Account form.
 *
 * @package templateSystem - FEC
 * @copyright Copyright 2007-2008 Numinix Technology http://www.numinix.com
 * @copyright Copyright 2003-2007 Zen Cart Development Team
 * @copyright Portions Copyright 2007 Joseph Schilz
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_modules_no_account.php 80 2009-07-30 03:52:55Z numinix $
 */
?>

<?php //if ($messageStack->size('no_account') > 0) echo $messageStack->output('no_account'); ?>

<div class="clearBoth"></div>

<?php
  if (DISPLAY_PRIVACY_CONDITIONS == 'true') {
?>
<div class="clearBoth">
<div class="information"><?php echo TEXT_PRIVACY_CONDITIONS_DESCRIPTION;?></div>
<?php echo zen_draw_checkbox_field('privacy_conditions', '1', false, 'id="privacy"');?>
<label class="checkboxLabel" for="privacy"><?php echo TEXT_PRIVACY_CONDITIONS_CONFIRM;?></label>
</div>
<?php
  }
?>


<div id="billingField">
<div class="clearBoth AddTitle">
<h3><?php echo TABLE_HEADING_BILLING_ADDRESS; ?></h3>
<div class="alert forward"><?php echo FORM_REQUIRED_INFORMATION; ?></div>
</div>
<input type="hidden" name="email_format" value="HTML" checked="checked" id="email-format-text" />
<input type="hidden" name="newsletter" value="1" checked="checked" id="newsletter-checkbox" />
<?php /*<input type="hidden" name="shippingAddress" value="1" checked="checked" id="shippingAddress-checkbox" />*/?>

  <?php
    if (ACCOUNT_GENDER == 'true') {
  ?>
<div class="clearBoth">
  <?php echo zen_draw_radio_field('gender', 'm', '', 'id="gender-male"') . '<label class="radioButtonLabel" for="gender-male">' . MALE . '</label>' . zen_draw_radio_field('gender', 'f', '', 'id="gender-female"') . '<label class="radioButtonLabel" for="gender-female">' . FEMALE . '</label>' . (zen_not_null(ENTRY_GENDER_TEXT) ? '<span class="alert">' . ENTRY_GENDER_TEXT . '</span>': ''); ?>
</div>
  <?php
    }
  ?>
  
<div class="clearBoth">
  <label class="inputLabel" for="firstname"><?php echo ENTRY_FIRST_NAME; ?></label>
  <?php echo zen_draw_input_field('firstname', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_firstname', '40') . ' id="firstname"') . (zen_not_null(ENTRY_FIRST_NAME_TEXT) ? '<span class="alert">' . ENTRY_FIRST_NAME_TEXT . '</span>': ''); ?>
</div>
  
<div class="clearBoth">
  <label class="inputLabel" for="lastname"><?php echo ENTRY_LAST_NAME; ?></label>
  <?php echo zen_draw_input_field('lastname', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_lastname', '40') . ' id="lastname"') . (zen_not_null(ENTRY_LAST_NAME_TEXT) ? '<span class="alert">' . ENTRY_LAST_NAME_TEXT . '</span>': ''); ?>
</div>
  
  <?php
    if (ACCOUNT_COMPANY == 'true') {
  ?>
<div class="clearBoth">
  <label class="inputLabel" for="company"><?php echo ENTRY_COMPANY; ?></label>
  <?php echo zen_draw_input_field('company', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_company', '40') . ' id="company"') . (zen_not_null(ENTRY_COMPANY_TEXT) ? '<span class="alert">' . ENTRY_COMPANY_TEXT . '</span>': ''); ?>
</div>
  <?php
    }
  ?>

<?php
  if (ACCOUNT_DOB == 'true') {
?>
<div id="noAccountDOB" class="clearBoth">
<label class="inputLabel" for="dob"><?php echo ENTRY_DATE_OF_BIRTH; ?></label>
<?php echo zen_draw_input_field('dob','', 'id="dob"') . (zen_not_null(ENTRY_DATE_OF_BIRTH_TEXT) ? '<span class="alert">' . ENTRY_DATE_OF_BIRTH_TEXT . '</span>': ''); ?>
</div>
<?php
  }
?>
  
<div class="clearBoth">
  <label class="inputLabel" for="street-address"><?php echo ENTRY_STREET_ADDRESS; ?></label>
    <?php echo zen_draw_input_field('street_address', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_street_address', '40') . ' id="street-address"') . (zen_not_null(ENTRY_STREET_ADDRESS_TEXT) ? '<span class="alert">' . ENTRY_STREET_ADDRESS_TEXT . '</span>': ''); ?>
</div>
  
  <?php
    if (ACCOUNT_SUBURB == 'true') {
  ?>
<div class="clearBoth">
  <label class="inputLabel" for="suburb"><?php echo ENTRY_SUBURB; ?></label>
  <?php echo zen_draw_input_field('suburb', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_suburb', '40') . ' id="suburb"') . (zen_not_null(ENTRY_SUBURB_TEXT) ? '<span class="alert">' . ENTRY_SUBURB_TEXT . '</span>': ''); ?>
</div>
  <?php
    }
  ?>
  
<div class="clearBoth">
  <label class="inputLabel" for="city"><?php echo ENTRY_CITY; ?></label>
  <?php echo zen_draw_input_field('city', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_city', '40') . ' id="city"') . (zen_not_null(ENTRY_CITY_TEXT) ? '<span class="alert">' . ENTRY_CITY_TEXT . '</span>': ''); ?>
</div>

  <?php
    if ($disable_country == true) {
      $addclass = " hiddenDiv";
    }
  ?>
<div class="clearBoth<?php echo $addclass; ?>">
    <label class="inputLabel" for="country"><?php echo ENTRY_COUNTRY; ?></label>
    <?php echo zen_get_country_list('zone_country_id', $selected_country, 'id="country" ' . ($flag_show_pulldown_states == true ? 'onchange="update_zone(this.form);"' : '')) . (zen_not_null(ENTRY_COUNTRY_TEXT) ? '<span class="alert">' . ENTRY_COUNTRY_TEXT . '</span>': ''); ?>
</div>

  <?php
    if (ACCOUNT_STATE == 'true') {
      if ($flag_show_pulldown_states == true) {
  ?>
  <div class="clearBoth" id="zoneDiv">
  <label class="inputLabel" for="stateZone" id="zoneLabel"><?php echo ENTRY_STATE; ?></label>
  <?php
        echo zen_draw_pull_down_menu('zone_id', zen_prepare_country_zones_pull_down($selected_country), $zone_id, 'id="stateZone"');
        if (zen_not_null(ENTRY_STATE_TEXT)) echo '<span class="alert" id="zoneText">' . ENTRY_STATE_TEXT . '</span>';
	?>
  </div>
  <?php
      }
  ?>

  <div class="clearBoth" id="stDiv">
  <label class="inputLabel" for="state" id="stateLabel"><?php echo ENTRY_STATE; ?></label>
	<?php
      echo zen_draw_input_field('state', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_state', '40') . ' id="state"');
      if (zen_not_null(ENTRY_STATE_TEXT)) echo '<span class="alert" id="stText">' . ENTRY_STATE_TEXT . '</span>';
      if ($flag_show_pulldown_states == false) {
        echo zen_draw_hidden_field('zone_id', $zone_name, ' ');
      }
  ?>
  </div>
  <?php
    }
  ?>
  
<div class="clearBoth">
  <label class="inputLabel" for="postcode"><?php echo ENTRY_POST_CODE; ?></label>
  <?php echo zen_draw_input_field('postcode', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_postcode', '40') . ' id="postcode"') . (zen_not_null(ENTRY_POST_CODE_TEXT) ? '<span class="alert">' . ENTRY_POST_CODE_TEXT . '</span>': ''); ?>
</div>
  
  <?php
  if (ACCOUNT_TELEPHONE == 'true') {
?>
<div class="clearBoth">
<label class="inputLabel" for="telephone"><?php echo ENTRY_TELEPHONE_NUMBER; ?></label>
<?php echo zen_draw_input_field('telephone', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_telephone', '40') . ' id="telephone"') . (zen_not_null(ENTRY_TELEPHONE_NUMBER_TEXT) ? '<span class="alert">' . ENTRY_TELEPHONE_NUMBER_TEXT . '</span>': ''); ?>
</div>
<?php } ?>
<?php
  if (ACCOUNT_FAX_NUMBER == 'true') {
?>
<div class="clearBoth">
<label class="inputLabel" for="fax"><?php echo ENTRY_FAX_NUMBER; ?></label>
<?php echo zen_draw_input_field('fax', '', 'id="fax"') . (zen_not_null(ENTRY_FAX_NUMBER_TEXT) ? '<span class="alert">' . ENTRY_FAX_NUMBER_TEXT . '</span>': ''); ?>
</div>
<?php
  }
?>

<?php if(enable_shippingAddressCheckbox()){ ?>
<div class="clearBoth">
<?php echo zen_draw_checkbox_field('shippingAddress', '1', $shippingAddress, 'id="shippingAddress-checkbox" checked="checked"') . '<label style="" class="checkboxLabel" for="shippingAddress-checkbox">' . ENTRY_COPYBILLING . '</label>' . (zen_not_null(ENTRY_COPYBILLING_TEXT) ? '<span class="alert">' . ENTRY_COPYBILLING_TEXT . '</span>': ''); ?>
</div>
<?php } ?>
  
</div><!-- eof billing box -->
  
  <?php if(enable_shippingAddressCheckbox()){ ?>
  <!-- begin shipping box -->
  <div id="shippingField" class="hiddenDiv">
  <div class="clearBoth AddTitle">
  <h3><?php echo TABLE_HEADING_SHIPPING_ADDRESS; ?></h3>
  <div class="alert forward"><?php echo FORM_REQUIRED_INFORMATION; ?></div>
  </div>
  <?php
    if (ACCOUNT_GENDER == 'true') {
  ?>
<div class="clearBoth">
  <?php echo zen_draw_radio_field('gender_shipping', 'm', '', 'id="gender-male_shipping"') . '<label class="radioButtonLabel" for="gender-male">' . MALE . '</label>' . zen_draw_radio_field('gender_shipping', 'f', '', 'id="gender-female_shipping"') . '<label class="radioButtonLabel" for="gender-female">' . FEMALE . '</label>' . (zen_not_null(ENTRY_GENDER_TEXT) ? '<span class="alert">' . ENTRY_GENDER_TEXT . '</span>': ''); ?>
</div>
  <?php
    }
  ?>
  
<div class="clearBoth">
  <label class="inputLabel" for="firstname_shipping"><?php echo ENTRY_FIRST_NAME; ?></label>
  <?php echo zen_draw_input_field('firstname_shipping', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_firstname', '40') . ' id="firstname_shipping"') . (zen_not_null(ENTRY_FIRST_NAME_TEXT) ? '<span class="alert">' . ENTRY_FIRST_NAME_TEXT . '</span>': ''); ?>
</div>
  
<div class="clearBoth">
  <label class="inputLabel" for="lastname_shipping"><?php echo ENTRY_LAST_NAME; ?></label>
  <?php echo zen_draw_input_field('lastname_shipping', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_lastname', '40') . ' id="lastname_shipping"') . (zen_not_null(ENTRY_LAST_NAME_TEXT) ? '<span class="alert">' . ENTRY_LAST_NAME_TEXT . '</span>': ''); ?>
</div>
  
  <?php
    if (ACCOUNT_COMPANY == 'true') {
  ?>
<div class="clearBoth">
  <label class="inputLabel" for="company_shipping"><?php echo ENTRY_COMPANY; ?></label>
  <?php echo zen_draw_input_field('company_shipping', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_company', '40') . ' id="company_shipping"') . (zen_not_null(ENTRY_COMPANY_TEXT) ? '<span class="alert">' . ENTRY_COMPANY_TEXT . '</span>': ''); ?>
</div>
  <?php
    }
  ?>
  
<div class="clearBoth">
  <label class="inputLabel" for="street-address_shipping"><?php echo ENTRY_STREET_ADDRESS; ?></label>
    <?php echo zen_draw_input_field('street_address_shipping', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_street_address', '40') . ' id="street-address_shipping"') . (zen_not_null(ENTRY_STREET_ADDRESS_TEXT) ? '<span class="alert">' . ENTRY_STREET_ADDRESS_TEXT . '</span>': ''); ?>
</div>
  
  <?php
    if (ACCOUNT_SUBURB == 'true') {
  ?>
<div class="clearBoth">
  <label class="inputLabel" for="suburb_shipping"><?php echo ENTRY_SUBURB; ?></label>
  <?php echo zen_draw_input_field('suburb_shipping', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_suburb', '40') . ' id="suburb_shipping"') . (zen_not_null(ENTRY_SUBURB_TEXT) ? '<span class="alert">' . ENTRY_SUBURB_TEXT . '</span>': ''); ?>
</div>
  <?php
    }
  ?>
  
<div class="clearBoth">
  <label class="inputLabel" for="city_shipping"><?php echo ENTRY_CITY; ?></label>
  <?php echo zen_draw_input_field('city_shipping', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_city', '40') . ' id="city_shipping"') . (zen_not_null(ENTRY_CITY_TEXT) ? '<span class="alert">' . ENTRY_CITY_TEXT . '</span>': ''); ?>
</div>
   
  <div class="clearBoth<?php echo $addclass; ?>">
    <label class="inputLabel" for="country_shipping"><?php echo ENTRY_COUNTRY; ?></label>
    <?php echo zen_get_country_list('zone_country_id_shipping', $selected_country, 'id="country_shipping" ' . ($flag_show_pulldown_states_shipping == true ? 'onchange="update_zone_shipping(this.form);"' : '')) . (zen_not_null(ENTRY_COUNTRY_TEXT) ? '<span class="alert">' . ENTRY_COUNTRY_TEXT . '</span>': ''); ?>
  </div>
  
  <?php
    if (ACCOUNT_STATE == 'true') {
      if ($flag_show_pulldown_states_shipping == true) {
  ?>
<div class="clearBoth" id="zoneSDiv">
  <label class="inputLabel" for="stateZone_shipping" id="zoneLabel"><?php echo ENTRY_STATE; ?></label>
  <?php
        echo zen_draw_pull_down_menu('zone_id_shipping', zen_prepare_country_zones_pull_down($selected_country), $zone_id_shipping, 'id="stateZone_shipping"');
        if (zen_not_null(ENTRY_STATE_TEXT)) echo '&nbsp;<span class="alert">' . ENTRY_STATE_TEXT . '</span>';
?>
</div>
  <?php
      }
  ?>
  
<div class="clearBoth hiddenDiv" id="stSDiv">
  <label class="inputLabel" for="state_shipping" id="stateLabelShipping"><?php echo ENTRY_STATE; ?></label>
  <?php
      echo zen_draw_input_field('state_shipping', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_state', '40') . ' id="state_shipping"');
      if (zen_not_null(ENTRY_STATE_TEXT)) echo '&nbsp;<span class="alert" id="stTextShipping">' . ENTRY_STATE_TEXT . '</span>';
      if ($flag_show_pulldown_states_shipping == false) {
        echo zen_draw_hidden_field('zone_id_shipping', $zone_name_shipping, ' ');
      }
  ?>
</div>
  <?php
    }
  ?>
  
<div class="clearBoth">
  <label class="inputLabel" for="postcode_shipping"><?php echo ENTRY_POST_CODE; ?></label>
  <?php echo zen_draw_input_field('postcode_shipping', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_postcode', '40') . ' id="postcode_shipping"') . (zen_not_null(ENTRY_POST_CODE_TEXT) ? '<span class="alert">' . ENTRY_POST_CODE_TEXT . '</span>': ''); ?>
</div>
  <input type="hidden" name="quick_checkout" value="TEXT" checked="checked" id="quick-checkout-text" />
  </div><!-- eof shipping box -->
  <?php } ?>

<?php // login detail ************************* ?>
<div class="clearBoth">  
  <label class="inputLabel" for="email-address"><?php echo ENTRY_EMAIL_ADDRESS; ?></label>
<?php echo zen_draw_input_field('email_address', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_email_address', '40') . ' id="email-address"') . (zen_not_null(ENTRY_EMAIL_ADDRESS_TEXT) ? '<span class="alert">' . ENTRY_EMAIL_ADDRESS_TEXT . '</span>': ''); ?>
<span id="email_check_result"></span>
</div>
<?php if (FEC_CONFIRM_EMAIL == 'true') { ?>
<div class="clearBoth">
  <label class="inputLabel" for="email-address-confirm"><?php echo ENTRY_EMAIL_ADDRESS_CONFIRM; ?></label>
  <?php echo zen_draw_input_field('email_address_confirm', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_email_address', '40') . ' id="email-address-confirm"') . (zen_not_null(ENTRY_EMAIL_ADDRESS_TEXT) ? '<span class="alert">' . ENTRY_EMAIL_ADDRESS_TEXT . '</span>': ''); ?>
  </div>
<?php } ?>

<div class="clearBoth">
<label class="inputLabel" for="password-new"><?php echo ENTRY_PASSWORD; ?></label>
<?php echo zen_draw_password_field('password', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_password', '20') . ' id="password-new"') . (zen_not_null(ENTRY_PASSWORD_TEXT) ? '<span class="alert">' . ENTRY_PASSWORD_TEXT . '</span>': ''); ?>
</div>

<div class="clearBoth">
<label class="inputLabel" for="password-confirm"><?php echo ENTRY_PASSWORD_CONFIRMATION; ?></label>
<?php echo zen_draw_password_field('confirmation', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_password', '20') . ' id="password-confirm"') . (zen_not_null(ENTRY_PASSWORD_CONFIRMATION_TEXT) ? '<span class="alert">' . ENTRY_PASSWORD_CONFIRMATION_TEXT . '</span>': ''); ?>
</div>

<?php /*if (FEC_NOACCOUNT_HIDEEMAIL == 'false') { ?>
<!-- BEGIN CHECKOUT WITHOUT ACCOUNT -->
<div class="clearBoth">
<?php echo zen_draw_radio_field('email_format', 'HTML', ($email_format == 'HTML' ? true : false),'id="email-format-html"') . '<label class="radioButtonLabel" for="email-format-html">' . ENTRY_EMAIL_HTML_DISPLAY . '</label>' .  zen_draw_radio_field('email_format', 'TEXT', ($email_format == 'TEXT' ? true : false), 'id="email-format-text"') . '<label class="radioButtonLabel" for="email-format-text">' . ENTRY_EMAIL_TEXT_DISPLAY . '</label>'; ?>
</div>
<?php } else { ?>
<input type="hidden" name="email_format" value="HTML" checked="checked" id="email-format-text" />
<!-- END CHECKOUT WITHOUT ACCOUNT -->
<?php } ?>
<?php
  if (ACCOUNT_NEWSLETTER_STATUS != 0) {
?>
<div class="clearBoth">
<?php echo zen_draw_checkbox_field('newsletter', '1', $newsletter, 'id="newsletter-checkbox" checked="checked"') . '<label class="checkboxLabel" for="newsletter-checkbox">' . ENTRY_NEWSLETTER . '</label>' . (zen_not_null(ENTRY_NEWSLETTER_TEXT) ? '<span class="alert">' . ENTRY_NEWSLETTER_TEXT . '</span>': ''); ?>
</div>
<?php }*/ ?>

<?php
  if (CUSTOMERS_REFERRAL_STATUS == 2) {
?>
<div id="noAccountReferral" class="clearBoth">
<label class="inputLabel" for="customers_referral"><?php echo ENTRY_CUSTOMERS_REFERRAL; ?></label>
<?php echo zen_draw_input_field('customers_referral', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_referral', '15') . ' id="customers_referral"'); ?>
</div>
<?php } ?>