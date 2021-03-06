package com.baidu.third;

public class IAppPayConfig {
	/**
	 * appid		-- 应用appid
	 * privateKey   -- 应用私钥
	 * publicKey	-- 平台公钥(platKey)
	 * notifyurl	-- 商户服务端接收支付结果通知的地址
	 * */
	

//	/**测试环境*/
//	public static final String appid = "3006213620";
//	public static final String privateKey = "MIICWwIBAAKBgQCvzhrNT1noVBQElJ7vpz2QrNJs/33Bisxmc+Kn4zz5Pnn8PUGbdYjNaR175amKIr9cr1NDWa476ILCLiHQCoolkHtCxByLRnnDsoAHQkREoC6ybvMOTtvCCq3PDC1Sg54/UXst9rYKRKPhHaXF50IZl0smFPYGil0LUpWWIN/s6QIDAQABAoGAK0WcvTox1AiV4MbAFBbjBXA0XxXH21KRmwodGNvKz0J8fgSJ2HzmjkC4PB28TD7fQS36XZJ+W0qQnMEEG5PsiofqC4V44yL37BR/uE5RGEcCebDdZfuAy1y7jwyC2vfl2GWJBGI6xzoFCxn2b6TG/Sk1dHHebG64U993o7GRDX0CQQDd5x9yU4aCLYP3n8QaE2cMgcsNVbL9fhSicLDJ1Q3jXUNrfEqfMcPwVJ7TtocbinE/d/VypHh2+nFcvMFGWoWXAkEAytGox0vrzjMoWMEtDKZjyc/m7nnXiM2433S26YWFmhlmPNiQKbaPSYeQYqK98X52ffeBlQWESBfsYHhGgwBxfwJATm2QrLyersXdfZinG7w90KrTFGx0ralxK5R+t2co2HIvEP9F9IwHP5r448UAbZh5vD/urU06Ensjs+42tWHrEwJAAuMrOPMev0fBKkpNvuFbiFL6YajAZcUv/ZZmyDpLZZ848mLO4pkjvxM7/ft9IsP/Idn1MU7rzC7zAYiU0GagNwJAaa1uGvWPOSZTFpt4Jsjn/hJ+fwwIi4xXp2n3LJBtl32BWFsfYBi4/e+GR94SgNoY1chODCiUW0O0az0FBNwfjQ==" ;
//	public static final String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDWuH1fRSnNm+QvDuDc+FjKAflnlXvvIB4YyvDBm7CWmGEtF4yTKwOL7JYovXpTjRzUUVEWTb9joMLD8hK4p3/kkvq42wcxq2bDMzYqnBJSr71v/Bbl6Ntty9atUimCdSEDBA7y1Y1G8Os41ZQ0ylzuKqKNAS/t45RpZwejax1m6wIDAQAB";
//	public static final String notifyurl = "http://192.168.0.140:8094/monizhuang/api?type=100";

	
//	/**线上环境*/
//	public static final String appid = "3006182570";
//	public static final String privateKey = "MIICXAIBAAKBgQCRjQWnwyFveNSqKB750a1BtqZjy6yJuMR3o8L+yRV2dUZo/6NKvI6KSDiduaG2KLDaboQD+roOeQHvk1APpYS6rEECzxUn8th3v1lEEIc4oEtmD4rcE6H773XY+7f5G92Z14m1L4wZgbjiZbFkXX+GKpp32k9xyo00RGYYz675UwIDAQABAoGABzIZnXmgJYqruN3S6jbbgewtvbFMDmL8E44dDcuTLBAQMxCwWfmIcn2vjtymigLo+2OLU0cJ+70vw1BdHOjp7t1nHwWr36pGhYqskZno/FZRCasYANPzlv4pqpw41FemzKvG//il8TQ9BZzeETYd+QKJXw3KlAujyA31ygPh1kECQQDbXMHN4nPJ5qOfELRxn+1qQ5C6MI5yGdYoUveFI4hnfpbrrNAYUS9/XN5oJgVbhyLQWjtjWb/z1cqbFrJLkxotAkEAqdxRsVMmt09kyEFFJzuAXMvI/0FXbp1oAoBeyRzt0nawi35qfcuEs3y9C8q7mHlnvKuRO2nbEOIaguwps9gRfwJAca3pNbQUQ16GDsnOaU/y7m3jU8oUF+dxx2XvpgybBN9igPIGyYNRNRTAoYKKpjsq/IS2YC9Dva5el+60KVwrZQJAR7vosCd6mT2pRTzzVG83dXkGaG4S238UNsI5xVs3QIOpaS+5D2kMoLRULg+vC+Rxn+cgzWOWBVpHcfUEY5QjRwJBANqsQuh9uaR3NtITHAuunRnZ4tVgfh99VWw0e5oC9YlAibWeXvu3iu03RqrNL5yOYIPkNNPTtOdVEEhc9qJzccM=" ;
//	public static final String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYGpprmfBUNfj2oI4THnPsNEpvQ2z/YnIrTBD8OWdtw6Lf6K5OYXcALedomwOqAi2nSPGygNPPYSzw4MuV6SZ8KGhvywybxMnVrBORa3yzVOcV1M5+TfkaZI6SX51euDzy/ZDUiWbwIKmxWlt4wcZtvgko/tVgUOXmWc27G+t7oQIDAQAB";
//	public static final String notifyurl = "http://www.cyjd1300.com:9020/pay/synch/netpay/iapppayNotify.do";

