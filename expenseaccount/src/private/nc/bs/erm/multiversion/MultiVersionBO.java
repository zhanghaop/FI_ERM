package nc.bs.erm.multiversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtil;
import nc.itf.erm.multiversion.IMultiversionVO;
import nc.jdbc.framework.SQLParameter;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;

public class MultiVersionBO {

	@SuppressWarnings("unchecked")
	public Map<String,String> queryOidByVid(IMultiversionVO vo, String[] vids) throws BusinessException {
		if(vids==null||vids.length==0){
			return null;
		}
		//可能使用临时表
		String condition = SqlUtil.buildInSql(vo.getVidField(),vids);
		List<String> result = (List<String>) new BaseDAO().retrieveByClause(vo.getVersionVOClass(), condition,new String[] { vo.getOidField() });
		if(result==null||result.size()==0){
			return null;
		}
		result.iterator();
		Map<String,String> map = new HashMap<String, String>();
		for (int i = 0; i < vids.length; i++) {
			map.put(vids[i], result.get(i));
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public List<SuperVO> queryVersionVO(IMultiversionVO vo)
			throws BusinessException {
		return (List<SuperVO>) new BaseDAO().retrieveAll(vo.getVersionVOClass());
	}

	@SuppressWarnings("unchecked")
	public Map<String,List<String>> queryVidByOid(IMultiversionVO vo, String[] oids) throws BusinessException {
		String condition = SqlUtil.buildInSql(vo.getVidField(),oids);
		List<Object[]> result =  (List<Object[]>) new BaseDAO().retrieveByClause(vo.getVersionVOClass(), condition , new String[] { vo.getOidField(),vo.getVidField() });
		if(result==null||result.size()==0){
			return null;
		}
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		for(Object[] values: result){
			String vid = (String)values[1];
			if(vid==null||vid.length()==0){
				continue;
			}
			String oid = (String)values[0];
			if(!map.containsKey(oid)){
				List<String> list = new ArrayList<String>();
				list.add(vid);
				map.put(oid, list);
			}else{
				List<String> list = map.get(oid);
				list.add(vid);
				map.put(oid, list);
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String,String> queryVidByOid(IMultiversionVO vo, String[] oids, UFDate date) throws BusinessException {
		String condition = SqlUtil.buildInSql(vo.getVidField(),oids);
		condition += " and "+vo.getVstartdateField()+"=?";
		SQLParameter params = new SQLParameter();
		params.addParam(date);
		List<Object[]> result =  (List<Object[]>) new BaseDAO().retrieveByClause(vo.getVersionVOClass(), condition , new String[] { vo.getOidField(),vo.getVidField(),vo.getVstartdateField() },params);
		Map<String,String> map = new HashMap<String, String>();
		for(Object[] values: result){
			String vid = (String)values[1];
			if(vid==null||vid.length()==0){
				continue;
			}
			String oid = (String)values[0];
			map.put(oid, vid);
			
		}
		return map;
	}

}
