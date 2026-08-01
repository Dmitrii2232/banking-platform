// ====================
// ТИПЫ
// ====================

export type ClassValue = string | number | boolean | undefined | null
export type ClassArray = ClassValue[]
export type ClassDictionary = Record<string, any>

// ====================
// УТИЛИТЫ ДЛЯ КЛАССОВ
// ====================

/**
 * Объединение классов (замена clsx)
 */
export function cn(...inputs: (ClassValue | ClassArray | ClassDictionary)[]): string {
  const classes: string[] = []

  for (const input of inputs) {
    if (!input) continue

    if (typeof input === 'string') {
      classes.push(input)
    } else if (typeof input === 'number') {
      classes.push(input.toString())
    } else if (Array.isArray(input)) {
      const inner = cn(...input)
      if (inner) {
        classes.push(inner)
      }
    } else if (typeof input === 'object') {
      for (const [key, value] of Object.entries(input)) {
        if (value) {
          classes.push(key)
        }
      }
    }
  }

  // Уникальные классы и сортировка для Tailwind (грубая замена twMerge)
  const uniqueClasses = new Set<string>()
  const sortedClasses: string[] = []

  for (const className of classes) {
    const parts = className.split(' ')
    for (const part of parts) {
      if (part.trim()) {
        // Удаляем конфликтующие классы (очень упрощенная версия)
        const baseClass = part.split('-')[0]
        const conflict = Array.from(uniqueClasses).find(c => c.startsWith(baseClass + '-'))
        if (conflict) {
          uniqueClasses.delete(conflict)
        }
        uniqueClasses.add(part.trim())
      }
    }
  }

  return Array.from(uniqueClasses).join(' ').trim()
}

// ====================
// ФОРМАТИРОВАНИЕ
// ====================

/**
 * Форматирование денежных сумм
 */
export function formatCurrency(
  amount: number,
  currency: string = 'RUB',
  locale: string = 'ru-RU',
  options?: Intl.NumberFormatOptions
): string {
  const defaultOptions: Intl.NumberFormatOptions = {
    style: 'currency',
    currency,
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
    ...options
  }

  return new Intl.NumberFormat(locale, defaultOptions).format(amount)
}

/**
 * Форматирование даты
 */
export function formatDate(
  date: Date | string | number,
  options?: Intl.DateTimeFormatOptions & { locale?: string }
): string {
  const dateObj = typeof date === 'string' || typeof date === 'number' ? new Date(date) : date
  const { locale = 'ru-RU', ...formatOptions } = options || {}

  const defaultOptions: Intl.DateTimeFormatOptions = {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    ...formatOptions
  }

  return dateObj.toLocaleDateString(locale, defaultOptions)
}

/**
 * Форматирование времени
 */
export function formatTime(
  date: Date | string | number,
  options?: Intl.DateTimeFormatOptions & { locale?: string }
): string {
  const dateObj = typeof date === 'string' || typeof date === 'number' ? new Date(date) : date
  const { locale = 'ru-RU', ...formatOptions } = options || {}

  const defaultOptions: Intl.DateTimeFormatOptions = {
    hour: '2-digit',
    minute: '2-digit',
    ...formatOptions
  }

  return dateObj.toLocaleTimeString(locale, defaultOptions)
}

/**
 * Форматирование даты и времени
 */
export function formatDateTime(
  date: Date | string | number,
  options?: { dateFormat?: Intl.DateTimeFormatOptions; timeFormat?: Intl.DateTimeFormatOptions; locale?: string }
): string {
  const { locale = 'ru-RU', dateFormat, timeFormat } = options || {}
  const dateStr = formatDate(date, { ...dateFormat, locale })
  const timeStr = formatTime(date, { ...timeFormat, locale })
  return `${dateStr}, ${timeStr}`
}


export function declension(
  number: number,
  words: [string, string, string] // [для 1, для 2, для 5]
): string {
  const cases = [2, 0, 1, 1, 1, 2]
  return words[
    number % 100 > 4 && number % 100 < 20 
      ? 2 
      : cases[Math.min(number % 10, 5)]
  ]
}

/**
 * Форматирование относительного времени (5 минут назад)
 */
