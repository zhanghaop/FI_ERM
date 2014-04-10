package nc.bs.er.control;

import java.util.Vector;

import nc.bs.logging.Logger;
import nc.vo.erm.control.FuncResultVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.tb.obj.NtbParamVO;


public class YsDataBO {
	//FIXME 0426　暂时注销
	public FuncResultVO[] queryFuncs(NtbParamVO[] ntbvos) throws BusinessException {
		if (ntbvos == null || ntbvos.length == 0) {
			return null;
		}
		FuncResultVO[] resultvo = null;
		try {
			for(int i=0; i<ntbvos.length; i++){
				ntbvos[i].setIndex(i);
			}
			Vector<NtbParamVO> vect_notnull = new Vector<NtbParamVO>();// 币种不为null的
			Vector<NtbParamVO> vect_null = new Vector<NtbParamVO>();// 币种为null
			for (int i = 0; i < ntbvos.length; i++) {
				if (ntbvos[i] == null) {
					continue;
				}
				String pk_curr = ntbvos[i].getPk_currency();
				if (pk_curr == null || pk_curr.trim().length() == 0) {
					vect_null.add(ntbvos[i]);
				} else {
					vect_notnull.add(ntbvos[i]);
				}
			}

			Vector<FuncResultVO> vect_ret_notnull = query3(vect_notnull);
			Vector<FuncResultVO> vect_ret_null = query3(vect_null);
			
			Vector<FuncResultVO> vect_ret_all = new Vector<FuncResultVO>();
			if (vect_ret_notnull != null && vect_ret_notnull.size() > 0) {
				vect_ret_all.addAll(vect_ret_notnull);
			}
			if (vect_ret_null != null && vect_ret_null.size() > 0) {
				vect_ret_all.addAll(vect_ret_null);
			}

			if (vect_ret_all.size() > 0) {
				resultvo = new FuncResultVO[vect_ret_all.size()];
				vect_ret_all.copyInto(resultvo);
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return resultvo;
	}
	
	private Vector<FuncResultVO> query3(Vector vect) throws BusinessException {
		if(vect == null || vect.size() == 0){
			return null;
		}
		NtbParamVO[] ntbvos = new NtbParamVO[vect.size()];
		vect.copyInto(ntbvos);
		
		java.sql.Connection con = null;
		Vector<FuncResultVO> vectResult = new Vector<FuncResultVO>();
		try {
			QueryFuncDAO dmo = new QueryFuncDAO();
			con = dmo.getConn();
//			for (int i = 0; i < qvos.length; i++) {
//				sqlTools.setSqlVO(qvos[i]);
//				qvos[i].setSql(sqlTools.getSql());
//				/*存在问题，如果两条记录的key相同，是否应该相加？而不是取交集*/
//				HashMap<String,ArrayList<UFDouble>> hashResult = queryFunc(qvos[i], con);
//				preFenfa(hashResult,qvos[i]);
//				Vector<FuncResultVO> vect = fenFa(hashResult, qvos[i]);
//				vectResult.addAll(vect);
//			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			throw new BusinessException("", e);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return vectResult;
	}
	
}
