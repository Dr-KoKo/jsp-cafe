package codesquad.common.db.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TransactionProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(TransactionProxy.class);

    private final Object target;
    private final TxManager txManager;

    public TransactionProxy(Object target, TxManager txManager) {
        this.target = target;
        this.txManager = txManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        txManager.begin();
        logger.info("transaction started");
        try {
            Object result = method.invoke(target, args);
            txManager.commit();
            logger.info("transaction committed");
            return result;
        } catch (InvocationTargetException e) {
            txManager.rollback();
            throw e.getCause();
        } catch (RuntimeException e) {
            txManager.commit();
            throw e;
        }
    }
}
