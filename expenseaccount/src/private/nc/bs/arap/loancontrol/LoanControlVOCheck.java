package nc.bs.arap.loancontrol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.er.common.dao.CommonDAO;
import nc.vo.ep.bx.LoanControlSchemaVO;
import nc.vo.ep.bx.LoanControlVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.common.VOCheck;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 *
 * nc.bs.arap.loancontrol.LoanControlVOCheck
 *
 * 借款报销控制vo检查类
 */
public class LoanControlVOCheck implements VOCheck {

	/**
	 *
	 * @throws BusinessException
	 * @see nc.vo.erm.common.VOCheck#check(nc.vo.pub.SuperVO)
	 *
	 * 检查内容: 币种/结算方式(为空匹配所有)/单据类型 的组合 在同一个公司下唯一
	 */
	public void check(SuperVO vo) throws BusinessException {
		LoanControlVO controlVO = (LoanControlVO) vo;
		CommonDAO dao = new CommonDAO();
		try {
			String sql = LoanControlVO.CURRENCY + "='" + controlVO.getCurrency() + "'" + " and " + LoanControlVO.PK_ORG + "='" + controlVO.getPk_org() + "'";

			if (controlVO.getPk_control() != null) {
				sql = sql + " and " + LoanControlVO.PK_CONTROL + "<>'" + controlVO.getPk_control() + "'";
			}
			Collection<SuperVO> os = dao.getVOs(LoanControlVO.class, sql, true);

			Map<String,String> keyV=new HashMap<String, String>();
			for (Iterator iter = os.iterator(); iter.hasNext();) {
				LoanControlVO nVO = (LoanControlVO) iter.next();
				List<LoanControlSchemaVO> schemavos = nVO.getSchemavos();
				for(LoanControlSchemaVO schema:schemavos){
					keyV.put(schema.getDjlxbm()+schema.getBalatype(),"");
				}
			}

			List<LoanControlSchemaVO> schemavos = controlVO.getSchemavos();
			for(LoanControlSchemaVO schema:schemavos){
				if(keyV.containsKey(schema.getDjlxbm()) || keyV.containsKey(schema.getDjlxbm()+schema.getBalatype())){
					ExceptionHandler.cteateandthrowException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000232")/*@res "保存失败,该配置包含币种/单据类型/结算方式的组合在该公司已经存在,不能重复配置"*/);
				}
			}
		} catch (DAOException e) {
			ExceptionHandler.handleException(this.getClass(), e);
		}
	}

}