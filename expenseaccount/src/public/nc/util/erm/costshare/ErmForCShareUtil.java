package nc.util.erm.costshare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.extendconfig.ErmExtendconfigInterfaceCenter;
import nc.pubitf.erm.billcontrast.IErmBillcontrastQueryService;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
/**
 * 报销费用结转单工具类
 * @author chenshuaia
 *
 */
public class ErmForCShareUtil {

	/**
	 * 报销单冗余到费用结转单的字段
	 * @return
	 */
	public static String[] getFieldFromBxVo() {
		// 报销单冗余信息
		return new String[] { BXHeaderVO.TOTAL, BXHeaderVO.BZBM, BXHeaderVO.BBHL, BXHeaderVO.YBJE,
				BXHeaderVO.BBJE, BXHeaderVO.FYDWBM, BXHeaderVO.FYDWBM_V, BXHeaderVO.FYDEPTID,
				BXHeaderVO.FYDEPTID_V, BXHeaderVO.SZXMID, BXHeaderVO.JOBID, BXHeaderVO.CASHITEM,
				BXHeaderVO.DWBM, BXHeaderVO.DWBM_V, BXHeaderVO.JSFS, BXHeaderVO.DEPTID,
				BXHeaderVO.DEPTID_V, BXHeaderVO.JKBXR, BXHeaderVO.FKYHZH, BXHeaderVO.HBBM,
				BXHeaderVO.CASHPROJ, BXHeaderVO.DJBH, BXHeaderVO.PK_ORG, BXHeaderVO.PK_ORG_V,
				BXHeaderVO.ZY, BXHeaderVO.PK_GROUP , JKBXHeaderVO.ISEXPAMT, BXHeaderVO.GROUPBBHL,
				BXHeaderVO.GLOBALBBHL, BXHeaderVO.PK_CHECKELE, BXHeaderVO.PK_RESACOSTCENTER,
				BXHeaderVO.PROJECTTASK, BXHeaderVO.JSFS, BXHeaderVO.CUSTOMER,BXHeaderVO.GLOBALBBJE,BXHeaderVO.GROUPBBJE};
	}

	/**
	 * 报销单转换成费用结转单(报销单使用)
	 * @param bxvo 报销单VO
	 * @return
	 * @throws BusinessException
	 */
	public static AggCostShareVO convertFromBXVO(JKBXVO bxvo) throws BusinessException {
		AggCostShareVO result = null;
		//只有存在费用明细的报销单才可以转换
		if(bxvo == null || bxvo.getcShareDetailVo() == null || bxvo.getcShareDetailVo().length == 0){
			return result;
		}

		JKBXHeaderVO head = bxvo.getParentVO();

		result = new AggCostShareVO();

		CostShareVO shareVo = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class).queryCShareVOByBxVoHead(head, UFBoolean.TRUE);

		if(shareVo != null){
			shareVo.setHasntbcheck(UFBoolean.valueOf(bxvo.getHasNtbCheck()));
			result.setParentVO(getCShareVOFromBxHead(shareVo,head));
		}else{
			result.setParentVO(getCShareVOFromBxHead(null,head));
		}
		//预算校验标志设置
		((CostShareVO)result.getParentVO()).setHasntbcheck(UFBoolean.valueOf(bxvo.getHasNtbCheck()));

		result.setChildrenVO(dealWithCShareDetailsStatus(bxvo, bxvo.getcShareDetailVo()));
		
		// 补充扩展页签信息
		ErmExtendconfigInterfaceCenter.fillExtendTabVOs(head.getPk_group(), CostShareVO.PK_TRADETYPE, result);
		// 将bx单信息设置到结转单上备用
		result.setBxvo(bxvo);
		
