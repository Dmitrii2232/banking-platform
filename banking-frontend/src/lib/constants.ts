// Конфигурация приложения
export const APP_CONFIG = {
  NAME: 'ТБанк',
  DESCRIPTION: 'Современный банкинг для ваших финансовых потребностей',
  VERSION: '1.0.0',
  SUPPORT_EMAIL: 'support@tbank.ru',
  SUPPORT_PHONE: '8-800-555-35-35',
  SUPPORT_PHONE_FORMATTED: '+7 (800) 555-35-35',
  SUPPORT_HOURS: 'Круглосуточно',
  COMPANY: 'АО "ТБанк"',
  FOUNDED_YEAR: 1998,
  LICENSE: 'Лицензия ЦБ РФ №1234 от 01.01.1998',
  ADDRESS: 'г. Москва, ул. Примерная, д. 1',
  INN: '7701234567',
  KPP: '770101001',
  OGRN: '1234567890123',
  OKPO: '12345678',
} as const

// API конфигурация
export const API_CONFIG = {
  BASE_URL: process.env.NEXT_PUBLIC_API_URL || 'https://api.tbank.ru',
  TIMEOUT: 30000,
  VERSION: 'v1',
  ENDPOINTS: {
    AUTH: {
      LOGIN: '/auth/login',
      REGISTER: '/auth/register',
      LOGOUT: '/auth/logout',
      REFRESH: '/auth/refresh',
    },
    USER: {
      PROFILE: '/user/profile',
      ACCOUNTS: '/user/accounts',
      CARDS: '/user/cards',
      TRANSACTIONS: '/user/transactions',
      SETTINGS: '/user/settings',
    },
    CARDS: {
      LIST: '/cards',
      CREATE: '/cards',
      DETAILS: '/cards/{id}',
      BLOCK: '/cards/{id}/block',
      UNBLOCK: '/cards/{id}/unblock',
    },
    TRANSFERS: {
      INTERNAL: '/transfers/internal',
      EXTERNAL: '/transfers/external',
      HISTORY: '/transfers/history',
      TARIFFS: '/transfers/tariffs',
    },
    DEPOSITS: {
      LIST: '/deposits',
      OPEN: '/deposits/open',
      CLOSE: '/deposits/{id}/close',
      CALCULATE: '/deposits/calculate',
    },
    CREDITS: {
      LIST: '/credits',
      APPLY: '/credits/apply',
      CALCULATE: '/credits/calculate',
      SCHEDULE: '/credits/{id}/schedule',
    },
    INVESTMENTS: {
      PRODUCTS: '/investments/products',
      PORTFOLIO: '/investments/portfolio',
      BUY: '/investments/buy',
      SELL: '/investments/sell',
    },
  },
} as const

// Навигация
export const NAVIGATION = {
  MAIN: [
    { href: '/', label: 'Главная', icon: 'Home' },
    { href: '/dashboard', label: 'Личный кабинет', icon: 'User', auth: true },
    { href: '/cards', label: 'Карты', icon: 'CreditCard' },
    { href: '/transfers', label: 'Переводы', icon: 'Send', auth: true },
    { href: '/deposits', label: 'Вклады', icon: 'PiggyBank' },
    { href: '/credits', label: 'Кредиты', icon: 'TrendingUp' },
    { href: '/investments', label: 'Инвестиции', icon: 'BarChart3' },
    { href: '/branches', label: 'Отделения', icon: 'MapPin' },
    { href: '/help', label: 'Помощь', icon: 'HelpCircle' },
  ],
  FOOTER_SECTIONS: [
    {
      title: 'О банке',
      links: [
        { label: 'История банка', href: '/about/history' },
        { label: 'Руководство', href: '/about/leadership' },
        { label: 'Лицензии', href: '/about/licenses' },
        { label: 'Реквизиты', href: '/about/requisites' },
        { label: 'Вакансии', href: '/about/career' },
        { label: 'Новости', href: '/about/news' },
      ],
    },
    {
      title: 'Клиентам',
      links: [
        { label: 'Тарифы и условия', href: '/clients/tariffs' },
        { label: 'Документы и формы', href: '/clients/documents' },
        { label: 'Частые вопросы', href: '/clients/faq' },
        { label: 'Контакты отделений', href: '/clients/branches' },
        { label: 'Онлайн-помощник', href: '/clients/support' },
        { label: 'Обучение', href: '/clients/education' },
      ],
    },
    {
      title: 'Инвесторам',
      links: [
        { label: 'Финансовая отчетность', href: '/investors/reports' },
        { label: 'Раскрытие информации', href: '/investors/disclosure' },
        { label: 'Акционерам', href: '/investors/shareholders' },
        { label: 'Кредитный рейтинг', href: '/investors/rating' },
        { label: 'Дивиденды', href: '/investors/dividends' },
        { label: 'Стратегия развития', href: '/investors/strategy' },
      ],
    },
    {
      title: 'Бизнесу',
      links: [
        { label: 'Расчетный счет', href: '/business/account' },
        { label: 'Бизнес-карты', href: '/business/cards' },
        { label: 'Торговый эквайринг', href: '/business/acquiring' },
        { label: 'Бизнес-кредиты', href: '/business/credits' },
        { label: 'Зарплатные проекты', href: '/business/salary' },
        { label: 'Интернет-банк для бизнеса', href: '/business/internet-bank' },
      ],
    },
  ],
} as const

