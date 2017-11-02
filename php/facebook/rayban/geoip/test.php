    <?php  
        //计时开始  
        function utime() {  
            $time = explode( " ", microtime() );  
            $usec = (double)$time[0];  
            $sec = (double)$time[1];  
            return $usec + $sec;  
        }  
        $startTimes = utime();  
       
        // include the php script  
        // wget -c http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz
		// wget -c http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz
        include("geoip.inc");  
        //include("geoipcity.inc");  
       
        // open the geoip database  
         $gi = geoip_open("./GeoIP.dat",GEOIP_STANDARD);  
        // $gi = geoip_open("./GeoLiteCity.dat",GEOIP_STANDARD);  
       
        // 获取国家代码  
        $country_code = geoip_country_code_by_addr($gi, @$_GET["ip"]);  
       // echo "Your country code is: <strong>$country_code</strong> <br />";  
        print_r($country_code);
       
        // 获取国家名称  
        //$country_name = geoip_country_name_by_addr($gi, '173.252.252.190');  
        //echo "Your country name is: <strong>$country_name</strong> <br />";  
       
        // close the database  
        geoip_close($gi);  
       
        //运行结束时间  
        $endTimes = utime();  
        $runTimes = sprintf( '%0.4f', ( $endTimes - $startTimes ) );  
        echo "<p>Processed in " . $runTimes . "second.</p>";  
    ?>  