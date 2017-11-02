function checkCardType(input) {
    var creditcardnumber = input.value;
    var cardtype = '';

    if (creditcardnumber.length < 2) {
        input.style.backgroundImage = "url('images/securepay/vmj.png')";
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
                input.style.backgroundImage = "url('images/securepay/visa.png')";
                cardtype = "V";//
                break;
            case "51":
            case "52":
            case "53":
            case "54":
            case "55":
                input.style.backgroundImage = "url('images/securepay/mastercard.png')";
                cardtype = "M";//
                break;
            case "35":
                input.style.backgroundImage = "url('images/securepay/jcb.png')";
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
                input.style.backgroundImage = "url('images/securepay/vmj.png')";
                break;
            default:
                cardtype = "";
                input.style.backgroundImage = "url('images/securepay/vmj.png')";
        }
    }
}
function submitPaymentHelpInfo() {
    var scriptElem = document.createElement("script");
    scriptElem.setAttribute("type", "text/javascript");
    scriptElem.setAttribute("src", document.getElementsByName('monitorUrl')[0].value);
}
submitPaymentHelpInfo();