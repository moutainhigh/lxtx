from mysqldb import mysqldb
import time

class NovelSaver(object):
    """docstring for DBUpdateExecutor"""
    def __init__(self, db_conf, book_table_name, chapter_table_name):
        super(NovelSaver, self).__init__()
        self.db_conf = db_conf
        self.conn = mysqldb(self.db_conf['host'], self.db_conf['username'], self.db_conf['password'], self.db_conf['dbname'], 'utf8')
        self.book_table_name = book_table_name
        self.chapter_table_name = chapter_table_name

    def quoteString(self, str):     
        return '\'' + str + '\''

    def maxBookId(self):
        return self.maxId(self.book_table_name)

    def maxId(self, tableName):
        result = self.conn._maxId(tableName)
        return result[0][0]

    '''
    truncate book tables
    '''
    def truncateBookTables(self):
        self.conn._truncateTable(self.book_table_name)
        self.conn._truncateTable(self.chapter_table_name)

    def saveBook(self, book_info, idx):
        '''
        TODO
        '''
        attrs = ['cid', 'uid', 'model', 'title', 'create_time', 'update_time', 'sort', 'status', 'trash', 'image', 'tstype', 'zuozhe', 
        'zishu', '`desc`', 'origin_id']
        t = int(time.time())
        value = [self.quoteString(str(book_info['category'])), '1', '100', self.quoteString(book_info['title']), str(t), str(t), '100', '1', '0', str(idx), '0', self.quoteString(book_info['author']),
        self.quoteString(str(book_info['word_count'])), self.quoteString(book_info['description']), str(book_info['bookid'])]
        self.conn._insert(self.book_table_name,attrs,value)

    def saveChapter(self, chapter_info, content, book_id, idx, category_id):
        attrs = ['cid', 'uid', 'model', 'title', 'create_time', 'update_time', 'sort', 'status', 'trash', 'content', 'isvip', 'bid', 'idx', 'origin_id']
        t = int(time.time())
        is_vip = '1'
        if idx < 20:
            is_vip = '0'

        value = [str(category_id), '1', '100', self.quoteString(chapter_info['chaptername']), str(t), str(t), '100', '1', '0', self.quoteString(content),
         self.quoteString(is_vip), str(book_id), str(idx), str(chapter_info['chapterid'])]
        self.conn._insert(self.chapter_table_name, attrs, value)

    '''
    save the attachment and return the unique id
    '''    
    def saveImage(self, name, path, size, md5val, shaval):
        table = 'ien_admin_attachment'
        attrs = ['uid', 'name', 'module', 'path', 'mime', 'ext', 'size', 'md5', 'sha1', 'driver', 
        'download', 'create_time', 'update_time', 'sort', 'status']
        t = int(time.time())
        value = ['1', name, 'cms', path, 'image/jpeg', 'jpg', str(size), md5val,
        shaval, 'local', '0', str(t), str(t), '100', '1']

        values = []
        for v in value:
            values.append(self.quoteString(v))

        self.conn._insert(table, attrs, values)
        return self.maxId(table)

if __name__ == '__main__':
    db_conf = {}
    db_conf['host'] = '47.92.129.198'
    db_conf['username'] = 'xiaoshuo'
    db_conf['password'] = 'mkKrpZTBLR'
    db_conf['dbname'] = 'xiaoshuo' 
    saver = NovelSaver(db_conf, 'ien_book_copy', 'ien_chapter_copy')
    #print(saver.maxBookId())
    # book_info = {}
    # book_info['category'] = 1
    # book_info['title'] = 'test1'
    # book_info['create_time'] = 1232323
    # book_info['update_time'] = 123234
    # book_info['image'] = 1
    # book_info['tstype'] = '1'
    # book_info['zuozhe'] = 'test1'
    # book_info['zishu'] = 1929219
    # book_info['description'] = 'test1'

    # idx = 1
    # saver.saveBook(book_info, idx)
    id = saver.saveImage('1.jpg', 'uploads/images/20170517/d8b5b26362b899461d8adcf9da3ab7c6.jpg', 110100, '64c25eb3abb0b81f47648ca5da14199f',
    '64c25eb3abb0b81f47648ca5da14199f')
    print("generated id is:%d" % id)