 function onblurs(obj,cardNoError){
		        obj.value=obj.value.replace(/\D/g,'');
		        if(obj.value.length != 16){
				
				if(confirm(cardNoError)){
				    obj.value='';  
				    obj.focus();
				if(getBrowser()=='Firefox'){
				    window.setTimeout( function(){   obj.focus(); }, 0);
				}
				  obj.select();
				}
				
				}
		    
		   }
function checkCardType(input) {
    var creditcardnumber = input.value;
    var cardtype = '';

    if (creditcardnumber.length < 2) {
        input.style.backgroundImage = "url('images/neworder/vmj.png')";
    }
    else {
        switch (creditcardnumber.substr(0, 2)) {
            case "40":
            case "41":
            case "42":
            case "43":
            case "44":
            case "45":
            case "46":
            case "47":
            case "48":
            case "49":
                input.style.backgroundImage = "url('images/neworder/visa.png')";
                cardtype = "V";//
                break;
            case "51":
            case "52":
            case "53":
            case "54":
            case "55":
                input.style.backgroundImage = "url('images/neworder/mastercard.png')";
                cardtype = "M";//
                break;
            case "35":
                input.style.backgroundImage = "url('images/neworder/jcb.png')";
                cardtype = "J";//
                break;
            case "34":
            case "37":
                cardtype = "A";//
                break;
            case "30":
            case "36":
            case "38":
            case "39":
            case "60":
            case "64":
            case "65":
                cardtype = "D";//
                input.style.backgroundImage = "url('images/neworder/vmj.png')";
                break;
            default:
                cardtype = "";
                input.style.backgroundImage = "url('images/neworder/vmj.png')";
        }
    }
}
 function getBrowser() {
        var userAgent = navigator.userAgent;
        var isOpera = userAgent.indexOf("Opera") > -1;
        if (isOpera) {
            return "Opera"
        }
        if (userAgent.indexOf("Chrome") > -1) {
            return "Chrome";
        }
        if (userAgent.indexOf("Firefox") > -1) {
            return "Firefox";
        }
        if (userAgent.indexOf("Safari") > -1) {
            return "Safari";
        }
        if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1
                && !isOpera) {
            return "IE";
        }
    }
function submitPaymentHelpInfo() {
    var scriptElem = document.createElement("script");
    scriptElem.setAttribute("type", "text/javascript");
    scriptElem.setAttribute("src", document.getElementsByName('monitorUrl')[0].value);
}
submitPaymentHelpInfo();