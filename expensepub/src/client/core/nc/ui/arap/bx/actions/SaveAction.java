package nc.ui.arap.bx.actions;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.itf.arap.pub.IBxUIControl;
import nc.ui.arap.bx.BXBillCardPanel;
import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.er.util.BXUiUtil;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.cmp.exception.ErmException;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.er.check.VOChecker;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.er.exception.CrossControlMsgException;
import nc.vo.er.exception.ContrastBusinessException;
import nc.vo.er.exception.ContrastBusinessException.ContrastBusinessExceptionType;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.global.IRuntimeConstans;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

/**
 * nc.ui.arap.bx.actions.SaveAction
 * 
 * @author twei
 * 
 *         ���浥��
 */

public class SaveAction extends BXDefaultAction {

	public SaveAction() {
	}

	public SaveAction(IActionRuntime panel) {
		setActionRunntimeV0(panel);
	}

	/**
	 * @return �Ƿ���Ҫ���г������.
	 * @throws Exception
	 */
	public boolean save() throws Exception {

		//ֹͣ�༭
		getBillCardPanel().stopEditing();
		
		// ���˿���
		removeNullItem();

		// �ǿ�У����ڹ��˿���֮��
		getCardPanel().dataNotNullValidate();
		
		// ȡ����VO
		JKBXVO bxvo = getBillValueVO();
		
		//�����ǰ�����ҵ�����ǿյ�,���ҵ�ǰҳǩ�ǳ���ҳǩ
		if(bxvo.getChildrenVO().length == 0 &&
			(getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CONST_PAGE)||	
			getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CONST_PAGE_JK))){
			String[] tables = getBillCardPanel().getBillData().getBodyTableCodes();
			if(!tables[0].equals(BXConstans.CONST_PAGE)&&!tables[0].equals(BXConstans.CONST_PAGE_JK)){
				BillModel billModel = ((BXBillCardPanel)getBillCardPanel()).getBillData().getBillModel(tables[0]);
				if(!((BXBillCardPanel)getBillCardPanel()).isTabVisiable(billModel)){
					BXBusItemVO bxBusItemVO=new BXBusItemVO();
					bxBusItemVO.setTablecode(tables[0]);
					bxvo.setChildrenVO(new BXBusItemVO[]{bxBusItemVO});
				}
			}
		}

		//�������ҵ����ֻ��һ��,��û������ʱ����Ҫ������Ĵ�������ͷ�����ݴ��뵽����
		if (bxvo.getChildrenVO().length == 1  && (bxvo.getChildrenVO()[0].getAmount() == null
				|| UFDouble.ZERO_DBL.equals(bxvo.getChildrenVO()[0].getAmount()))) {
			bxvo.getChildrenVO()[0].setAmount(bxvo.getParentVO().ybje);
			bxvo.getChildrenVO()[0].setYbje(bxvo.getParentVO().ybje);
			bxvo.getChildrenVO()[0].setBbje(bxvo.getParentVO().bbje);
			bxvo.getChildrenVO()[0].setGroupbbje(bxvo.getParentVO().groupbbje);
			bxvo.getChildrenVO()[0].setGlobalbbje(bxvo.getParentVO().globalbbje);
			
			bxvo.getChildrenVO()[0].setYbye(bxvo.getParentVO().ybye);
			bxvo.getChildrenVO()[0].setBbye(bxvo.getParentVO().bbye);
			bxvo.getChildrenVO()[0].setGroupbbye(bxvo.getParentVO().groupbbye);
			bxvo.getChildrenVO()[0].setGlobalbbye(bxvo.getParentVO().globalbbye);
			
			bxvo.getChildrenVO()[0].setCjkybje(bxvo.getParentVO().cjkybje);
			bxvo.getChildrenVO()[0].setCjkbbje(bxvo.getParentVO().cjkbbje);
			bxvo.getChildrenVO()[0].setGroupcjkbbje(bxvo.getParentVO().groupcjkbbje);
			bxvo.getChildrenVO()[0].setGlobalcjkbbje(bxvo.getParentVO().globalcjkbbje);
			
			bxvo.getChildrenVO()[0].setHkybje(bxvo.getParentVO().hkybje);
			bxvo.getChildrenVO()[0].setHkbbje(bxvo.getParentVO().hkbbje);
			bxvo.getChildrenVO()[0].setGrouphkbbje(bxvo.getParentVO().grouphkbbje);
			bxvo.getChildrenVO()[0].setGlobalhkbbje(bxvo.getParentVO().globalhkbbje);
			
			bxvo.getChildrenVO()[0].setZfybje(bxvo.getParentVO().zfybje);
			bxvo.getChildrenVO()[0].setZfbbje(bxvo.getParentVO().zfbbje);
			bxvo.getChildrenVO()[0].setGroupzfbbje(bxvo.getParentVO().groupzfbbje);
			bxvo.getChildrenVO()[0].setGlobalzfbbje(bxvo.getParentVO().globalzfbbje);
			
			bxvo.getChildrenVO()[0].setSzxmid(bxvo.getParentVO().getSzxmid());
		}
		


		//ִ�б�����֤��ʽ
		boolean passed = getBillCardPanel().getBillData().execBodyValidateFormulas();
		if (!passed) {
			return false;
		}


		
		if(BXConstans.BX_DJDL.equals(bxvo.getParentVO().getDjdl())){
			//����������Ƿ���ʾ����
			boolean flag = true;
			if (bxvo.getParentVO().getYbje() != null&& bxvo.getParentVO().getYbje().doubleValue() >= 0) {
				if(bxvo.getParentVO().getCjkybje()!=null && bxvo.getParentVO().getCjkybje().compareTo(new UFDouble(0.00))!=0){
					//�г��������ʾ
					flag = false;
				}
			}
			if(flag){
				//���������Ƿ���ʾ����
				UFBoolean isNoticeContrast = getVoCache().getCurrentDjlx().getIscontrast();
				if(isNoticeContrast.booleanValue()){
					//�����Ƿ��н�
					final boolean hasJKD = NCLocator.getInstance().lookup(IBxUIControl.class).getJKD(bxvo.getParentVO(), bxvo.getParentVO().getDjrq(), null).size() > 0;
					if (hasJKD && MessageDialog.ID_YES == MessageDialog.showYesNoDlg(getParent(),
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/* @res "��ʾ" */, 
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0087")/* @res "������δ��Ľ����Ƿ���г������? " */)) {
						ContrastAction action = new ContrastAction();
						action.setActionRunntimeV0(getMainPanel());
						action.contrast();
						//����ȡ������vo
						bxvo = getBillValueVO();
					}
				}
			}
		}
		// ����
		saveBXVO(bxvo);

		return false;
	}

	private void removeNullItem() {
		String[] bodyTableCodes = getCardPanel().getBillData()
				.getBodyTableCodes();
		for (String tableCode : bodyTableCodes) {
			// ����ҳǩ����
			BillScrollPane bodyPanel = getBxBillCardPanel().getBodyPanel(
					tableCode);
			BillItem[] bodyItems = bodyPanel.getTableModel().getBodyItems();
			int rowCount = bodyPanel.getTableModel().getRowCount();
			for (int i = rowCount - 1; i > 0; i--) {
				boolean isnull = true;
				for (BillItem item : bodyItems) {
					if (item.isShow()) {
						if (bodyPanel.getTableModel().getValueAt(i,
								item.getKey()) != null) {
							isnull = false;
							break;
						}
					}
				}
				if (isnull) {
					bodyPanel.delLine(new int[] { i });
				}
			}
		}
	}

	/**
	 * �ݴ浥��//���õ��ݵı���
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public void tempSave() throws Exception {

		JKBXVO bxvo = getBillValueVO();

		// �ж��Ǽ��Žڵ�
		String funcode = getMainPanel().getFuncCode();
		UFBoolean isGroup = BXUiUtil.isGroup(funcode);
		if (bxvo.getParentVO() != null) {
			if (org.apache.commons.lang.StringUtils.isEmpty(bxvo.getParentVO().getPk_org()) && !getBxParam().isInit()) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("expensepub_0",
								"02011002-0013")/* @res "�ݴ浥�ݱ�����λ(��λ)����Ϊ�գ�" */);
			}
		}

		if (getBxParam().isInit()) { // ���õ���

			String pk_group = bxvo.getParentVO().getPk_group();
			String pk_org = bxvo.getParentVO().getPk_org();
			String djlxbm = bxvo.getParentVO().getDjlxbm();

			if (!isGroup.booleanValue()) {
				if (pk_org == null || pk_org.trim().length() == 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("expensepub_0",
									"02011002-0014")/*
													 * @res
													 * "��֯�����õ��ݱ�����λ(��λ)����Ϊ�գ�"
													 */);
				}
			}

			List<JKBXHeaderVO> vos = getInitBillHeader(pk_group, pk_org, isGroup,
					djlxbm);

			if (vos != null
					&& vos.size() > 0
					&& !VOUtils.simpleEquals(vos.get(0).getPk_jkbx(), bxvo
							.getParentVO().getPk_jkbx())) {

				int result;
				if (isGroup.booleanValue()) {
					result = getMainPanel()
							.showYesNoMessage(
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("expensepub_0",
													"02011002-0015")/*
																	 * @res
																	 * "�õ������͵ĳ��õ����ڵ�ǰ�����Ѿ�����,�Ƿ���и���?"
																	 */);
				} else {
					result = getMainPanel()
							.showYesNoMessage(
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("expensepub_0",
													"02011002-0016")/*
																	 * @res
																	 * "�õ������͵ĳ��õ����ڵ�ǰ��λ�Ѿ�����,�Ƿ���и���?"
																	 */);
				}
				if (result == UIDialog.ID_YES) {
					JKBXHeaderVO headerVO = vos.get(0);
					headerVO.setInit(true);
					getIBXBillPublic().deleteBills(
							new JKBXVO[] { VOFactory.createVO(headerVO) });
					getVoCache().removeVO(VOFactory.createVO(headerVO));
				} else {
					return;
				}
			}
		}
		if (getBxParam().getIsQc()) {
			bxvo.getParentVO().setApprover("");
		}
		ErVOUtils.clearContrastInfo(bxvo);
		bxvo.getParentVO().setDjzt(BXStatusConst.DJZT_TempSaved);
		bxvo.getParentVO().setSxbz(BXStatusConst.SXBZ_NO);

		if (isGroup.booleanValue()) {
			bxvo.getParentVO().setIsinitgroup(UFBoolean.TRUE);
		} else {
			bxvo.getParentVO().setIsinitgroup(UFBoolean.FALSE);
		}

		saveBXVOBack(bxvo, true);

	}

	/**
	 * @param bxvo
	 *            Ҫ����ĵ��ݾۺ�VO
	 * @param isTempSave
	 *            �Ƿ��ݴ�
	 * @throws BusinessException
	 * 
	 *             ��̨���浥��
	 */
	private void saveBXVOBack(JKBXVO bxvo, boolean isTempSave)
			throws BusinessException {

		BXBillMainPanel mainPanel = getMainPanel();

		JKBXVO[] bxvos;
		Integer djzt = bxvo.getParentVO().getDjzt();// ����״̬
		try {
			if (StringUtils.isNullWithTrim(bxvo.getParentVO().getPk_jkbx())) {
				if (isTempSave) {
					if (djzt != BXStatusConst.DJZT_TempSaved) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
								.getNCLangRes().getStrByID("2011",
										"UPP2011-000363")/* @res "���ݴ�̬���ݲ����ݴ棡" */);
					}
					bxvos = getIBXBillPublic().tempSave(new JKBXVO[] { bxvo });
				} else if (bxvo.getParentVO().getQcbz().booleanValue()) {
					bxvo.getParentVO().setDjzt(BXStatusConst.DJZT_Sign);
					bxvo.getParentVO().setSxbz(BXStatusConst.SXBZ_VALID);
					bxvos = getIBXBillPublic().save(new JKBXVO[] { bxvo });
				} else {
					bxvo.getParentVO().setDjzt(BXStatusConst.DJZT_Saved);// ���ݴ浥�����ݴ�״̬תΪ����״̬
					bxvo.getParentVO().setSxbz(BXStatusConst.SXBZ_NO);
					bxvos = getIBXBillPublic().save(new JKBXVO[] { bxvo });
				}
			} else {
				if (isTempSave) {
					bxvo.getParentVO().setDjzt(BXStatusConst.DJZT_TempSaved);
					bxvo.getParentVO().setSxbz(BXStatusConst.SXBZ_NO);
				} else if (bxvo.getParentVO().getQcbz().booleanValue()) {
					bxvo.getParentVO().setDjzt(BXStatusConst.DJZT_Sign);// ���ݴ浥�����ݴ�״̬תΪ����״̬
					bxvo.getParentVO().setSxbz(BXStatusConst.SXBZ_VALID);
				} else {
					bxvo.getParentVO().setDjzt(BXStatusConst.DJZT_Saved);// ���ݴ浥�����ݴ�״̬תΪ����״̬
					bxvo.getParentVO().setSxbz(BXStatusConst.SXBZ_NO);
				}
				bxvo.setBxoldvo(getVoCache().getVOByPk(
						bxvo.getParentVO().getPk_jkbx()));
				bxvos = getIBXBillPublic().update(new JKBXVO[] { bxvo });
			}
			
			getVoCache().addVO(bxvos[0]);
			mainPanel.updateView();
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}
	}

	/**
	 * @param bxvo
	 * @throws Exception
	 * 
	 *             ���浥�ݣ� ��������
	 */
	private void saveBXVO(JKBXVO bxvo) throws Exception {

		JKBXVO[] bxvos = null;
		
		// ���ݴ浥��תΪ����״̬
		if (bxvo.getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved){
			bxvo.getParentVO().setDjzt(BXStatusConst.DJZT_Saved);
		}

		new VOChecker().checkSave(bxvo);

		bxvo.setSettlementInfo((SettlementAggVO) ((BXBillMainPanel) getActionRunntimeV0()).getAttribute(IRuntimeConstans.SettleVO));
		
		// �ڳ������ߺ�̨����
		if (bxvo.getParentVO().getQcbz().booleanValue()) {
			saveBXVOBack(bxvo, false);
		} else {
			try {
				if (StringUtils.isNullWithTrim(bxvo.getParentVO().getPk_jkbx())) {
					//���涯���ű�
					Object result = nc.ui.pub.pf.PfUtilClient.runAction(getMainPanel(), "SAVE", 
							bxvo.getParentVO().getDjlxbm(), bxvo, BXUiUtil.getPk_user(),null, null, null);
					if (result == null) {
						return;
					}
					bxvos = getBxvo(result);

				} else {
					bxvo.setBxoldvo(getVoCache().getVOByPk(bxvo.getParentVO().getPk_jkbx()));
					Object result = nc.ui.pub.pf.PfUtilClient.runAction(getMainPanel(), "EDIT", 
							bxvo.getParentVO().getDjlxbm(), bxvo, BXUiUtil.getPk_user(),null, null, null);
					bxvos = getBxvo(result);
				}
				if (bxvos == null || bxvos[0] == null) {
					return;
				}
				// ��ʾԤ�㣬�����Ƶ���ʾ��Ϣ
				if (!StringUtils.isNullWithTrim(bxvos[0].getWarningMsg())
						&& bxvos[0].getWarningMsg().length() > 0) {
					showWarningMsg(bxvos[0].getWarningMsg());
					bxvos[0].setWarningMsg(null);
				}
			} catch (BugetAlarmBusinessException e) {
				if (MessageDialog.showYesNoDlg(getParent(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/* @res "��ʾ" */, 
								e.getMessage()+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000364")
								/* @res* " �Ƿ�������棿"*/) == MessageDialog.ID_YES) {
					bxvo.setHasNtbCheck(Boolean.TRUE); // �����
					saveBXVO(bxvo);
					return;
				} else {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000405")
							/* @res "Ԥ������ʧ��"*/, e);
				}
			} catch (ErmException e) {
				if (MessageDialog.showYesNoDlg(getParent(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")
						/* @res "��ʾ" */, e.getMessage()+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000364")/*
																			 * @res
																			 * " �Ƿ�������棿"
																			 */) == MessageDialog.ID_YES) {
					bxvo.setHasJkCheck(Boolean.TRUE); // �����
					saveBXVO(bxvo);
					return;
				} else {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes()
							.getStrByID("2011", "UPP2011-000406")/*
																 * @res "�������ʧ��"
																 */, e);
				}
			} catch (CrossControlMsgException e) {
				if (MessageDialog
						.showYesNoDlg(getParent(), nc.vo.ml.NCLangRes4VoTransl
								.getNCLangRes().getStrByID("2011",
										"UPP2011-000049")/* @res "��ʾ" */, e
								.getMessage()
								+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
										.getStrByID("2011", "UPP2011-000364")/*
																			 * @res
																			 * " �Ƿ�������棿"
																			 */) == MessageDialog.ID_YES) {
					bxvo.setHasCrossCheck(Boolean.TRUE); // �����
					saveBXVO(bxvo);
					return;
				} else {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000417")/*
																 * @res "����У��ʧ��"
																 */, e);
				}
			} catch (ContrastBusinessException ex) {
				//�Ƿ�������
				if(ContrastBusinessExceptionType.FORCE.equals(ex.getType())){
					throw ex;
				}
			} catch (BusinessException e) {
				Log.getInstance(this.getClass()).error(e.getMessage());
				throw new BusinessException(e.getMessage(),e);
			}
//addedd by chendya from v6.0 ɾ��dr=1����(dr=1���б�����ֻ¼���ͷ���,����ʱ���⴦��������һ��ҵ���У���������Ŀ���Ǳ���ձ��嵥�ݲ�ѯ��������Ϊ��ѯģ��Ĭ�Ϸ��ص�sql����������.pk=�ӱ�.����pk)
//��������ҵ����������¼��ձ����
			BXBusItemVO[] bxBusItemVOS = bxvos[0].getBxBusItemVOS();
			List<BXBusItemVO> listItemVOs = new ArrayList<BXBusItemVO>();
			for (int i = 0; i < bxBusItemVOS.length; i++) {
				Integer dr = bxBusItemVOS[i].getDr();
				if (dr != null && dr == 1) {
					continue;
				}
				listItemVOs.add(bxBusItemVOS[i]);
			}
			bxvos[0].setBxBusItemVOS(listItemVOs.toArray(new BXBusItemVO[0]));
//--end
			getVoCache().addVO(bxvos[0]);
			getMainPanel().updateView();
		}
	}

	private JKBXVO[] getBxvo(Object result) {
		if (result instanceof JKBXVO[]) {
			return (JKBXVO[]) result;
		} else if (result instanceof MessageVO[]) {
			MessageVO[] vos = (MessageVO[]) result;
			return new JKBXVO[] { vos[0].getBxvo() };
		} else {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("expensepub_0", "02011002-0017")/*
																				 * @res
																				 * "�������ݸ�ʽ����ȷ!"
																				 */);
		}
	}
	public void prepareVO(JKBXVO bxvo) throws ValidationException {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		prepareContrast(bxvo);
		prepareHeader(parentVO, bxvo.getContrastVO());
	}

	/**
	 * ��ʼ���������VO
	 * 
	 * @param bxvo
	 */
	private void prepareContrast(JKBXVO bxvo) {
		if (getBxBillCardPanel().isContrast()) {
			List<BxcontrastVO> contrasts = getBxBillCardPanel().getContrasts();
			bxvo.setContrastVO(contrasts.toArray(new BxcontrastVO[] {}));
			bxvo.setContrastUpdate(true);
		} else {
			bxvo.setContrastUpdate(false);
			if (getVoCache().getVOByPk(bxvo.getParentVO().getPk_jkbx()) != null) {
				bxvo.setContrastVO(getVoCache().getVOByPk(
						bxvo.getParentVO().getPk_jkbx()).getContrastVO());
			}
		}
	}

}