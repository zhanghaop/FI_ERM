package nc.bs.er.common.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtils;
import nc.bs.logging.Log;
import nc.vo.arap.bx.util.CommonUtils;

import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.common.CommonSuperVO;
import nc.vo.erm.common.VOCheck;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.FieldObject;
import nc.vo.pub.SuperVO;

public class CommonDAO {

	private BaseDAO baseDAO = new BaseDAO();

	public String save(SuperVO vo, boolean cascade) throws DAOException {

		execVO(vo, 'C', cascade);

		return vo.getPrimaryKey();
	}

	public void update(SuperVO vo, boolean cascade) throws DAOException {

		execVO(vo, 'U', cascade);

	}

	public void delete(SuperVO vo, boolean cascade) throws DAOException {

		execVO(vo, 'D', cascade);

	}

	public String save(AggregatedValueObject vo, boolean cascade) throws DAOException {

		SuperVO parentVO = (SuperVO) vo.getParentVO();
		execVO(parentVO, 'C', cascade);

		CircularlyAccessibleValueObject[] childrenVO = vo.getChildrenVO();
		execVOArray((SuperVO[]) childrenVO, 'C', cascade);

		return parentVO.getPrimaryKey();
	}

	public void update(AggregatedValueObject vo, boolean cascade) throws DAOException {

		SuperVO parentVO = (SuperVO) vo.getParentVO();
		execVO(parentVO, 'U', cascade);

		CircularlyAccessibleValueObject[] childrenVO = vo.getChildrenVO();
		execVOArray((SuperVO[]) childrenVO, 'U', cascade);

	}

	public void delete(AggregatedValueObject vo, boolean cascade) throws DAOException {

		SuperVO parentVO = (SuperVO) vo.getParentVO();
		execVO(parentVO, 'D', cascade);

		CircularlyAccessibleValueObject[] childrenVO = vo.getChildrenVO();
		execVOArray((SuperVO[]) childrenVO, 'D', cascade);

	}

	public SuperVO getVOByPk(Class clazz, String pk, boolean cascade) throws DAOException {

		SuperVO vo;
		try {
			vo = (SuperVO) clazz.newInstance();
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			return null;
		}

		Collection<SuperVO> vos = getVOs(clazz, vo.getPKFieldName() + "='" + pk + "'", cascade);

		if (vos == null || vos.size() == 0) {
			return null;
		} else {
			return vos.iterator().next();
		}
	}

	public Collection<SuperVO> getVOByPks(Class clazz, String[] pks, boolean cascade) throws DAOException {

		SuperVO vo;

		try {
			vo = (SuperVO) clazz.newInstance();
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			return null;
		}

		String sql;
		try {
			sql = SqlUtils.getInStr(vo.getPKFieldName(),pks);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}

		return getVOs(clazz, sql, cascade);
	}

