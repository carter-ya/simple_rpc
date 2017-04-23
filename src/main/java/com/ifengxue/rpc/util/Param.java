package com.ifengxue.rpc.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * 用于辅助参数验证的实体
 * 
 * @author LiuKeFeng
 * @date 2016年12月14日
 */
public class Param implements Serializable {
	private static final long serialVersionUID = 3631057822031065438L;
	/** 参数的key */
	private String key;
	/** 参数的默认值 */
	private String value;
	/** 参数是否必须 */
	private boolean required;
	/** 参数的注释 */
	private String comment;

	/**
	 * 指定该参数必须传入
	 * 
	 * @param key
	 */
	public Param(String key) {
		this(key, null, true);
	}

	/**
	 * @param key
	 * @param value
	 *            如果required为false则必须有默认值
	 * @param required
	 *            true:必须传入;false:可选传入
	 */
	public Param(String key, String value, boolean required) {
		this(key, value, required, null);
	}

	/**
	 * @param key
	 *            如果required为false则必须有默认值
	 * @param value
	 * @param required
	 *            true:必须传入;false:可选传入
	 * @param comment
	 *            该参数的提示信息
	 */
	@SuppressWarnings("deprecation")
	public Param(String key, String value, boolean required, String comment) {
		super();
		if (!required) {
			Objects.requireNonNull(value);
		}
		this.key = key;
		this.value = value;
		this.required = required;
		this.comment = comment;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "Param [key=" + key + ", value=" + value + ", required=" + required + ", comment=" + comment + "]";
	}
}
