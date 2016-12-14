package io.jpress.front.controller;

import com.jfinal.aop.Before;
import com.jfinal.log.Log;
import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.model.Message;
import io.jpress.model.query.MessageQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.router.RouterMapping;

import javax.swing.tree.ExpandVetoException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageController
 *
 * @author chenkui
 * @version 1.0
 * @date 2016/11/9
 */
@RouterMapping(url = Consts.ROUTER_MSG)
@Before(UserInterceptor.class)
public class MessageController extends BaseFrontController {
    private static final Log log = Log.getLog(MessageController.class);

    //url : /msg/getMessage/123456
    public void getMessage(){
        try {
            BigInteger messageId = new BigInteger(getPara(0));
            Message message = MessageQuery.me().findById(messageId);
            if(message == null){
                renderAjaxResult("获取消息详细信息失败:未找到指定消息", 400);
            }
            if(message.getTo().equals(getLoginedUser().getId())){
                renderJson(message);
            }
        }catch (Exception e){
            renderAjaxResult("获取消息详细信息失败", 400);
            log.error("获取消息详细信息失败"+e.getMessage());
        }

    }
    //url : /msg/deleteMessage/123456
    public void deleteMessage(){
        try {
            BigInteger messageId = new BigInteger(getPara(0));
            Message message = MessageQuery.me().findById(messageId);
            if(message == null){
                renderAjaxResult("删除消息失败：未找到指定消息", 400);
            }
            if(message.getTo().equals(getLoginedUser().getId())){
                message.delete();
                renderAjaxResult("删除消息成功", 200);
            }
        }catch (Exception e){
            renderAjaxResult("删除消息失败", 400);
            log.error("删除消息失败失败"+e.getMessage());
        }

    }

    /***
     *  获取用户所有消息
     */
    public void getAllMessage(){
        BigInteger userId = getLoginedUser().getId();
        try {
            List<Message> messageList = MessageQuery.me().findAllByUser(userId);
            if(messageList == null){
                renderJson(new ArrayList<>());
            }
            for (Message message : messageList){
                try {
                    message.setFromName(UserQuery.me().findById(message.getFrom()).getRealname());
                }catch (Exception e){
                    message.setFromName("管理员");
                }
            }
            renderJson(messageList);
        }catch (Exception e){
            log.error("获取用户消息失败："+e.getMessage());
            renderJson(new ArrayList<>());
        }
    }

    /***
     *  获取用户所有已读消息
     */
    public void getAllReadMessage(){
        BigInteger userId = new BigInteger("1");//getLoginedUser().getId();
        try {
            List<Message> messageList = MessageQuery.me().findReadByUser(userId);
            if(messageList == null){
                renderJson(new ArrayList<>());
            }
            for (Message message : messageList){
                try {
                    message.setFromName(UserQuery.me().findById(message.getFrom()).getRealname());
                }catch (Exception e){
                    message.setFromName("管理员");
                }
            }
            log.info(messageList.toString());
            renderJson(messageList);
        }catch (Exception e){
            log.error("获取用户已读消息失败："+e.getMessage());
            renderJson(new ArrayList<>());
        }
    }

    /***
     *  获取用户所有已删除消息
     */
    public void getAllDeleteMessage(){
        BigInteger userId = getLoginedUser().getId();
        try {
            List<Message> messageList = MessageQuery.me().findDeleteByUser(userId);
            if(messageList == null){
                renderJson(new ArrayList<>());
            }
            for (Message message : messageList){
                try {
                    //message.setFromName(UserQuery.me().findById(message.getFrom()).getRealname());
                    message.set("from_name", UserQuery.me().findById(message.getFrom()).getRealname());
                }catch (Exception e){
                    message.setFromName("管理员");
                    message.set("from_name", "管理员");
                }

            }
            renderJson(messageList);
        }catch (Exception e){
            log.error("获取用户已删除失败 "+e.getMessage());
            renderJson(new ArrayList<>());
        }
    }

    //url: /msg/setMessageDelete  POST：messageId
    public void setMessageDelete(){
        try {
            BigInteger messageiId = getParaToBigInteger("messageId");
            Message message = MessageQuery.me().findById(messageiId);
            if(message == null){
                renderAjaxResult("设置消息已删除失败：未找到指定消息", 400);
            }
            message.setDelete(1);
            message.saveOrUpdate();
            renderAjaxResult("设置消息已删除成功", 200);
        }catch (Exception e){
            renderAjaxResult("设置消息已删除失败", 400);
            log.error("设置消息已删除失败: " + e.getMessage());
        }


    }

    public void setMessageRead(){
        try {
            BigInteger messageiId = getParaToBigInteger("messageId");
            Message message = MessageQuery.me().findById(messageiId);
            if(message == null){
                renderAjaxResult("设置消息已读失败：未找到指定消息", 400);
            }
            message.setRead(1);
            message.saveOrUpdate();
            renderAjaxResult("设置消息已读成功", 200);
        }catch (Exception e){
            renderAjaxResult("设置消息已读失败", 400);
            log.error("设置消息已读失败: " + e.getMessage());
        }
    }


    public void addMessage(){
        try {
            String title = getPara("title");
            if(title == null || title.isEmpty()){
                renderAjaxResult("发送消息失败：名称为空", 400);
            }
            String content = getPara("content");
            BigInteger to = getParaToBigInteger("to");
            if(to == null){
                renderAjaxResult("发送消息失败：接收人不存在", 400);
            }
            BigInteger from = getParaToBigInteger("from");

            Message message = new Message();
            message.setTitle(title);
            message.setContent(content);
            message.setTo(to);
            message.setFrom(from);
            message.setRead(0);
            message.setDelete(0);
            message.save();
        }catch (Exception e){
            renderAjaxResult("发送消息失败", 400);
            log.error("发送消息失败:"+e.getMessage());
        }

    }
}
