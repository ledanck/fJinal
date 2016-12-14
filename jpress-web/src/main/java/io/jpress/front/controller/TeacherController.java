package io.jpress.front.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.jfinal.aop.Before;
import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.interceptor.TeacherInterceptor;
import io.jpress.message.Message;
import io.jpress.model.Groupinfo;
import io.jpress.model.Prize;
import io.jpress.model.User;
import io.jpress.model.query.GroupinfoQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.notify.email.Email;
import io.jpress.notify.email.EmailSenderFactory;
import io.jpress.notify.email.IEmailSender;
import io.jpress.notify.sms.SmsMessage;
import io.jpress.router.RouterMapping;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * TeacherController
 *
 * @author chenkui
 * @version 1.0
 * @date 2016/10/30
 */
@RouterMapping(url = Consts.ROUTER_THEACHER)
@Before(TeacherInterceptor.class)
public class TeacherController  extends BaseFrontController {
    /***
     *  获取当前老师的所有学生信息。如果当前用户为项目组长，同样获取所有学生信息
     */
    public void getMyStudents(){
        User teacher = getRequsetUser();
        if(teacher.isTeacher()){
            //如果是老师，那么久直接查询即可
            List<User> students = UserQuery.me().findStudentsByTeacher(teacher.getId());
            if(students != null){
                renderJson(students);
                return;
            }
        }
        else if(teacher.isLeader()){
            //如果是年级组长，首先要查询该年级组长下属所有老师，再根据这些老师的ID去查相关的所有学生
            List<User> teachers = UserQuery.me().findStudentsByTeacher(teacher.getId());
            if(teachers != null && teachers.isEmpty()){
                List<User> allStudents = new ArrayList<>();
                for (User user : teachers){
                    List<User> tmp = UserQuery.me().findStudentsByTeacher(user.getId());
                    if(tmp!=null && !tmp.isEmpty()){
                        allStudents.addAll(tmp);
                    }
                }
                renderJson(allStudents);
                return;
            }
        }
        renderJson(new ArrayList<>());
    }

    /***
     *  给指定学生发送邮件
     */
    public void sendEmailToStudent(){
        String subject = getPara("subject");
        String content = getPara("content");
        String id = getPara("student");

        BigInteger studentId = new BigInteger(id);
        User student = UserQuery.me().findById(studentId);
        if(student != null && student.getEmail()!=null && !student.getEmail().isEmpty()
                && student.getEmailStatus()!=null && student.getEmailStatus().equalsIgnoreCase("true")){
            Email.create().subject(subject).content(content).to(student.getEmail()).send();
        }
    }

    /***
     *  批量发送邮件。相同的内容发给一批学生
     */
    public void sendEmailToStudentByBatch(){
        String subject = getPara("subject");
        String content = getPara("content");
        String to = getPara("students");
        List<String> stringList = JSON.parseObject(to, List.class);
        List<String> toEmail = new ArrayList<>();
        for (String s : stringList){
            BigInteger studentId = new BigInteger(s);
            User student = UserQuery.me().findById(studentId);
            if(student != null && student.getEmail()!=null && !student.getEmail().isEmpty()
                    && student.getEmailStatus()!=null && student.getEmailStatus().equalsIgnoreCase("true")){
                toEmail.add(student.getEmail());
            }
        }
        if(toEmail.size() > 0) {
            String[] emailsTo = new String[toEmail.size()];
            toEmail.toArray(emailsTo);
            Email.create().subject(subject).content(content).to(emailsTo).send();
        }
    }

    /***
     *  批量发送邮件。相同的内容发给一批学生. TODO 尚未完成
     */
    public void sendSmsToStudent(){
        String content = getPara("content");
        String to = getPara("students");
        List<String> stringList = JSON.parseObject(to, List.class);

        StringBuilder sb = new StringBuilder();
        for (String s : stringList){
            BigInteger studentId = new BigInteger(s);
            User student = UserQuery.me().findById(studentId);
            if(student != null && student.getEmail()!=null && !student.getEmail().isEmpty()
                    && student.getEmailStatus()!=null && student.getEmailStatus().equalsIgnoreCase("true")){
                sb.append(student.getEmail());
                sb.append(",");
            }
        }
        if(sb.length() > 0) {
            SmsMessage.create().setContent(content).setRec_num(sb.substring(0, sb.length()-1));
        }
    }


    /***
     *  获取年级组长下属老师信息
     */
    public void getMyTeachers(){
        User leader = getRequsetUser();
        if(leader.isLeader()){
            List<User> teachers = UserQuery.me().findStudentsByTeacher(leader.getId());
            if(teachers != null){
                renderJson(teachers);
                return;
            }
        }
        renderJson(new ArrayList<>());
    }


    public void getGroupInfo(){
        User teacher = getRequsetUser();
        if(teacher.isTeacher()){
            List<Groupinfo> groups = GroupinfoQuery.me().findAllByUser(teacher.getId());
            if(groups != null || !groups.isEmpty()){
                renderJson(groups);
                return;
            }
        }
        renderJson(new ArrayList<>());
    }

