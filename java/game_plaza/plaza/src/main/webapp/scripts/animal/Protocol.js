/*
* 协议相关
*/
var ROOM_STATE_WAITING_FOR_MASTER = 0;
var ROOM_STATE_WAITING_FOR_START = 1;
var ROOM_STATE_WAITING_FOR_SET_CHIPS = 2;
var ROOM_STATE_WAITING_FOR_CACLULATE = 3;
var ROOM_STATE_WAITING_FOR_SERVER = 4;
var ROOM_STATE_MAX = 5;

var REQEST_MASTER_STATE_NOT_IN_START_STATE = 0;
var REQEST_MASTER_STATE_BE_SETTED_BY_OTHER = 1;
var REQEST_MASTER_STATE_NOT_ENOUGH_CHIPS = 2;
var REQEST_MASTER_STATE_SUCCESS = 3;
var REQEST_MASTER_STATE_INFOMATION = 4;
var REQEST_MASTER_STATE_DOWN_SUCCESS = 5;

var S2C_PROTOCOL_LOGIN					= 2001;
var S2C_PROTOCOL_SETTED_CHIPS 			= 2002;
var S2C_PROTOCOL_GAME_STATE				= 2003;
var S2C_PROTOCOL_REQUEST_MASTER			= 2004;
var S2C_PROTOCOL_LAST_RESULT			= 2005;
var S2C_PROTOCOL_TE_MA					= 2006;
var S2C_PROTOCOL_SET_CHIPS				= 2007;
var S2C_PROTOCOL_MASTER_LIST			= 2008;
var S2C_PROTOCOL_GET_OR_SAVE_MONEY		= 2009;
var S2C_PROTOCOL_RESULT					= 2010;
var S2C_PROTOCOL_QUERY_OPEN_HISTORY		= 2011;
var S2C_PROTOCOL_QUERY_SET_HISTORY		= 2012;
var S2C_PROTOCOL_CHAT					= 2013;
var S2C_PROTOCOL_QUERY_GAME_SETTING		= 2014;
var S2C_PROTOCOL_HINT					= 2015;
var S2C_PROTOCOL_NOTIFYCATION			= 2016;
var S2C_PROTOCOL_FORBID					= 2018;
var S2C_PROTOCOL_RELIEF					= 2019;
var S2C_PROTOCOL_CONTINE_SUCC			= 2020;
var S2C_PROTOCOL_TICKOUT				= 2021;

var S2CUserInfo = {
	id:0,
	name:"",
	chips:0,
	money:0,
	reliefCount:0
}
var S2CProtocolLogin = {
	protocol:S2C_PROTOCOL_LOGIN,
	code:0,
	errMsg:"",
	user:new Array()
}
var S2CProtocolGameState = {
	protocol:S2C_PROTOCOL_GAME_STATE,
	state:0,
	interval:0
}
var S2CProtocolRequestMaster = {
	protocol:S2C_PROTOCOL_REQUEST_MASTER,
	user:new Array(),
	state:0,
	remainCount:0,
	score:0
}
var S2CProtocolSetChips = {
	protocol:S2C_PROTOCOL_SET_CHIPS,
	code:0,
	errMsg:"",
	user:new Array(),
	lotteryIndex:0,
	num:0
}
var S2CProtocolResult = {
	protocol:S2C_PROTOCOL_RESULT,
	result_td:0,
	result_wx:0,
	result_sx:0,
	win_num:0,
	setted_num:0,
	master_score:0
}
var S2CProtocolLastResult = {
	protocol:S2C_PROTOCOL_LAST_RESULT,
	result_td:0,
	result_wx:0,
	result_sx:0
}
var S2CProtocolTeMa = {
	protocol:S2C_PROTOCOL_TE_MA,
	tm_sm_td:0,
	tm_sm_wx:0,
	tm_sm_sx:0,
	tm_lm_td:0,
	tm_lm_sx:0
}
var S2CProtocolQueryOpenHistory = {
	protocol:S2C_PROTOCOL_QUERY_OPEN_HISTORY,
	list:new Array(),
	tema_yilou:new Array()
}
var S2CProtocolQuerySetHistory = {
	protocol:S2C_PROTOCOL_QUERY_SET_HISTORY,
	list:new Array()
}
var S2CProtocolChat = {
	protocol:S2C_PROTOCOL_CHAT,
	userName:"",
	msg:""
}
var S2CProtocolGetOrSaveMoney = {
	protocol:S2C_PROTOCOL_GET_OR_SAVE_MONEY,
	code:0,
	msg:"",
	chips:0,
	moeny:0
}
var S2CProtocolMasterList = {
	protocol:S2C_PROTOCOL_MASTER_LIST,
	users:new Array()	
}
var S2CProtocolSettedChips = {
	protocol:S2C_PROTOCOL_SETTED_CHIPS,
	total:new Array(),
	single:new Array()
}
var S2CProtocolQueryGameSetting = {
	protocol:S2C_PROTOCOL_QUERY_GAME_SETTING
}

