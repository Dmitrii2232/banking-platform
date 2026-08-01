// src/lib/api.ts
const API_BASE = '/api';

function getToken(): string | null {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('access_token');
  }
  return null;
}

async function fetchApi<T>(url: string, options?: RequestInit): Promise<T> {
  const token = getToken();
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options?.headers,
  };

  const response = await fetch(`${API_BASE}${url}`, { ...options, headers });
  
  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Ошибка' }));
    throw new Error(error.message || `HTTP ${response.status}`);
  }
  
  return response.json();
}

export interface Product {
  productId: string;
  clientId: string;
  productType: string;
  status: string;
  balance: string;
  currency: string;
  version: number;
  isMaster: boolean;  // ← ДОБАВЛЕНО
}

export interface AccountBalance {
  accountCode: string;
  accountName: string;
  side: string;
  balance: string;
  currency: string;
}

export interface TransactionResult {
  transactionId: string;
  status: string;
}

export const productNames: Record<string, string> = {
  CURRENT_ACCOUNT: 'Текущий счёт',
  TERM_DEPOSIT: 'Срочный вклад',
  CREDIT_CARD: 'Кредитная карта',
  LOAN: 'Кредит',
};

export const api = {
  // Продукты
  getProducts: () => fetchApi<{ products: Product[]; totalCount: number }>('/products'),
  
  getProduct: (productId: string) =>
    fetchApi<Product>(`/products/${productId}`),

  openProduct: (data: { productType: string; interestRate: string; termMonths: number }) =>
    fetchApi<Product>('/products', { method: 'POST', body: JSON.stringify(data) }),

  openTermDeposit: (data: { interestRate: string; termMonths: number; capitalization: boolean; replenishable: boolean }) =>
    fetchApi<Product>('/products', { method: 'POST', body: JSON.stringify({ productType: 'TERM_DEPOSIT', interestRate: data.interestRate, termMonths: data.termMonths, capitalization: data.capitalization, replenishable: data.replenishable }) }),

  openCreditCard: (data: { interestRate: string; creditLimit: string }) =>
    fetchApi<Product>('/products', { method: 'POST', body: JSON.stringify({ productType: 'CREDIT_CARD', interestRate: data.interestRate, creditLimit: data.creditLimit }) }),

  openLoan: (data: { amount: string; interestRate: string; termMonths: number }) =>
    fetchApi<Product>('/products', { method: 'POST', body: JSON.stringify({ productType: 'LOAN', interestRate: data.interestRate, termMonths: data.termMonths, creditLimit: data.amount }) }),

  closeProduct: (productId: string, reason: string = 'Client request') =>
    fetchApi<{ productId: string; status: string }>(`/products/${productId}/close`, { 
      method: 'POST', 
      body: JSON.stringify({ reason }) 
    }),

  // Транзакции
  transfer: (data: { sourceProductId: string; destinationProductId: string; amount: number; currency: string }) =>
    fetchApi<TransactionResult>('/transactions/transfer', { method: 'POST', body: JSON.stringify(data) }),

  deposit: (data: { productId: string; amount: number; currency: string }) =>
    fetchApi<TransactionResult>('/transactions/deposit', { method: 'POST', body: JSON.stringify(data) }),
  
  withdraw: (data: { productId: string; amount: number; currency: string }) =>
    fetchApi<TransactionResult>('/transactions/withdraw', { method: 'POST', body: JSON.stringify(data) }),

  // Баланс
  getAccountBalance: (accountCode: string) =>
    fetchApi<AccountBalance>(`/accounts/${accountCode}/balance`),

  // Поиск клиента по телефону
  findClientByPhone: (phone: string) =>
    fetchApi<{ found: boolean; clientId: string; masterProductId: string }>(
      `/clients/search?phone=${encodeURIComponent(phone)}`
    ),

  // Получение мастер-счета
  getMasterAccount: () =>
    fetchApi<Product>('/master-account'),

  // Смена мастер-счета
  setMasterAccount: (productId: string) =>
    fetchApi<{ success: boolean; message: string }>('/master-account', {
      method: 'POST',
      body: JSON.stringify({ productId })
    }),
};