// Финансовые константы
export const FINANCE = {
  CURRENCIES: {
    RUB: { code: 'RUB', symbol: '₽', name: 'Российский рубль' },
    USD: { code: 'USD', symbol: '$', name: 'Доллар США' },
    EUR: { code: 'EUR', symbol: '€', name: 'Евро' },
    CNY: { code: 'CNY', symbol: '¥', name: 'Китайский юань' },
  },
  
  CREDIT_TYPES: [
    { id: 'cash', name: 'Кредит наличными', min: 10000, max: 5000000, rate: 5.9 },
    { id: 'mortgage', name: 'Ипотека', min: 500000, max: 50000000, rate: 7.4 },
    { id: 'auto', name: 'Автокредит', min: 100000, max: 10000000, rate: 8.9 },
    { id: 'refinance', name: 'Рефинансирование', min: 50000, max: 3000000, rate: 10.5 },
  ],
  
  DEPOSIT_TYPES: [
    { id: 'savings', name: 'Накопительный счет', rate: 6.5, min: 0, capitalization: true },
    { id: 'term', name: 'Срочный вклад', rate: 8.5, min: 10000, capitalization: true },
    { id: 'business', name: 'Вклад для бизнеса', rate: 7.2, min: 100000, capitalization: false },
    { id: 'multicurrency', name: 'Мультивалютный', rate: 5.8, min: 50000, capitalization: true },
  ],
  
  CARD_TYPES: [
    { id: 'debit', name: 'Дебетовая карта', annual: 0, cashback: 10, color: 'blue' },
    { id: 'credit', name: 'Кредитная карта', annual: 0, gracePeriod: 120, color: 'purple' },
    { id: 'premium', name: 'Премиум карта', annual: 12000, concierge: true, color: 'gold' },
    { id: 'virtual', name: 'Виртуальная карта', annual: 0, instant: true, color: 'green' },
  ],
} as const

// Социальные сети
export const SOCIAL_MEDIA = [
  { platform: 'Facebook', url: 'https://facebook.com/tbank', icon: 'Facebook', color: '#1877F2' },
  { platform: 'Twitter', url: 'https://twitter.com/tbank', icon: 'Twitter', color: '#1DA1F2' },
  { platform: 'Instagram', url: 'https://instagram.com/tbank', icon: 'Instagram', color: '#E4405F' },
  { platform: 'YouTube', url: 'https://youtube.com/tbank', icon: 'Youtube', color: '#FF0000' },
  { platform: 'LinkedIn', url: 'https://linkedin.com/company/tbank', icon: 'Linkedin', color: '#0A66C2' },
  { platform: 'VK', url: 'https://vk.com/tbank', icon: 'MessageCircle', color: '#0077FF' },
] as const

