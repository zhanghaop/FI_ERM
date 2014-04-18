package nc.bs.erm.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.erm.common.ErmConst;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.org.IOrgConst;
import nc.itf.uap.IUAPQueryBS;
import nc.vo.cache.CacheManager;
import nc.vo.cache.ICache;
import nc.vo.cache.config.CacheConfig;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.trade.summarize.Hashlize;
import nc.vo.trade.summarize.IHashKey;
import nc.vo.util.SqlWhereUtil;

/**
 * �����ֶζ������ݻ���
 */
public class ErmBillFieldContrastCache {

	private ErmBillFieldContrastCache() {
	}

	/**
	 * ����map��key�ָ���
	 */
	public static final String KEY_SPLIT_CHARS = "_";
	/**
	 * Ԥ���ֶζ��ճ���
	 */
	public static final Integer FieldContrast_SCENE_BudGetField = 1;
	/**
	 * ������ת�����ճ���
	 */
	public static final Integer FieldContrast_SCENE_ExpenseAccount = 2;
	/**
	 * �������뵥����ά�ȶ��ճ���
	 */
	public static final Integer FieldContrast_SCENE_MatterAppCtrlField = 4;

	/**
	 * ��̯�����ֶζ���
	 */
	public static final Integer FieldContrast_SCENE_SHARERULEField = 5;

	/**
	 * ��õ�ǰ����Դ
	 *
	 * @return
	 */
	private static String getCurrentDs() {
		return InvocationInfoProxy.getInstance().getUserDataSource();
	}

	private static String fileRegion = "ErmBillFieldContrast";

    private static int INTERVAL_SERVER = 1000 * 60 * 10;

    private static int INTERVAL_CLIENT = 1000 * 60 * 60 * 24;

	private static ICache getFieldContrastCache() {
		CacheConfig config = new CacheConfig();
		if (RuntimeEnv.getInstance().isRunningInServer()) {
			config.setCacheType(CacheConfig.CacheType.MEMORY);
            config.setFlushInterval(INTERVAL_SERVER);
		} else {
			config.setCacheType(CacheConfig.CacheType.DYNAMIC_FILE);
            config.setFlushInterval(INTERVAL_CLIENT);
		}
		config.setRegionName(fileRegion);
		return CacheManager.getInstance().getCache(config);
	}

	private static final String FieldContrast_SrcFieldKey = "SrcFieldKey_";
	private static final String FieldContrast_DesFieldKey = "DesFieldKey_";
	/**
	 * ��ϸ�ֶζ��յ�key����
	 */
    // private static final String[] FieldContrast_KeyFields = new String[] {
    // FieldcontrastVO.SRC_BILLTYPE, FieldcontrastVO.DES_BILLTYPE,
    // FieldcontrastVO.PK_ORG, FieldcontrastVO.PK_GROUP };

