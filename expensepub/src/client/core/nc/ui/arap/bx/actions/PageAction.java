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
 *         翻页活动
 * 
 *         调用缓存的翻页组件 的 previous 和 next 方法进行上翻下翻, 再更新视图
 * 
 *         next的时候如果缓存记录不足，需要调用查询
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
	 * 翻到首页
	 * 
	 * @throws BusinessException
	 */
	public void first() throws BusinessException {
		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0083")
		/*
		 * @res "翻到首页!"
		 */);

		String currentDjpk = getVoCache().getCurrentDjpk();
		
		JKBXHeaderVO[] bodyValueVOs = (JKBXHeaderVO[]) getBillListPanel().getHeadBillModel().getBodyValueVOs(getCurrentBodyVOName());

		if (bodyValueVOs == null || bodyValueVOs.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("expensepub_0", "02011002-0009")/*
																				 * @res
																				 * "列表界面无数据!"
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
		 * @res "已经翻到首页!"
		 */);
		
	}

	/**
	 * 翻到最有一页
	 * 
	 * @throws BusinessException
	 */
	public void last() throws BusinessException {
		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0084")
		/*
		 * @res "翻到末页!"
		 */);
		
		String currentDjpk = getVoCache().getCurrentDjpk();
		
		JKBXHeaderVO[] bodyValueVOs = (JKBXHeaderVO[]) getBillListPanel().getHeadBillModel().getBodyValueVOs(getCurrentBodyVOName());

		if (bodyValueVOs == null || bodyValueVOs.length == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("expensepub_0", "02011002-0009")/*
																				 * @res
																				 * "列表界面无数据!"
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
		 * @res "已经翻到末页!"
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
//		 * @res "翻到上一页!"
//		 */);
//		
		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0081")
		/*
		 * @res "翻到上一页!"
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
		 * @res "翻到上一页!"
		 */);

//		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
//				.getStrByID("2011v61013_0", "02011v61013-0079")
//		/*
//		 * @res "已经翻到上一页!"
//		 */);
	}

	public void next() throws BusinessException {

		showStatusMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2011v61013_0", "02011v61013-0082")
		/*
		 * @res "翻到下一页!"
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
		 * @res "已经翻到下一页!"
		 */);
	}

	/**
	 * 根据偏移量转到相应的页面
	 * @param mainPanel 主面板
	 * @param offset 偏移量
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
																				 * "列表界面无数据!"
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
			// 前面没有数据了
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("2006030102", "UPP2006030102-000005")/*
																	 * @res
																	 * "已经是第一张单据"
																	 */);
		}
		if (pos == bodyValueVOs.length) {
			// 后面没有数据了
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("2006030102", "UPP2006030102-000002")/*
																	 * @res
																	 * "已经是最后一张单据"
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
																 * "单据类型信息丢失!"
																 */);

				getVoCache().setCurrentDjlx(djlxVO);

				getVoCache().setCurrentDjpk(zbvo.getParentVO().getPk_jkbx());

				mainPanel.loadCardTemplet();
			}

			if ((zbvo.getChildrenVO() == null || zbvo.getChildrenVO().length == 0)) { // 初始化表体vo
				zbvo = retrieveChidren(zbvo);
			}

			// 更新缓存中的数据
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
				.size() - 1) { // 缓存记录不足，需要调用查询

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
																		 * "到达最后一页,已无数据!"
																		 */);

		}

	}

}