	/**线上环境*/
	public static final String appid = "3006336249";
	public static final String privateKey = "MIICWwIBAAKBgQCC/OfbaeLpihwT+L53xF86uzdEoaiBtwXGDTBNZcKzgHfzRlh0oq5TkPbHWhOIwH0CnXQ1SqCFPWhkf8GbGzC3lITinpTqHunNqfPdTBbEvGszyByMRVjLS1M3J7L+Q74R2MJhfkBnLm9A2BfegyNFF2Jg+ldklYBaTEgAlXWRLwIDAQABAoGAAsCPmpOZ+fA18NF8JwYQPiqq2Z1P7hHLZELoreeidKURYCItP54fbZARL1UcLfZlagWm6Gu9/a6xn9LzXW/v0RPQIHFQSMVE6Lfw4wlQRokQbi7Jd/j+G30EDv22oYpVwiqegrHgr7trpAhdVC1wEjJBj6Qe6QtIMrQ5AII/jlkCQQDc3WMayeHF/Re/3jIF219rYiCMkPBouKzlw08Y0yTP3TMun0GjI12+svzW9Vbww86wAU38MLcUh2Mv66/Cs/GdAkEAl9NTXzglnlRD+oSpkFpI/q7cuFtRCiismJYKTuXv9EmhbfZC68ZJ7YcyV4Q32/KWIkPnvAzOMD3w5cHfjoPKOwJADhOqKl3tnuHXrqnpxEfMGBsD+hGO0Q3cLzS4iNuEQB9YEMOjulMZuXrM9KJkbSn9tWgP4V/1e0B5yOcbp6wyXQJAUiVJ77QOTosjOLF8mRjwc7QiwjDOOSPmtqw/qY6pdNA94qQiczUXHMhQiZ+FJaEscB+zi0CWcu2goH90OMS/rwJAS3jOSJ9pXxxJKPfqo/UQThe99h7rt0TDQJcx07uhRuisE+OccSmmN10HAUKzdDk9o8mU9samDrqfBq6kKWchow==" ;
	public static final String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDiosDcpE9atRSd++BQo+nfbfI+/4izFgx8UsHbbDhtLOxDE03RimIxwDOvy+96tLdhVFvw6y88ql1jVrg8oZsAh34lcCEmAHst17e/5ydAkLUPYfNEoQ6DAAlqvXOZrtjm8d3uwbJq42x/2hT5W7dhjBg2E0z4J37Q5k3/o0i9hwIDAQAB";
	public static final String notifyurl = "http://www.cyjd1300.com:9020/pay/synch/netpay/iapppayNotify.do";

}
