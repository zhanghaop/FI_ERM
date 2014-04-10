package nc.ui.er.indauthorize.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.er.indauthorize.IIndAuthorizeQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.dbcache.DBCacheQueryFacade;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.ArrayUtils;

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
		//保存时脱离编辑状态，无数据时，则不需要，防止报空指针错
		if(getEditor().getModel().getRowCount()>0){
			getEditor().getBillCardPanel().stopEditing();
		}
		BatchOperateVO operVO = this.getModel().getCurrentSaveObject();
		Object[] addObjs = operVO.getAddObjs();
		Object[] updObjs = operVO.getUpdObjs();
		Object[] delObjs = operVO.getDelObjs();

		//判断如果更改需要更新数据
	    if(ArrayUtils.isEmpty(addObjs) && ArrayUtils.isEmpty(updObjs) && ArrayUtils.isEmpty(delObjs)){
			if(this.getModel().getUiState()==UIState.EDIT){
			    throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0013")/*@res " 请修改数据后，进行保存！"*/);
			}
		}
	    Object[] indVOs = this.getModel().getRows().toArray();


	    //人员对应的单据类型
	    HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
	    for(Object svo:indVOs){
			String code=(String) ((IndAuthorizeVO)svo).getAttributeValue("pk_operator");
			UFDate startDate = (UFDate) ((IndAuthorizeVO)svo).getAttributeValue("startdate");
			UFDate endDate = (UFDate) ((IndAuthorizeVO)svo).getAttributeValue("enddate");
			String billtype = (String) ((IndAuthorizeVO)svo).getAttributeValue("pk_billtypeid");


			if(code == null || "".equals(code)){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0014")/*@res "操作员不能为空"*/);
			}
			if(startDate == null){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0015")/*@res "开始日期不能为空"*/);
			}
			if(endDate == null){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0016")/*@res "结束日期不能为空"*/);
			}
			if(billtype == null || "".equals(billtype)){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0017")/*@res "交易类型不能为空"*/);
			}
			if(startDate.compareTo(endDate) > 0){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0018")/*@res "开始日期不能大于结束日期，请重新输入!"*/);
			}

			if(map.get(code)!=null&&map.get(code).contains(billtype)){
				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0019")/*@res "操作员不能授权相同的单据类型，请重新输入！"*/);
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
//	    	//增加，用来处理存入er_indauthorize 中单据类型为pk_billtypeid的情况
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
			String cid = ErUiUtil.getPk_user();
			String[] queryPsnidAndDeptid = null;
			try {
				queryPsnidAndDeptid = NCLocator.getInstance().lookup(IIndAuthorizeQueryService.class).queryPsnidAndDeptid(cid,ErUiUtil.getPK_group());
			} catch (ComponentException e2) {
				ExceptionHandler.consume(e2);
			} catch (BusinessException e2) {
				throw e2;
			}
			if (queryPsnidAndDeptid == null)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000065")/*@res "操作员对应的业务员为空，此节点不可用！"*/);
			else
				psndoc = queryPsnidAndDeptid[0];

		}
		return psndoc;
	}






}