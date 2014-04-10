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
 * �ͻ��͹�Ӧ�̵�����ȡ������У��Ͳ�����Ϊǿ����У�����
 * @author wangled
 *
 */
public class ErmBdDocVisibleListener implements IBusinessListener{
	// ���õ�������/��������
	public String[] errorMsg = {"�ͻ�","��Ӧ��"};
	
	public String[] customerTb_N1 = {"er_bxzb","er_jkzb","er_costshare","er_cshare_detail"};//��ſͻ��������ڵ����ı���1
	
	public String[] customerTb_N2 = {"er_mtapp_detail"}; //��ſͻ��������ڵ����ı���2
	
	public String[] supplierTb_N1 = {"er_bxzb","er_jkzb","er_costshare","er_cshare_detail"}; //��Ź�Ӧ�̵������ڵ����ı���1

	public String[] supplierTb_N2 = {"er_mtapp_detail"}; //��Ź�Ӧ�̵������ڵ����ı���2
	
	/**ע�����õ��������� docPk_1��Ӧ**Tb_N1 ; docPk_2��Ӧ**Tb_N2 */
	public String[] docPk_1 ={"customer","hbbm"};
	public String[] docPk_2 ={"pk_customer","pk_supplier"};
	
	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		//���ɼ��Է�Χ��ȡ������ǰУ��Ͳ�����Ϊǿ����У��
		if(!IBDMetaDataIDConst.CUSTORG.equals(event.getSourceID())&&
				!IBDMetaDataIDConst.SUPORG.equals(event.getSourceID())){
			return;
		}
		
        if ((!IBDEventType.MULTIMODE_PARA_CHANGE_TO_TRUE.equals(event.getEventType()) && !
        		IEventType.TYPE_CANCELASSIGN_CHECK.equals(event.getEventType()))) {
            return;
        }
        
        BDCommonEvent eve = (BDCommonEvent) event;
        // ��ȡ��ʱ�����
        IVisibleLostValidateContext context =(IVisibleLostValidateContext) eve.getObjs()[0];
        
        // ���������
        String[] checkSQL = getCheckSQL(context, event.getSourceID());
        
        // ִ�����
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
        // ��ȡ�ѷ�����ʱ����Ϣ
        INewVisibleDocTempTbInfo visibleDocInfo =context.getNewVisibleDocInfo();
        
        // ��ȡ�������ʱ����Ϣ
        ICheckResultTempTbInfo checkResultInfo = context.getCheckResultInfo();
        // �����޸ļ���ʱ�����δ���������;ȡ������ʱ�����������ʹ�õ�����
        String exitsstr = context.isVisibleLostData() ? " exists " : " not exists ";
        
        List<String> sqlList =new ArrayList<String>();
        // �Ȳ�ѯδ���䵫�����õ����ݣ��ٲ��뵽���������
        if(IBDMetaDataIDConst.CUSTORG.equals(SourceID)){ //�ͻ�
        	for(String tableName: customerTb_N1){
        		sqlList.add(dealCheckSql(visibleDocInfo, checkResultInfo, exitsstr,  getDocErrMsg(tableName), docPk_1[0],tableName));	
        	}
        	for(String tableName: customerTb_N2){
        		sqlList.add(dealCheckSql(visibleDocInfo, checkResultInfo, exitsstr,  getDocErrMsg(tableName), docPk_2[0],tableName));	
        	}
        }else if(IBDMetaDataIDConst.SUPORG.equals(SourceID)){ //��Ӧ��
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
        // �Ȳ�ѯδ���䵫�����õ����ݣ��ٲ��뵽���������,DR=0��ʾû�б�ɾ���ĵ��� 
    	
    	//���ݲ�ͬ�ı��Ӧ��ͬ�ķ��óе���λ������
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
      String msg =nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000938")/*@res "���ù���"*/;
  	  if(tableName.equals("er_bxzb")){
  		  return msg+"/"+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000934")/*@res "������"*/;
  	  }else if(tableName.equals("er_jkzb")){
  		  return msg+"/"+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000935")/*@res "��"*/;
  	  }else if(tableName.equals("er_costshare") || tableName.equals("er_cshare_detail")){
  		  return msg+"/"+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000937")/*@res "��ת��"*/; 
  	  }else if(tableName.equals("er_mtapp_detail")){
  		  return msg+"/"+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000936")/*@res "���뵥"*/;  
  	  }
  	  return msg+"/";
    }
}
