package io.jpress.model.query;


import io.jpress.model.Message;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * MessageQuery
 *
 * @author chenkui
 * @version 1.0
 * @date 2016/10/27
 */
public class MessageQuery extends JBaseQuery {
    protected static final Message DAO = new Message();
    private static final MessageQuery QUERY = new MessageQuery();

    public static MessageQuery me() {
        return QUERY;
    }

    /***
     *  分页查询消息
     * @param page
     * @param pagesize
     * @param userId
     * @param read
     * @param detele
     * @param orderBy
     * @return
     */
    public List<Message> findList(int page, int pagesize, BigInteger userId, int read, int detele, String orderBy) {
        StringBuilder sqlBuilder = new StringBuilder("select * from message m ");
        LinkedList<Object> params = new LinkedList<Object>();

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "m.to", userId, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "u.read", read+"", params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "u.detele", detele+"", params, needWhere);

        buildOrderBy(orderBy, sqlBuilder);

        sqlBuilder.append(" LIMIT ?, ?");
        params.add(page - 1);
        params.add(pagesize);

        if (params.isEmpty()) {
            return DAO.find(sqlBuilder.toString());
        } else {
            return DAO.find(sqlBuilder.toString(), params.toArray());
        }
    }

/*
    public Page<Message> paginate(int pageNumber, int pageSize , String orderby) {
        String select = "select * ";
        StringBuilder fromBuilder = new StringBuilder(" from message m ");
        buildOrderBy(orderby, fromBuilder);
        return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
    }
*/

    /***
     *  获取当前用户所有消息的总数
     * @param userId
     * @return
     */
    public long findCount(final BigInteger userId) {
        return DAO.find("select * from message m where m.to=?", userId).size();
    }

    /***
     *  获取用户最近N条信息
     * @param top
     * @param userId
     * @return
     */
    public List<Message> findLastNMessage(int top, BigInteger userId){
        return DAO.find("select * from message m where m.to=? order by m.create_time DESC limit ?", userId, top);
    }

    /***
     *  查询某条消息的具体信息
     * @param messageId
     * @return
     */
    public Message findById(final BigInteger messageId) {
        return DAO.findById(messageId);
    }

    /***
     *  获取当前用户所有消息
     * @param userId
     * @return
     */
    public List<Message> findAllByUser(final BigInteger userId){
        return DAO.find("select * from message m where m.to=?", userId);
    }

    /***
     *  获取当前用户所有已读消息
     * @param userId
     * @return
     */
    public List<Message> findReadByUser(final BigInteger userId){
        return DAO.find("select * from message m where m.to=? and m.read=1", userId);
    }

    /***
     *  获取当前用户所有已删除消息
     * @param userId
     * @return
     */
    public List<Message> findDeleteByUser(final BigInteger userId){
        return DAO.find("select * from message m where m.to=? and m.delete=1", userId);
    }

    /***
     *  设定指定消息为已读
     * @param userId
     * @param messageId
     * @return
     */
    public boolean setMessageRead(final BigInteger userId, final BigInteger messageId){
        Message message = DAO.findFirst("select * from message m where m.to=? and m.id=?", userId, messageId);
        if(message!=null){
            message.setRead(1);
            return message.update();
        }
        return false;
    }

    /***
     *  批量设置消息为已读
     * @param userId
     * @param messageIds
     * @return
     */
    public boolean setMessageRead(final BigInteger userId, List<BigInteger> messageIds){
        for (BigInteger id: messageIds){
            setMessageRead(userId, id);
        }
        return true;
    }

    /***
     *  设置当前消息状态为已删除
     * @param userId
     * @param messageId
     * @return
     */
    public boolean setMessageDelete(final BigInteger userId, final BigInteger messageId){
        Message message = DAO.findFirst("select * from message m where m.to=? and m.id=?", userId, messageId);
        if(message!=null){
            message.setDelete(1);
            return message.update();
        }
        return false;
    }

    /***
     *  批量设置消息状态为已删除
     * @param userId
     * @param messageIds
     * @return
     */
    public boolean setMessageDelete(final BigInteger userId, List<BigInteger> messageIds){
        for (BigInteger id: messageIds){
            setMessageDelete(userId, id);
        }
        return true;
    }


    protected static void buildOrderBy(String orderBy, StringBuilder fromBuilder) {
        if ("create_time".equals(orderBy)) {
            fromBuilder.append(" ORDER BY m.create_time DESC");
        }
    }
}
