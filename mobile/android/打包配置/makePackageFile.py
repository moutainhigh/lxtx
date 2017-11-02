#!/usr/bin/python
# coding=utf8
# build_native.py
# Build native codes

import sys
import os, os.path
import shutil
import xlrd
from xml.dom import minidom

reload(sys)
sys.setdefaultencoding('utf-8')

configExcelPath = "payConfig.xls"
targetProjectRootPath = "../"
projectTemplates = {}
projectTemplates["wydp"]={"templateDir":"午夜大片97_", "androidProjectDir":"wydp0"}
projectTemplates["zb001"]={"templateDir":"zb001_", "androidProjectDir":"zb001"}
projectTemplates["mmzb"]={"templateDir":"幂幂直播_", "androidProjectDir":"zhibo0"}
projectTemplates["ybsp"]={"templateDir":"云播影视352_", "androidProjectDir":"ybsp"}
projectTemplates["3dkb"]={"templateDir":"3dkb_", "androidProjectDir":"3dkb0"}
projectTemplates["sdk"]={"templateDir":"AppBoxSdk_", "androidProjectDir":"AppBoxSdk"}



def copy_files(src, dst):
	for item in os.listdir(src):
		path = os.path.join(src, item)
		# Android can not package the file that ends with ".gz"
		if not item.startswith('.') and not item.endswith('.gz') and os.path.isfile(path):
			shutil.copy(path, dst)
		if os.path.isdir(path):
			new_dst = os.path.join(dst, item)
			if False == os.path.exists(new_dst):
				os.mkdir(new_dst)
			copy_files(path, new_dst)

def check_element(element, filters):
	for j in range(0, len(filters)):
		if element.getAttribute(filters[j]["key"]) != filters[j]["value"]:
			return False
	return True
	
def replace_xml(rootElement, elementPath, elementFilterAttribute, replaceAttribute, replaceValue): 
	elementNames = elementPath.split('/')
	targets = {}
	targets[0] = rootElement
	for i in range(1, len(elementNames)):
		newTargets = {}
		index = 0
		for j in range(0, len(targets)):
			children = targets[j].getElementsByTagName(elementNames[i])
			for k in range(0, len(children)):
				newTargets[index] = children[k]
				index += 1
		targets = newTargets

	filters={}
	filterAttributes = elementFilterAttribute.split(';')
	for i in range(0, len(filterAttributes)):
		if len(filterAttributes[i]) > 0:
			filterAttribute = filterAttributes[i].split('=')
			filter = {}
			filter["key"] = filterAttribute[0]
			filter["value"] = filterAttribute[1]
			filters[i] = filter

	for i in range(0, len(targets)):
		if check_element(targets[i], filters) == True:
			if len(replaceAttribute) > 0:
				targets[i].setAttribute(replaceAttribute, replaceValue)
			else:
				targets[i].firstChild.data = replaceValue

def replace_manifest(manifestFullPath, configData):
	dom = minidom.parse(manifestFullPath)
	manifestRootElement = dom.documentElement
	replace_xml(manifestRootElement, "manifest", "", "package", configData["pack_name"])
	replace_xml(manifestRootElement, "manifest/application/meta-data", "android:name=WX_PAY_SDK_TYPE", "android:value", configData["type"])
	replace_xml(manifestRootElement, "manifest/application/meta-data", "android:name=WX_PAY_CONFIG_SUFFIX", "android:value", configData["config_suffix"])
	replace_xml(manifestRootElement, "manifest/application/meta-data", "android:name=WX_PAY_SDK_PARAMS", "android:value", configData["sdk_params"])
	replace_xml(manifestRootElement, "manifest/application/activity-alias", "android:name=.wxapi.WXPayEntryActivity", "android:targetActivity", configData["wx_pay_entry_activity"])
	output=manifestRootElement.toprettyxml(indent='', newl='', encoding='UTF-8')
	file=open(manifestFullPath,'w+')
	try:
		file.write('<?xml version="1.0" encoding="utf-8" standalone="no"?>\n')
		file.write(output)
	finally:
		file.close()

def makePackageSmali(srcSmaliPath, srcPackageName, targetSmaliRootPath, targetPackageName):
	print "makePackageSmali:" + srcSmaliPath + "->" + targetSmaliRootPath
	srcPackageName = srcPackageName.replace(".", "/")
	targetPackageName = targetPackageName.replace(".", "/")
	targetPathArr = targetPackageName.split('/')
	targetPath = targetSmaliRootPath
	for i in range(0, len(targetPathArr)):
		if False == os.path.exists(targetPath):
			os.mkdir(targetPath)
		print "targetPath:" + targetPath
		print "targetPathArr[i]:"+targetPathArr[i]
		targetPath = os.path.join(targetPath, targetPathArr[i])
	if False == os.path.exists(targetPath):
		os.mkdir(targetPath)

	for item in os.listdir(srcSmaliPath):
		path = os.path.join(srcSmaliPath, item)
		file_object = open(path)
		try:
			all_the_text = file_object.read()
			all_the_text = all_the_text.replace(srcPackageName, targetPackageName)
			targetFilePath = os.path.join(targetPath, item)
			targetFile=open(targetFilePath,'w+')
			try:
				targetFile.write(all_the_text)
			finally:
				targetFile.close()
		finally:
			file_object.close()