	@SuppressWarnings("unchecked")
	public Collection<SuperVO> getVOs(Class clazz, String whereStr, boolean cascade) throws DAOException {

		Collection<SuperVO> collection = baseDAO.retrieveByClause(clazz, whereStr);

		Map<String, SuperVO> retrieveParent = new HashMap<String, SuperVO>();
		Map<String, SuperVO> retrieveChild = new HashMap<String, SuperVO>();

		if (collection != null && collection.size() != 0 && cascade) {
			try {
				SuperVO vo = (SuperVO) clazz.newInstance();
				String[] attributeNames = vo.getAttributeNames();

				for (int i = 0; i < attributeNames.length; i++) {
					String typeName = vo.getClass().getField(attributeNames[i]).getType().getName();

					if (typeName.equals("java.util.List")) {
						if (vo instanceof CommonSuperVO) {
							CommonSuperVO cmSuperVO = (CommonSuperVO) vo;
							FieldObject[] fields = cmSuperVO.getFields();
							for (int j = 0; j < fields.length; j++) {
								if (fields[j].getLabel().equals(attributeNames[i])) {
									Object inst = Class.forName(fields[j].getName()).newInstance();
									if (inst instanceof SuperVO) {
										SuperVO tvo = (SuperVO) inst;
										retrieveChild.put(attributeNames[i], tvo);
									}
									break;
								}
							}
						}
					} else {
						Object inst;
						try {
							inst = Class.forName(typeName).newInstance();
							if (inst instanceof SuperVO) {
								SuperVO tvo = (SuperVO) inst;
								retrieveParent.put(attributeNames[i], tvo);
							}
						}catch (InstantiationException e) {
						} catch (IllegalAccessException e) {
						}
					}
				}

				for (Iterator<String> iter = retrieveParent.keySet().iterator(); iter.hasNext();) {

					String key = iter.next();

					SuperVO superVO = retrieveParent.get(key);

					String fieldName = superVO.getPKFieldName();

					String[] parentPks = VOUtils.changeCollectionToArray(collection, fieldName);

					Collection<SuperVO> values = getVOs(superVO.getClass(), SqlUtils.getInStr(fieldName,parentPks), true);

					Map<String, SuperVO> maps = VOUtils.changeCollectionToMap(values, fieldName);

					for (Iterator<SuperVO> newiter = collection.iterator(); newiter.hasNext();) {
						SuperVO newvo = newiter.next();
						newvo.setAttributeValue(key, maps.get(newvo.getAttributeValue(fieldName)));
					}
				}

				String[] pks = null;
				String pkField = null;
				if (retrieveChild.size() != 0) {
					pkField = collection.iterator().next().getPKFieldName();
					pks = VOUtils.changeCollectionToArray(collection, pkField);
				}

				for (Iterator<String> iter = retrieveChild.keySet().iterator(); iter.hasNext();) {

					String key = iter.next();

					SuperVO superVO = retrieveChild.get(key);

					Collection<SuperVO> values = getVOs(superVO.getClass(), SqlUtils.getInStr(pkField,pks), true);

					Map<String, List<SuperVO>> maps = VOUtils.changeCollectionToMapList(values, pkField);

					for (Iterator<SuperVO> newiter = collection.iterator(); newiter.hasNext();) {
						SuperVO newvo = newiter.next();
						newvo.setAttributeValue(key, maps.get(newvo.getPrimaryKey()));
					}
				}

			} catch (SQLException e) {
				throw new DAOException(e.getMessage(), e);
			} catch (SecurityException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
			} catch (NoSuchFieldException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
			} catch (InstantiationException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
			}
		}

		return collection;

	}

	private void execVOArray(SuperVO[] superVOs, char action, boolean cascade) throws DAOException {

		execVOArryPri(superVOs, action, cascade, false);

	}

	@SuppressWarnings("unchecked")
	private void execVOArryPri(SuperVO[] superVOs, char action, boolean cascade, boolean isCascade) throws DAOException {

		if(superVOs==null || superVOs.length==0)
			return ;

		SuperVO[] baseVOs =null ;
		switch (action) {
		case 'C':
			insertVOArray(superVOs);
			takeTsBack(superVOs);
			break;
		case 'U':
			if (!isCascade) {
				baseVOs = checkTsRetrBaseVO(superVOs, cascade);
			}
			updateVOArray(superVOs);
			takeTsBack(superVOs);
			break;
		case 'D':
			if (!isCascade) {
				superVOs = checkTsRetrBaseVO(superVOs, cascade);
			}
			deleteVOArray(superVOs);
			break;
		default:
			break;
		}

		if (cascade) {

			String[] attributeNames = superVOs[0].getAttributeNames();

			for (int i = 0; i < attributeNames.length; i++) {

				String typeName = "";
				try {
					typeName = superVOs[0].getClass().getField(attributeNames[i]).getType().getName();
				} catch (Exception e) {
					Log.getInstance(this.getClass()).error(e.getMessage(), e);
				}

				if (typeName.equals("java.util.List")) {

					List<SuperVO> voArrays = new ArrayList<SuperVO>();

					if (action == 'U') {

						List<SuperVO> voArrays2 = new ArrayList<SuperVO>();
						List<SuperVO> voArrays3 = new ArrayList<SuperVO>();

						for (int j = 0; j < superVOs.length; j++) {

							Object value = superVOs[j].getAttributeValue(attributeNames[i]);

							List<SuperVO> newVOs = null;

							if (value != null) {

								newVOs = (List<SuperVO>) value;

								for (Iterator<SuperVO> iter = newVOs.iterator(); iter.hasNext();) {
									SuperVO vo = iter.next();
									vo.setAttributeValue(vo.getParentPKFieldName(), superVOs[j].getPrimaryKey());
								}
							}

							List<SuperVO> baseVos = null;
							for (int k = 0; k < baseVOs.length; k++) {
								if(baseVOs[k].getPrimaryKey().equals(superVOs[j].getPrimaryKey())){
									baseVos = (List<SuperVO>) baseVOs[k].getAttributeValue(attributeNames[i]);
									break;
								}
							}

							List<List<SuperVO>> targetVos = generUpdateList(newVOs,baseVos);

							voArrays.addAll(targetVos.get(0));
							voArrays2.addAll(targetVos.get(1));
							voArrays3.addAll(targetVos.get(2));
						}



						execVOArryPri(voArrays.toArray(new SuperVO[] {}), 'C', cascade, true);
						execVOArryPri(voArrays2.toArray(new SuperVO[] {}), 'D', cascade, true);
						execVOArryPri(voArrays3.toArray(new SuperVO[] {}), 'U', cascade, true);

					} else {
						for (int j = 0; j < superVOs.length; j++) {

							Object value = superVOs[j].getAttributeValue(attributeNames[i]);

							if (value != null) {

								List<SuperVO> newVO = (List<SuperVO>) value;

								for (Iterator<SuperVO> iter = newVO.iterator(); iter.hasNext();) {
									SuperVO vo = iter.next();
									vo.setAttributeValue(vo.getParentPKFieldName(), superVOs[j].getPrimaryKey());
								}

								voArrays.addAll(newVO);

							}
						}
						execVOArryPri(voArrays.toArray(new SuperVO[] {}), action, cascade, true);
					}


				}
			}
		}
	}

