﻿var banklist=new Array(
	['工商银行','中国工商银行'],
	['农业银行','中国农业银行'],
	['招商银行','招商银行']
);

var banklistall=new Array(
	['工商银行','中国工商银行'],
	['农业银行','中国农业银行'],
	['中国银行','中国银行'],
	['建设银行','中国建设银行'],
	['交通银行','交通银行'],
	['中国邮政','中国邮政储蓄银行'],
	['中信银行','中信银行'],
	['光大银行','中国光大银行'],
	['华夏银行','华夏银行'],
	['民生银行','民生银行'],
	['广发银行','广发银行'],
	['招商银行','招商银行'],
	['兴业银行','兴业银行'],
	['浦发银行','浦发银行'],
	['平安银行','平安银行'],
	['恒丰银行','恒丰银行'],
	['','渤海银行'],
	['浙商银行','浙商银行']
	,['北京银行','北京银行'],
	['','上海银行'],
	['','安顺市商业银行'],
	['','安徽省农村信用社'],
	['','安阳银行'],
	['','安徽当涂新华村镇银行'],
	['','安徽和县新华村镇银行'],
	['','安徽郎溪新华村镇银行'],
	['','安徽望江新华村镇银行'],
	['','保定银行'],
	['','北京农商银行'],
	['','北京大兴九银村镇银行'],
	['','包商银行'],
	['','成都农村商业银行'],
	['','成都银行'],
	['','重庆农村商业银行'],
	['','重庆北碚稠州村镇银行'],
	['','重庆三峡银行'],
	['','重庆银行'],
	['','重庆南川石银村镇银行'],
	['','重庆忠县稠州村镇银行'],
	['','常熟农村商业银行'],
	['','长安银行'],
	['','长沙银行'],
	['','长治市商业银行'],
	['','长葛轩辕村镇银行'],
	['','承德银行'],
	['','沧州银行'],
	['','大连银行'],
	['','德州银行'],
	['','德州市商业银行'],
	['','德阳银行'],
	['','达州商行'],
	['','大连保税区珠江村镇银行'],
	['','大武口石银村镇银行'],
	['','东莞银行'],
	['','东莞农村商业银行'],
	['','东营莱商村镇银行'],
	['','大同银行'],
	['','丹东银行'],
	['','东营银行'],
	['','鄂尔多斯银行'],
	['','福建省农村信用社'],
	['','福建海峡银行'],
	['','抚顺银行'],
	['','富滇银行'],
	['','佛山高明顺银村镇银行'],
	['','广西北部湾银行'],
	['','广西农村信用社'],
	['','广州银行'],
	['','广东农村信用社'],
	['','广州农村商业银行'],
	['','广州花都稠州村镇银行'],
	['','广东华兴银行'],
	['','广东南粤银行'],
	['','广元市贵商村镇银行'],
	['','广州番禺新华村镇银行'],
	['','赣州银行'],
	['','贵州银行'],
	['','贵州农村信用社'],
	['','桂林银行'],
	['','贵阳银行'],
	['','甘肃省农村信用社联合社'],
	['','杭州银行'],
	['','哈密市商业银行'],
	['','河北银行'],
	['','河北省农村信用社'],
	['','河南农村信用社'],
	['','徽商银行'],
	['','鹤壁银行'],
	['','鹤山珠江村镇银行'],
	['','衡水市商业银行'],
	['','哈尔滨银行'],
	['','海南省农村信用社联合社'],
	['','汉口银行'],
	['','湖南省农村信用社联合社'],
	['','湖北省农村信用社联合社'],
	['','湖北银行'],
	['','葫芦岛银行'],
	['','邯郸银行'],
	['','华融湘江银行'],
	['','黄河农村商业银行'],
	['','嘉兴银行'],
	['','嘉善联合村镇银行'],
	['','晋城银行'],
	['','江西农村信用联合社'],
	['','江苏省农村信用社联合社'],
	['','江苏锡州农村商业银行'],
	['','江苏长江商业银行'],
	['','江苏银行'],
	['','江阴农村商业银行'],
	['','江南农村商业银行'],
	['','江南银行'],
	['','江苏江阴农村商业银行'],
	['','吉林省农村信用社'],
	['','吉林银行'],
	['','晋中银行'],
	['','晋商银行'],
	['','锦州银行'],
	['','金华银行'],
	['','济宁银行'],
	['','九江银行'],
	['','昆山农村商业银行'],
	['','库尔勒市商业银行'],
	['','昆仑银行'],
	['','龙江银行'],
	['','凉山州商业银行'],
	['','乐山市商业银行'],
	['','乐清联合村镇银行'],
	['','临商银行'],
	['','柳州银行'],
	['','漯河银行'],
	['','莱商银行股份有限公司'],
	['','六盘水市商业银行'],
	['','兰州银行'],
	['','泸州市商业银行'],
	['','泸州商业银行'],
	['','洛阳银行'],
	['','梅县客家村镇银行'],
	['','绵阳市商业银行'],
	['','内蒙古银行'],
	['','内蒙古农村信用社'],
	['','宁波银行'],
	['','宁波东海银行'],
	['','宁夏银行'],
	['','宁夏黄河农商银行'],
	['','南昌银行'],
	['','南京银行'],
	['','南充市商业银行'],
	['','南阳村镇银行'],
	['','平顶山银行'],
	['','盘锦市商业银行'],
	['','攀枝花市商业银行'],
	['','濮阳银行'],
	['','青海银行'],
	['','齐鲁银行'],
	['','秦皇岛银行'],
	['','泉州银行'],
	['','曲靖市商业银行'],
	['','齐商银行'],
	['','青海省农村信用社'],
	['','青岛银行'],
	['','日照银行'],
	['','上海农商银行'],
	['','盛京银行'],
	['','三门峡银行'],
	['','上饶银行'],
	['','深圳福田银座村镇银行'],
	['','商丘银行'],
	['','深圳农商银行'],
	['','苏州银行'],
	['','遂宁市商业银行'],
	['','石嘴山银行'],
	['','顺德农村商业银行'],
	['','四川省农村信用合作社'],
	['','山东省农村信用社'],
	['','山西尧都农村商业银行'],
	['','山西省农村信用社联合社'],
	['','陕西省农村信用社'],
	['','绍兴银行'],
	['','深圳龙岗鼎业村镇银行'],
	['','泰安市商业银行'],
	['','太仓农村商业银行'],
	['','唐山市商业银行'],
	['','天津滨海农村商业银行'],
	['','天津农商银行'],
	['','天津银行'],
	['','天津武清村镇银行'],
	['','天津华明村镇银行'],
	['','台州银行'],
	['','天津静海新华村镇银行'],
	['','潍坊银行'],
	['','威海市商业银行'],
	['','乌海银行'],
	['','吴江农商银行'],
	['','无锡农村商业银行'],
	['','温州银行'],
	['','温岭联合村镇银行'],
	['','乌鲁木齐市商业银行'],
	['','厦门银行'],
	['','许昌银行'],
	['','新疆汇和银行'],
	['','邢台银行'],
	['','西安银行'],
	['','信阳银行'],
	['','新疆农信'],
	['','宜昌市商业银行'],
	['','宜宾市商业银行'],
	['','雅安市商业银行'],
	['','阳泉市商业银行'],
	['','玉溪市商业银行'],
	['','银川市商业银行'],
	['','云南省农村信用社'],
	['','烟台银行'],
	['','郾城包商银行'],
	['','鄞州银行'],
	['','营口银行'],
	['','浙江省农村信用社'],
	['','浙江稠州商业银行'],
	['','浙江民泰商业银行'],
	['','浙江泰隆商业银行'],
	['','朝阳银行'],
	['','珠海华润银行'],
	['','郑州银行'],
	['','张家港农村商业银行'],
	['','张家口市商业银行'],
	['','自贡市商业银行'],
	['','周口银行'],
	['','遵义银行'],
	['','浙江长兴联合村镇银行'],
	['','遵义市商业银行'],
	['','枣庄银行'],
    ['','驻马店银行']
);