package nc.impl.arap.bx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IErmBillUIPublic;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.itf.uap.pf.IPFConfig;
import nc.pubitf.erm.matterappctrl.IMatterAppCtrlService;
import nc.pubitf.org.cache.IOrgUnitPubService_C;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.resa.costcenter.CostCenterVO;
/**
 * ���ý����������Ĭ��ֵ��̨������
 * @author wangled
 *
 */
public class ErmBillUIImpl implements IErmBillUIPublic{

	@Override
	/**
	 * JKBXVO����Ϊ��ʱ���ƣ���Ϊ��ʱΪ����
	 */
	public JKBXVO setBillVOtoUI(DjLXVO djlx, String funnode, JKBXVO vo)
			throws BusinessException {
		JKBXVO jkbxvo = null;
		boolean isLoadInitBill = false;
		if(vo != null){
			//�������
			jkbxvo = vo;
			
		}else{
			//��ѯ���õ��ݣ��������������Ҳ��ǳ��õ��ݽڵ�ʱ
			if(!BXConstans.BXINIT_NODECODE_G.equals(funnode)
					&& !BXConstans.BXINIT_NODECODE_U.equals(funnode)){
				UFBoolean isloadtemplate = djlx.getIsloadtemplate();
				if(isloadtemplate != null && isloadtemplate.booleanValue()){
					//���س��õ���
					isLoadInitBill= true;
				}
			}
			if (BXConstans.BX_DJDL.equals(djlx.getDjdl())) {
				jkbxvo = new BXVO();
			} else {
				jkbxvo = new JKVO();
			}
		}
		
		String cuserid = InvocationInfoProxy.getInstance().getUserId();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();

		// ���ý������Ļ�����Ϣ
		setJkBxBaseInfo(djlx, funnode, jkbxvo, cuserid, pk_group);

		// ��������ʱ���������˵������Ϣ
		try {
			jkbxvo = setPsnInfoByUserId(jkbxvo, cuserid, pk_group, vo ,isLoadInitBill);
			
			insertBusitype(jkbxvo);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}
		
		return jkbxvo;
	}
	