	private void deleteVOArray(SuperVO[] superVOs) throws DAOException {
		baseDAO.deleteVOArray(superVOs);
	}

	private void updateVOArray(SuperVO[] superVOs) throws DAOException {

		for (int i = 0; i < superVOs.length; i++) {
			check(superVOs[i]);
		}

		baseDAO.updateVOArray(superVOs);
	}

	private void insertVOArray(SuperVO[] superVOs) throws DAOException {

		for (int i = 0; i < superVOs.length; i++) {
			check(superVOs[i]);
		}

		baseDAO.insertVOArray(superVOs);
	}

	private void takeTsBack(SuperVO[] superVOs) throws DAOException {

		String[] pks = getPrimaryKeys(superVOs);

		Collection<SuperVO> basevos = getVOByPks(superVOs[0].getClass(), pks, false);

		Map<String, SuperVO> mapvos = VOUtils.changeCollectionToMap(basevos, superVOs[0].getPKFieldName());

		for (int i = 0; i < superVOs.length; i++) {
			superVOs[i].setAttributeValue("ts", mapvos.get(superVOs[i].getPrimaryKey()).getAttributeValue("ts"));
		}
	}

	@SuppressWarnings("unchecked")
	private void execVO(SuperVO vo, char action, boolean cascade) throws DAOException {

		if(vo==null)
			return ;

		SuperVO baseVO=null;

		switch (action) {
		case 'C':
			insertVO(vo);
			takeTsBack(new SuperVO[] { vo });
			break;
		case 'U':
			baseVO = checkTsRetrBaseVO(new SuperVO[] { vo }, cascade)[0];
			updateVO(vo);
			takeTsBack(new SuperVO[] { vo });
			break;
		case 'D':
			vo = checkTsRetrBaseVO(new SuperVO[] { vo }, cascade)[0];
			deleteVO(vo);
			break;
		default:
			break;
		}

		if (cascade) {
			String[] attributeNames = vo.getAttributeNames();

			for (int i = 0; i < attributeNames.length; i++) {
				Object value = vo.getAttributeValue(attributeNames[i]);

				if (value != null) {

					if (value instanceof SuperVO) {

					}
					if (value instanceof List) {
						List<SuperVO> newVOs = (List<SuperVO>) value;

						for (Iterator<SuperVO> iter = newVOs.iterator(); iter.hasNext();) {
							SuperVO von = iter.next();
							von.setAttributeValue(von.getParentPKFieldName(), vo.getPrimaryKey());
						}

						if (action == 'U') {
							List<SuperVO> baseVos = (List<SuperVO>) baseVO.getAttributeValue(attributeNames[i]);
							List<List<SuperVO>> targetVos = generUpdateList(newVOs,baseVos);

							execVOArryPri(targetVos.get(0).toArray(new SuperVO[] {}), 'C', cascade, true);
							execVOArryPri(targetVos.get(1).toArray(new SuperVO[] {}), 'D', cascade, true);
							execVOArryPri(targetVos.get(2).toArray(new SuperVO[] {}), 'U', cascade, true);

						} else {
							SuperVO[] voArray = newVOs.toArray(new SuperVO[] {});
							execVOArryPri(voArray, action, cascade, true);
						}
					}
				}
			}
		}
	}

