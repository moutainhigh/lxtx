jQuery.fn.extend({  slideRightShow: function() {    return this.each(function() {        $(this).show('slide', {direction: 'right'}, 1000);    });  },  slideLeftHide: function() {    return this.each(function() {      $(this).hide('slide', {direction: 'left'}, 1000);    });  },  slideRightHide: function() {    return this.each(function() {      $(this).hide('slide', {direction: 'right'}, 1000);    });  },  slideLeftShow: function() {    return this.each(function() {      $(this).show('slide', {direction: 'left'}, 1000);    });  }});
$(function() {
    var t = $("#text_box");   
    $("#add").click(function(){        
        t.val(parseInt(t.val())+1)   
        if (parseInt(t.val())!=1){   
            $('#min').attr('disabled',false);   
        }     
    })     
    $("#min").click(function(){   
        t.val(parseInt(t.val())-1)   
        if (parseInt(t.val())==1){   
            $('#min').attr('disabled',true);   
        }   
        if(parseInt(t.val())==0){   
            alert("Quantity can not be less than 1 !");   
            t.val(parseInt(t.val())+1)   
        }   
           
    });
    $("#index-submit").click(function(){
    	if($(".index-share-input").val() == ''){
    		alert("Please fill in your email address to subscribe to our discount news .");
    		$(".index-share-input").focus();
    	}else{
    		if(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test($(".index-share-input").val()) == false){
    			alert("Sorry this is not a correct Email.");
    			$(".index-share-input").focus();
    		}else{
    			alert("SUCCESS!! Thanks you for Subscribe.");
    			location.reload();
    		}
    		
    	}
    	
    });
    $('.first_top').hover(function(){
    	$(this).find("ul").slideDown();
    },function(){
    	$(this).find("ul").slideUp();
    }
    );
    $(window).scroll(function(){
		var h = $(window).scrollTop();
		if (h>100){
			$("#scrollToTop").fadeIn();
		}
		else{
			$("#scrollToTop").fadeOut();
		}
	});	

	$('#tagD ul li').click(function(){
		$(this).addClass("secleted").siblings().removeClass("secleted");
	})
	$('#tagDES').click(function(){
		$('.tagdescription').hide();
		$('.tagshipping').hide();
		$('.tagonline').hide();
		$('.tagdescription').show();
	});
	$('#tagSHI').click(function(){
		$('.tagdescription').hide();
		$('.tagshipping').hide();
		$('.tagonline').hide();
		$('.tagshipping').show();
	});
	$('#tagONL').click(function(){
		$('.tagdescription').hide();
		$('.tagshipping').hide();
		$('.tagonline').hide();
		$('.tagonsize').show();
	});
	$('#tagF').click(function(){
		$('.tagdescription').hide();
		$('.tagshipping').hide();
		$('.tagonline').hide();
		$('.tagonfaq').show();
	});

	$("#scrollToTop").click(function(){
		$('body,html').animate({scrollTop:0},500);
		return false;
	});
	$('.color-choice').click(function(){
		var value = $(this).attr('alt');
		var url = 'index.php?main_page=advanced_search_result&search_in_description=0&keyword='+value;
		window.location.href = url;
	});
	$('.price-choice').click(function(){
		var min = $(this).attr("data-min");
		var max = $(this).attr("data-max");
		if(max){
			window.location.href = 'index.php?main_page=advanced_search_result&pfrom='+min+'&pto='+max;
		}else{
			window.location.href = 'index.php?main_page=advanced_search_result&pfrom='+min+'&pto=99999';
		}
	});
	$("#top-menu").toggle(function(){
		$("#bcover").show().animate({right:"0%"});
	},function(){
		$("#bcover").animate({right:"100%"}).fadeOut();
	});
	$("#top-shopping").click(function(){
		window.location.href = 'index.php?main_page=shopping_cart';
	});
	$('.index-search-btn').click(function(){
		var searchkey = $('.index-search-input').val();
		if(searchkey == ''){
			$('.index-search-input').focus();
			$('.index-search-input').css('border','1px solid #f00');
		}else{
			window.location.href = 'index.php?main_page=advanced_search_result&search_in_description=0&keyword='+ searchkey;
		}
	});
});	