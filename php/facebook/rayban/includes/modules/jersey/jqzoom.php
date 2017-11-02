<script language="javascript" type="text/javascript"><!--

$(document).ready(function(){

var options = {
	zoomType: "<?= JQZOOM_ZOOMTYPE; ?>",
	zoomWidth: <?= JQZOOM_ZOOMWIDTH; ?>,
	zoomHeight: <?= JQZOOM_ZOOMHEIGHT; ?>,
	xOffset: <?= JQZOOM_XOFFSET; ?>,
	yOffset: <?= JQZOOM_YOFFSET; ?>,
	position: "<?= JQZOOM_POSITION; ?>",
	lens: <?= JQZOOM_LENS; ?>,
	imageOpacity: <?= JQZOOM_IMAGEOPACITY; ?>,
	title: <?= JQZOOM_TITLE; ?>,
	showEffect: "<?= JQZOOM_SHOWEFFECT; ?>",
	hideEffect: "<?= JQZOOM_HIDEEFFECT; ?>",
	fadeinSpeed: "<?= JQZOOM_FADEINSPEED; ?>",
	fadeoutSpeed: "<?= JQZOOM_FADEOUTSPEED; ?>",
	showPreload: <?= JQZOOM_SHOWPRELOAD; ?>,
	preloadText: "<?= JQZOOM_PRELOADTEXT; ?>",
	preloadPosition: "<?= JQZOOM_PRELOADPOSITION; ?>"
};

$("#jqzoomMain").jqzoom(options);

$(".jqzoom, .jqzoomAdditional").click(function(){return false;});

$(".jqzoomAdditional").hover(function(){
	var $jqzoomMain = $("#jqzoomMain");
	var $jqzoomMainImg = $("#jqzoomMain").children('img:first');
	var $thisImag = $(this).children('img:first');
	
	var jqzoomMainHref = $jqzoomMain.attr('href');
	var jqzoomMainImgSrc = $jqzoomMainImg.attr('src');
		
	$jqzoomMain.attr('href', $(this).attr('href'));
	$jqzoomMainImg.attr('src', $thisImag.attr('src'));
	
	$(this).attr('href', jqzoomMainHref);
	$thisImag.attr('src', jqzoomMainImgSrc);
	
	return false;
}, function(){})
})
//--></script>