        function mainmenu(){
            $(" .dynamic_menu div ").hover(function(){
            $(this).find('div:first:hidden').show();
            },function(){
                $(this).find('div:first').css({display: "none"});
            });
        }
        function hidePopups(actpopup, keep_active){
            $('.popup').each(function() { 
                if ($(this).attr('id') != actpopup){
                    $(this).hide(); 
                }
            });
			if (!keep_active) {
            	$('.tm-catalog').removeClass('tab-active');
	            $('.tm-cart').removeClass('tab-active');
			}
        }        


        function mainmenu(){
            $(" .dynamic_menu div ").hover(function(){
            $(this).find('div:first:hidden').show();
            },function(){
                $(this).find('div:first').css({display: "none"});
            });
        }
        function hidePopups(actpopup, keep_active){
            $('.popup').each(function() { 
                if ($(this).attr('id') != actpopup){
                    $(this).hide(); 
                }
            });
			if (!keep_active) {
            	$('.tm-catalog').removeClass('tab-active');
	            $('.tm-cart').removeClass('tab-active');
			}
        }        
$(document).ready(function() {
//Currency switcher
		$("#currency_switcher").click(function(){
            hidePopups("currPopup");
            $("#currPopup").toggle();
            return false;
        });
//Language switcher        
        $("#language_switcher").click(function(){
            hidePopups("langPopup");
            $("#langPopup").toggle();
            return false;
        });
//more
        $("#menu_more").click(function(){
            hidePopups("menu_more_popup");
            $("#menu_more_popup").toggle();
            return false;
        });
//catalog                
        $("#catalog").click(function(){
            $('.tm-cart').removeClass('tab-active');
            hidePopups("categoriesPopup", true);
            $(".tm-catalog").toggleClass("tab-active");
            $("#categoriesPopup").toggle();
            return false;
        });


		$("#cart_icon").click(function(){
            $('.tm-catalog').removeClass('tab-active');
            hidePopups("cart_popup", true);
            $(".tm-cart").toggleClass("tab-active");
            $("#cart_popup").toggle();
            return false;
        });
		
		
			$("#search_icon").click(function(){
            $('.tm-catalog').removeClass('tab-active');
            hidePopups("search_popup", true);
            $(".tm-search").toggleClass("tab-active");
            $("#search_popup").toggle();
            return false;
        });


        mainmenu();

        $('.popup').click(function(e) {
            e.stopPropagation();
        });
        $(document).click(function() {
            $('.popup').hide();
            $('.tm-catalog').removeClass('tab-active');
            $('.tm-cart').removeClass('tab-active');
        }); 
        $(".close-pic").click(function(){
            $('.popup').hide();
        }); 
});
$(window).load(function() {
	$('.flexslider').flexslider({
  	  animation: "slide", 
	  animationSpeed: 300,
	  slideshowSpeed: 3000,
	  start: function(){ $(".flex-control-nav ").show(); }
  });
});

$(window).resize(function() {
	$('.flexslider').flexslider({
  	  animation: "slide", 
	});

});