export function formatRelativeTime(date: Date | string | number): string {
  const dateObj = typeof date === 'string' || typeof date === 'number' ? new Date(date) : date
  const now = new Date()
  const diffMs = now.getTime() - dateObj.getTime()
  const diffSec = Math.floor(diffMs / 1000)
  const diffMin = Math.floor(diffSec / 60)
  const diffHour = Math.floor(diffMin / 60)
  const diffDay = Math.floor(diffHour / 24)

  if (diffSec < 10) return 'только что'
  if (diffSec < 60) return `${diffSec} ${declension(diffSec, ['секунду', 'секунды', 'секунд'])} назад`
  if (diffMin < 60) return `${diffMin} ${declension(diffMin, ['минуту', 'минуты', 'минут'])} назад`
  if (diffHour < 24) return `${diffHour} ${declension(diffHour, ['час', 'часа', 'часов'])} назад`
  if (diffDay === 1) return 'вчера'
  if (diffDay === 2) return 'позавчера'
  if (diffDay < 7) return `${diffDay} ${declension(diffDay, ['день', 'дня', 'дней'])} назад`
  if (diffDay < 30) {
    const weeks = Math.floor(diffDay / 7)
    return `${weeks} ${declension(weeks, ['неделю', 'недели', 'недель'])} назад`
  }
  
  return formatDate(dateObj, { month: 'short', day: 'numeric' })
}


export function maskCardNumber(cardNumber: string): string {
  if (!cardNumber) return ''
  const cleaned = cardNumber.replace(/\D/g, '')
  if (cleaned.length < 4) return cleaned
  
  const lastFour = cleaned.slice(-4)
  const masked = '•'.repeat(Math.max(0, cleaned.length - 4))
  
  // Форматирование группами по 4 символа
  const formatted = (masked + lastFour).replace(/(.{4})/g, '$1 ').trim()
  return formatted
}

/**
 * Маска для телефона (+7 (999) 999-99-99)
 */
export function maskPhone(phone: string): string {
  if (!phone) return ''
  const cleaned = phone.replace(/\D/g, '')
  
  if (cleaned.startsWith('7') || cleaned.startsWith('8')) {
    const match = cleaned.match(/^[78]?(\d{3})(\d{3})(\d{2})(\d{2})$/)
    if (match) {
      return `+7 (${match[1]}) ${match[2]}-${match[3]}-${match[4]}`
    }
  }
  
  // Для других форматов
  if (cleaned.length === 10) {
    const match = cleaned.match(/^(\d{3})(\d{3})(\d{2})(\d{2})$/)
    if (match) {
      return `+7 (${match[1]}) ${match[2]}-${match[3]}-${match[4]}`
    }
  }
  
  return phone
}

/**
 * Маска для email (te***@ex***.com)
 */
export function maskEmail(email: string): string {
  if (!email || !email.includes('@')) return email
  
  const [local, domain] = email.split('@')
  const [domainName, ...domainRest] = domain.split('.')
  
  const maskedLocal = local.length > 2 
    ? local[0] + '*'.repeat(Math.min(3, local.length - 2)) + local.slice(-1)
    : local
    
  const maskedDomain = domainName.length > 2
    ? domainName[0] + '*'.repeat(Math.min(3, domainName.length - 2)) + domainName.slice(-1)
    : domainName
    
  return `${maskedLocal}@${maskedDomain}.${domainRest.join('.')}`
}