		return result;
	}

	/**
	 * 报销单单表头转换成费用结转单（为费用结转单提供）
	 * @param head 报销单表头
	 * @return
	 * @throws BusinessException
	 */
	public static AggCostShareVO convertFromBxHead(JKBXHeaderVO head) throws BusinessException {
		AggCostShareVO result = new AggCostShareVO();
		//单据状态生效时才可以
		if(head == null || head.getDjzt() !=BXStatusConst.DJZT_Sign ){
			return result;
		}

		CostShareVO vo = getCShareVOFromBxHead(null,head);
		vo.setSrc_type(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL);
		result.setParentVO(vo);

		return result;
	}

	/**
	 * 处理分摊明细状态
	 * 使用情况：存在分摊明细的报销单的修改
	 * @param bxvo 报销VO
	 * @param bxDetails 为空时，取bxvo中的分摊信息
	 * @return 返回所有增删改的分摊明细数组
	 */
	public static CShareDetailVO[] dealWithCShareDetailsStatus(JKBXVO bxvo, CShareDetailVO[] bxDetails) {
		JKBXVO bxoldvo = bxvo.getBxoldvo();

		if(bxoldvo == null){//不存在旧报销Vo，则表示为新增
			return bxvo.getcShareDetailVo();
		}

		List<CShareDetailVO> resultList = new ArrayList<CShareDetailVO>();

		//修改前分摊信息
		CShareDetailVO[] childrenVO_old = bxoldvo.getcShareDetailVo();

		//修改后分摊信息
		CShareDetailVO[] childrenVO = null;

		if(bxDetails == null){
			childrenVO = bxvo.getcShareDetailVo();
		}else{
			childrenVO = bxDetails;
		}

		for (int i = 0; i < childrenVO.length; i++) {
			resultList.add(childrenVO[i]);
		}

		//修改前分摊信息放入Map中
		Map<String,CShareDetailVO> oldDetailMap=new HashMap<String, CShareDetailVO>();
		if(childrenVO_old!=null){
			for(CShareDetailVO vo:childrenVO_old){
				oldDetailMap.put(vo.getPrimaryKey(), vo);
			}
		}

		if(childrenVO!=null){
			for(CShareDetailVO child:childrenVO){
				if(StringUtils.isNullWithTrim(child.getPrimaryKey())){
					child.setStatus(VOStatus.NEW);
				}else if(oldDetailMap.containsKey(child.getPrimaryKey())){
					child.setStatus(VOStatus.UPDATED);
					oldDetailMap.remove(child.getPrimaryKey());
				}
			}
		}

		//将删除的分摊明细放入集合中
		Collection<CShareDetailVO> delCollection = oldDetailMap.values();
		for (Iterator<CShareDetailVO> iterator = delCollection.iterator(); iterator.hasNext();) {
			CShareDetailVO object = (CShareDetailVO) iterator.next().clone();
			object.setStatus(VOStatus.DELETED);
			resultList.add(object);
		}

		return resultList.toArray(new CShareDetailVO[]{});
	}

	/**
	 * 报销单转换成费用结转单(批量)
	 * @param bxvo 报销单VO
	 * @return
	 * @throws BusinessException
	 */
	public static AggCostShareVO[] convertFromBXVOS(BXVO[] bxvo) throws BusinessException {
		AggCostShareVO[] result = null;
		//只有存在费用明细的报销单才可以转换
		if(bxvo == null || bxvo.length == 0 ){
			return result;
		}
		result = new AggCostShareVO[bxvo.length];

		for(int i = 0; i < result.length; i ++){
			result[i] = convertFromBXVO(bxvo[i]);
		}

		return result;
	}


	/**
	 * 获取费用结转表头VO
	 * @param shareVo 如果不为null ，则会给改shareVo设置值，否则将new 出新CostShareVO
	 * @param head 报销head
	 * @return
	 * @throws ValidationException
	 */
	public static CostShareVO getCShareVOFromBxHead(CostShareVO shareVo ,JKBXHeaderVO head) throws ValidationException {
		if(head == null){
			return null;
		}

		//新增情况下
		CostShareVO cShareVo = null;

		if(shareVo == null){
			cShareVo = new CostShareVO();
		}else{
			cShareVo = shareVo;
		}
		cShareVo.setBxheadvo(head);
		//制单人
		cShareVo.setBillmaker(head.getCreator());
		cShareVo.setBilldate(head.getDjrq());

		//单据来源方式
		cShareVo.setSrc_type(IErmCostShareConst.CostShare_Bill_SCRTYPE_BX);
		cShareVo.setSrc_id(head.getPk_jkbx());

		// 报销信息
		cShareVo.setBx_group(head.getPk_group());
		cShareVo.setBx_org(head.getPk_org());
		cShareVo.setBx_fiorg(head.getPk_fiorg());
		cShareVo.setBx_pcorg(head.getPk_pcorg());
		cShareVo.setBx_djrq(head.getDjrq());//单据日期
		cShareVo.setDjlxbm(head.getDjlxbm());
		cShareVo.setJkbxr(head.getJkbxr());
		//增加产品线和品牌字段
		cShareVo.setPk_brand(head.getPk_brand());
		cShareVo.setPk_proline(head.getPk_proline());
		
		//设置报销单冗余字段
		String[] fields = getFieldFromBxVo();
		for (String attr : fields) {
			cShareVo.setAttributeValue(attr, head.getAttributeValue(attr));
		}
		
		//设置报销单自定义项冗余到结转单，30个自定义项
		for (int i = 1; i <= 30; i++) {
			cShareVo.setAttributeValue("defitem"+i, head.getAttributeValue("zyx"+i));
		}

		//单据类型(这里默认设置，后期初初始设置对照完成后，查对照表来控制)

		String pk_tradeType = getCostShareBillTypeByBxVo(head);

		if(pk_tradeType == null){
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0117")/*@res "该报销单交易类型未设置对应的费用结转交易类型"*/);
		}

		cShareVo.setPk_tradetype(pk_tradeType);
		cShareVo.setPk_billtype("265X");

		return cShareVo;
	}

	/**
	 * 根据报销单获取对应的费用结转单交易类型
	 * @param head
	 * @return 交易类型
	 */
	public static String getCostShareBillTypeByBxVo(JKBXHeaderVO head){

		String pk_tradeType = null;
		try {
			IErmBillcontrastQueryService tradeTypeService = NCLocator.getInstance().lookup(IErmBillcontrastQueryService.class);
			pk_tradeType = tradeTypeService.queryDesTradetypeBySrc(head.getPk_org(), head.getDjlxbm());
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

		return pk_tradeType;
	}

	/**
	 * 报销VO中是否有分摊信息
	 * @param bxvo
	 * @return
	 */
	public static boolean isHasCShare(JKBXVO bxvo){
		if(bxvo == null || bxvo.getcShareDetailVo() == null || bxvo.getcShareDetailVo().length == 0){
			return false;
		}

		return true;
	}

	/**
	 * 判断UFDouble是否大于0
	 * @param uf
	 * @return true表示不为大于0
	 */
	public static boolean isUFDoubleGreaterThanZero(UFDouble uf){
		if(uf == null || uf.compareTo(UFDouble.ZERO_DBL) <= 0){
			return false;
		}
		return true;
	}

	/**
	 * 格式化金额
	 * @param uf
	 * @param power 精度，小于0时不计算
	 */
	public static UFDouble formatUFDouble(UFDouble uf, int power){
		uf = (uf == null) ? UFDouble.ZERO_DBL :uf;
		if(power > 0){
			uf = uf.setScale(power, UFDouble.ROUND_HALF_UP);
		}

		return uf;
	}
}