// Дни и время работы
export const WORK_SCHEDULE = {
  BRANCHES: {
    weekdays: '09:00 - 20:00',
    saturday: '10:00 - 18:00',
    sunday: '10:00 - 16:00',
    holidays: '10:00 - 14:00',
  },
  CALL_CENTER: {
    daily: '00:00 - 24:00',
    support: 'Круглосуточная поддержка',
  },
  ONLINE_BANK: {
    available: '24/7',
    maintenance: 'Ежедневно 03:00 - 04:00',
  },
} as const

// Безопасность
export const SECURITY = {
  PASSWORD: {
    MIN_LENGTH: 8,
    REQUIRE_UPPERCASE: true,
    REQUIRE_LOWERCASE: true,
    REQUIRE_NUMBERS: true,
    REQUIRE_SPECIAL: false,
    EXPIRE_DAYS: 90,
  },
  SESSION: {
    TIMEOUT_MINUTES: 30,
    MAX_DEVICES: 5,
  },
  VERIFICATION: {
    SMS_CODE_LENGTH: 6,
    EMAIL_CODE_LENGTH: 6,
    CODE_EXPIRE_MINUTES: 5,
    MAX_ATTEMPTS: 3,
  },
} as const

// Статистика банка
export const BANK_STATS = {
  CLIENTS: 2000000,
  CARDS_ISSUED: 5000000,
  BRANCHES: 250,
  ATMS: 5000,
  EMPLOYEES: 15000,
  SATISFACTION_RATE: 98.7,
  ASSETS: 2500000000000, // 2.5 трлн рублей
  CAPITAL: 300000000000, // 300 млрд рублей
} as const

// Коды ошибок
export const ERROR_CODES = {
  // Auth errors
  AUTH_INVALID_CREDENTIALS: 'AUTH001',
  AUTH_SESSION_EXPIRED: 'AUTH002',
  AUTH_UNAUTHORIZED: 'AUTH003',
  AUTH_INVALID_TOKEN: 'AUTH004',
  
  // Validation errors
  VALIDATION_FAILED: 'VAL001',
  VALIDATION_REQUIRED: 'VAL002',
  VALIDATION_INVALID_FORMAT: 'VAL003',
  VALIDATION_OUT_OF_RANGE: 'VAL004',
  
  // Business errors
  INSUFFICIENT_FUNDS: 'BIZ001',
  TRANSACTION_LIMIT_EXCEEDED: 'BIZ002',
  CARD_BLOCKED: 'BIZ003',
  ACCOUNT_FROZEN: 'BIZ004',
  
  // System errors
  INTERNAL_ERROR: 'SYS001',
  SERVICE_UNAVAILABLE: 'SYS002',
  DATABASE_ERROR: 'SYS003',
  EXTERNAL_API_ERROR: 'SYS004',
  
  // Client errors
  CLIENT_NOT_FOUND: 'CLI001',
  CARD_NOT_FOUND: 'CLI002',
  ACCOUNT_NOT_FOUND: 'CLI003',
  TRANSACTION_NOT_FOUND: 'CLI004',
} as const

// Типы уведомлений
export const NOTIFICATION_TYPES = {
  INFO: 'info',
  SUCCESS: 'success',
  WARNING: 'warning',
  ERROR: 'error',
  TRANSACTION: 'transaction',
  PROMOTION: 'promotion',
  SECURITY: 'security',
} as const

// Размеры экранов для responsive дизайна
export const BREAKPOINTS = {
  xs: 0,
  sm: 640,
  md: 768,
  lg: 1024,
  xl: 1280,
  '2xl': 1536,
} as const

// Цвета банка
export const BANK_COLORS = {
  primary: {
    50: '#eff6ff',
    100: '#dbeafe',
    200: '#bfdbfe',
    300: '#93c5fd',
    400: '#60a5fa',
    500: '#3b82f6', // Основной синий
    600: '#2563eb',
    700: '#1d4ed8',
    800: '#1e40af',
    900: '#1e3a8a',
  },
  secondary: {
    50: '#f0f9ff',
    100: '#e0f2fe',
    200: '#bae6fd',
    300: '#7dd3fc',
    400: '#38bdf8',
    500: '#0ea5e9',
    600: '#0284c7',
    700: '#0369a1',
    800: '#075985',
    900: '#0c4a6e',
  },
  accent: {
    50: '#ecfdf5',
    100: '#d1fae5',
    200: '#a7f3d0',
    300: '#6ee7b7',
    400: '#34d399',
    500: '#10b981',
    600: '#059669',
    700: '#047857',
    800: '#065f46',
    900: '#064e3b',
  },
  dark: {
    50: '#f9fafb',
    100: '#f3f4f6',
    200: '#e5e7eb',
    300: '#d1d5db',
    400: '#9ca3af',
    500: '#6b7280',
    600: '#4b5563',
    700: '#374151',
    800: '#1f2937',
    900: '#111827',
  },
} as const

