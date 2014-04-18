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
 * �������ý�ת��������
 * @author chenshuaia
 *
 */
public class ErmForCShareUtil {

	/**
	 * ���������ൽ���ý�ת�����ֶ�
	 * @return
	 */
	public static String[] getFieldFromBxVo() {
		// ������������Ϣ
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
	 * ������ת���ɷ��ý�ת��(������ʹ��)
	 * @param bxvo ������VO
	 * @return
	 * @throws BusinessException
	 */
	public static AggCostShareVO convertFromBXVO(JKBXVO bxvo) throws BusinessException {
		AggCostShareVO result = null;
		//ֻ�д��ڷ�����ϸ�ı������ſ���ת��
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
		//Ԥ��У���־����
		((CostShareVO)result.getParentVO()).setHasntbcheck(UFBoolean.valueOf(bxvo.getHasNtbCheck()));

		result.setChildrenVO(dealWithCShareDetailsStatus(bxvo, bxvo.getcShareDetailVo()));
		
		// ������չҳǩ��Ϣ
		ErmExtendconfigInterfaceCenter.fillExtendTabVOs(head.getPk_group(), CostShareVO.PK_TRADETYPE, result);
		// ��bx����Ϣ���õ���ת���ϱ���
		result.setBxvo(bxvo);
		
		return result;
	}

	/**
	 * ����������ͷת���ɷ��ý�ת����Ϊ���ý�ת���ṩ��
	 * @param head ��������ͷ
	 * @return
	 * @throws BusinessException
	 */
	public static AggCostShareVO convertFromBxHead(JKBXHeaderVO head) throws BusinessException {
		AggCostShareVO result = new AggCostShareVO();
		//����״̬��Чʱ�ſ���
		if(head == null || head.getDjzt() !=BXStatusConst.DJZT_Sign ){
			return result;
		}

		CostShareVO vo = getCShareVOFromBxHead(null,head);
		vo.setSrc_type(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL);
		result.setParentVO(vo);

		return result;
	}

	/**
	 * �����̯��ϸ״̬
	 * ʹ����������ڷ�̯��ϸ�ı��������޸�
	 * @param bxvo ����VO
	 * @param bxDetails Ϊ��ʱ��ȡbxvo�еķ�̯��Ϣ
	 * @return ����������ɾ�ĵķ�̯��ϸ����
	 */
	public static CShareDetailVO[] dealWithCShareDetailsStatus(JKBXVO bxvo, CShareDetailVO[] bxDetails) {
		JKBXVO bxoldvo = bxvo.getBxoldvo();

		if(bxoldvo == null){//�����ھɱ���Vo�����ʾΪ����
			return bxvo.getcShareDetailVo();
		}

		List<CShareDetailVO> resultList = new ArrayList<CShareDetailVO>();

		//�޸�ǰ��̯��Ϣ
		CShareDetailVO[] childrenVO_old = bxoldvo.getcShareDetailVo();

		//�޸ĺ��̯��Ϣ
		CShareDetailVO[] childrenVO = null;

		if(bxDetails == null){
			childrenVO = bxvo.getcShareDetailVo();
		}else{
			childrenVO = bxDetails;
		}

		for (int i = 0; i < childrenVO.length; i++) {
			resultList.add(childrenVO[i]);
		}

		//�޸�ǰ��̯��Ϣ����Map��
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

		//��ɾ���ķ�̯��ϸ���뼯����
		Collection<CShareDetailVO> delCollection = oldDetailMap.values();
		for (Iterator<CShareDetailVO> iterator = delCollection.iterator(); iterator.hasNext();) {
			CShareDetailVO object = (CShareDetailVO) iterator.next().clone();
			object.setStatus(VOStatus.DELETED);
			resultList.add(object);
		}

		return resultList.toArray(new CShareDetailVO[]{});
	}

	/**
	 * ������ת���ɷ��ý�ת��(����)
	 * @param bxvo ������VO
	 * @return
	 * @throws BusinessException
	 */
	public static AggCostShareVO[] convertFromBXVOS(BXVO[] bxvo) throws BusinessException {
		AggCostShareVO[] result = null;
		//ֻ�д��ڷ�����ϸ�ı������ſ���ת��
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
	 * ��ȡ���ý�ת��ͷVO
	 * @param shareVo �����Ϊnull ��������shareVo����ֵ������new ����CostShareVO
	 * @param head ����head
	 * @return
	 * @throws ValidationException
	 */
	public static CostShareVO getCShareVOFromBxHead(CostShareVO shareVo ,JKBXHeaderVO head) throws ValidationException {
		if(head == null){
			return null;
		}

		//���������
		CostShareVO cShareVo = null;

		if(shareVo == null){
			cShareVo = new CostShareVO();
		}else{
			cShareVo = shareVo;
		}
		cShareVo.setBxheadvo(head);
		//�Ƶ���
		cShareVo.setBillmaker(head.getCreator());
		cShareVo.setBilldate(head.getDjrq());

		//������Դ��ʽ
		cShareVo.setSrc_type(IErmCostShareConst.CostShare_Bill_SCRTYPE_BX);
		cShareVo.setSrc_id(head.getPk_jkbx());

		// ������Ϣ
		cShareVo.setBx_group(head.getPk_group());
		cShareVo.setBx_org(head.getPk_org());
		cShareVo.setBx_fiorg(head.getPk_fiorg());
		cShareVo.setBx_pcorg(head.getPk_pcorg());
		cShareVo.setBx_djrq(head.getDjrq());//��������
		cShareVo.setDjlxbm(head.getDjlxbm());
		cShareVo.setJkbxr(head.getJkbxr());
		//���Ӳ�Ʒ�ߺ�Ʒ���ֶ�
		cShareVo.setPk_brand(head.getPk_brand());
		cShareVo.setPk_proline(head.getPk_proline());
		
		//���ñ����������ֶ�
		String[] fields = getFieldFromBxVo();
		for (String attr : fields) {
			cShareVo.setAttributeValue(attr, head.getAttributeValue(attr));
		}
		
		//���ñ������Զ��������ൽ��ת����30���Զ�����
		for (int i = 1; i <= 30; i++) {
			cShareVo.setAttributeValue("defitem"+i, head.getAttributeValue("zyx"+i));
		}

		//��������(����Ĭ�����ã����ڳ���ʼ���ö�����ɺ󣬲���ձ�������)

		String pk_tradeType = getCostShareBillTypeByBxVo(head);

		if(pk_tradeType == null){
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0117")/*@res "�ñ�������������δ���ö�Ӧ�ķ��ý�ת��������"*/);
		}

		cShareVo.setPk_tradetype(pk_tradeType);
		cShareVo.setPk_billtype("265X");

		return cShareVo;
	}

	/**
	 * ���ݱ�������ȡ��Ӧ�ķ��ý�ת����������
	 * @param head
	 * @return ��������
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
	 * ����VO���Ƿ��з�̯��Ϣ
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
	 * �ж�UFDouble�Ƿ����0
	 * @param uf
	 * @return true��ʾ��Ϊ����0
	 */
	public static boolean isUFDoubleGreaterThanZero(UFDouble uf){
		if(uf == null || uf.compareTo(UFDouble.ZERO_DBL) <= 0){
			return false;
		}
		return true;
	}

	/**
	 * ��ʽ�����
	 * @param uf
	 * @param power ���ȣ�С��0ʱ������
	 */
	public static UFDouble formatUFDouble(UFDouble uf, int power){
		uf = (uf == null) ? UFDouble.ZERO_DBL :uf;
		if(power > 0){
			uf = uf.setScale(power, UFDouble.ROUND_HALF_UP);
		}

		return uf;
	}
}