export function isValidEmail(email: string): boolean {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

/**
 * Валидация телефона (российский формат)
 */
export function isValidPhone(phone: string): boolean {
  const cleaned = phone.replace(/\D/g, '')
  return cleaned.length >= 10 && cleaned.length <= 11
}

/**
 * Валидация ИНН
 */
export function isValidINN(inn: string): boolean {
  const cleaned = inn.replace(/\D/g, '')
  return cleaned.length === 10 || cleaned.length === 12
}

/**
 * Проверка сложности пароля
 */
export function checkPasswordStrength(password: string): {
  score: number
  strength: 'weak' | 'medium' | 'strong' | 'very-strong'
  feedback: string[]
} {
  const feedback: string[] = []
  let score = 0

  // Длина
  if (password.length >= 12) score += 2
  else if (password.length >= 8) score += 1
  else feedback.push('Пароль должен содержать минимум 8 символов')

  // Заглавные буквы
  if (/[A-ZА-Я]/.test(password)) score += 1
  else feedback.push('Добавьте заглавные буквы')

  // Строчные буквы
  if (/[a-zа-я]/.test(password)) score += 1
  else feedback.push('Добавьте строчные буквы')

  // Цифры
  if (/[0-9]/.test(password)) score += 1
  else feedback.push('Добавьте цифры')

  // Специальные символы
  if (/[^A-Za-zА-Яа-я0-9]/.test(password)) score += 1
  else feedback.push('Добавьте специальные символы (!@#$% и т.д.)')

  // Определяем силу
  let strength: 'weak' | 'medium' | 'strong' | 'very-strong'
  if (score <= 2) strength = 'weak'
  else if (score <= 3) strength = 'medium'
  else if (score <= 4) strength = 'strong'
  else strength = 'very-strong'

  return { score, strength, feedback }
}



/**
 * Генерация случайного ID
 */
export function generateId(length: number = 8): string {
  return Math.random()
    .toString(36)
    .substring(2, 2 + length)
    .toUpperCase()
}

/**
 * Генерация случайного цвета
 */
export function generateRandomColor(): string {
  const colors = [
    '#3B82F6', // blue
    '#10B981', // green
    '#8B5CF6', // purple
    '#F59E0B', // yellow
    '#EF4444', // red
    '#EC4899', // pink
    '#06B6D4', // cyan
    '#F97316', // orange
  ]
  return colors[Math.floor(Math.random() * colors.length)]
}

/**
 * Генерация градиента
 */
export function generateGradient(): string {
  const gradients = [
    'from-blue-500 to-cyan-500',
    'from-purple-500 to-pink-500',
    'from-green-500 to-emerald-500',
    'from-orange-500 to-red-500',
    'from-indigo-500 to-purple-500',
    'from-teal-500 to-green-500',
    'from-pink-500 to-rose-500',
    'from-yellow-500 to-orange-500',
  ]
  return gradients[Math.floor(Math.random() * gradients.length)]
}


/**
 * Расчет ежемесячного платежа по аннуитетному кредиту
 */
export function calculateMonthlyPayment(
  amount: number,      // Сумма кредита
  annualRate: number,  // Годовая ставка (%)
  months: number       // Срок в месяцах
): number {
  const monthlyRate = annualRate / 100 / 12
  const payment = (amount * monthlyRate * Math.pow(1 + monthlyRate, months)) /
    (Math.pow(1 + monthlyRate, months) - 1)
  return Math.round(payment)
}

/**
 * Расчет переплаты по кредиту
 */
export function calculateOverpayment(
  amount: number,
  annualRate: number,
  months: number
): number {
  const monthlyPayment = calculateMonthlyPayment(amount, annualRate, months)
  return Math.round(monthlyPayment * months - amount)
}

/**
 * Расчет дохода по вкладу
 */
export function calculateDepositIncome(
  amount: number,
  annualRate: number,
  months: number,
  capitalization: boolean = true
): number {
  if (capitalization) {
    const monthlyRate = annualRate / 100 / 12
    const totalAmount = amount * Math.pow(1 + monthlyRate, months)
    return Math.round(totalAmount - amount)
  } else {
    return Math.round(amount * annualRate / 100 * months / 12)
  }
}


/**
 * Обрезание текста с многоточием
 */
export function truncateText(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength - 3) + '...'
}

/**
 * Получение инициалов
 */
