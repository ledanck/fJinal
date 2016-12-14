package io.jpress.model.query;

import io.jpress.model.Signup;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * SignupQuery
 *
 * @author chenkui
 * @version 1.0
 * @date 2016/11/9
 */
public class SignupQuery extends JBaseQuery{


    protected static final Signup DAO = new Signup();
    private static final SignupQuery QUERY = new SignupQuery();

    public static SignupQuery me() {
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
    public List<Signup> findList(int page, int pagesize, BigInteger userId, String orderBy) {
        StringBuilder sqlBuilder = new StringBuilder("select * from signup s ");
        LinkedList<Object> params = new LinkedList<Object>();

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "s.user_id", userId, params, needWhere);
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


    /***
     *  获取当前用户所有报名的总数
     * @param userId
     * @return
     */
    public long findCount(final BigInteger userId) {
        return DAO.find("select * from signup s where s.user_id=?", userId).size();
    }

    /***
     *  获取用户最近N条报名信息
     * @param top
     * @param userId
     * @return
     */
    public List<Signup> findLastNPrize(int top, BigInteger userId){
        return DAO.find("select * from signup s where s.user_id=? order by s.create_time DESC limit ?", userId, top);
    }

    /***
     *  查询某条获奖的具体信息
     * @param signupId
     * @return
     */
    public Signup findById(final BigInteger signupId) {
        return DAO.findById(signupId);
    }

    /***
     *  获取当前用户所有获奖消息
     * @param userId
     * @return
     */
    public List<Signup> findAllByUser(final BigInteger userId){
        return DAO.find("select * from signup s where s.user_id=?", userId);
    }


    protected static void buildOrderBy(String orderBy, StringBuilder fromBuilder) {
        if ("create_time".equals(orderBy)) {
            fromBuilder.append(" ORDER BY s.create_time DESC");
        }
    }
}
