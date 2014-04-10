package nc.ui.arap.bx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.logging.Logger;
import nc.itf.fi.pub.SysInit;
import nc.ui.er.util.BXUiUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.CurrencyControlBO;
import nc.vo.arap.bx.util.Page;
import nc.vo.arap.bx.util.PageUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.fipub.exception.ExceptionHandler;

/**
 * @author twei
 * 
 *         借款报销VO缓存
 * 
 *         nc.ui.arap.bx.VOCache
 */
public class VOCache {

	private LinkedHashMap<String, JKBXVO> voCache = new LinkedHashMap<String, JKBXVO>(); // 当前单据Vo缓存

	private DjLXVO currentDjlx; // 当前单据类型VO

	private String currentDjpk; // 当前单据pk

	private DjLXVO[] djlxVOS; // 当前所有的单据类型

	private int maxRecords = 30; // 每页的记录数量

	private Page page; // 界面分页

	private Page queryPage; // 查询分页

	private boolean isChangeView = false;

	public boolean isChangeView() {
		return isChangeView;
	}

	public void setChangeView(boolean isChangeView) {
		this.isChangeView = isChangeView;
	}

	private final Map<String, Object> rawMap = new HashMap<String, Object>();

	public Object getAttribute(String key) {
		return rawMap.get(key);
	}

	public void setAttribute(String key, Object value) {
		rawMap.put(key, value);
	}

	public VOCache() {
		initialize();
	}

	private void initialize() {
		try {
//			setMaxRecords(SysInit.getParaInt(BXUiUtil.getPK_group(),
//					BXParamConstant.PARAM_PAGE_SIZE));
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			setMaxRecords(BXConstans.LIST_PAGE_SIZE);
		}
	}

	public Page getPage() {

//		if (page == null) {
//			setPage(new PageUtil(voCache.size(), Page.STARTPAGE,
//					getMaxRecords()));
//		}
//
//		return page;
		return new PageUtil(voCache.size(), Page.STARTPAGE,
				getMaxRecords());
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public DjLXVO[] getDjlxVOS() {
		return djlxVOS;
	}

	public void setDjlxVOS(DjLXVO[] djlxVOS) {
		this.djlxVOS = djlxVOS;
	}

	public LinkedHashMap<String, JKBXVO> getVoCache() {
		return voCache;
	}

	public String getCurrentDjdl() {
		return getCurrentDjlx() == null ? null : getCurrentDjlx().getDjdl();
	}

	public String getCurrentDjlxbm() {
		return getCurrentDjlx() == null ? null : getCurrentDjlx().getDjlxbm();
	}

	public JKBXVO getCurrentVOClone() {
		return (JKBXVO) getVOByPk(getCurrentDjpk()).clone();
	}

	public JKBXVO getCurrentVO() {
		return getVOByPk(getCurrentDjpk());
	}

	public JKBXVO getVOByPk(String djoid) {
		return getVoCache() == null || djoid == null ? null : getVoCache().get(
				djoid);
	}

	public void putVOArray(JKBXVO[] vos) {
		if (vos == null || vos.length == 0)
			return;
		for (JKBXVO vo : vos) {
			if (getVoCache().containsKey(vo.getParentVO().getPk_jkbx())) {
				if (vo.getChildrenVO() == null
						|| vo.getChildrenVO().length == 0) {
					vo.setChildrenVO(getVoCache().get(
							vo.getParentVO().getPk_jkbx()).getChildrenVO());
				}
			}
			getVoCache().put(vo.getParentVO().getPk_jkbx(), vo);
		}
		setCurrentDjpk(vos[0].getParentVO().getPk_jkbx());
		setChangeView(true);
	}

	public void addVO(JKBXVO vo) {

		if (vo == null)
			return;

		//根据币种处理VO精度
		CurrencyControlBO currencyControlBO = new CurrencyControlBO();
		currencyControlBO.dealBXVOdigit(vo);
		
		if (getVoCache().containsKey(vo.getParentVO().getPk_jkbx())) {
			getVoCache().put(vo.getParentVO().getPk_jkbx(), vo);
		} else {
			// 更新分页信息
			getPage().setTotalNumberOfElements(getPage().getTotalNumberOfElements() + 1);

			// 将新增加的单据加到当前页的第一个位置,其他元素后移.
			LinkedHashMap<String, JKBXVO> newMap = new LinkedHashMap<String, JKBXVO>();
			LinkedHashMap<String, JKBXVO> voCache = getVoCache();
			int start = getPage().getThisPageFirstElementNumber();
			int i = 0;
			Set<String> keys = voCache.keySet();

			for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
				if (i == start) {
					newMap.put(vo.getParentVO().getPk_jkbx(), vo);
					start--;
				} else {
					String key = iter.next();
					newMap.put(key, voCache.get(key));
					i++;
				}
			}
			if (keys.size() == 0) {
				newMap.put(vo.getParentVO().getPk_jkbx(), vo);
			}
			// 更新VoCache
			this.voCache = newMap;
			setChangeView(true);
		}

		setCurrentDjpk(vo.getParentVO().getPk_jkbx());

	}

