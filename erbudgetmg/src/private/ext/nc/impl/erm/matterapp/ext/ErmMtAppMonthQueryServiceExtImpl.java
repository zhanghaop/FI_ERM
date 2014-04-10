package nc.impl.erm.matterapp.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.itf.erm.matterapp.ext.IErmMtAppMonthQueryServiceExt;
import nc.pubitf.erm.extendtab.IErmExtendtabQueryService;
import nc.vo.erm.matterapp.ext.MtappMonthExtVO;
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
public class ErmMtAppMonthQueryServiceExtImpl implements
		IErmMtAppMonthQueryServiceExt,IErmExtendtabQueryService {

	@SuppressWarnings("unchecked")
	@Override
	public MtappMonthExtVO[] queryMonthVOs(String pk_mtapp_bill)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("rawtypes")
		Collection res = dao.retrieveByClause(MtappMonthExtVO.class,
				MtappMonthExtVO.PK_MTAPP_BILL + " = '" + pk_mtapp_bill + "'");
		return (MtappMonthExtVO[]) res.toArray(new MtappMonthExtVO[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,List<MtappMonthExtVO>> queryMonthVOs(String[] mtapp_billPks)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection<MtappMonthExtVO> res = dao.retrieveByClause(MtappMonthExtVO.class, SqlUtils
				.getInStr(MtappMonthExtVO.PK_MTAPP_BILL, mtapp_billPks, true));
		
		Map<String,List<MtappMonthExtVO>> map = new HashMap<String, List<MtappMonthExtVO>>();
		for (MtappMonthExtVO vo : res) {
			List<MtappMonthExtVO> list = map.get(vo.getPk_mtapp_bill());
			if(list == null){
				list = new ArrayList<MtappMonthExtVO>();
				map.put(vo.getPk_mtapp_bill(), list);
			}
			list.add(vo);
		}
		return map;
	}

	@Override
	public CircularlyAccessibleValueObject[] queryByMaPK(String pk_mtapp_bill)
			throws BusinessException {
		return queryMonthVOs(pk_mtapp_bill);
	}

	@Override
	public Map<String, CircularlyAccessibleValueObject[]> queryByMaPKs(
			String[] mtapp_billPks) throws BusinessException {
		Map<String, List<MtappMonthExtVO>> listmap = queryMonthVOs(mtapp_billPks);
		Map<String, CircularlyAccessibleValueObject[]> res = new HashMap<String, CircularlyAccessibleValueObject[]>();
		for (Entry<String, List<MtappMonthExtVO>> entry : listmap.entrySet()) {
			res.put(entry.getKey(), entry.getValue().toArray(new MtappMonthExtVO[0]));
		}
		return res;
	}

}
