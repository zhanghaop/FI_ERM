package nc.bs.er.control;


//import nc.vo.gateway60.itfs.NtbParamVO;

public class SqlTools extends ErmNtbSqlTools {
	private String[] selectFields;
	public SqlTools() {
		super();
	}
	public String getGroupBySql() throws Exception {
		String sql = " ";
		if(selectFields == null){
			return "";
		}
		String[] names = selectFields;
//		QueryVO.getGroupCount��һ����
		getSqlVO().setGroupCount(names.length);
		for (int i = 0; i < names.length; i++) {
			if (names[i] == null || names[i].trim().equals("")) {
				throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000077")/*@res "����GroupBy�е��ֶβ���Ϊ�գ�"*/);
			}
			String colName = getcolName(names,i);
			if (i == 0) {
				sql += colName;
			} else {
				sql += " ," + colName;
			}

		}
		String s_bhxj=getBhxjStr(getFirstNtbVO());
		sql+=s_bhxj;
		return sql;
	}
//FIXME ���������ע��
//	public String getWhereFromVO(boolean bArapbill) throws Exception {
//		ArrayList array = getSqlVO().getSourceArr();
//		NtbParamVO ntbvo = (NtbParamVO) array
//		.get(0);
//
//		String sql = " and ";
//		sql += getDwbmPartWhere();
//		if(getSqlVO().isDetail())
//		{
////			String pk_corp = ntbvo.getPkcorp();
////			sql += " and zb.dwbm='" + pk_corp + "' ";
//			//����Ϊʲôע������
//			if (ntbvo.getPk_currency() != null &&
//					!ntbvo.getPk_currency().trim().equals("")) {
//				sql += " and fb.bzbm ='" + ntbvo.getPk_currency() + "' ";
//			}
//		}
//
////		boolean isUnInure = ntbvo.isUnInure();
//		String rq = "zb.djrq";
////		if(isUnInure){//����δ��ˣ�������δ��Ч
////		rq = "zb.djrq";
////		}else{//����δ��Ч
////		rq = "zb.shrq";
////		}
//		//�����Ϸ�������������
//		String dataType = ntbvo.getDateType();
//		if(ntbvo.getDateType() != null)
////			if(ntbvo.getDateType().trim().equals("��������"))
////			rq = "zb.djrq";
////			else if(ntbvo.getDateType().trim().equals("�������"))
////			rq = "zb.shrq";
////			else
//			if(ntbvo.getDateType().trim().equals("zb.sxrq"))
//			{
//				if(bArapbill)
//					rq = "zb.sxrq";
//				else
//					rq = "zb.shrq";
//			}
////		Ԥ��淶  2008-11-21
//			else if(dataType.trim().equals("zb.djrq") || dataType.trim().equals("zb.shrq"))
//				rq = dataType.trim();
//			else
//				throw new Exception(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000391")/*@res "Ԥ�㴫���������ʹ��󣡣�"*/);
//		if (ntbvo.getBegDate() != null && ntbvo.getBegDate().toString().trim().length()>0) {
//			sql += " and "+rq+" >='" + ntbvo.getBegDate() + "' ";
//		}
//		if (ntbvo.getEndDate() != null && ntbvo.getEndDate().toString().trim().length()>0) {
//			sql += " and "+rq+" <='" + ntbvo.getEndDate() + "' ";
//		}
//		String strBillType = getDjlxbmPartWhere();
//		if(strBillType.trim().length() > 0){
//			sql += " and   " + strBillType;
//		}
//		return sql;
//	}

	/**
	 * @return the selectFields
	 */
	public String[] getSelectFields() {
		return selectFields;
	}

	/**
	 * @param selectFields the selectFields to set
	 */
	public void setSelectFields(String[] selectFields) {
		this.selectFields = selectFields;
	}
}