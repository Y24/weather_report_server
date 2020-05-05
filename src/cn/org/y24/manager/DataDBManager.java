package cn.org.y24.manager;

import cn.org.y24.entity.AccountEntity;
import cn.org.y24.entity.QueryHistoryEntity;
import cn.org.y24.interfaces.BaseDBManager;
import cn.org.y24.actions.DataDBAction;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

public class DataDBManager extends BaseDBManager<DataDBAction> {

    public DataDBManager(Connection connection) {
        super(connection);
    }

    @Override
    public boolean execute(DataDBAction action) {
        final AccountEntity account = action.getAccount();
        switch (action.getType()) {
            case fetch -> {
                final List<String[]> result = executeQuerySQL("select weatherData from data where username=\"" + account.getName() + "\";");
                final List<QueryHistoryEntity> list = new LinkedList<>();
                result.remove(0);
                result.forEach(s -> list.add(new QueryHistoryEntity(s[0])));
                action.setData(list);
                return true;
            }
            case push -> {
                List<QueryHistoryEntity> list = action.getData();
                list.forEach(queryHistoryEntity -> executeUpdateSQL("insert into data(username,weatherData) values(\"" + account.getName() + "\",\"" + queryHistoryEntity.toString() + "\");"));
                return true;
            }
        }
        return false;
    }
}
