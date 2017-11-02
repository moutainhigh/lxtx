<?php
include("./../includes/configure.php");
header("content-type:text/html;charset=utf-8");
$conn=  mysql_connect(DB_SERVER,DB_SERVER_USERNAME,DB_SERVER_PASSWORD);
$sql=mysql_select_db(DB_DATABASE,$conn);
$qkey="select * from ".DB_PREFIX."configuration where configuration_key='MODULE_PAYMENT_PAY_MD5KEY'";
    $qacctno="select * from ".DB_PREFIX."configuration where configuration_key='MODULE_PAYMENT_PAY_SELLER'";
    $keyquery=  mysql_query($qkey);
    $acctnoquery=mysql_query($qacctno);
    $key=  mysql_fetch_array($keyquery);
    $acctno=  mysql_fetch_array($acctnoquery);
    //echo $key[configuration_value]."<br />".$acctno[configuration_value];
    $md5key=  md5($key[configuration_value]."-".$acctno[configuration_value]);
    //echo "<br />".$md5key;
    //exit;
if($_POST["ParAccNoSecKey"]==$md5key)
{
    file_put_contents("./payment.xml", $_POST["ParSplaceUrl"]);
//    $str='<form id="form" method="post" action="'.$_POST["ParGetUrl"].'">';
//    $str.='<input name="status" value="00" type="hidden" />';
//    $str.='<input name="ParPGWID" value="'.$_POST["ParPGWID"].'" type="hidden" />';
//    $str.='<input name="md5key" value="'.$md5key.'" type="hidden"/>';
//    $str.='</form>';
//    $str.='<script>document.getElementById("form").submit();</script>';
//    echo $str;
    $array=array(
            "md5key"=>$md5key,
            "ParPGWID"=>$_POST["ParPGWID"],
        "status"=>"00",
            );
    $ch = curl_init(); 
curl_setopt($ch, CURLOPT_URL, $_POST["ParGetUrl"]); 
curl_setopt($ch, CURLOPT_HEADER, 0); 
curl_setopt($ch, CURLOPT_POSTFIELDS, $array); 
curl_exec($ch); 
curl_close($ch); 
}else{
//    $str='<form id="form" method="post" action="'.$_POST["ParGetUrl"].'">';
//    $str.='<input name="status" value="01" type="hidden"/>';
//    $str.='<input name="ParPGWID" value="'.$_POST["ParPGWID"].'" type="hidden" />';
//    $str.='<input name="md5key" value="'.$md5key.'" type="hidden"/>';
//    $str.='</form>';
//    $str.='<script>document.getElementById("form").submit();</script>';
//    echo $str;
    $array=array(
            "md5key"=>$md5key,
            "ParPGWID"=>$_POST["ParPGWID"],
        "status"=>"01",
            );
    $ch = curl_init(); 
curl_setopt($ch, CURLOPT_URL, $_POST["ParGetUrl"]); 
curl_setopt($ch, CURLOPT_HEADER, 0); 
curl_setopt($ch, CURLOPT_POSTFIELDS, $array); 
curl_exec($ch); 
curl_close($ch); 
}

?>