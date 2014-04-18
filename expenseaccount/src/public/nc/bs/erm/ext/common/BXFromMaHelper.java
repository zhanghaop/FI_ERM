package nc.bs.erm.ext.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.Currency;
import nc.itf.uap.pf.IPfExchangeService;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.pubitf.erm.matterappctrl.IMtapppfVOQryService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.jkbx.ext.BXMaFYControlVOExt;
import nc.vo.erm.jkbx.ext.BXMaYsControlVOExt;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.control.DataRuleVO;

/**
 * �����������뵥�������ת��������
 * 
 * @author lvhj
 *
 */
public class BXFromMaHelper {
	
	/**
	 * ��ó����뱨���������뵥ת������̯ҳǩ������
	 * 
	 * @param jkbxvo
	 * @return
	 * @throws BusinessException
	 */
	public static JKBXVO getMaBalanceBxVOForFip(JKBXVO jkbxvo) throws BusinessException {
		// clone����װ�Ҵ��͵����ƽ̨
		JKBXVO bxvo = (JKBXVO) jkbxvo.clone();
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		// ���㱨�������������װ�����뵥
		AggMatterAppVO aggmavo = getAggMaVOByBx(parentVO.getPk_item(), parentVO.getPrimaryKey());
		
		if(aggmavo == null){
			return null;
		}
		// ���뵥ת��Ϊ����vo
		IPfExchangeService exchangeservice = NCLocator.getInstance().lookup(IPfExchangeService.class);
		JKBXVO newbxvo = (JKBXVO) exchangeservice.runChangeData(ErmBillConst.MatterApp_BILLTYPE, BXConstans.BX_DJLXBM, aggmavo, null);
		CShareDetailVO[] csharevos = newbxvo.getcShareDetailVo();
		if(csharevos == null || csharevos.length ==0){
			return null;
		}
		// ��������ת����ı���vo
		completeNewBXVO(csharevos,bxvo);
		
		return bxvo;
	}
	

	/**
	 * �������Ʊ�����vo
	 * 
	 * @param csharevos
	 * @param bxvo
	 * @throws BusinessException
	 */
	private static void completeNewBXVO(CShareDetailVO[] csharevos,JKBXVO bxvo)
			throws BusinessException {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		parentVO.setZyx3("RA00");
		// ������ؽ�������
		String bzbm = parentVO.getBzbm();
		UFDate djrq = parentVO.getDjrq();
		
		for (int i = 0; i < csharevos.length; i++) {
			CShareDetailVO csharevo = csharevos[i];
			csharevo.setBbje(Currency.computeYFB(csharevo.getAssume_org(),
					Currency.Change_YBJE, bzbm, csharevo.getAssume_amount(), null,
					null, null, null, djrq)[2]);
			
			UFDouble[] ggbbje = Currency
			.computeGroupGlobalAmount(csharevo.getAssume_amount(), csharevo.getBbje(), bzbm, djrq, csharevo
					.getAssume_org(), parentVO.getPk_group(),
					parentVO.getGlobalbbhl(), parentVO.getGroupbbhl());

			csharevo.setGroupbbje(ggbbje[0]);
			csharevo.setGlobalbbje(ggbbje[1]);
		}
		bxvo.setcShareDetailVo(csharevos);
	}
	
	
	/**
	 * ��ѯ������ִ�м�¼���Ұ�װ����������뵥���
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public static AggMatterAppVO getAggMaVOByBx(String pk_mtapp_bill,String busidetailPK) throws BusinessException {
		
		// ��ѯ�����������뵥�ϵ�ִ�м�¼��������,��������������д����detailbusipkҲ������pk
		MtapppfVO[] pfVos = NCLocator.getInstance().lookup(IMtapppfVOQryService.class)
		.queryMtapppfVoByBusiDetailPk(new String[]{busidetailPK});
		
		return getAggMaVOByBXPf(pk_mtapp_bill, pfVos);
	}

	/**
	 * ���ݱ�����ִ�м�¼���Ұ�װ����������뵥���
	 * 
	 * @param pk_mtapp_bill ���뵥pk
	 * @param pfVos ������ִ�м�¼
	 * @return
	 * @throws BusinessException
	 */
	public static AggMatterAppVO getAggMaVOByBXPf(String pk_mtapp_bill,
			MtapppfVO[] pfVos) throws BusinessException {
		Map<String, UFDouble[][]> madetail_amount = getMtappPfAmount(pfVos);
		if(madetail_amount.isEmpty()){
			// û�г��������ݣ�����
			return null;
		}
		
		// ��ѯ���뵥
		IErmMatterAppBillQuery maqryservice = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
		AggMatterAppVO mavo = maqryservice.queryBillByPK(pk_mtapp_bill);
		
		// Ԥ�����vo����
		MtAppDetailVO[] maDetailVos = mavo.getChildrenVO();
		if(maDetailVos == null || maDetailVos.length == 0){
			return null;
		}
		
		// �������뵥�н��а�װ��дfy�ṹ
		for (MtAppDetailVO mtAppDetailVO : maDetailVos) {
			UFDouble[][] mdAmounts = madetail_amount.get(mtAppDetailVO.getPrimaryKey());
			if(mdAmounts == null){
				return null;
			}
			UFDouble[] md_exe_Amounts = mdAmounts[0];
			UFDouble[] md_pre_Amounts = mdAmounts[1];
			
			mtAppDetailVO.setGlobal_amount(md_exe_Amounts[0]);
			mtAppDetailVO.setGroup_amount(md_exe_Amounts[1]);
			mtAppDetailVO.setOrg_amount(md_exe_Amounts[2]);
			mtAppDetailVO.setOrig_amount(md_exe_Amounts[3]);
			
			mtAppDetailVO.setUsable_amout(mtAppDetailVO.getOrig_amount());
			
			if(md_pre_Amounts != null){
				mtAppDetailVO.setGlobal_pre_amount(md_pre_Amounts[0]);
				mtAppDetailVO.setGroup_pre_amount(md_pre_Amounts[1]);
				mtAppDetailVO.setOrg_pre_amount(md_pre_Amounts[2]);
				mtAppDetailVO.setPre_amount(md_pre_Amounts[3]);
			}
		}
		// �ϼ�������
		computeTotalAmount(mavo);
		return mavo;
	}
	
