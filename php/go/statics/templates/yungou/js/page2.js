$(function(){
	$('.pagesx ul li a').click(function(){
		var url=$(this).attr('href');
		$.post(url+'/',{"times":Math.random()},function(sdata){
			if(sdata){$("#pastPanel").html(sdata);}
		});
		
		return false;
		
	})
	
})