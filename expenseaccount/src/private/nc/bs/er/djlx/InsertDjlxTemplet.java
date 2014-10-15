package nc.bs.er.djlx;


public class InsertDjlxTemplet {

//	public void insertDefaultDjlxTemplet() throws BusinessException {
//		try {
//			DjlxtempletVO[] vos = null;
//			java.util.Vector vec = new java.util.Vector();
//			DjlxtempletDMO tempdmo = new DjlxtempletDMO();
//			DjLXDMO lxdmo = new DjLXDMO();
//			DjLXVO[] djlxvo = lxdmo.queryAllByInit(null);
//			java.util.Hashtable hash = new java.util.Hashtable();
//			if (djlxvo != null && djlxvo.length != 0) {
//				for (int i = 0; i < djlxvo.length; i++) {
//					if (djlxvo[i].getDwbm().equalsIgnoreCase("0001")
//							&& djlxvo[i].getDjmboid() != null) {
//						hash.put(djlxvo[i].getDjdl(), djlxvo[i].getDjmboid());
//					}
//					if (djlxvo[i].getDjmboid() == null) {
//						djlxvo[i].setDjmboid((String) hash.get(djlxvo[i]
//								.getDjdl()));
//					}
//					// if (djlxvo[i].getDjmboid() != null) {
//					DjlxtempletVO[] temps = tempdmo.queryTemplets(djlxvo[i]
//							.getDjlxoid(), djlxvo[i].getDjdl());
//					if (temps != null && temps.length != 0) {
//						for (int j = 0; j < temps.length; j++) {
//							// 如果已分配就不重复分配
//							if (temps[j].getPk_djlx() != null)
//								continue;
//							temps[j].setPk_djlx(djlxvo[i].getDjlxoid());
//							temps[j].setPk_billtemplet(djlxvo[i].getDjmboid());
//							vec.addElement(temps[j]);
//						}
//
//					}
//					// }
//
//				}
//
//			}
//			if (vec.size() > 0) {
//				vos = new DjlxtempletVO[vec.size()];
//				vec.copyInto(vos);
//				BaseDAO dao = new BaseDAO();
//				dao.insertVOArray(vos);
//			}
//		} catch (Exception ex) {
//			Log.getInstance(this.getClass()).error(ex);
//			throw new BusinessException(ex);
//		}
//	}

}
