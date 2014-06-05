package org.jboss.soa.qa.services;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.xa.Xid;

import org.jboss.soa.qa.EjbTransactionTestException;
import org.jboss.soa.qa.beans.TestBeanLocal;
import org.jboss.soa.qa.services.util.DummyXAResource;
import org.switchyard.annotations.Requires;
import org.switchyard.component.bean.Service;
import org.switchyard.policy.TransactionPolicy;


@Service(TaskAService.class) 
@Requires(transaction = {TransactionPolicy.MANAGED_TRANSACTION_GLOBAL})
public class TaskAServiceBean implements TaskAService {
	
	@EJB
    private TestBeanLocal bean;
	
	
	@Resource(lookup="java:jboss/TransactionManager")
	private TransactionManager transactionManager;
	
	
	
	@Override
	public void delegateInExistingTransaction(List<Xid> xids) throws EjbTransactionTestException{
		process(xids);
		bean.delegateInExistingTransaction(xids);
	}

	

	@Override
	public void delegateInNewTransaction(List<Xid> xids) throws EjbTransactionTestException {
		process(xids);
		bean.delegateInNewTransaction(xids);
	}
	
	
	
	@Override
	public void process(List<Xid> xids) throws EjbTransactionTestException {
		System.out.println("TaskAServiceBean.process()");
		
		DummyXAResource xaRes = new DummyXAResource();
		try {
			transactionManager.getTransaction().enlistResource(xaRes);
			
		} catch (IllegalStateException e) {
			throw new EjbTransactionTestException(e);
		} catch (RollbackException e) {
			throw new EjbTransactionTestException(e);
		} catch (SystemException e) {
			throw new EjbTransactionTestException(e);
		} 
		xids.add(xaRes.getXid());
	}

}