	public void removeVO(JKBXVO vo) {

		String pk = vo.getParentVO().getPrimaryKey();

		getVoCache().remove(pk);

		if (currentDjpk != null && currentDjpk.equals(pk)) {
			currentDjpk = null;
		}

		// 更新分页信息
		getPage().setTotalNumberOfElements(
				getPage().getTotalNumberOfElements() - 1);
		setChangeView(true);

	}

	public void removeVOList(Collection<JKBXVO> vos) {
		for (Iterator<JKBXVO> iter = vos.iterator(); iter.hasNext();) {
			JKBXVO vo = iter.next();
			removeVO(vo);
		}
		setChangeView(true);
	}

	public DjLXVO getCurrentDjlx() {
		return currentDjlx;
	}

	public void setCurrentDjlx(DjLXVO currentDjlx) {
		this.currentDjlx = currentDjlx;
	}

	public void setCurrentDjlxByPk(String djlxbm) {
		if (djlxVOS == null)
			return;
		for (DjLXVO vo : djlxVOS) {
			if (vo.getDjlxbm().equals(djlxbm)) {
				setCurrentDjlx(vo);
			}
		}
	}

	public String getCurrentDjpk() {
		return currentDjpk;
	}

	public void setCurrentDjpk(String currentDjpk) {
		this.currentDjpk = currentDjpk;
	}

	/**
	 * @return 选择的vo列表
	 */
	public List<JKBXVO> getSelectedVOs() {
		List<JKBXVO> selectedVOs = new ArrayList<JKBXVO>();

		for (Iterator<String> iter = voCache.keySet().iterator(); iter
				.hasNext();) {
			String key = iter.next();
			if (voCache.get(key).getParentVO().getSelected().booleanValue()) {
				selectedVOs.add(voCache.get(key));
			}
		}
		return selectedVOs;
	}

	/**
	 * @return 选择的vo列表，返回clone vo
	 */
	public List<JKBXVO> getSelectedVOsClone() {
		List<JKBXVO> selectedVOs = new ArrayList<JKBXVO>();

		for (Iterator<String> iter = voCache.keySet().iterator(); iter
				.hasNext();) {
			String key = iter.next();
			if (voCache.get(key).getParentVO().getSelected().booleanValue()) {
				selectedVOs.add((JKBXVO) voCache.get(key).clone());
			}
		}
		return selectedVOs;
	}
	public List<JKBXVO> getDisplayVOsClone() {
		List<JKBXVO> selectedVOs = new ArrayList<JKBXVO>();

		for (Iterator<String> iter = voCache.keySet().iterator(); iter
				.hasNext();) {
			String key = iter.next();
				selectedVOs.add((JKBXVO) voCache.get(key).clone());
		}
		return selectedVOs;
	}

	
	public int getMaxRecords() {
		return maxRecords;
	}

	public void setMaxRecords(int maxRecords) {
		this.maxRecords = maxRecords;
	}

	public void clearVOData() {
		currentDjpk = null;
		setChangeView(true);
		voCache = new LinkedHashMap<String, JKBXVO>();
	}

	public DjLXVO getDjlxVO(String djlxbm) {

		if (djlxbm == null || djlxVOS == null)
			return null;

		for (DjLXVO djlx : djlxVOS) {
			if (djlx.getDjlxbm().equals(djlxbm)) {
				return djlx;
			}
		}

		return null;
	}

	/**
	 * @return 返回当前页的VO列表
	 */
	public JKBXHeaderVO[] getCurrentPageVOs() {
		Collection<JKBXVO> vos = voCache.values();

//		int start = getPage().getThisPageFirstElementNumber();
//		int end = getPage().getThisPageLastElementNumber();

		int i = 0;
		List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
		for (Iterator<JKBXVO> iter = vos.iterator(); iter.hasNext(); i++) {

			JKBXHeaderVO parentVO = iter.next().getParentVO();

//			if (i < start)
//				continue;
//			if (i > end)
//				break;

			list.add(parentVO);

		}
		return list.toArray(new JKBXHeaderVO[] {});
	}

	public Page getQueryPage() {
		return queryPage;
	}

	public void setQueryPage(Page queryPage) {
		this.queryPage = queryPage;
	}
}
