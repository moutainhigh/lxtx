<?php
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_ADMIN_TITLE', 'MYORDERS  Payment' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CATALOG_TITLE', 'TuoFu Online Payment' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_DESCRIPTION', 'TuoFu Online Payment' );

define ( 'MODULE_PAYMENT_MYORDERS_MARK_BUTTON_IMG', './myorders/myorders.png' );
define ( 'MODULE_PAYMENT_MYORDERS_MARK_BUTTON_ALT', 'Credit Card Payment' );
define ( 'MODULE_PAYMENT_MYORDERS_ACCEPTANCE_MARK_TEXT', 'Neworder payment Online Payment' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CATALOG_LOGO', '<img src="' . MODULE_PAYMENT_MYORDERS_MARK_BUTTON_IMG . '" alt="' . MODULE_PAYMENT_MYORDERS_MARK_BUTTON_ALT . '" widtf="197px" height="38px" title="' . MODULE_PAYMENT_MYORDERS_MARK_BUTTON_ALT . '" />' );

//Setting info 后台配置
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_1_1', '是否开启托付支付' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_1_2', 'Do you want to accept Credit card payments?' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_2_1', 'Tuofu MerchantID' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_2_2', '交易号 [可在商户后台查询]' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_3_1', 'Tuofu Key' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_3_2', '密钥 [可在商户后台查询]' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_4_1', 'Payment Zone' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_4_2', '允许支付区域 [当选择为空,则表示可在任何区域都可进行支付]' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_5_1', 'Successfully set the order status' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_5_2', '订单成功状态 [即已下订单时状态,请自主选择]' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_6_1', 'Set Pending  Status' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_6_2', '订单初始状态 [即刚下订单时状态,请自主选择]' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_7_1', 'Sort order of display' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_7_2', '支付排序 [数字越小,排序越靠前,不可与其他支付重复]' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_8_1', 'Order id prefix/Site identifier' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_8_2', '订单号前缀 [可以为空,无需加横杆(-)],字符串不能超过10个字符。<br />' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_9_1', 'Payment Gateway' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CONFIG_9_2', '支付网关[建议填写,当正式支付网关地址无法连接支付时,将采用备份地址进行支付]' );

//card info 卡信息
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CREDIT_CARD_NUMBER', 'Kredietkaart Informatie:' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CREDIT_CARD_CVV', 'CVV2/CSC:' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CREDIT_CNAME', 'Signature du titulaire:');
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_MONTH', 'maand' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_YEAR', 'jaar' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_NAME', 'naam op kaart' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CREDIT_CARD_EXPIRES', 'Vervaldatum:');
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_CARD', 'Kaart Type' );

//js info 提示错误
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_NAME', '* Naam is verplicht !\n' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_NUMBER', '* Numéro de carte de crédit est incorrect !\n' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_NUMBER_01', '* Card No. Is Fout!\n' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_EXPIRES_MONTH', '* Creditcard vervalt Maand Is Fout!\n' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_EXPIRES_YEAR', '* Creditcard vervalt Jaar Is Fout!\n');
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_ERROR', 'Carte de crédit Erreur:' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_CVV', '* CVV2 / CSC Is Fout!\n' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_JS_MYORDERS_CVV_01', '* Creditcard verificatiecode fout!' );
//Gateway info 网关消息
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_PENDING', 'Uw betaling voor uw bestelling wordt verwerkt en wij zullen u later via de email informeren over het resultaat van deze verwerking. Wees geduldig, bedankt!' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_FAILURE', 'Sorry. Uw bank van uitgifte of uw credit card onderneming zegt \'@@@\'. Neem a.u.b contact op met uw bank van uitgifte of probeer het opnieuw met andere kaarten' );
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_ERRORNOTE', 'Sorry. Uw bank van uitgifte of uw credit card onderneming zegt \'niet accepteren\'. Neem a.u.b contact op met uw bank van uitgifte of probeer het opnieuw met andere kaarten');
define ( 'MODULE_PAYMENT_MYORDERS_TEXT_SUCCESS', 'Uw betaling werd met succes verwerkt!' );

?>
