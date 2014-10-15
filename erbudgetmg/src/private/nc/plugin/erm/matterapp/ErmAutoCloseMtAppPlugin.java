package nc.plugin.erm.matterapp;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.pubitf.erm.matterapp.IErmMatterAppBillClose;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.ArrayUtils;

/**
 * �������뵥�Զ��ر� ��̨���� <b>Date:</b>2012-11-21<br>
 *
 * @author��wangyhh@ufida.com.cn
 */
public class ErmAutoCloseMtAppPlugin implements IBackgroundWorkPlugin {

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc) throws BusinessException {
		try {
			// ��ѯȫ��δ�ر������㡾ϵͳʱ��>�Զ��ر����ڡ�����
			String sql = "  CLOSE_STATUS = " + ErmMatterAppConst.CLOSESTATUS_N + " AND effectstatus = " + BXStatusConst.SXBZ_VALID + " AND AUTOCLOSEDATE <'" + new UFDate().toStdString() + "' ";
			AggMatterAppVO[] mtAppVos = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByWhere(sql);
			if (ArrayUtils.isEmpty(mtAppVos)) {
				bgwc.setLogStr(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0032")/*@res "�޵��ݿ��Զ��ر�"*/);
				return null;
			}

			// �����ر�
			NCLocator.getInstance().lookup(IErmMatterAppBillClose.class).closeVOs(mtAppVos);
			bgwc.setLogStr(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0033")/*@res "�Զ��رճɹ�"*/);
		} catch (Exception e) {
			bgwc.setLogStr(e.getMessage());
		}
		return null;
	}

}