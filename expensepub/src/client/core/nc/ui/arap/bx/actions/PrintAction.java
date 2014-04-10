package nc.ui.arap.bx.actions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.md.data.access.NCObject;
import nc.ui.arap.bx.print.ERMMDDataSource;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.print.MDDataSource;
import nc.ui.pub.print.PrintEntry;
import nc.ui.pub.print.TemplateContainer;
import nc.ui.pub.print.TemplateItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.CurrencyControlBO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.util.StringUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.print.PrintTempletmanageHeaderVO;

/**
 * ����Uif2��д������ӡ
 * 
 * @author chendya
 * 
 * 
 * 
 */

public class PrintAction extends BXDefaultAction {

	public static String[] getBodyUFDoubleFields() {
		return new String[] { BXBusItemVO.AMOUNT };
	}

	public static String[] getHeadUFDoubleFields() {
		return new String[] { JKBXHeaderVO.TOTAL };
	}

	private static JKBXVO getResetDigitVO(JKBXVO vo) {
		new CurrencyControlBO().dealBXVOdigit(vo);
		return vo;
	}
	
	private JKBXVO[] getResetDigitVO(JKBXVO[] vos){
		List<JKBXVO> voList = new ArrayList<JKBXVO>();
		if(vos!=null){
			for(JKBXVO vo : vos){
				voList.add(getResetDigitVO(vo));
			}
		}
		return voList.toArray(new JKBXVO[]{});
	}

	/**
	 * �Ƿ񵥾ݹ�����ѯ�ڵ㣬���ڳ��ڵ�
	 * 
	 * @return
	 */
	private boolean isBillMngQryQcNode() {
		final String nodeCode = getMainPanel().getNodeCode();
		return BXConstans.BXMNG_NODECODE.equals(nodeCode)
				|| BXConstans.BXBILL_QUERY.equals(nodeCode)
				|| BXConstans.BXLR_QCCODE.equals(nodeCode);
	}

	/**
	 * �Ƿ��б��ӡ����
	 * 
	 * @return
	 */
	private boolean isListView() {
		return BillWorkPageConst.LISTPAGE == getMainPanel().getCurrWorkPage();
	}

	public String getNodeKey(boolean isPrintList) {
		if (isPrintList) {
			return "list";
		}
		if (isBillMngQryQcNode()) {
			// ���ݹ������ݲ�ѯ���ڳ��ڵ�
			return getMainPanel().getCache().getCurrentDjlxbm();
		} else {
			// ����¼��ڵ�
			if (isPrintList) {
				return "list";
			}
		}
		return null;
	}

	protected PrintEntry getPrintEntry(boolean isPrintList) {
		PrintEntry printEntry = new PrintEntry(getMainPanel());
		printEntry.setTemplateID(BXUiUtil.getPK_group(), getNodeCode(),
				BXUiUtil.getPk_user(), null, getNodeKey(isPrintList));
		addDataSource(printEntry, isPrintList);
		return printEntry;
	}

	/**
	 * 
	 * @param printEntry
	 * @param isPrintList
	 *            �Ƿ��ӡ�б�
	 */
	private void addDataSource(PrintEntry printEntry, boolean isPrintList) {
		//������
		JKBXVO[] vos = getResetDigitVO(getSelBxvos());
		if (isListView()) {
			if (!isPrintList) {
				// �б������"��ӡ"
				for (int i = 0; i < vos.length; i++) {
					printEntry.setDataSource(getDataSource(new JKBXVO[]{vos[i]},
							getDataSourceVarList(printEntry, isPrintList)));
				}
			} else {
				// ��ӡ�б�
				printEntry.setDataSource(getDataSource(vos, getDataSourceVarList(printEntry, isPrintList)));
			}
		} else {
			printEntry.setDataSource(getDataSource(vos,getDataSourceVarList(printEntry, isPrintList)));
		}
	}

