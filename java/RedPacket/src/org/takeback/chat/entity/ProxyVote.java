// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import java.util.Date;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "pub_proxyVote", schema = "gamechat", catalog = "")
public class ProxyVote
{
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column
    Integer uid;
    @Column
    Integer parentId;
    @Column
    Double lose;
    @Column
    Double vote;
    @Column
    String nickName;
    @Column
    String parentNickName;
    @Column
    Date cacuDate;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getUid() {
        return this.uid;
    }
    
    public void setUid(final Integer uid) {
        this.uid = uid;
    }
    
    public Double getLose() {
        return this.lose;
    }
    
    public void setLose(final Double lose) {
        this.lose = lose;
    }
    
    public Double getVote() {
        return this.vote;
    }
    
    public void setVote(final Double vote) {
        this.vote = vote;
    }
    
    public String getNickName() {
        return this.nickName;
    }
    
    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }
    
    public Date getCacuDate() {
        return this.cacuDate;
    }
    
    public void setCacuDate(final Date cacuDate) {
        this.cacuDate = cacuDate;
    }
    
    public Integer getParentId() {
        return this.parentId;
    }
    
    public void setParentId(final Integer parentId) {
        this.parentId = parentId;
    }
    
    public String getParentNickName() {
        return this.parentNickName;
    }
    
    public void setParentNickName(final String parentNickName) {
        this.parentNickName = parentNickName;
    }
}
