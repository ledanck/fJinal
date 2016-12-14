package io.jpress.model.query;

import com.jfinal.plugin.ehcache.IDataLoader;
import io.jpress.model.Prize;

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
public class PrizeQuery extends JBaseQuery {
    protected static final Prize DAO = new Prize();
    private static final PrizeQuery QUERY = new PrizeQuery();

    public static PrizeQuery me() {
        return QUERY;
    }

    /***
     *  分页查询消息
     * @param page
     * @param pagesize
     * @param userId
     * @param orderBy
     * @return
     */
    public List<Prize> findList(int page, int pagesize, BigInteger userId, String orderBy) {
        StringBuilder sqlBuilder = new StringBuilder("select * from prize p ");
        LinkedList<Object> params = new LinkedList<Object>();

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "p.user_id", userId, params, needWhere);
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
        StringBuilder fromBuilder = new StringBuilder(" from prize p ");
        buildOrderBy(orderby, fromBuilder);
        return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
    }
*/

    /***
     *  获取当前用户所有获奖的总数
     * @param userId
     * @return
     */
    public long findCount(final BigInteger userId) {
        return DAO.find("select * from prize p where p.user_id=?", userId).size();
    }

    /***
     *  获取用户最近N条获奖信息
     * @param top
     * @param userId
     * @return
     */
    public List<Prize> findLastNPrize(int top, BigInteger userId){
        return DAO.find("select * from prize p where p.user_id=? order by p.create_time DESC limit ?", userId, top);
    }

    /***
     *  查询某条获奖的具体信息
     * @param prizeId
     * @return
     */
    public Prize findById(final BigInteger prizeId) {
        return DAO.findById(prizeId);
    }

    /***
     *  获取当前用户所有获奖消息
     * @param userId
     * @return
     */
    public List<Prize> findAllByUser(final BigInteger userId){
        return DAO.find("select * from prize p where p.user_id=?", userId);
    }

    protected static void buildOrderBy(String orderBy, StringBuilder fromBuilder) {
        if ("create_time".equals(orderBy)) {
            fromBuilder.append(" ORDER BY p.create_time DESC");
        }
    }
}
