package nc.impl.erm.common;

import java.util.ArrayList;
import java.util.List;

import nc.bs.bd.assignservice.bizvalidate.ICheckResultTempTbInfo;
import nc.bs.bd.assignservice.bizvalidate.INewVisibleDocTempTbInfo;
import nc.bs.bd.assignservice.bizvalidate.IVisibleLostValidateContext;
import nc.bs.bd.pub.IBDEventType;
import nc.bs.bd.util.DBAUtil;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.IEventType;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.itf.bd.pub.IBDMetaDataIDConst;
import nc.vo.pub.BusinessException;

/**
 * 客户和供应商档案的取消分配校验和参数改为强分配校验监听
 * @author wangled
 *
 */
public class ErmBdDocVisibleListener implements IBusinessListener{
	// 引用档案名称/单据名称
	public String[] errorMsg = {"客户","供应商"};
	
	public String[] customerTb_N1 = {"er_bxzb","er_jkzb","er_costshare","er_cshare_detail"};//存放客户档案存在档案的表名1
	
	public String[] customerTb_N2 = {"er_mtapp_detail"}; //存放客户档案存在档案的表名2
	
	public String[] supplierTb_N1 = {"er_bxzb","er_jkzb","er_costshare","er_cshare_detail"}; //存放供应商档案存在档案的表名1

	public String[] supplierTb_N2 = {"er_mtapp_detail"}; //存放供应商档案存在档案的表名2
	
	/**注释引用档案主键： docPk_1对应**Tb_N1 ; docPk_2对应**Tb_N2 */
	public String[] docPk_1 ={"customer","hbbm"};
	public String[] docPk_2 ={"pk_customer","pk_supplier"};
	
	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		//检查可见性范围，取消分配前校验和参数改为强分配校验
		if(!IBDMetaDataIDConst.CUSTORG.equals(event.getSourceID())&&
				!IBDMetaDataIDConst.SUPORG.equals(event.getSourceID())){
			return;
		}
		
        if ((!IBDEventType.MULTIMODE_PARA_CHANGE_TO_TRUE.equals(event.getEventType()) && !
        		IEventType.TYPE_CANCELASSIGN_CHECK.equals(event.getEventType()))) {
            return;
        }
        
        BDCommonEvent eve = (BDCommonEvent) event;
        // 获取临时表对象
        IVisibleLostValidateContext context =(IVisibleLostValidateContext) eve.getObjs()[0];
        
        // 构造检查语句
        String[] checkSQL = getCheckSQL(context, event.getSourceID());
        
        // 执行语句
        if(checkSQL.length>0){
        	DBAUtil.execBatchSql(checkSQL);
        }
	}
	
    /**
     * @param context
     * @param SourceID
     * @return
     */
    private String[] getCheckSQL(IVisibleLostValidateContext context,String SourceID) {
        // 获取已分配临时表信息
        INewVisibleDocTempTbInfo visibleDocInfo =context.getNewVisibleDocInfo();
        
        // 获取结果集临时表信息
        ICheckResultTempTbInfo checkResultInfo = context.getCheckResultInfo();
        // 参数修改监听时，检查未分配的数据;取消分配时，检查分配后已使用的数据
        String exitsstr = context.isVisibleLostData() ? " exists " : " not exists ";
        
        List<String> sqlList =new ArrayList<String>();
        // 先查询未分配但已引用的数据，再插入到检查结果表中
        if(IBDMetaDataIDConst.CUSTORG.equals(SourceID)){ //客户
        	for(String tableName: customerTb_N1){
        		sqlList.add(dealCheckSql(visibleDocInfo, checkResultInfo, exitsstr,  getDocErrMsg(tableName), docPk_1[0],tableName));	
        	}
        	for(String tableName: customerTb_N2){
        		sqlList.add(dealCheckSql(visibleDocInfo, checkResultInfo, exitsstr,  getDocErrMsg(tableName), docPk_2[0],tableName));	
        	}
        }else if(IBDMetaDataIDConst.SUPORG.equals(SourceID)){ //供应商
        	for(String tableName: supplierTb_N1){
        		sqlList.add(dealCheckSql(visibleDocInfo, checkResultInfo, exitsstr,  getDocErrMsg(tableName), docPk_1[1],tableName));	
        	}
        	
        	for(String tableName: supplierTb_N2){
        		sqlList.add(dealCheckSql(visibleDocInfo, checkResultInfo, exitsstr,  getDocErrMsg(tableName), docPk_2[1],tableName));	
        	}
        }
        return sqlList.toArray(new String[0]);
    }

    private String dealCheckSql(INewVisibleDocTempTbInfo visibleDocInfo, ICheckResultTempTbInfo checkResultInfo,
  		  String exitsstr,String errormsg,String docPK,String tableName){
        // 先查询未分配但已引用的数据，再插入到检查结果表中,DR=0表示没有被删除的单据 
    	
    	//根据不同的表对应不同的费用承担单位的名称
    	String orgColName = (tableName.equals("er_bxzb")|| tableName.equals("er_jkzb") 
    			|| tableName.equals("er_costshare")) ? "fydwbm" : "assume_org";
    	
        String checkSQL =
                "insert into " + checkResultInfo.getTempTbName() + "("
                        + ICheckResultTempTbInfo.COL_DOC_ID + ", "
                        + ICheckResultTempTbInfo.COL_ORG_ID + ", "
                        + ICheckResultTempTbInfo.COL_GROUP_ID + ", "
                        + ICheckResultTempTbInfo.COL_MSG + ") "
                        + "select "+docPK+" ,"+orgColName+",pk_group,'" + errormsg
                        + "' from "+tableName+" d " + " where  d.pk_group ='"+InvocationInfoProxy.getInstance().getGroupId()+"'  and " 
                        +" d."+docPK+" <> '~' and d.dr='0' and "
                        + exitsstr
                        + " (select 1 from " + visibleDocInfo.getTempTbName()
                        + " where " + INewVisibleDocTempTbInfo.COL_ORG_ID
                        + "=d."+orgColName+" and "
                        + INewVisibleDocTempTbInfo.COL_DOC_ID
                        + "=d."+docPK+" and "
                        + INewVisibleDocTempTbInfo.COL_GROUP_ID
                        + "=d.pk_group)";
        
        return checkSQL;
    }
    
    private String getDocErrMsg(String tableName){
      String msg =nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000938")/*@res "费用管理"*/;
  	  if(tableName.equals("er_bxzb")){
  		  return msg+"/"+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000934")/*@res "报销单"*/;
  	  }else if(tableName.equals("er_jkzb")){
  		  return msg+"/"+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000935")/*@res "借款单"*/;
  	  }else if(tableName.equals("er_costshare") || tableName.equals("er_cshare_detail")){
  		  return msg+"/"+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000937")/*@res "结转单"*/; 
  	  }else if(tableName.equals("er_mtapp_detail")){
  		  return msg+"/"+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000936")/*@res "申请单"*/;  
  	  }
  	  return msg+"/";
    }
}