export function getInitials(name: string): string {
  return name
    .split(' ')
    .map(word => word[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)
}

/**
 * Форматирование имени (Иванов И. И.)
 */
export function formatName(fullName: string): string {
  const parts = fullName.split(' ')
  if (parts.length === 1) return fullName
  
  const lastName = parts[0]
  const firstName = parts[1] ? `${parts[1][0]}.` : ''
  const middleName = parts[2] ? `${parts[2][0]}.` : ''
  
  return `${lastName} ${firstName} ${middleName}`.trim()
}


/**
 * Копирование текста в буфер обмена
 */
export async function copyToClipboard(text: string): Promise<boolean> {
  try {
    await navigator.clipboard.writeText(text)
    return true
  } catch (err) {
    // Fallback для старых браузеров
    try {
      const textArea = document.createElement('textarea')
      textArea.value = text
      textArea.style.position = 'fixed'
      textArea.style.left = '-999999px'
      textArea.style.top = '-999999px'
      document.body.appendChild(textArea)
      textArea.focus()
      textArea.select()
      const success = document.execCommand('copy')
      document.body.removeChild(textArea)
      return success
    } catch (fallbackErr) {
      console.error('Copy failed:', fallbackErr)
      return false
    }
  }
}


/**
 * Дебаунс функция
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: NodeJS.Timeout | null = null
  
  return (...args: Parameters<T>) => {
    if (timeout) clearTimeout(timeout)
    timeout = setTimeout(() => func(...args), wait)
  }
}

/**
 * Троттлинг функция
 */
export function throttle<T extends (...args: any[]) => any>(
  func: T,
  limit: number
): (...args: Parameters<T>) => void {
  let inThrottle: boolean = false
  
  return (...args: Parameters<T>) => {
    if (!inThrottle) {
      func(...args)
      inThrottle = true
      setTimeout(() => (inThrottle = false), limit)
    }
  }
}


/**
 * Получение цвета по типу уведомления
 */
export function getNotificationColor(type: 'success' | 'warning' | 'error' | 'info'): {
  bg: string
  text: string
  border: string
  icon: string
} {
  switch (type) {
    case 'success':
      return {
        bg: 'bg-green-50',
        text: 'text-green-800',
        border: 'border-green-200',
        icon: 'text-green-500'
      }
    case 'warning':
      return {
        bg: 'bg-yellow-50',
        text: 'text-yellow-800',
        border: 'border-yellow-200',
        icon: 'text-yellow-500'
      }
    case 'error':
      return {
        bg: 'bg-red-50',
        text: 'text-red-800',
        border: 'border-red-200',
        icon: 'text-red-500'
      }
    case 'info':
    default:
      return {
        bg: 'bg-blue-50',
        text: 'text-blue-800',
        border: 'border-blue-200',
        icon: 'text-blue-500'
      }
  }
}

/**
 * Получение градиента по типу карты
 */
export function getCardGradient(type: string): string {
  switch (type) {
    case 'debit':
      return 'bg-gradient-to-br from-blue-600 to-cyan-500'
    case 'credit':
      return 'bg-gradient-to-br from-purple-600 to-pink-500'
    case 'premium':
      return 'bg-gradient-to-br from-yellow-600 to-orange-500'
    case 'business':
      return 'bg-gradient-to-br from-gray-800 to-gray-600'
    case 'virtual':
      return 'bg-gradient-to-br from-green-600 to-emerald-500'
    default:
      return 'bg-gradient-to-br from-blue-500 to-blue-700'
  }
}


/**
 * Удаление дубликатов из массива
 */
export function uniqueArray<T>(array: T[]): T[] {
  const result: T[] = []
  const seen = new Map<T, boolean>()
  
  for (const item of array) {
    if (!seen.has(item)) {
      seen.set(item, true)
      result.push(item)
    }
  }
  
  return result
}

/**
 * Случайный элемент массива
 */
export function randomItem<T>(array: T[]): T {
  return array[Math.floor(Math.random() * array.length)]
}

/**
 * Перемешивание массива (Fisher-Yates shuffle)
 */
export function shuffleArray<T>(array: T[]): T[] {
  const newArray = [...array]
  for (let i = newArray.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[newArray[i], newArray[j]] = [newArray[j], newArray[i]]
  }
  return newArray
}


/**
 * Задержка (sleep)
 */
export function delay(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}

/**
 * Форматирование номера счета
 */
export function formatAccountNumber(account: string): string {
  if (!account) return ''
  const cleaned = account.replace(/\D/g, '')
  return cleaned.replace(/(\d{4})/g, '$1 ').trim()
}

/**
 * Проверка на пустое значение
 */
export function isEmpty(value: any): boolean {
  if (value === null || value === undefined) return true
  if (typeof value === 'string') return value.trim() === ''
  if (Array.isArray(value)) return value.length === 0
  if (typeof value === 'object') return Object.keys(value).length === 0
  return false
}

/**
 * Форматирование байтов в читаемый вид
 */
export function formatBytes(bytes: number, decimals: number = 2): string {
  if (bytes === 0) return '0 Bytes'
  
  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i]
}


export default {
  // Классы
  cn,
  
  // Форматирование
  formatCurrency,
  formatDate,
  formatTime,
  formatDateTime,
  formatRelativeTime,
  declension,
  
  // Маскирование
  maskCardNumber,
  maskPhone,
  maskEmail,
  
  // Валидация
  isValidEmail,
  isValidPhone,
  isValidINN,
  checkPasswordStrength,
  
  // Генерация
  generateId,
  generateRandomColor,
  generateGradient,
  
  // Финансы
  calculateMonthlyPayment,
  calculateOverpayment,
  calculateDepositIncome,
  
  // Текст
  truncateText,
  getInitials,
  formatName,
  
  // Буфер обмена
  copyToClipboard,
  
  // Оптимизация
  debounce,
  throttle,
  
  // Стили
  getNotificationColor,
  getCardGradient,
  
  // Массивы и объекты
  uniqueArray,
  randomItem,
  shuffleArray,
  
  // Прочие
  delay,
  formatAccountNumber,
  isEmpty,
  formatBytes,
}