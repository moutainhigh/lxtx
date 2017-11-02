package com.lxtx.util;

/**
 * 分页的参数，基于CFOP源码做了少量改动.
 * 
 */
public class PageParameter {

	public static final int DEFAULT_PAGE_SIZE = 10;
	private int start; // 查询起始记录数
	private int pagenum; // 查询起始页
	private int pagesize; // 每页记录数
	private int prepage; // 上页数
	private int nextpage; // 下页数
	private int totalpage; // 总页数
	private int totalrecords; // 总记录数
	private String sortdatafield;// 排序字段 modify by swh
	private String sortorder;// 排序方式

	public PageParameter() {
		// this.START = 0;
		// this.PAGENOW = 1;
		// this.LIMIT = DEFAULT_PAGE_SIZE;
	}

	/**
	 * 
	 * @param currentPage
	 * @param pageSize
	 */
	public PageParameter(int pagenum, int pagesize) {
		this.pagenum = pagenum;
		this.pagesize = pagesize;
		this.start = pagenum * pagesize;
	}

	/**
	 * @return the sortdatafield
	 */
	public String getSortdatafield() {
		return sortdatafield;
	}

	/**
	 * @param sortdatafield
	 *            the sortdatafield to set
	 */
	public void setSortdatafield(String sortdatafield) {
		this.sortdatafield = sortdatafield;
	}

	/**
	 * @return the sortorder
	 */
	public String getSortorder() {
		return sortorder;
	}

	/**
	 * @param sortorder
	 *            the sortorder to set
	 */
	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		if (pagenum > this.totalpage) {
			this.totalpage = pagenum;
		}
		this.pagenum = pagenum;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getPrepage() {
		return prepage;
	}

	public void setPrepage(int prepage) {
		this.prepage = prepage;
	}

	public int getNextpage() {
		return nextpage;
	}

	public void setNextpage(int nextpage) {
		this.nextpage = nextpage;
	}

	public int getTotalpage() {
		if (totalpage == 0) {
			return totalpage + 1;
		}
		return totalpage;
	}

	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}

	public int getTotalrecords() {
		return totalrecords;
	}

	public void setTotalrecords(int totalrecords) {
		this.totalrecords = totalrecords;
	}
}
