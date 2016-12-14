package io.jpress.model.query;

import com.jfinal.plugin.ehcache.IDataLoader;
import io.jpress.model.Groupinfo;

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
public class GroupinfoQuery extends JBaseQuery {
    protected static final Groupinfo DAO = new Groupinfo();
    private static final GroupinfoQuery QUERY = new GroupinfoQuery();

    public static GroupinfoQuery me() {
        return QUERY;
    }

    /***
     *  获取当前用户所有分组总数
     * @param userId
     * @return
     */
    public long findCount(final BigInteger userId) {
        return DAO.find("select * from groupinfo g where g.teacher_id=?", userId).size();
    }

    /***
     *  查询分组的具体信息
     * @param groupId
     * @return
     */
    public Groupinfo findById(final BigInteger groupId) {
        return DAO.findById(groupId);
    }

    /***
     *  获取当前用户所有分组
     * @param userId
     * @return
     */
    public List<Groupinfo> findAllByUser(final BigInteger userId){
        return DAO.find("select * from groupinfo g where g.teacher_id=?", userId);
    }

    public Groupinfo findByUserAndName(final BigInteger userId, String groupName){
        return DAO.findFirst("select * from groupinfo g where g.teacher_id=? and g.group_name=?", userId, groupName);
    }


    protected static void buildOrderBy(String orderBy, StringBuilder fromBuilder) {
        if ("id".equals(orderBy)) {
            fromBuilder.append(" ORDER BY g.id DESC");
        }
    }
}
