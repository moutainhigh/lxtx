/** 
*/
function SSize(w,h){
	this.w = w;
	this.h = h;
}
function SPoint(x, y){
	this.x = x;
	this.y = y;
}
function SRect(x, y, w, h){
	this.x = x;
	this.y = y;
	this.w = w;
	this.h = h;
}
function posDistance(srcPos, dstPos){
	x = Math.abs(dstPos.x - srcPos.x);
	y = Math.abs(dstPos.y - srcPos.y);
	return Math.sqrt(x*x + y*y);
}
function posDiv(pos, div){
	return new SPoint(pos.x / div, pos.y / div);
}
function isPosInRect(posX, posY, rect){
	if (posX < rect.x)
		return false;
	if (posX > rect.x+rect.w)
		return false;
	if (posY < rect.y)
		return false;
	if (posY > rect.y + rect.h)
		return false;
	return true;
}
function posAngle(start, end){
    var diff_x = end.x - start.x;
    var diff_y = end.y - start.y;
	var angle = 360*Math.atan(diff_y/diff_x)/(2*Math.PI);
	if (end.y >= start.y){
		if (end.x >= start.x){
			return angle;
		}
		return 180 + angle;
	}

	if (end.x >= start.x){
		return 360 + angle;
	}

	return 180 + angle;
}

