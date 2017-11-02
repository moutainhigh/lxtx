package com.lxtech.model;

import java.io.Serializable;
import java.util.Date;

public class CloudTargetIndexChange implements Serializable {
    /**
     * cloud_target_index_change.id
     * @ibatorgenerated 2016-10-28 18:18:26
     */
    private Integer id;

    /**
     * cloud_target_index_change.subject_id
     * @ibatorgenerated 2016-10-28 18:18:26
     */
    private Integer subjectId;
    
    private String subject;

    public String getSubject() {
      return subject;
    }

    public void setSubject(String subject) {
      this.subject = subject;
    }

    /**
     * cloud_target_index_change.time
     * @ibatorgenerated 2016-10-28 18:18:26
     */
    private Date time;

    /**
     * cloud_target_index_change.index
     * @ibatorgenerated 2016-10-28 18:18:26
     */
    private Integer index;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}