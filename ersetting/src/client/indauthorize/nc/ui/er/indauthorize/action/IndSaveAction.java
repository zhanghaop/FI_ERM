package nc.ui.er.indauthorize.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.dbcache.DBCacheQueryFacade;
import nc.ui.er.util.BXUiUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public class IndSaveAction extends BatchSaveAction {

	/**
	 * @author liansg
	 */
	private static final long serialVersionUID = 1L;
	private String psndoc = null;
	private IActionRuntime actionRunntimeV0 = null;

	public void setActionRunntimeV0(IActionRuntime runtime) {
		actionRunntimeV0 = runtime;
	}

	protected IActionRuntime getActionRunntimeV0() {
		return actionRunntimeV0;
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		//����ʱ����༭״̬
		getEditor().getBillCardPanel().stopEditing();
		BatchOperateVO operVO = this.getModel().getCurrentSaveObject();
		Object[] addObjs = operVO.getAddObjs();
		Object[] updObjs = operVO.getUpdObjs();
		Object[] delObjs = operVO.getDelObjs();

		//�ж����������Ҫ��������
	    if(addObjs.length ==0 && updObjs.length ==0 && delObjs.length ==0){
			if(this.getModel().getUiState()==UIState.EDIT){
				if(updObjs==null || updObjs.length==0)
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0013")/*@res " ���޸����ݺ󣬽��б��棡"*/);
			}
		}
	    Object[] indVOs = this.getModel().getRows().toArray();


	    //��Ա��Ӧ�ĵ�������
	    HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
	    for(Object svo:indVOs){
			String code=(String) ((IndAuthorizeVO)svo).getAttributeValue("pk_operator");
			UFDate startDate = (UFDate) ((IndAuthorizeVO)svo).getAttributeValue("startdate");
			UFDate endDate = (UFDate) ((IndAuthorizeVO)svo).getAttributeValue("enddate");
			String billtype = (String) ((IndAuthorizeVO)svo).getAttributeValue("pk_billtypeid");


			if(code == null || "".equals(code)){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0014")/*@res "����Ա����Ϊ��"*/);
			}
			if(startDate == null || "".equals(startDate)){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0015")/*@res "��ʼ���ڲ���Ϊ��"*/);
			}
			if(endDate == null || "".equals(endDate)){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0016")/*@res "�������ڲ���Ϊ��"*/);
			}
			if(billtype == null || "".equals(billtype)){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0017")/*@res "�������Ͳ���Ϊ��"*/);
			}
			if(startDate.compareTo(endDate) > 0){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0018")/*@res "��ʼ���ڲ��ܴ��ڽ������ڣ�����������!"*/);
			}

			if(map.get(code)!=null&&map.get(code).contains(billtype)){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0019")/*@res "����Ա������Ȩ��ͬ�ĵ������ͣ����������룡"*/);
			}else if(map.get(code)==null){
				ArrayList<String> list = new ArrayList<String>();
				list.add(billtype);
				map.put(code, list);
			}else if(!map.get(code).contains(billtype)){
				ArrayList<String> list = map.get(code);
				list.add(billtype);
				map.put(code, list);
			}
	    }
	    for(Object obj:indVOs){
	    	String psndoc=getPsndocByUser();
	    	String billtypeid = (String) ((IndAuthorizeVO)obj).getAttributeValue("pk_billtypeid");
	    	String pk_org = (String) ((IndAuthorizeVO)obj).getAttributeValue("pk_org");
//	    	//���ӣ������������er_indauthorize �е�������Ϊpk_billtypeid�����
	    	String sql = "select distinct pk_billtypecode from bd_billtype where pk_billtypeid=?";
	    	  SQLParameter param = new SQLParameter();
	    	  Object[] arrs = null;
	    	  param.addParam(billtypeid);
	    	  List res =  (List)DBCacheQueryFacade.runQueryByPk(sql, param, new ArrayListProcessor());
	    	  String billtype = null;
	    	  if (res != null && res.size() != 0) {
	    		  arrs = (Object[]) res.get(0);
	    		  billtype= ((String)arrs[0]);
	    	  }
	    	((IndAuthorizeVO)obj).setPk_user(psndoc);
	    	((IndAuthorizeVO)obj).setType(1);
	    	((IndAuthorizeVO)obj).setEnddate(((IndAuthorizeVO)obj).getEnddate().asEnd());
	    	((IndAuthorizeVO)obj).setBilltype(billtype);
	    	((IndAuthorizeVO)obj).setPk_billtypeid(billtypeid);
	    	((IndAuthorizeVO)obj).setPk_org(pk_org);

	    }
	    super.doAction(e);
	}
	private String getPsndocByUser() throws BusinessException {
		if(psndoc == null){
			String cid = BXUiUtil.getPk_user();
			String[] queryPsnidAndDeptid = null;
			try {
				queryPsnidAndDeptid = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPsnidAndDeptid(cid, BXUiUtil.getPK_group());
			} catch (ComponentException e2) {
				ExceptionHandler.consume(e2);
			} catch (BusinessException e2) {
				throw e2;
			}
			if (queryPsnidAndDeptid == null)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000065")/*@res "����Ա��Ӧ��ҵ��ԱΪ�գ��˽ڵ㲻���ã�"*/);
			else
				psndoc = queryPsnidAndDeptid[0];

		}
		return psndoc;
	}






}