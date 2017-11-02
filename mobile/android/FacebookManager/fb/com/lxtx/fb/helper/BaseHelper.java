package com.lxtx.fb.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.pojo.IKey;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class BaseHelper<T extends IKey<S>, S> implements InitializingBean{

	private List<T> list = new ArrayList<T>();
	private ConcurrentMap<S, T> map = new ConcurrentHashMap<S, T>();
	
	public List<T> list(){
		return this.list;
	}
	
	public T get(S s){
		return map.get(s);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		realInit();
		
		if(sleep > 0l){
			while(true){
				try{
					Thread.sleep(sleep);
				}catch(Exception e){
				
				}
				
				realInit();
			}
		}
	}
	
	protected void realInit(){
		this.list = handler.getAll();
		
		if(this.list != null && this.list.size() > 0){
			for(T t : this.list){
				this.map.put(t.getKey(), t);
			}
		}
	}

	//ioc
	private SimpleIbatisEntityHandler<T> handler;

	private long sleep = 0l;
	
	public void setHandler(SimpleIbatisEntityHandler<T> handler) {
		this.handler = handler;
	}

	public void setSleep(long sleep) {
		this.sleep = sleep;
	}
	
	
}
