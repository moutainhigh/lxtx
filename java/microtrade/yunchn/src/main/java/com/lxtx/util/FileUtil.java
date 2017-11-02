package com.lxtx.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
	public static void main(String[] args){
		/*String remote="http://10.23.118.155/group1/M00/55/97/oYYBAFXf-1OISc0WAAACwSak1P4AAAACgP_8mkAAALZ543.txt";
		String nativefile="FileTemp";
		String sysTemp = System.getProperty("java.io.tmpdir"); 
		String rootPath=sysTemp+ File.separator+ nativefile; //根路径
		saveFile(remote,rootPath,"abc","abc.txt");
		fileToZip(rootPath,sysTemp,"111111");//压缩文件
		System.out.println("压缩完成！！！");
		File rootFile=new File(rootPath+"111111.zip");
		System.out.println(rootFile.isFile());*/
		String fileName="abc.aa.txt";
		String qian=fileName.substring(0,fileName.lastIndexOf("."));
		String hou=fileName.substring(fileName.lastIndexOf(".")+1);
		System.out.println(qian+new Date().getTime()+"."+hou);
	}
	/**
	 * 保存文件
	 * @author 郝斌斌
	 * @date 2015年8月28日 下午3:28:53
	 * @param remoteFilePath 远程文件路径
	 * @param nativeFile 需要新建的文件夹
	 * @param rootPath 跟路径
	 * @param fileName 需要保存的文件名称带后缀名称
	 * @return
	 */
	public static boolean saveFile(String remoteFilePath,String rootPath,String nativeFile,String fileName){
		URL url=null;
		try{
			url=new URL(remoteFilePath);
		}catch(MalformedURLException e){
			e.printStackTrace();
		}
		File file = new File(rootPath
		+ File.separator  
		+nativeFile );  
		if  (!file .exists()  && !file .isDirectory())      
		{       
		    file.mkdir();    
		}
		InputStream in=null;
		HttpURLConnection uc=null;
		try {
			uc = (HttpURLConnection) url.openConnection();
			uc.setDoInput(true);//设置是否要从 URL 连接读取数据,默认为true
			uc.setConnectTimeout(5 * 1000);
			//uc.setRequestProperty("Connetion", "Keep-Alive");
			//uc.connect();
			in = uc.getInputStream();
			//in=url.openStream();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		try {
			if(in!=null){
				Files.copy(in, Paths.get(file.getPath(),fileName));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			
			try {
				if(null != in)
				in.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	public static String getNm(String name){
		String pdName="";
		if(!"".equals(name)){
		 if(name.split("-").length>1){
			 pdName=name.split("-")[1];
		 }else{
			 if(name.length()<10){
				 pdName=name;
			 }else{
				 pdName= name.substring(0, 9);
				
			 }
		 }
		}
		return pdName;
	}
	public static String getTime(long times){
		Date date=new Date();
		long timeL= date.getTime()-times;
		long days = timeL / (1000 * 60 * 60 * 24);
	    long hours = (timeL % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
	    long minutes = (timeL % (1000 * 60 * 60)) / (1000 * 60);
	    long seconds = (timeL % (1000 * 60)) / 1000;
	    if(days==0){
	    	return hours+"时"+minutes+"分"+seconds+"秒";
	    }else{
	    	return days+"天"+hours+"小时"+minutes+"分"+seconds+"秒";
	    }
	}
	  /**
	     * 将存放在sourceFilePath目录下的源文件,打包成fileName名称的ZIP文件,并存放到zipFilePath。
	     * @param sourceFilePath 待压缩的文件路径
	     * @param zipFilePath    压缩后存放路径
	     * @param fileName       压缩后文件的名称
	     * @return flag
	     */
		public static boolean fileToZip(String sourceFilePath,String zipFilePath,String fileName) {
	        boolean flag = false;
	        File sourceFile = new File(sourceFilePath);
	        FileInputStream fis = null;
	        BufferedInputStream bis = null;
	        FileOutputStream fos = null;
	        ZipOutputStream zos = null;
	         
	        if(sourceFile.exists() == false) {
	          //  System.out.println(">>>>>> 待压缩的文件目录：" + sourceFilePath + " 不存在. <<<<<<");
	        } else {
	            try {
	                File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
	                if(zipFile.exists()) {
	                   // System.out.println(">>>>>> " + zipFilePath + " 目录下存在名字为：" + fileName + ".zip" + " 打包文件. <<<<<<");
	                } else {
	                    File[] sourceFiles = sourceFile.listFiles();
	                    if(null == sourceFiles || sourceFiles.length < 1) {
	                      //  System.out.println(">>>>>> 待压缩的文件目录：" + sourceFilePath + " 里面不存在文件,无需压缩. <<<<<<");
	                    } else {
	                        fos = new FileOutputStream(zipFile);
	                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
	                        byte[] bufs = new byte[1024*10];
	                        for(int i=0;i<sourceFiles.length;i++) {
	                        	if(sourceFiles[i].isDirectory()){
	                        		
	                        	}
	                            // 创建ZIP实体,并添加进压缩包
	                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
	                            zos.putNextEntry(zipEntry);
	                            // 读取待压缩的文件并写进压缩包里
	                            fis = new FileInputStream(sourceFiles[i]);
	                            bis = new BufferedInputStream(fis,1024*10);
	                            int read = 0;
	                            while((read=bis.read(bufs, 0, 1024*10)) != -1) {
	                                zos.write(bufs, 0, read);
	                            }
	                            fis.close();
	                            bis.close();
	                        }
	                        flag = true;
	                    }
	                }
	            } catch (FileNotFoundException e) {
	                e.printStackTrace();
	                throw new RuntimeException(e);
	            } catch (IOException e) {
	                e.printStackTrace();
	                throw new RuntimeException(e);
	            } finally {
	                // 关闭流
	                try {
	                    if(null != bis) bis.close();
	                    if(null != zos) zos.close();
	                    if(null !=fis) fis.close();
	                    if(null !=fos) fos.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    throw new RuntimeException(e);
	                }
	            }
	        }
	         
	        return flag;
	    }
		/**
		 * 压缩文件
		 * @author 郝斌斌
		 * @date 2015年9月1日 上午10:11:55
		 * @param inputFileName 需要压缩的路径
		 * @param zipFileName 压缩后的路径
		 * @throws Exception
		 */
		public static void zip(String inputFileName,String zipFileName) throws Exception {
			  System.out.println(zipFileName);//压缩文件路径
			  zip1(zipFileName, new File(inputFileName));
			 }
	    private static void zip1(String zipFileName, File inputFile)  {
	    	
			  ZipOutputStream out=null;
			  try{
				  out  = new ZipOutputStream(new FileOutputStream(
						    zipFileName));
						  zip(out, inputFile, "");
						//  System.out.println("zip ok");
			  }catch(Exception e){
				  e.printStackTrace();
			  }finally{
				 
					  try {
						  if(null !=out){
							  out.close();
						  }
					} catch (IOException e) {
						e.printStackTrace();
					}
				  
			  }
			  
			 }
		 private static void zip(ZipOutputStream out, File f, String base)  {
			  if (f.isDirectory()) {
				 try{
					 File[] fl = f.listFiles();
					   out.putNextEntry(new ZipEntry(base + "/"));
					   base = base.length() == 0 ? "" : base + "/";
					   for (int i = 0; i < fl.length; i++) {
					    zip(out, fl[i], base + fl[i].getName());
					   }
				 }catch(Exception e){
					 e.printStackTrace();
				 }
			  
			  } else {
			   try {
				out.putNextEntry(new ZipEntry(base));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			   FileInputStream in=null;
			   try{
				   in= new FileInputStream(f);
				   int b;
				   while ((b = in.read()) != -1) {
				    out.write(b);
				   }
			   }catch(Exception e){
				   e.printStackTrace();
			   }finally{
				   
					try {
						if(null !=in)
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			   }
			  
			   
			  }
			}
		/** 
		  * 删除某个文件夹下的所有文件夹和文件 
		  * 
		  * @param delpath 
		  *            String 
		  * @throws FileNotFoundException 
		  * @throws IOException 
		  * @return boolean 
		  */  
		 public static boolean deletefile(String delpath) throws Exception {  
		  try {  
			  System.out.println("删除！！！");
		   File file = new File(delpath);  
		   // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true  
		   if (file.isFile()) {
			   System.out.println(delpath);
			   System.out.println("是个文件，删除文件！！！");
			   return    file.delete(); 
		   
		    
		   } else if (file.isDirectory()) {  
		    String[] filelist = file.list();  
		    for (int i = 0; i < filelist.length; i++) {  
		     File delfile = new File(delpath + "\\" + filelist[i]);  
		     if (!delfile.isDirectory()) {  
		      delfile.delete();  
		      System.out  
		        .println(delfile.getAbsolutePath() + "删除文件成功");  
		     } else if (delfile.isDirectory()) {  
		      deletefile(delpath + "\\" + filelist[i]);  
		     }  
		    }  
		    System.out.println(file.getAbsolutePath()+"删除成功");  
		    file.delete();  
		   }  
		  
		  } catch (FileNotFoundException e) {  
		   System.out.println("deletefile() Exception:" + e.getMessage());  
		  }  
		  return true;  
		 }  
}