	// ����ҵ������
	private void insertBusitype(JKBXVO jkbxvo) throws BusinessException {
		if(jkbxvo.getParentVO().getPk_org() != null){
			IPFConfig pFConfig = NCLocator.getInstance().lookup(IPFConfig.class);
			String billtype = BXConstans.BX_DJDL.equals(jkbxvo.getParentVO().getDjdl()) ? BXConstans.BX_DJLXBM : BXConstans.JK_DJLXBM;
			//����ί�и���ֻ֧�ְ���������ȥ�����������������Ϊ���վ��彻������ȥ��ҵ������
			String transtype = jkbxvo.getParentVO().getDjlxbm();
			String userid = InvocationInfoProxy.getInstance().getUserId();
			String pk_busiflowValue = pFConfig.retBusitypeCanStart(billtype, transtype, jkbxvo.getParentVO().getPk_org(), userid);
			
			jkbxvo.getParentVO().setBusitype(pk_busiflowValue);
		}
	}
	
	
	/**
	 * ��������ʱ���������˵������Ϣ
	 * @param jkbxvo
	 * @throws BusinessException 
	 */
	private JKBXVO setPsnInfoByUserId(JKBXVO jkbxvo ,String cuserid , String pk_group, JKBXVO isMattVo,boolean isLoadInitBill) throws BusinessException {
		
		String[] result = NCLocator.getInstance().lookup(
				IBXBillPrivate.class).queryPsnidAndDeptid(cuserid,pk_group);
		if(result[0] == null || StringUtil.isEmpty(result[0])){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0",
			"02011ermpub0316-0000")/* * * @res*
			* "��ǰ�û�δ������Ա������ϵ������ԱΪ���û�ָ�����"*/);
		}
		String pk_psndoc = result[0];
		String pk_dept = result[1];
		String pk_org = result[2];
		
		//���ý����˺��տ���
		setHeadVO(new String[] {JKBXHeaderVO.JKBXR, JKBXHeaderVO.RECEIVER},
				pk_psndoc ,isMattVo ,jkbxvo);
		
		//���ò�����ص���Ϣ 
		setHeadVO(new String[] {  JKBXHeaderVO.DEPTID, JKBXHeaderVO.FYDEPTID },
				pk_dept ,isMattVo ,jkbxvo);
		
		//������֯��ص���Ϣ
		setHeadVO(new String[] { JKBXHeaderVO.PK_ORG, JKBXHeaderVO.FYDWBM,JKBXHeaderVO.DWBM,
				JKBXHeaderVO.PK_FIORG,JKBXHeaderVO.PK_PAYORG },
				//���Ʋ������������ģ�JKBXHeaderVO.PK_PCORG(����������ã������Ÿ��ݷ��óй����ĳɱ�����������)
				pk_org ,isMattVo ,jkbxvo);
		
		//���س��õ��ݵĴ���
		if(isLoadInitBill){
			List<JKBXVO> initBill = getInitBill(jkbxvo.getParentVO().getPk_org(),jkbxvo.getParentVO().getPk_group()
					,jkbxvo.getParentVO().getDjlxbm(),true);
			if(initBill != null && initBill.size() > 0){//���س��õ��ݵ���Ϣ
				JKBXVO bxvo = initBill.get(0);
				String[] fieldNotCopy = JKBXHeaderVO.getFieldNotInit();
				List<String> notNeedCopy = Arrays.asList(fieldNotCopy);
				String[] attributeNames = bxvo.getParentVO().getAttributeNames();
				String[] specialNames = new String[]{JKBXHeaderVO.PK_PAYORG,
						JKBXHeaderVO.PK_ORG,JKBXHeaderVO.FYDWBM,JKBXHeaderVO.FYDEPTID
				};
				List<String> specialNameList = Arrays.asList(specialNames);
				jkbxvo.getParentVO().setLoadInitBill(true);
				jkbxvo.setChildrenVO(bxvo.getChildrenVO());
				for(String attribute : attributeNames){
					// ���س��õ��ݵ�ԭ�򣺽������������Ϣ�������أ�����Ĭ��ֵ��
					if(!notNeedCopy.contains(attribute)){
						//��Ҫ�ӳ��õ��ݿ��������ֶ�
						if(!specialNameList.contains(attribute)){
							jkbxvo.getParentVO().setAttributeValue(attribute, bxvo.getParentVO().getAttributeValue(attribute));
						}else{
							if (bxvo.getParentVO().getPk_org() != null) {
								jkbxvo.getParentVO().setPk_org(bxvo.getParentVO().getPk_org());
							}
							if(bxvo.getParentVO().getFydwbm() != null){
								jkbxvo.getParentVO().setFydwbm(bxvo.getParentVO().getFydwbm());
							}
							if(bxvo.getParentVO().getFydeptid() != null){
								jkbxvo.getParentVO().setFydeptid(bxvo.getParentVO().getFydeptid());
							}
							if(bxvo.getParentVO().getPk_payorg() != null){
								jkbxvo.getParentVO().setPk_payorg(bxvo.getParentVO().getPk_payorg());
							}
						}
					}
				}
			}
		}
		//���óɱ����ĺ���������
		setCostCenter(jkbxvo);
		
		return jkbxvo;
	}
	
	/**
	 * ���ݷ��óе����Ŵ����ɱ����ĺͶ�Ӧ����������
	 * 
	 * @param pk_fydept
	 */
	protected void setCostCenter(JKBXVO jkbxvo) {
//		boolean isResInstalled = BXUtil.isProductInstalled(jkbxvo.getParentVO().getPk_group()
//				, BXConstans.FI_RES_FUNCODE);
		if (StringUtil.isEmpty(jkbxvo.getParentVO().getFydeptid())) {
			return;
		}
		String pk_costcenter = null;
		String pk_pcorg = null;
		CostCenterVO[] vos = null;
			try {
				vos = NCLocator.getInstance().lookup(ICostCenterQueryOpt.class)
						.queryCostCenterVOByDept(new String[] { jkbxvo.getParentVO().getFydeptid() });
			} catch (BusinessException e) {
				Log.getInstance(getClass()).error(e.getMessage());
				return;
			}
			if (vos != null) {
				for (CostCenterVO vo : vos) {
						pk_costcenter = vo.getPk_costcenter();
						pk_pcorg = vo.getPk_profitcenter();
						break;
				}
			}
			if(jkbxvo.getParentVO().getPk_resacostcenter()== null){
				jkbxvo.getParentVO().setPk_resacostcenter(pk_costcenter);
			}
			if(jkbxvo.getParentVO().getPk_pcorg()== null){
				// �������ģ����ж��Ƿ����������ģ����ǲ���ֵ
				if(pk_pcorg!=null && fillPcOrg(pk_pcorg)){
					jkbxvo.getParentVO().setPk_pcorg(pk_pcorg);
				}
			}
	}
	
	/**
	 * ���س��õ���
	 * @param pk_org
	 * @param pk_group
	 * @param djlxbm
	 * @param includeGroup
	 * @return
	 * @throws BusinessException
	 */
	public static List<JKBXVO> getInitBill(String pk_org, String pk_group,
			String djlxbm, boolean includeGroup) throws BusinessException {

		DjCondVO condVO = new DjCondVO();

		condVO.isInit = true;
		condVO.defWhereSQL = " zb.djlxbm='" + djlxbm
				+ "' and zb.dr=0 and ((isinitgroup='N' and pk_org='" + pk_org
				+ "') or isinitgroup='Y') ";
		condVO.isCHz = false;
		condVO.pk_group = new String[] { pk_group };
		// ��֯�ͼ��ŵĺϲ���ѯ������Զ�̵��ô�������ѯ������,���ȷ�����֯����VO
		List<JKBXVO> all = NCLocator.getInstance().lookup(IBXBillPrivate.class)
				.queryVOs(0, -99, condVO);

		if (all == null) {
			return new ArrayList<JKBXVO>();
		}
		List<JKBXVO> orgVOList = new ArrayList<JKBXVO>();
		List<JKBXVO> groupVOList = new ArrayList<JKBXVO>();
		for (Iterator<JKBXVO> iterator = all.iterator(); iterator.hasNext();) {
			JKBXVO vo = iterator.next();
			if (UFBoolean.TRUE.equals(vo.getParentVO().getIsinitgroup())) {
				groupVOList.add(vo);
			} else {
				orgVOList.add(vo);
			}
		}
		// ���ȷ�����֯���ĳ��õ���
		return orgVOList.size() > 0 ? orgVOList : groupVOList;
	}

	
	/**
	 * 
	 * @param fields:������ֶ�
	 * @param defaluevalue�� Ĭ��ֵ
	 * @param ismattvo �� ������VO
	 * @param jkbxvo �� ��󷵻ص�VO
	 */
	private JKBXVO setHeadVO(String[] fields, String defaluevalue,JKBXVO ismattvo,JKBXVO jkbxvo) {
		for (String field : fields) {
			String value = defaluevalue;
			//���������⴦������û��ֵʱ������Ĭ��ֵ
			if(ismattvo !=null ){
				JKBXHeaderVO parentVO = ismattvo.getParentVO();
				if (parentVO.getAttributeValue(field) != null) {
					value = (String) parentVO.getAttributeValue(field);
				}
				//�����������⴦����Ϊ����ʱ������Ĭ��ֵ
				if(ismattvo.getParentVO().getPk_pcorg()!=null 
						&& jkbxvo.getParentVO().getPk_pcorg()!=null){
					jkbxvo.getParentVO().setPk_pcorg(ismattvo.getParentVO().getPk_pcorg());
				}
			}
			jkbxvo.getParentVO().setAttributeValue(field, value);
		}
		return jkbxvo;
	}
	
	/**
	 * ���ý������Ļ�����Ϣ
	 * @param djlx
	 * @param funnode
	 * @param jkbxvo
	 */
	private void setJkBxBaseInfo(DjLXVO djlx, String funnode, JKBXVO jkbxvo,String cuserid , String pk_group) {
		UFDate busiDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
		if(BXConstans.BXLR_QCCODE.equals(funnode)){
			jkbxvo.getParentVO().setQcbz(UFBoolean.TRUE);
			jkbxvo.getParentVO().setDjzt(BXStatusConst.DJZT_Sign);
			jkbxvo.getParentVO().setSxbz(BXStatusConst.SXBZ_VALID);
			
		}else{
			jkbxvo.getParentVO().setQcbz(UFBoolean.FALSE);
			jkbxvo.getParentVO().setDjrq(busiDate);
			jkbxvo.getParentVO().setShrq(null);
			jkbxvo.getParentVO().setJsrq(null);
			jkbxvo.getParentVO().setApprover(null);
			jkbxvo.getParentVO().setOperator(cuserid);
			jkbxvo.getParentVO().setDjzt(BXStatusConst.DJZT_Saved);
			jkbxvo.getParentVO().setSxbz(BXStatusConst.SXBZ_NO);
		}
		jkbxvo.getParentVO().setDjdl(djlx.getDjdl());
		jkbxvo.getParentVO().setDjlxbm(djlx.getDjlxbm());
		jkbxvo.getParentVO().setDjbh("");
		jkbxvo.getParentVO().setDr(Integer.valueOf(0));
		jkbxvo.getParentVO().setPk_group(pk_group);
		jkbxvo.getParentVO().setOperator(cuserid);
		jkbxvo.getParentVO().setCreator(cuserid);
	}
	
	/**
	 * �ж��Ƿ�����������
	 * @param value
	 * @return
	 */
	private boolean fillPcOrg(Object value) {
		try {
			OrgVO[] orgs = NCLocator.getInstance().lookup(IOrgUnitPubService_C.class).getOrgs(new String[]{(String) value}, new String[]{});
			if(orgs[0].getOrgtype15().booleanValue()){
				return true;
			}else{
				return false;
			}
		} catch (BusinessException e) {
			throw new RuntimeException(e);
		}
	}




	@Override
	/**
	 * ��������
	 */
	public MatterAppConvResVO setBillVOtoUIByMtappVO(String pk_org,
			AggMatterAppVO retvo, DjLXVO djlx, String funnode)
			throws BusinessException {
		
		MatterAppConvResVO resVO = convertAggMattappVO(djlx.getDjlxbm(),pk_org,  retvo);

		JKBXVO vo = (JKBXVO) resVO.getBusiobj();
		vo.setMaheadvo(retvo.getParentVO());
		MatterAppVO maparentvo = retvo.getParentVO();
		vo.getParentVO().setBzbm(maparentvo.getPk_currtype());
		vo.getParentVO().setPk_org(maparentvo.getPk_org());
		vo.getParentVO().setPk_group(maparentvo.getPk_group());
		
		if(maparentvo.getIscostshare() == null || !maparentvo.getIscostshare().booleanValue()){
			// ���뵥�Ƿ�̯���ʱ�������̯ҳǩת������
			vo.getParentVO().setIscostshare(UFBoolean.FALSE);
			vo.setcShareDetailVo(null);
		}
		// ��������Ĭ��ֵ
		JKBXVO jkbxvo = this.setBillVOtoUI(djlx, funnode, vo);
		resVO.setBusiobj(jkbxvo);
		return resVO;
	}
	
	/**
	 * ���뵥����ת��Ϊ���ε���
	 * 
	 * @param billTypeCode
	 * @param pk_org
	 * @param retvo
	 * @return
	 * @throws BusinessException
	 */
	private MatterAppConvResVO convertAggMattappVO(String billTypeCode, String pk_org, AggMatterAppVO retvo) throws BusinessException {

		IMatterAppCtrlService ctrlService = NCLocator.getInstance().lookup(IMatterAppCtrlService.class);
		return ctrlService.getConvertBusiVOs(billTypeCode, pk_org, retvo);
	}
	
	
}
