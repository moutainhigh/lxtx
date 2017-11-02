package com.lxtx.fb.task.util;

import java.awt.AWTException;
import java.awt.Robot;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public class RobotUtil {
	
	private static Point point1 = new Point(0, 0);
	
//	public static void save(WebElement)
	
	public static void move(Point point2){
		Robot robot;
	    try {
	      robot = new Robot();
	      
	      robot.mouseMove(point2.x,point2.y);
	    } catch (AWTException e1) {
	      e1.printStackTrace();
	    }finally{
	    	
	    }
	}
	
	
}