// Карусель баннеров
export const BANNERS = [
  {
    id: 1,
    title: 'Новая кредитная карта',
    description: 'Кредитный лимит до 1 млн ₽, 0% на всё первые 3 месяца',
    buttonText: 'Оформить карту',
    gradient: 'from-blue-600 to-purple-600',
    image: 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?auto=format&fit=crop&w=1600&q=80',
    features: ['Кэшбэк до 10%', 'Бесплатное обслуживание', 'Мобильное приложение']
  },
  {
    id: 2,
    title: 'Выгодный вклад',
    description: 'До 12% годовых с капитализацией процентов',
    buttonText: 'Открыть вклад',
    gradient: 'from-green-600 to-teal-600',
    image: 'https://images.unsplash.com/photo-1554224155-6726b3ff858f?auto=format&fit=crop&w=1600&q=80',
    features: ['Пополнение в любое время', 'Снятие без потерь', 'Страхование вклада']
  },
  {
    id: 3,
    title: 'Ипотека 5.9%',
    description: 'Самые низкие ставки на рынке. Одобрение за 30 минут',
    buttonText: 'Рассчитать ипотеку',
    gradient: 'from-orange-600 to-red-600',
    image: 'https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&w=1600&q=80',
    features: ['Первоначальный взнос 15%', 'Онлайн одобрение', 'Субсидированная ставка']
  },
  {
    id: 4,
    title: 'Инвестиции',
    description: 'Доверительное управление с доходностью до 25% годовых',
    buttonText: 'Начать инвестировать',
    gradient: 'from-purple-600 to-pink-600',
    image: 'https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1600&q=80',
    features: ['От 10 000 ₽', 'Личный брокер', 'Обучение торговле']
  },
] as const

// Тестовые данные для разработки
export const MOCK_DATA = {
  USER: {
    id: 'user_123',
    name: 'Иван Иванов',
    email: 'ivan@example.com',
    phone: '+7 (999) 999-99-99',
    avatar: 'https://i.pravatar.cc/150?img=1',
    accounts: [
      { id: 'acc_001', balance: 125000, currency: 'RUB', type: 'checking', number: '40817810099910004312' },
      { id: 'acc_002', balance: 50000, currency: 'USD', type: 'savings', number: '40817810099910004313' },
    ],
    cards: [
      { id: 'card_001', number: '1234567890123456', type: 'debit', balance: 125000, expiry: '12/25', holder: 'IVAN IVANOV' },
      { id: 'card_002', number: '9876543210987654', type: 'credit', balance: 500000, expiry: '06/26', holder: 'IVAN IVANOV' },
    ],
  },
  TRANSACTIONS: [
    { id: 't1', date: '2024-01-15', amount: -1500, description: 'Оплата в магазине', category: 'shopping' },
    { id: 't2', date: '2024-01-14', amount: 50000, description: 'Зарплата', category: 'income' },
    { id: 't3', date: '2024-01-13', amount: -2500, description: 'Кафе', category: 'food' },
    { id: 't4', date: '2024-01-12', amount: -7500, description: 'Транспорт', category: 'transport' },
    { id: 't5', date: '2024-01-11', amount: 20000, description: 'Перевод от друга', category: 'transfer' },
  ],
} as const

// Экспорт всех констант
export default {
  APP_CONFIG,
  API_CONFIG,
  NAVIGATION,
  FINANCE,
  SOCIAL_MEDIA,
  WORK_SCHEDULE,
  SECURITY,
  BANK_STATS,
  ERROR_CODES,
  NOTIFICATION_TYPES,
  BREAKPOINTS,
  BANK_COLORS,
  BANNERS,
  MOCK_DATA,
}