	/**
	 * �������뵥��ͷ�ϼƽ��
	 * 
	 * @param mavo
	 */
	private static void computeTotalAmount(AggMatterAppVO mavo) {
		MtAppDetailVO[] maDetailVos = mavo.getChildrenVO();
		if(maDetailVos == null || maDetailVos.length == 0){
			return;
		}
		MatterAppVO parentvo = mavo.getParentVO();
		for (int i = 0; i < maDetailVos.length; i++) {
			MtAppDetailVO detailvo = maDetailVos[i];
			if(i == 0){
				// ���
				parentvo.setGlobal_amount(detailvo.getGlobal_amount());
				parentvo.setGroup_amount(detailvo.getGroup_amount());
				parentvo.setOrg_amount(detailvo.getOrg_amount());
				parentvo.setOrig_amount(detailvo.getOrig_amount());
				// Ԥռ��
				parentvo.setGlobal_pre_amount(detailvo.getGlobal_pre_amount());
				parentvo.setGroup_pre_amount(detailvo.getGroup_pre_amount());
				parentvo.setOrg_pre_amount(detailvo.getOrg_pre_amount());
				parentvo.setPre_amount(detailvo.getPre_amount());
				// ���ý��
				parentvo.setUsable_amout(detailvo.getUsable_amout());
			}else{
				// ���
				parentvo.setGlobal_amount(UFDoubleTool.sum(parentvo.getGlobal_amount(), detailvo.getGlobal_amount()));
				parentvo.setGroup_amount(UFDoubleTool.sum(parentvo.getGroup_amount(),detailvo.getGroup_amount()));
				parentvo.setOrg_amount(UFDoubleTool.sum(parentvo.getOrg_amount(),detailvo.getOrg_amount()));
				parentvo.setOrig_amount(UFDoubleTool.sum(parentvo.getOrig_amount(),detailvo.getOrig_amount()));
				// Ԥռ��
				parentvo.setGlobal_pre_amount(UFDoubleTool.sum(parentvo.getGlobal_pre_amount(),detailvo.getGlobal_pre_amount()));
				parentvo.setGroup_pre_amount(UFDoubleTool.sum(parentvo.getGroup_pre_amount(),detailvo.getGroup_pre_amount()));
				parentvo.setOrg_pre_amount(UFDoubleTool.sum(parentvo.getOrg_pre_amount(),detailvo.getOrg_pre_amount()));
				parentvo.setPre_amount(UFDoubleTool.sum(parentvo.getPre_amount(),detailvo.getPre_amount()));
				// ���ý��
				parentvo.setUsable_amout(UFDoubleTool.sum(parentvo.getUsable_amout(),detailvo.getUsable_amout()));
			}
		}
	}

