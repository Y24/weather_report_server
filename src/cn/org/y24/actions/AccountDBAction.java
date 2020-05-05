package cn.org.y24.actions;

import cn.org.y24.entity.AccountEntity;
import cn.org.y24.enums.AccountDBActionType;

public class AccountDBAction {
    private final AccountDBActionType type;

    private final AccountEntity account;

    public AccountDBAction(AccountEntity account, AccountDBActionType type) {
        this.type = type;
        this.account = account;
    }

    public AccountDBActionType getType() {
        return type;
    }

    public AccountEntity getAccount() {
        return account;
    }


}
