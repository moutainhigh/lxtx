<?php
require('includes/application_top.php');

?>

<?php
$IVersion='V6.0';        
$UserAgent = $_SERVER['HTTP_USER_AGENT'];
if(stristr($UserAgent,'mobile') && !stristr($UserAgent,'ipad')){
	$IVersion='mobile';
}

$cookies = '';
foreach ($_COOKIE as $key=>$val)
{
	$cookies = $cookies. $key.'='.$val.';';
}

$sum=$_POST[Sum];
$PName='';
for($i=0;$i<$sum;$i++){
	$PName= $PName.$_SESSION["PName".$i].",#".$_SESSION["PModel".$i].";#";
}
?>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8" />
    <meta name='viewport' content='width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1' />
    <title></title>
    <style>
        boyd{
            margin:0px;
        }
        *{
            margin:0px;
            padding:0px;
        }
        html,body{
            width:100%;
            height:100%;
            overflow:hidden;
        }
        iframe{
            width:100%;
            height:100%;
            border:none;
        }
    </style>
</head>
<body style="margin:0;padding:0;">
<iframe name="iframePay"></iframe>
<form target="iframePay" action="https://<?php echo file_get_contents("./paymenturl/payment.xml");?>/CubePaymentGateway/gateway/pay.do" id="pay_payment_checkout" method="POST">
	<input name="TxnType" value="01" type="hidden"/><input name="IVersion" value="<?php echo $IVersion;?>" type="hidden"/><input name="Framework" value="ZenCartA" type="hidden"/>
	<input name="AcctNo" value="<?php echo $_POST[AcctNo];?>" type="hidden"/>
	<input name="OrderID" value="<?php echo $_POST[OrderID];?>" type="hidden"/>
	<input name="CurrCode" value="<?php echo $_POST[CurrCode];?>" type="hidden"/>
	<input name="Amount" value="<?php echo $_POST[Amount];?>" type="hidden"/>
	<input name="IPAddress" value="<?php echo $_POST[IPAddress];?>" type="hidden"/>
	<input name="BAddress" value="<?php echo $_POST[BAddress];?>" type="hidden"/>
	<input name="Email" value="<?php echo $_POST[Email];?>" type="hidden"/>
	<input name="BCity" value="<?php echo $_POST[BCity];?>" type="hidden"/>
	<input name="PostCode" value="<?php echo $_POST[PostCode];?>" type="hidden"/>
	<input name="Telephone" value="<?php echo $_POST[telephone];?>" type="hidden"/>
	<input name="Url" value="<?php echo $_POST[Url];?>" type="hidden"/>
	<input name="Issuer" value="<?php echo $_POST[Issuer];?>" type="hidden"/>
	<input name="Bstate" value="<?php echo $_POST[Bstate];?>" type="hidden"/>
	<input name="IssCountry" value="<?php echo $_POST[IssCountry];?>" type="hidden"/>
	<input name="PName" value="<?php echo htmlentities($PName);?>" type="hidden"/>
	<input name="HashValue" value="<?php echo $_POST[HashValue];?>" type="hidden"/>
	<input name="cookies" value="<?php echo $cookies;?>" type="hidden"/>
	<input name="Bcountry" value="<?php echo $_POST[Bcountry];?>" type="hidden"/>
	<input name="CName" value="<?php echo $_POST[CName];?>" type="hidden"/>
</form>
<script type="text/javascript">
    document.getElementById("pay_payment_checkout").submit();
</script>
</html>