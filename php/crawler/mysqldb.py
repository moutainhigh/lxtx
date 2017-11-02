# -*- coding:utf-8 -*-
import MySQLdb
import datetime
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

#封装MySQLdb类基本操作mysqldb
class mysqldb:
    conn = ''
    cursor = ''
    def __init__(self,host1='localhost',user1='root',passwd1='www',database1='test',charset1='utf8'):
        try:
            print 'init_ ok'
            self.conn=MySQLdb.connect(host=host1,user=user1,passwd=passwd1,db=database1,port=3306,charset=charset1);
            self.cursor = self.conn.cursor(MySQLdb.cursors.DictCursor)
        except MySQLdb.Error,e:
            error = 'Connect failed! ERROR (%s): %s' %(e.args[0],e.args[1])
            print error
            sys.exit()

    def _escapeString(self, raw_str):
        return self.conn.escape_string(raw_str)

    def _addIndex(self, table_name, col_name, idx_name):
        sql = "SELECT * FROM information_schema.STATISTICS WHERE table_schema = DATABASE()  AND table_name = '" + table_name +"' AND index_name = '" + idx_name + "'"
        #alter table rooms add index `id1` (`id1`);
        print sql 
        result = self._execute(sql) 
        if not result:
            print "add index now."
            sql = "alter table `" + table_name + "` add index `" + idx_name + "` (" + col_name + ")"
            self._executeCommit(sql)
        else:
            print "index for column " + col_name + " in table " + table_name + " already exists."       

    def _addColumn(self, table_name, col_name, desc):
        #firstly, check whether that column already exists
        sql = "SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = '" + table_name +"' AND column_name = '" + col_name + "'"
        if not self._execute(sql):
            sql = "alter table " + table_name + " add column `" + col_name + "` " + desc
            self._executeCommit(sql)
        else:
            print "column " + col_name + " already exists in table " + table_name

    def _modifyColumn(self, table_name, col_name, desc):
        #firstly, check whether that column already exists
        sql = "SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = '" + table_name +"' AND column_name = '" + col_name + "'"
        if not self._execute(sql):
            print "column " + col_name + " doesn't exist in table " + table_name
        else:
            #alter table rooms alter column `states` varchar(2000) NOT NULL;
            sql = "alter table `" + table_name + "` modify column `" + col_name + "` " + desc
            self._executeCommit(sql)

    #针对读操作返回结果集
    def _execute(self,sql=''):
        try:
            self.cursor.execute(sql)
            records = self.cursor.fetchall()
            return records
        except MySQLdb.Error,e:
            error = 'MySQL execute failed! ERROR (%s): %s' %(e.args[0],e.args[1])
            print error
            #sys.exit()

    #针对更新,删除,事务等操作失败时回滚
    def _executeCommit(self,sql=''):
        try:
            self.cursor = self.conn.cursor()
            self.cursor.execute(sql)
            self.conn.commit()
        except MySQLdb.Error,e:
            self.conn.rollback()
            error = 'MySQL execute failed! ERROR (%s): %s' %(e.args[0],e.args[1])
            print error
            #sys.exit()

    #创建表        
    #tablename:表名称,attr_dict:属性键值对,constraint:主外键约束
    #attr_dict:{'book_name':'varchar(200) NOT NULL'...}
    #constraint:PRIMARY KEY(`id`)
    def _createTable(self,table,attr_dict,constraint):
        sql = ''
        sql_mid = '`id` bigint(11) NOT NULL AUTO_INCREMENT,'
        for attr,value in attr_dict.items():
            sql_mid = sql_mid + '`'+attr + '`'+' '+ value+','
        sql = sql + 'CREATE TABLE IF NOT EXISTS %s ('%table
        sql = sql + sql_mid
        sql = sql + constraint
        sql = sql + ') ENGINE=InnoDB DEFAULT CHARSET=utf8'
        print '_createTable:'+sql
        self._executeCommit(sql)

    def _truncateTable(self, table):
        sql = "TRUNCATE TABLE `" + table + "`"
        self._executeCommit(sql)

    #查询表内容
    #cond_dict:{'name':'xiaoming'...}
    #order:'order by id desc'
    def _select(self,table,cond_dict='',order=''):
        consql = ' '
        if cond_dict!='':
            for k,v in cond_dict.items():
                consql = consql+k+'='+v+' and'
        
        consql = consql + ' 1=1 '
        sql = 'select * from %s where '%table
        sql = sql + consql + order
        print '_select:'+sql
        return self._execute(sql)

    #插入单条数据
    def _insert(self,table,attrs,value):
        #values_sql = ['%s' for v in attrs]
        attrs_sql = '('+','.join(attrs)+')'
        values_sql = ' values('+','.join(value)+')'
        sql = 'insert into %s'%table
        sql = sql + attrs_sql + values_sql
        #print '_insert:'+sql
        self._executeCommit(sql)

    def _maxId(self, table):
        sql = "select max(id) as max from %s"%table
        return self._execute(sql)

    #插入多条数据
    #attrs:[id,name,...]
    #values:[[1,'jack'],[2,'rose']]
    def _insertMany(self,table,attrs,values):
        values_sql = ['%s' for v in attrs]
        attrs_sql = '('+','.join(attrs)+')'
        values_sql = ' values('+','.join(values_sql)+')'
        sql = 'insert into %s'%table
        sql = sql + attrs_sql + values_sql
        print '_insertMany:'+sql
        try:
            print sql
            #print values
            for i in range(0,len(values),20000):
                self.cursor.executemany(sql,values[i:i+20000])
                self.conn.commit()
        except MySQLdb.Error,e:
            self.conn.rollback()
            error = '_insertMany executemany failed! ERROR (%s): %s' %(e.args[0],e.args[1])
            print error
            sys.exit()

    def _now(self):
        now0 = datetime.datetime.now()
        now = now0.strftime('%Y-%m-%d %H:%M:%S')
        print now
    
    def _close(self):
        self.cursor.close()
        self.conn.close()

    def _del_(self):
        self._close()

if __name__ == '__main__':
    a = mysqldb('47.92.129.198','xiaoshuo','mkKrpZTBLR','xiaoshuo','utf8')
    print a._escapeString("hello'123")
