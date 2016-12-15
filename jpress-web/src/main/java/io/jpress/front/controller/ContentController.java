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

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.render.Render;

import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.core.addon.HookInvoker;
import io.jpress.core.cache.ActionCache;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Content;
import io.jpress.model.Taxonomy;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.TaxonomyQuery;
import io.jpress.router.RouterMapping;
import io.jpress.template.TemplateManager;
import io.jpress.template.TplModule;
import io.jpress.ui.freemarker.tag.CommentPageTag;
import io.jpress.ui.freemarker.tag.MenusTag;
import io.jpress.ui.freemarker.tag.NextContentTag;
import io.jpress.ui.freemarker.tag.PreviousContentTag;
import io.jpress.utils.StringUtils;

@RouterMapping(url = Consts.ROUTER_CONTENT)
public class ContentController extends BaseFrontController {

	private String slug;
	private BigInteger id;
	private int page;

	@ActionCache
	public void index() {
		try {
			Render render = onRenderBefore();
			if (render != null) {
				render(render);
			} else {
				doRender();
			}
		} finally {
			onRenderAfter();
		}

	}

	private void doRender() {
		initRequest();

		Content content = queryContent();
		if (null == content) {
			renderError(404);
			return;
		}

		TplModule module = TemplateManager.me().currentTemplateModule(content.getModule());

		if (module == null) {
			renderError(404);
			return;
		}

		updateContentViewCount(content);
		setGlobleAttrs(content);

		setAttr("p", page);
		setAttr("content", content);

		setAttr(NextContentTag.TAG_NAME, new NextContentTag(content));
		setAttr(PreviousContentTag.TAG_NAME, new PreviousContentTag(content));

		setAttr(CommentPageTag.TAG_NAME, new CommentPageTag(getRequest(), content, page));

		List<Taxonomy> taxonomys = TaxonomyQuery.me().findListByContentId(content.getId());
		setAttr("taxonomys", taxonomys);
		setAttr(MenusTag.TAG_NAME, new MenusTag(getRequest(), taxonomys, content));

		String style = content.getStyle();
		if (StringUtils.isNotBlank(style)) {
			render(String.format("content_%s_%s.html", module.getName(), style.trim()));
			return;
		}

		style = tryGetTaxonomyTemplate(module, taxonomys);
		if (style != null) {
			render(String.format("content_%s_%s.html", module.getName(), style));
			return;
		}

		render(String.format("content_%s.html", module.getName()));

	}

	private String tryGetTaxonomyTemplate(TplModule module, List<Taxonomy> taxonomys) {
		if (taxonomys == null || taxonomys.isEmpty())
			return null;
		String forSlug = null;
		for (Taxonomy taxonomy : taxonomys) {
			String tFile = String.format("content_%s_%s%s.html", module.getName(), Consts.TAXONOMY_TEMPLATE_PREFIX,taxonomy.getSlug());
			if (templateExists(tFile)) {
				if (forSlug == null) {
					forSlug = Consts.TAXONOMY_TEMPLATE_PREFIX + taxonomy.getSlug();
				} else {
					forSlug = null;
					break;
				}
			}
		}
		return forSlug;
	}

	private void updateContentViewCount(Content content) {
		long visitorCount = VisitorCounter.getVisitorCount(content.getId());
		Long viewCount = content.getViewCount() == null ? visitorCount : content.getViewCount() + visitorCount;
		content.setViewCount(viewCount);
		if (content.update()) {
			VisitorCounter.clearVisitorCount(content.getId());
		}
	}

	private void setGlobleAttrs(Content content) {

		setAttr(Consts.ATTR_GLOBAL_WEB_TITLE, content.getTitle());

		if (StringUtils.isNotBlank(content.getMetaKeywords())) {
			setAttr(Consts.ATTR_GLOBAL_META_KEYWORDS, content.getMetaKeywords());
		} else {
			setAttr(Consts.ATTR_GLOBAL_META_KEYWORDS, content.getTaxonomyAsString(null));
		}

		if (StringUtils.isNotBlank(content.getMetaDescription())) {
			setAttr(Consts.ATTR_GLOBAL_META_DESCRIPTION, content.getMetaDescription());
		} else {
			setAttr(Consts.ATTR_GLOBAL_META_DESCRIPTION, content.getSummary());
		}

	}

	private Content queryContent() {
		if (id != null) {
			return ContentQuery.me().findById(id);
		} else {
			return ContentQuery.me().findBySlug(StringUtils.urlDecode(slug));
		}
	}

	private void initRequest() {
		String para = getPara(0);
		if (StringUtils.isBlank(para)) {
			id = getParaToBigInteger("id");
			slug = getPara("slug");
			page = getParaToInt("p", 1);
			if (id == null && slug == null) {
				renderError(404);
			}
			return;
		}

		if (StringUtils.isNumeric(para)) {
			id = new BigInteger(para);
		} else {
			slug = para;
		}
		page = getParaToInt(1, 1);

	}

	private Render onRenderBefore() {
		return HookInvoker.contentRenderBefore(this);
	}

	private void onRenderAfter() {
		HookInvoker.contentRenderAfter(this);
	}


	/**
	 *  获取所有比赛信息
	 */
	public void getAllCompetition(){
		List<Content> contentList=  ContentQuery.me().findByModule("page");
		if(contentList != null){
			renderJson(contentList);
		}
		else {
			renderJson(new ArrayList<>());
		}
	}

	/***
	 *  根据比赛科目，比赛时间搜索比赛信息。
	 *   POST消息，title 表示比赛名称， starttime/endtime表示时间
	 */
	public void searchCompetition() {
		String title = getPara("title");    //比赛科目
		String endtime = getPara("endtime");       //比赛消息名称
		String starttime = getPara("starttime");       //比赛开始时间

		if(starttime!=null && endtime!=null){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date sdate = simpleDateFormat.parse(starttime);
				Date edata = simpleDateFormat.parse(endtime);
				List<Content> contentList = ContentQuery.me().searchPageByTitleAndTime(title, sdate, edata);
                contentList.add(new Content());
				if(contentList!=null){
                    renderJson(JSON.toJSONString(contentList));
                    return;
				}
				else {
					renderJson(new ArrayList<>());
                    return;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		List<Content> contentList = ContentQuery.me().searchPageByTitleAndTime(title, null, null);
		if(contentList!=null){
			renderJson(contentList);
		}
		else {
			renderJson(new ArrayList<>());
		}
	}
}
