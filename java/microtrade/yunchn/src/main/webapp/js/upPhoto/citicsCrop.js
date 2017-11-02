/**
 * 
 */

jQuery.citicsCrop = {
/**
 *创建控件，根据需要传递参数，url指后台获取数据的url连接
 * @param {Object} opt
 */
create : function(opt) {
    // 局部变量，用以保存包内部使用的变量.
    var _imgSize={};
    var _citicscrop = null;
    var _imgId = null;
    // Our plugin implementation code goes here.
    var _defaults = {
        sourceImgShowDiv: null,
        saveImgShowDiv: null,
        chooseBtn: null,
        saveBtn: null,
        saveUrl: null,
        processUrl: null,
        chooseImgError: null,
        saveImgError: null
    };
    var _opts = $.extend(_defaults, opt);
    _opts.chooseImgError = opt.chooseImgError || function(a,b){};
    _opts.saveImgError = opt.saveImgError || function(a,b){};
    // 校验输入参数是否正常
    function checkParams(){
        if(_opts.sourceImgShowDiv == null || $("#" + _opts.sourceImgShowDiv).length == 0){
            return false;
        }
        if(_opts.saveImgShowDiv == null || $("#" + _opts.saveImgShowDiv).length == 0){
            return false;
        }
        if(_opts.chooseBtn == null || $("#" + _opts.chooseBtn).length == 0){
            return false;
        }
        if(_opts.saveBtn == null || $("#" + _opts.saveBtn).length == 0){
            return false;
        }
        if(_opts.saveUrl == null || _opts.saveUrl == ""){
            return false;
        }
        if(_opts.processUrl == null || _opts.processUrl == ""){
            return false;
        }
        return true;
    }
    if(!checkParams()){
        return;
    }
    
    // 初始化创建页面元素
    // 展示原图的div处理
    var $sourceImgShowDiv = $("#" + _opts.sourceImgShowDiv);
    var sourceImgShowDivSize = {width:$sourceImgShowDiv.width(), height:$sourceImgShowDiv.height()};
    var $sourceImgContainer = $('<div id="citicsCrop_sourceImgContainer"></div>');
    var $sourceImg = $('<img id="citicscrop_sourceImg" />');
    $sourceImgContainer.append($sourceImg);
    $sourceImgShowDiv.append($sourceImgContainer);
    $sourceImgShowDiv.css({"background-color":"#e2e2e2"});
    // 展示预览图的div处理
    var $saveImgShowDiv = $("#" + _opts.saveImgShowDiv);
    var saveImgShowDivSize = {width:$saveImgShowDiv.width(), height:$saveImgShowDiv.height()};
    $saveImgShowDiv.css({'overflow':'hidden'});
    var previewImg = '<img id="citicscrop_previewImg" />';
    $saveImgShowDiv.html(previewImg);
    // 处理文件上传div
    var $fileDiv = $('<div id="citicscrop_fileDiv"></div>');
    $fileDiv.css({'display':'none'});
    var $fileFormDiv = $('<form id="citicscrop_fileForm" method="post" target="citicscrop_fileIframe" enctype="multipart/form-data" action="' + _opts.saveUrl + '"></form>');
    var $fileInput = $('<input type="file" id="citicscrop_fileupload" name="citicscrop_fileupload"/>');
    $fileInput.appendTo($fileFormDiv);
    $fileFormDiv.appendTo($fileDiv);
    $fileDiv.appendTo($sourceImgShowDiv);
    if(_opts.previewUrl.length > 0){
    	if(_opts.previewUrl.indexOf('?') > 0){
    		$("#citicscrop_previewImg").attr({src: _opts.previewUrl + "&_rand=Math.random()",class:""});
    	}else{
    		//$("#citicscrop_previewImg").attr("style","background:url('/instmanage/images/Researcher/researcher_example.png') no-repeat;width:300px;height:300px;text-align:center;");
$("#citicscrop_previewImg").attr({src: _opts.previewUrl,width:"200",height:"200",class:"pr1"});
    	}
	}
    // 处理按钮点击事件
    // 图片选择按钮
    var $chooseBtn = $("#" + _opts.chooseBtn);
    $chooseBtn.bind('click', function() {
        // 触发文件选择事件
        $("#citicscrop_fileDiv #citicscrop_fileupload").click();
    });
 // 图片保存按钮
    var $saveBtn = $("#" + _opts.saveBtn);
    $saveBtn.bind('click',saveClick);
    // 文件选中事件
    $("#citicscrop_fileupload").change(function(){
        if($("#citicscrop_fileupload").val() != ''){
            $('#citicscrop_fileForm').ajaxSubmit({
                beforeSubmit: function() {
                },
                success: function(data) {
                    if(data.RET_CODE == 0){
                        var ni = new Image();
                        var rand = Math.random();
                        var str = data.RET_CONTENT.imagePreviewUrl;
                        var url = "";
                        if(str.indexOf("?")>=0){
                        	url = str + "&_rand="+rand;
                        }else{
                        	url = str + "?_rand="+rand;
                        }
                        
                        ni.onload = function(){
                        	$("#citicscrop_previewImg").attr("src", url);
                        	$("#citicscrop_previewImg").removeClass();
                            _setSize(ni.width, ni.height);
                            //$sourceImg.css({"width":_imgSize.nw + "px","height":_imgSize.nh +"px"});
                            $sourceImg.removeAttr("style");
                            $sourceImgContainer.css({"padding-top":_imgSize.y+"px","padding-left":_imgSize.x + "px"});
                    		$sourceImg.attr("src", url);
                            _imgId = data.RET_CONTENT.imageId;
                            if(_citicscrop != null){
                                _citicscrop.destroy();
                            }

                            $("#citicscrop_sourceImg").Jcrop({
                                setSelect: [0,0,saveImgShowDivSize.width*_imgSize.r,saveImgShowDivSize.height*_imgSize.r],
                                onSelect:_showPreview,
                                boxWidth:sourceImgShowDivSize.width,
                                boxHeight:sourceImgShowDivSize.height,
                                aspectRatio:saveImgShowDivSize.width/saveImgShowDivSize.height},
                                function(){
                                    _citicscrop = this;
                                }
                               
                            );
                        };
                       
                        ni.src = url;
                    }
                },
                error: function(a, b) {
                    _opts.chooseImgError(a, b);
                }
            });
        }
    });
    
    //保存头像提示成功1秒后自动消失
    var layer=document.createElement("div");
    layer.id="layer";
    function func()
    {
        var style=
        {
            background:"gray",
            position:"absolute",
            zIndex:50,
            width:"150px",
            height:"20px",
            left:"400px",
            top:"450px"
        };
        for(var i in style)
            layer.style[i]=style[i];   
        if(document.getElementById("layer")==null)
        {
        	layer.innerHTML='<span style="color:blue;">保存头像成功！</span>';
            document.body.appendChild(layer);
            setTimeout("document.body.removeChild(layer)",1000);
        };
    }
    
    // 保存按钮点击事件
    function saveClick(){
        var info = _citicscrop.tellSelect();
        $.ajax({
            url: _opts.processUrl,
            data: {'imgId':_imgId,'xPos': Math.round(info.x), 'yPos': Math.round(info.y), 'width':Math.round(info.w), 'height':Math.round(info.h), 'tWidth':saveImgShowDivSize.width, 'tHeight':saveImgShowDivSize.height},
            type: 'POST',
            dataType: 'json',
            exception: function(code){
            },
            success: function(data){
                if(data.RET_CODE == 0){
                    var url = data.RET_CONTENT.imagePreviewUrl;
                    if(url.indexOf("?")>=0){
                    	//$("#citicscrop_previewImg").attr("src", url + "&_rand=Math.random()");
                    }else{
                    	//$("#citicscrop_previewImg").attr("src", url + "?_rand=Math.random()");
                    }
                    func();
                }
            },
            error: function(a,b){
                _opts.saveImgError(a, b);
            }
        });
    }
    // 计算比例及尺寸
    function _setSize(w, h){
        var wc = $sourceImgShowDiv.width();
        var hc = $sourceImgShowDiv.height();
        _imgSize.w = w;
        _imgSize.h = h;
        if(w <= wc && h <= hc){
            _imgSize.r = 1;
            _imgSize.x = (wc-w)/2;
            _imgSize.y = (hc-h)/2;
            _imgSize.nw = w;
            _imgSize.nh = h;
        }else{
            var rw = w/wc;
            var rh = h/hc;
            if(rw > rh){
                _imgSize.r = rw;
                _imgSize.x = 0;
                _imgSize.y = (hc-h/rw)/2;
                _imgSize.nw = wc;
                _imgSize.nh = h/rw;
            }else{
                _imgSize.r = rh;
                _imgSize.x = (wc-w/rh)/2;
                _imgSize.y = 0;
                _imgSize.nw = w/rh;
                _imgSize.nh = hc;
            }
        }
    }
    // 前端根据选择暂时预览图
    function _showPreview(coords){
        if(parseInt(coords.w) > 0){
            //计算预览区域图片缩放的比例，通过计算显示区域的宽度(与高度)与剪裁的宽度(与高度)之比得到
            var rx = saveImgShowDivSize.width / coords.w; 
            var ry = saveImgShowDivSize.height / coords.h;
            //通过比例值控制图片的样式与显示
            $("#citicscrop_previewImg").css({
                width:Math.round(rx * $("#citicscrop_sourceImg").width()) + "px", //预览图片宽度为计算比例值与原图片宽度的乘积
                height:Math.round(ry * $("#citicscrop_sourceImg").height()) + "px",  //预览图片高度为计算比例值与原图片高度的乘积
                marginLeft:"-" + Math.round(rx * coords.x) + "px",
                marginTop:"-" + Math.round(ry * coords.y) + "px"
            });
        }
    }
}
};