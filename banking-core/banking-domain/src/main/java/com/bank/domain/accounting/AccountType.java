package com.bank.domain.accounting;

public enum AccountType {
    CASH_DESK("20202", "Касса кредитных организаций", Side.ACTIVE),
    CORR_ACCOUNT("30102", "Корреспондентские счета в Банке России", Side.ACTIVE),
    CLIENT_DEPOSIT("42301", "Депозиты физических лиц до востребования", Side.PASSIVE),
    CLIENT_TERM_DEPOSIT("42303", "Срочные депозиты физических лиц", Side.PASSIVE),
    CLIENT_LOAN("45502", "Кредиты физическим лицам", Side.ACTIVE),
    INTEREST_PAYABLE("47411", "Начисленные проценты к уплате", Side.PASSIVE),
    INTEREST_RECEIVABLE("47427", "Требования по процентам", Side.ACTIVE),
    INCOME_INTEREST("70601", "Процентные доходы", Side.PASSIVE),
    EXPENSE_INTEREST("70201", "Процентные расходы", Side.ACTIVE),
    RESERVE("32001", "Резервы на возможные потери", Side.PASSIVE),
    CAPITAL("10207", "Уставный капитал", Side.PASSIVE);

    private final String code;
    private final String name;
    private final Side side;

    AccountType(String code, String name, Side side) {
        this.code = code;
        this.name = name;
        this.side = side;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public Side getSide() { return side; }

    public static AccountType fromCode(String code) {
        for (AccountType type : values()) {
            if (type.code.equals(code)) return type;
        }
        throw new IllegalArgumentException("Неизвестный счёт: " + code);
    }
}