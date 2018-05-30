package com.jinanlongen.manatee.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.stereotype.Service;

/**
 * 类说明
 * 
 * @author shangyao
 * @date 2017年11月24日
 */
@Entity
@Service
public class Shop {
	@Id
	private String shopId;
	private String name;
	private String appKey;
	private String appSecret;
	private String accessToken;
	private String platform;// sn,tb,jd

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

}