	private void deleteVO(SuperVO vo) throws DAOException {
		baseDAO.deleteVO(vo);
	}

	private void updateVO(SuperVO vo) throws DAOException {
		check(vo);
		baseDAO.updateVO(vo);
	}

	private void insertVO(SuperVO vo) throws DAOException {
		check(vo);
		baseDAO.insertVO(vo);
	}

	private void check(SuperVO vo) {
		if (vo instanceof CommonSuperVO) {
			CommonSuperVO comVO = (CommonSuperVO) vo;
			String checkClass = comVO.getCheckClass();
			if(StringUtils.isNullWithTrim(checkClass))
				return ;
			try {
				((VOCheck)Class.forName(checkClass).newInstance()).check(vo);
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	/**
	 * 对比传入的volist和数据库中的volist
	 *
	 * @param newVOs
	 * @param baseVos
	 *
	 * @return
	 *
	 * List[需要增加的数据,需要删除的数据,需要修改的数据]
	 *
	 */
	private List<List<SuperVO>> generUpdateList(List<SuperVO> newVOs, List<SuperVO> baseVos) {

		List<List<SuperVO>> list=new ArrayList<List<SuperVO>>();

		list.add(new ArrayList<SuperVO>());
		list.add(new ArrayList<SuperVO>());
		list.add(new ArrayList<SuperVO>());

		if(VOUtils.isEmpty(newVOs) && VOUtils.isEmpty(baseVos)){
			return list;
		}

		if(VOUtils.isEmpty(newVOs)){
			list.set(1, baseVos);
		}else if(VOUtils.isEmpty(baseVos)){
			list.set(0, newVOs);
		}else{
			Map<String, SuperVO> baseMap = VOUtils.changeCollectionToMap(baseVos, baseVos.get(0).getPKFieldName());

			for (Iterator<SuperVO> iter = newVOs.iterator(); iter.hasNext();) {
				SuperVO newvo = iter.next();
				String primaryKey = newvo.getPrimaryKey();
				if(primaryKey==null || !baseMap.containsKey(primaryKey)){
					list.get(0).add(newvo);

				}else if(baseMap.containsKey(primaryKey)){
					list.get(2).add(newvo);
					baseMap.remove(primaryKey);
				}
			}
			list.get(1).addAll(baseMap.values());
		}

		return list;
	}

	private SuperVO[] checkTsRetrBaseVO(SuperVO[] superVOs, boolean cascade) throws DAOException {

		String[] pks = getPrimaryKeys(superVOs);

		Collection<SuperVO> basevos = getVOByPks(superVOs[0].getClass(), pks, cascade);

		SuperVO[] superVOs2 = basevos.toArray(new SuperVO[] {});

		if (!CommonUtils.checkTs(superVOs, superVOs2)) {
			throw new DAOException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000255")/*@res "并发操作失败, 数据已经更新!"*/);
		}

		return superVOs2;

	}

	private String[] getPrimaryKeys(SuperVO[] superVOs) {

		String[] pks = new String[superVOs.length];
		for (int i = 0; i < superVOs.length; i++) {
			pks[i] = superVOs[i].getPrimaryKey();
		}
		return pks;
	}

	public void update(SuperVO vo, String[] fields) throws DAOException {
		baseDAO.updateVO(vo, fields);
	}

	public void update(SuperVO[] vos, String[] fields) throws DAOException {
		baseDAO.updateVOArray(vos, fields);
	}

}