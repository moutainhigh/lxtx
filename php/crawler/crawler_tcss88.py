import os
import sys
import json
import urllib
import urllib2
import threading
import thread
import time
from novel_saver import NovelSaver
from file_utils import FileUtils
from time_utils import TimeUtils
from file_hash import FileHash

class BasicCrawler(object):
    """docstring for ClassName"""
    def __init__(self, host, apikey, spid):
        super(BasicCrawler, self).__init__()
        self.host = host
        self.apikey = apikey
        self.spid = spid
        #be careful with the table names
        self.dbSaver = NovelSaver(BasicCrawler.getDefaultDBConf(), 'ien_book', 'ien_chapter')
        self.lock = thread.allocate_lock()
        #create the book image directory if it doesn't exist
        cwd = os.getcwd()
        self.img_dir = cwd + os.sep + "images" + os.sep + TimeUtils.getDay()
        self.server_img_dir = 'uploads/images/' + TimeUtils.getDay()
        FileUtils.mkdir_p(self.img_dir)

    @staticmethod
    def decode_str(source):
        return source.decode('unicode-escape').encode('utf-8')

    @staticmethod
    def getDefaultDBConf():
        db_conf = {}
        db_conf['host'] = '47.92.129.198'
        db_conf['username'] = 'xiaoshuo'
        db_conf['password'] = 'mkKrpZTBLR'
        db_conf['dbname'] = 'xiaoshuo'      
        return db_conf

    '''
    save the cover image into mysql and return the generated id
    firstly download the image to the folder relative to current script
    secondly insert a record into mysql and returns the generated id
    '''
    def download_book_image(self, url):
        url = 'http://www.tcss88.com/imagedata/2017/09/25/59c8c5a10d5b0015591035.jpg'
        if url:
            #generate the new file name
            t = str(time.time())
            file_name = FileHash.md5val(t) + ".jpg"
            print("generate image file:%s" % file_name)
            file_path = self.img_dir + os.sep + file_name
            print("file path is : %s" % file_path)
            urllib.urlretrieve(url, file_path)
            #now save it to db
            size = FileUtils.getFileSize(file_path)
            md5v = FileHash.GetFileMd5(file_path)
            sha1v = FileHash.GetFileSha1(file_path)

            server_file_path = self.server_img_dir + os.sep + file_name 
            return self.dbSaver.saveImage(file_name, server_file_path, size, md5v, sha1v)
        else:
            return 0

    #save newly added chapters into database        
    def grab_chapters(self, chapter_list, book_pri_id, category_id, start_idx):
        idx = start_idx
        for chap in chapter_list:
            content = self.get_chapter_content(book_id, chap['chapterid'])
            self.dbSaver.saveChapter(chap, content, book_pri_id, idx, category_id)                
            idx = idx + 1

    def grab_single_book(self, book_id):
        book = self.get_book_info(book_id)
        if book:
            #save book image 
            print("book coverurl is %s" % book['cover_url'])
            img_id = self.download_book_image(book['cover_url'])
            self.prn_dict(book)       
            self.dbSaver.saveBook(book, int(img_id))
            book_primary_id = self.dbSaver.maxBookId()
            print("book primary id is%d" % book_primary_id)
            #save book chapters
            self.load_and_save_chapter(book_id, book_primary_id, book['category'])

    #check the new books and save the chapters        
    def update_books(self):
        '''
        todo
        '''

    '''
    the main entry point
    '''
    def main_logic(self):
        #truncate all book tables
        self.dbSaver.truncateBookTables()
        #firstly, get the book list 
        #http://www.tcss88.com/?s=JsonApi&a=index&api=get.spbooks&apikey=xiangjiaduijei&spid=67
        books = self.get_book_list()
        print("book count %d" % len(books))
        if books:
            idx = 1
            book_map = {}
            #secondly, iterate over the books and get all chapters
            for book in books:
                book_id = book['bookid']
                book = self.get_book_info(book_id)

                if book:
                    #save book image 
                    print("book coverurl is %s" % book['cover_url'])
                    img_id = self.download_book_image(book['cover_url'])
                    self.prn_dict(book)       
                    self.dbSaver.saveBook(book, int(img_id))
                    idx = idx + 1
                    book_primary_id = self.dbSaver.maxBookId()
                    print("book primary id is%d" % book_primary_id)
                    book_map[book_id] = {"primary_key": book_primary_id, "category_id" : book['category']}

            print("book length: %d" % len(book_map.keys())) 
            threads = []
            for key in book_map.keys():
                book_info_map = book_map[key]
                #self.load_and_save_chapter(key, book_info_map['primary_key'], book_info_map['category_id'])
                print("now start a thread for loading chatpers %d %d" % (book_info_map['primary_key'], book_info_map['category_id']))
                threadx = threading.Thread(target=self.load_and_save_chapter,args=(key, book_info_map['primary_key'], book_info_map['category_id']))
                threads.append(threadx)
                threadx.setDaemon(True)
                threadx.start()

            for t in threads:
                t.join()
            print("main thread exit")

    def load_and_save_chapter(self, book_id, book_pri_id, category_id):
        #save chapters
        chaps = self.get_chapters(book_id, book_pri_id, category_id)
        if chaps:
            print("there are %d chapters in book %s" % (len(chaps), book_id))

    def get_book_list(self):
        url = 'http://%s/?s=JsonApi&a=index&api=get.spbooks&apikey=%s&spid=%s' % (self.host, self.apikey, self.spid)
        req = urllib2.urlopen(url)
        page = req.read()
        dicts = json.loads(page)
        if dicts['code'] == 200:
            return dicts['data']
        else:
            return None

    def prn_dict(self, obj):
        for k in obj.keys():
            if k == 'category':
                print("category:%s title:%s bookid:%s" % (obj['category'], obj['title'], obj['bookid']))

    def get_book_info(self, book_id):
        url = 'http://%s/?s=JsonApi&a=index&api=get.book.info&apikey=%s&spid=%s&bookid=%s' % (self.host, self.apikey, self.spid, book_id)
        req = urllib2.urlopen(url)
        page = req.read()
        print('now get content for book %s ' % (book_id))
        if page:
            #print(page)
            try:
                dicts = json.loads(page)
                if dicts['code'] == 200:
                    return dicts['data']
            except:
                return None

    def get_chapters(self, book_id, book_pri_id, category_id):
        print("get chapter list for book %s" % (book_id))
        url = 'http://%s/?s=JsonApi&a=index&api=get.book.chapter&apikey=%s&spid=%s&bookid=%s' % (self.host, self.apikey, self.spid, book_id)
        print("url is %s" % (url))
        req = urllib2.urlopen(url)
        page = req.read()
        if page:
            try:
                response = json.loads(page)
                #print("response is %s" % (page))
                if response['code'] == 200:
                    volumes = response['data']
                    print("response code is 200 volume count is : %d " % (len(volumes)))
                    chapters = volumes[0]['chapters']
                    print("now get chapter content for book %s chapter count %d" % (book_id, len(chapters)))        

                    if len(volumes) > 1:
                        for vol in volumes[1:]:
                            chapters.extend(vol['chapters'])

                    chapter_idx = 1
                    for chap in chapters:
                        content = self.get_chapter_content(book_id, chap['chapterid'])
                        #save chapter data
                        self.lock.acquire()
                        self.dbSaver.saveChapter(chap, content, book_pri_id, chapter_idx, category_id)
                        self.lock.release()
                       
                        chapter_idx = chapter_idx + 1
                        print("chapter idx %d for book %s" % (chapter_idx, book_id))
                        
                    return chapters
                else:
                    print("empty response for loading chapters.")
            except:
                return None

    def get_chapter_content(self, book_id, chapter_id):
        url = 'http://%s/?s=JsonApi&a=index&api=get.book.content&apikey=%s&spid=%s&bookid=%s&chapterid=%s' % (self.host, self.apikey, self.spid, book_id, chapter_id)
        req = urllib2.urlopen(url)
        page = req.read()
        if page:
            try:
                response = json.loads(page)
                #print("response status %d" % (response['code']))
                if response['code'] == 200:
                    #content =  decode_str(response['data']['chaptercontent'])
                    content =  response['data']['content']
                    #print(" content for book %s chapter %s is: %s" % (book_id, chapter_id, content))
                    return content
                else:
                    return None
            except:
                return None

if __name__ == '__main__':
    crawler = BasicCrawler("www.tcss88.com", "xiangjiaduijei", 67)
    #crawler.main_logic()
    crawler.grab_single_book(52)