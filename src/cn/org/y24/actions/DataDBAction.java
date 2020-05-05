package cn.org.y24.actions;

import cn.org.y24.entity.AccountEntity;
import cn.org.y24.entity.QueryHistoryEntity;
import cn.org.y24.enums.DataDBActionType;

import java.util.List;

public class DataDBAction {

    private final AccountEntity account;
    private final DataDBActionType type;
    private List<QueryHistoryEntity> data;

    public DataDBAction(AccountEntity account, DataDBActionType type) {
        this.account = account;
        this.type = type;
    }

    public DataDBAction(AccountEntity account, DataDBActionType type, List<QueryHistoryEntity> data) {
        this.account = account;
        this.type = type;
        this.data = data;
    }

    public DataDBActionType getType() {
        return type;
    }

    public List<QueryHistoryEntity> getData() {
        return data;
    }

    public void setData(List<QueryHistoryEntity> data) {
        this.data = data;
    }


    public AccountEntity getAccount() {
        return account;
    }


}
