import datetime

class TimeUtils(object):
	"""docstring for ClassName"""
	def __init__(self, arg):
		super(ClassName, self).__init__()
		self.arg = arg

	@staticmethod
	def getDay():
		now = datetime.datetime.now()
		return now.strftime("%Y%m%d")

if __name__ == '__main__':
	print TimeUtils.getDay()
