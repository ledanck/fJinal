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
package io.jpress.notify.sms;

public class SmsMessage {

	private String rec_num;
	private String sign_name;
	private String param;
	private String template;
	private String content;

	public String getRec_num() {
		return rec_num;
	}

	public SmsMessage setRec_num(String rec_num) {
		this.rec_num = rec_num;
		return this;
	}

	public String getSign_name() {
		return sign_name;
	}

	public SmsMessage setSign_name(String sign_name) {
		this.sign_name = sign_name;
		return this;
	}

	public String getParam() {
		return param;
	}

	public SmsMessage setParam(String param) {
		this.param = param;
		return this;
	}

	public String getTemplate() {
		return template;
	}

	public SmsMessage setTemplate(String template) {
		this.template = template;
		return this;
	}

	public String getContent() {
		return content;
	}

	public SmsMessage setContent(String content) {
		this.content = content;
		return this;
	}

	public static SmsMessage create() {
		return new SmsMessage();
	}

	public void send() {
		SmsSenderFactory.createSender().send(this);
	}

}
