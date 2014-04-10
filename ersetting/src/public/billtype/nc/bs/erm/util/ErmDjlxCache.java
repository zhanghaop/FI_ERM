package nc.bs.erm.util;

import java.util.HashMap;
import java.util.Map;

import nc.vo.er.djlx.DjLXVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;

/**
 * er_djlx����
 * 
 * @author lvhj
 *
 */
public class ErmDjlxCache {
	
	private ErmDjlxCache() {
		super();
	}
	
	private static ErmDjlxCache instance = new ErmDjlxCache();
	
	/**
	 * @return the instance
	 */
	public static ErmDjlxCache getInstance() {
		return instance;
	}
	
	/**
	 * ���ݼ���+��������code����ѯer_djlx
	 * 
	 * @param pk_group
	 * @param pk_tradetype
	 * @return
	 * @throws BusinessException
	 */
	public DjLXVO getDjlxVO(String pk_group,String pk_tradetype) throws BusinessException{
		DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, "pk_group = '"+pk_group+"' and djlxbm = '"+pk_tradetype+"'");
		if(vos == null || vos.length ==0){
			return null;
		}
		// ����+�������ͣ�����Ψһȷ��һ����������
		return vos[0];
	}
	
	/**
	 * ���ݼ���+��������code��������ѯer_djlx
	 * 
	 * @param pk_group
	 * @param tradetypePks
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, DjLXVO> getDjlxVOs(String pk_group,String[] tradetypePks) throws BusinessException{
		Map<String, DjLXVO> res = new HashMap<String, DjLXVO>();
		String where =  "pk_group = '"+pk_group+"' and "+SqlUtils.getInStr("djlxbm", tradetypePks, true);
		DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, where);
		if(vos != null && vos.length >0){
			// һ�������£��������Ͳ����ظ�
			for (int i = 0; i < vos.length; i++) {
				res.put(vos[i].getDjlxbm(), vos[i]);
			}
		}
		return res;
	}

	/**
	 * ��ǰ���������Ƿ�Ϊָ���������͵Ľ�������
	 * 
	 * @param pk_group
	 * @param pk_tradetype
	 * @param bxtype
	 * @return
	 * @throws BusinessException
	 */
	public boolean isNeedBxtype(String pk_group,String pk_tradetype,int bxtype) throws BusinessException{
		DjLXVO djlxvo = getDjlxVO(pk_group, pk_tradetype);
		return isNeedBxtype(djlxvo, bxtype);
	}
	
	public boolean isNeedBxtype(DjLXVO djlxvo,int bxtype){
		return djlxvo != null && djlxvo.getBxtype() != null && djlxvo.getBxtype() == bxtype;
	}
	
}
