package nc.ui.arap.bx.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.FuncletInitData;
import nc.funcnode.ui.FuncletWindowLauncher;
import nc.itf.erm.proxy.ErmProxy;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBudgetControl;
import nc.pubitf.cmp.settlement.ICmpSettlementPubQueryService;
import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.pub.msg.PfLinkData;
import nc.ui.pub.workflownote.FlowStateDlg;
import nc.ui.uap.sf.SFClientUtil;
import nc.view.tb.control.NtbParamVOChooser;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.link.LinkQuery;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.erm.util.VOUtils;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.obj.NtbParamVO;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

/**
 * @author twei
 *
 * ����
 *
 * nc.ui.arap.bx.actions.LinkAction
 */

public class LinkAction extends BXDefaultAction {

	private boolean holdCurrWindow;

	private boolean checkPower;

	private static HashMap<Integer,String> index = new HashMap<Integer, String>();


	static {
		index.put(BXStatusConst.DJZT_Saved,BXConstans.ERM_NTB_APPROVE_KEY);
		index.put(BXStatusConst.DJZT_Verified,BXConstans.ERM_NTB_APPROVE_KEY);
		index.put(BXStatusConst.DJZT_Sign,BXConstans.ERM_NTB_APPROVE_KEY);
	}


	public void linkFpplan() throws BusinessException {
//added by chendya@ufida.com.cn�����ʽ�ƻ�������Ԥ����ͬһ���ӿ�
		try {
			linkYs();
		} catch (Exception e) {
			final String title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0003")/*@res "�����ʽ�ƻ�(Ҳ����Ԥ��)"*/;
			throw new BusinessException(title+","+e.getMessage());
		}
//--end
	}

	/**
	 * ����Ԥ��ִ�����
	 *
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("restriction")
	public void linkYs() throws Exception {
		boolean isExitParent = false;

		List<FiBillAccessableBusiVOProxy> voProxys = new ArrayList<FiBillAccessableBusiVOProxy>();

		JKBXVO bxvo = getCurrentSelectedVO();

		if (bxvo == null || bxvo.getParentVO().getPk_jkbx() == null)
			return;

		if(!isCard()){
			if( (bxvo.getChildrenVO()==null || bxvo.getChildrenVO().length==0 ) && !bxvo.isChildrenFetched()){  //��ʼ������vo
				bxvo=retrieveChidren(bxvo);
			}
		}

		boolean istbbused = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if(!istbbused){
			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0004")/*@res "û�а�װԤ���Ʒ����������Ԥ��ִ�������"*/);
		} else{
			JKBXHeaderVO headVO = bxvo.getParentVO();
			int billStatus = headVO.getDjzt();
			String actionCode = null;

			switch(billStatus){
				case BXStatusConst.DJZT_Saved:
					actionCode = "SAVE";
					break;
				case BXStatusConst.DJZT_Sign:
				    actionCode = index.get(billStatus);
				    break;
				case BXStatusConst.DJZT_Verified:
				    actionCode = index.get(billStatus);
				    break;
			}
			
			JKBXHeaderVO[] items = ErVOUtils.prepareBxvoItemToHeaderClone(bxvo);

			
			//��������,�������Ժ�,��һ�����ܷ����Ķ������������,���ܲ���ִ����,��ǰ��liangsg������,
			//�������Ժ�ȥ����,��Ҫ��busivo.getDataType()��ֵ���ü���Ϊ��˶�����busivo.getDataType()��ֵ,
			//��������һ��,�������͵�ֵ����Ϊ��ǰ����,������ҵ�񵥾����������,���û������
			for(JKBXHeaderVO vo:items){
				if(vo.getShrq()==null){
					vo.setShrq(new UFDateTime());
				}
			}
			actionCode=BXConstans.ERM_NTB_APPROVE_KEY;
			
			

			//����Ԥ��ӿڲ�ѯ���Ʋ��ԡ��������ֵΪ�ձ�ʾ�޿��Ʋ��ԣ������ơ����һ������Ϊfalse�������Ͳ���������β���
			DataRuleVO[] ruleVO = NCLocator.getInstance().lookup(IBudgetControl.class).queryControlTactics(headVO.getDjlxbm(),actionCode,isExitParent);
			YsControlVO[] ps = null;
			if(ruleVO!=null&&ruleVO.length>0){
				ps = getSaveControlVos(items,true,ruleVO);

			}
			if (ps != null) {
				for (YsControlVO vo : ps) {

					voProxys.add(getFiBillAccessableBusiVOProxy(vo));
				}
			}
			try {

				NtbParamVO[] vos = ErmProxy.getILinkQuery().getLinkDatas(voProxys.toArray(new IAccessableBusiVO[] {}));

				NtbParamVOChooser chooser = new NtbParamVOChooser(getParent(),nc.ui.ml.NCLangRes.getInstance().getStrByID(
								"2006030102", "UPP2006030102-000430")/**
				 * @res
				 *      "Ԥ��ִ�����"
				 */
				);

				if (null == vos || vos.length == 0) {
					throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0066")/*@res "û�з���������Ԥ������!"*/);
				}
				chooser.setParamVOs(vos);
				chooser.showModal();
			} catch (Exception e) {
				throw ExceptionHandler.handleException(this.getClass(), e);
			}
			}
		}
