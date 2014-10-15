package nc.impl.erm.costshare.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.itf.erm.costshare.ext.IErmCsMonthQueryServiceExt;
import nc.pubitf.erm.extendtab.IErmExtendtabQueryService;
import nc.vo.erm.costshare.ext.CShareMonthVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * 费用申请单，分期均摊记录查询服务实现
 * 
 * 合生元项目专用
 * 
 * @author lvhj
 * 
 */
public class ErmCsMonthQueryServiceExtImpl implements
		IErmCsMonthQueryServiceExt,IErmExtendtabQueryService {

	@SuppressWarnings("unchecked")
	@Override
	public CShareMonthVO[] queryMonthVOs(String pk_costshare)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("rawtypes")
		Collection res = dao.retrieveByClause(CShareMonthVO.class,
				CShareMonthVO.PK_COSTSHARE+ " = '" + pk_costshare + "'");
		return (CShareMonthVO[]) res.toArray(new CShareMonthVO[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,List<CShareMonthVO>> queryMonthVOs(String[] costsharePks)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection<CShareMonthVO> res = dao.retrieveByClause(CShareMonthVO.class, SqlUtils
				.getInStr(CShareMonthVO.PK_COSTSHARE, costsharePks, true));
		
		Map<String,List<CShareMonthVO>> map = new HashMap<String, List<CShareMonthVO>>();
		for (CShareMonthVO vo : res) {
			List<CShareMonthVO> list = map.get(vo.getPk_costshare());
			if(list == null){
				list = new ArrayList<CShareMonthVO>();
				map.put(vo.getPk_costshare(), list);
			}
			list.add(vo);
		}
		return map;
	}
	

	@Override
	public CircularlyAccessibleValueObject[] queryByMaPK(String pk_costshare)
			throws BusinessException {
		return queryMonthVOs(pk_costshare);
	}

	@Override
	public Map<String, CircularlyAccessibleValueObject[]> queryByMaPKs(
			String[] costsharePks) throws BusinessException {
		Map<String, List<CShareMonthVO>> listmap = queryMonthVOs(costsharePks);
		Map<String, CircularlyAccessibleValueObject[]> res = new HashMap<String, CircularlyAccessibleValueObject[]>();
		for (Entry<String, List<CShareMonthVO>> entry : listmap.entrySet()) {
			res.put(entry.getKey(), entry.getValue().toArray(new CShareMonthVO[0]));
		}
		return res;
	}

}
