package nc.ui.erm.billpub.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.arap.util.SqlUtils;
import nc.bs.erm.util.CacheUtil;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.erm.model.ERMBillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.dj.ERMDjCondVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.lang.UFBoolean;

import org.apache.commons.lang.ArrayUtils;

public class ErmBillBillManageModel extends ERMBillManageModel {

	/**
	 * 所有的单据类型
	 */
	private Map<String,DjLXVO> billTypeMapCache = new HashMap<String, DjLXVO>();  
	
	/**
	 * 当前单据类型编码
	 */
	private String currentBillTypeCode = null;  
	
	/**
	 * 交易类型按钮选择的单据类型编码
	 */
	private String selectBillTypeCode = null;  
	
	/**
	 * 是否追加数据
	 */
	private boolean isAppend = false;  
	
	/**
	 * 是否显示凭证号
	 */
	private boolean xspz = false;  
	
	private ERMDjCondVO djCondVO;
	
	/**
	 * 返回单据类型vo
	 * 
	 * @param billTypeCode
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public DjLXVO getCurrentDjlx(String billTypeCode) {
		if(billTypeMapCache.isEmpty()){
			initBillTypeMapCache();
		}
		
		return billTypeMapCache.get(billTypeCode);
	}

	/**
	 * 初始化缓存
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	
	private void initBillTypeMapCache() {
		String[] billType = new String[]{BXConstans.JK_DJDL,BXConstans.BX_DJDL};
		if(isInit()){
			billType = new String[]{BXConstans.JK_DJDL};
		}
		DjLXVO[] validDjlx = getDjlxvosByNodeCode(billType);
		if (ArrayUtils.isEmpty(validDjlx)) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000171")/**
			 * @res
			 *      * "该节点单据类型已被封存，不可操作节点！"
			 */
			);
		} else {
			// 此处默认读取第一个没有封存单据类型
			if(currentBillTypeCode == null){
				boolean allFcbz = true;
				for (DjLXVO djlx : validDjlx) {
					if (djlx.getFcbz().equals(UFBoolean.FALSE)) {
						setCurrentBillTypeCode(djlx.getDjlxbm());
						setSelectBillTypeCode(djlx.getDjlxbm());
						allFcbz = false;
						break;
					}
				}
				if (allFcbz) {
					// 全部封存单据类型时，取第一个
					setCurrentBillTypeCode(validDjlx[0].getDjlxbm());
					setSelectBillTypeCode(validDjlx[0].getDjlxbm());
				}
			}
		}
		
		for (DjLXVO djLXVO : validDjlx) {
			billTypeMapCache.put(djLXVO.getDjlxbm(), djLXVO);
		}
	}
 
	private DjLXVO[] getDjlxvosByNodeCode(String[] billType) {
		String group = WorkbenchEnvironment.getInstance().getGroupVO().getPrimaryKey();

		DjLXVO[] djLXVOs = null;
		String insql = SqlUtils.getInStr("djdl", billType);
		try {
			djLXVOs = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class," dr=0 and " + insql + "" +
					" and pk_group='" + group + "' order by djlxbm ");//按单据编号排序
		} catch (Exception e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		return djLXVOs;
	}

	public String getCurrentBillTypeCode() {
		if(currentBillTypeCode == null){
			if(selectBillTypeCode != null){
				currentBillTypeCode = selectBillTypeCode;
			}else{
				initBillTypeMapCache();
			}
		}
		return currentBillTypeCode;
	}

	public void setCurrentBillTypeCode(String currentBillTypeCode) {
		this.currentBillTypeCode = currentBillTypeCode;
	}

	public String getSelectBillTypeCode() {
		if(selectBillTypeCode == null){
			initBillTypeMapCache();
		}
		return selectBillTypeCode;
	}

	public void setSelectBillTypeCode(String selectBillTypeCode) {
		this.selectBillTypeCode = selectBillTypeCode;
	}

	public Map<String, DjLXVO> getBillTypeMapCache() {
		if (billTypeMapCache.isEmpty()) {
			initBillTypeMapCache();
		}
		return billTypeMapCache;
	}

	public DjLXVO getCurrentDjLXVO(){
		return getBillTypeMapCache().get(getCurrentBillTypeCode());
	}
	
	/**
	 * 是否是期初节点
	 * 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public boolean isInit(){
		return BXConstans.BXLR_QCCODE.equals(getContext().getNodeCode());
	}
	
	/**
	 * 是否常用单据
	 * 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public boolean iscydj() {
		return BXConstans.BXINIT_NODECODE_G.equals(getContext().getNodeCode())
				|| BXConstans.BXINIT_NODECODE_U.equals(getContext().getNodeCode());
	}

	public boolean isAppend() {
		return isAppend;
	}

	public void setAppend(boolean isAppend) {
		this.isAppend = isAppend;
	}
	
	public boolean isXspz(){
		return xspz;
	}
	
	public void setXspz(boolean xspz) {
		this.xspz = xspz;
	}
	

	public ERMDjCondVO getDjCondVO() {
		return djCondVO;
	}

	public void setDjCondVO(ERMDjCondVO djCondVO) {
		this.djCondVO = djCondVO;
	}

	/**
	 * 对于是否追加数据需要特殊处理
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initModel(Object data) {
		if(isAppend()){
			List<JKBXVO> oldData = getData();
			Map<String,JKBXVO> oldDataMap= new HashMap<String,JKBXVO>();
			//如果新查出来的数据在原数据中，就不再追加上。
			for (JKBXVO vo:oldData){
				if(oldDataMap.get(vo.getParentVO().getPk_jkbx())==null){
					oldDataMap.put(vo.getParentVO().getPk_jkbx(), vo);
				}
			}
			
			if(data!=null){
				Object[] objects = (Object[])data;
				JKBXVO[] jkbx=new JKBXVO[objects.length];
				if(data instanceof Object[]){
					for(int i=0;i<objects.length;i++){
						Object object = objects[i];
						if(object instanceof JKBXVO){
							jkbx[i]=(JKBXVO) object;
							if(!oldDataMap.keySet().contains(jkbx[i].getParentVO().getPk_jkbx())){
								oldData.add(jkbx[i]);
							}
						}
					}
				}
				super.initModel(oldData.toArray());
			}
			
		}else{
			super.initModel(data);
		}
	}
	
	
}