def readExcelConfig(path):
	print "readExcelConfig:" + path
	xlrd.Book.encoding = "gbk"
	book = xlrd.open_workbook(path)
	sh = book.sheet_by_index(0)
	print sh.name, sh.nrows, sh.ncols
	
	data = {}
	for rx in range(1, sh.nrows):
		line = {}
		for ri in range(0, sh.ncols):
			k = sh.row(0)[ri].value
			#k = k.decode("utf8").encode(sys.getfilesystemencoding())
			v = sh.row(rx)[ri].value
			if isinstance(v, basestring) == True:
				v = v.encode(sys.getfilesystemencoding())
			line[k] = v
		key = line["id"]
		data[key] = line
		#print line
	return data

def replacePackageCmd(cmdPath):
	file_object = open(cmdPath)
	try:
		all_the_text = file_object.read()
		keystoreFullPath = "../pack_tool/" + data[sdkId]["keystore"]
		all_the_text = all_the_text.replace("%___keystore___%", keystoreFullPath)
		all_the_text = all_the_text.replace("%___storepass___%", data[sdkId]["storepass"])
		all_the_text = all_the_text.replace("%___keypass___%", data[sdkId]["keypass"])
		all_the_text = all_the_text.replace("%___id___%", sdkId)
		all_the_text = all_the_text.replace("%___alias___%", data[sdkId]["alias"])
	finally:
		file_object.close()
	
	file_object = open(cmdPath,'w+')
	try:
		file_object.write(all_the_text)
	finally:
		file_object.close()

# -------------- main --------------
if __name__ == '__main__':   
	platform = sys.platform
	projectDir = os.path.dirname(os.path.realpath(__file__))
	commandArgs = {}
	for i in range(1, len(sys.argv)):
		if sys.argv[i].find("-") == 0:
			commandArgs[sys.argv[i]] = sys.argv[i + 1]
	for key in commandArgs.keys():
		print("%s:%s" % (key, commandArgs[key]))
	
	typeKey = "-type"
	if commandArgs.has_key(typeKey) == False:
		for key in projectTemplates.keys():
			n = projectTemplates[key]["templateDir"].decode("utf8").encode(sys.getfilesystemencoding())
			print("%s:%s" % (key, n))
		commandArgs[typeKey] = raw_input("Please input your package type:\n")
	if projectTemplates.has_key(commandArgs[typeKey]) == False:
		for key in projectTemplates.keys():
			n = projectTemplates[key]["templateDir"].decode("utf8").encode(sys.getfilesystemencoding())
			print("%s:%s" % (key, n))
		print "failed! not find package type:" + commandArgs[typeKey]
		sys.exit(1)
	template = projectTemplates[commandArgs[typeKey]]
	template["templateDir"] = template["templateDir"].decode("utf8").encode(sys.getfilesystemencoding())
	print "templateDir:" + template["templateDir"]

	sdkIdKey = "-key"
	if commandArgs.has_key(sdkIdKey) == False:
		commandArgs[sdkIdKey] = raw_input("Please input your sdk key:\n")
	sdkId = commandArgs[sdkIdKey]
	print sdkId

	excelFullPath = os.path.join(projectDir, configExcelPath)
	data = readExcelConfig(configExcelPath)

	if data.has_key(sdkId) == False:
		print "failed! not find key config"
		sys.exit(1)

	targetProjectPath = os.path.join(projectDir, targetProjectRootPath)
	targetProjectPath = os.path.join(targetProjectPath, template["templateDir"] + sdkId)
	if True == os.path.exists(targetProjectPath):
		print "failed! target path is exits:" + targetProjectPath
		sys.exit(1)
	os.mkdir(targetProjectPath)
	projectTemplatePath = os.path.join(projectDir, "template/" + template["templateDir"])
	print "copy_files:" + projectTemplatePath + "->" + targetProjectPath
	copy_files(projectTemplatePath, targetProjectPath)

	androidProjectFullPath = os.path.join(targetProjectPath, template["androidProjectDir"])
	manifestFullPath = os.path.join(androidProjectFullPath, "AndroidManifest.xml")
	print "manifestFullPath:" + manifestFullPath
	replace_manifest(manifestFullPath, data[sdkId])
	
	if commandArgs[typeKey] == 'sdk':
		stringConfigFullPath = os.path.join(androidProjectFullPath, "res/values/strings.xml")
		print "stringConfigFullPath:" + stringConfigFullPath
		dom = minidom.parse(stringConfigFullPath)
		stringConfigRootElement = dom.documentElement
		replace_xml(stringConfigRootElement, "resources/string", "name=app_name", "", commandArgs[sdkIdKey])
		output=stringConfigRootElement.toprettyxml(indent='', newl='', encoding='UTF-8')
		file=open(stringConfigFullPath,'w+')
		try:
			file.write('<?xml version="1.0" encoding="utf-8" standalone="no"?>\n')
			file.write(output)
		finally:
			file.close()

	srcSmaliPath = os.path.join(projectDir, "template/package_smali")
	targetSmaliRootPath = os.path.join(androidProjectFullPath, "smali")
	srcPackageName = "com.vjove.vxooie"
	makePackageSmali(srcSmaliPath, srcPackageName, targetSmaliRootPath, data[sdkId]["pack_name"])

	cmdPath = os.path.join(targetProjectPath, "编译_new.bat".decode("utf8").encode(sys.getfilesystemencoding()))
	print "cmdPath:" + cmdPath
	replacePackageCmd(cmdPath)

	packCmdPath = os.path.join(targetProjectPath, "pack.bat".decode("utf8").encode(sys.getfilesystemencoding()))
	print "packCmdPath:" + packCmdPath
	replacePackageCmd(packCmdPath)
	
