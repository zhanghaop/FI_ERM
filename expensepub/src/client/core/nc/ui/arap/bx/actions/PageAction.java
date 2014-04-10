package nc.ui.arap.bx.actions;

import java.util.List;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.er.pub.BillWorkPageConst;
import nc.vo.arap.bx.util.Page;
import nc.vo.arap.bx.util.PageUtil;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;

/**
 * @author twei
 * 
 *         ��ҳ�
 * 
 *         ���û���ķ�ҳ��� �� previous �� next ���������Ϸ��·�, �ٸ�����ͼ
 * 
 *         next��ʱ����������¼���㣬��Ҫ���ò�ѯ
 * 
 * @see PageUtil
 * 
 *      nc.ui.arap.bx.actions.PageAction
 */
public class PageAction extends BXDefaultAction {

	@SuppressWarnings("deprecation")
	private void showStatusMessage(String msg) {
		getMainPanel().showHintMessage(msg);
	}

	/**
	 * ������ҳ
	 * 
	 * @throws BusinessException
	 */
	public void first() throws BusinessException {
		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0083")
		/*
		 * @res "������ҳ!"
		 */);

		String currentDjpk = getVoCache().getCurrentDjpk();
		
		JKBXHeaderVO[] bodyValueVOs = (JKBXHeaderVO[]) getBillListPanel().getHeadBillModel().getBodyValueVOs(getCurrentBodyVOName());

		if (bodyValueVOs == null || bodyValueVOs.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("expensepub_0", "02011002-0009")/*
																				 * @res
																				 * "�б����������!"
																				 */);
		}
		int offset = 0;
		for (int i = 0; i < bodyValueVOs.length; i++) {
			if(bodyValueVOs[i].getPrimaryKey().equals(currentDjpk)){
				offset = i;
				break;
			}
		}
		
		try {
			setDataVO(getMainPanel(), -offset);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		getMainPanel().updateView();

		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0085")
		/*
		 * @res "�Ѿ�������ҳ!"
		 */);
		
	}

	/**
	 * ��������һҳ
	 * 
	 * @throws BusinessException
	 */
	public void last() throws BusinessException {
		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0084")
		/*
		 * @res "����ĩҳ!"
		 */);
		
		String currentDjpk = getVoCache().getCurrentDjpk();
		
		JKBXHeaderVO[] bodyValueVOs = (JKBXHeaderVO[]) getBillListPanel().getHeadBillModel().getBodyValueVOs(getCurrentBodyVOName());

		if (bodyValueVOs == null || bodyValueVOs.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("expensepub_0", "02011002-0009")/*
																				 * @res
																				 * "�б����������!"
																				 */);
		}
		int offset = 0;
		for (int i = 0; i < bodyValueVOs.length; i++) {
			if(bodyValueVOs[i].getPrimaryKey().equals(currentDjpk)){
				offset = bodyValueVOs.length - i - 1;
				break;
			}
		}

		try {
			setDataVO(getMainPanel(), offset);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		getMainPanel().updateView();

		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0086")
		/*
		 * @res "�Ѿ�����ĩҳ!"
		 */);
	}
	
	
	private String getCurrentBodyVOName() throws ValidationException{
		
		String djdl = this.getBillValueVO().getParentVO().getDjdl();
		
		if(djdl != null && djdl.equals("bx")){
			return BXHeaderVO.class.getName();
		}else if(djdl != null && djdl.equals("jk")){
			return JKHeaderVO.class.getName();
		}
		
		return null;
	}

	public void previous() throws BusinessException {
//		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
//				.getStrByID("2011v61013_0", "02011v61013-0081")
//		/*
//		 * @res "������һҳ!"
//		 */);
//		
		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0081")
		/*
		 * @res "������һҳ!"
		 */);

		try {
			setDataVO(getMainPanel(), -1);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		getMainPanel().updateView();
		
		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0080")
		/*
		 * @res "������һҳ!"
		 */);

