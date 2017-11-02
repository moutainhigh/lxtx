$(document).ready(function(){
    $(".shoppingbag").hover(function(){
        $(".shopping-bag span").css("background-position","-48px -20px");
        $(".shoppingbag a").css("color","#f2f2f2");
    },function(){
        $(".shopping-bag span").css("background-position","0 -20px");
        $(".shoppingbag a").css("color","#ccc");
    });
    $(".topSearch").hover(function(){
        $(".topSearch").css("background-position","0 -18px");
    },function(){
        $(".topSearch").css("background-position","0 6px");
    });
    $(".topSearch").click(function(){
        if($(".bodysearch").is(":hidden")){
            $(".bodysearch").slideDown(500);
        }else{
            $(".bodysearch").slideUp(500);
        }
    });
    $(".searchclose").click(function(){
        $(".bodysearch").slideUp(500);
    });
    
    
                       
});

$(window).load(function() {
	// Slider
    $("#mainslider").flexslider({
		animation: "slide",
        slideshow: false, // ***
        useCSS: false,
		controlNav: true,
		animationLoop: true,
		smoothHeight: true
	});
    // Accordion settings
    $(function() {
        $('.accordion').on('show', function (e) {
            $(e.target).prev('.accordion-heading').find('i').removeClass('icon-plus');
            $(e.target).prev('.accordion-heading').find('i').addClass('icon-minus');
        });
        $('.accordion').on('hide', function (e) {
            $(e.target).prev('.accordion-heading').find('i').removeClass('icon-minus');
            $(e.target).prev('.accordion-heading').find('i').addClass('icon-plus');
        });
    });
});
