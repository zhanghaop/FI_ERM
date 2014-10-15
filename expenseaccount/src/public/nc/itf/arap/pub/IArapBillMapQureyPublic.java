package nc.itf.arap.pub;


import java.util.List;
import java.util.Map;
import nc.vo.ep.bx.BillTypeMapVO;
import nc.vo.pub.BusinessException;

/**
 * 单据类型对照关系查询接口<p>
 * 
 * 1、原有三个方法，queryBillMap(String sourcebilltype)，queryBillMaps两个修改后可传0-*个公司编码，
   这两个方法原用调用可以不修改，即传入0个公司，返回为集团的对照关系；
   queryBillMap(String sourcebilltype, String targetparenttype)修改后，必须传入且只能传入一个公司
   编码，传入null时返回为集团的对照关系，原用调用必须传入公司编码或null。<p>
   2、新加三个方法，这三个方法，功能与原三个方法类似，只是在原有方法上新加公司数组参数，返回按公司key对应的
   Map，用于查多公司对应的对照关系，并按公司取相应原对照关系 <p>
   3、方法具体使用见IArapBillMapQureyPublic接口注释。
 
 * @author tanfh
 *
 */
public interface IArapBillMapQureyPublic {
	
	/**
	 * 根据来源单据编码得到相关对照关系列表,
	 * 如果为null,返回所有
	 * @param sourcebilltype 来源单据编码
	 * @param pk_corps 公司编码，为空时，返回集团的设置
	 * @return
	 */
	public List<BillTypeMapVO> queryBillMap(String sourcebilltype, String[] pk_corps) throws BusinessException;
	
	/**
	 * 根据多个来源单据编码得到相关对照关系列表
	 * 如果为null,返回所有
	 * @param sourcebilltype 来源单据编码 
	 * @param pk_corps 公司编码，为空时，返回集团的设置
	 * @return
	 */
	public Map<String,List<BillTypeMapVO>> queryBillMaps(String[] sourcebilltypes, String[] pk_corps) throws BusinessException;
	
	/**
	 * 根据来源单据编码和目标单据类型唯一确定一个对照关系
	 * @param sourcebilltype 来源单据编码
	 * @param targetparenttype 目标单据类型
	 * @param pk_corps 公司编码，为空时，返回集团的设置; 要返回多个公司， 用queryCorpsBillMap
	 * @see IArapBillMapQureyPublic#queryCorpsBillMap
	 * @return
	 */
	public BillTypeMapVO queryBillMap(String sourcebilltype, String targetparenttype, String pk_corp) throws BusinessException;
	
	/**
	 * 根据来源单据编码得到相关对照关系列表,
	 * 如果为null,返回所有
	 * @param sourcebilltype 来源单据编码
	 * @param pk_corps 公司编码，返回所有记录
	 * @return 按公司分组的根据来源单据编码得到相关对照关系列表
	 */
	public Map<String, List<BillTypeMapVO>> queryCorpsBillMap(String sourcebilltype, String[] pk_corps) throws BusinessException;
	
	/**
	 * 根据多个来源单据编码得到相关对照关系列表
	 * 如果为null,返回所有
	 * @param sourcebilltype 来源单据编码
	 * @param pk_corps 公司编码，为空时，返回所有记录
	 * @return 按公司分组的根据多个来源单据编码得到相关对照关系列表
	 */
	public Map<String, Map<String,List<BillTypeMapVO>>> queryCorpsBillMaps(String[] sourcebilltypes, String[] pk_corps) throws BusinessException;
	
	/**
	 * 根据来源单据编码和目标单据类型唯一确定一个对照关系
	 * @param sourcebilltype 来源单据编码
	 * @param targetparenttype 目标单据类型
	 * @param pk_corps 公司编码，为空时，返回所有记录
	 * @return 按公司分组的根据来源单据编码和目标单据类型唯一确定一个对照关系
	 */
	public Map<String, BillTypeMapVO> queryCorpsBillMap(String sourcebilltype, String targetparenttype, String[] pk_corps) throws BusinessException;
	

}
