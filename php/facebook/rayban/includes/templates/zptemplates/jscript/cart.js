$(function(){   
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
            alert("Quantité ne peut pas être inférieur à 1 !");   
            t.val(parseInt(t.val())+1)   
        }   
           
    })   
  
})    
