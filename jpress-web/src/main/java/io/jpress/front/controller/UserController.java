/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.front.controller;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;

import com.jfinal.log.Log;
import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.message.Actions;
import io.jpress.message.MessageKit;
import io.jpress.model.Message;
import io.jpress.model.Prize;
import io.jpress.model.User;
import io.jpress.model.query.MessageQuery;
import io.jpress.model.query.PrizeQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.notify.email.Email;
import io.jpress.router.RouterMapping;
import io.jpress.ui.freemarker.tag.UserContentPageTag;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.EncryptUtils;
import io.jpress.utils.StringUtils;

@RouterMapping(url = Consts.ROUTER_USER)
@Before(UserInterceptor.class)
public class UserController extends BaseFrontController {
	private static final Log log = Log.getLog(UserController.class);
	@Clear(UserInterceptor.class)
	public void index() {
		String action = getPara();
		if (StringUtils.isBlank(action)) {
			renderError(404);
		}

		keepPara();

		BigInteger userId = StringUtils.toBigInteger(action, null);

		if (userId != null) {
			User user = UserQuery.me().findById(userId);
			if (user != null) {
				setAttr("user", user);
				render(String.format("user_detail.html", action));
			} else {
				renderError(404);
			}
		} else {
			if ("detail".equalsIgnoreCase(action)) {
				renderError(404);
			}
			render(String.format("user_%s.html", action));
		}
	}

    /***
     *  用户登录
     */
	@Clear(UserInterceptor.class)
	@ActionKey(Consts.ROUTER_USER_LOGIN) // 固定登陆的url
	public void login() {
		keepPara();

		String username = getPara("username");
		String password = getPara("password");

		if (username == null || password == null) {
			render("user_login.html");
			return;
		}

		long errorTimes = CookieUtils.getLong(this, "_login_errors", 0);
		if (errorTimes >= 3) {
			if (!validateCaptcha("_login_captcha")) { // 验证码没验证成功！
				if (isAjaxRequest()) {
					renderAjaxResultForError("没有该用户");
				} else {
					redirect(Consts.ROUTER_USER_LOGIN);
				}
				return;
			}
		}

		User user = UserQuery.me().findUserByUsername(username);
		if (null == user) {
			if (isAjaxRequest()) {
				renderAjaxResultForError("没有该用户");
			} else {
				setAttr("errorMsg", "没有该用户");
				render("user_login.html");
			}
			CookieUtils.put(this, "_login_errors", errorTimes + 1);
			return;
		}

		if (EncryptUtils.verlifyUser(user.getPassword(), user.getSalt(), password)) {
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
			if (this.isAjaxRequest()) {
				renderAjaxResultForSuccess("登陆成功");
			} else {
				String gotoUrl = getPara("goto");
				if (StringUtils.isNotEmpty(gotoUrl)) {
					gotoUrl = StringUtils.urlDecode(gotoUrl);
					gotoUrl = StringUtils.urlRedirect(gotoUrl);
					redirect(gotoUrl);
				} else {
					redirect(Consts.ROUTER_USER_CENTER);
				}
			}
		} else {
			if (isAjaxRequest()) {
				renderAjaxResultForError("密码错误");
			} else {
				setAttr("errorMsg", "密码错误");
				render("user_login.html");
			}
			CookieUtils.put(this, "_login_errors", errorTimes + 1);
		}
	}

	public void logout() {
		CookieUtils.remove(this, Consts.COOKIE_LOGINED_USER);
		render("user_login.html");
	}

	@Clear(UserInterceptor.class)
	public void doRegister() {

		if (!validateCaptcha("_register_captcha")) { // 验证码没验证成功！
			renderForRegister("not validate captcha", Consts.ERROR_CODE_NOT_VALIDATE_CAPTHCHE);
			return;
		}

		keepPara();

		String username = getPara("username");
		String email = getPara("email");
		String mobile = getPara("mobile");
		String password = getPara("password");
		String confirm_password = getPara("confirm_password");

		if (StringUtils.isBlank(username)) {
			renderForRegister("username is empty!", Consts.ERROR_CODE_USERNAME_EMPTY);
			return;
		}

		if (!StringUtils.isNotBlank(email)) {
			renderForRegister("email is empty!", Consts.ERROR_CODE_EMAIL_EMPTY);
			return;
		} else {
			email = email.toLowerCase();
		}

		if (!StringUtils.isNotBlank(password)) {
			renderForRegister("password is empty!", Consts.ERROR_CODE_PASSWORD_EMPTY);
			return;
		}

		if (StringUtils.isNotEmpty(confirm_password)) {
			if (!confirm_password.equals(password)) {
				renderForRegister("password is not equals confirm_password!", Consts.ERROR_CODE_PASSWORD_EMPTY);
				return;
			}
		}

		if (UserQuery.me().findUserByUsername(username) != null) {
			renderForRegister("username has exist!", Consts.ERROR_CODE_USERNAME_EXIST);
			return;
		}

		if (UserQuery.me().findUserByEmail(email) != null) {
			renderForRegister("email has exist!", Consts.ERROR_CODE_EMAIL_EXIST);
			return;
		}

		if (null != mobile && UserQuery.me().findUserByMobile(mobile) != null) {
			renderForRegister("phone has exist!", Consts.ERROR_CODE_PHONE_EXIST);
			return;
		}

		User user = new User();
		user.setUsername(username);
		user.setNickname(username);
		user.setEmail(email);
		user.setMobile(mobile);

		String salt = EncryptUtils.salt();
		password = EncryptUtils.encryptPassword(password, salt);
		user.setPassword(password);
		user.setSalt(salt);
		user.setCreateSource("register");
		user.setCreated(new Date());

		if (user.save()) {
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
			MessageKit.sendMessage(Actions.USER_CREATED, user);

			if (isAjaxRequest()) {
				renderAjaxResultForSuccess();
			} else {
				String gotoUrl = getPara("goto");
				if (StringUtils.isNotEmpty(gotoUrl)) {
					gotoUrl = StringUtils.urlDecode(gotoUrl);
					gotoUrl = StringUtils.urlRedirect(gotoUrl);
					redirect(gotoUrl);
				} else {
					redirect(Consts.ROUTER_USER_CENTER);
				}
			}
		} else {
			renderAjaxResultForError();
		}
	}