//		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
//				.getStrByID("2011v61013_0", "02011v61013-0079")
//		/*
//		 * @res "�Ѿ�������һҳ!"
//		 */);
	}

	public void next() throws BusinessException {

		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0082")
		/*
		 * @res "������һҳ!"
		 */);

		BXBillMainPanel mainPanel = getMainPanel();

		try {
			setDataVO(mainPanel, 1);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		listPageNext();
		getMainPanel().updateView();

		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0079")
		/*
		 * @res "�Ѿ�������һҳ!"
		 */);
	}

	/**
	 * ����ƫ����ת����Ӧ��ҳ��
	 * @param mainPanel �����
	 * @param offset ƫ����
	 * @throws Exception
	 */
	private void setDataVO(BXBillMainPanel mainPanel, int offset) throws Exception {

//		JKBXVO zbvo = getVoCache().getCurrentVO();

//		JKBXVO[] values = getVoCache().getVoCache().values().toArray(new JKBXVO[] {});
//		
//		List<JKBXHeaderVO> jkbxList = new ArrayList<JKBXHeaderVO>();
//		List<String> keyList = new ArrayList<String>();
//		
//		for (JKBXVO vo : values) {
//			if(keyList.contains(vo.getParentVO().getPrimaryKey())){
//				continue;
//			}
//			jkbxList.add(vo.getParentVO());
//			keyList.add(vo.getParentVO().getPrimaryKey());
//		}
		
//		JKBXHeaderVO[] bodyValueVOs = (JKBXHeaderVO[])jkbxList.toArray(new JKBXHeaderVO[0]);
		
		JKBXHeaderVO[] bodyValueVOs = (JKBXHeaderVO[]) getBillListPanel().getHeadBillModel().getBodyValueVOs(getCurrentBodyVOName());
		
//		int i = 0;
//		for (JKBXVO svo : values) {
//			if (svo.getParentVO().getPrimaryKey().equals(getVoCache().getCurrentDjpk())) {
//				break;
//			}
//			i++;
//		}

		mainPanel.updateListView();

		if (bodyValueVOs == null || bodyValueVOs.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("expensepub_0", "02011002-0009")/*
																				 * @res
																				 * "�б����������!"
																				 */);
		}

		int currentPos = 0;
		for (JKBXHeaderVO svo : bodyValueVOs) {
			if (svo.getPrimaryKey().equals(getVoCache().getCurrentDjpk())) {
				break;
			}
			currentPos++;
		}

		int pos = currentPos + offset;

		// mainPanel.updateView();

		if (pos == -1) {
			// ǰ��û��������
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("2006030102", "UPP2006030102-000005")/*
																	 * @res
																	 * "�Ѿ��ǵ�һ�ŵ���"
																	 */);
		}
		if (pos == bodyValueVOs.length) {
			// ����û��������
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("2006030102", "UPP2006030102-000002")/*
																	 * @res
																	 * "�Ѿ������һ�ŵ���"
																	 */);
		}
		
		JKBXVO zbvo = getVoCache().getVOByPk(bodyValueVOs[pos].getPrimaryKey());

		if (zbvo != null) {
			
			getVoCache().setCurrentDjpk(zbvo.getParentVO().getPk_jkbx());

			String djlxbm = getVoCache().getCurrentDjlxbm();

			JKBXHeaderVO parentVO = zbvo.getParentVO();

			if (djlxbm == null || !djlxbm.equals(parentVO.getDjlxbm())) {

				DjLXVO djlxVO = getVoCache().getDjlxVO(parentVO.getDjlxbm());

				if (djlxVO == null)
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes()
							.getStrByID("2011", "UPP2011-000008")/*
																 * @res
																 * "����������Ϣ��ʧ!"
																 */);

				getVoCache().setCurrentDjlx(djlxVO);

				getVoCache().setCurrentDjpk(zbvo.getParentVO().getPk_jkbx());

				mainPanel.loadCardTemplet();
			}

			if ((zbvo.getChildrenVO() == null || zbvo.getChildrenVO().length == 0)) { // ��ʼ������vo
				zbvo = retrieveChidren(zbvo);
			}

			// ���»����е�����
			getVoCache().addVO(zbvo);

		}

		if (!mainPanel.isCardTemplateLoaded()) {
			mainPanel.loadCardTemplet();
		}

		getBxBillCardPanel().setBillValueVO(zbvo);
		getBxBillCardPanel().execHeadTailLoadFormulas();

		mainPanel.setCurrentPageStatus(BillWorkPageConst.WORKSTAT_BROWSE);
	}

	private void listPageNext() throws BusinessException {
		Page page = getVoCache().getPage();

		page.next();

		if (page.getThisPageLastElementNumber() > getVoCache().getVoCache()
				.size() - 1) { // �����¼���㣬��Ҫ���ò�ѯ

			if (!getVoCache().getQueryPage().hasNextPage()) {

				suspendLastPage(page);
			}

			getVoCache().getQueryPage().next();

			List<JKBXHeaderVO> vos = queryHeadersByPage(getVoCache()
					.getQueryPage(), getCondVO());

			getMainPanel().appendListVO(vos);

			if (page.getThisPageLastElementNumber() > getVoCache().getVoCache()
					.size() - 1) {

				suspendLastPage(page);

			}

		}

		getMainPanel().updateView();
	}

	public void suspendLastPage(Page page) throws BusinessException {

		getVoCache().setPage(
				new PageUtil(getVoCache().getVoCache().size(), page
						.getThisPageNumber(), page.getPageSize()));
		getVoCache().getPage().setThisPageNumber(
				getVoCache().getPage().getLastPageNumber());

		if (page.getThisPageFirstElementNumber() > getVoCache().getVoCache()
				.size() - 1) {

			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000015")/*
																		 * @res
																		 * "�������һҳ,��������!"
																		 */);

		}

	}

}