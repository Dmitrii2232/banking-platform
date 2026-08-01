package com.bank.domain.exception;

public class SanctionedClientException extends BankingException {
    
    private final String clientId;
    private final String sanctionList;
    
    public SanctionedClientException(String clientId, String sanctionList) {
        super("SANCTIONED_CLIENT",
            String.format("Клиент %s найден в санкционном списке: %s", clientId, sanctionList));
        this.clientId = clientId;
        this.sanctionList = sanctionList;
    }
    
    public String getClientId() { return clientId; }
    public String getSanctionList() { return sanctionList; }
}