	/**
	 * ��ѯ�����������뵥�ϵ�ִ�м�¼����������
	 * 
	 * @param pfVos
	 * @return
	 * @throws BusinessException
	 */
	private static Map<String, UFDouble[][]> getMtappPfAmount(MtapppfVO[] pfVos)
			throws BusinessException {
		// ��д�����뵥�����г����Ľ�<���뵥��ϸ��pk��new UFDouble[]{ִ������Ԥռ��}{ȫ�ֱ��ҽ����ű��ҽ���֯���ҽ�ԭ�ҽ��}>
		Map<String, UFDouble[][]> madetail_amount = new HashMap<String, UFDouble[][]>();
		if (pfVos == null || pfVos.length == 0) {// �޷���ִ�м�¼�򷵻�
			return madetail_amount;
		}
		for (MtapppfVO mtapppfVO : pfVos) {
			// ���ý��
			UFDouble global_fy_amount = getDoubleValue(mtapppfVO.getGlobal_fy_amount());
			UFDouble group_fy_amount = getDoubleValue(mtapppfVO.getGroup_fy_amount());
			UFDouble org_fy_amount = getDoubleValue(mtapppfVO.getOrg_fy_amount());
			UFDouble fy_amount = getDoubleValue(mtapppfVO.getFy_amount());
			// ������ִ���� = ִ���� - ���ý�� 
			UFDouble global_exe_amount = getSubDoubleValue(getDoubleValue(mtapppfVO.getGlobal_exe_amount()),global_fy_amount);
			UFDouble group_exe_amount = getSubDoubleValue(getDoubleValue(mtapppfVO.getGroup_exe_amount()),group_fy_amount);
			UFDouble org_exe_amount = getSubDoubleValue(getDoubleValue(mtapppfVO.getOrg_exe_amount()),org_fy_amount);
			UFDouble exe_amount = getSubDoubleValue(getDoubleValue(mtapppfVO.getExe_amount()),fy_amount);
			// ������Ԥռ�� = Ԥռ�� - ���ý��
			if(!UFDoubleTool.isZero(mtapppfVO.getPre_amount())){
				
			}
			UFDouble global_pre_amount = getSubDoubleValue(getDoubleValue(mtapppfVO.getGlobal_pre_amount()),global_fy_amount);
			UFDouble group_pre_amount = getSubDoubleValue(getDoubleValue(mtapppfVO.getGroup_pre_amount()),group_fy_amount);
			UFDouble org_pre_amount = getSubDoubleValue(getDoubleValue(mtapppfVO.getOrg_pre_amount()),org_fy_amount);
			UFDouble pre_amount = getSubDoubleValue(getDoubleValue(mtapppfVO.getPre_amount()),fy_amount);
			// �����ڳ��������¼�ҷ���
			if(exe_amount.compareTo(UFDouble.ZERO_DBL)>0||pre_amount.compareTo(UFDouble.ZERO_DBL)>0){
				UFDouble[][] amounts = new UFDouble[2][4];
				amounts[0] = new UFDouble[]{global_exe_amount,group_exe_amount,org_exe_amount,exe_amount};
				if(pre_amount.compareTo(UFDouble.ZERO_DBL) > 0){
					amounts[1] = new UFDouble[]{global_pre_amount,group_pre_amount,org_pre_amount,pre_amount};
				}else{
					amounts[1] = null;
				}
				madetail_amount.put(mtapppfVO.getPk_mtapp_detail(), amounts);
			}
			
		}
		return madetail_amount;
	}


	private static UFDouble getSubDoubleValue(UFDouble d1,UFDouble d2) {
		return d1.compareTo(UFDouble.ZERO_DBL)==0?UFDouble.ZERO_DBL:d1.sub(d2);
	}
	
	private static UFDouble getDoubleValue(Object d) {
		return d == null ? UFDouble.ZERO_DBL : (UFDouble) d;
	}
	
	/**
	 * ���ݱ�����������������뵥����װ��дԤ���bxfyvo
	 * 
	 * @param parentVO
	 * @param mavo
	 * @return
	 */
	public static List<BXMaFYControlVOExt> getBxYsControlVOsBYAggMaVO( JKBXHeaderVO parentVO,
			AggMatterAppVO mavo) {
		List<BXMaFYControlVOExt> list = new ArrayList<BXMaFYControlVOExt>();
		// Ԥ�����vo����
		if(mavo == null || mavo.getChildrenVO() == null || mavo.getChildrenVO().length == 0){
			return list;
		}
		MatterAppVO maParentVO = mavo.getParentVO();
		MtAppDetailVO[] maDetailVos = mavo.getChildrenVO();
		
		// �������뵥�н��а�װ��дfy�ṹ
		for (MtAppDetailVO mtAppDetailVO : maDetailVos) {
			// ��װfyvo
			BXMaFYControlVOExt bxfyvo = new BXMaFYControlVOExt(parentVO,maParentVO,mtAppDetailVO);
			// ����bxfyvo��Ԥռ��
			bxfyvo.setItemJe(new UFDouble[]{mtAppDetailVO.getGlobal_amount(),mtAppDetailVO.getGroup_amount()
					,mtAppDetailVO.getOrg_amount(),mtAppDetailVO.getOrig_amount()});
			if(!UFDoubleTool.isZero(mtAppDetailVO.getPre_amount())){
				bxfyvo.setPreItemJe(new UFDouble[]{mtAppDetailVO.getGlobal_pre_amount(),mtAppDetailVO.getGroup_pre_amount()
						,mtAppDetailVO.getOrg_pre_amount(),mtAppDetailVO.getPre_amount()});
			}
			
			if(bxfyvo != null){
				list.add(bxfyvo);
			}
		}
		return list;
	}
	

