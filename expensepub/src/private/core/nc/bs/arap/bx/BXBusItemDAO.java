package nc.bs.arap.bx;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtils;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 *
 * nc.bs.arap.bx.BXBusItemDAO
 * 
 * 报销单据表体数据读取类
 * 
 */
public class BXBusItemDAO extends BXSuperDAO{

	private final String PK_FIELD=new BXBusItemVO().getPKFieldName();
	private final String PARENT_PK_FIELD=new BXBusItemVO().getParentPKFieldName();

	public BXBusItemDAO() throws NamingException {
		super();
	}

	@SuppressWarnings("unchecked")
	public BXBusItemVO[] queryByBXVOPks(String[] pks) throws DAOException, SQLException {


		String sql = SqlUtils.getInStr(PARENT_PK_FIELD,pks);
		
		sql += " and dr=0 ";
		
		BXBusItemVO bxBusItemVO[] = null ;
		ArrayList<BXBusItemVO> v = new ArrayList<BXBusItemVO>();

//		v=(ArrayList<BXBusItemVO>)baseDao.retrieveByClause(BXBusItemVO.class,sql);
		final String[] fields=new String[]{"AMOUNT","BBJE","BBYE","CJKBBJE","CJKYBJE","DEFITEM1","DEFITEM10","DEFITEM11","DEFITEM12","DEFITEM13","DEFITEM14","DEFITEM15","DEFITEM16","DEFITEM17","DEFITEM18","DEFITEM19","DEFITEM2","DEFITEM20","DEFITEM21","DEFITEM22","DEFITEM23","DEFITEM24","DEFITEM25","DEFITEM26","DEFITEM27","DEFITEM28","DEFITEM29","DEFITEM3","DEFITEM30","DEFITEM31","DEFITEM32","DEFITEM33","DEFITEM34","DEFITEM35","DEFITEM36","DEFITEM37","DEFITEM38","DEFITEM39","DEFITEM4","DEFITEM40","DEFITEM41","DEFITEM42","DEFITEM43","DEFITEM44","DEFITEM45","DEFITEM46","DEFITEM47","DEFITEM48","DEFITEM49","DEFITEM5","DEFITEM50","DEFITEM6","DEFITEM7","DEFITEM8","DEFITEM9","DR","GLOBALBBJE","GLOBALBBYE","GLOBALCJKBBJE","GLOBALHKBBJE","GLOBALZFBBJE","GROUPBBJE","GROUPBBYE","GROUPCJKBBJE","GROUPHKBBJE","GROUPZFBBJE","HKBBJE","HKYBJE","PK_BUSITEM","PK_JKBX","PK_REIMTYPE","ROWNO","SZXMID","TABLECODE","TS","YBJE","YBYE","YJYE","ZFBBJE","ZFYBJE"};
		v=(ArrayList<BXBusItemVO>)new BaseDAO().executeQuery("select AMOUNT,BBJE,BBYE,CJKBBJE,CJKYBJE,DEFITEM1,DEFITEM10,DEFITEM11,DEFITEM12,DEFITEM13,DEFITEM14,DEFITEM15,"+ 
		"DEFITEM16,DEFITEM17,DEFITEM18,DEFITEM19,DEFITEM2,DEFITEM20,DEFITEM21,DEFITEM22,DEFITEM23,DEFITEM24,DEFITEM25,DEFITEM26, "+
		"DEFITEM27,DEFITEM28,DEFITEM29,DEFITEM3,DEFITEM30,DEFITEM31,DEFITEM32,DEFITEM33,DEFITEM34,DEFITEM35,DEFITEM36,DEFITEM37, "+
		"DEFITEM38,DEFITEM39,DEFITEM4,DEFITEM40,DEFITEM41,DEFITEM42,DEFITEM43,DEFITEM44,DEFITEM45,DEFITEM46,DEFITEM47,DEFITEM48, "+
		"DEFITEM49,DEFITEM5,DEFITEM50,DEFITEM6,DEFITEM7,DEFITEM8,DEFITEM9,DR,GLOBALBBJE,GLOBALBBYE,GLOBALCJKBBJE,GLOBALHKBBJE, "+
		"GLOBALZFBBJE,GROUPBBJE,GROUPBBYE,GROUPCJKBBJE,GROUPHKBBJE,GROUPZFBBJE,"+ 
		"HKBBJE,HKYBJE,PK_BUSITEM,PK_JKBX,PK_REIMTYPE,ROWNO,SZXMID,TABLECODE, "+
		"TS,YBJE,YBYE,YJYE,ZFBBJE,ZFYBJE from er_busitem where "+sql,new ResultSetProcessor() {
		public Object handleResultSet(ResultSet rs) throws SQLException {
			ArrayList<BXBusItemVO> result=new ArrayList<BXBusItemVO>();
			while(rs.next()){
				BXBusItemVO v=new BXBusItemVO();
				for(String field:fields){
					Object object = rs.getObject(field);
					if(object ==null ){
						continue;
					}
					if(field.equalsIgnoreCase("ts")){
						v.setAttributeValue(field, new UFDateTime((String)object));
					}else if(object instanceof BigDecimal){
						v.setAttributeValue(field, new UFDouble((BigDecimal)object));
					}else if(object instanceof String){
						v.setAttributeValue(field, (String)object);
					}else if(object instanceof Integer){
						v.setAttributeValue(field, (Integer)object);
					}else{
						v.setAttributeValue(field, object);
					}
				}
				result.add(v);
			}
			return result;
		}});

		bxBusItemVO = new BXBusItemVO[v.size()];
		if (v.size() > 0) {
			v.toArray(bxBusItemVO);
		}
		
		return bxBusItemVO;

	}

	@SuppressWarnings("unchecked")
	public BXBusItemVO[] queryByPks(String[] pks) throws DAOException, SQLException {
		
		String sql = SqlUtils.getInStr(PK_FIELD,pks);
		
		BXBusItemVO bxBusItemVO[] = null ;
		ArrayList<BXBusItemVO> v = new ArrayList<BXBusItemVO>();

		v=(ArrayList<BXBusItemVO>)baseDao.retrieveByClause(BXBusItemVO.class,sql);

		bxBusItemVO = new BXBusItemVO[v.size()];
		if (v.size() > 0) {
			v.toArray(bxBusItemVO);
		}
		
		return bxBusItemVO;
	}

}
