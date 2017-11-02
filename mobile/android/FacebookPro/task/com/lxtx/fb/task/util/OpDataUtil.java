package com.lxtx.fb.task.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OpDataUtil {
	
	private static boolean test = false;
	
	public static void main(String[] args) throws Exception{
		
		List<OpData> opDataList = new ArrayList<OpData>();
		WebDriver driver = null;
		
//		opDataList.add(new OpData("0", 0, new OpSendKeys(null, By.xpath("//div[@data-testid='message-field']"), ""), 2000, ""));
//		opDataList.add(new OpData("1", 0, new OpClick(null, By.xpath("//div[@data-testid='ads-endcard-checkbox']/div")), 1000, ""));
//		opDataList.add(new OpData("2", 0, new OpClick(null, By.xpath("//button[@data-testid='SUIAbstractMenu/button']")), 1000, ""));
//		opDataList.add(new OpData("3", 0, new OpClick(null, By.xpath("//div[@data-testid='SUISelectorOption/container']/div[contains(text(),'Shop Now')]/parent::div")), 1000, "2"));
//		opDataList.add(new OpData("4", 0, new OpFunc() {
//			@Override
//			public void op() throws Exception {
//				
//			}
//		}, 2000, ""));
//		opDataList.add(new OpData("5", 1, new OpClick(null, By.xpath("//button[@data-testid='place-order-button-WIZARD_FOOTER']")), 15000, "3"));
		
		int i = 0;
		
		if(i >= 3){
			opDataList.add(new OpData("0", -1, new OpClick(driver, By.xpath("//table/tbody/tr/td[2]/button/div/span[contains(text(),'Add')]/parent::div/parent::button")), 2000, ""));
		}
		opDataList.add(new OpData("1", -1, new OpClick(driver, By.xpath("//table/tbody/tr/td[1]/div/div["+(i+1)+"]/div[@data-testid='ads-draggable-indexed-tab-bar-item']")), 1000, "0"));
		opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//button[@data-testid='image-select-button']")), 1000, ""));
		opDataList.add(new OpData("3", 0, new OpSendKeys(driver, By.name("fileToUpload"), ""), 10000, "2"));
		opDataList.add(new OpData("4", 0 , new OpClick(driver, By.xpath("//button[@data-testid='image-dialog-close-button']")),5000, "3"));
		
		opDataList.add(new OpData("5", 0, new OpSendKeys(driver, By.xpath("//textarea[@data-testid='headline_field']"), ""), 1000, ""));
		
		opDataList.add(new OpData("6", 0, new OpSendKeys(driver, By.xpath("//div[contains(text(),'Describe why people should visit your site')]/parent::div/textarea"), ""), 1000, ""));
		
		if(i == 0){
			opDataList.add(new OpData("7", 0, new OpSendKeys(driver, By.xpath("//*[@data-testid='website-url-field']"), ""), 1000, ""));
//			opDataList.add(new OpData("8", 0, new OpClick(driver, By.xpath("//div[@title='Ex: http://www.example.com/page']/parent::div/parent::div/div[2]/div/div/div/div/ul/li[1]")), 3000, "7"));
		}
		
		List<OpData> randomList = OpDataUtil.randomList(opDataList);
		
		for(OpData opData : randomList){
			System.out.println(opData.id);
		}
	}
	
	public static void randomOp(List<OpData> opDataList) throws Exception{

		List<OpData> list = randomList(opDataList);
	
		op(list);
	}
	
	public static void op(List<OpData> opDataList) throws Exception{
		for(OpData opData : opDataList){
			op(opData);
		}
	}
	
	public static void op(OpData opData) throws Exception{
		if(test){
			System.out.println("op:"+opData.id+";"+opData.func.getClass().getName());
		}else{
			if(opData.func != null){
			
				try{
					opData.func.op();
				}catch(Exception e){
					CommonUtil.debugErr(e);
				}
			}
			
			CommonUtil.sleep(opData.sleep);
		}
	}
	
	private static List<OpData> randomList(List<OpData> opDataList){
		List<OpData> ret = new ArrayList<OpData>();
		List<List<OpData>> list = new ArrayList<List<OpData>>();
		int firstIdx = -1;
		int lastIdx = -1;
		
		for(OpData opData : opDataList){
			
			if(opData.pre == null || opData.pre.length() == 0){
				addOne(list, opData);
				
				if(opData.point != 0){
					if(opData.point == 1){
						lastIdx = list.size() - 1; 
					}else{
						firstIdx = list.size() - 1;
					}
				}
			}else{
				boolean find = false;
				
				for(int k = 0; k < list.size(); k ++){
					List<OpData> subList = list.get(k);
					
					for(int i = 0; i < subList.size(); i ++){
						OpData od = subList.get(i);
						
						if(opData.pre.equals(od.id)){
							subList.add(opData);
							find = true;
						}else if(od.pre != null && od.pre.equals(opData.id)){
							subList.add(i, opData);
							find = true;
						}
						
						if(find){
							if(opData.point == 1){
								lastIdx = k;
							}else if(opData.point == -1){
								firstIdx = k;
							}
							
							break;
						}
					}
					if(find){
						break;
					}
				}
				if(!find){
					addOne(list, opData);
					
					if(opData.point == 1){
						lastIdx = list.size() - 1;
					}else if(opData.point == -1){
						firstIdx = list.size() - 1;
					}
				}
			}
		}
		
		if(firstIdx >= 0){
			ret.addAll(list.remove(firstIdx));
		}
		
		List<OpData> last = null;
		if(lastIdx >= 0){
			if(lastIdx > firstIdx && firstIdx >= 0){
				last = list.remove(lastIdx - 1);
			}else{
				last = list.remove(lastIdx);
			}
		}
		
		int size = list.size();
		for(int i = 0; i < size; i ++){
			List<OpData> subList = list.remove(new Random().nextInt(list.size()));
			
			ret.addAll(subList);
		}
		
		if(last != null){
			ret.addAll(last);
		}
		
		return ret;
	}
	
	private static void addOne(List<List<OpData>> list, OpData opData){
		List<OpData> sub = new ArrayList<OpData>();
		sub.add(opData);
		list.add(sub);
	}
}
