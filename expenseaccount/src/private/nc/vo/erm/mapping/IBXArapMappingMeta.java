package nc.vo.erm.mapping;

import nc.vo.fipub.mapping.IArapMappingMeta;

/**
 * v6.1新增  扩展元数据子接口，借款报销元数据对应的类 类型
 * @author chendya
 *
 */
public interface IBXArapMappingMeta extends IArapMappingMeta{

	public Class<?> getMetaClass();
}