	/**
	 * ���ؿ�Ƭ�����ӡ����Դ����
	 * 
	 * @param printEntry
	 * @return
	 */
	private List<String> getDataSourceVarList(PrintEntry printEntry,
			boolean isPrintList) {
		List<String> dsVarList = new ArrayList<String>();
		try {
			// ����ȡ�ô�ӡģ��
			Field templates = printEntry.getClass().getDeclaredField(
					"templates");
			templates.setAccessible(true);
			TemplateContainer tplContainer = (TemplateContainer) templates
					.get(printEntry);
			List<TemplateItem> tplList = tplContainer.getItems();
			TemplateItem tplItem = tplList.get(0);
			// �ýڵ��������ӡģ��
			PrintTempletmanageHeaderVO[] tplHeaderVO = tplItem.getWaitingList();
			if (tplHeaderVO == null || tplHeaderVO.length == 0)
				return null;
			if (tplItem.getSelected() == null) {
				for (PrintTempletmanageHeaderVO sel : tplHeaderVO) {
					tplItem.selectTemplate(sel.getCtemplateid());
				}
			}
			// �Զ������
			dsVarList = tplItem.getSelected().getOldTemplate() != null 
						? tplItem.getSelected().getOldTemplate().getDSVarList()
						: Arrays.asList(new String[0]);
		} catch (Exception e) {
			ExceptionHandler.consume(e);;
		}
		
		return dsVarList;
	}

//	/**
//	 * ת����VO���������VO����Ԫ����ʧ��
//	 * 
//	 * @param vos
//	 * @return
//	 */
//	private BXVO[] convertBX2JK(BXVO[] vos) {
//		List<BXVO> list = new ArrayList<BXVO>();
//		for (int i = 0; i < vos.length; i++) {
//			list.add(convertBX2JK(vos[i]));
//		}
//		return list.toArray(new BXVO[0]);
//	}

//	private BXVO convertBX2JK(BXVO bxvo) {
//		if (bxvo.getParentVO().djdl.equals(BXConstans.JK_DJDL)) {
//			JKHeaderVO jkhead = new JKHeaderVO();
//			BXHeaderVO head = bxvo.getParentVO();
//			String[] attributeNames = head.getAttributeNames();
//			for (String attr : attributeNames) {
//				jkhead.setAttributeValue(attr, head.getAttributeValue(attr));
//			}
//			bxvo.setParentVO(jkhead);
//			BXBusItemVO[] bxBusItemVOS = bxvo.getBxBusItemVOS();
//			for (int i = 0; i < bxBusItemVOS.length; i++) {
//				bxBusItemVOS[i].setTablecode(BXConstans.BUS_PAGE_JK);
//			}
//			bxvo.setChildrenVO(bxBusItemVOS);
//		}
//		return getResetDigitVO(bxvo);
//	}
	
	ERMMDDataSource ds = null;
	
	private ERMMDDataSource getDS(List<String> dsVarList) {
		if (ds == null) {
			ds = new ERMMDDataSource(new MDDataSource(null, dsVarList), dsVarList);
		}
		return ds;
	}

	public ERMMDDataSource getDataSource(JKBXVO[] bxvos, List<String> dsVarList) {
		ERMMDDataSource ds = getDS(dsVarList);
		List<NCObject> ncObjectList = new ArrayList<NCObject>();
		for(JKBXVO vo:bxvos){
			ncObjectList.add(NCObject.newInstance(vo));
		}
		ds.setNCObjects((NCObject[])ncObjectList.toArray(new NCObject[0]));
		ds.setBxvos(bxvos);
		return ds;
	}

	/**
	 * Ԥ��
	 */
	public void printView() throws BusinessException {
		print(1, false);
	}

	/**
	 * ���
	 */
	public void Output() throws BusinessException {
		print(2, false);
	}

	/**
	 * ��ӡ��Ƭ
	 */
	public void printCard() throws BusinessException {
		print(0, false);
	}

	/**
	 * ��ӡ�б�
	 */
	public void printList() throws BusinessException {
		doPrintList(1);
	}