    /***
     *  更新分组名称
     */
    public void updateGroup(){
        String name = getPara("group_name");
        String group_id = getPara("group_id");
        if(name != null && !name.isEmpty()) {
            User teacher = getRequsetUser();
            Groupinfo groupExist = GroupinfoQuery.me().findById(new BigInteger(group_id));
            if(groupExist != null){
                groupExist.setGroupName(name);
                groupExist.saveOrUpdate();
                renderAjaxResult("更新分组名称成功", 200);
                return;
            }
            else {
                renderAjaxResult("更新分组名称失败: 分组不存在", 400);
            }
        }
        else {
            renderAjaxResult("更新分组名称失败", 400);
        }
    }

    /***
     *  添加新分组
     */
    public void addNewGroup(){
        Groupinfo group = new Groupinfo();
        String name = getPara("group_name");
        if(name != null && !name.isEmpty()){

            User teacher = getRequsetUser();
            Groupinfo groupExist = GroupinfoQuery.me().findByUserAndName(teacher.getId(), name);
            if(groupExist != null){
                renderAjaxResult("添加分组失败: 已存在同名分组", 400);
                return;
            }
            group.setGroupName(name);
            group.setTeacherId(getRequsetUser().getId());
            group.save();
            renderAjaxResult("添加分组成功", 200);
        }
        else {
            renderAjaxResult("添加分组失败：分组名称错误", 400);
        }
    }

    /***
     *  删除分组。分组被删除，相关联的学生分组信息需要被更新
     */
    public void deleteGroup(){
        String name = getPara("group_name");
        if(name != null && !name.isEmpty()) {
            User teacher = getRequsetUser();
            Groupinfo groupExist = GroupinfoQuery.me().findByUserAndName(teacher.getId(), name);
            if(groupExist != null){
                boolean status= groupExist.delete();
                if(status){
                    List<User> userList = UserQuery.me().findStudentsByTeacher(teacher.getId());
                    for (User student:userList){
                        if(student.getGroupId() == groupExist.getId()){
                            student.setGroupId(new BigInteger("-1"));
                            student.saveOrUpdate();
                        }
                    }
                    renderAjaxResult("删除分组成功", 200);
                }
                else {
                    renderAjaxResult("删除分组失败组", 400);
                }
            }
            else {
                renderAjaxResult("删除分组失败：不存在该分组", 400);
            }
        }
        else {
            renderAjaxResult("删除分组失败：分组名称错误", 400);
        }
    }

    /***
     *  更新学生分组信息
     *
     *  POST 请求。{group_id：xxxx, students:[zz, zzz, zzz]}
     */
    public void addStudentToGroup(){
        BigInteger group_id = getParaToBigInteger("group_id");

        String students = getPara("students");
        if(group_id!=null && students!= null){
            List<String> studentList = new Gson().fromJson(students, List.class);
            if(studentList != null && !studentList.isEmpty()){
                for(String id:studentList){
                    BigInteger studid = new BigInteger(id);
                    User student = UserQuery.me().findById(studid);
                    if(student.getTeacherId().equals(getRequsetUser().getId())){
                        student.setGroupId(group_id);
                        student.update();
                    }
                }
            }
        }
    }

    private User getRequsetUser(){
        //BigInteger userId = getLoginedUser().getId();
        User user = getLoginedUser();//UserQuery.me().findById(userId);
        return user;
    }

    /***
     *  添加更新学生获奖信息
     */
    private void addOrUpdatePrizeForStudent(){
        try {
            BigInteger userId = getParaToBigInteger("userId");
            String subject = getPara("subject");
            String path = getPara("picture");
            String scroe = getPara("score", "100");
            String info = getPara("info", "恭喜！");
            Prize prize = new Prize();
            prize.setUserId(userId);
            prize.setInfo(info);
            prize.setPath(path);
            prize.setSubject(subject);
            prize.setScore(Long.parseLong(scroe));
            if(prize.saveOrUpdate()) {
                renderAjaxResult("添加学生获奖信息成功", 200);
            }
            else {
                throw new Exception();
            }
        }catch (Exception e){
            renderAjaxResult("添加学生获奖信息失败", 400);
        }
    }

    /***
     *  通知学生消息
     */
    private void addOrUpdateMessageForStudent(){
        try {
            BigInteger to = getParaToBigInteger("userId");
            String title = getPara("title");
            String content = getPara("content");
            io.jpress.model.Message message = new io.jpress.model.Message();
            message.setContent(content);
            message.setTitle(title);
            message.setFrom(getLoginedUser().getId());
            message.setTo(to);
            if(message.saveOrUpdate()) {
                renderAjaxResult("添加学生获奖信息成功", 200);
            }
            else {
                throw new Exception();
            }
        }catch (Exception e){
            renderAjaxResult("添加学生获奖信息失败", 400);
        }
    }
}
