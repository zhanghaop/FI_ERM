package nc.bs.erm.event;

import nc.bs.businessevent.AbstractBusinessEvent;
import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;

public class ErmBusinessEvent extends AbstractBusinessEvent {

	private static final long serialVersionUID = -5553263284849440774L;

	public static class ErmCommonUserObj extends ValueObject {
		/**
		 * 
		 */ 
		private static final long serialVersionUID = 1L;

		private Object newObjects;
		private Object oldObjects;

		private ErmCommonUserObj(Object newObjects, Object oldObjects) {
			this.newObjects = newObjects;
			this.oldObjects = oldObjects;
		}

		public Object getNewObjects() {
			return newObjects;
		}

		public void setNewObjects(Object newObjects) {
			this.newObjects = newObjects;
		}

		public Object getOldObjects() {
			return oldObjects;
		}

		public void setOldObjects(Object oldObjects) {
			this.oldObjects = oldObjects;
		}

		@Override
		public String getEntityName() {
			return this.getClass().getName();
		}

		@Override
		public void validate() throws ValidationException {

		}

	}

	private Object oldObjs = null;
	private Object newObjs = null;
	private Object userDefineObjs = null;

	public ErmBusinessEvent(String sourceID, String eventType, Object objs) {
		super(sourceID, eventType);
		setNewObjs(objs);
	}

	public ErmBusinessEvent(String sourceID, String eventType, Object newObjs, Object oldObjs) {
		super(sourceID, eventType);
		setOldObjs(oldObjs);
		setNewObjs(newObjs);
	}

	public Object getOldObjs() {
		return oldObjs;
	}

	public void setOldObjs(Object oldObjs) {
		this.oldObjs = oldObjs;
	}

	public Object getNewObjs() {
		return newObjs;
	}

	public void setNewObjs(Object newObjs) {
		this.newObjs = newObjs;
	}

	public Object getObjs() {
		return newObjs;
	}

	public void setObjs(Object newObjs) {
		this.newObjs = newObjs;
	}

	public Object getUserDefineObjs() {
		return userDefineObjs;
	}

	public void setUserDefineObjs(Object userDefineObjs) {
		this.userDefineObjs = userDefineObjs;
	}

	@Override
	public ValueObject getUserObject() {
		return new ErmCommonUserObj(this.newObjs, this.oldObjs);
	}
}
