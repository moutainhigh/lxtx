import os,errno  
import shutil
from jinja2 import Template
 
class FileUtils(object):
 	"""docstring for FileUtils"""
 	def __init__(self, arg):
 		super(ClassName, self).__init__()
 		self.arg = arg
 	
 	@staticmethod
	def dirlist(path, allfile, ext_name=None):  
	    filelist =  os.listdir(path)  
	  
	    for filename in filelist:  
	        filepath = os.path.join(path, filename)  
	        if os.path.isdir(filepath):  
	            FileUtils.dirlist(filepath, allfile, ext_name)  
	        else:  
	        	if not ext_name:
	        		allfile.append(filepath)
	        	else:
		        	if filepath.endswith(ext_name):
		           		allfile.append(filepath)  
	    return allfile 

	"""remove the whole directory"""
	@staticmethod
	def removeDir(directory):
		shutil.rmtree(directory)

	"""remove all folders/files under a specified directory"""	
	@staticmethod
	def clearDir(directory):
		for root, dirs, files in os.walk(directory, False):
		    for name in files:
		        os.remove(os.path.join(root, name))
		    for name in dirs:
		        os.rmdir(os.path.join(root, name))

	@staticmethod
	def removeAllFolders(directory):
		for root, dirs, files in os.walk(directory, topdown=False):
			for name in dirs:
				os.rmdir(os.path.join(root, name))

	@staticmethod
	def removeFile(file_to_remove):
		os.remove(file_to_remove)

	@staticmethod
	def copyFile(src, dest):
		shutil.copy(src, dest)

	""" Copy the content of a directory to another one """	
	@staticmethod
	def copyTemplates(source_dir, target_dir):
		shutil.copytree(source_dir, target_dir)	

	@staticmethod
	def getFileContent(file_path):
		file_obj = open(file_path)
		try:
			all_content = file_obj.read()	
		finally:
			file_obj.close()
		
		return all_content

	@staticmethod
	def mkdir_p(path):
	    try:
	        os.makedirs(path)
	    except OSError as exc: # Python >2.5 (except OSError, exc: for Python <2.5)
	        if exc.errno == errno.EEXIST and os.path.isdir(path):
	            pass
	        else: raise

	@staticmethod
	def writeFileContent(file_path, content):
		""""""
		file_object = open(file_path, 'w+')
		file_object.write(content)
		file_object.close()

	@staticmethod
	def generateFileContent(file_content, param_map):
		template = Template(file_content)
		return template.render(param_map)

	@staticmethod
	def removeAllFiles(directory, file_ext_name):
		filelist = []
		FileUtils.dirlist(directory, filelist, file_ext_name)
		for filename in filelist:
			FileUtils.removeFile(filename)
  
	@staticmethod
	def getFileSize(file_path):
		return os.path.getsize(file_path)

	@staticmethod
	def readParamsFromFile(param_file_path):
		params = FileUtils.getFileContent(param_file_path)
		paramList = params.split("\n")
		paramMap = {}
		for param in paramList:
			k,v = param.split("=")
			paramMap[k] = v

		return paramMap

"""print FileUtils.dirlist("/Users/wei/dev/projects/clisample", [], ".m") 
FileUtils.copyTemplates("/Users/wei/dev/python/ly/server_template", "/Users/wei/dev/python/ly/server_bj")
FileUtils.removeDir("/Users/wei/dev/python/ly/server_bj")
template = 'Hello {{name}}'
params = {"name": "world"}
print FileUtils.generateFileContent(template, params)
print FileUtils.getFileContent("/Users/wei/dev/python/ly/sample.txt")
FileUtils.writeFileContent("/Users/wei/dev/python/ly/sample.txt", "original content")
paramMap = FileUtils.readParamsFromFile("/Users/wei/dev/game/trunk/mahjong/productivity/server_installer/configs/parama.config")
for k,v in paramMap.items():
	print "%s : %s" % (k, v)"""