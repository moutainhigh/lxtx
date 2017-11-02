$(function(){
	$('.pagesx1 ul li a').click(function(){
		var url=$(this).attr('href');
		$.post(url+'/',{"times":Math.random()},function(sdata){
			if(sdata){$("#recordPanel").html(sdata);}
		});
		
		return false;
		
	})
	
})