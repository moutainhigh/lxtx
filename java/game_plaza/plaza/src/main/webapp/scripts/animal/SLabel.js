/*
* 文本标签类
*/
function SLabel(text, size, color){
	base(this,LSprite,[]);
	this.addChild(textFieldWith(text, size, color));
	this.text = text;
	this.size = size;
	this.color = color;
}
SLabel.prototype.setText = function(text){
	this.text = text;
	this.removeAllChild();
	this.addChild(textFieldWith(this.text, this.size, this.color));
}
SLabel.prototype.getText = function(){
	return this.text;
}
SLabel.prototype.setSize = function(size){
	this.size = size;
	this.removeAllChild();
	this.addChild(textFieldWith(this.text, this.size, this.color));
}
SLabel.prototype.setColor = function(color){
	this.color = color;
	this.removeAllChild();
	this.addChild(textFieldWith(this.text, this.size, this.color));
}
function textFieldWith(text, size, color){
	labelText = new LTextField();
	labelText.color = color;
	labelText.font = "HG行書体";
	labelText.size = size;
	labelText.x = 0;
	labelText.y = 0;
	labelText.text = text;
	return labelText;
}