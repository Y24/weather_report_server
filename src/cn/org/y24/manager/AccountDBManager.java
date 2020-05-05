package cn.org.y24.manager;

import cn.org.y24.entity.AccountEntity;
import cn.org.y24.interfaces.BaseDBManager;
import cn.org.y24.actions.AccountDBAction;

import java.sql.Connection;
import java.util.List;

public class AccountDBManager extends BaseDBManager<AccountDBAction> {
    public AccountDBManager(Connection connection) {
        super(connection);
    }

    @Override
    public boolean execute(AccountDBAction action) {
        final AccountEntity account = action.getAccount();
        switch (action.getType()) {
            case login, logout -> {
                final List<String[]> result = executeQuerySQL("select * from account where username=\"" + account.getName() + "\"&&password=\"" + account.getPassword() + "\";");
                return result.size() == 2;
            }
            case register -> {
                final List<String[]> result = executeQuerySQL("select * from account where username=\"" + account.getName() + "\";");
                if (result.size() == 1) {
                    executeUpdateSQL("insert into account(username,password) values(\"" + account.getName() + "\",\"" + account.getPassword() + "\");");
                    return true;
                }
                return false;
            }
            case dispose -> {
                final List<String[]> result = executeQuerySQL("select * from account where username=\"" + account.getName() + "\"&&password=\"" + account.getPassword() + "\";");
                if (result.size() == 2) {
                    executeUpdateSQL("delete from account where username=\"" + account.getName() + "\";");
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