	/**
	 * ��û���keyֵ
	 *
	 * @param qryVO
	 * @param qryFields
	 * @param cacheName
	 * @return
	 */
	private static String getCacheKey(FieldcontrastVO qryVO) {
		StringBuffer bf = new StringBuffer();
		bf.append(getCurrentDs());

		Integer app_scene = qryVO.getApp_scene();
		if (app_scene == null) {
			ExceptionHandler.consume(ExceptionHandler
					.createException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0021")/*@res "��ѯ�ֶζ��գ�Ӧ�ó�������Ϊ�գ�"*/));
		}
		bf.append(KEY_SPLIT_CHARS);
		bf.append(app_scene);
		
		bf.append(KEY_SPLIT_CHARS);
		bf.append(filterNullValue(FieldcontrastVO.SRC_BILLTYPE, qryVO.getSrc_billtype()));
		bf.append(KEY_SPLIT_CHARS);
		bf.append(filterNullValue(FieldcontrastVO.DES_BILLTYPE, qryVO.getDes_billtype()));
		bf.append(KEY_SPLIT_CHARS);
		bf.append(filterNullValue(FieldcontrastVO.PK_ORG, qryVO.getPk_org()));
		bf.append(KEY_SPLIT_CHARS);
		bf.append(filterNullValue(FieldcontrastVO.PK_GROUP, qryVO.getPk_group()));

//		for (int i = 0; i < FieldContrast_KeyFields.length; i++) {
//			bf.append(KEY_SPLIT_CHARS);
//			
//			String field = FieldContrast_KeyFields[i];
//			Object value = qryVO.getAttributeValue(field);
//			value = filterNullValue(field,
//					value == null ? null : value.toString());
//			bf.append(value);
//			qryVO.setAttributeValue(field, value);
//		}
		return bf.toString();
	}

	private static String filterNullValue(String field, String value) {
		if (value == null) {
			if ("pk_org".equals(field)||"pk_group".equals(field)) {
				// Ĭ����֯Ϊȫ��
				value = IOrgConst.GLOBEORG;
			} else {
				value = "~";
			}
		}
		return value;
	}

	/**
	 * ��ѯ�����Դ���ݵ��ֶ�
	 *
	 * @param qryVO
	 * @return
	 */
	public static String getSrcField(FieldcontrastVO qryVO) {
		if(qryVO.getPk_org() == null){
			qryVO.setPk_org(IOrgConst.GLOBEORG);
		}
		
		if(qryVO.getPk_group() == null){
			qryVO.setPk_group(IOrgConst.GLOBEORG);
		}
		Map<String, String> map = getFieldcontrastMap(qryVO);
		return map.get(getDesFieldKey(qryVO));
	}


	/**
	 * ��ѯ�����ϸ�ֶζ���map
	 *
	 * @param qryVO
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> getFieldcontrastMap(FieldcontrastVO qryVO){
		String cacheKey = getCacheKey(qryVO);
		Map<String, String> res = (Map<String, String>) getFieldContrastCache().get(cacheKey);
        if (res == null || res.isEmpty()) {
			synchronized(cacheKey){
				res = (Map<String, String>) getFieldContrastCache().get(cacheKey);
                if (res == null || res.isEmpty()) {
					res = initCache(qryVO, cacheKey);
				}
			}
		}
		return res;
	}

	private static Map<String, String> initCache(FieldcontrastVO qryVO,
			String cacheKey) {
		Map<String, String> res = new HashMap<String, String>();
		// ��ѯ����
		Collection<FieldcontrastVO> vos = qryVOs(qryVO);
		// ���ػ���
		if (vos != null && !vos.isEmpty()) {
			for (FieldcontrastVO fieldcontrastVO : vos) {
				String src_fieldcode = fieldcontrastVO.getSrc_fieldcode();
				String des_fieldcode = fieldcontrastVO.getDes_fieldcode();
				// ��Դ�����ֶ� ���� Ŀ�굥���ֶΣ�֧��һ����Դ�ֶζ�Ӧ���Ŀ���ֶΣ���des_fieldcode1,des_fieldcode2��ʽ�洢
				String srcFieldKey = getSrcFieldKey(fieldcontrastVO);
				String srcValue = res.get(srcFieldKey);
				if(srcValue == null){
					srcValue = des_fieldcode;
				}else{
					srcValue +=","+des_fieldcode;
				}
				res.put(srcFieldKey, srcValue);
				// Ŀ�굥���ֶ� ���� ��Դ�����ֶΣ���֧��һ��Ŀ���ֶζ�Ӧ�����Դ�ֶ�
				String desFieldKey = getDesFieldKey(fieldcontrastVO);
				if(res.containsKey(desFieldKey)){
					ExceptionHandler.consume(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0022")/*@res "�ֶζ��ղ�֧��һ��Ŀ���ֶζ�Ӧ�����Դ�ֶ�,�ظ�keyֵΪ"*/+desFieldKey));
				}else{
					res.put(desFieldKey, src_fieldcode);
				}
			}
		}
		getFieldContrastCache().put(cacheKey, res);

		return res;
	}

	private static String getSrcFieldKey(FieldcontrastVO qryVO) {
		return qryVO.getSrc_busitype() + "_" + FieldContrast_SrcFieldKey
				+ qryVO.getSrc_fieldcode();
	}

	private static String getDesFieldKey(FieldcontrastVO qryVO) {
		return qryVO.getSrc_busitype() + "_" + FieldContrast_DesFieldKey
				+ qryVO.getDes_fieldcode();
	}
	private static String getDesFieldKey(String src_busitype,String des_fieldcode) {
		return src_busitype + "_" + FieldContrast_DesFieldKey
		+ des_fieldcode;
	}

	/**
	 * ���� Ӧ�ó���+��֯+����+��Դ��������+Ŀ�ĵ������ͣ����в�ѯ�ֶζ���
	 *
	 * @param qryVO
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Collection<FieldcontrastVO> qryVOs(FieldcontrastVO qryVO) {
		IUAPQueryBS uapQry = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		Integer app_scene = qryVO.getApp_scene();
		if (app_scene == null) {
			ExceptionHandler.consume(ExceptionHandler
					.createException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0021")/*@res "��ѯ�ֶζ��գ�Ӧ�ó�������Ϊ�գ�"*/));
		}
		SqlWhereUtil util = new SqlWhereUtil(FieldcontrastVO.APP_SCENE + "="
				+ app_scene);
		// ��ѯ��֯Ϊ ����֯+����+ȫ��
		util.and(getOrgSql(qryVO.getPk_org(),qryVO.getPk_group()));

		String src_billtype = qryVO.getSrc_billtype();
		String des_billtype = qryVO.getDes_billtype();
		String src_parentBilltype = getParentBilltype(src_billtype);
		String des_parentBilltype = getParentBilltype(des_billtype);

		util.and(getBillltypesql(FieldcontrastVO.SRC_BILLTYPE, src_billtype,
				src_parentBilltype));
		util.and(getBillltypesql(FieldcontrastVO.DES_BILLTYPE, des_billtype,
				des_parentBilltype));

		Collection<FieldcontrastVO> fldContrasts = null;
		try {
			fldContrasts = uapQry.retrieveByClause(FieldcontrastVO.class,
					util.getSQLWhere());
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		if (fldContrasts == null || fldContrasts.isEmpty()) {
			return null;
		}
		// �ϲ���ȥ���ظ����չ�ϵ
		List<FieldcontrastVO> res = getQryResult(qryVO, src_parentBilltype,
				des_parentBilltype, fldContrasts);

		return res;
	}

	private static String getOrgSql(String pk_org, String pk_group) {
		String sql = "(" + FieldcontrastVO.PK_ORG + "='" + IOrgConst.GLOBEORG
				+ "'";
		if (!IOrgConst.GLOBEORG.equals(pk_org)) {
			sql += " or ( "
					+ FieldcontrastVO.PK_GROUP
					+ " = '"
					+ pk_group
					+ "' and "
					+ FieldcontrastVO.PK_ORG
					+ " in ("
					+ (pk_org.equals(pk_group) ? "'" + pk_org + "'" : "'"
							+ pk_org + "','" + pk_group + "'") + "))";
		}
		sql += ")";
		return sql;
	}

	@SuppressWarnings("unchecked")
	private static List<FieldcontrastVO> getQryResult(FieldcontrastVO qryVO,
			String src_parentBilltype, String des_parentBilltype,
			Collection<FieldcontrastVO> fldContrasts) {
		// ȥ���ظ�
		HashMap<String, ArrayList<FieldcontrastVO>> map = Hashlize.hashlizeObjects(
				fldContrasts.toArray(new FieldcontrastVO[0]), new IHashKey() {

					@Override
					public String getKey(Object o) {
						FieldcontrastVO vo = (FieldcontrastVO) o;
						return getFieldContrastKey(vo.getPk_org(),
								vo.getSrc_billtype(), vo.getDes_billtype());
					}
				});

		List<FieldcontrastVO> res = new ArrayList<FieldcontrastVO>();
		List<String> srcFields = new ArrayList<String>();

		String src_billtype = qryVO.getSrc_billtype();
		String des_billtype = qryVO.getDes_billtype();
		String pk_org = qryVO.getPk_org();
		String pk_group = qryVO.getPk_group();
		// �������ԵĽ��кϲ�������ԭ��Ϊ���������������ȣ���֯������
		ArrayList<FieldcontrastVO> list = map
				.get(getFieldContrastKey(pk_org, src_billtype, des_billtype));
		if (list != null && !list.isEmpty()) {
			for (FieldcontrastVO vo : list) {
				res.add(vo);
				srcFields.add(vo.getSrc_busitype() + "_"
						+ vo.getSrc_fieldcode());
			}
		}
		// pk_org,src_billtype, des_parentBilltype
		list = map.get(getFieldContrastKey(pk_org,
				src_billtype, des_parentBilltype));
		combiDatas(list, res, srcFields);
		// pk_org,src_parentBilltype, des_billtype
		list = map.get(getFieldContrastKey(pk_org,
				src_parentBilltype, des_billtype));
		combiDatas(list, res, srcFields);
		// pk_org,src_parentBilltype, des_parentBilltype
		list = map.get(getFieldContrastKey(pk_org,
				src_parentBilltype, des_parentBilltype));
		combiDatas(list, res, srcFields);

		if(!IOrgConst.GLOBEORG.equals(pk_org)){
			if(!pk_org.equals(pk_group)){
				// pk_group,src_billtype, des_billtype
				list = map.get(getFieldContrastKey(
						pk_group, src_billtype, des_billtype));
				combiDatas(list, res, srcFields);
				// pk_group,src_billtype, des_parentBilltype
				list = map.get(getFieldContrastKey(
						pk_group, src_billtype, des_parentBilltype));
				combiDatas(list, res, srcFields);
				// pk_group,src_parentBilltype, des_billtype
				list = map.get(getFieldContrastKey(
						pk_group, src_parentBilltype, des_billtype));
				combiDatas(list, res, srcFields);
				// pk_group,src_parentBilltype, des_parentBilltype
				list = map.get(getFieldContrastKey(
						pk_group, src_parentBilltype, des_parentBilltype));
				combiDatas(list, res, srcFields);
			}
			// globel,src_billtype, des_billtype
			list = map.get(getFieldContrastKey(
					IOrgConst.GLOBEORG, src_billtype, des_billtype));
			combiDatas(list, res, srcFields);
			// globel,src_billtype, des_parentBilltype
			list = map.get(getFieldContrastKey(
					IOrgConst.GLOBEORG, src_billtype, des_parentBilltype));
			combiDatas(list, res, srcFields);
			// globel,src_parentBilltype, des_billtype
			list = map.get(getFieldContrastKey(
					IOrgConst.GLOBEORG, src_parentBilltype, des_billtype));
			combiDatas(list, res, srcFields);
			// globel,src_parentBilltype, des_parentBilltype
			list = map.get(getFieldContrastKey(
					IOrgConst.GLOBEORG, src_parentBilltype, des_parentBilltype));
			combiDatas(list, res, srcFields);
		}

		return res;
	}

	private static String getFieldContrastKey(String pk_org,
			String src_billtype, String des_billtype) {
		return pk_org + "," + FieldContrast_SrcFieldKey + src_billtype + ","
				+ FieldContrast_DesFieldKey + des_billtype;
	}

	private static void combiDatas(List<FieldcontrastVO> list,
			List<FieldcontrastVO> res, List<String> srcFields) {
		if (list != null && !list.isEmpty()) {
			for (FieldcontrastVO vo : list) {
				if (srcFields.contains(vo.getSrc_busitype() + "_"+vo.getSrc_fieldcode())) {
					continue;
				} else {
					res.add(vo);
					srcFields.add(vo.getSrc_busitype() + "_"
							+ vo.getSrc_fieldcode());
				}
			}
		}
	}

	private static String getParentBilltype(String billtypePK) {
		if (!"~".equals(billtypePK)) {
			BilltypeVO vo = PfDataCache.getBillType(billtypePK);
			if (vo != null) {
				return vo.getParentbilltype();
			}
		}
		return null;
	}

	private static String getBillltypesql(String billtypeField,
			String billtypePK, String parentBilltypePK) {
        if (StringUtil.isEmpty(billtypePK)) {
            billtypePK = "~";
        }
		String sql = "(" + billtypeField + " = '" + billtypePK + "'";
		if (!StringUtil.isEmpty(parentBilltypePK)) {
			sql += " or " + billtypeField + " = '" + parentBilltypePK + "'";
		}
		sql += ")";
		return sql;
	}

	/**
	 * ��ö���Ŀ���ֶεĵ����ֶ�
	 *
	 * @param appscene
	 *            Ӧ�ó���
	 * @param src_billtype
	 *            ҵ�񵥾�
	 * @param des_fieldcode
	 *            ��ӦĿ���ֶ�
	 * @return
	 */
	public static String getSrcField(Integer appscene, String src_billtype,
			String des_fieldcode) {
		return getSrcField(appscene, src_billtype, ErmConst.NULL_VALUE,
				des_fieldcode);
	}

	/**
	 * ��ö���Ŀ���ֶεĵ����ֶ�
	 *
	 * @param appscene
	 *            Ӧ�ó���
	 * @param src_billtype
	 *            ҵ�񵥾�
	 * @param des_billtype
	 *            Ŀ�굥��
	 * @param des_fieldcode
	 *            ��ӦĿ�굥���ֶ�
	 * @return
	 */
	public static String getSrcField(Integer appscene, String src_billtype,
			String des_billtype, String des_fieldcode) {

		FieldcontrastVO qryVO = new FieldcontrastVO();
		qryVO.setApp_scene(appscene);
		qryVO.setSrc_billtype(src_billtype);
		qryVO.setDes_billtype(des_billtype);
		qryVO.setDes_fieldcode(des_fieldcode);

		return getSrcField(qryVO);
	}

	/**
	 * �滻str�е��ֶ�ΪĿ�굥�����͵��ֶ���Ϣ
	 *
	 * @param str
	 *            ��ת���ֶ�
	 * @param appscene
	 *            Ӧ�ó���
	 * @param src_billtype
	 *            ��Դ��������
	 * @return
	 */
	public static String getConvertStr(String str, Integer appscene,
			String src_billtype) {
		return getConvertStr(str, appscene, src_billtype, ErmConst.NULL_VALUE);
	}

	/**
	 * �滻str�е��ֶ�ΪĿ�굥�����͵��ֶ���Ϣ
	 *
	 * @param str
	 *            ��ת���ֶ�
	 * @param appscene
	 *            Ӧ�ó���
	 * @param src_billtype
	 *            ��Դ��������
	 * @param des_billtype
	 *            Ŀ�ĵ�������
	 * @return
	 */
	public static String getConvertStr(String str, Integer appscene,
			String src_billtype, String des_billtype) {
		FieldcontrastVO qryVO = new FieldcontrastVO();
		qryVO.setApp_scene(appscene);
		qryVO.setSrc_billtype(src_billtype);
		qryVO.setDes_billtype(des_billtype);
		Map<String, String> map = getFieldcontrastMap(qryVO);
		//��Դ�ֶζ���ǰ׺
		String srcprefix = qryVO.getSrc_busitype() + "_" + FieldContrast_SrcFieldKey;
		// �����ֶζ�����Ϣ������滻��Ӧ�ֶ�
		for (Entry<String, String> fieldcontrast : map.entrySet()) {
			String key = fieldcontrast.getKey();
			if (key.startsWith(srcprefix)) {
				String[] des_fieldcodes = fieldcontrast.getValue().split(",");
				for (String des_fieldcode : des_fieldcodes) {
					//���Ŀ���ֶκ󣬷�����Ŀ���ֶζ�Ӧ����Դ�ֶΣ��ҽ����滻�ַ����еĶ�Ӧ����
					String src_fieldcode = map.get(getDesFieldKey(qryVO.getSrc_busitype(),des_fieldcode));
					str = StringUtil.replaceAllString(str, des_fieldcode,
							src_fieldcode);
				}
			}
		}
		return str;
	}

	/**
	 * ��ջ���
	 *
	 * @param qryVO
	 */
	public static void clearCache() {
		// ��ջ���
		getFieldContrastCache().flush();
	}
}