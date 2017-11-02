<?php
/**
 * Page Template
 *
 * Loaded automatically by index.php?main_page=create_account.<br />
 * Displays Create Account form.
 *
 * @package templateSystem
 * @copyright Copyright 2007 Numinix Technology http://www.numinix.com
 * @copyright Copyright 2003-2006 Zen Cart Development Team
 * @copyright Portions Copyright 2003 osCommerce
 * @license http://www.zen-cart.com/license/2_0.txt GNU Public License V2.0
 * @version $Id: tpl_modules_create_account.php 82 2009-08-05 03:33:26Z numinix $
 */
?>

<?php if ($messageStack->size('create_account') > 0) echo $messageStack->output('create_account'); ?>
<div class="alert forward"><?php echo FORM_REQUIRED_INFORMATION; ?></div>
<br class="clearBoth" />
<?php
  if (DISPLAY_PRIVACY_CONDITIONS == 'true') { //display privacy conditions
?>
<div class="clearBoth">
<h3><?php echo TABLE_HEADING_PRIVACY_CONDITIONS; ?></h3>
<div class="information"><?php echo TEXT_PRIVACY_CONDITIONS_DESCRIPTION;?></div>
<?php echo zen_draw_checkbox_field('privacy_conditions', '1', false, 'id="privacy"');?>
<label class="checkboxLabel" for="privacy"><?php echo TEXT_PRIVACY_CONDITIONS_CONFIRM;?></label>
</div>
<?php
  }
?>
<?php
  if (ACCOUNT_DOB == 'true') { //display customer birthday
?>
<div class="clearBoth">
<h3><?php echo TABLE_HEADING_DATE_OF_BIRTH; ?></h3>
<label class="inputLabel" for="dob"><?php echo ENTRY_DATE_OF_BIRTH; ?></label>
<?php echo zen_draw_input_field('dob','', 'id="dob"') . (zen_not_null(ENTRY_DATE_OF_BIRTH_TEXT) ? '<span class="alert">' . ENTRY_DATE_OF_BIRTH_TEXT . '</span>': ''); ?>
</div>
<?php
  }