	private void renderForRegister(String message, int errorCode) {
		String referer = getRequest().getHeader("Referer");
		if (isAjaxRequest()) {
			renderAjaxResult(message, errorCode);
		} else {
			redirect(referer + "?errorcode=" + errorCode);
		}
	}

	public void center() {
		keepPara();
		String action = getPara(0, "index");
		render(String.format("user_center_%s.html", action));
		int pageNumber = getParaToInt(1, 1);
		//BigInteger userId = getLoginedUser().getId();
		User user = getLoginedUser();
		setAttr(UserContentPageTag.TAG_NAME, new UserContentPageTag(action, user.getId(), pageNumber));
		setAttr("action", action);
		setAttr(action, "active");
		setAttr("user", user);
	}


	public void userInfo(){
        addUserInfoToAttr();
		render("user_userInfo.html");
	}
	public void userMessage(){
		addUserInfoToAttr();
		render("user_userMessage.html");
	}
	private void addUserInfoToAttr(){
        //BigInteger userId = getLoginedUser().getId();
        User user = getLoginedUser();
        setAttr("user", user);
	}

	public void getUserMsgData(){
        System.out.println("come here");
        renderJson("{\"rows\":[{\"MeterMeasureHistoryID\":123,\"Value\":234,\"Timestamp\":\"2016-09-09 12:12:12\"}],\"total\":1}");
        //return ;
	}

	/***
	 *  发送验证邮件：
	 *  	处理方式：（1）发送构造连接进行验证；（2）发送验证码进行验证。
	 *  现在使用验证码进行验证
	 */
	public void sendValidateEmail(){
		String email = getPara("email");
		int randomCode = new Random().nextInt(9999);
		while(randomCode <= 1000){
			randomCode = new Random().nextInt(9999);
		}
		User user = getLoginedUser();
		user.setEmailStatus(randomCode + "");
		if(user.update()) {
			Email.create().subject("邮箱验证").content("").to(email).send();
			renderAjaxResult("验证邮件已发送，请注意查收", 200);
		}
		else {
			renderAjaxResult("验证邮件发送失败", 400);
		}
	}

	public void validateEmail(){
		String randwonCode = getPara("randwonCode");
		User user = getLoginedUser();
		if(user.getEmailStatus().equalsIgnoreCase(randwonCode)){
			user.setEmailStatus("true");
			user.update();
			renderAjaxResult("邮箱验证成功！", 200);
		}
		else {
			renderAjaxResult("邮箱验证失败！", 400);
		}
	}



	/*******************************************************************************************************************/
	/*  					通用服务																				   */
	/*******************************************************************************************************************/
	/**
	 *  获取用户信息
	 */
	public void getUserInfo(){
		BigInteger userId = getLoginedUser().getId();
		User user = UserQuery.me().findById(userId);
		renderJson(user);
	}

    /***
     *  更新用户信息
     */
    public void updateUserInfo(){
        User user = getModel(User.class, "");
        if(user.getId() == null){
            renderAjaxResult("更新失败，用户ID信息不正确", 400);
        }
        String password = user.getPassword();
        //这个对每个可修改字段进行更新，需要区分老师和学生？
        if (StringUtils.isNotBlank(user.getEmail())) {
            User dbUser = UserQuery.me().findUserByEmail(user.getEmail());
            if (dbUser != null && user.getId().compareTo(dbUser.getId()) != 0) {
                renderAjaxResultForError("邮件地址已经存在，不能修改为该邮箱。");
                return;
            }
        }
        if (StringUtils.isNotBlank(user.getMobile())) {
            User dbUser = UserQuery.me().findUserByMobile(user.getMobile());
            if (dbUser != null && user.getId().compareTo(dbUser.getId()) != 0) {
                renderAjaxResultForError("手机号码地址已经存在，不能修修改为该手机号码。");
                return;
            }
        }

        // 用户修改了密码
        if (StringUtils.isNotEmpty(password)) {
            User dbUser = UserQuery.me().findById(user.getId());
            user.setSalt(dbUser.getSalt());
            password = EncryptUtils.encryptPassword(password, dbUser.getSalt());
            user.setPassword(password);
        } else {
            // 清除password，防止密码被置空
            user.remove("password");
        }
        renderAjaxResult("更新成功", 200);
    }

}