//	}

	private YsControlVO[] getSaveControlVos(IFYControl[] items, boolean iscontrary,DataRuleVO[] ruleVOs) {

		YsControlVO[] ps = null;
		Vector<YsControlVO> v = new Vector<YsControlVO>();
		for(int n=0;n<(ruleVOs==null?0:ruleVOs.length);n++){
			DataRuleVO ruleVo = ruleVOs[n];
			/**��������/��������*/
			String billType =  ruleVo.getBilltype_code();
			/**Ԥռ�ģ�PREFIND,ִ�У�UFIND*/
			String methodFunc = ruleVo.getDataType();
	        /**��������ӣ�true������Ǽ��٣�false*/
			boolean isAdd = ruleVo.isAdd();
			IFYControl[] itemsTemp = getRealItems(items, billType);
			for (int i = 0; i < items.length; i++) {
				YsControlVO psTemp = new YsControlVO();
				psTemp.setIscontrary(iscontrary);
				psTemp.setItems(new IFYControl[]{itemsTemp[i]});
				psTemp.setAdd(isAdd);
				psTemp.setMethodCode(methodFunc);
				v.addElement(psTemp);
			}
	    }
		ps = new YsControlVO[v.size()];
		v.copyInto(ps);

		return ps;
	}

	/**���ݵ�������������VOS*/
	private IFYControl[] getRealItems (IFYControl[] items,String billtype){
		return items;
	}

	private FiBillAccessableBusiVOProxy getFiBillAccessableBusiVOProxy(FiBillAccessableBusiVO vo) {
		FiBillAccessableBusiVOProxy voProxy;
		voProxy = new FiBillAccessableBusiVOProxy(vo, BXConstans.ERM_PRODUCT_CODE_Lower);
		voProxy.setLinkQuery(true);
		return voProxy;
	}

	/**
	 *
	 * ����ƾ֤
	 *
	 * @throws BusinessException
	 */
	@SuppressWarnings("restriction")
	public void linkVoucher() throws BusinessException {

		JKBXVO selectedVO = getCurrentSelectedVO();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		/** ��ȥ��ʵ�ַ�ʽ��ע��
		String vouchid = selectedVO.getParentVO().getPk_jkbx();
		BillQueryVoucherVO voquery = new BillQueryVoucherVO();
		voquery.setPk_bill(vouchid);
		voquery.setDestSystem(0);
		try {
			DapBillQueryVoucher.showVoucher(voquery, this.getMainPanel());
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
 */

		//����FipRelationInfoVO

		FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
		srcinfovo.setPk_group(selectedVO.getParentVO().getPk_group());
		srcinfovo.setPk_org(selectedVO.getParentVO().getPk_org());
		srcinfovo.setRelationID(selectedVO.getParentVO().getPk());
		srcinfovo.setPk_billtype(selectedVO.getParentVO().getDjlxbm());

		try {
//begin--modified by chendya ���Լ���Ϊ��Դ��ѯĿ�굥��
			FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(this.getParent(), srcinfovo);
//--end
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * �����������
	 *
	 * @throws BusinessException
	 */
	public void approveStatus() throws BusinessException {

		JKBXVO selectedVO = getCurrentSelectedVO();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		String pk_jkbx = selectedVO.getParentVO().getPk_jkbx();
		String djlxbm = selectedVO.getParentVO().getDjlxbm();

		FlowStateDlg app = new FlowStateDlg(getMainPanel(), djlxbm, pk_jkbx, WorkflowTypeEnum.Approveflow.getIntValue());

		app.showModal();
	}

	/**
	 * �����Ӧ�Ľ�
	 *
	 * @throws BusinessException
	 */
	public void linkJkd() throws BusinessException {

		JKBXVO selectedVO = getCurrentSelectedVO();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;
		if(selectedVO.getParentVO().getDjdl().equals(BXConstans.JK_DJDL))
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000354")/*@res "����൥�ݲ����������"*/);

		JKBXHeaderVO parentVO = selectedVO.getParentVO();

		Collection<BxcontrastVO> contrasts = getIBXBillPrivate().queryContrasts(parentVO);

		if (contrasts == null || contrasts.size() == 0) {

			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0100")/*@res "������û�г���������û�н����Ϣ��"*/);

		}

		String[] oids = VOUtils.changeCollectionToArray(contrasts, BxcontrastVO.PK_JKD);

		LinkQuery linkQuery = new LinkQuery(BXConstans.JK_DJDL,oids);

		SFClientUtil.openLinkedQueryDialog(BXConstans.BXMNG_NODECODE, this.getMainPanel(), linkQuery);

	}



	/**
	 * �����Ӧ�ı�����
	 *
	 * @throws BusinessException
	 */
	public void linkBxd() throws BusinessException {

		JKBXVO selectedVO = getCurrentSelectedVO();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		if(selectedVO.getParentVO().getDjdl().equals(BXConstans.BX_DJDL))
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000355")/*@res "�����൥�ݲ������鱨������"*/);

		JKBXHeaderVO parentVO = selectedVO.getParentVO();

		Collection<BxcontrastVO> contrasts = getIBXBillPrivate().queryContrasts(parentVO);

		if (contrasts == null || contrasts.size() == 0) {

			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000011")/*@res "��ѡ����δ���г���,�޷����鱨����!"*/);
		}

		String[] oids = VOUtils.changeCollectionToArray(contrasts, BxcontrastVO.PK_BXD);

		LinkQuery linkQuery = new LinkQuery(BXConstans.BX_DJDL,oids);

		SFClientUtil.openLinkedQueryDialog(BXConstans.BXMNG_NODECODE, this.getMainPanel(), linkQuery);

	}
	/**
	 * ���������Ϣ
	 *
	 * @throws BusinessException
	 */
	public void linkSettleInfo() throws BusinessException{
		
		JKBXVO selectedVO = getCurrentSelectedVO();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;
		
		JKBXHeaderVO head = selectedVO.getParentVO();
		
		SettlementAggVO[] bills = null;
		
		boolean iscmpused = BXUtil.isProductInstalled(head.getPk_group(), BXConstans.TM_CMP_FUNCODE);

		if(!iscmpused){
			throw new BusinessShowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0005")/*@res "û�а�װ�ֽ�����Ʒ�����ܲ�ѯ������Ϣ�����"*/);
		} else{
			String pk_jkbx = selectedVO.getParentVO().getPk_jkbx();
			bills = NCLocator.getInstance().lookup(ICmpSettlementPubQueryService.class).queryBillsBySourceBillID(new String[] { pk_jkbx });
		}
		
		if (bills != null && bills[0] != null) {
			LinkQuery linkQuery = new LinkQuery(bills[0].getParentVO().getPrimaryKey());
			SFClientUtil.openLinkedQueryDialog(BXConstans.SETTLE_FUNCCODE, this.getParent(), linkQuery);
		} else {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0003")/* @res "û�н��������Ϣ,û�л�����Ҳû��֧�����,�������"*/);
		}
	}

	/**
	 * �����ո�������
	 *
	 * @throws BusinessException
	 */
	public void linkJsd() throws BusinessException {

		JKBXVO selectedVO = getCurrentSelectedVO();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		if (!selectedVO.getParentVO().getDjzt().equals(BXStatusConst.DJZT_Sign)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000012")/*@res "����δ��Ч,�޷�������������!"*/);
		}

		Collection<JsConstrasVO> contrasts = getIBXBillPrivate().queryJsContrasts(selectedVO.getParentVO());

		if (contrasts == null || contrasts.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000013")/*@res "�����ո���������ʧ��,δ�����ո���������!"*/);
		}

		List<JsConstrasVO> contrasts0 = new ArrayList<JsConstrasVO>();
		List<JsConstrasVO> contrasts1 = new ArrayList<JsConstrasVO>();


		for(JsConstrasVO contrast:contrasts){
			if(contrast.getBillflag()==0){
				contrasts0.add(contrast);
			} else{
				contrasts1.add(contrast);
			}

		}
		String[] oidsys = VOUtils.changeCollectionToArray(contrasts0, JsConstrasVO.PK_JSD);
		String[] oidsyf = VOUtils.changeCollectionToArray(contrasts1, JsConstrasVO.PK_JSD);

		if (oidsys == null || oidsys.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000013")/*@res "�����ո���������ʧ��,δ�����ո���������!"*/);
		}
		FuncRegisterVO registerVO = WorkbenchEnvironment.getInstance().getFuncRegisterVO(BXConstans.FI_AR_MNGFUNCODE);
		
		if (registerVO == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000435")/*@res "�����ո���������ʧ��,û�ж�Ӧ�ڵ��Ȩ��!"*/);
		}
		
		
		PfLinkData link =  new PfLinkData();
		link.setUserObject(oidsys);
		if(oidsys[0]!=null){
			link.setBillID(oidsys[0]);
		}

		//����Ӧ�յ�
		FuncletInitData initData = new FuncletInitData();
        initData.setInitType(ILinkType.LINK_TYPE_QUERY);
        initData.setInitData(link);
		FuncletWindowLauncher.openFuncNodeInTabbedPane(this.getMainPanel(), registerVO, initData, null, false);
		if (oidsyf == null || oidsyf.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000013")/*@res "�����ո���������ʧ��,δ�����ո���������!"*/);
		}

		//����Ӧ����
    	initData = new FuncletInitData();
		FuncRegisterVO registerVO1 = WorkbenchEnvironment.getInstance().getFuncRegisterVO(BXConstans.FI_AP_MNGFUNCODE);
		link =  new PfLinkData();
		link.setUserObject(oidsyf);
		if(oidsyf[0]!=null){
			link.setBillID(oidsyf[0]);
		}
		initData.setInitType(ILinkType.LINK_TYPE_QUERY);
		initData.setInitData(link);
		FuncletWindowLauncher.openFuncNodeInTabbedPane(this.getMainPanel(), registerVO1, initData, null, false);

	}

	/**
	 * ��������������
	 *
	 * @throws BusinessException
	 */
	public void linkSS() throws BusinessException {

		JKBXVO selectedVO = getCurrentSelectedVO();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		if (StringUtils.isNullWithTrim(selectedVO.getParentVO().getPk_item())) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000014")/*@res "����û����������������,�޷�����!"*/);
		}

		LinkQuery linkQuery = new LinkQuery(new String[] { selectedVO.getParentVO().getPk_item() });

		SFClientUtil.openLinkedQueryDialog("20040202", this.getParent(), linkQuery);

	}

	/**
	 * ���鱨���ƶ�
	 *
	 * @throws BusinessException
	 * @throws MalformedURLException
	 */
	public void linkRule() throws BusinessException {

		JKBXVO selectedVO = getCurrentSelectedVO();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		JKBXHeaderVO parentVO = selectedVO.getParentVO();
		BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(parentVO.getDjlxbm(), parentVO.getDjdl());
//added by chendya
		final String strUrl = busTypeVO.getRule();
		if(strUrl==null||strUrl.trim().length()==0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0006")/*@res "���鱨���ƶȳ���û������URL������"*/);
		}
		URL url = null;
		try {
			url = new URL(strUrl);
			nc.sfbase.client.ClientToolKit.showDocument(url, "_blank");
		} catch (MalformedURLException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0007")/*@res "�Ƿ���URL������"*/);
		}
//--end
	}

	/**
	 * ���鱨����׼
	 *
	 * @throws BusinessException
	 * @throws MalformedURLException
	 */
	public void linkLimit() throws BusinessException {

		JKBXVO selectedVO = getCurrentSelectedVO();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		JKBXHeaderVO parentVO = selectedVO.getParentVO();
		BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(parentVO.getDjlxbm(), parentVO.getDjdl());
//added by chendya
		final String strUrl = busTypeVO.getLimit();
		if(strUrl==null||strUrl.trim().length()==0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0008")/*@res "���鱨����׼����û������URL������"*/);
		}
		URL url = null;
		try {
			url = new URL(strUrl);
			nc.sfbase.client.ClientToolKit.showDocument(url, "_blank");
		} catch (MalformedURLException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0007")/*@res "�Ƿ���URL������"*/);
		}
//--end
	}


}