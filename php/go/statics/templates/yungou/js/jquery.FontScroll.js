/**
 +-------------------------------------------------------------------
 * jQuery FontScroll - ���������Ϲ������ - http://www.cibiji.com
 +-------------------------------------------------------------------
 * @version    1.0.0 beta
 * @since      2014.06.12
 * @author     kongzhim <kongzhim@163.com> <http://www.cibiji.com>
 * @github     http://git.oschina.net/kzm/FontScroll
 +-------------------------------------------------------------------
 */

(function($){
    $.fn.FontScroll = function(options){
        var opts = $.extend({},$.fn.FontScroll.defualts,options);
		opts._this = this;
		$.fn.FontScroll.setInit(opts);
		$.fn.FontScroll.Up(opts);
		
    }
	//Ĭ����������
	$.fn.FontScroll.defualts = {
		Time: 3000,	//ʱ��
		Class: 'fontColor', //��ʽ
		Num: 1  //��ֵ��ʽ��λ�ã����㿪ʼ������Ĭ�ϵڶ���
	}
	//��ʼ��
	$.fn.FontScroll.setInit = function(opts){
        var _ul = opts._this.children().first();
        _ul.clone().insertAfter(_ul);//��ʼ����¡
        _ul.children().eq(opts.Num).addClass(opts.Class);//�����ʽ
	}

	//���Ϲ���
	$.fn.FontScroll.Up = function(opts){
		var _ul = opts._this.children().first();
		var _li = opts._this.find('li');
		var _ulH = _ul.height(); //�ܸ߶�
		var _liH = _ul.children().first().outerHeight();//һ�и߶�
		var _temp = _liH;
		var _temp1 = opts.Num;

        var timeID = setInterval(Up,opts.Time);
		_ul.hover(function(){clearInterval(timeID)},function(){timeID = setInterval(Up,opts.Time);});
		
		function Up(){
			_ul.animate({marginTop: '-'+_temp});
			_li.eq(_temp1).removeClass(opts.Class)
			_temp1++;
			_li.eq(_temp1).addClass(opts.Class);
			if(_ulH == _temp){
				_ul.animate({marginTop: '-'+_temp},"normal",over);
			} else {
				_temp += _liH;
			}
		}
		function over(){
            _ul.attr("style",'margin-top:0');
            _temp = _liH;
			_temp1 = opts.Num;
			_li.removeClass(opts.Class).next().eq(0).addClass(opts.Class);
		}
		
	}
	
})(jQuery);