?>
<div class="billing_address clearBoth">
<h3><?php echo TABLE_HEADING_BILLING_ADDRESS; ?></h3>
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
  if (ACCOUNT_COMPANY == 'true') { //display customer company
?>
<div class="clearBoth">
<label class="inputLabel" for="company"><?php echo ENTRY_COMPANY; ?></label>
<?php echo zen_draw_input_field('company', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_company', '40') . ' id="company"') . (zen_not_null(ENTRY_COMPANY_TEXT) ? '<span class="alert">' . ENTRY_COMPANY_TEXT . '</span>': ''); ?>
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
      if (zen_not_null(ENTRY_STATE_TEXT)) echo '<span class="alert">' . ENTRY_STATE_TEXT . '</span>'; 
?>
  </div>
<?php } ?>
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
<?php if (enable_shippingAddressCheckbox()) { ?>
<div class="clearBoth">
<?php echo zen_draw_checkbox_field('shippingAddress', '1', $shippingAddress, 'id="shippingAddress-checkbox" checked="checked"') . '<label style="" class="checkboxLabel" for="shippingAddress-checkbox">' . ENTRY_COPYBILLING . '</label>' . (zen_not_null(ENTRY_COPYBILLING_TEXT) ? '<span class="alert">' . ENTRY_COPYBILLING_TEXT . '</span>': ''); ?>
</div>
<?php } ?>
</div>

<?php if(enable_shippingAddressCheckbox()) { ?>
<div id="shippingField" class="sidebysidefields clearBoth">
<h3><?php echo TABLE_HEADING_SHIPPING_ADDRESS; ?></h3>
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
<?php
  if ($disable_country == true) {
    $addclass = " hiddenDiv";
  }
?>
<div class="clearBoth<?php echo $addclass; ?>">
<label class="inputLabel" for="country_shipping"><?php echo ENTRY_COUNTRY; ?></label>
<?php echo zen_get_country_list('zone_country_id_shipping', $selected_country_shipping, 'id="country_shipping" ' . ($flag_show_pulldown_states_shipping == true ? 'onchange="update_zone_shipping(this.form);"' : '')) . (zen_not_null(ENTRY_COUNTRY_TEXT) ? '<span class="alert">' . ENTRY_COUNTRY_TEXT . '</span>': ''); ?>
</div>
<?php
  if (ACCOUNT_STATE == 'true') {
    if ($flag_show_pulldown_states_shipping == true) {
?>
<div class="clearBoth" id="zoneSDiv">
<label class="inputLabel" for="stateZone_shipping" id="zoneLabel"><?php echo ENTRY_STATE; ?></label>
<?php
      echo zen_draw_pull_down_menu('zone_id_shipping', zen_prepare_country_zones_pull_down($selected_country_shipping), $zone_id_shipping, 'id="stateZone_shipping"');
      if (zen_not_null(ENTRY_STATE_TEXT)) echo '<span class="alert">' . ENTRY_STATE_TEXT . '</span>';
?>
</div>
<?php
    }
?>
<div class="clearBoth hiddenDiv" id="stSDiv">
<label class="inputLabel" for="state_shipping" id="stateLabelShipping"><?php echo ENTRY_STATE; ?></label>
<?php
    echo zen_draw_input_field('state_shipping', '', zen_set_field_length(TABLE_ADDRESS_BOOK, 'entry_state', '40') . ' id="state_shipping"');
    if (zen_not_null(ENTRY_STATE_TEXT)) echo '<span class="alert" id="stTextShipping">' . ENTRY_STATE_TEXT . '</span>';
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
</div>
<?php } ?>

<?php if (ACCOUNT_TELEPHONE == 'true' || ACCOUNT_FAX_NUMBER == 'true') { ?>
<div class="clearBoth">
<h3><?php echo TABLE_HEADING_PHONE_FAX_DETAILS; ?></h3>
<?php if (ACCOUNT_TELEPHONE == 'true') { ?>
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
</div>
<?php } ?>
<div class="clearBoth">
<h3><?php echo TABLE_HEADING_LOGIN_DETAILS; ?></h3>
<div class="clearBoth">
<label class="inputLabel" for="email-address"><?php echo ENTRY_EMAIL_ADDRESS; ?></label>
<?php echo zen_draw_input_field('email_address', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_email_address', '40') . ' id="email-address"') . (zen_not_null(ENTRY_EMAIL_ADDRESS_TEXT) ? '<span class="alert">' . ENTRY_EMAIL_ADDRESS_TEXT . '</span>': ''); ?>
</div>
<?php if (FEC_CONFIRM_EMAIL == 'true') { ?>
<div class="clearBoth">
  <label class="inputLabel" for="email-address-confirm"><?php echo ENTRY_EMAIL_ADDRESS_CONFIRM; ?></label>
  <?php echo zen_draw_input_field('email_address_confirm', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_email_address', '40') . ' id="email-address-confirm"') . (zen_not_null(ENTRY_EMAIL_ADDRESS_TEXT) ? '<span class="alert">' . ENTRY_EMAIL_ADDRESS_TEXT . '</span>': ''); ?>
</div>  
<?php } ?>
<?php
  if ($phpBB->phpBB['installed'] == true) {
?>
<div class="clearBoth">
<label class="inputLabel" for="nickname"><?php echo ENTRY_NICK; ?></label>
<?php echo zen_draw_input_field('nick','','id="nickname"') . (zen_not_null(ENTRY_NICK_TEXT) ? '<span class="alert">' . ENTRY_NICK_TEXT . '</span>': ''); ?>
</div>
<?php
  }
?>
<div class="clearBoth">
<label class="inputLabel" for="password-new"><?php echo ENTRY_PASSWORD; ?></label>
<?php echo zen_draw_password_field('password', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_password', '20') . ' id="password-new"') . (zen_not_null(ENTRY_PASSWORD_TEXT) ? '<span class="alert">' . ENTRY_PASSWORD_TEXT . '</span>': ''); ?>
</div>
<div class="clearBoth">
<label class="inputLabel" for="password-confirm"><?php echo ENTRY_PASSWORD_CONFIRMATION; ?></label>
<?php echo zen_draw_password_field('confirmation', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_password', '20') . ' id="password-confirm"') . (zen_not_null(ENTRY_PASSWORD_CONFIRMATION_TEXT) ? '<span class="alert">' . ENTRY_PASSWORD_CONFIRMATION_TEXT . '</span>': ''); ?>
</div>
</div>
<div class="clearBoth">
<h3><?php echo ENTRY_EMAIL_PREFERENCE; ?></h3>
<?php
  if (ACCOUNT_NEWSLETTER_STATUS != 0) {
?>
<div class="clearBoth">
<?php echo zen_draw_checkbox_field('newsletter', '1', $newsletter, 'id="newsletter-checkbox"') . '<label class="checkboxLabel" for="newsletter-checkbox">' . ENTRY_NEWSLETTER . '</label>' . (zen_not_null(ENTRY_NEWSLETTER_TEXT) ? '<span class="alert">' . ENTRY_NEWSLETTER_TEXT . '</span>': ''); ?>
</div>
<?php } ?>
<div class="clearBoth">
<?php echo zen_draw_radio_field('email_format', 'HTML', ($email_format == 'HTML' ? true : false),'id="email-format-html"') . '<label class="radioButtonLabel" for="email-format-html">' . ENTRY_EMAIL_HTML_DISPLAY . '</label>' .  zen_draw_radio_field('email_format', 'TEXT', ($email_format == 'TEXT' ? true : false), 'id="email-format-text"') . '<label class="radioButtonLabel" for="email-format-text">' . ENTRY_EMAIL_TEXT_DISPLAY . '</label>'; ?>
</div>
</div>
<?php
  if (CUSTOMERS_REFERRAL_STATUS == 2) {
?>
<div class="clearBoth">
<h3><?php echo TABLE_HEADING_REFERRAL_DETAILS; ?></h3>
<div class="clearBoth">
<label class="inputLabel" for="customers_referral"><?php echo ENTRY_CUSTOMERS_REFERRAL; ?></label>
<?php echo zen_draw_input_field('customers_referral', '', zen_set_field_length(TABLE_CUSTOMERS, 'customers_referral', '15') . ' id="customers_referral"'); ?>
</div>
</div>
<?php } ?>
<?php
// BOF Captcha
if(is_object($captcha)) {
?>
<fieldset>
<legend><?php echo TITLE_CAPTCHA; ?></legend>  
<?php echo $captcha->img(); ?>
<?php echo $captcha->redraw_button(BUTTON_IMAGE_CAPTCHA_REDRAW, BUTTON_IMAGE_CAPTCHA_REDRAW_ALT); ?>
<br class="clearBoth" />
<label for="captcha"><?php echo TITLE_CAPTCHA; ?></label>
<?php echo $captcha->input_field('captcha', 'id="captcha"') . '&nbsp;<span class="alert">' . TEXT_CAPTCHA . '</span>'; ?>
<br class="clearBoth" />
</fieldset>
<?php
}
// BOF Captcha
?>