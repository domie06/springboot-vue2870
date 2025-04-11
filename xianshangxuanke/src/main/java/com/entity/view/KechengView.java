package com.entity.view;

import com.entity.KechengEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * 课程
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 */
@TableName("kecheng")
public class KechengView extends KechengEntity implements Serializable {
    private static final long serialVersionUID = 1L;

		/**
		* 课程类型的值
		*/
		private String kechengValue;
		/**
		* 是否上架的值
		*/
		private String shangxiaValue;



		//级联表 banzhuren
			/**
			* 班主任姓名
			*/
			private String banzhurenName;
			/**
			* 班主任手机号
			*/
			private String banzhurenPhone;
			/**
			* 班主任头像
			*/
			private String banzhurenPhoto;
			/**
			* 电子邮箱
			*/
			private String banzhurenEmail;

	public KechengView() {

	}

	public KechengView(KechengEntity kechengEntity) {
		try {
			BeanUtils.copyProperties(this, kechengEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



			/**
			* 获取： 课程类型的值
			*/
			public String getKechengValue() {
				return kechengValue;
			}
			/**
			* 设置： 课程类型的值
			*/
			public void setKechengValue(String kechengValue) {
				this.kechengValue = kechengValue;
			}
			/**
			* 获取： 是否上架的值
			*/
			public String getShangxiaValue() {
				return shangxiaValue;
			}
			/**
			* 设置： 是否上架的值
			*/
			public void setShangxiaValue(String shangxiaValue) {
				this.shangxiaValue = shangxiaValue;
			}




				//级联表的get和set banzhuren

					/**
					* 获取： 班主任姓名
					*/
					public String getBanzhurenName() {
						return banzhurenName;
					}
					/**
					* 设置： 班主任姓名
					*/
					public void setBanzhurenName(String banzhurenName) {
						this.banzhurenName = banzhurenName;
					}

					/**
					* 获取： 班主任手机号
					*/
					public String getBanzhurenPhone() {
						return banzhurenPhone;
					}
					/**
					* 设置： 班主任手机号
					*/
					public void setBanzhurenPhone(String banzhurenPhone) {
						this.banzhurenPhone = banzhurenPhone;
					}

					/**
					* 获取： 班主任头像
					*/
					public String getBanzhurenPhoto() {
						return banzhurenPhoto;
					}
					/**
					* 设置： 班主任头像
					*/
					public void setBanzhurenPhoto(String banzhurenPhoto) {
						this.banzhurenPhoto = banzhurenPhoto;
					}

					/**
					* 获取： 电子邮箱
					*/
					public String getBanzhurenEmail() {
						return banzhurenEmail;
					}
					/**
					* 设置： 电子邮箱
					*/
					public void setBanzhurenEmail(String banzhurenEmail) {
						this.banzhurenEmail = banzhurenEmail;
					}
















}
