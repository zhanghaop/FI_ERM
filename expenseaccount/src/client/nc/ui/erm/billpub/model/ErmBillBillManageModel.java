package nc.ui.erm.billpub.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.erm.model.ERMBillManageModel;
import nc.ui.erm.util.TransTypeUtil;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.dj.ERMDjCondVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

import org.apache.commons.lang.ArrayUtils;

public class ErmBillBillManageModel extends ERMBillManageModel {
	
	/**
	 * �����������뵥�Ĺ����ֶ�ֵ����
	 */
	private Map<String, IConstEnum[]> marelationValueMap = new HashMap<String, IConstEnum[]>();
	
	public void addMaRelationValues(String maids,IConstEnum[] values){
		marelationValueMap.put(maids, values);
	}
	public IConstEnum[] getMaReationValues(String maids){
		return marelationValueMap.get(maids);
	}

	/**
	 * ���еĵ�������
	 */
	private Map<String,DjLXVO> billTypeMapCache = new HashMap<String, DjLXVO>();  
	
	/**
	 * ��ǰ�������ͱ���
	 */
	private String currentBillTypeCode = null;  
	
	/**
	 * �������Ͱ�ťѡ��ĵ������ͱ���
	 */
	private String selectBillTypeCode = null;  
	
	/**
	 * �Ƿ�׷������
	 */
	private boolean isAppend = false;  
	
	/**
	 * �Ƿ���ʾƾ֤��
	 */
	private boolean xspz = false;  
	
	private ERMDjCondVO djCondVO;
	
	/**
	 * ���ص�������vo
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
	 * ��ʼ������
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	
	private void initBillTypeMapCache() {
		//���õ�ǰ���ܽڵ�Ľ�������
		String nodeCode = getContext().getNodeCode();
		if(!nodeCode.equals(BXConstans.BXLR_QCCODE)&& !nodeCode.equals(BXConstans.BXMNG_NODECODE)&& !nodeCode.equals(BXConstans.BXBILL_QUERY) &&
		        !nodeCode.equals(BXConstans.BXINIT_NODECODE_G) && !nodeCode.equals(BXConstans.BXINIT_NODECODE_U) &&!nodeCode.equals(BXConstans.MONTHEND_DEAL)){
			String transtype = TransTypeUtil.getTranstype(this);
		    setCurrentBillTypeCode(transtype);
		    setSelectBillTypeCode(transtype);
		    DjLXVO djlxVO;
			try {
				djlxVO = ErmDjlxCache.getInstance().getDjlxVO(getContext().getPk_group(), transtype);
				billTypeMapCache.put(transtype, djlxVO);
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		}else{
			String[] billType = new String[]{BXConstans.JK_DJDL,BXConstans.BX_DJDL};
			if(isInit()){
				billType = new String[]{BXConstans.JK_DJDL};
			}
			DjLXVO[] validDjlx = getDjlxvosByNodeCode(billType);
			if (ArrayUtils.isEmpty(validDjlx)) {
				throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000171")/**
				 * @res
				 *      * "�ýڵ㵥�������ѱ���棬���ɲ����ڵ㣡"
				 */
				);
			} else {
				// �˴�Ĭ�϶�ȡ��һ��û�з�浥������
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
						// ȫ����浥������ʱ��ȡ��һ��
						setCurrentBillTypeCode(validDjlx[0].getDjlxbm());
						setSelectBillTypeCode(validDjlx[0].getDjlxbm());
					}
				}
			}
			
			for (DjLXVO djLXVO : validDjlx) {
				billTypeMapCache.put(djLXVO.getDjlxbm(), djLXVO);
			}
		}
	}
 
	private DjLXVO[] getDjlxvosByNodeCode(String[] billType) {
		String group = WorkbenchEnvironment.getInstance().getGroupVO().getPrimaryKey();

		DjLXVO[] djLXVOs = null;
		try {
			String insql = SqlUtils.getInStr("djdl", billType, true);
			djLXVOs = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, " dr=0 and " + insql + ""
					+ " and pk_group='" + group + "' order by djlxbm ");// �����ݱ������
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
	 * �Ƿ����ڳ��ڵ�
	 * 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public boolean isInit(){
		return BXConstans.BXLR_QCCODE.equals(getContext().getNodeCode());
	}
	
	/**
	 * �Ƿ��õ���
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
	 * �����Ƿ�׷��������Ҫ���⴦��
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initModel(Object data) {
		if(isAppend()){
			List<JKBXVO> oldData = getData();
			Map<String,JKBXVO> oldDataMap= new HashMap<String,JKBXVO>();
			//����²������������ԭ�����У��Ͳ���׷���ϡ�
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
