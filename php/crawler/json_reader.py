from file_utils import FileUtils
import json

class JsonReader(object):
	"""docstring for ClassName"""
	def __init__(self, file):
		super(JsonReader, self).__init__()
		self.file = file

	def getContent(self):
		return FileUtils.getFileContent(self.file)

	def parse(self):
		source = self.getContent()
		print source
		arr = json.loads(source)
		print len(arr)

	def test(self):
		source = '[{"domain":".facebook.com","expirationDate":1507651777,"hostOnly":false,"httpOnly":true,"name":"fr","path":"/","secure":false,"session":true,"storeId":"0","value":"0qs0mCiDzs33oRmyR.AWWWWquKnCq2G2I0gPGBymcSKwU.BZgOIL.ou.AAA.0.0.BZgOId.AWWRIHRG","id":0},{"domain":".facebook.com","expirationDate":1507651777,"hostOnly":false,"httpOnly":true,"name":"xs","path":"/","secure":false,"session":true,"storeId":"0","value":"45%3Am9G0n5KzZ4R6gw%3A2%3A1501618717%3A16645%3A13639","id":0},{"domain":".facebook.com","expirationDate":1507651777,"hostOnly":false,"httpOnly":true,"name":"c_user","path":"/","secure":false,"session":true,"storeId":"0","value":"100001822371291","id":0},{"domain":".facebook.com","expirationDate":1507651777,"hostOnly":false,"httpOnly":true,"name":"datr","path":"/","secure":false,"session":true,"storeId":"0","value":"EOKAWZZEaVYnvuammQ1WmMXg","id":0},{"domain":".facebook.com","expirationDate":1507651777,"hostOnly":false,"httpOnly":true,"name":"sb","path":"/","secure":false,"session":true,"storeId":"0","value":"HeKAWeEJXY9zeTH-hTVvR2xR","id":0},{"domain":".facebook.com","expirationDate":1507651777,"hostOnly":false,"httpOnly":true,"name":"pl","path":"/","secure":false,"session":true,"storeId":"0","value":"n","id":0},{"domain":".facebook.com","expirationDate":1507651777,"hostOnly":false,"httpOnly":true,"name":"lu","path":"/","secure":false,"session":true,"storeId":"0","value":"gA","id":0}]'
		arr = json.loads(source)
		print len(arr)

if __name__ == '__main__':
	reader = JsonReader("D:\\daily\\0930\\annie.de.txt")
	reader.parse()
