$(document).ready(function () {
    var nput = $('.one').val();

    $('#buy_num').val(nput);
    $('#total').text(nput * 10);
});

$(document).ready(function () {
    $('#buy_num').change(function () {
        var nput = parseInt($('.num-input').val());
        $('#total').text(nput * 10);
    });
});

function minus() {
    var nput = parseInt($('.num-input').val());
    nput -= 1;
    if (nput >= 1) {
        $('.num-input').val(nput);
        $('#total').text(nput * 10);
    }
    if (nput < 1) {
        $('.zhishao').show();
        setTimeout(function () {
            $('.zhishao').hide();
        }, 2000);
        $('.num-input').val(1);
        $('#total').text(1 * 10);
    }
}
function plus() {
    var nput = parseInt($('.num-input').val());
    nput += 1;
    if (nput <= 500) {
        $('.num-input').val(nput);
        $('#total').text(nput * 10);
    }
    if (nput > 500) {
        $('.zuiduo').show();
        setTimeout(function () {
            $('.zuiduo').hide();
        }, 2000);
        $('.num-input').val(500);
        $('#total').text(500 * 10);
    }
}

function inp(obj) {
    $('.num-input').attr('value', obj);
    $('#total').text(obj * 10);
}

$(function () {
    $('.li').click(function () {
        $(this).find('i').addClass('show');
        $(this).siblings().find('i').removeClass('show');
    });
    $('.confirm').click(function () {
        $('.payment').show();
        $('.bodys').hide();
        $('.buycont').hide();
    });

});
function partake(obj) {
    $('#buy_num').val(2);
    $('#total').text(2 * 10);
    $('.bodys').show();
    $('.buycont').show();
    $('#partake_number').text(obj);
}
function col() {
    $('.bodys').hide();
    $('.buycont').hide();
}

$(document).ready(function () {
    var kj = $(window).height();
    var wd = $(document).height();
    if (wd > kj) {
        $('.bom').css('position', 'relative');
        $('.dom').css('position', 'absolute');
        $('.dom').css('top', '0px');
    } else {
        $('.bom').css('position', 'absolute')
    }
});
//往期揭晓红点
$(document).ready(function () {
    $(".past").find(".ul .li:first-child .le").css('color', '#d91d37');
    $(".past").find(".ul .li:first-child .cont .yuan").css('background', '#d91d37');
    $(".past").find(".ul .li:first-child .ri").css('color', '#d91d37');
});


/* *
 * 倒计时
 */

var Tday_jx = [];
var timeID_jx = [];
var timeID_h_jx = [];
var updatePrecode_jx = [];
var Secondms_jx = 60 * 1000;
var minutems_jx = 1000;
var appPath = global_util.getAppPath();

//倒计时时钟
function clock_jx(key, skin) {
    var diff = Tday_jx[key];
    var DifferSecond = Math.floor(diff / Secondms_jx);
    diff -= DifferSecond * Secondms_jx;
    var DifferMinute = Math.floor(diff / minutems_jx);
    diff -= DifferMinute * minutems_jx;

    if (DifferSecond.toString().length < 2) DifferSecond = '0' + DifferSecond;
    if (DifferMinute.toString().length < 2) DifferMinute = '0' + DifferMinute;

    var sTime = "";
    sTime += "<span>" + DifferSecond + "</span><b>:</b>";
    sTime += "<span>" + DifferMinute + "</span><b>:</b>";
    sTime += "<span class='timeHm'>" + 99 + "</span>";

    if (Tday_jx[key] <= 0) {
        //结束计时，开奖
        timeID_jx[key] = window.clearInterval(timeID_jx[key]);
        timeID_h_jx[key] = window.clearInterval(timeID_h_jx[key]);
        // $('#bgaudio-apan').append('<audio autoplay="autoplay" loop="loop" id="bgaudio">' +
        //     '<source src="http://www.miliduobao.com/style/mobile/music/guoan.mp3" type="audio/mpeg">' +
        //     '</audio>');
        //
        // // $('#bgaudio').attr('autoplay','autoplay');
        // document.getElementById('bgaudio').play();
        $("#leftTimeJx100").text("");
        $("#Timecont").text("获取官方时时彩数据...");
        setTimeout(function () {
            //ajax_index_up(100);
            var path = global_util.getAppPath();
            window.location.href = path + '/index';
        }, 5000);
    } else {
        Tday_jx[key] = Tday_jx[key] - 1000;
        document.getElementById("leftTimeJx" + key).innerHTML = sTime;
    }
}

function ajax_index_up(i) {
    if (i <= 0) {
        return;
    }
    var path = global_util.getAppPath();
    $.ajax({
        type: "POST",
        url: path + '/lotteryorder/ajax_index_up',
        dataType: 'json',
        error: function (request) {
            console.log('error');
        },
        success: function (data) {
            console.log("in ajax_index_up, status is :" + data.status);
            if (data.status == '1') {
                // window.location.reload(true);
                // window.location.reload();
                window.location.href = path + '/index?token=' + token;
            } else {

                setTimeout(function () {
                    i--;
                    ajax_index_up(i);
                }, 5000);
            }
        }
    });
}

/**                                                                                                                                                                            
 * 倒计时入口函数
 * @param key       计时DIV的循环ID key,即 id="leftTimeJx{$key}"
 * @param diff_time 倒计时时间差
 * @param skin      倒计时样式：默认0
 */
function onload_leftTime_jx(key, diff_time, skin) {
    skin = skin ? skin : 'default';
    Tday_jx[key] = parseInt(diff_time);
    timeID_jx[key] = window.setInterval(function () {
        clock_jx(key, skin);
    }, 1000);

    //毫秒单独计时
    var h = 100;
    timeID_h_jx[key] = window.setInterval(function () {
        if (h <= 0) h = 100;
        h = parseInt(h) - 1;
        if (h.toString().length < 1) h = '00';
        if (h.toString().length == 1) h = '0' + h;
        if (h.toString().length > 2) h = '99';
        $("#leftTimeJx" + key).find('.timeHm').html(h)
    }, 10);
}

function showPrecode(data) {
    var contentDisclosed = "<span>第{{pre_sn}}期结果为：</span><font class=\"old_qishu\">{{code}}</font>";
    $(".top3").eq(0).find(".le").eq(0).html(Mustache.render(contentDisclosed, data));
}

function loadPreCode(key, pre_sn) {
    var path = global_util.getAppPath();
    updatePrecode_jx[key] = setInterval(function() {
        $.get(path + '/lotteryorder/ajax_index_check?index='+pre_sn, {}, function(data) {
            if (data.status == 1) {
                var code = data.code;
                data.pre_sn = pre_sn;
                showPrecode(data);
                clearInterval(updatePrecode_jx[key]);
            }
        });
    }, 5000);
}