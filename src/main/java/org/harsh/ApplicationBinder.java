package org.harsh;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.harsh.daos.QADao;
import org.harsh.daos.UserDao;
import org.harsh.services.QAService;
import org.harsh.services.UserService;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(QAService.class).to(QAService.class);
        bind(UserService.class).to(UserService.class);
        bind(QADao.class).to(QADao.class);
        bind(UserDao.class).to(UserDao.class);
    }
}
