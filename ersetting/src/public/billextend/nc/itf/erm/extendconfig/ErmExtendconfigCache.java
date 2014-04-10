package nc.itf.erm.extendconfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.pf.pub.PfDataCache;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.extendconfig.ErmExtendConfigVO;
import nc.vo.pub.BusinessException;
import nc.vo.trade.summarize.Hashlize;
import nc.vo.trade.summarize.IHashKey;


/**
 * MaExtendconfig����
 * 
 * @author lvhj
 *
 */
public class ErmExtendconfigCache {
	
	private ErmExtendconfigCache() {
		super();
	}
	
	private static ErmExtendconfigCache instance = new ErmExtendconfigCache();
	
	/**
	 * @return the instance
	 */
	public static ErmExtendconfigCache getInstance() {
		return instance;
	}
	
	/**
	 * ���ݼ���+���õ��ݽ������ͱ��룬�����չ��Ϣ
	 * 
	 * @param pk_group
	 * @param pk_tradetype
	 * @return
	 * @throws BusinessException
	 */
	public ErmExtendConfigVO[] getErmExtendConfigVOs(String pk_group,String pk_tradetype) throws BusinessException{
		
		DjLXVO djlx = ErmDjlxCache.getInstance().getDjlxVO(pk_group, pk_tradetype);
		if(djlx == null){
			return null;
		}
		StringBuffer wherePart = new StringBuffer();
		String pk_billtype = PfDataCache.getBillTypeInfo(pk_group,djlx.getDjlxbm()).getParentbilltype();
		// �����������͹���
		wherePart.append(ErmExtendConfigVO.PK_BILLTYPE+"='"+pk_billtype+"'");
		// �����������͹��ˣ���~����ʶȫ��
		wherePart.append(" and ("+ErmExtendConfigVO.PK_TRADETYPE +" = '~' or "+ErmExtendConfigVO.PK_TRADETYPE +" = '"+pk_tradetype+"')");
		// �������͵�ҵ�����͹���,0��ʶȫ��
		wherePart.append(" and ("+ErmExtendConfigVO.BUSITYPE +" = 0 ");
		Integer matype = getDjlxBusiType(djlx);
		if(matype != null){
			wherePart.append(" or "+ErmExtendConfigVO.BUSITYPE +" = "+matype);
		}
		wherePart.append(")");
		return CacheUtil.getValueFromCacheByWherePart(ErmExtendConfigVO.class, wherePart.toString(),false);
	}
	
	/**
	 * ��þ��彻�����͵�ҵ������
	 * 
	 * @param djlx
	 * @return
	 */
	private Integer getDjlxBusiType(DjLXVO djlx){
		if(djlx.getDjdl().equals(ErmBillConst.MatterApp_DJDL)){
			return djlx.getMatype();
		}
		return null;
	}
	
	/**
	 * ���ݼ���+���õ��ݽ������ͱ��룬������ѯ��չ��Ϣ
	 * 
	 * @param pk_group
	 * @param pk_tradetypes
	 * @return ma_tradetype�������չ������Ϣ
	 * @throws BusinessException
	 */
	
	@SuppressWarnings("unchecked")
	public Map<String,List<ErmExtendConfigVO>> getMaExtendxaconfigVOs(String pk_group,String[] pk_tradetypes) throws BusinessException{
		Map<String,List<ErmExtendConfigVO>> res = new HashMap<String,List<ErmExtendConfigVO>>();
		Map<String, DjLXVO> djlxmap = ErmDjlxCache.getInstance().getDjlxVOs(pk_group, pk_tradetypes);
		if(djlxmap.isEmpty()){
			return res;
		}
		StringBuffer wherePart = new StringBuffer();
		int i = 0;
		for (Entry<String, DjLXVO> djlx : djlxmap.entrySet()) {
			if(i != 0){
				wherePart.append(" or ");
			}
			wherePart.append(" (");
			String pk_billtype =PfDataCache.getBillTypeInfo(pk_group,djlx.getKey()).getParentbilltype();
			// �����������͹���
			wherePart.append(ErmExtendConfigVO.PK_BILLTYPE+"='"+pk_billtype+"'");
			wherePart.append(" and ("+ErmExtendConfigVO.PK_TRADETYPE +" = '~' or "+ErmExtendConfigVO.PK_TRADETYPE +" = '"+djlx.getKey()+"')");
			wherePart.append(" and ("+ErmExtendConfigVO.BUSITYPE +" = 0 " );
			Integer busitype = getDjlxBusiType(djlx.getValue());
			if(busitype != null){
				wherePart.append(" or "+ErmExtendConfigVO.BUSITYPE +" = "+busitype);
			}
			wherePart.append(") )");
			i++;
		}
		ErmExtendConfigVO[] vos = CacheUtil.getValueFromCacheByWherePart(ErmExtendConfigVO.class, wherePart.toString(),false);
		
		if(vos != null && vos.length >0){
			res = Hashlize.hashlizeVOs(vos, new IHashKey() {
				
				@Override
				public String getKey(Object o) {
					return ((ErmExtendConfigVO)o).getPk_tradetype();
				}
			});
		}
		return res;
	}

}
