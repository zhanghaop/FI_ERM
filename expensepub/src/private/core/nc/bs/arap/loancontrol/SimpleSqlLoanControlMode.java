package nc.bs.arap.loancontrol;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtils;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.LoanControlModeVO;
import nc.vo.ep.bx.LoanControlSchemaVO;
import nc.vo.ep.bx.LoanControlVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.StringUtils;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 *
 * nc.bs.arap.loancontrol.SimpleSqlLoanControlMode
 * 
 * �����Ʒ�ʽ��Ĭ��ʵ��
 * 
 * �ṩ���ƵĻ�����ܴ��룬����ʵ�־����
 * ȡ���߼�       getSql ()
 * ����ֵУ���߼�  compare ()
 */ 
public abstract class SimpleSqlLoanControlMode implements LoanControlMode {
	
	public String control(LoanControlVO defvo,LoanControlModeVO modevo, IFYControl[] vos) throws BusinessException {
		
		IFYControl vo=vos[0];
		
		BaseDAO baseDAO=new BaseDAO();
		
		Object value=null;
		
		try {
			value = baseDAO.executeQuery(getSql(defvo,vos), new ColumnProcessor());
		} catch (DAOException e) {
			ExceptionHandler.handleException(this.getClass(), e);
		}
		
		Object itemValue = vo.getItemValue(getControlJeField(defvo,true));

		if(!compare(value, modevo.getValue(),itemValue)){
		
			Object value2=getValue(value,itemValue);
			
			return getMessage(defvo,modevo,vo,value2,itemValue);
			
		}
		
		return null;
	}
	
	//��Ҫ�Ļ�����@Override
	protected Object getValue(Object value, Object itemValue) {
		if(value==null){
			value=new String("0");
		}
		return value;
	}

	protected String getControlJeField(LoanControlVO defvo, boolean isJeField){
		String field = null;
		if(defvo.getBbcontrol()==1){
			if(defvo.getPk_org()==null || defvo.getPk_org().trim().length()==0){
				//�������õķ������ռ��ű�λ�ҿ���
				if(isJeField)
					field = JKBXHeaderVO.GROUPBBJE;
				else
					field = JKBXHeaderVO.GROUPBBYE;	
			}else{
				if(isJeField)
					field = JKBXHeaderVO.BBJE;
				else
					field = JKBXHeaderVO.BBYE;	
			}
		}else{
			if(isJeField)
				field = JKBXHeaderVO.YBJE;
			else
				field = JKBXHeaderVO.YBYE;
		}
		return field;
	}
	
	protected String getDjlxbmAndJsfsInStr(LoanControlVO defvo, IFYControl[] vos) throws BusinessException {
		String djlxbmStr="";

		List<LoanControlSchemaVO> schemavos = defvo.getSchemavos();
		Set<String> djlxbms=new HashSet<String>();
		Set<String> jsfs=new HashSet<String>();
		for(LoanControlSchemaVO schema:schemavos){
			if(!StringUtils.isNullWithTrim(schema.getBalatype())){
				jsfs.add(schema.getBalatype());
			}
			if(!StringUtils.isNullWithTrim(schema.getDjlxbm())){
				djlxbms.add(schema.getDjlxbm());
			}
		}
		
		try {
			if(djlxbms.size()!=0){
				djlxbmStr+=SqlUtils.getInStr("zb.djlxbm",djlxbms.toArray(new String[]{}));
			}
			if(jsfs.size()!=0){
				if(djlxbms.size()!=0){
					djlxbmStr+=" and ";
				}
				djlxbmStr+= SqlUtils.getInStr("zb.jsfs",jsfs.toArray(new String[]{}));
			}
		} catch (SQLException e) {
			ExceptionHandler.handleException(this.getClass(), e);
		}
		
		return djlxbmStr;
	}
	
	private String getMessage(LoanControlVO defvo,LoanControlModeVO modevo, IFYControl vo, Object value,Object value2) {
		
		String viewMsg = modevo.getDefvo().getViewmsg();
		StringBuffer sb=new StringBuffer("");
		String[] values = viewMsg.split(",");
		
		for (String txt:values){
			String text="";
			
			if(txt.startsWith("@")){
				
				if("@paraname@".equals(txt)){
					text=defvo.getParaname();
				}else if("@value@".equals(txt)){
					text=modevo.getValue().toString();
				}else if("@spliter@".equals(txt)){
					text=",\n";
				}else if("@attribute@".equals(txt)){
					text=defvo.getAttributename();
				}else if("@attname@".equals(txt)){
					text="";
				}else if("@attvalue@".equals(txt)){
					text=value.toString();
				}else if("@attvalue2@".equals(txt)){
					text=value2.toString();
				}
				
			}else if (txt.indexOf("@") != -1) {
				String[] strings = txt.split("@");
				text = NCLangRes4VoTransl.getNCLangRes().getStrByID(strings[0], strings[1]);
			}else{
				text=txt;
			}
			
			sb.append(text);
		}
		
		return sb.toString();
	}

	/**
	 * @param defvo
	 * @param vo
	 * @return
	 * @throws BusinessException
	 * 
	 * �����ѯ���
	 */
	public abstract String getSql(LoanControlVO defvo,IFYControl[] vo) throws BusinessException;
	
	/**
	 * @param dataValue
	 * @param controlValue
	 * @param itemValue 
	 * @return
	 * 
	 * �Ƿ�ͨ��������
	 */
	public abstract boolean compare(Object dataValue,Object controlValue, Object itemValue);

	
	protected String getTableName(IFYControl vo) throws BusinessException {
		
		SuperVO superVO=null;
		
		try {
			superVO = (SuperVO) vo;
		} catch (Exception e) {
			ExceptionHandler.handleException(this.getClass(), e);
		}
		return superVO.getTableName();
	}
}
