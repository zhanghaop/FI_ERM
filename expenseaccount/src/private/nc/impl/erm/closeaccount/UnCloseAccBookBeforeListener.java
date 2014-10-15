package nc.impl.erm.closeaccount;

import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.bs.dao.BaseDAO;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;

/**
 * 
 * <p>
 * ���ڱ�����������ǰ��ҵ��У��
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see 

 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-10-18 ����10:04:13
 */
public class UnCloseAccBookBeforeListener implements IBusinessListener {
	
	
	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		if (event instanceof BDCommonEvent) {
			BDCommonEvent commonEvent = (BDCommonEvent) event;
			Object[] objs = commonEvent.getObjs();
			CloseAccBookVO closeAccBookVO = (CloseAccBookVO) objs[0];
			String module_id = closeAccBookVO.getModuleid();
			if (!BXConstans.ERM_MODULEID.equals(module_id)) {// ֻУ�鱨��ģ��
				return;

//				closeAccBookVO.setPk_closeaccbook("1001Z31000000000099I");
//				closeAccBookVO.setPk_accperiodmonth("1001AA1000000000YC4O");
			}
			String pk_liabook = closeAccBookVO.getCloseorgpks();
			String pk_accperiodmonth = closeAccBookVO.getPk_accperiodmonth();

			boolean bPreClose = isAutoPreClose(pk_liabook, pk_accperiodmonth, module_id);
			
			// ����ǰ������ΪϵͳĬ���û�ʱ��Ϊ���Զ���ǰ����
			if (bPreClose /*&& "NC_USER0000000000000".equals(closeAccBookVO.getPrecloseuser())*/) {
				//�Զ�����ǰ����
				closeAccBookVO.setIspreclose(UFBoolean.FALSE);
				closeAccBookVO.setUnprecloseuser(closeAccBookVO.getPrecloseuser());
				closeAccBookVO.setUnpreclosetime(new UFDateTime());
				
				new BaseDAO().updateVO(closeAccBookVO);
			}
		}
	}

	/*
	 * ��Ϊ�Զ���ǰ�������Զ�ȡ��
	 */
	@SuppressWarnings("unchecked")
	private boolean isAutoPreClose(String pk_liabook, String pk_accperiodmonth,
			String module_id) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String condition = CloseAccBookVO.CLOSEORGPKS + " = '" + pk_liabook
				+ "' and " + CloseAccBookVO.PK_ACCPERIODMONTH + " = '"
				+ pk_accperiodmonth + "' and " + CloseAccBookVO.MODULEID
				+ " = '" + module_id + "' and " + CloseAccBookVO.ISPRECLOSE
				//+ " = 'Y'";
				+ " = 'Y' and " + CloseAccBookVO.PRECLOSEUSER + " = 'NC_USER0000000000000'";
		List<CloseAccBookVO> closeAccBookVOList = (List<CloseAccBookVO>) dao
				.retrieveByClause(CloseAccBookVO.class, condition);
		if (closeAccBookVOList == null || closeAccBookVOList.size() != 1) {
			return false;
		}else{
			return true;
		}
		
	}

}