var S2CProtocolHint = {
	protocol:S2C_PROTOCOL_HINT,
	gold:0,
	gamename:"",
	time:""
}

var S2CProtocolNotifycation = {
	protocol:S2C_PROTOCOL_NOTIFYCATION,
	msg:""
}
var S2CProtocolForbid = {
	protocol:S2C_PROTOCOL_FORBID,
	state:0
}
var S2CProtocolRelief = {
	protocol:S2C_PROTOCOL_RELIEF,
	chips:0,
	reliefCount:0
}
var S2CProtocolContinueSucc = {
	protocol:S2C_PROTOCOL_CONTINE_SUCC
}
var S2CProtocolTickOut = {
	protocol:S2C_PROTOCOL_TICKOUT
}
/**
*the protocol of client to server;
*/
var C2S_PROTOCOL_LOGIN					= 1002;
var C2S_PROTOCOL_REQUEST_MASTER			= 1102;
var C2S_PROTOCOL_SET_CHIPS				= 1103;
var C2S_PROTOCOL_CHAT					= 1104;
var C2S_PROTOCOL_GET_MASTER_LIST		= 1105;
var C2S_PROTOCOL_GET_OR_SAVE_MONEY		= 1106;
var C2S_PROTOCOL_CONTINE 				= 1107;
var C2S_PROTOCOL_SET_GAME_SETTING		= 1108;
var C2S_PROTOCOL_RELIEF					= 1109;
var C2S_PROTOCOL_TICK					= 1110;

var C2S_PROTOCOL_QUERY_OPEN_HISTORY		= 1602;
var C2S_PROTOCOL_QUERY_SET_HISTORY		= 1603;
var C2S_PROTOCOL_QUERY_GAME_SETTING		= 1604;


var C2SProtocolLogin = {
	protocol:C2S_PROTOCOL_LOGIN,
	cookie:''
}
var C2SProtocolRequestMaster = {
	protocol:C2S_PROTOCOL_REQUEST_MASTER,
	up:1 //1上庄， 0下庄
}
var C2SProtocolSetChips = {
	protocol:C2S_PROTOCOL_SET_CHIPS,
	lotteryIndex:1,
	num:10000
}
var C2SProtocolQueryOpenHistory = {
	protocol:C2S_PROTOCOL_QUERY_OPEN_HISTORY
}
var C2SProtocolQuerySetHistory = {
	protocol:C2S_PROTOCOL_QUERY_SET_HISTORY
}
var C2SProtocolChat = {
	protocol:C2S_PROTOCOL_CHAT,
	msg:""
}
var C2SProtocolGetOrSaveMoney = {
	protocol:C2S_PROTOCOL_GET_OR_SAVE_MONEY,
	getNum:0,
	saveNum:0
}
var C2SProtocolGetMasterList = {
	protocol:C2S_PROTOCOL_GET_MASTER_LIST
}
var C2SProtocolContinue = {
	protocol:C2S_PROTOCOL_CONTINE,
	arrSetChips:new Array()
}

var C2SProtocolQueryGameSetting = {
	protocol:C2S_PROTOCOL_QUERY_GAME_SETTING,
}

var C2SProtocolSetGameSetting = {
	protocol:C2S_PROTOCOL_SET_GAME_SETTING,
	bgMusic:0,
	music:0
}
var C2SProtocolRelief = {
	protocol:C2S_PROTOCOL_RELIEF
}
var C2SProtocolTick = {
	protocol:C2S_PROTOCOL_TICK
}