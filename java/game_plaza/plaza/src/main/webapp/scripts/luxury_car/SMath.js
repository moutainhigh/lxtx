/*
* 取两个整数之间的随机值
*/
function Srand(n, m){
	var c = m-n+1;	
	return Math.floor(Math.random() * c + n);
}
