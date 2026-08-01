CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS chart_of_accounts (
    account_code    VARCHAR(20) PRIMARY KEY,
    account_name    VARCHAR(200) NOT NULL,
    side            VARCHAR(10) NOT NULL CHECK (side IN ('ACTIVE', 'PASSIVE')),
    parent_code     VARCHAR(20),
    level           INT NOT NULL DEFAULT 1,
    is_leaf         BOOLEAN NOT NULL DEFAULT TRUE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS account_balances (
    account_code    VARCHAR(20) NOT NULL,
    balance_date    DATE NOT NULL,
    opening_balance NUMERIC(20,2) NOT NULL DEFAULT 0,
    debit_turnover  NUMERIC(20,2) NOT NULL DEFAULT 0,
    credit_turnover NUMERIC(20,2) NOT NULL DEFAULT 0,
    closing_balance NUMERIC(20,2) NOT NULL DEFAULT 0,
    currency        VARCHAR(3) NOT NULL DEFAULT 'RUB',
    PRIMARY KEY (account_code, balance_date, currency)
);

CREATE TABLE IF NOT EXISTS accounting_entries (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_id        UUID NOT NULL,
    debit_account   VARCHAR(20) NOT NULL REFERENCES chart_of_accounts(account_code),
    credit_account  VARCHAR(20) NOT NULL REFERENCES chart_of_accounts(account_code),
    amount          NUMERIC(20,2) NOT NULL,
    currency        VARCHAR(3) NOT NULL DEFAULT 'RUB',
    description     TEXT,
    posted_date     DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_accounting_event ON accounting_entries(event_id);
CREATE INDEX IF NOT EXISTS idx_accounting_posted_date ON accounting_entries(posted_date);
CREATE INDEX IF NOT EXISTS idx_accounting_debit ON accounting_entries(debit_account, posted_date);
CREATE INDEX IF NOT EXISTS idx_accounting_credit ON accounting_entries(credit_account, posted_date);