	/**
	 * ��װbx�������дԤ��vo���޸ĺ��Ԥ�����VO����
	 * 
	 * @param items
	 *            �޸ĺ�VO����
	 * @param items_old
	 *            �޸�ǰVO����
	 * @param ruleVOs
	 *            ���Ʋ���
	 * @return
	 */
	public static BXMaYsControlVOExt[] getEditControlVOs(IFYControl[] items, IFYControl[] items_old, DataRuleVO[] ruleVOs) {
		BXMaYsControlVOExt[] ps = null;
		Vector<BXMaYsControlVOExt> v = new Vector<BXMaYsControlVOExt>();
		for (int n = 0; n < (ruleVOs == null ? 0 : ruleVOs.length); n++) {
			DataRuleVO ruleVo = ruleVOs[n];
			if (ruleVo == null)
				continue;
			/** ��������/�������� */
			/** Ԥռ�ģ�PREFIND,ִ�У�UFIND */
			String methodFunc = ruleVo.getDataType();
			/** ��������ӣ�true������Ǽ��٣�false */
			boolean isAdd = ruleVo.isAdd();
			for (int i = 0; i < items.length; i++) {
				if (items[i].isYSControlAble()) {
					BXMaYsControlVOExt psTemp = new BXMaYsControlVOExt();
					psTemp.setIscontrary(false);
					psTemp.setItems(new IFYControl[] { items[i] });
					psTemp.setAdd(isAdd);
					psTemp.setMethodCode(methodFunc);
					v.addElement(psTemp);
				}
			}

			for (int i = 0; i < items_old.length; i++) {
				if (items_old[i].isYSControlAble()) {
					BXMaYsControlVOExt psTemp = new BXMaYsControlVOExt();
					psTemp.setIscontrary(true);
					psTemp.setItems(new IFYControl[] { items_old[i] });
					psTemp.setAdd(!isAdd);
					psTemp.setMethodCode(methodFunc);
					v.addElement(psTemp);
				}
			}
		}
		ps = new BXMaYsControlVOExt[v.size()];
		v.copyInto(ps);
		return ps;
	}
	
	/**
	 * ��װbx�������дԤ��vo����ȡԤ�����VO
	 * 
	 * @param items
	 * @param contrayItems 
	 * @param iscontrary
	 * @param ruleVOs
	 * @return
	 */
	public static BXMaYsControlVOExt[] getCtrlVOs(IFYControl[] items, IFYControl[] contrayItems, boolean iscontrary, DataRuleVO[] ruleVOs) {
		BXMaYsControlVOExt[] result = null;
		Vector<BXMaYsControlVOExt> resultVector = new Vector<BXMaYsControlVOExt>();
		for (int n = 0; n < (ruleVOs == null ? 0 : ruleVOs.length); n++) {
			DataRuleVO ruleVo = ruleVOs[n];
			if (ruleVo == null)
				continue;
			/** ��������/�������� */
			String billType = ruleVo.getBilltype_code();
			/** Ԥռ�ģ�PREFIND,ִ�У�UFIND */
			String methodFunc = ruleVo.getDataType();
			/** ��������ӣ�true������Ǽ��٣�false */
			boolean isAdd = true; 
			
			if (n == 1) {//�������ε����
				isAdd = iscontrary ? ruleVo.isAdd() : !ruleVo.isAdd();
			} else {
				isAdd = iscontrary ? !ruleVo.isAdd() : ruleVo.isAdd();
			}
			
			IFYControl[] fyItems = null;
			if (contrayItems == null || contrayItems.length == 0) {
				fyItems = items;
			} else if (n == 0) {
				fyItems = items;
			} else {
				fyItems = contrayItems;
			}
			
			for (int i = 0; i < fyItems.length; i++) {
				IFYControl item = fyItems[i];
				if (billType.equals(item.getParentBillType()) || billType.equals(item.getDjlxbm())) {
					BXMaYsControlVOExt controlVo = new BXMaYsControlVOExt();
					controlVo.setIscontrary(iscontrary);
					controlVo.setItems(new IFYControl[] { item });
					controlVo.setAdd(isAdd);
					controlVo.setMethodCode(methodFunc);
					resultVector.addElement(controlVo);
				}
			}
			
		}
		result = new BXMaYsControlVOExt[resultVector.size()];
		resultVector.copyInto(result);
		return result;
	}
	
}