	/**
	 * ��ӡǰУ��
	 */
	private void chkBeforePrint() throws BusinessException {

		if (getSelBxvos() == null || getSelBxvos().length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("expensepub_0", "02011002-0010")/*
																				 * @res
																				 * "������ѡ��һ��Ҫ����ĵ���"
																				 */);
		}
	}

	/**
	 * ��Ƭ�����ӡ
	 * 
	 * @param type
	 * @param isPrintList
	 * @throws BusinessException
	 */
	private void print(int type, boolean isPrintList) throws BusinessException {
		
		// ��ӡǰУ��
		chkBeforePrint();

		if (getPrintEntry(isPrintList).selectTemplate() != 1) {
			return;
		}
		switch (type) {
		case 0:
			getPrintEntry(isPrintList).print();
			break;
		case 2:
			getPrintEntry(isPrintList).output();
			break;
		default:
			getPrintEntry(isPrintList).preview();
		}
	}

	/**
	 * ��ӡ�б�
	 */
	private void doPrintList(int type) throws BusinessException {
		print(type, true);
	}

	/**
	 * �б�����ӡ
	 */
	public void printBill() throws BusinessException {

		chkBeforePrint();

		print(0, false);
	}

	/**
	 * ��ʽ��ӡ
	 */
	public void printOfficial() throws BusinessException {
		JKBXVO[] selBxvos = getSelBxvos();
		for (JKBXVO vo : selBxvos) {
			if (!checkOfficialPrint(vo))
				return;
			printCard();
		}
	}

	/**
	 * ȡ����ʽ��ӡ
	 */
	@SuppressWarnings( { "deprecation" })
	public void cancelPrintOfficial() throws BusinessException {
		JKBXVO[] selBxvos = getSelBxvos();

		boolean hasnoOff = false;

		for (JKBXVO vo : selBxvos) {

			JKBXHeaderVO head = (JKBXHeaderVO) vo.getParentVO().clone();

			if (head.getOfficialprintuser() == null) {
				hasnoOff = true;
			}

			head.setOfficialprintdate(null);
			head.setOfficialprintuser(null);

			head = getIBXBillPublic().updateHeader(
					head,
					new String[] { JKBXHeaderVO.OFFICIALPRINTDATE,
							JKBXHeaderVO.OFFICIALPRINTUSER });

			vo.setParentVO(head);

			// ����vo����
			getVoCache().putVOArray(new JKBXVO[] { vo });

			getBillCardPanel().setHeadItem(JKBXHeaderVO.OFFICIALPRINTDATE, "");
			getBillCardPanel().setHeadItem(JKBXHeaderVO.OFFICIALPRINTUSER, "");
			getBillCardPanel().setHeadItem(JKBXHeaderVO.TS,
					vo.getParentVO().getTs());
		}

		getMainPanel().showWarningMessage(
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000415")/*
										 * @res "ȡ����ʽ��ӡ�ɹ�!"
										 */
						+ (hasnoOff ? nc.vo.ml.NCLangRes4VoTransl
								.getNCLangRes().getStrByID("2011",
										"UPP2011-000416")/*
														 * @res " ���ֵ���û����ʽ��ӡ!"
														 */: ""));
	}

	/**
	 * ��ʽ��ӡ���
	 */
	private boolean checkOfficialPrint(JKBXVO vo) throws BusinessException {

		JKBXHeaderVO head = (JKBXHeaderVO) vo.getParentVO().clone();

		if (!(head.getDjzt().intValue() == BXStatusConst.DJZT_Verified || head
				.getDjzt().intValue() == BXStatusConst.DJZT_Sign)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000017")/*
																		 * @res
																		 * "ֻ������˵ĵ��ݲ�������ʽ��ӡ"
																		 */);
		}
		try {
			String user = head.getOfficialprintuser();
			if (!StringUtils.isNullWithTrim(user)) {
				throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("2006030102", "UPP2006030102-000405")/*
																		 * @res
																		 * "�����ظ���ʽ��ӡ"
																		 */);
			}

			head.setOfficialprintuser(getBxParam().getPk_user());
			head.setOfficialprintdate(getBxParam().getSysDate());

			head = getIBXBillPublic().updateHeader(
					head,
					new String[] { JKBXHeaderVO.OFFICIALPRINTDATE,
							JKBXHeaderVO.OFFICIALPRINTUSER });

			vo.setParentVO(head);

			// ����vo����
			getVoCache().putVOArray(new JKBXVO[] { vo });

			getBillCardPanel().setHeadItem(JKBXHeaderVO.OFFICIALPRINTDATE,
					head.getOfficialprintdate());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.OFFICIALPRINTUSER,
					head.getOfficialprintuser());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.TS,
					vo.getParentVO().getTs());

		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return true;
	}
}