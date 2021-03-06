package nc.bs.er.djlx;

/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product.                              *
 \***************************************************************/

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.logging.Log;
import nc.bs.pub.SystemException;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.engine.IConfigVO;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.arap.workflow.config.ConfigurationException;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;

/**
 * DjLX的BO类
 * 
 * 创建日期：(2001-8-30)
 * 
 * @author：
 */
public class DjLXBO {
	/**
	 * DjLXBO 构造子注解。
	 */
	public DjLXBO() {
		super();
	}

	// 新添加的方法
	// 根据单据VO和公司PK删除单据类型
	// 返回删除失败的公司PK和已被删除相应单据类型的公司PK 2004-8-4 xhb
    @SuppressWarnings("rawtypes")
    public Hashtable deleteCorps(BillTypeVO billtypevo, String[] corps) throws BusinessException {
		if (BXConstans.BX_DJDL.equalsIgnoreCase(billtypevo.getDjdl()) || BXConstans.JK_DJDL.equalsIgnoreCase(billtypevo.getDjdl())) {
			try {
				ConfigAgent.getInstance().deleteCommonVO(BusiTypeVO.key, billtypevo.getDjlxbm());
			} catch (ConfigurationException e) {
				Log.getInstance(this.getClass()).error(e);
				throw new BusinessException(e.getMessage(), e);
			}
		}
		DjLXVO vo = (DjLXVO) billtypevo.getParentVO();
        Hashtable<String, Hashtable> result = new Hashtable<String, Hashtable>();
		try {
			DjLXDMO dmo = new DjLXDMO();

			Hashtable<String, String> delcorp = new Hashtable<String, String>();
			Hashtable<String, String> failcorp = new Hashtable<String, String>();
			result.put("del", delcorp);
			result.put("fail", failcorp);

			BaseDAO dao = new BaseDAO();
            Collection coll = dao.retrieveByClause(DjLXVO.class, " djlxbm='" + vo.getDjlxbm() + "'");
			Hashtable<String, DjLXVO> hash = new Hashtable<String, DjLXVO>();
			DjLXVO[] djlxvos = (DjLXVO[]) changeCollection2Array(coll, DjLXVO.class);
			for (int i = 0; i < djlxvos.length; i++) {
				hash.put(djlxvos[i].getDwbm(), djlxvos[i]);
			}
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < corps.length; i++) {
				DjLXVO checkvo = (DjLXVO) hash.get(corps[i]);
				if (checkvo == null) {
					delcorp.put(corps[i], corps[i]);
				} else if (dmo.isInUse(checkvo)) {
					failcorp.put(corps[i], corps[i]);
				} else {
					list.add(checkvo.getDjlxoid());
				}
			}
			if (list.size() > 0) {
			    dao.deleteByPKs(DjLXVO.class, list.toArray(new String[list.size()]));
			}

		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
			throw e;
		} catch (SystemException e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
			throw new BusinessRuntimeException(e.getMessage());
		} catch (NamingException e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
			throw new BusinessRuntimeException(e.getMessage());
		}
		return result;
	}

	/**
	 * 通过单位编码返回指定公司所有记录VO数组。如果单位编码为空返回所有记录。
	 * 
	 * 创建日期：(2001-8-30)
	 * 
	 * @return nc.vo.arap.djlx.DjLXVO[] 查到的VO对象数组
	 * @param unitCode
	 *            int
	 * @exception BusinessException
	 *                异常说明。
	 */
	public DjLXVO[] queryAll(String pk_corp) throws BusinessException {

		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("rawtypes")
        Collection cl = null;
		if (pk_corp == null) {
			cl = dao.retrieveAll(DjLXVO.class);
		} else {
			cl = dao.retrieveByClause(DjLXVO.class, " pk_group='" + pk_corp + "'");
		}
		DjLXVO[] djlxvos = (DjLXVO[]) changeCollection2Array(cl, DjLXVO.class);
		djlxvos = queryBusiTypes(djlxvos);
		return djlxvos;
	}

	/**
	 * 根据VO中所设定的条件返回所有符合条件的VO数组
	 * 
	 * 创建日期：(2001-8-30)
	 * 
	 * @return nc.vo.arap.djlx.DjLXVO[]
	 * @param djLXVO
	 *            nc.vo.arap.djlx.DjLXVO
	 * @param isAnd
	 *            boolean 以与条件查询还是以或条件查询
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public DjLXVO[] queryByVO(DjLXVO vo) throws BusinessException {

		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("rawtypes")
        Collection cl = dao.retrieve(vo, true);
		DjLXVO[] djLXs =  (DjLXVO[]) changeCollection2Array(cl, DjLXVO.class);
		djLXs = queryBusiTypes(djLXs);
		return djLXs;
	}

	public DjLXVO[] queryBusiTypes(DjLXVO[] djlxvos) throws BusinessException {
		if (null != djlxvos) {
			Map<String, DjLXVO> ret = new HashMap<String, DjLXVO>();
			for (DjLXVO djlx : djlxvos) {
				if (BXConstans.JK_DJDL.equalsIgnoreCase(djlx.getDjdl()) || BXConstans.BX_DJDL.equalsIgnoreCase(djlx.getDjdl())) {
					ret.put(djlx.getDjlxbm(), djlx);
				}
			}
			Map<String, IConfigVO> mapDjlx;

			mapDjlx = ConfigAgent.getInstance().getCommonVOs(BusiTypeVO.key, ret.keySet().toArray(new String[] {}));

			if (null != mapDjlx) {
				for (String key : mapDjlx.keySet()) {
					ret.get(key).setBusitypeVO((BusiTypeVO) mapDjlx.get(key));
				}
			}
		}
		return djlxvos;
	}

	/**
	 * 根据VO中所设定的条件返回所有符合条件的VO数组
	 * 
	 * 创建日期：(2001-8-30)
	 * 
	 * @return nc.vo.arap.djlx.DjLXVO[]
	 * @param djLXVO
	 *            nc.vo.arap.djlx.DjLXVO
	 * @param isAnd
	 *            boolean 以与条件查询还是以或条件查询
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public DjLXVO[] queryForTreeNode(DjLXVO condDjLXVO) throws BusinessException {

		return queryByVO(condDjLXVO);
	}

	public DjLXVO[] queryByWhereStr(String where) throws BusinessException {

		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("rawtypes")
        Collection cl = null;

		cl = dao.retrieveByClause(DjLXVO.class, where);

		DjLXVO[] djlxvos = (DjLXVO[]) changeCollection2Array(cl, DjLXVO.class);

		djlxvos = queryBusiTypes(djlxvos);

		return djlxvos;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
    private Object changeCollection2Array(Collection cl, Class sClass) {
		if (cl.isEmpty()) {
			return null;
		}
		Object[] o = (Object[]) Array.newInstance(sClass, cl.size());
		o = cl.toArray(o);
		return o;

	}
}