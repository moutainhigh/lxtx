<script language="javascript" type="text/javascript"><!--

$(document).ready(function(){

var options = {
	zoomType: "<?php echo JQZOOM_ZOOMTYPE; ?>",
	zoomWidth: <?php echo JQZOOM_ZOOMWIDTH; ?>,
	zoomHeight: <?php echo JQZOOM_ZOOMHEIGHT; ?>,
	xOffset: <?php echo JQZOOM_XOFFSET; ?>,
	yOffset: <?php echo JQZOOM_YOFFSET; ?>,
	position: "<?php echo JQZOOM_POSITION; ?>",
	lens: <?php echo JQZOOM_LENS; ?>,
	imageOpacity: <?php echo JQZOOM_IMAGEOPACITY; ?>,
	title: <?php echo JQZOOM_TITLE; ?>,
	showEffect: "<?php echo JQZOOM_SHOWEFFECT; ?>",
	hideEffect: "<?php echo JQZOOM_HIDEEFFECT; ?>",
	fadeinSpeed: "<?php echo JQZOOM_FADEINSPEED; ?>",
	fadeoutSpeed: "<?php echo JQZOOM_FADEOUTSPEED; ?>",
	showPreload: <?php echo JQZOOM_SHOWPRELOAD; ?>,
	preloadText: "<?php echo JQZOOM_PRELOADTEXT; ?>",
	preloadPosition: "<?php echo JQZOOM_PRELOADPOSITION; ?>"
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