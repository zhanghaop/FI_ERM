package nc.bs.er.outer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.uif.pub.exception.UifRuntimeException;
import nc.vo.arap.bx.util.ErmDataFinder;
import nc.vo.trade.billsource.LightBillVO;

public class ErmBxDataFinder extends ErmDataFinder{

	/**
	 * AbstractBillFinder ������ע�⡣
	 */
	public ErmBxDataFinder() {
		super();
	}
	
	private String createSourceOtherSQL() {
		return "select distinct zb.djlxbm,zb.vouchid  from arap_item zb,er_bxzb fb where fb.pk_item is not null and fb.pk_jkbx=? and zb.vouchid=fb.pk_item and fb.dr=0 ";
	}
 	private String createForwardOtherSQL() {
		return "select distinct fb.pk_jkbx,fb.pk_corp,fb.djbh,fb.djlxbm from arap_item zb,er_bxzb fb where fb.pk_item is not null and zb.vouchid=? and zb.vouchid=fb.pk_item and fb.dr=0 ";
	}
 	/*
	 * ����:���ݵ�ǰ�ĵ���ID,��������,���ָ�����͵ĺ�������.
	 * ����:LightBillVO[],��������VO����,����Ҫ��дLightBillVO��ID,TYPE,CODE��������.
	 * ����TYPE���Ծ���forwardBillTYPE�Ĳ���ֵ ����: 1.String curBillType :��ǰ�������� 2.String
	 * curBillID:��ǰ����ID 3.String forwardBillType:�������ݵ�����
	 * 
	 */
	@SuppressWarnings( { "unchecked", "serial" })
	public nc.vo.trade.billsource.LightBillVO[] getForwardBills(
			String srcBillType, String srcBillID, final String curBillType) {
		PersistenceManager sessionManager = null;
		try {
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			SQLParameter para = new SQLParameter();
			para.addParam(srcBillID);
			ResultSetProcessor p = new ResultSetProcessor() {
				
				public Object handleResultSet(ResultSet rs) throws SQLException {
					Map<String,LightBillVO> al = new HashMap<String,LightBillVO>();
					while (rs.next()) {
						String id = rs.getString(1);
						String corp = rs.getString(2);
						String code = rs.getString(3);
						String djlxbm = rs.getString(4);
						if ( id != null					
								&& id.trim().length() > 0) {
							LightBillVO svo = new LightBillVO();
							svo.setID(id);
							svo.setCorp(corp);
							svo.setCode(code);
							svo.setType(djlxbm);
							al.put(id,  svo);
						}
					}
					return al;
				}
			};
			List<LightBillVO> result =new ArrayList<LightBillVO>();
				
			String othersql=this.createForwardOtherSQL();
			Map<String,LightBillVO> othersqlresults=(Map<String,LightBillVO>)session.executeQuery(othersql, para, p);
			
			for(Map.Entry<String, LightBillVO> entry : othersqlresults.entrySet()){
				if(!result.contains(entry.getValue())){
					result.add(entry.getValue());
				}
			}
			
			if (result.size() == 0)
				return null;
			// �������ε��ݺ�
			for (LightBillVO vo : result) {
				if(null==vo.getCode()||null==vo.getCorp()){
					vo.setCode(getBillCodeAndCorp(vo.getType(), vo.getID()).get(0));
					vo.setCorp(getBillCodeAndCorp(vo.getType(), vo.getID()).get(1));
				}
			}
			return (nc.vo.trade.billsource.LightBillVO[]) result
					.toArray(new nc.vo.trade.billsource.LightBillVO[result
							.size()]);
		} catch (DbException e) {
			throw new UifRuntimeException(e.getMessage());
		} finally {
			sessionManager.release();
		}

	}
	/*
	 * ����:���ݵ�ǰ�ĵ���ID,��������,������е���Դ����
	 * ����:LightBillVO[],��Դ����VO����,����Ҫ��дLightBillVO��ID,TYPE,CODE��������. ����: 1.String
	 * curBillType :��ǰ�������� 2.String curBillID:��ǰ����ID
	 * 
	 */
	@SuppressWarnings( { "serial", "unchecked" })
	public nc.vo.trade.billsource.LightBillVO[] getSourceBills(
			String curBillType, String curBillID) {
//		String sql = createSQL(curBillType);
//		if (sql == null)
//			return null;
		PersistenceManager sessionManager = null;
		try {
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			SQLParameter para = new SQLParameter();
			para.addParam(curBillID);

			ResultSetProcessor p = new ResultSetProcessor() {
				public Object handleResultSet(ResultSet rs) throws SQLException {
					Map<String,LightBillVO> al = new HashMap<String,LightBillVO>();
					while (rs.next()) {
						String type = rs.getString(1);
						String id = rs.getString(2);
						if (type != null && id != null
								&& type.trim().length() > 0
								&& id.trim().length() > 0&&!al.containsKey(id)) {
							LightBillVO svo = new LightBillVO();
							svo.setType(type);
							svo.setID(id);
							al.put(id, svo);
						}
					}
					return al;
				}
			};
			List<LightBillVO> result =new ArrayList<LightBillVO>();
			String othersql=this.createSourceOtherSQL();
			Map<String,LightBillVO> othersqlresults=(Map<String,LightBillVO>)session.executeQuery(othersql, para, p);
			
			for(Map.Entry<String, LightBillVO> entry : othersqlresults.entrySet()){
				if(!result.contains(entry.getValue())){
					result.add(entry.getValue());
				}
			}
			
			if (result.size() == 0)
				return null;
			// �������ε��ݺ�
			for (LightBillVO vo : result) {
				if(null==vo.getCode()||null==vo.getCorp()){
					List<String> rs = getBillCodeAndCorp(vo.getType(), vo.getID());
					if (rs != null) {
						vo.setCode(rs.get(0));
						vo.setCorp(rs.get(1));
					}
					
				}
			}
			return (nc.vo.trade.billsource.LightBillVO[]) result
					.toArray(new nc.vo.trade.billsource.LightBillVO[result
							.size()]);
		} catch (DbException e) {
			throw new UifRuntimeException(e.getMessage(),e);
		} finally {
			sessionManager.release();
		}

	}
}
