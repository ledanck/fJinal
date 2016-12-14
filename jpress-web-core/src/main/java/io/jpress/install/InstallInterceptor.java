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
package io.jpress.install;

import com.jfinal.log.Log;
import io.jpress.core.Jpress;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * @title 用于拦截web如果已经安装，则不让其访问InstallController
 * @author Michael Yang （http://fuhai.me）
 * @version 1.0
 * @created 2016年2月1日
 */
public class InstallInterceptor implements Interceptor {
    private static final Log log = Log.getLog(InstallInterceptor.class);
	@Override
	public void intercept(Invocation inv) {
		if (Jpress.isInstalled()) {
            log.info("服务已安装，跳转到主页程...");
			inv.getController().redirect("/");
		}
		else {
            log.info("服务未安装，跳转安装流程...");
			inv.invoke();
		}
	}

}
