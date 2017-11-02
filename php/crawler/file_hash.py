import hashlib
import os
 
class FileHash(object):
    """docstring for ClassName"""
    def __init__(self, arg):
        super(ClassName, self).__init__()
        self.arg = arg

    @staticmethod
    def md5val(source):
        m2 = hashlib.md5()   
        m2.update(source)   
        return m2.hexdigest() 

    @staticmethod
    def GetHashGigest(method, filename):
        if not os.path.isfile(filename):
            return None

        if method == 'md5':
            myhash = hashlib.md5()
        else:
            myhash = hashlib.sha1()

        f = file(filename,'rb')
        while True:
            b = f.read(8096)
            if not b:
                break
            myhash.update(b)
        f.close()
        return myhash.hexdigest()

    @staticmethod
    def GetFileMd5(filename):
        return FileHash.GetHashGigest("md5", filename)

    @staticmethod
    def GetFileSha1(filename):
        return FileHash.GetHashGigest("sha1", filename)

if __name__ == '__main__':
    #print FileHash.GetFileSha1('D:\\other\\php\\xiaoshuo\\crawler\\d8b5b26362b899461d8adcf9da3ab7c5.jpg')
    print FileHash.md5val('hello123')