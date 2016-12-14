/**
 * Copyright (c) 2015-2016, Michael Yang(fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import io.jpress.model.Metadata;
import io.jpress.model.core.JModel;
import io.jpress.model.query.MetaDataQuery;

import java.math.BigInteger;

/**
 *  Auto generated by JPress, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUser<M extends BaseUser<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "user";
	public static final String METADATA_TYPE = "user";

	public void removeCache(Object key){
		if(key == null) return;
		CacheKit.remove(CACHE_NAME, key);
	}

	public void putCache(Object key,Object value){
		CacheKit.put(CACHE_NAME, key, value);
	}

	public M getCache(Object key){
		return CacheKit.get(CACHE_NAME, key);
	}

	public M getCache(Object key,IDataLoader dataloader){
		return CacheKit.get(CACHE_NAME, key, dataloader);
	}

	public Metadata createMetadata(){
		Metadata md = new Metadata();
		md.setObjectId(getId());
		md.setObjectType(METADATA_TYPE);
		return md;
	}

	public Metadata createMetadata(String key,String value){
		Metadata md = new Metadata();
		md.setObjectId(getId());
		md.setObjectType(METADATA_TYPE);
		md.setMetaKey(key);
		md.setMetaValue(value);
		return md;
	}

	public boolean saveOrUpdateMetadta(String key,String value){
		Metadata metadata = MetaDataQuery.me().findByTypeAndIdAndKey(METADATA_TYPE, getId(), key);
		if (metadata == null) {
			metadata = createMetadata(key, value);
			return metadata.save();
		}
		metadata.setMetaValue(value);
		return metadata.update();
	}

	public String metadata(String key) {
		Metadata m = MetaDataQuery.me().findByTypeAndIdAndKey(METADATA_TYPE, getId(), key);
		if (m != null) {
			return m.getMetaValue();
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null){ return false; }
		if(!(o instanceof BaseUser<?>)){return false;}

		BaseUser<?> m = (BaseUser<?>) o;
		if(m.getId() == null){return false;}

		return m.getId().compareTo(this.getId()) == 0;
	}

	public void setId(BigInteger id) {
		set("id", id);
	}

	public BigInteger getId() {
		Object id = get("id");
		if (id == null)
			return null;

		return id instanceof BigInteger ? (BigInteger)id : new BigInteger(id.toString());
	}

	public void setUsername(String username) {
		set("username", username);
	}

	public String getUsername() {
		return get("username");
	}

	public void setNickname(String nickname) {
		set("nickname", nickname);
	}

	public String getNickname() {
		return get("nickname");
	}

	public void setRealname(String realname) {
		set("realname", realname);
	}

	public String getRealname() {
		return get("realname");
	}

	public void setPassword(String password) {
		set("password", password);
	}

	public String getPassword() {
		return get("password");
	}

	public void setSalt(String salt) {
		set("salt", salt);
	}

	public String getSalt() {
		return get("salt");
	}

	public void setEmail(String email) {
		set("email", email);
	}

	public String getEmail() {
		return get("email");
	}

	public void setEmailStatus(String emailStatus) {
		set("email_status", emailStatus);
	}

	public String getEmailStatus() {
		return get("email_status");
	}

	public void setMobile(String mobile) {
		set("mobile", mobile);
	}

	public String getMobile() {
		return get("mobile");
	}

	public void setMobileStatus(String mobileStatus) {
		set("mobile_status", mobileStatus);
	}

	public String getMobileStatus() {
		return get("mobile_status");
	}

	public void setTelephone(String telephone) {
		set("telephone", telephone);
	}

	public String getTelephone() {
		return get("telephone");
	}

	public void setAmount(java.math.BigDecimal amount) {
		set("amount", amount);
	}

	public java.math.BigDecimal getAmount() {
		return get("amount");
	}

	public void setContentCount(Long contentCount) {
		set("content_count", contentCount);
	}

	public Long getContentCount() {
		return get("content_count");
	}

	public void setCommentCount(Long commentCount) {
		set("comment_count", commentCount);
	}

	public Long getCommentCount() {
		return get("comment_count");
	}

	public void setQq(String qq) {
		set("qq", qq);
	}

	public String getQq() {
		return get("qq");
	}

	public void setWechat(String wechat) {
		set("wechat", wechat);
	}

	public String getWechat() {
		return get("wechat");
	}

	public void setWeibo(String weibo) {
		set("weibo", weibo);
	}

	public String getWeibo() {
		return get("weibo");
	}

	public void setSignature(String signature) {
		set("signature", signature);
	}

	public String getSignature() {
		return get("signature");
	}

	public void setAvatar(String avatar) {
		set("avatar", avatar);
	}

	public String getAvatar() {
		return get("avatar");
	}

	public void setSchool(String school) {
		set("school", school);
	}

	public String getSchool() {
		return get("school");
	}

	public void setGrade(String grade) {
		set("grade", grade);
	}

	public String getGrade() {
		return get("grade");
	}

	public void setClasses(String classes) {
		set("classes", classes);
	}

	public String getClasses() {
		return get("classes");
	}

	public void setGender(String gender) {
		set("gender", gender);
	}

	public String getGender() {
		return get("gender");
	}

	public void setBirthday(java.util.Date birthday) {
		set("birthday", birthday);
	}

	public java.util.Date getBirthday() {
		return get("birthday");
	}

	public void setAddress(String address) {
		set("address", address);
	}

	public String getAddress() {
		return get("address");
	}

	public void setTeacherId(BigInteger teacherId) {
		set("teacher_id", teacherId);
	}

	public BigInteger getTeacherId() {
		return get("teacher_id");
	}

	public void setGroupId(BigInteger groupId) {
		set("group_id", groupId);
	}

	public BigInteger getGroupId() {
		return get("group_id");
	}

	public void setRole(String role) {
		set("role", role);
	}

	public String getRole() {
		return get("role");
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public String getStatus() {
		return get("status");
	}

	public void setCreated(java.util.Date created) {
		set("created", created);
	}

	public java.util.Date getCreated() {
		return get("created");
	}

	public void setCreateSource(String createSource) {
		set("create_source", createSource);
	}

	public String getCreateSource() {
		return get("create_source");
	}

	public void setLogged(java.util.Date logged) {
		set("logged", logged);
	}

	public java.util.Date getLogged() {
		return get("logged");
	}

	public void setActivated(java.util.Date activated) {
		set("activated", activated);
	}

	public java.util.Date getActivated() {
		return get("activated");
	}

}
