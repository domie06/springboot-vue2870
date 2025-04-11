package com.entity.model;

import com.entity.KechengOrderEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;


/**
 * 课程选课
 * 接收传参的实体类
 *（实际开发中配合移动端接口开发手动去掉些没用的字段， 后端一般用entity就够用了）
 * 取自ModelAndView 的model名称
 */
public class KechengOrderModel implements Serializable {
    private static final long serialVersionUID = 1L;




    /**
     * 主键
     */
    private Integer id;


    /**
     * 选课唯一编号
     */
    private String kechengOrderUuidNumber;


    /**
     * 课程
     */
    private Integer kechengId;


    /**
     * 学生
     */
    private Integer xueshengId;


    /**
     * 选课时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
    private Date insertTime;


    /**
     * 课程成绩
     */
    private Double kechengOrderNumber;


    /**
     * 创建时间 show3
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
    private Date createTime;


    /**
	 * 获取：主键
	 */
    public Integer getId() {
        return id;
    }


    /**
	 * 设置：主键
	 */
    public void setId(Integer id) {
        this.id = id;
    }
    /**
	 * 获取：选课唯一编号
	 */
    public String getKechengOrderUuidNumber() {
        return kechengOrderUuidNumber;
    }


    /**
	 * 设置：选课唯一编号
	 */
    public void setKechengOrderUuidNumber(String kechengOrderUuidNumber) {
        this.kechengOrderUuidNumber = kechengOrderUuidNumber;
    }
    /**
	 * 获取：课程
	 */
    public Integer getKechengId() {
        return kechengId;
    }


    /**
	 * 设置：课程
	 */
    public void setKechengId(Integer kechengId) {
        this.kechengId = kechengId;
    }
    /**
	 * 获取：学生
	 */
    public Integer getXueshengId() {
        return xueshengId;
    }


    /**
	 * 设置：学生
	 */
    public void setXueshengId(Integer xueshengId) {
        this.xueshengId = xueshengId;
    }
    /**
	 * 获取：选课时间
	 */
    public Date getInsertTime() {
        return insertTime;
    }


    /**
	 * 设置：选课时间
	 */
    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    /**
	 * 获取：课程成绩
	 */
    public Double getKechengOrderNumber() {
        return kechengOrderNumber;
    }


    /**
	 * 设置：课程成绩
	 */
    public void setKechengOrderNumber(Double kechengOrderNumber) {
        this.kechengOrderNumber = kechengOrderNumber;
    }
    /**
	 * 获取：创建时间 show3
	 */
    public Date getCreateTime() {
        return createTime;
    }


    /**
	 * 设置：创建时间 show3
	 */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    }
